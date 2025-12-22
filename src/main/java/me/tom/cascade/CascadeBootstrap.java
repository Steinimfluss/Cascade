package me.tom.cascade;

import me.tom.cascade.net.CascadeServer;

public class CascadeBootstrap 
{
	private static final CascadeServer SERVER = new CascadeServer(25564);
	
    public static void main( String[] args ) throws InterruptedException
    {
    	SERVER.start();
    }
}
