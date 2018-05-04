package com.github.chasegnil.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * 服务处理类
 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // discard
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) {
                System.out.println(in.readByte());
                System.out.flush();
            }

        } finally {
//            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 关闭资源
        ctx.close();
    }
}
