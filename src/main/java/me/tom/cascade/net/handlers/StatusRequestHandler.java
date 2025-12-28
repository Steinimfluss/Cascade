package me.tom.cascade.net.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.protocol.packet.packets.clientbound.StatusResponsePacket;
import me.tom.cascade.protocol.packet.packets.serverbound.StatusRequestPacket;

public class StatusRequestHandler extends SimpleChannelInboundHandler<StatusRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StatusRequestPacket packet) {
    	String json = "{\r\n"
    			+ "              \"version\": {\r\n"
    			+ "                \"name\": \"Cascade\",\r\n"
    			+ "                \"protocol\": 47\r\n"
    			+ "              },\r\n"
    			+ "              \"players\": {\r\n"
    			+ "                \"max\": 100,\r\n"
    			+ "                \"online\": 0\r\n"
    			+ "              },\r\n"
    			+ "              \"description\": {\r\n"
    			+ "                \"text\": \"§aCascade Proxy\"\r\n"
    			+ "              }\r\n"
    			+ "            }";

            StatusResponsePacket response = new StatusResponsePacket(json);

            ctx.writeAndFlush(response);
    }
}