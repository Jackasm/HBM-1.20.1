package com.hbm.blocks.generic;

import com.hbm.blocks.BlockEnumMulti;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockDeadPlant.EnumDeadPlantType;
import com.hbm.items.block.ItemBlockResourceStone;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.FoliageColor;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockNTMFlower extends Block implements IPlantable, BonemealableBlock, ITooltipProvider {

    public static final IntegerProperty META = IntegerProperty.create("meta", 0, EnumFlowerType.values().length - 1);

    private static final VoxelShape SHAPE = Shapes.box(0.3D, 0.0D, 0.3D, 0.7D, 0.6D, 0.7D);

    public BlockNTMFlower(Properties properties) {
        super(properties);
    }

    public enum EnumFlowerType {
        FOXGLOVE(false),
        TOBACCO(false),
        NIGHTSHADE(false),
        WEED(false),
        CD0(true),
        CD1(true);

        public final boolean needsOil;

        EnumFlowerType(boolean needsOil) {
            this.needsOil = needsOil;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(META);
    }

    public int rectify(int meta) {
        if (meta < 0) return 0;
        if (meta >= EnumFlowerType.values().length) return EnumFlowerType.values().length - 1;
        return meta;
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.PLAINS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (!canPlaceBlockOn(level.getBlockState(pos.below()).getBlock())) {
            return null; // нельзя поставить
        }
        ItemStack stack = context.getItemInHand();
        int meta = getCustomModelData(stack);
        return this.defaultBlockState().setValue(META, meta);
    }

    private int getCustomModelData(ItemStack stack) {
        return ItemBlockResourceStone.getType(stack);
    }

    public int validateMeta(int meta) {
        if (meta < 0) return 0;
        if (meta >= EnumFlowerType.values().length) return EnumFlowerType.values().length - 1;
        return meta;
    }

    @Override
    public BlockState getPlant(BlockGetter level, BlockPos pos) {
        return this.defaultBlockState();
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
            dropResources(state, level, pos);
            level.removeBlock(pos, false);
        }
    }

    public boolean canBlockStay(Level level, BlockPos pos, BlockState state) {
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

        int meta = state.getValue(META);
        EnumFlowerType type = EnumFlowerType.values()[rectify(meta)];

        if (!(type == EnumFlowerType.WEED || type == EnumFlowerType.CD0 || type == EnumFlowerType.CD1)) return;

        if (this.isValidBonemealTarget(level, pos, state, false) &&
                this.isBonemealSuccess(level, random, pos, state) &&
                random.nextInt(3) == 0) {
            this.performBonemeal(level, random, pos, state);
        }
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, BlockState state, boolean isClient) {
        int meta = state.getValue(META);
        EnumFlowerType type = EnumFlowerType.values()[rectify(meta)];

        if (type != EnumFlowerType.WEED && type != EnumFlowerType.CD0 && type != EnumFlowerType.CD1) {
            return false;
        }

        if (meta == EnumFlowerType.CD0.ordinal() || meta == EnumFlowerType.CD1.ordinal()) {
            if (level.getFluidState(pos.east().below()).isEmpty() &&
                    level.getFluidState(pos.west().below()).isEmpty() &&
                    level.getFluidState(pos.south().below()).isEmpty() &&
                    level.getFluidState(pos.north().below()).isEmpty()) {
                return false;
            }
        }

        if (meta == EnumFlowerType.WEED.ordinal() || meta == EnumFlowerType.CD1.ordinal()) {
            return level.isEmptyBlock(pos.above());
        }
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, BlockState state) {
        int meta = state.getValue(META);
        if (meta == EnumFlowerType.WEED.ordinal() || meta == EnumFlowerType.CD0.ordinal() || meta == EnumFlowerType.CD1.ordinal()) {
            return random.nextFloat() < 0.33F;
        }
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull RandomSource random, BlockPos pos, BlockState state) {
        int meta = state.getValue(META);
        Block onTop = level.getBlockState(pos.below()).getBlock();

        if (meta == EnumFlowerType.WEED.ordinal()) {
            if (onTop == ModBlocks.DIRT_DEAD.get() || onTop == ModBlocks.DIRT_OILY.get()) {
                BlockDeadPlant deadPlant = (BlockDeadPlant) ModBlocks.PLANT_DEAD.get();
                level.setBlock(pos, ModBlocks.PLANT_DEAD.get().defaultBlockState()
                        .setValue(deadPlant.META, EnumDeadPlantType.GENERIC.ordinal()), 3);
                return;
            }
        }

        if (meta == EnumFlowerType.WEED.ordinal()) {
            level.setBlock(pos, ModBlocks.PLANT_TALL.get().defaultBlockState()
                    .setValue(BlockTallPlant.HALF, DoubleBlockHalf.LOWER)
                    .setValue(BlockTallPlant.TYPE, BlockTallPlant.EnumTallFlower.WEED), 3);
            level.setBlock(pos.above(), ModBlocks.PLANT_TALL.get().defaultBlockState()
                    .setValue(BlockTallPlant.HALF, DoubleBlockHalf.UPPER)
                    .setValue(BlockTallPlant.TYPE, BlockTallPlant.EnumTallFlower.WEED), 3);
            return;
        }

        if (meta == EnumFlowerType.CD0.ordinal()) {
            level.setBlock(pos, ModBlocks.PLANT_FLOWER.get().defaultBlockState()
                    .setValue(META, EnumFlowerType.CD1.ordinal()), 3);
            return;
        }

        if (meta == EnumFlowerType.CD1.ordinal()) {
            level.setBlock(pos, ModBlocks.PLANT_TALL.get().defaultBlockState()
                    .setValue(BlockTallPlant.HALF, DoubleBlockHalf.LOWER)
                    .setValue(BlockTallPlant.TYPE, BlockTallPlant.EnumTallFlower.CD2), 3);
            level.setBlock(pos.above(), ModBlocks.PLANT_TALL.get().defaultBlockState()
                    .setValue(BlockTallPlant.HALF, DoubleBlockHalf.UPPER)
                    .setValue(BlockTallPlant.TYPE, BlockTallPlant.EnumTallFlower.CD2), 3);
        }
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, BlockState state) {
        int meta = state.getValue(META);
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("CustomModelData", meta);
        return stack;
    }


    @Override
    public void playerDestroy(Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, BlockEntity blockEntity, @NotNull ItemStack tool) {
        if (!level.isClientSide) {
            BlockPos dropPos = pos;
            BlockState dropState = state;

            List<ItemStack> drops = getCustomDrops(dropState, (ServerLevel) level, dropPos, blockEntity, player, tool);
            for (ItemStack drop : drops) {
                if (!drop.isEmpty()) {
                    Block.popResource(level, dropPos, drop);
                }
            }
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    public List<ItemStack> getCustomDrops(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool) {
        List<ItemStack> drops = new ArrayList<>();
        int meta = state.getValue(META);
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("CustomModelData", meta);
        drops.add(stack);

        // Для CD1 дропаем CD0 (как в оригинале)
        if (meta == EnumFlowerType.CD1.ordinal()) {
            ItemStack cd0 = new ItemStack(this);
            cd0.getOrCreateTag().putInt("CustomModelData", EnumFlowerType.CD0.ordinal());
            drops.add(cd0);
        }

        return drops;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        // можно добавить информацию
    }

    @Override
    public void addInformation(ItemStack stack, Player player, List list, boolean ext) {
        // ничего
    }

    @OnlyIn(Dist.CLIENT)
    public int getRenderColor(BlockState state) {
        int meta = state.getValue(META);
        if (meta == 1 || meta == 3) {
            return FoliageColor.getDefaultColor();
        }
        return 0xFFFFFF;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBlockColor(BlockState state, BlockGetter level, BlockPos pos, int tintIndex) {
        int meta = state.getValue(META);
        if (meta == 1 || meta == 3) {
            return FoliageColor.getDefaultColor();
        }
        return 0xFFFFFF;
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