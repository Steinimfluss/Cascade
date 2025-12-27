package me.tom.cascade.net.handlers;

import java.security.SecureRandom;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.c2s.EncryptionRequestPacket;
import me.tom.cascade.protocol.packet.packets.c2s.LoginStartPacket;
import me.tom.cascade.protocol.packet.packets.s2c.CookieRequestPacket;
import me.tom.cascade.util.Crypto;

public class LoginStartHandler extends SimpleChannelInboundHandler<LoginStartPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginStartPacket packet) {
    	System.out.println("Client starting login process");
    	ctx.channel().attr(ProtocolAttributes.USERNAME).set(packet.getName());
    	
    	ctx.channel().attr(ProtocolAttributes.LOGIN_START_PACKET).set(packet);

    	CookieRequestPacket cookieRequest = new CookieRequestPacket("token");
        ctx.writeAndFlush(cookieRequest);
    }
}