package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.clientbound.CookieRequestPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginStartPacket;

public class LoginStartHandler extends SimpleChannelInboundHandler<LoginStartPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginStartPacket packet) {
    	ctx.channel().attr(ProtocolAttributes.USERNAME).set(packet.getName());
    	ctx.channel().attr(ProtocolAttributes.LOGIN_START_PACKET).set(packet);

    	CookieRequestPacket cookieRequest = new CookieRequestPacket(
    				"cascade:token"
    			);
    	
        ctx.writeAndFlush(cookieRequest);
    }
}