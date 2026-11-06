package com.hbm.network.client;

import com.hbm.client.ClientModelDataHandler;
import com.hbm.network.PacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ModelDataUpdatePacket extends PacketBase implements PacketBase.DecodablePacket {

    private BlockPos pos;

    public ModelDataUpdatePacket() {}

    public ModelDataUpdatePacket(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new ModelDataUpdatePacket(buf.readBlockPos());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // На сервере пакет только отправляется, обрабатывать нечего
            if (context.get().getSender() != null) return;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientModelDataHandler.handle(pos));
        });
        context.get().setPacketHandled(true);
    }
}