package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.serverbound.HandshakePacket;

public class HandshakeHandler extends SimpleChannelInboundHandler<HandshakePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HandshakePacket packet) {
    	ConnectionState nextState = ConnectionState.values()[packet.getNextState()];
    	boolean isTransfer = nextState == ConnectionState.TRANSFER;
    	
    	if(isTransfer) {
    		nextState = ConnectionState.LOGIN;
    	}
    	
    	ctx.channel().attr(ProtocolAttributes.HANDSHAKE_PACKET).set(packet);
    	ctx.channel().attr(ProtocolAttributes.TRANSFER).set(isTransfer);
        ctx.channel().attr(ProtocolAttributes.STATE).set(nextState);
    }
}