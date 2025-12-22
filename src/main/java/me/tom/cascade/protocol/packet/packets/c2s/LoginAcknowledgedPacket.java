package me.tom.cascade.protocol.packet.packets.c2s;

import io.netty.buffer.ByteBuf;
import me.tom.cascade.protocol.packet.Packet;

public class LoginAcknowledgedPacket implements Packet {

    @Override
    public void decode(ByteBuf in) {}

    @Override
    public void encode(ByteBuf out) {}
}