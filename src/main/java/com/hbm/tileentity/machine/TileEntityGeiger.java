package com.hbm.tileentity.machine;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileEntityGeiger extends BlockEntity {

    int timer = 0;
    float ticker = 0;

    public TileEntityGeiger(BlockPos pos, BlockState state) {
        super(ModTileEntity.GEIGER.get(), pos, state);
    }

    public void tick() {
        if (level == null) return;

        timer++;

        if (timer == 10) {
            timer = 0;
            ticker = check();

            // To update the adjacent comparators
            level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        }

        if (timer % 5 == 0) {
            if (ticker > 0) {
                List<Integer> list = new ArrayList<>();

                if (ticker < 1) list.add(0);
                if (ticker < 5) list.add(0);
                if (ticker < 10) list.add(1);
                if (ticker > 5 && ticker < 15) list.add(2);
                if (ticker > 10 && ticker < 20) list.add(3);
                if (ticker > 15 && ticker < 25) list.add(4);
                if (ticker > 20 && ticker < 30) list.add(5);
                if (ticker > 25) list.add(6);

                int r = list.get(level.random.nextInt(list.size()));

                if (r > 0) {
                    // Выбор звука в зависимости от r
                    SoundEvent sound = switch (r) {
                        case 1 -> ModSounds.GEIGER_1.get();
                        case 2 -> ModSounds.GEIGER_2.get();
                        case 3 -> ModSounds.GEIGER_3.get();
                        case 4 -> ModSounds.GEIGER_4.get();
                        case 5 -> ModSounds.GEIGER_5.get();
                        case 6 -> ModSounds.GEIGER_6.get();
                        default -> null;
                    };
                    if (sound != null) {
                        level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                                sound, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            } else if (level.random.nextInt(50) == 0) {
                // Случайный звук 1 или 2
                SoundEvent sound = level.random.nextBoolean() ? ModSounds.GEIGER_1.get() : ModSounds.GEIGER_2.get();
                level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        sound, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public float check() {
        if (level == null) return 0;
        return RadiationEvents.getRadiation(level, worldPosition);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.ticker = nbt.getFloat("ticker");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putFloat("ticker", this.ticker);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
    }
}