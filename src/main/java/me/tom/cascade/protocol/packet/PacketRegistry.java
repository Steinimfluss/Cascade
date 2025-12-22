package me.tom.cascade.protocol.packet;

import java.util.HashMap;
import java.util.Map;

import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.packet.packets.HandshakePacket;

public class PacketRegistry {

    private final Map<Integer, Class<? extends Packet>> handshakePackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> statusPackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> loginPackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> playPackets = new HashMap<>();

    public PacketRegistry() {
        handshakePackets.put(0x00, HandshakePacket.class);
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
}