## Netty notes.

&emsp;&emsp; Channel是NIO基本结构，用来表示一个用于连接到实体，能够执行一个或多个不同I/O的开放连接。
<br>
&emsp;&emsp; Callback回调。像ChannelHandler的exceptionCaught就是采用回调的机制。
<br>
&emsp;&emsp; Future用于处理异步操作的结果。Netty中使用ChannelFuture，ChannelFuture可以添加一个或多个ChannelFutureListener实例，
在操作完成后，可以回调operationComplete()，这种机制可以不用手动的检查操作是否完成。每一个netty的outbound I/O操作都会返回一个ChannelFuture。
<br>
&emsp;&emsp; Event和Handler，不同的事件会触发不同的操作。
<br>
&emsp;&emsp; Netty主要优点在于模块组件化，非阻塞化。整个逻辑处理过程中都使用了ChannelFuture，这样避免阻塞操作，个人认为本质是利用了事件触发机制，
通过事件的发生来触发不同的ChannelHandler，以此来继续执行逻辑，用户可以自定义事件处理逻辑，这样不同的事件会触发不同的handler，
执行不同的逻辑。而且这种机制还能够在非阻塞的前提下，让事件能够按照正确的顺序执行。
<br>
&emsp;&emsp; Undertow 的多路复用，ByteBuffer，HttpHandler机制类似于Netty，Netty适合用来做简单的Socket Server，不适合用来做Web Server，
而且个人感觉Netty也不适合用来做HTTP 客户端。相比来说OkHttp的处理链，请求多路复用机制，使用简单的特性，更适合用来做单纯的RPC 客户端。