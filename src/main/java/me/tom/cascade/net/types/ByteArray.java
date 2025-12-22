package me.tom.cascade.net.types;

import io.netty.buffer.ByteBuf;

public class ByteArray {
    public static byte[] read(ByteBuf in) {
    	int length = VarInt.read(in);
    	byte[] bytes = new byte[length];
    	in.readBytes(bytes);
    	return bytes;
    }

    public static void write(ByteBuf out, byte[] bytes) {
    	VarInt.write(out, bytes.length);
    	out.writeBytes(bytes);
    }
}