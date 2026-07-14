package com.hbm.network.client;

import com.hbm.network.PacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
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
            Level level = context.get().getSender() != null ?
                    Objects.requireNonNull(context.get().getSender()).level() :
                    net.minecraft.client.Minecraft.getInstance().level;

            if (level != null && level.isClientSide()) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity != null) {
                    blockEntity.requestModelDataUpdate();
                    level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}