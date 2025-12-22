package me.tom.cascade.net.handlers;

import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.auth.GameProfile;
import me.tom.cascade.auth.MojangSessionService;
import me.tom.cascade.net.crypto.AesDecryptHandler;
import me.tom.cascade.net.crypto.AesEncryptHandler;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.s2c.EncryptionResponsePacket;
import me.tom.cascade.protocol.packet.packets.s2c.LoginSuccessPacket;
import me.tom.cascade.util.Crypto;

public class EncryptionResponseHandler extends SimpleChannelInboundHandler<EncryptionResponsePacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, EncryptionResponsePacket packet) {
	    PrivateKey privateKey = Crypto.KEY_PAIR.getPrivate();

	    byte[] sharedSecret = Crypto.rsaDecrypt(packet.getSharedSecret(), privateKey);
	    byte[] token = Crypto.rsaDecrypt(packet.getVerifyToken(), privateKey);

	    byte[] expected = ctx.channel().attr(ProtocolAttributes.VERIFY_TOKEN).get();
	    if (!Arrays.equals(token, expected)) {
	        ctx.close();
	        return;
	    }

	    byte[] publicKeyBytes = Crypto.KEY_PAIR.getPublic().getEncoded();

	    String serverIdHash = Crypto.minecraftSha1Hash("", sharedSecret, publicKeyBytes);
	    String username = ctx.channel().attr(ProtocolAttributes.USERNAME).get();
	    String clientIp = ((InetSocketAddress) ctx.channel().remoteAddress())
	            .getAddress().getHostAddress();

	    GameProfile profile =
	            MojangSessionService.hasJoined(username, serverIdHash, clientIp);

	    if (profile == null) {
	        ctx.close();
	        return;
	    }

	    SecretKey aesKey = new SecretKeySpec(sharedSecret, "AES");
	    
	    LoginSuccessPacket response = new LoginSuccessPacket(profile);
	    
    	ctx.pipeline().addFirst("decrypt", new AesDecryptHandler(aesKey));
    	ctx.pipeline().addBefore("packet-encoder", "encrypt", new AesEncryptHandler(aesKey));

	    ctx.writeAndFlush(response);
	}
}