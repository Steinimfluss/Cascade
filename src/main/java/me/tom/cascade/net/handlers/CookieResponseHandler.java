package me.tom.cascade.net.handlers;

import java.security.SecureRandom;
import java.util.Arrays;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.c2s.CookieResponsePacket;
import me.tom.cascade.protocol.packet.packets.c2s.EncryptionRequestPacket;
import me.tom.cascade.protocol.packet.packets.c2s.HandshakePacket;
import me.tom.cascade.protocol.packet.packets.c2s.LoginStartPacket;
import me.tom.cascade.protocol.packet.packets.s2c.DisconnectPacket;
import me.tom.cascade.util.Crypto;

@AllArgsConstructor
public class CookieResponseHandler extends SimpleChannelInboundHandler<CookieResponsePacket> {

    private static final byte[] SECRET = new byte[] { 0x01 };

    private final Channel clientChannel;
    private final Channel backendChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CookieResponsePacket packet) {

        ConnectionState state = ctx.channel().attr(ProtocolAttributes.STATE).get();
        byte[] payload = packet.getPayload();

        if (state == ConnectionState.LOGIN) {
        	if (payload != null && Arrays.equals(payload, SECRET)) {

        	    System.out.println("Cookie matched — enabling forwarding.");

        	    HandshakePacket storedHandshake = ctx.channel().attr(ProtocolAttributes.HANDSHAKE_PACKET).get();
        	    LoginStartPacket storedLoginStart = ctx.channel().attr(ProtocolAttributes.LOGIN_START_PACKET).get();

        	    System.out.println("2!!!");

        	    // 1) Send handshake in HANDSHAKE state
        	    backendChannel.attr(ProtocolAttributes.STATE).set(ConnectionState.HANDSHAKE);
        	    backendChannel.writeAndFlush(storedHandshake).addListener(f -> {
        	        if (!f.isSuccess()) {
        	            f.cause().printStackTrace();
        	            return;
        	        }

        	        // 2) After handshake is encoded/sent, switch to LOGIN and send login start
        	        backendChannel.attr(ProtocolAttributes.STATE).set(ConnectionState.LOGIN);
        	        backendChannel.writeAndFlush(storedLoginStart).addListener(f2 -> {
        	            if (!f2.isSuccess()) {
        	                f2.cause().printStackTrace();
        	            }
        	        });
        	    });

        	    System.out.println("3!!!");

        	    removeOldHandlers(clientChannel);
        	    removeOldHandlers(backendChannel);
        	    
        	    clientChannel.pipeline().addLast("clientToServer",
                        new ClientToServerHandler(backendChannel));

                backendChannel.pipeline().addLast("serverToClient",
                        new ServerToClientHandler(clientChannel));
                System.out.println("ADDED THE FORWARDERS!");

        	    return;
        	}

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
            return;
        }

        ctx.writeAndFlush(new DisconnectPacket("{\"text\":\"Invalid or missing token\",\"color\":\"red\"}"));
        ctx.close();
    }

    private void removeOldHandlers(Channel ch) {
        removeIfPresent(ch, "packet-framer");
        removeIfPresent(ch, "packet-decoder");
        removeIfPresent(ch, "packet-encoder");
        removeIfPresent(ch, "handshake-handler");
        removeIfPresent(ch, "status-request-handler");
        removeIfPresent(ch, "ping-request-handler");
        removeIfPresent(ch, "login-start-handler");
        removeIfPresent(ch, "cookie-response-handler");
        removeIfPresent(ch, "encryption-response-handler");
        removeIfPresent(ch, "login-acknowledged-handler");
        removeIfPresent(ch, "connection-handler");
    }

    private void removeIfPresent(Channel ch, String name) {
        if (ch.pipeline().get(name) != null) {
            ch.pipeline().remove(name);
        }
    }
}