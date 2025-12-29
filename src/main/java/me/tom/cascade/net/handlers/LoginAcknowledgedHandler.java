package me.tom.cascade.net.handlers;

import java.net.InetSocketAddress;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.net.CascadeProxy;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.clientbound.StoreCookiePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.TransferPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginAcknowledgedPacket;

public class LoginAcknowledgedHandler extends SimpleChannelInboundHandler<LoginAcknowledgedPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginAcknowledgedPacket packet) throws Exception {
        ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.CONFIGURATION);
		String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();

		String jwt = Jwts.builder()
		        .setSubject(ctx.channel().attr(ProtocolAttributes.USERNAME).get())
		        .claim("ip", ip)
		        .setIssuedAt(new Date())
		        .setExpiration(new Date(System.currentTimeMillis() + 5_000))
		        .signWith(CascadeBootstrap.JWT_KEY, SignatureAlgorithm.HS256)
		        .compact();

		StoreCookiePacket storeCookie = new StoreCookiePacket(
					"cascade:token", 
					jwt.getBytes()
				);
		
		TransferPacket transfer = new TransferPacket(
					((InetSocketAddress)ctx.channel().remoteAddress()).getHostString(), 
					CascadeBootstrap.CONFIG.getProxyPort()
				);
		
		ctx.writeAndFlush(storeCookie);
		ctx.writeAndFlush(transfer);
    }
}