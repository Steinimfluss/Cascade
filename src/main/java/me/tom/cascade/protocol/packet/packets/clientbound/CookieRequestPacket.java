package me.tom.cascade.protocol.packet.packets.clientbound;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.types.Utf8String;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CookieRequestPacket implements Packet {
	private String key;

    @Override
    public void decode(ByteBuf in) {
    	key = Utf8String.read(in, 32767);
    }

    @Override
    public void encode(ByteBuf out) {
    	Utf8String.write(out, key, 32767);
    }
}