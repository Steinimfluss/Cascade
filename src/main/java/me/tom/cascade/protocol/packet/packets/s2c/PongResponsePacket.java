package me.tom.cascade.protocol.packet.packets.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.tom.cascade.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
public class PongResponsePacket implements Packet {
	
    public long timestamp;

    @Override
    public void decode(ByteBuf in) {
    	in.readLong();
    }

    @Override
    public void encode(ByteBuf out) {
    	out.writeLong(timestamp);
    }
}