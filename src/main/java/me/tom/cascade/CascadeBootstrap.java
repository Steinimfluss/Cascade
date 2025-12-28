package me.tom.cascade;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.tom.cascade.config.ConfigLoader;
import me.tom.cascade.config.ProxyConfig;
import me.tom.cascade.net.CascadeServer;
import me.tom.cascade.util.MojangUUIDAdapter;

public class CascadeBootstrap 
{
	public static final ProxyConfig CONFIG = ConfigLoader.load();
	
	private static final CascadeServer SERVER = new CascadeServer(CONFIG.getProxyPort());
	
	public static final Gson GSON = new GsonBuilder()
    	    .registerTypeAdapter(UUID.class, new MojangUUIDAdapter())
    	    .create();
	
    public static void main( String[] args ) throws InterruptedException
    {
    	SERVER.start();
    }
}
