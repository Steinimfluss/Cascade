package me.tom.cascade.protocol.packet.packets.serverbound;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PingRequestPacket implements Packet {

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