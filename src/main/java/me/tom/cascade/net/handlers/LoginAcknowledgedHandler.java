package me.tom.cascade.net.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.clientbound.StoreCookiePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.TransferPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginAcknowledgedPacket;

public class LoginAcknowledgedHandler extends SimpleChannelInboundHandler<LoginAcknowledgedPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginAcknowledgedPacket packet) throws Exception {
        Channel client = ctx.channel();
        client.attr(ProtocolAttributes.STATE).set(ConnectionState.CONFIGURATION);

		byte[] secret = {0x01};
        ctx.writeAndFlush(new StoreCookiePacket("token", secret));
        ctx.writeAndFlush(new TransferPacket("localhost", 25564));
    }
}