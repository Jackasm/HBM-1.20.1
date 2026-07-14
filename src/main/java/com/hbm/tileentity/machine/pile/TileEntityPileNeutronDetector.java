package com.hbm.tileentity.machine.pile;

import com.hbm.api.block.IPileNeutronReceiver;
import com.hbm.blocks.machine.pile.BlockGraphiteDrilledBase;
import com.hbm.blocks.machine.pile.BlockGraphiteNeutronDetector;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TileEntityPileNeutronDetector extends TileEntityPileBase implements IPileNeutronReceiver {

    public int lastNeutrons;
    public int neutrons;
    public int maxNeutrons = 10;

    public TileEntityPileNeutronDetector(BlockPos pos, BlockState state) {
        super(ModTileEntity.PILE_NEUTRON_DETECTOR.get(), pos, state);
    }

    public void tick() {
        if (!Objects.requireNonNull(level).isClientSide) {
            BlockState state = getBlockState();
            int meta = 0;
            if (state.hasProperty(BlockGraphiteDrilledBase.META)) {
                meta = state.getValue(BlockGraphiteDrilledBase.META);
            }

            // Альтернатива - использовать IntegerProperty

            if (this.neutrons >= this.maxNeutrons && (meta & 8) > 0) {
                if (state.getBlock() instanceof BlockGraphiteNeutronDetector detector) {
                    detector.triggerRods(level, worldPosition);
                }
            }
            if (this.neutrons < this.maxNeutrons && this.lastNeutrons < this.maxNeutrons && (meta & 8) == 0) {
                if (state.getBlock() instanceof BlockGraphiteNeutronDetector detector) {
                    detector.triggerRods(level, worldPosition);
                }
            }

            this.lastNeutrons = this.neutrons;
            this.neutrons = 0;
        }
    }

    @Override
    public void receiveNeutrons(int n) {
        this.neutrons += n;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.maxNeutrons = nbt.getInt("maxNeutrons");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("maxNeutrons", this.maxNeutrons);
    }
}