package me.tom.cascade.protocol;

public enum ConnectionState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    CONFIGURATION,
    PLAY
}