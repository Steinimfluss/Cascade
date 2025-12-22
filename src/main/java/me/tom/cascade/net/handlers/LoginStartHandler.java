package me.tom.cascade.net.handlers;

import java.security.SecureRandom;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.c2s.EncryptionRequestPacket;
import me.tom.cascade.protocol.packet.packets.c2s.LoginStartPacket;
import me.tom.cascade.util.Crypto;

public class LoginStartHandler extends SimpleChannelInboundHandler<LoginStartPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginStartPacket packet) {
    	ctx.channel().attr(ProtocolAttributes.USERNAME).set(packet.getName());

        byte[] verifyToken = new byte[4];
        new SecureRandom().nextBytes(verifyToken);

        ctx.channel().attr(ProtocolAttributes.VERIFY_TOKEN).set(verifyToken);

        EncryptionRequestPacket response = new EncryptionRequestPacket(
        		"",
                Crypto.KEY_PAIR.getPublic().getEncoded(),
                verifyToken,
                true
        );

        ctx.writeAndFlush(response);
    }
}