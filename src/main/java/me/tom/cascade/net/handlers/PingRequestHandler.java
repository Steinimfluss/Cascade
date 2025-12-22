package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.packet.packets.c2s.PingRequestPacket;
import me.tom.cascade.protocol.packet.packets.s2c.PongResponsePacket;

public class PingRequestHandler extends SimpleChannelInboundHandler<PingRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingRequestPacket packet) {
        PongResponsePacket response = new PongResponsePacket(packet.getTimestamp());

        ctx.writeAndFlush(response);
    }
}