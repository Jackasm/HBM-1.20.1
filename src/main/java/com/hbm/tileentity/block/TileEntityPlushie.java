package com.hbm.tileentity.block;

import com.hbm.blocks.generic.BlockPlushie.PlushieType;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityPlushie extends BlockEntity {

    public PlushieType type = PlushieType.NONE;
    public int squishTimer;

    public TileEntityPlushie(BlockPos pos, BlockState state) {
        super(ModTileEntity.PLUSHIE.get(), pos, state);
    }

    public void tick() {
        if (squishTimer > 0) squishTimer--;
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
        this.type = PlushieType.values()[Math.abs(typeOrdinal) % PlushieType.values().length];
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("type", type.ordinal());
    }

    public void transformTE(Level world, int coordBaseMode) {
        type = PlushieType.values()[world.random.nextInt(PlushieType.values().length - 1) + 1];
    }
}