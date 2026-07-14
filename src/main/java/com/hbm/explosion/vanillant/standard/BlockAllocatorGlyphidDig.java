package com.hbm.explosion.vanillant.standard;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

public class BlockAllocatorGlyphidDig implements IBlockAllocator {

    protected double maximum;
    protected int resolution;

    public BlockAllocatorGlyphidDig(double maximum) {
        this(maximum, 16);
    }

    public BlockAllocatorGlyphidDig(double maximum, int resolution) {
        this.resolution = resolution;
        this.maximum = maximum;
    }

    @Override
    public HashSet<BlockPos> allocate(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {

        HashSet<BlockPos> affectedBlocks = new HashSet<>();

        for (int i = 0; i < this.resolution; ++i) {
            for (int j = 0; j < this.resolution; ++j) {
                for (int k = 0; k < this.resolution; ++k) {

                    if (i == 0 || i == this.resolution - 1 || j == 0 || j == this.resolution - 1 || k == 0 || k == this.resolution - 1) {

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

                        for (float stepSize = 0.3F; dist <= explosion.size; ) {

                            double deltaX = currentX - x;
                            double deltaY = currentY - y;
                            double deltaZ = currentZ - z;
                            dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                            int blockX = Mth.floor(currentX);
                            int blockY = Mth.floor(currentY);
                            int blockZ = Mth.floor(currentZ);
                            BlockPos pos = new BlockPos(blockX, blockY, blockZ);

                            // Проверяем, что позиция в пределах мира
                            if (blockY < world.getMinBuildHeight() || blockY >= world.getMaxBuildHeight()) break;

                            BlockState state = world.getBlockState(pos);
                            Block block = state.getBlock();

                            // Проверяем, не воздух ли блок
                            if (!state.isAir()) {
                                // Получаем сопротивление блока взрыву
                                // В 1.20.1 используем стандартный метод блока
                                float blockResistance = block.getExplosionResistance(state, world, pos, explosion.compat);

                                // Если сопротивление больше максимального или это спавнер глифидов - останавливаемся
                                if (this.maximum < blockResistance || block == ModBlocks.GLYPHID_SPAWNER.get()) {
                                    break;
                                }
                            }

                            // Проверяем, можно ли разрушить блок
                            // В 1.20.1 проверяем через block.getExplosionResistance
                            boolean canDestroy = true;
                            if (explosion.exploder != null) {
                                // Если есть exploder, используем его для проверки
                                canDestroy = explosion.exploder.shouldBlockExplode(explosion.compat, world, pos, state, explosion.size);
                            }

                            if (canDestroy) {
                                affectedBlocks.add(pos);
                            }

                            currentX += d0 * (double) stepSize;
                            currentY += d1 * (double) stepSize;
                            currentZ += d2 * (double) stepSize;
                        }
                    }
                }
            }
        }

        return affectedBlocks;
    }
}