package me.tom.cascade.net.handlers;

import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.auth.GameProfile;
import me.tom.cascade.auth.MojangSessionService;
import me.tom.cascade.crypto.AesDecryptHandler;
import me.tom.cascade.crypto.AesEncryptHandler;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.clientbound.EncryptionResponsePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.LoginSuccessPacket;
import me.tom.cascade.util.Crypto;

public class EncryptionResponseHandler extends SimpleChannelInboundHandler<EncryptionResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EncryptionResponsePacket packet) {
        PrivateKey privateKey = Crypto.KEY_PAIR.getPrivate();

        byte[] sharedSecret = Crypto.rsaDecrypt(packet.getSharedSecret(), privateKey);
        byte[] verifyToken = Crypto.rsaDecrypt(packet.getVerifyToken(), privateKey);

        if (!isValidVerifyToken(ctx, verifyToken)) {
            ctx.close();
            return;
        }

        GameProfile profile = authenticate(ctx, sharedSecret);
        if (profile == null) {
            ctx.close();
            return;
        }

        enableEncryption(ctx.pipeline(), sharedSecret);
        
        ctx.channel().attr(ProtocolAttributes.GAME_PROFILE).set(profile);
        
        ctx.writeAndFlush(new LoginSuccessPacket(profile));
    }

    private boolean isValidVerifyToken(ChannelHandlerContext ctx, byte[] token) {
        byte[] expected = ctx.channel().attr(ProtocolAttributes.VERIFY_TOKEN).get();
        return Arrays.equals(token, expected);
    }

    private GameProfile authenticate(ChannelHandlerContext ctx, byte[] sharedSecret) {
        String username = ctx.channel().attr(ProtocolAttributes.USERNAME).get();
        byte[] publicKey = Crypto.KEY_PAIR.getPublic().getEncoded();
        String serverHash = Crypto.minecraftSha1Hash("", sharedSecret, publicKey);
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
                .getAddress()
                .getHostAddress();

        return MojangSessionService.hasJoined(username, serverHash, ip);
    }

    private void enableEncryption(ChannelPipeline pipeline, byte[] sharedSecret) {
        SecretKey aesKey = new SecretKeySpec(sharedSecret, "AES");
        pipeline.addFirst("decrypt", new AesDecryptHandler(aesKey));
        pipeline.addBefore("packet-encoder", "encrypt", new AesEncryptHandler(aesKey));
    }
}