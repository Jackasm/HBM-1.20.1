package com.hbm.tileentity.machine.pile;

import com.hbm.api.block.IPileNeutronReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TileEntityPileBreedingFuel extends TileEntityPileBase implements IPileNeutronReceiver {

    public int neutrons;
    public int lastNeutrons;
    public int progress;
    public static final int maxProgress = GeneralConfig.enable528.get() ? 50000 : 30000;

    public TileEntityPileBreedingFuel(BlockPos pos, BlockState state) {
        super(ModTileEntity.PILE_BREEDING_FUEL.get(), pos, state);
    }

    public void tick() {
        if (!Objects.requireNonNull(level).isClientSide) {
            react();

            if (this.progress >= maxProgress) {
                level.setBlock(worldPosition, ModBlocks.BLOCK_GRAPHITE_TRITIUM.get().defaultBlockState(), 3);
            }
        }
    }

    private void react() {
        this.lastNeutrons = this.neutrons;
        this.progress += this.neutrons;

        this.neutrons = 0;

        if (lastNeutrons <= 0) return;

        for (int i = 0; i < 2; i++) {
            this.castRay(1);
        }
    }

    @Override
    public void receiveNeutrons(int n) {
        this.neutrons += n;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.progress = nbt.getInt("progress");
        this.neutrons = nbt.getInt("neutrons");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("progress", this.progress);
        nbt.putInt("neutrons", this.neutrons);
    }
}