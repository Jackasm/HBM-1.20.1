package com.hbm.explosion.vanillant.standard;

import java.util.HashSet;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockAllocatorStandard implements IBlockAllocator {

    protected int resolution;

    public BlockAllocatorStandard() {
        this(16);
    }

    public BlockAllocatorStandard(int resolution) {
        this.resolution = resolution;
    }

    @Override
    public HashSet<BlockPos> allocate(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
        HashSet<BlockPos> affectedBlocks = new HashSet<>();
        RandomSource rand = world.random;

        for(int i = 0; i < this.resolution; ++i) {
            for(int j = 0; j < this.resolution; ++j) {
                for(int k = 0; k < this.resolution; ++k) {

                    if(i == 0 || i == this.resolution - 1 ||
                            j == 0 || j == this.resolution - 1 ||
                            k == 0 || k == this.resolution - 1) {

                        double d0 = (double) ((float) i / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
                        double d1 = (double) ((float) j / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
                        double d2 = (double) ((float) k / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;

                        float powerRemaining = size * (0.7F + rand.nextFloat() * 0.6F);
                        double currentX = x;
                        double currentY = y;
                        double currentZ = z;

                        for(float stepSize = 0.3F; powerRemaining > 0.0F; powerRemaining -= stepSize * 0.75F) {

                            int blockX = Mth.floor(currentX);
                            int blockY = Mth.floor(currentY);
                            int blockZ = Mth.floor(currentZ);

                            BlockPos pos = new BlockPos(blockX, blockY, blockZ);
                            BlockState state = world.getBlockState(pos);

                            if(!state.isAir()) {
                                float blockResistance = state.getBlock().getExplosionResistance(
                                        state, world, pos, explosion.compat
                                );
                                powerRemaining -= (blockResistance + 0.3F) * stepSize;
                            }

                            if(powerRemaining > 0.0F) {
                                // Проверяем, может ли блок быть разрушен взрывом
                                if(explosion.exploder == null ||
                                        !state.getBlock().canDropFromExplosion(state, world, pos, explosion.compat)) {
                                    affectedBlocks.add(pos);
                                }
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
}