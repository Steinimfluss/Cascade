package me.tom.cascade.crypto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.List;

public class AesDecryptHandler extends ByteToMessageDecoder {

    private final Cipher cipher;

    public AesDecryptHandler(SecretKey key) {
        this.cipher = me.tom.cascade.util.Crypto.createAesCipher(Cipher.DECRYPT_MODE, key);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] input = new byte[in.readableBytes()];
        in.readBytes(input);

        byte[] decrypted = cipher.update(input);
        out.add(ctx.alloc().buffer(decrypted.length).writeBytes(decrypted));
    }
}