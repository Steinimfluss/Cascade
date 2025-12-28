package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.packet.packets.clientbound.PongResponsePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.PingRequestPacket;

public class PingRequestHandler extends SimpleChannelInboundHandler<PingRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingRequestPacket packet) {
        PongResponsePacket response = new PongResponsePacket(packet.getTimestamp());

        ctx.writeAndFlush(response);
    }
}