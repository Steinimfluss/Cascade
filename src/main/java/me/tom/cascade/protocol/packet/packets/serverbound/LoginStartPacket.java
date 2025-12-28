package me.tom.cascade.protocol.packet.packets.serverbound;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.types.Utf8String;
import me.tom.cascade.protocol.types.UuidType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginStartPacket implements Packet {

    public String name;
    public UUID uuid;
    
    @Override
    public void decode(ByteBuf in) {
    	name = Utf8String.read(in, 16);
    	uuid = UuidType.read(in);
    }

    @Override
    public void encode(ByteBuf out) {
    	Utf8String.write(out, name, 16);
    	UuidType.write(out, uuid);
    }
}