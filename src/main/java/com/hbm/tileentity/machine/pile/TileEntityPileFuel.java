package com.hbm.tileentity.machine.pile;

import com.hbm.api.block.IPileNeutronReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.pile.BlockGraphiteDrilledBase;
import com.hbm.config.GeneralConfig;

import com.hbm.network.PacketDispatcher;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TileEntityPileFuel extends TileEntityPileBase implements IPileNeutronReceiver {

    public int heat;
    public static final int maxHeat = 1000;
    public int neutrons;
    public int lastNeutrons;
    public int progress;
    public static final int maxProgress = GeneralConfig.enable528.get() ? 75000 : 50000;

    public TileEntityPileFuel(BlockPos pos, BlockState state) {
        super(ModTileEntity.PILE_FUEL.get(), pos, state);
    }

    public void tick() {
        if (!Objects.requireNonNull(level).isClientSide) {
            dissipateHeat();
            checkRedstone(react());
            transmute();

            if (this.heat >= maxHeat) {
                level.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                        4.0F, Level.ExplosionInteraction.TNT);
                level.setBlock(worldPosition, ModBlocks.GAS_RADON_DENSE.get().defaultBlockState(), 3);
            }

            if (level.random.nextFloat() * 2F <= this.heat / (float) maxHeat) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "vanillaExt");
                data.putString("mode", "smoke");
                data.putDouble("mY", 0.05);
                data.putDouble("posX", worldPosition.getX() + 0.25 + level.random.nextDouble() * 0.5);
                data.putDouble("posY", worldPosition.getY() + 1);
                data.putDouble("posZ", worldPosition.getZ() + 0.25 + level.random.nextDouble() * 0.5);
                // Отправка частиц
                PacketDispatcher.sendAuxParticleNT(data,
                        data.getDouble("posX"), data.getDouble("posY"), data.getDouble("posZ"), null);
            }

            if (this.progress >= maxProgress) {
                BlockState newState = ModBlocks.BLOCK_GRAPHITE_PLUTONIUM.get().defaultBlockState();
                level.setBlock(worldPosition, newState, 3);
            }
        }
    }

    private void dissipateHeat() {
        BlockState state = getBlockState();
        int meta = ((BlockGraphiteDrilledBase) state.getBlock()).getMeta(state);
        this.heat -= (meta & 4) == 4 ? heat * 0.065 : heat * 0.05;
    }

    private int react() {
        int reaction = (int) (this.neutrons * (1D - ((double) this.heat / (double) maxHeat) * 0.5D));

        this.lastNeutrons = this.neutrons;
        this.neutrons = 0;

        int lastProgress = this.progress;
        this.progress += reaction;

        if (reaction <= 0) return lastProgress;

        this.heat += reaction;

        for (int i = 0; i < 12; i++) {
            this.castRay((int) Math.max(reaction * 0.25, 1));
        }

        return lastProgress;
    }

    private void checkRedstone(int lastProgress) {
        int lastLevel = Mth.clamp((lastProgress * 16) / maxProgress, 0, 15);
        int newLevel = Mth.clamp((progress * 16) / maxProgress, 0, 15);
        if (lastLevel != newLevel) {
            Objects.requireNonNull(level).updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }

    private void transmute() {
        BlockState state = getBlockState();
        Block block = state.getBlock();

        int meta = 0;
        if (state.hasProperty(BlockGraphiteDrilledBase.META)) {
            meta = state.getValue(BlockGraphiteDrilledBase.META);
        }

        if ((meta & 8) == 8) {
            if (this.progress < maxProgress - 1000) {
                this.progress = maxProgress - 1000;
            }
            return;
        } else if (this.progress >= maxProgress - 1000) {
            // Установить мету с битом 8
            if (block instanceof BlockGraphiteDrilledBase base) {
                BlockState newState = base.setMeta(state, meta | 8);
                Objects.requireNonNull(level).setBlock(worldPosition, newState, 3);
            }
            return;
        }
    }

    @Override
    public void receiveNeutrons(int n) {
        this.neutrons += n;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.heat = nbt.getInt("heat");
        this.progress = nbt.getInt("progress");
        this.neutrons = nbt.getInt("neutrons");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("heat", this.heat);
        nbt.putInt("progress", this.progress);
        nbt.putInt("neutrons", this.neutrons);
    }
}