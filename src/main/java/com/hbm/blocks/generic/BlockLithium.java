package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class BlockLithium extends Block {

    public BlockLithium() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops()
        );
    }

    private boolean touchesWater(Level world, BlockPos pos) {
        if (world.isClientSide) return false;

        // Проверяем соседние блоки на наличие воды
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos neighborPos = pos.offset(dx, dy, dz);
                    FluidState fluidState = world.getFluidState(neighborPos);
                    if (fluidState.getType() == Fluids.WATER || fluidState.getType() == Fluids.FLOWING_WATER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        if (touchesWater(world, pos)) {
            world.destroyBlock(pos, false);
            world.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15.0F, Level.ExplosionInteraction.TNT);
        }
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);

        if (touchesWater(world, pos)) {
            world.destroyBlock(pos, false);
            world.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15.0F, Level.ExplosionInteraction.TNT);
        }
    }
}