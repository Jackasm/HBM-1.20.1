package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.block.ItemBlockResourceStone;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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

public class BlockDeadPlant extends Block implements IPlantable {

    private static final VoxelShape SHAPE = Shapes.box(0.3D, 0.0D, 0.3D, 0.7D, 0.6D, 0.7D);

    public static final IntegerProperty META = IntegerProperty.create("meta", 0, EnumDeadPlantType.values().length - 1);

    public BlockDeadPlant(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(META, 0));
    }

    public enum EnumDeadPlantType {
        GENERIC,
        GRASS,
        FLOWER,
        BIGFLOWER,
        FERN
    }

    // ========== РАЗМЕЩЕНИЕ ПО CustomModelData ==========
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

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, BlockState state) {
        int meta = state.getValue(META);
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("CustomModelData", meta);
        return stack;
    }

    // ========== ЛОГИКА БЛОКА ==========
    protected boolean canPlaceBlockOn(Block block) {
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT ||
                block == ModBlocks.WASTE_EARTH.get() || block == ModBlocks.DIRT_OILY.get() ||
                block == ModBlocks.DIRT_DEAD.get();
    }

    public int validateMeta(int meta) {
        if (meta < 0) return 0;
        if (meta >= EnumDeadPlantType.values().length) return EnumDeadPlantType.values().length - 1;
        return meta;
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

    public List<ItemStack> getCustomDrops(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool) {
        List<ItemStack> drops = new ArrayList<>();
        int meta = state.getValue(META);
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("CustomModelData", meta);
        drops.add(stack);
        return drops;
    }

    // ========== IPlantable ==========
    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.PLAINS;
    }

    @Override
    public BlockState getPlant(BlockGetter level, BlockPos pos) {
        return this.defaultBlockState().setValue(META, level.getBlockState(pos).getValue(META));
    }

    // ========== СОЗДАНИЕ СВОЙСТВ ==========
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(META);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.0F)
                .sound(SoundType.GRASS)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
}