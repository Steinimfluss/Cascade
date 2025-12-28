package me.tom.cascade.protocol.packet.packets.serverbound;

import io.netty.buffer.ByteBuf;
import me.tom.cascade.protocol.packet.Packet;

public class StatusRequestPacket implements Packet {
    @Override
    public void decode(ByteBuf in) {}

    @Override
    public void encode(ByteBuf out) {}
}