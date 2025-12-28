package me.tom.cascade.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.tom.cascade.protocol.ConnectionState;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.Packet;
import me.tom.cascade.protocol.packet.PacketRegistry;
import me.tom.cascade.protocol.types.VarInt;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final PacketRegistry registry = new PacketRegistry();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        ByteBuf body = ctx.alloc().buffer();

        ConnectionState state = ctx.channel().attr(ProtocolAttributes.STATE).get();
        int packetId = registry.getPacketId(packet.getClass(), state);

        if (packetId == -1) {
            return;
        }

        VarInt.write(body, packetId);

        packet.encode(body);

        VarInt.write(out, body.readableBytes());

        out.writeBytes(body);

        body.release();
    }
}