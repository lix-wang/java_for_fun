## Socket IO 工作机制浅析

&emsp;&emsp; 通常我们会使用Socket或者ServerSocket利用TCP／IP协议来进行可靠数据传输。TCP/IP 模型中有5层结构：应用层、传输层、网络层、数据链路层、物理层。
Socket是应用层与TCP／IP协议通信中间软件抽象层，提供了一组接口。Socket底层数据结构，包含：套接字所关联的本地和远程互联网地址和端口；
一个FIFO队列用于存放接收到的等待分配的数据（RecvQ），以及一个用于存放等待传输数据的队列(SendQ);打开和关闭TCP握手相关的额外协议状态信息。
<p>
    
    final Socket socket = new Socket(address, port);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(("I am message: " + i).getBytes());
                outputStream.flush();
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[4096];
                StringBuilder stringBuilder = new StringBuilder();
                while (inputStream.read(buffer) > 0) {
                    stringBuilder.append(buffer);
                }
</p>

&emsp;&emsp; 对于Socket传输中的FIFO队列，分为三种：1，SendQ，在上述的实现中，我们调用了OutputStream.write()方法，来写入数据到Socket输出流中，
此时，写入的字节会缓存在SendQ中，占用大约37KB内存。2，RecvQ，TCP协议会负责按照顺序，进行远程数据传递，此时会从SendQ中读取一定大小的字节块，
这个字节块的大小与write写入的大小无关，然后把数据传递到RecvQ，占用大约25KB。3，Delivered，表示接收者从输入流中已经读取到的字节。
TCP将SendQ中数据按顺序转移到RecvQ这个过程，用户是无法控制和观察的。可以用SocketOptions.SO_SNDBUF 和 SocketOptions.SO_RCVBUF 来设置。

<br>
<br>
&emsp;&emsp; OutputStream -> 发送缓冲队列SendQ -> NetWork -> 接收缓冲队列RecvQ -> InputStream。

<br>
<br>
&emsp;&emsp; Socket连接时，需要三次握手： Client发送SYN到服务器 -> 服务器接收到SYN报文，返回SYN + ACK给客户端 -> 客户端接收到SYN + ACK，发送ACK给服务器
-> 服务器接收到ACK，连接建立。Socket关闭时，进行四次握手：客户端发送FIN到服务器 -> 服务器接收到，发送ACK给客户端 -> 服务器发送FIN给客户端 
-> 客户端发送ACK给服务器，关闭连接。连接数是有限的，如果连接满了，那么不会接收新的SYN请求，那么客户端发送SYN一段时间后没有接收到SYN + ACK，
会重传SYN两次，如果仍然没有收到SYN + ACK，那么客户端将放弃连接，连接请求超时。

<br>
<br>
&emsp;&emsp; Socket读超时，如果RecvQ中没有数据，那么read操作会一直阻塞，直到有数据到来或者异常产生，因此设置超时时间是很有必要的。
TCP模块把接收到的数据放入RecvQ，直到应用层调用read方法读取，如果RecvQ被填满，TCP会根据滑动窗口机制通知对方不再发送数据，直到RecvQ腾出空间。

<br>
<br>
&emsp;&emsp; Socket写超时，Socket超时重传机制是用来保证数据可靠性传输的机制，发送一个数据报文后就开启一个计时器，在一定时间内没有得到ACk确认，
那么重新发送报文，如果重新发送多次后仍没有确认报文，那么发送一个复位报文RST，然后关闭TCP连接。如果发送端调用write持续写数据，直到SendQ被填满，
如果SendQ已经填满，那么再次调用write时，write方法将被阻塞，直到有SendQ空闲空间为止。如果此时接收端RecvQ也被填满，那么所有操作及数据传输，
都会停止，直到接收端调用read方法将一些字节传输到应用程序。