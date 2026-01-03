package me.tom.cascade.protocol;

import me.tom.cascade.protocol.packet.PacketRegistry;

public enum ConnectionState {
    HANDSHAKE(PacketRegistry.HANDSHAKE),
    STATUS(PacketRegistry.STATUS),
    LOGIN(PacketRegistry.LOGIN),
    TRANSFER(PacketRegistry.TRANSFER),
    CONFIGURATION(PacketRegistry.CONFIGURATION);

    private final PacketRegistry registry;

    ConnectionState(PacketRegistry registry) {
        this.registry = registry;
    }

    public PacketRegistry getRegistry() {
        return registry;
    }
}