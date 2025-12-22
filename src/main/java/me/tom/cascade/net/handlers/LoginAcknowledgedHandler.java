package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.c2s.LoginAcknowledgedPacket;

public class LoginAcknowledgedHandler extends SimpleChannelInboundHandler<LoginAcknowledgedPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginAcknowledgedPacket packet) {
        ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.CONFIGURATION);
    }
}