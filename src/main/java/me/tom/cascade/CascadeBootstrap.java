package me.tom.cascade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.security.Keys;
import me.tom.cascade.config.ProxyConfig;
import me.tom.cascade.config.ProxyConfigLoader;
import me.tom.cascade.net.CascadeProxy;
import me.tom.cascade.util.MojangUUIDAdapter;

public class CascadeBootstrap 
{
	public static final ProxyConfig CONFIG = ProxyConfigLoader.load();
	public static final Key JWT_KEY = Keys.hmacShaKeyFor(CONFIG.getJwtSecret().getBytes());
	
	private static final CascadeProxy PROXY = new CascadeProxy(CONFIG.getProxyPort());
	
	public static final Gson GSON = new GsonBuilder()
    	    .registerTypeAdapter(UUID.class, new MojangUUIDAdapter())
    	    .create();
	

	public static final String PROXY_STATUS_JSON;
	public static final String INVALID_TOKEN_JSON;

	static {
	    try {
	        PROXY_STATUS_JSON = new String(Files.readAllBytes(Paths.get("status.json")));
	        INVALID_TOKEN_JSON = new String(Files.readAllBytes(Paths.get("invalid_token.json")));
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to load status.json", e);
	    }
	}
			
	
    public static void main( String[] args ) throws InterruptedException
    {
    	PROXY.start();
    }
}
