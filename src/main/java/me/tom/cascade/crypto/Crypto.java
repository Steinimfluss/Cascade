package me.tom.cascade.crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class Crypto {

    public static final KeyPair KEY_PAIR;

    static {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024);
            KEY_PAIR = gen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA keypair", e);
        }
    }
    
    public static byte[] rsaDecrypt(byte[] data, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("RSA decrypt failed", e);
        }
    }
    
    public static Cipher createAesCipher(int mode, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(mode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException("Failed to init AES cipher", e);
        }
    }
    
    public static String minecraftSha1Hash(String serverId, byte[] sharedSecret, byte[] publicKeyBytes) {
	    try {
	        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
	        sha1.update(serverId.getBytes(StandardCharsets.US_ASCII));
	        sha1.update(sharedSecret);
	        sha1.update(publicKeyBytes);
	        byte[] digest = sha1.digest();
	        return toMinecraftHex(digest);
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to compute serverId hash", e);
	    }
	}

	private static String toMinecraftHex(byte[] digest) {
	    BigInteger i = new BigInteger(digest);
	    return i.toString(16);
	}
}