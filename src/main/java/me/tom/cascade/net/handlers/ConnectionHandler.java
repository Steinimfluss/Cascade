package me.tom.cascade.net.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.net.types.VarInt;

public class ConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        int length = msg.readableBytes();
        int packetId = VarInt.read(msg);
        msg.resetReaderIndex();
    }
}