package com.hbm.network.client;

import com.hbm.main.MainRegistry;
import com.hbm.network.PacketBase;
import com.hbm.tileentity.IBufPacketReceiver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BufPacket extends PacketBase implements PacketBase.DecodablePacket {

    private BlockPos pos;
    private IBufPacketReceiver receiver;
    private ByteBuf data;

    public BufPacket() {
        // Для десериализации
    }

    public BufPacket(BlockPos pos, IBufPacketReceiver receiver) {
        this.pos = pos;
        this.receiver = receiver;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);

        // Создаём буфер для данных и сериализуем в него
        ByteBuf dataBuf = Unpooled.buffer();
        receiver.serialize(dataBuf);

        // Записываем длину данных и сами данные
        buf.writeInt(dataBuf.readableBytes());
        buf.writeBytes(dataBuf);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleClient);
        });
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null || data == null) return;

        BlockEntity te = mc.level.getBlockEntity(pos);

        if (te instanceof IBufPacketReceiver receiver) {
            try {
                receiver.deserialize(data);
            } catch (Exception e) {
                MainRegistry.logger.warn("A ByteBuf packet failed to be read and has thrown an error. This normally means that there was a buffer underflow and more data was read than was actually in the packet.");
                MainRegistry.logger.warn("Tile: {}", te.getBlockState().getBlock().getDescriptionId());
                MainRegistry.logger.warn(e.getMessage());
            } finally {
                data.release();
            }
        }
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();

        int length = buf.readInt();
        this.data = Unpooled.buffer(length);
        buf.readBytes(this.data, length);

        return this;
    }
}