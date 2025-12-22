package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.HandshakePacket;

public class HandshakeHandler extends SimpleChannelInboundHandler<HandshakePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HandshakePacket packet) {
        if (packet.nextState == 1) {
            ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.STATUS);
        } else if (packet.nextState == 2) {
            ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.LOGIN);
        }
    }
}