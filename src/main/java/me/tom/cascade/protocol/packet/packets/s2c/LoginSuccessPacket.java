package me.tom.cascade.protocol.packet.packets.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tom.cascade.auth.MojangProfile;
import me.tom.cascade.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginSuccessPacket implements Packet {
	private MojangProfile profile;

    @Override
    public void decode(ByteBuf in) {
    	profile = MojangProfile.read(in);
    }

    @Override
    public void encode(ByteBuf out) {
    	profile.write(out);
    }
}