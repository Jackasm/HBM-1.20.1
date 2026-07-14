package com.hbm.blocks.fluid;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


public class VolcanicBlock extends LiquidBlock {

    public VolcanicBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
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
            return ModBlocks.ORE_BASALT.get();
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

    public Block getBasaltForCheck() {
        return ModBlocks.BASALT.get();
    }

    public void onSolidify(Level level, BlockPos pos, int lavaCount, int basaltCount, RandomSource random) {
        int r = random.nextInt(200);

        BlockPos abovePos = pos.above(10);
        Block above = level.getBlockState(abovePos).getBlock();
        boolean canMakeGem = lavaCount + basaltCount == 6 && lavaCount < 3 &&
                (above == ModBlocks.BASALT.get() || above == ModBlocks.VOLCANIC_LAVA_BLOCK.get());

        if (r < 2) {
            level.setBlock(pos, ModBlocks.ORE_BASALT.get().defaultBlockState(), 3);
        } else if (r == 2) {
            level.setBlock(pos, ModBlocks.ORE_BASALT.get().defaultBlockState(), 3); // meta 1
        } else if (r == 3) {
            level.setBlock(pos, ModBlocks.ORE_BASALT.get().defaultBlockState(), 3); // meta 2
        } else if (r == 4) {
            level.setBlock(pos, ModBlocks.ORE_BASALT.get().defaultBlockState(), 3); // meta 4
        } else if (r < 15 && canMakeGem) {
            level.setBlock(pos, ModBlocks.ORE_BASALT.get().defaultBlockState(), 3); // meta 3
        } else {
            level.setBlock(pos, ModBlocks.BASALT.get().defaultBlockState(), 3);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        double d0;
        double d1;
        double d2;

        if (aboveState.isAir() && !aboveState.isSolid()) {
            if (random.nextInt(100) == 0) {
                d0 = (double) pos.getX() + random.nextDouble();
                d1 = (double) pos.getY() + 1.0D;
                d2 = (double) pos.getZ() + random.nextDouble();
                level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                level.playLocalSound(d0, d1, d2, SoundEvents.LAVA_POP, SoundSource.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }

        if (random.nextInt(10) == 0 && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP) &&
                !level.getBlockState(pos.below(2)).isSolid()) {
            d0 = (double) pos.getX() + random.nextDouble();
            d1 = (double) pos.getY() - 1.05D;
            d2 = (double) pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.DRIPPING_LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .noCollission()
                .strength(500.0F)
                .replaceable()
                .noLootTable()
                .liquid();
    }
}