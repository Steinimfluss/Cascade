package me.tom.cascade.protocol.packet.packets.c2s;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import me.tom.cascade.net.types.Utf8String;
import me.tom.cascade.net.types.UuidType;
import me.tom.cascade.protocol.packet.Packet;

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