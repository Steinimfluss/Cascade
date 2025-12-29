package me.tom.cascade.net.handlers;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.crypto.Crypto;
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

@AllArgsConstructor
public class CookieResponseHandler extends SimpleChannelInboundHandler<CookieResponsePacket> {

    private static final Logger log = LoggerFactory.getLogger(CookieResponseHandler.class);

    private final Channel client;
    private final Channel backend;

    private final SecureRandom random = new SecureRandom();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CookieResponsePacket packet) {

        boolean transferMode = ctx.channel().attr(ProtocolAttributes.TRANSFER).get();
        boolean tokenValid = packet.getPayload() != null &&
                validateJwt(new String(packet.getPayload(), StandardCharsets.UTF_8), ctx);

        if (!tokenValid && !transferMode) {
            log.debug("Authenticating client {}", ctx.channel());

            byte[] verifyToken = new byte[4];
            random.nextBytes(verifyToken);

            ctx.channel().attr(ProtocolAttributes.VERIFY_TOKEN).set(verifyToken);

            EncryptionRequestPacket encryptionRequest = new EncryptionRequestPacket(
                    "",
                    Crypto.KEY_PAIR.getPublic().getEncoded(),
                    verifyToken,
                    CascadeBootstrap.CONFIG.isAuthVerification()
            );

            ctx.writeAndFlush(encryptionRequest);
            return;
        }

        if (tokenValid && transferMode) {
            log.debug("Tunneling client {}", ctx.channel());

            HandshakePacket handshake = ctx.channel().attr(ProtocolAttributes.HANDSHAKE_PACKET).get();
            LoginStartPacket loginStart = ctx.channel().attr(ProtocolAttributes.LOGIN_START_PACKET).get();

            backend.attr(ProtocolAttributes.STATE).set(ConnectionState.HANDSHAKE);
            backend.writeAndFlush(handshake).addListener(f -> {
                backend.attr(ProtocolAttributes.STATE).set(ConnectionState.LOGIN);
                backend.writeAndFlush(loginStart);
            });

            client.pipeline().remove(PacketFramer.class);
            client.pipeline().remove(PacketDecoder.class);
            client.pipeline().remove(PacketEncoder.class);
            client.pipeline().remove(ConnectionHandler.class);

            backend.pipeline().remove(PacketFramer.class);
            backend.pipeline().remove(PacketDecoder.class);
            backend.pipeline().remove(PacketEncoder.class);

            client.pipeline().addLast("client-to-server", new ClientToServerHandler(backend));
            backend.pipeline().addLast("server-to-client", new ServerToClientHandler(client));
            return;
        }

        ctx.writeAndFlush(new DisconnectPacket(CascadeBootstrap.INVALID_TOKEN_JSON));
        ctx.close();
    }

    private boolean validateJwt(String jwt, ChannelHandlerContext ctx) {
        try {
            Key key = CascadeBootstrap.JWT_KEY;

            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(jwt)
                    .getBody();

            String tokenUser = claims.getSubject();
            String tokenIp = claims.get("ip", String.class);

            String currentUser = ctx.channel().attr(ProtocolAttributes.USERNAME).get();
            String currentIp = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();

            return tokenUser.equals(currentUser) && tokenIp.equals(currentIp);
        } catch (Exception ignored) {
            return false;
        }
    }
}