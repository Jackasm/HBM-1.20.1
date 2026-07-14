package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCoalBurning extends BlockOutgas {

    public BlockCoalBurning(Properties properties) {
        super(properties.randomTicks(), 1, false);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        super.animateTick(state, level, pos, rand);

        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN) continue;

            BlockPos neighborPos = pos.relative(dir);
            if (level.getBlockState(neighborPos).isAir()) {

                double ix = pos.getX() + 0.5F + dir.getStepX() + rand.nextDouble() - 0.5D;
                double iy = pos.getY() + 0.5F + dir.getStepY() + rand.nextDouble() - 0.5D;
                double iz = pos.getZ() + 0.5F + dir.getStepZ() + rand.nextDouble() - 0.5D;

                if (dir.getStepX() != 0)
                    ix = pos.getX() + 0.5F + dir.getStepX() * 0.5 + rand.nextDouble() * 0.125 * dir.getStepX();
                if (dir.getStepY() != 0)
                    iy = pos.getY() + 0.5F + dir.getStepY() * 0.5 + rand.nextDouble() * 0.125 * dir.getStepY();
                if (dir.getStepZ() != 0)
                    iz = pos.getZ() + 0.5F + dir.getStepZ() * 0.5 + rand.nextDouble() * 0.125 * dir.getStepZ();

                level.addParticle(ParticleTypes.FLAME, ix, iy, iz, 0.0, 0.0, 0.0);
                level.addParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.0, 0.0);
                level.addParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.1, 0.0);
            }
        }
    }

    @Override
    public void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ItemStack stack, boolean dropXp) {
        // Не дропаем предмет
        super.spawnAfterBreak(state, level, pos, stack, dropXp);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            // Заменяем на огонь
            level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);

            // Выпускаем газ
            for (int ix = -2; ix <= 2; ix++) {
                for (int iy = -2; iy <= 2; iy++) {
                    for (int iz = -2; iz <= 2; iz++) {
                        if (Math.abs(ix + iy + iz) < 5) {
                            BlockPos targetPos = pos.offset(ix, iy, iz);
                            if (level.getBlockState(targetPos).isAir()) {
                                level.setBlock(targetPos, getGas().defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        entity.setRemainingFireTicks(60); // 3 секунды огня
    }
}