package com.xiao.framework.rpc.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 *
 * @author lix wang
 */
public class DiscardClientHandler extends SimpleChannelInboundHandler {
    private ByteBuf content;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        // Initialize the message.
        this.content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.content.release();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Server is supposed to send nothing.
        System.out.println("Client received message: " + msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void generrateTraffic() {
        this.ctx.writeAndFlush(this.content.retainedDuplicate()).addListener(trafficGenerator);
    }

    private final ChannelFutureListener trafficGenerator = future -> {
        if (future.isSuccess()) {
            generrateTraffic();
        } else {
            future.cause().printStackTrace();
            future.channel().close();
        }
    };
}
