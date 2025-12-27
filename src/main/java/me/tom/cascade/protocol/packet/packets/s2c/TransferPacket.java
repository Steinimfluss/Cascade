package me.tom.cascade.protocol.packet.packets.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.net.types.Utf8String;
import me.tom.cascade.net.types.VarInt;
import me.tom.cascade.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TransferPacket implements Packet {
	private String host;
	private int port;
	
	@Override
	public void decode(ByteBuf in) throws Exception {
		host = Utf8String.read(in, 32767);
		port = VarInt.read(in);
	}
	
	@Override
	public void encode(ByteBuf out) throws Exception {
		Utf8String.write(out, host, 32767);
		VarInt.write(out, port);
	}
}