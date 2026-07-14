package com.hbm.tileentity.block;

import com.hbm.blocks.generic.BlockSnowglobe.SnowglobeType;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntitySnowglobe extends BlockEntity {

    public SnowglobeType type = SnowglobeType.NONE;

    public TileEntitySnowglobe(BlockPos pos, BlockState state) {
        super(ModTileEntity.SNOWGLOBE.get(), pos, state);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.load(tag);
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        int typeOrdinal = nbt.getInt("type");
        this.type = SnowglobeType.values()[Math.abs(typeOrdinal) % SnowglobeType.values().length];
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("type", type.ordinal());
    }
}