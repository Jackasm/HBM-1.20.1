package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class BlockOutgas extends BlockOre {

    private int rate;
    private boolean onBreak;
    private boolean onNeighbour;

    public BlockOutgas(Properties properties, int rate, boolean onBreak) {
        super(properties);
        this.rate = rate;
        this.onBreak = onBreak;
        this.onNeighbour = false;
    }

    public BlockOutgas(Properties properties, int rate, boolean onBreak, boolean onNeighbour) {
        this(properties, rate, onBreak);
        this.onNeighbour = onNeighbour;
    }

    @Override
    public int getTickRate(@NotNull LevelReader level) {
        return rate;
    }

    protected Block getGas() {
        if (this == ModBlocks.ORE_URANIUM.get() || this == ModBlocks.ORE_URANIUM_SCORCHED.get() ||
                this == ModBlocks.ORE_GNEISS_URANIUM.get() || this == ModBlocks.ORE_GNEISS_URANIUM_SCORCHED.get() ||
                this == ModBlocks.ORE_NETHER_URANIUM.get() || this == ModBlocks.ORE_NETHER_URANIUM_SCORCHED.get()) {
            return ModBlocks.GAS_RADON.get();
        }

        if (this == ModBlocks.ANCIENT_SCRAP.get())
            return ModBlocks.GAS_RADON_TOMB.get();


        if (this == ModBlocks.BLOCK_CORIUM_COBBLE.get())
            return ModBlocks.GAS_RADON.get();

        if (this == ModBlocks.ORE_COAL_OIL_BURNING.get() || this == ModBlocks.ORE_NETHER_COAL.get()) {
            return ModBlocks.GAS_MONOXIDE.get();
        }

        if (this == ModBlocks.ORE_ASBESTOS.get() || this == ModBlocks.ORE_GNEISS_ASBESTOS.get() ||
                this == ModBlocks.BLOCK_ASBESTOS.get() || this == ModBlocks.DECO_ASBESTOS.get() ||
                this == ModBlocks.BRICK_ASBESTOS.get() || this == ModBlocks.TILE_LAB.get() ||
                this == ModBlocks.TILE_LAB_CRACKED.get() || this == ModBlocks.TILE_LAB_BROKEN.get()) {
            return ModBlocks.GAS_ASBESTOS.get();
        }

        return Blocks.AIR;
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (this.isRandomlyTicking(state) && getGas() == ModBlocks.GAS_ASBESTOS.get()) {
            BlockPos above = pos.above();
            if (level.getBlockState(above).isAir()) {
                if (level.random.nextInt(10) == 0) {
                    level.setBlock(above, ModBlocks.GAS_ASBESTOS.get().defaultBlockState(), 3);
                }
                for (int i = 0; i < 5; i++) {
                    level.addParticle(ParticleTypes.MYCELIUM,
                            pos.getX() + level.random.nextFloat(),
                            pos.getY() + 1.1,
                            pos.getZ() + level.random.nextFloat(),
                            0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
                           @NotNull BlockPos pos, @NotNull RandomSource random) {
        Direction dir = Direction.values()[random.nextInt(6)];

        BlockPos targetPos = pos.relative(dir);
        if (level.getBlockState(targetPos).isAir()) {
            level.setBlock(targetPos, getGas().defaultBlockState(), 3);
        }
    }

    @Override
    public void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ItemStack stack, boolean dropXp) {
        if (onBreak) {
            level.setBlock(pos, getGas().defaultBlockState(), 3);
        }
        super.spawnAfterBreak(state, level, pos, stack, dropXp);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (onNeighbour && level.random.nextInt(3) == 0) {
            for (Direction dir : Direction.values()) {
                BlockPos targetPos = pos.relative(dir);
                if (level.getBlockState(targetPos).isAir()) {
                    level.setBlock(targetPos, getGas().defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {

        if (this == ModBlocks.ANCIENT_SCRAP.get() && state.getBlock() != newState.getBlock()) {
            for (int ix = -2; ix <= 2; ix++) {
                for (int iy = -2; iy <= 2; iy++) {
                    for (int iz = -2; iz <= 2; iz++) {
                        if (Math.abs(ix + iy + iz) < 5 && Math.abs(ix + iy + iz) > 0) {
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

    // Фабричный метод для создания свойств
    public static BlockBehaviour.Properties createProperties(MapColor color, float hardness, float resistance, boolean randomTick) {
        Properties props = BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .requiresCorrectToolForDrops();

        if (randomTick) {
            props.randomTicks();
        }

        return props;
    }
}