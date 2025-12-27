package me.tom.cascade.protocol.packet.packets.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.net.types.Utf8String;
import me.tom.cascade.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DisconnectPacket implements Packet {
	private String json;

    @Override
    public void decode(ByteBuf in) {
    	json = Utf8String.read(in, 32767);
    }

    @Override
    public void encode(ByteBuf out) {
    	Utf8String.write(out, json, 32767);
    }
}