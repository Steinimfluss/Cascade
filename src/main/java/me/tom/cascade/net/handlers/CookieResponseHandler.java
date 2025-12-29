package me.tom.cascade.net.handlers;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tom.cascade.CascadeBootstrap;
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

    private final Channel clientChannel;
    private final Channel backendChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CookieResponsePacket packet) {
        boolean isTransfer = ctx.channel().attr(ProtocolAttributes.TRANSFER).get();

        boolean isValidToken = false;
        if (packet.getPayload() != null && packet.getPayload().length > 0) {
            String token = new String(packet.getPayload(), StandardCharsets.UTF_8);
            isValidToken = isValidToken(token, ctx);
        }

        if (!isValidToken && !isTransfer) {
            handleNormalLogin(ctx);
            return;
        }

        if (isTransfer && isValidToken) {
            completeTransfer(ctx);
            return;
        }

        ctx.writeAndFlush(new DisconnectPacket(CascadeBootstrap.INVALID_TOKEN_JSON));
        ctx.close();
    }

    private void handleNormalLogin(ChannelHandlerContext ctx) {
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
    }

    private void completeTransfer(ChannelHandlerContext ctx) {
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
    }

    private boolean isValidToken(String jwt, ChannelHandlerContext ctx) {
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

            if (!tokenUser.equals(currentUser) || !tokenIp.equals(currentIp)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}