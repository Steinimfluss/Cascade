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

        ConnectionState state = ctx.channel().attr(ProtocolAttributes.STATE).get();
        if (state == null) {
            state = ConnectionState.HANDSHAKE;
        }
        int packetId = registry.getPacketId(packet.getClass(), state);
        if (packetId == -1) {
            throw new IllegalStateException("Unknown packet ID for " + packet.getClass().getSimpleName() + " in state " + state);
        }

        VarInt.write(body, packetId);

        packet.encode(body);

        VarInt.write(out, body.readableBytes());

        out.writeBytes(body);

        body.release();
    }
}