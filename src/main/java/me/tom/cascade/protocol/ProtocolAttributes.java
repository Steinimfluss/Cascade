package me.tom.cascade.protocol;

import io.netty.util.AttributeKey;

public class ProtocolAttributes {
    public static final AttributeKey<ConnectionState> STATE =
            AttributeKey.valueOf("cascade-connection-state");
}