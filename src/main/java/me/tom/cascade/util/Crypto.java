package me.tom.cascade.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class Crypto {

    public static final KeyPair KEY_PAIR;

    static {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024);
            KEY_PAIR = gen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA keypair", e);
        }
    }
}