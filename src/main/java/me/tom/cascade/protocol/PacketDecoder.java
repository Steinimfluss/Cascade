package me.tom.cascade.protocol;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.tom.cascade.net.types.VarInt;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.packet.PacketRegistry;

public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final PacketRegistry registry = new PacketRegistry();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int packetId = VarInt.read(in);

        ConnectionState state = ctx.channel().attr(ProtocolAttributes.STATE).get();
        if (state == null) {
            state = ConnectionState.HANDSHAKE;
            ctx.channel().attr(ProtocolAttributes.STATE).set(state);
        }

        Class<? extends Packet> clazz = registry.getPacket(packetId, state);
        if (clazz == null) {
            System.out.println("[Cascade] Unknown packet ID " + packetId + " in state " + state);
            return;
        }

        Packet packet = clazz.newInstance();
        packet.decode(in);

        out.add(packet);
    }
}