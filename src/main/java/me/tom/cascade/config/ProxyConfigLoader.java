package me.tom.cascade.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ProxyConfigLoader {
    private static final File propertiesFile = new File("proxy.properties");

    public static ProxyConfig load() {
        Properties props = new Properties();

        try (Reader reader = new InputStreamReader(
                new FileInputStream(propertiesFile), StandardCharsets.UTF_8)) {

            props.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }

        ProxyConfig config = new ProxyConfig();
        config.setProxyPort(Short.parseShort(props.getProperty("proxy_port")));
        config.setTargetHost(props.getProperty("target_host"));
        config.setTargetPort(Short.parseShort(props.getProperty("target_port")));

        return config;
    }
}