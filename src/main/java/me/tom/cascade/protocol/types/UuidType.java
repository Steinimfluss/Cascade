package me.tom.cascade.protocol.types;

import io.netty.buffer.ByteBuf;

public class UuidType {
    public static java.util.UUID read(ByteBuf in) {
        long most = in.readLong();
        long least = in.readLong();
        return new java.util.UUID(most, least);
    }

    public static void write(ByteBuf out, java.util.UUID uuid) {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }
}