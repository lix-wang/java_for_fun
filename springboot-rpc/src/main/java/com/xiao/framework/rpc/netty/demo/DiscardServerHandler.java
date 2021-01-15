package com.xiao.framework.rpc.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 *
 * @author lix wang
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ctx.writeAndFlush(msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 这个方法在连接建立，准备产生数据交换时执行。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final ByteBuf byteBuf = ctx.alloc().buffer(4);
        byteBuf.writeInt((int) System.currentTimeMillis());
        final ChannelFuture channelFuture = ctx.writeAndFlush(byteBuf);
        channelFuture.addListener(future -> {
            assert future == channelFuture;
            ctx.close();
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
