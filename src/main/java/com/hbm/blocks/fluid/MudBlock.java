package com.hbm.blocks.fluid;

import com.hbm.util.ArmorUtil;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;

public class MudBlock extends LiquidBlock {

    public MudBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {

        if (entity instanceof LivingEntity living) {
            living.setIsInPowderSnow(true);
        }

        if (entity instanceof Player player) {
            if (!ArmorUtil.checkForHazmat(player)) {
                player.hurt(ModDamageSource.mudPoisoning(player), 8.0F);
            }
        } else {
            entity.hurt(ModDamageSource.mudPoisoning(entity.level()), 8.0F);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        reactToBlocks(level, pos.east());
        reactToBlocks(level, pos.west());
        reactToBlocks(level, pos.above());
        reactToBlocks(level, pos.below());
        reactToBlocks(level, pos.north());
        reactToBlocks(level, pos.south());
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);

        level.scheduleTick(pos, this, 15);
        reactToBlocks2(level, pos.east(), random);
        reactToBlocks2(level, pos.west(), random);
        reactToBlocks2(level, pos.above(), random);
        reactToBlocks2(level, pos.below(), random);
        reactToBlocks2(level, pos.north(), random);
        reactToBlocks2(level, pos.south(), random);
    }

    public void reactToBlocks(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getFluidState().getType() != ModFluids.MUD_FLUID.get()) {
            if (state.getFluidState().is(FluidTags.WATER) || state.getFluidState().is(FluidTags.LAVA)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    public void reactToBlocks2(Level level, BlockPos pos, RandomSource rand) {
        BlockState state = level.getBlockState(pos);
        if (state.getFluidState().getType() == ModFluids.MUD_FLUID.get()) return;

        Block block = state.getBlock();

        if (block == Blocks.STONE || block == Blocks.STONE_BRICKS ||
                block == Blocks.STONE_STAIRS || block == Blocks.STONE_BRICK_STAIRS ||
                block == Blocks.STONE_SLAB) {
            if (rand.nextInt(20) == 0)
                level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
        } else if (block == Blocks.COBBLESTONE) {
            if (rand.nextInt(15) == 0)
                level.setBlock(pos, Blocks.GRAVEL.defaultBlockState(), 3);
        } else if (block == Blocks.SANDSTONE) {
            if (rand.nextInt(5) == 0)
                level.setBlock(pos, Blocks.SAND.defaultBlockState(), 3);
        } else if (block == Blocks.TERRACOTTA) {
            if (rand.nextInt(10) == 0)
                level.setBlock(pos, Blocks.CLAY.defaultBlockState(), 3);
        } else if (state.is(BlockTags.LEAVES) ||
                state.is(BlockTags.WOOL) ||
                state.is(BlockTags.ICE) ||
                block == Blocks.CACTUS ||
                block == Blocks.CAKE ||
                block == Blocks.COBWEB ||
                block == Blocks.SPONGE ||
                block == Blocks.WET_SPONGE) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else if (state.getDestroySpeed(level, pos) < 1.2F && !state.isAir()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
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