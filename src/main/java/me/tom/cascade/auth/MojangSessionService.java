package me.tom.cascade.auth;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.tom.cascade.MojangUUIDAdapter;

public class MojangSessionService {

    private static final Gson GSON = new GsonBuilder()
    	    .registerTypeAdapter(UUID.class, new MojangUUIDAdapter())
    	    .create();

    public static GameProfile hasJoined(String username, String serverIdHash, String ip) {
        try {
            StringBuilder url = new StringBuilder("https://sessionserver.mojang.com/session/minecraft/hasJoined");
            url.append("?username=").append(URLEncoder.encode(username, StandardCharsets.UTF_8.name()));
            url.append("&serverId=").append(URLEncoder.encode(serverIdHash, StandardCharsets.UTF_8.name()));

            if (ip != null) {
                url.append("&ip=").append(URLEncoder.encode(ip, StandardCharsets.UTF_8.name()));
            }

            HttpURLConnection conn = (HttpURLConnection) new URL(url.toString()).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            if (code != 200) {
                return null;
            }

            try (InputStream in = conn.getInputStream();
                 Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {

                return GSON.fromJson(reader, GameProfile.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}