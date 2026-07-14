package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;

import com.hbm.datagen.worldgen.feature.HugeMush;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BlockMush extends BushBlock implements BonemealableBlock, IPlantable {

    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 6.0D, 12.0D);

    private static final Set<Block> canGrowOn = new HashSet<>();

    public BlockMush(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.isSolid();
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (level.getRawBrightness(pos, 0) < 13) {
            return belowState.canSustainPlant(level, belowPos, Direction.UP, this) || canMushGrowHere(level, pos);
        }
        return false;
    }

    private boolean canMushGrowHere(LevelReader level, BlockPos pos) {
        if (canGrowOn.isEmpty()) {
            initGrowSet();
        }

        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return canGrowOn.contains(belowState.getBlock());
    }

    private static void initGrowSet() {
        canGrowOn.add(ModBlocks.WASTE_EARTH.get());
        canGrowOn.add(ModBlocks.WASTE_MYCELIUM.get());
        canGrowOn.add(ModBlocks.WASTE_TRINITITE.get());
        canGrowOn.add(ModBlocks.WASTE_TRINITITE_RED.get());
        canGrowOn.add(ModBlocks.BLOCK_WASTE.get());
        canGrowOn.add(ModBlocks.BLOCK_WASTE_PAINTED.get());
        canGrowOn.add(ModBlocks.BLOCK_WASTE_VITRIFIED.get());
    }

    public boolean growHuge(ServerLevel level, BlockPos pos, RandomSource rand) {
        level.removeBlock(pos, false);
        HugeMush feature = new HugeMush();
        return feature.place(new FeaturePlaceContext<>(
                Optional.empty(),
                level,
                level.getChunkSource().getGenerator(),
                rand,
                pos,
                NoneFeatureConfiguration.INSTANCE
        ));
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClient) {
        return canSurvive(state, level, pos);
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource rand, @NotNull BlockPos pos, @NotNull BlockState state) {
        return rand.nextFloat() < 0.4D;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource rand, @NotNull BlockPos pos, @NotNull BlockState state) {
        this.growHuge(level, pos, rand);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }

        if (GeneralConfig.enableMycelium.get() && level.getBlockState(pos.below()).getBlock() == ModBlocks.WASTE_EARTH.get() && rand.nextInt(5) == 0) {
            level.setBlock(pos.below(), ModBlocks.WASTE_MYCELIUM.get().defaultBlockState(), 3);
        }

        if (rand.nextInt(25) == 0) {
            int range = 4;
            int maxShroom = 3;

            // Проверка плотности грибов
            for (int ix = -range; ix <= range; ++ix) {
                for (int iz = -range; iz <= range; ++iz) {
                    for (int iy = -1; iy <= 1; ++iy) {
                        BlockPos checkPos = pos.offset(ix, iy, iz);
                        if (level.getBlockState(checkPos).getBlock() == this) {
                            --maxShroom;
                            if (maxShroom <= 0) {
                                return;
                            }
                        }
                    }
                }
            }

            // Попытка распространиться
            for (int attempt = 0; attempt < 4; ++attempt) {
                BlockPos newPos = pos.offset(
                        rand.nextInt(5) - 2,
                        rand.nextInt(2) - rand.nextInt(2),
                        rand.nextInt(5) - 2
                );

                if (level.isEmptyBlock(newPos) && canSurvive(state, level, newPos)) {
                    level.setBlock(newPos, this.defaultBlockState(), 2);
                    return;
                }
            }
        }
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.CAVE;
    }

    @Override
    public @NotNull BlockState getPlant(@NotNull BlockGetter level, @NotNull BlockPos pos) {
        return defaultBlockState();
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .strength(0.0F)
                .lightLevel(state -> 7) // 0.5F * 15 = 7.5 -> 7
                .sound(SoundType.GRASS)
                .noOcclusion()
                .noCollission()
                .pushReaction(PushReaction.DESTROY)
                .randomTicks();
    }
}