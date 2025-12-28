package me.tom.cascade.net.handlers;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.clientbound.StoreCookiePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.TransferPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginAcknowledgedPacket;

public class LoginAcknowledgedHandler extends SimpleChannelInboundHandler<LoginAcknowledgedPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginAcknowledgedPacket packet) throws Exception {
        ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.CONFIGURATION);

		byte[] secret = {0x01};
		
		StoreCookiePacket storeCookie = new StoreCookiePacket(
					"token", 
					secret
				);
		
		TransferPacket transfer = new TransferPacket(
					((InetSocketAddress)ctx.channel().remoteAddress()).getHostString(), 
					CascadeBootstrap.CONFIG.getProxyPort()
				);
		
		ctx.writeAndFlush(storeCookie);
		ctx.writeAndFlush(transfer);
    }
}