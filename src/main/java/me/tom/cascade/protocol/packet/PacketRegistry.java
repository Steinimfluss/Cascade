package me.tom.cascade.protocol.packet;

import java.util.HashMap;
import java.util.Map;

import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.packet.packets.c2s.EncryptionRequestPacket;
import me.tom.cascade.protocol.packet.packets.c2s.HandshakePacket;
import me.tom.cascade.protocol.packet.packets.c2s.LoginStartPacket;
import me.tom.cascade.protocol.packet.packets.c2s.PingRequestPacket;
import me.tom.cascade.protocol.packet.packets.c2s.StatusRequestPacket;
import me.tom.cascade.protocol.packet.packets.s2c.PongResponsePacket;
import me.tom.cascade.protocol.packet.packets.s2c.StatusResponsePacket;

public class PacketRegistry {
    private final Map<Integer, Class<? extends Packet>> handshakePackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> statusPackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> loginPackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> playPackets = new HashMap<>();

    private final Map<Class<? extends Packet>, Integer> handshakeIds = new HashMap<>();
    private final Map<Class<? extends Packet>, Integer> statusIds = new HashMap<>();
    private final Map<Class<? extends Packet>, Integer> loginIds = new HashMap<>();
    private final Map<Class<? extends Packet>, Integer> playIds = new HashMap<>();

    public PacketRegistry() {
        register(handshakePackets, handshakeIds, 0x00, HandshakePacket.class);
        
        register(statusPackets, statusIds, 0x00, StatusRequestPacket.class);
        register(statusPackets, statusIds, 0x01, PingRequestPacket.class);
        
        register(loginPackets, loginIds, 0x00, LoginStartPacket.class);

        statusIds.put(StatusResponsePacket.class, 0x00);
        statusIds.put(PongResponsePacket.class, 0x01);
        
        loginIds.put(EncryptionRequestPacket.class, 0x01);
    }

    private void register(Map<Integer, Class<? extends Packet>> forward,
                          Map<Class<? extends Packet>, Integer> reverse,
                          int id,
                          Class<? extends Packet> clazz) {

        forward.put(id, clazz);
        reverse.put(clazz, id);
    }

    public Class<? extends Packet> getPacket(int id, ConnectionState state) {
        switch (state) {
            case HANDSHAKE:
                return handshakePackets.get(id);
            case STATUS:
                return statusPackets.get(id);
            case LOGIN:
                return loginPackets.get(id);
            case PLAY:
                return playPackets.get(id);
            default:
                return null;
        }
    }

    public int getPacketId(Class<? extends Packet> clazz, ConnectionState state) {
        switch (state) {
            case HANDSHAKE:
                return handshakeIds.getOrDefault(clazz, -1);
            case STATUS:
                return statusIds.getOrDefault(clazz, -1);
            case LOGIN:
                return loginIds.getOrDefault(clazz, -1);
            case PLAY:
                return playIds.getOrDefault(clazz, -1);
            default:
                return -1;
        }
    }
}