package me.tom.cascade.protocol;

import io.netty.util.AttributeKey;
import me.tom.cascade.auth.GameProfile;
import me.tom.cascade.protocol.packet.packets.c2s.HandshakePacket;
import me.tom.cascade.protocol.packet.packets.c2s.LoginStartPacket;

public class ProtocolAttributes {
    public static final AttributeKey<ConnectionState> STATE =
            AttributeKey.valueOf("cascade-connection-state");
    
    public static final AttributeKey<ConnectionState> INTENT =
            AttributeKey.valueOf("cascade-connection-intent");
    
    public static final AttributeKey<byte[]> VERIFY_TOKEN =
            AttributeKey.valueOf("cascade-verify-token");
    
    public static final AttributeKey<String> USERNAME =
            AttributeKey.valueOf("cascade-username");
    
    public static final AttributeKey<Integer> PROTOCOL_VERSION =
            AttributeKey.valueOf("cascade-protocol-version");
    
    public static final AttributeKey<String> HOSTNAME =
            AttributeKey.valueOf("cascade-hostname");
    
    public static final AttributeKey<Integer> PORT =
            AttributeKey.valueOf("cascade-port");
    
    public static final AttributeKey<GameProfile> GAME_PROFILE =
            AttributeKey.valueOf("cascade-game-profile");
    
    public static final AttributeKey<HandshakePacket> HANDSHAKE_PACKET =
            AttributeKey.valueOf("cascade-handshake-packet");
    
    public static final AttributeKey<LoginStartPacket> LOGIN_START_PACKET =
            AttributeKey.valueOf("cascade-login-start-packet");
}