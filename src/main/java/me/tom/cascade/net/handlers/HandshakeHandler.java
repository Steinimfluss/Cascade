package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.c2s.HandshakePacket;

public class HandshakeHandler extends SimpleChannelInboundHandler<HandshakePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HandshakePacket packet) {
        ctx.channel().attr(ProtocolAttributes.PROTOCOL_VERSION).set(packet.getProtocolVersion());

        ctx.channel().attr(ProtocolAttributes.HOSTNAME).set(packet.getHostname());

        ctx.channel().attr(ProtocolAttributes.PORT).set(packet.getPort());

        if (packet.nextState == 1) {
            ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.STATUS);
        } else if (packet.nextState == 2) {
            ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.LOGIN);
        } else if (packet.nextState == 3) {
            ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.LOGIN);
        }

    	ctx.channel().attr(ProtocolAttributes.HANDSHAKE_PACKET).set(packet);
    }
}