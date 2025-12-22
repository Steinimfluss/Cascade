package me.tom.cascade.protocol.packet;

import io.netty.buffer.ByteBuf;

public interface Packet {
    void decode(ByteBuf in) throws Exception;
    
    void encode(ByteBuf out) throws Exception;
}