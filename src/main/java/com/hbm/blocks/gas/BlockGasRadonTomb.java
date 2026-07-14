package com.hbm.blocks.gas;

import com.hbm.blocks.ModBlocks;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public class BlockGasRadonTomb extends BlockGasBase {

    public BlockGasRadonTomb(Properties properties) {
        super(properties, 0.1F, 0.3F, 0.1F);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living) {
            // Удаляем защитные эффекты
            living.removeEffect(HbmPotion.RADAWAY.get());
            living.removeEffect(HbmPotion.RADX.get());

            // Радиация и асбест
            ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 0.5F);
            HbmLivingProps.incrementAsbestos(living, 10);
        }
    }

    @Override
    public Direction getFirstDirection(Level level, BlockPos pos) {
        if (level.random.nextInt(3) == 0) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    @Override
    public Direction getSecondDirection(Level level, BlockPos pos) {
        return randomHorizontal(level.random);
    }

    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.isClientSide) {
            if (random.nextInt(10) == 0) {
                BlockPos belowPos = pos.below();
                BlockState belowState = level.getBlockState(belowPos);
                Block below = belowState.getBlock();

                if (below == Blocks.GRASS_BLOCK) {
                    if (random.nextInt(5) == 0) {
                        level.setBlock(belowPos, Blocks.DIRT.defaultBlockState(), 3);
                    } else {
                        level.setBlock(belowPos, ModBlocks.WASTE_EARTH.get().defaultBlockState(), 3);
                    }
                }

                if (belowState.is(BlockTags.DIRT) ||
                        belowState.is(BlockTags.LEAVES) ||
                        belowState.is(BlockTags.FLOWERS) ||
                        belowState.is(BlockTags.CROPS) ||
                        belowState.is(BlockTags.SAPLINGS)) {

                    if (belowState.is(BlockTags.DIRT) && random.nextInt(5) == 0) {
                        level.setBlock(belowPos, Blocks.DIRT.defaultBlockState(), 3);
                    } else if (belowState.is(BlockTags.DIRT)) {
                        level.setBlock(belowPos, ModBlocks.WASTE_EARTH.get().defaultBlockState(), 3);
                    } else {
                        level.setBlock(belowPos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }

            if (random.nextInt(600) == 0) {
                level.removeBlock(pos, false);
                return;
            }
        }

        super.randomTick(state, level, pos, random);
    }
}