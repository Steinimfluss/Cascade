package me.tom.cascade.net.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.types.VarInt;

public class ConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    ctx.channel().attr(ProtocolAttributes.STATE).set(ConnectionState.HANDSHAKE);
	    super.channelActive(ctx);
	}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        msg.readableBytes();
        VarInt.read(msg);
        msg.resetReaderIndex();
    }
}