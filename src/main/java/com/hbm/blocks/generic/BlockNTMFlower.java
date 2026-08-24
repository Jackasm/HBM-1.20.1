package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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

public class BlockNTMFlower extends Block implements IPlantable, BonemealableBlock {

    private static final VoxelShape SHAPE = Shapes.box(0.3D, 0.0D, 0.3D, 0.7D, 0.6D, 0.7D);

    private final FlowerVariant variant;

    public enum FlowerVariant {
        FOXGLOVE(false),
        TOBACCO(false),
        NIGHTSHADE(false),
        WEED(false),
        CD0(true),
        CD1(true);

        public final boolean needsOil;

        FlowerVariant(boolean needsOil) {
            this.needsOil = needsOil;
        }
    }

    public BlockNTMFlower(Properties properties, FlowerVariant variant) {
        super(properties);
        this.variant = variant;
        this.registerDefaultState(this.stateDefinition.any());
    }

    public FlowerVariant getVariant() {
        return variant;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        // Нет свойств состояния
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.PLAINS;
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
        if (!this.canBlockStay(level, pos)) {
            dropResources(state, level, pos);
            level.removeBlock(pos, false);
        }
    }

    public boolean canBlockStay(Level level, BlockPos pos) {
        return canPlaceBlockOn(level.getBlockState(pos.below()).getBlock());
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (level.isClientSide) return;

        if (!(variant == FlowerVariant.WEED || variant == FlowerVariant.CD0 || variant == FlowerVariant.CD1)) return;

        if (this.isValidBonemealTarget(level, pos, state, false) &&
                this.isBonemealSuccess(level, random, pos, state) &&
                random.nextInt(3) == 0) {
            this.performBonemeal(level, random, pos, state);
        }
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClient) {
        if (variant != FlowerVariant.WEED && variant != FlowerVariant.CD0 && variant != FlowerVariant.CD1) {
            return false;
        }

        if (variant == FlowerVariant.CD0 || variant == FlowerVariant.CD1) {
            if (level.getFluidState(pos.east().below()).isEmpty() &&
                    level.getFluidState(pos.west().below()).isEmpty() &&
                    level.getFluidState(pos.south().below()).isEmpty() &&
                    level.getFluidState(pos.north().below()).isEmpty()) {
                return false;
            }
        }

        if (variant == FlowerVariant.WEED || variant == FlowerVariant.CD1) {
            return level.isEmptyBlock(pos.above());
        }
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return random.nextFloat() < 0.33F;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull RandomSource random, BlockPos pos, @NotNull BlockState state) {
        Block onTop = level.getBlockState(pos.below()).getBlock();

        if (variant == FlowerVariant.WEED) {
            if (onTop == ModBlocks.DIRT_DEAD.get() || onTop == ModBlocks.DIRT_OILY.get()) {
                level.setBlock(pos, ModBlocks.PLANT_DEAD_GENERIC.get().defaultBlockState(), 3);
                return;
            }
            // WEED → высокий сорняк
            level.setBlock(pos, ModBlocks.PLANT_TALL_WEED.get().defaultBlockState()
                    .setValue(BlockTallPlant.HALF, DoubleBlockHalf.LOWER), 3);
            level.setBlock(pos.above(), ModBlocks.PLANT_TALL_WEED.get().defaultBlockState()
                    .setValue(BlockTallPlant.HALF, DoubleBlockHalf.UPPER), 3);
            return;
        }

        if (variant == FlowerVariant.CD0) {
            level.setBlock(pos, ModBlocks.PLANT_FLOWER_CD1.get().defaultBlockState(), 3);
            return;
        }

        if (variant == FlowerVariant.CD1) {
            level.setBlock(pos, ModBlocks.PLANT_TALL_CD2.get().defaultBlockState()
                    .setValue(BlockTallPlant.HALF, DoubleBlockHalf.LOWER), 3);
            level.setBlock(pos.above(), ModBlocks.PLANT_TALL_CD2.get().defaultBlockState()
                    .setValue(BlockTallPlant.HALF, DoubleBlockHalf.UPPER), 3);
        }
    }

    @Override
    public void playerDestroy(Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, BlockEntity blockEntity, @NotNull ItemStack tool) {
        if (!level.isClientSide) {
            List<ItemStack> drops = getCustomDrops();
            for (ItemStack drop : drops) {
                if (!drop.isEmpty()) {
                    Block.popResource(level, pos, drop);
                }
            }
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    public List<ItemStack> getCustomDrops() {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this));

        // Для CD1 дропаем CD0 (как в оригинале)
        if (variant == FlowerVariant.CD1) {
            drops.add(new ItemStack(ModBlocks.PLANT_FLOWER_CD0.get().asItem()));
        }

        return drops;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(this);
    }

    // ========== IPlantable ==========
    @Override
    public BlockState getPlant(BlockGetter level, BlockPos pos) {
        return defaultBlockState();
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.0F)
                .sound(SoundType.GRASS)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY)
                .randomTicks();
    }
}