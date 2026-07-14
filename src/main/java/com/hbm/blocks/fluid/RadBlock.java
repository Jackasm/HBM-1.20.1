package com.hbm.blocks.fluid;

import com.hbm.blocks.ModBlocks;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;

public class RadBlock extends LiquidBlock {

    public RadBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living) {
            ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.CREATIVE, 5.0F);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.relative(dir);
            Block replacement = getReaction(level, targetPos);

            if (replacement != null) {
                level.setBlock(targetPos, replacement.defaultBlockState(), 3);
            }
        }
    }

    public Block getReaction(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (state.getFluidState().is(net.minecraft.tags.FluidTags.WATER)) return Blocks.STONE;
        if (block == Blocks.OAK_LOG || block == Blocks.SPRUCE_LOG || block == Blocks.BIRCH_LOG ||
                block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG ||
                block == Blocks.MANGROVE_LOG) {
            return ModBlocks.WASTE_LOG.get();
        }
        if (block == Blocks.OAK_PLANKS || block == Blocks.SPRUCE_PLANKS || block == Blocks.BIRCH_PLANKS ||
                block == Blocks.JUNGLE_PLANKS || block == Blocks.ACACIA_PLANKS || block == Blocks.DARK_OAK_PLANKS ||
                block == Blocks.MANGROVE_PLANKS) {
            return ModBlocks.WASTE_PLANKS.get();
        }
        if (block == Blocks.OAK_LEAVES || block == Blocks.SPRUCE_LEAVES || block == Blocks.BIRCH_LEAVES ||
                block == Blocks.JUNGLE_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.DARK_OAK_LEAVES ||
                block == Blocks.MANGROVE_LEAVES) {
            return Blocks.FIRE;
        }
        if (block == Blocks.DIAMOND_ORE) {
            return ModBlocks.ORE_SELLAFIELD_RADGEM.get();
        }
        if (block == ModBlocks.ORE_URANIUM.get() || block == ModBlocks.ORE_GNEISS_URANIUM.get()) {
            return level.random.nextInt(5) == 0 ? ModBlocks.ORE_SELLAFIELD_SCHRABIDIUM.get() : ModBlocks.ORE_SELLAFIELD_URANIUM_SCORCHED.get();
        }
        return null;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.randomTick(state, level, pos, random);

        int lavaCount = 0;
        int basaltCount = 0;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            Block neighborBlock = level.getBlockState(neighborPos).getBlock();

            if (neighborBlock == this) {
                lavaCount++;
            }
            if (neighborBlock == getBasaltForCheck()) {
                basaltCount++;
            }
        }

        if (!level.isClientSide) {
            boolean isSource = level.getFluidState(pos).isSource();
            boolean condition = (!isSource && lavaCount < 2) || (random.nextInt(5) == 0 && lavaCount < 5);
            if (condition && level.getBlockState(pos.below()).getBlock() != this) {
                this.onSolidify(level, pos, lavaCount, basaltCount, random);
            }
        }
    }

    public void onSolidify(Level level, BlockPos pos, int lavaCount, int basaltCount, RandomSource random) {
        int r = random.nextInt(400);

        BlockPos abovePos = pos.above(10);
        Block above = level.getBlockState(abovePos).getBlock();
        boolean canMakeGem = lavaCount + basaltCount == 6 && lavaCount < 3 &&
                (above == ModBlocks.SELLAFIELD_SLAKED.get() || above == ModBlocks.RAD_LAVA_BLOCK.get());
        int meta = 5 + random.nextInt(3);

        if (r < 2) {
            level.setBlock(pos, ModBlocks.ORE_SELLAFIELD_DIAMOND.get().defaultBlockState(), 3);
        } else if (r == 2) {
            level.setBlock(pos, ModBlocks.ORE_SELLAFIELD_EMERALD.get().defaultBlockState(), 3);
        } else if (r < 20 && canMakeGem) {
            level.setBlock(pos, ModBlocks.ORE_SELLAFIELD_RADGEM.get().defaultBlockState(), 3);
        } else {
            level.setBlock(pos, ModBlocks.SELLAFIELD_SLAKED.get().defaultBlockState(), 3);
        }
    }

    public Block getBasaltForCheck() {
        return ModBlocks.SELLAFIELD_SLAKED.get();
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .noCollission()
                .strength(500.0F)
                .noLootTable()
                .replaceable()
                .liquid()
                .randomTicks();
    }
}