package me.tom.cascade.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.tom.cascade.net.types.VarInt;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.packet.PacketRegistry;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final PacketRegistry registry = new PacketRegistry();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        ByteBuf body = ctx.alloc().buffer();

        System.out.println(packet);
        ConnectionState state = ctx.channel().attr(ProtocolAttributes.STATE).get();
        int packetId = registry.getPacketId(packet.getClass(), state);
        System.out.println(state + " " + packetId);

        if (packetId == -1) {
            throw new IllegalStateException("Sent unknown packet ID for " + packet.getClass().getSimpleName() + " in state " + state);
        }

        VarInt.write(body, packetId);

        packet.encode(body);

        VarInt.write(out, body.readableBytes());

        out.writeBytes(body);
        System.out.println("Sent " + packetId + " " + state + " " + ctx.name() + " " + packet);

        body.release();
    }
}