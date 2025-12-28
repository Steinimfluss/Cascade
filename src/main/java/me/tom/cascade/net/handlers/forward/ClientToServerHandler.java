package me.tom.cascade.net.handlers.forward;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientToServerHandler extends ChannelInboundHandlerAdapter {

    private final Channel backend;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (backend.isActive()) {
            backend.writeAndFlush(msg);
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (backend.isActive()) {
            backend.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}