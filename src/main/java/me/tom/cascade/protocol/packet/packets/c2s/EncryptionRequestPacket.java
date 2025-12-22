package me.tom.cascade.protocol.packet.packets.c2s;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.tom.cascade.net.types.ByteArray;
import me.tom.cascade.net.types.Utf8String;
import me.tom.cascade.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
public class EncryptionRequestPacket implements Packet {
	
	private String serverId;
	private byte[] publicKey;
	private byte[] verifyToken;
	private boolean shouldAuthenticate;

    @Override
    public void decode(ByteBuf in) {
    	serverId = Utf8String.read(in, 20);
    	publicKey = ByteArray.read(in);
    	verifyToken = ByteArray.read(in);
    	shouldAuthenticate = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
    	Utf8String.write(out, serverId, 20);
    	ByteArray.write(out, publicKey);
    	ByteArray.write(out, verifyToken);
    	out.writeBoolean(shouldAuthenticate);
    }
}