package me.tom.cascade.net.handlers;

import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.auth.GameProfile;
import me.tom.cascade.auth.MojangSessionService;
import me.tom.cascade.crypto.AesDecryptHandler;
import me.tom.cascade.crypto.AesEncryptHandler;
import me.tom.cascade.crypto.Crypto;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.clientbound.EncryptionResponsePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.LoginSuccessPacket;

public class EncryptionResponseHandler extends SimpleChannelInboundHandler<EncryptionResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EncryptionResponsePacket packet) {
        PrivateKey privateKey = Crypto.KEY_PAIR.getPrivate();

        byte[] sharedSecret = Crypto.rsaDecrypt(packet.getSharedSecret(), privateKey);
        byte[] verifyToken = Crypto.rsaDecrypt(packet.getVerifyToken(), privateKey);
        boolean validToken = validateVerifyToken(ctx, verifyToken);
        boolean onlineMode = CascadeBootstrap.CONFIG.isAuthVerification();

        if (!validToken) {
            ctx.close();
            return;
        }
        
        GameProfile profile = getGameProfile(ctx, onlineMode, sharedSecret);
        enableEncryption(ctx.pipeline(), sharedSecret);
        ctx.writeAndFlush(new LoginSuccessPacket(profile));
    }
    
    private GameProfile getGameProfile(ChannelHandlerContext ctx, boolean onlineMode, byte[] sharedSecret) {
    	if(onlineMode) {
	    	GameProfile profile = authenticate(ctx, sharedSecret);
	        if (profile == null) {
	            ctx.close();
	        }
	    	System.out.println(profile.getName());
	        return profile;
    	} else {
    		return new GameProfile(UUID.randomUUID(), "", null);
    	}
    }

    private boolean validateVerifyToken(ChannelHandlerContext ctx, byte[] token) {
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