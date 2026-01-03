package me.tom.cascade.protocol.packet;

import java.util.HashMap;
import java.util.Map;

import me.tom.cascade.net.NetworkSide;
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

public enum PacketRegistry {

    HANDSHAKE {
        {
            serverbound.put(0x00, HandshakePacket.class);
        }
    },

    STATUS {
    	{
    		clientbound.put(0x00, StatusResponsePacket.class);
    		clientbound.put(0x01, PongResponsePacket.class);
    		
    		serverbound.put(0x00, StatusRequestPacket.class);
    		serverbound.put(0x01, PingRequestPacket.class);
    	}
    },
    
    LOGIN {
        {
    		clientbound.put(0x00, DisconnectPacket.class);
    		clientbound.put(0x01, EncryptionRequestPacket.class);
    		clientbound.put(0x02, LoginSuccessPacket.class);
    		clientbound.put(0x05, CookieRequestPacket.class);
    		
            serverbound.put(0x00, LoginStartPacket.class);
            serverbound.put(0x01, EncryptionResponsePacket.class);
            serverbound.put(0x03, LoginAcknowledgedPacket.class);
            serverbound.put(0x04, CookieResponsePacket.class);
        }
    },
    
    TRANSFER,
     
    CONFIGURATION {
    	{
    		clientbound.put(0x0A, StoreCookiePacket.class);
    		clientbound.put(0x0B, TransferPacket.class);
    	}
    };

    protected final Map<Integer, Class<? extends Packet>> clientbound = new HashMap<>();
    protected final Map<Integer, Class<? extends Packet>> serverbound = new HashMap<>();

    public Class<? extends Packet> getPacket(NetworkSide dir, int id) {
        switch (dir) {
	        case CLIENTBOUND:
	            return clientbound.get(id);
            case SERVERBOUND:
                return serverbound.get(id);
            default:
                return null;
        }
    }
    
    public int getPacketId(NetworkSide dir, Class<? extends Packet> clazz) {
        Map<Integer, Class<? extends Packet>> map;

        switch (dir) {
            case CLIENTBOUND:
                map = clientbound;
                break;
            case SERVERBOUND:
                map = serverbound;
                break;
            default:
                return -1;
        }

        for (Map.Entry<Integer, Class<? extends Packet>> entry : map.entrySet()) {
            if (entry.getValue().equals(clazz)) {
                return entry.getKey();
            }
        }

        return -1;
    }
}