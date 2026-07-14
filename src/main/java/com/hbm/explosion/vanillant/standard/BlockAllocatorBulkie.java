package com.hbm.explosion.vanillant.standard;

import java.util.HashSet;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockAllocatorBulkie implements IBlockAllocator {

    protected double maximum;
    protected int resolution;

    public BlockAllocatorBulkie(double maximum) {
        this(maximum, 16);
    }

    public BlockAllocatorBulkie(double maximum, int resolution) {
        this.resolution = resolution;
        this.maximum = maximum;
    }

    @Override
    public HashSet<BlockPos> allocate(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
        HashSet<BlockPos> affectedBlocks = new HashSet<>();

        for (int i = 0; i < this.resolution; ++i) {
            for (int j = 0; j < this.resolution; ++j) {
                for (int k = 0; k < this.resolution; ++k) {
                    if (i == 0 || i == this.resolution - 1 ||
                            j == 0 || j == this.resolution - 1 ||
                            k == 0 || k == this.resolution - 1) {

                        double d0 = (double) ((float) i / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
                        double d1 = (double) ((float) j / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
                        double d2 = (double) ((float) k / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;

                        double currentX = x;
                        double currentY = y;
                        double currentZ = z;
                        double dist = 0;

                        for (float stepSize = 0.3F; dist <= size; stepSize = 0.3F) {
                            double deltaX = currentX - x;
                            double deltaY = currentY - y;
                            double deltaZ = currentZ - z;
                            dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                            int blockX = Mth.floor(currentX);
                            int blockY = Mth.floor(currentY);
                            int blockZ = Mth.floor(currentZ);
                            BlockPos pos = new BlockPos(blockX, blockY, blockZ);
                            BlockState state = world.getBlockState(pos);

                            if (!state.isAir()) {
                                float blockResistance = state.getBlock().getExplosionResistance(state, world, pos, explosion.compat);
                                if (this.maximum < blockResistance) {
                                    break; // слишком прочный блок — луч останавливается
                                }
                                // блок можно разрушить, добавляем
                                affectedBlocks.add(pos);
                            }

                            currentX += d0 * stepSize;
                            currentY += d1 * stepSize;
                            currentZ += d2 * stepSize;
                        }
                    }
                }
            }
        }

        return affectedBlocks;
    }

    // Методы для настройки поведения аллокатора

    public BlockAllocatorBulkie withMaximumResistance(double maximum) {
        this.maximum = maximum;
        return this;
    }

    public BlockAllocatorBulkie withResolution(int resolution) {
        this.resolution = resolution;
        return this;
    }

    // Статические фабричные методы для удобства

    public static BlockAllocatorBulkie forWeakExplosions() {
        return new BlockAllocatorBulkie(10.0); // Низкое сопротивление
    }

    public static BlockAllocatorBulkie forMediumExplosions() {
        return new BlockAllocatorBulkie(30.0); // Среднее сопротивление
    }

    public static BlockAllocatorBulkie forStrongExplosions() {
        return new BlockAllocatorBulkie(100.0); // Высокое сопротивление
    }

    public static BlockAllocatorBulkie forObsidianBreaking() {
        return new BlockAllocatorBulkie(1200.0, 32); // Очень высокое сопротивление, больше лучей
    }

    public static BlockAllocatorBulkie forCustom(double maximumResistance, int rayCount) {
        return new BlockAllocatorBulkie(maximumResistance, rayCount);
    }
}