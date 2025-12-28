package me.tom.cascade.net.handlers;

import java.security.SecureRandom;
import java.util.Arrays;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import me.tom.cascade.net.handlers.forward.ClientToServerHandler;
import me.tom.cascade.net.handlers.forward.ServerToClientHandler;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.codec.PacketDecoder;
import me.tom.cascade.protocol.codec.PacketEncoder;
import me.tom.cascade.protocol.codec.PacketFramer;
import me.tom.cascade.protocol.packet.packets.clientbound.DisconnectPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.CookieResponsePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.EncryptionRequestPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.HandshakePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginStartPacket;
import me.tom.cascade.util.Crypto;

@AllArgsConstructor
public class CookieResponseHandler extends SimpleChannelInboundHandler<CookieResponsePacket> {

    private static final byte[] SECRET = new byte[] { 0x01 };

    private final Channel clientChannel;
    private final Channel backendChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CookieResponsePacket packet) {
        boolean isValidToken = packet.getPayload() != null 
        		&& Arrays.equals(packet.getPayload(), SECRET);
        boolean isTransfer = ctx.channel().attr(ProtocolAttributes.TRANSFER).get();

    	if (!isValidToken) {
    		byte[] verifyToken = new byte[4];
            new SecureRandom().nextBytes(verifyToken);

            ctx.channel().attr(ProtocolAttributes.VERIFY_TOKEN).set(verifyToken);

            EncryptionRequestPacket encryptionRequest = new EncryptionRequestPacket(
                    "",
                    Crypto.KEY_PAIR.getPublic().getEncoded(),
                    verifyToken,
                    true
            );

            ctx.writeAndFlush(encryptionRequest);
    	    return;
    	}
    	
    	if(isTransfer) {
	    	HandshakePacket handshake = ctx.channel().attr(ProtocolAttributes.HANDSHAKE_PACKET).get();
		    LoginStartPacket loginStart = ctx.channel().attr(ProtocolAttributes.LOGIN_START_PACKET).get();
	
		    backendChannel.attr(ProtocolAttributes.STATE).set(ConnectionState.HANDSHAKE);
		    backendChannel.writeAndFlush(handshake).addListener(f -> {
		        backendChannel.attr(ProtocolAttributes.STATE).set(ConnectionState.LOGIN);
		        backendChannel.writeAndFlush(loginStart);
		    });
	
		    clientChannel.pipeline().remove(PacketFramer.class);
		    clientChannel.pipeline().remove(PacketDecoder.class);
		    clientChannel.pipeline().remove(PacketEncoder.class);
		    clientChannel.pipeline().remove(ConnectionHandler.class);
		    
		    backendChannel.pipeline().remove(PacketFramer.class);
		    backendChannel.pipeline().remove(PacketDecoder.class);
		    backendChannel.pipeline().remove(PacketEncoder.class);
		    
		    clientChannel.pipeline().addLast("client-to-server",
	                new ClientToServerHandler(backendChannel));
	
	        backendChannel.pipeline().addLast("server-to-client",
	                new ServerToClientHandler(clientChannel));
	        return;
    	}
    	
    	System.out.println("IT DONT ACCEPT SHIT NIGGA");
    }
}