package com.xiao.framework.rpc.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 *
 * @author lix wang
 */
public class DiscardServer {
    public void run(int port) throws InterruptedException {
        // NioEventLoopGroup 是一个多线程的用来处理IO操作的消息循环。
        // serverGroup接受到来的连接，
        EventLoopGroup serverGroup = new NioEventLoopGroup(1);
        // 在serverGroup接受连接并且注册连接到clientGroup后，clientGroup处理被接受的连接的数据传输。
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        try {
            // ServerBootstrap 用来设置server 可以用channel来设置server。
            // 这里指定用NioServerSocketChannel 初始化一个新的channel 来接受请求连接。
            // ChannelInitializer 是一个特别的handler 用来配置一个新的channel，
            // 这里给channel配置了DiscardServerHandler，
            // 这里的option针对serverGroup，childOption针对clientGroup。
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(serverGroup, clientGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // bind port and start to accept incoming connections
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            clientGroup.shutdownGracefully();
            serverGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new DiscardServer().run(8999);
    }
}
