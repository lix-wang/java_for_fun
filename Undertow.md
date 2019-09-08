## Undertow 笔记

* [1.Undertow浅析](#1)

<h2 id="1">1.Undertow浅析</h2>
&emsp;&emsp; 在SpringBoot启动过程中，跟undertow相关的分为：refreshContext(ConfigurableApplicationContext) 
-> refresh(ApplicationContext) -> refresh() -> finishRefresh()。

<br>
&emsp;&emsp; refresh(ApplicationContext)，此时的ApplicationContext是AnnotationConfigServletWebServerApplicationContext，
这个类继承自ServletWebServerApplicationContext，ServletWebServerApplicationContext类间接继承了AbstractApplicationContext，
所以此时调用((AbstractApplicationContext) applicationContext).refresh()，实质上是调用了ServletWebServerApplicationContext中重写的refresh()方法，
在该方法中调用了super.refresh()，该方法中会进行onRefresh()， 最终会调用finishRefresh() 方法，在这一步开始准备启动Undertow。

<br>
&emsp;&emsp; 在onRefresh()方法中，调用了createWebServer()方法，首先会去GenericWebApplicationContext中获取ServletContext，
此时ServletContext可以发现为空，下一步会获取执行getWebServerFactory() 获取ServletWebServerFactory。
getWebServerFactory()方法通过getBeanNamesForType(ServletWebServerFactory.class) 获取ServletWebServerFactory 这个bean。
我们发现获取到的ServletWebServerFactory的beanName为"undertowServletWebServerFactory"。根据这个beanName，
我们发现来自于ServletWebServerFactoryConfiguration。

<br>
&emsp;&emsp; 通过查询ServletWebServerFactoryConfiguration调用方，我们发现在ServletWebServerFactoryAutoConfiguration中，
会加载ServletWebServerFactoryConfiguration中的三个类，这三个类分别会产生Undertow、Jetty、Undertow具体实现的ServletWebServerFactory。
SpringBoot不允许多个类型的ServletWebServerFactory存在。因此我们最终会获得 UndertowServletWebServerFactory bean。

<br>
&emsp;&emsp; 获取到ServletWebServerFactory后，会创建webServer。在创建过程中会执行createBuilder(int port)方法，
ioThreads取max(jvm最大可用的处理器数量，2), workThreads = ioThreads * 8, 如果jvm最大可用的内存 < 64mbytes，那么directBuffers = false，
bufferSize = 512bytes，如果最大可用内存 < 128mbytes，那么directBuffers = true，bufferSize = 1024bytes，如果最大可用内存 >= 128mbytes，
那么使用16Kbytes，这样能使得性能最大化，此时directBuffers = true，bufferSize = 1024 * 16 - 20。减20是用来存放协议头，另见UNDERTOW-1209。
在createBuilder(int port)中可以自定义这些参数，还可以通过getSsl() 来设置ssl和http2，最后添加httpListener(int port, String host)。
在createBuilder(port)后，会创建UndertowServletWebServer对象。

<br>
&emsp;&emsp; 在finishRefresh()阶段，会启动webServer，通过UndertowServletWebServer.start()方法启动。在start()过程中，
首先会创建Undertow对象，然后调用DeploymentManagerImpl.start()方法，在这一步中，会调用createUndertowServer()方法，创建Undertow对象。
Undertow对象中包含bufferSize、ioThreads、workerThreads、directBuffers等属性，还包含listeners、rootHandler。
Http 类型的listener会包含port、host等属性。然后调用Undertow.start()方法，启动Undertow。

<br>
&emsp;&emsp; 在启动undertow过程中，首先会创建一个XnioWorker，XnioWorker用来维护IO线程池及应用任务线程池。并且会设置socketOptions、
serverOptions。然后会创建ByteBufferPool类型的DefaultByteBufferPool实例，如果direftBuffers = true，
那么会给当前ByteBufferPool 的arrayBlockedPool 赋值一个DefaultByteBufferPool。

<br>
&emsp;&emsp; 在ByteBufferPool创建完成后，由于我们的当前listener type 为 HTTP，因此会创建一个HttpOpenListener对象。
在HttpOpenListener构造方法中，会为当前的ByteBufferPool进行allocate() 创建buffer，如果directBuffers，那么会创建DirectByteBuffer，
否则，创建HeapByteBuffer，最终根据根据ByteBufferPool和 buffer，构造一个DeafultPooledBuffer对象。然后会为HttpOpenListener设置HttpRequestParser，
这里默认不允许未转义的字符在路径中，maxParameters = 1000，maxHeaders = 200， 不允许未编码的/，需要decode，charset = UTF-8，
maxCachedHeaderSize = 150。最后设置连接统计是否开启。如果开启了Http2，那么HttpHandler将会是Http2UpgradeHandler。

<br>
&emsp;&emsp; 在确定了最终的ChannelListener后，会调用ChannelListeners.openListenerAdapter(ChannelListener)来为openListener添加适配器。
本项目中采用HTTP，所以这里是HttpOpenListener，openListenerAdapter的作用是创建适配器，自动接收连接，并把请求映射到一个openListener中。
然后通过XnioWorker.createStreamConnectionServer(SocketAddress，ChannelListener， OptionMap)，创建一个stream server。
至此，undertow就启动完毕了，SpringBoot整个项目也启动完毕。

<br>
&emsp;&emsp; 可以发现在Undertow类中，Xnio.getInstance(Undertow.class.getClassLoader()); 获取到的是NioXnio对象，然后使用该对象来创建workers。
xnio.createWorker(）创建workerThread，根据执行结果我们发现，实际上NioXnio创建的WorkerThread的数量为ioThreads，然后启动这些WorkerThread。
这些WorkerThread将用来处理请求。

<br>
&emsp;&emsp; 处理请求时，首先进入到WorkerThread.run()，然后发现有任务时，将任务交给safeRun()，此时我们发现任务类型为QueuedNioTcpServer。
然后调用ChannelListeners.invokeChannelListener(QueuedNioTcpServer.this, getAcceptListener())，把处理交给对应的ChannelListener，
然后利用channel.accept() 获取NioSocketStreamConnection，然后利用HttpOpenListener.handleEvent，来处理这个NioSocketStreamConnection。
经过这些流程，请求通过xnio，转发到了我们开始启动undertow时绑定的HttpOpenListener了，此时将由undertow来处理请求。

<br>
&emsp;&emsp; 在HttpOpenListener.handleEvent()方法中，创建HttpServerConnection、HttpReadListener，
然后使用HttpReadListener.handleEvent(ConduitStreamSourceChannel)方法。在该方法结尾，执行了handleEventWithNoRunningRequest(ConduitStreamSourceChannel)方法。




