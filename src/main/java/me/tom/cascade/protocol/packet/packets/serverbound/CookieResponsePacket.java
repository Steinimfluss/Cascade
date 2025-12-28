package me.tom.cascade.protocol.packet.packets.serverbound;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.types.ByteArray;
import me.tom.cascade.protocol.types.Utf8String;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CookieResponsePacket implements Packet {
	private String key;
	private byte[] payload;

    @Override
    public void decode(ByteBuf in) {
    	key = Utf8String.read(in, 32767);
    	payload = ByteArray.read(in);
    }

    @Override
    public void encode(ByteBuf out) {
    	Utf8String.write(out, key, 32767);
    	ByteArray.write(out, payload);
    }
}