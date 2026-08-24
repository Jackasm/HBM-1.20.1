package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockTallPlant extends Block implements IPlantable, BonemealableBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private static final VoxelShape SHAPE = Shapes.box(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    private final PlantVariant variant;

    public enum PlantVariant {
        WEED,
        CD2,
        CD3,
        CD4
    }

    public BlockTallPlant(Properties properties, PlantVariant variant) {
        super(properties);
        this.variant = variant;
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    public PlantVariant getVariant() {
        return variant;
    }

    // ========== ЛОГИКА БЛОКА ==========
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    protected boolean canPlaceBlockOn(Block block) {
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.FARMLAND ||
                block == ModBlocks.DIRT_DEAD.get() || block == ModBlocks.DIRT_OILY.get();
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        this.checkAndDropBlock(level, pos, state);
    }

    protected void checkAndDropBlock(Level level, BlockPos pos, BlockState state) {
        if (!this.canBlockStay(level, pos, state)) {
            if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                dropResources(state, level, pos);
            }
            level.removeBlock(pos, false);
        }
    }

    public boolean canBlockStay(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        return canPlaceBlockOn(level.getBlockState(pos.below()).getBlock());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos facingPos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            if (!below.is(this) || below.getValue(HALF) != DoubleBlockHalf.LOWER) {
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            if (facing == Direction.UP && !facingState.is(this)) {
                return Blocks.AIR.defaultBlockState();
            }
            BlockPos soilPos = pos.below();
            BlockState soil = level.getBlockState(soilPos);
            if (!soil.canSustainPlant(level, soilPos, Direction.UP, this)) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return state;
    }

    @Override
    public void playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide && player.isCreative()) {
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockPos lowerPos = pos.below();
                BlockState lowerState = level.getBlockState(lowerPos);
                if (lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlock(lowerPos, Blocks.AIR.defaultBlockState(), 35);
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, BlockEntity blockEntity, @NotNull ItemStack tool) {
        if (!level.isClientSide) {
            BlockPos dropPos = pos;
            BlockState dropState = state;

            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockPos lowerPos = pos.below();
                BlockState lowerState = level.getBlockState(lowerPos);
                if (lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    dropPos = lowerPos;
                    dropState = lowerState;
                    level.destroyBlock(lowerPos, false, player);
                } else {
                    super.playerDestroy(level, player, pos, state, blockEntity, tool);
                    return;
                }
            }

            List<ItemStack> drops = getCustomDrops(dropState, (ServerLevel) level);
            for (ItemStack drop : drops) {
                if (!drop.isEmpty()) {
                    Block.popResource(level, dropPos, drop);
                }
            }

            if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.destroyBlock(pos, false, player);
            }
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (level.isClientSide) return;
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) return;

        if (variant == PlantVariant.WEED) {
            Block onTop = level.getBlockState(pos.below()).getBlock();
            if (onTop == ModBlocks.DIRT_DEAD.get() || onTop == ModBlocks.DIRT_OILY.get()) {
                level.setBlock(pos, ModBlocks.PLANT_DEAD_GENERIC.get().defaultBlockState(), 3);
                return;
            }
        }

        if (this.isValidBonemealTarget(level, pos, state, false) &&
                this.isBonemealSuccess(level, random, pos, state) &&
                random.nextInt(3) == 0) {
            this.performBonemeal(level, random, pos, state);
        }
    }

    // ========== BONEMEAL ==========
    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, BlockState state, boolean isClient) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) return false;

        // CD2 → CD3, CD3 → CD4
        return variant == PlantVariant.CD2 || variant == PlantVariant.CD3;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return random.nextFloat() < 0.33F;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, BlockState state) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) return;
        BlockPos upperPos = pos.above();

        if (variant == PlantVariant.CD2) {
            level.setBlock(pos, ModBlocks.PLANT_TALL_CD3.get().defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER), 3);
            level.setBlock(upperPos, ModBlocks.PLANT_TALL_CD3.get().defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 3);
        } else if (variant == PlantVariant.CD3) {
            level.setBlock(pos, ModBlocks.PLANT_TALL_CD4.get().defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER), 3);
            level.setBlock(upperPos, ModBlocks.PLANT_TALL_CD4.get().defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 3);
            level.setBlock(pos.below(), Blocks.DIRT.defaultBlockState(), 3);
        }
    }

    // ========== ДРОП ==========
    public List<ItemStack> getCustomDrops(BlockState state, ServerLevel level) {
        List<ItemStack> drops = new ArrayList<>();
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) return drops;

        drops.add(new ItemStack(this));

        if (variant == PlantVariant.CD4) {
            drops.add(new ItemStack(ModItems.PLANT_ITEM_MUSTARDWILLOW.get(), 3 + level.random.nextInt(4)));
        }

        return drops;
    }

    // ========== IPlantable ==========
    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.PLAINS;
    }

    @Override
    public BlockState getPlant(BlockGetter level, BlockPos pos) {
        return defaultBlockState();
    }

    // ========== СОЗДАНИЕ СВОЙСТВ ==========
    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.0F)
                .sound(SoundType.GRASS)
                .noCollission()
                .randomTicks()
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion();
    }
}