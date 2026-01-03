package me.tom.cascade.net.pipeline;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import me.tom.cascade.net.NetworkSide;
import me.tom.cascade.protocol.codec.PacketDecoder;
import me.tom.cascade.protocol.codec.PacketEncoder;
import me.tom.cascade.protocol.codec.PacketFramer;

@AllArgsConstructor
public class BackendInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
        	.addLast("packet-framer", new PacketFramer())
        	.addLast("packet-decoder", new PacketDecoder(NetworkSide.CLIENTBOUND))
        	.addLast("packet-encoder", new PacketEncoder(NetworkSide.SERVERBOUND));
    }
}