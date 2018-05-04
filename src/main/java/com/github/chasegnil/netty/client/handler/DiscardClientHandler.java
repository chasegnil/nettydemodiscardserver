package com.github.chasegnil.netty.client.handler;

import com.github.chasegnil.netty.client.DiscardClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DiscardClientHandler extends SimpleChannelInboundHandler<Object> {

    private ByteBuf content;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        // 初始化信息
        content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE);
        // 发送初始化信息
        generateTraffic();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        content.release();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Server is supposed to send nothing, but if it sends something, discard it.
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    long counter;

    private void generateTraffic(){
        ctx.writeAndFlush(content.retainedDuplicate()).addListener(trafficGenerator);
    }

    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                generateTraffic();
            } else {
                future.cause().printStackTrace();
                future.channel().close();
            }

        }
    };
}
