## Netty notes.

* [1.构成](#1)

&emsp;&emsp; Channel是NIO基本结构，用来表示一个用于连接到实体，能够执行一个或多个不同I/O的开放连接。
<br>
&emsp;&emsp; Callback回调。像ChannelHandler的exceptionCaught就是采用回调的机制。
<br>
&emsp;&emsp; Future用于处理异步操作的结果。Netty中使用ChannelFuture，ChannelFuture可以添加一个或多个ChannelFutureListener实例，
在操作完成后，可以回调operationComplete()，这种机制可以不用手动的检查操作是否完成。每一个netty的outbound I/O操作都会返回一个ChannelFuture。
<br>
&emsp;&emsp; Event和Handler，不同的事件会触发不同的操作。