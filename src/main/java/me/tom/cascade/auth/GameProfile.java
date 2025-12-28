package me.tom.cascade.auth;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import me.tom.cascade.protocol.types.Property;
import me.tom.cascade.protocol.types.Utf8String;
import me.tom.cascade.protocol.types.UuidType;
import me.tom.cascade.protocol.types.VarInt;

@Getter
public class GameProfile {
    public UUID id;
    public String name;
    public Property[] properties;

    public void write(ByteBuf out) {
        UuidType.write(out, id);
        Utf8String.write(out, name, 16);

        if (properties == null) {
            VarInt.write(out, 0);
            return;
        }

        int count = Math.min(properties.length, 16);
        VarInt.write(out, count);

        for (int i = 0; i < count; i++) {
            Property p = properties[i];

            Utf8String.write(out, p.name, 64);
            Utf8String.write(out, p.value, 32767);

            if (p.signature != null) {
                out.writeBoolean(true);
                Utf8String.write(out, p.signature, 1024);
            } else {
                out.writeBoolean(false);
            }
        }
    }

    public static GameProfile read(ByteBuf in) {
        GameProfile profile = new GameProfile();

        profile.id = UuidType.read(in);
        profile.name = Utf8String.read(in, 16);

        int count = VarInt.read(in);
        profile.properties = new Property[count];

        for (int i = 0; i < count; i++) {
            Property p = new Property();
            p.name = Utf8String.read(in, 64);
            p.value = Utf8String.read(in, 32767);

            boolean hasSignature = in.readBoolean();
            if (hasSignature) {
                p.signature = Utf8String.read(in, 1024);
            } else {
                p.signature = null;
            }

            profile.properties[i] = p;
        }

        return profile;
    }
}