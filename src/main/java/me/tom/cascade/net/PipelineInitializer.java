package me.tom.cascade.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.tom.cascade.net.handlers.ConnectionHandler;
import me.tom.cascade.net.handlers.HandshakeHandler;
import me.tom.cascade.net.handlers.PingRequestHandler;
import me.tom.cascade.net.handlers.StatusRequestHandler;
import me.tom.cascade.protocol.PacketDecoder;
import me.tom.cascade.protocol.PacketEncoder;
import me.tom.cascade.protocol.PacketFramer;

public class PipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast("packet-framer", new PacketFramer())
            .addLast("packet-decoder", new PacketDecoder())
            .addLast("packet-encoder", new PacketEncoder())
            .addLast("handshake-handler", new HandshakeHandler())
            .addLast("status-request-handler", new StatusRequestHandler())
            .addLast("ping-request-handler", new PingRequestHandler())
            .addLast("connection-handler", new ConnectionHandler());
    }
}