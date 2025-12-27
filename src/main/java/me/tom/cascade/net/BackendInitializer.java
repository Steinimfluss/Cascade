package me.tom.cascade.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import me.tom.cascade.net.handlers.ConnectionHandler;
import me.tom.cascade.net.handlers.ServerToClientHandler;
import me.tom.cascade.protocol.PacketDecoder;
import me.tom.cascade.protocol.PacketEncoder;
import me.tom.cascade.protocol.PacketFramer;

@AllArgsConstructor
public class BackendInitializer extends ChannelInitializer<SocketChannel> {

    private final Channel clientChannel;

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
        	.addLast("packet-framer", new PacketFramer())
        	.addLast("packet-encoder", new PacketEncoder())
        	.addLast("packet-decoder", new PacketDecoder());
    }
}