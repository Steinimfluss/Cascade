package me.tom.cascade.protocol.packet.packets;

import io.netty.buffer.ByteBuf;
import me.tom.cascade.net.types.VarInt;
import me.tom.cascade.protocol.packet.Packet;

public class HandshakePacket implements Packet {

    public int protocolVersion;
    public String hostname;
    public int port;
    public int nextState;

    @Override
    public void decode(ByteBuf in) {
        protocolVersion = VarInt.read(in);

        int hostLength = VarInt.read(in);
        hostname = in.readCharSequence(hostLength, java.nio.charset.StandardCharsets.UTF_8).toString();

        port = in.readUnsignedShort();
        nextState = VarInt.read(in);
    }

    @Override
    public void encode(ByteBuf out) {
        VarInt.write(out, protocolVersion);
        VarInt.write(out, hostname.length());
        out.writeCharSequence(hostname, java.nio.charset.StandardCharsets.UTF_8);
        out.writeShort(port);
        VarInt.write(out, nextState);
    }
}