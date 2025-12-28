package me.tom.cascade.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class AesEncryptHandler extends MessageToByteEncoder<Object> {

    private final Cipher cipher;

    public AesEncryptHandler(SecretKey key) {
        this.cipher = me.tom.cascade.util.Crypto.createAesCipher(Cipher.ENCRYPT_MODE, key);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof ByteBuf) {
        	ByteBuf buf = (ByteBuf)msg;

            int readable = buf.readableBytes();
            if (readable == 0) {
                return;
            }

            byte[] input = new byte[readable];
            buf.readBytes(input);

            byte[] encrypted = cipher.update(input);
            if (encrypted == null || encrypted.length == 0) {
                return;
            }

            out.writeBytes(encrypted);
            return;
        }

        ctx.write(msg);
    }
}