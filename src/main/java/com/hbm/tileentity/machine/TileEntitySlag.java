package com.hbm.tileentity.machine;

import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntitySlag extends TileEntityLoadedBase {

    public NTMMaterial mat;
    public int amount;
    public final int maxAmount = MaterialShapes.BLOCK.q(4);

    public TileEntitySlag(BlockPos pos, BlockState state) {
        super(ModTileEntity.SLAG.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntitySlag te) {
        // Шлак просто лежит, ничего не делает
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.mat = Mats.matById.get(nbt.getInt("mat"));
        this.amount = nbt.getInt("amount");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("mat", this.mat != null ? this.mat.id : -1);
        nbt.putInt("amount", this.amount);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }
}