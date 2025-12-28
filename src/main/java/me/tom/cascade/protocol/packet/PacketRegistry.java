package me.tom.cascade.protocol.packet;

import java.util.HashMap;
import java.util.Map;

import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.packet.packets.clientbound.CookieRequestPacket;
import me.tom.cascade.protocol.packet.packets.clientbound.DisconnectPacket;
import me.tom.cascade.protocol.packet.packets.clientbound.EncryptionResponsePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.LoginSuccessPacket;
import me.tom.cascade.protocol.packet.packets.clientbound.PongResponsePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.StatusResponsePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.StoreCookiePacket;
import me.tom.cascade.protocol.packet.packets.clientbound.TransferPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.CookieResponsePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.EncryptionRequestPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.HandshakePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginAcknowledgedPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.LoginStartPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.PingRequestPacket;
import me.tom.cascade.protocol.packet.packets.serverbound.StatusRequestPacket;

public class PacketRegistry {
    private final Map<Integer, Class<? extends Packet>> handshakePackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> statusPackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> loginPackets = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> configPackets = new HashMap<>();

    private final Map<Class<? extends Packet>, Integer> handshakeIds = new HashMap<>();
    private final Map<Class<? extends Packet>, Integer> statusIds = new HashMap<>();
    private final Map<Class<? extends Packet>, Integer> loginIds = new HashMap<>();
    private final Map<Class<? extends Packet>, Integer> configIds = new HashMap<>();

    public PacketRegistry() {
        register(handshakePackets, handshakeIds, 0x00, HandshakePacket.class);
        
        register(statusPackets, statusIds, 0x00, StatusRequestPacket.class);
        register(statusPackets, statusIds, 0x01, PingRequestPacket.class);
        
        register(loginPackets, loginIds, 0x00, LoginStartPacket.class);
        register(loginPackets, loginIds, 0x01, EncryptionResponsePacket.class);
        register(loginPackets, loginIds, 0x02, LoginSuccessPacket.class);
        register(loginPackets, loginIds, 0x03, LoginAcknowledgedPacket.class);
        register(loginPackets, loginIds, 0x04, CookieResponsePacket.class);

        handshakeIds.put(HandshakePacket.class, 0x00);
        
        statusIds.put(StatusResponsePacket.class, 0x00);
        statusIds.put(PongResponsePacket.class, 0x01);

        loginIds.put(LoginStartPacket.class, 0x00);
        loginIds.put(DisconnectPacket.class, 0x00);
        loginIds.put(EncryptionRequestPacket.class, 0x01);
        loginIds.put(LoginSuccessPacket.class, 0x02);
        loginIds.put(CookieRequestPacket.class, 0x05);
        
        configIds.put(StoreCookiePacket.class, 0x0A);
        configIds.put(TransferPacket.class, 0x0B);
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
            case CONFIGURATION:
                return configPackets.get(id);
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
            case CONFIGURATION:
                return configIds.getOrDefault(clazz, -1);
            default:
                return -1;
        }
    }
}