package me.tom.cascade.protocol.packet.packets.c2s;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.net.types.Utf8String;
import me.tom.cascade.net.types.VarInt;
import me.tom.cascade.protocol.packet.Packet;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HandshakePacket implements Packet {

    public int protocolVersion;
    public String hostname;
    public int port;
    public int nextState;

    @Override
    public void decode(ByteBuf in) {
        protocolVersion = VarInt.read(in);
        hostname = Utf8String.read(in, 255);
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