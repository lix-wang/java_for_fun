## NIO 工作机制浅析

&emsp;&emsp; 我采用如下方式来进行Socket NIO操作：
<p>
    
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            serverSocket.bind(inetSocketAddress);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());
            for (;;) {
                selector.select();
                Set<SelectionKey> readKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    try {
                        if (key.isAcceptable()) {
                            ......
                        }
                        if (key.isWritable()) {
                            ......
                        }
                    } catch (Exception e) {
                        key.cancel();
                        key.channel().close();
                    }
                }
            }
        
</p>
&emsp;&emsp; 在上述的代码中，首先我会创建一个ServerSocketChannel，这个ServerSocketChannel对象实际上是ServerSocketChannelImpl，
然后设置了这个channel为非阻塞。ServerSocketChannel.socket()方法，会创建ServerSocketAdaptor，这个对象本质上是个ServerSocket。
然后ServerSocket调用bind(SocketAddress)绑定端口。紧接着创建了一个Selector实例，再把这个Channel绑定到这个Selector实例上。
<br>
&emsp;&emsp; 在Socket NIO中，一个SelectableChannel可以有多个SelectionKey，一个SelectionKey存储着一个Channel和一个Selector及interestOps的映射。
一个Selector对应于多个SelectionKey。绑定时采用SelectableChannel.register(Selector, int)，首先遍历SelectableChannel中的SelectionKey集合，
判断是否存在一个SelectionKey中的Selector为当前即将注册的Selector，如果存在那么更新掉当前SelectionKey中的interestOps，如果不存在，则创建一个SelectionKey。
<br>
&emsp;&emsp; 使用for无限循环来轮询请求。Selector.select()是一个轮询行为，会轮询当前Selector中的SelectionKey集合中的所有的SelectableChannel，
找出就绪的SelectableChannel对应的SelectionKeys，至少有一个SelectableChannel就绪才回往下执行，否则会继续阻塞轮询。然后Selector.selectedKeys()，
会返回就绪的SelectionKey集合。

<br>
&emsp;&emsp; 然后遍历处理这些就绪的SelectionKey，因为一个SelectableChannel，一个Selector，构成了唯一的一个SelectionKey。
因此可以通过遍历SelectionKey来遍历处理SelectableChannel，处理SelectableChannel时，通过SelectionKey的interestOps和readyOps，
来决定当前就绪操作类型。

<br>
&emsp;&emsp; 本质上来讲，传统的阻塞型Socket IO的缺点在于，一旦一个请求建立连接后，当前线程只能处理这个连接的IO，在不需要读写时，
这个线程没必要使用CPU，但其阻塞特性导致这个线程一直空等，直到完成请求，效率很低。传统Socket通信，如果要处理大量请求，那么就必须创建多个线程，
这样会导致资源浪费，同时大量线程的切换会耗费大量时间。采用NIO，主要优点在于利用多路复用的特性，只需要一个线程轮询就绪任务，只要有任意一个任务就绪，
那么就执行这个任务。避免一个任务阻塞导致其他任务也无法执行。

