package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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

    public BlockDeadPlant(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    // ========== ЛОГИКА БЛОКА ==========
    protected boolean canPlaceBlockOn(Block block) {
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT ||
                block == ModBlocks.WASTE_EARTH.get() || block == ModBlocks.DIRT_OILY.get() ||
                block == ModBlocks.DIRT_DEAD.get();
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
        return drops;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(this);
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
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        // Нет свойств состояния
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