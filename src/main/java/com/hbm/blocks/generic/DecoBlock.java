package com.hbm.blocks.generic;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;

import com.hbm.datagen.worldgen.nbt.INBTBlockTransformable;
import com.hbm.tileentity.deco.TileEntityDeco;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DecoBlock extends BaseEntityBlock implements IToolable, INBTBlockTransformable {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public DecoBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(10.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float fX, float fY, float fZ, ToolType tool) {
        if (tool != ToolType.SCREWDRIVER) return false;
        if (this != ModBlocks.STEEL_WALL.get() && this != ModBlocks.STEEL_CORNER.get()) return false;

        BlockState state = level.getBlockState(pos);
        Direction facing = state.getValue(FACING);
        Direction newFacing;

        if (!player.isShiftKeyDown()) {
            newFacing = facing.getClockWise();
        } else {
            newFacing = facing.getCounterClockWise();
        }

        level.setBlock(pos, state.setValue(FACING, newFacing), 3);
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(@NotNull BlockState state, @NotNull BlockState adjacentState, @NotNull Direction side) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityDeco(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {

        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(FACING);
        float f = 0.0625F;

        if (this == ModBlocks.STEEL_WALL.get()) {
            return switch (facing) {
                case WEST -> Shapes.box(14 * f, 0, 0, 1, 1, 1);
                case EAST -> Shapes.box(0, 0, 0, 2 * f, 1, 1);
                case NORTH -> Shapes.box(0, 0, 14 * f, 1, 1, 1); //Shapes.box(0, 0, 0, 2 * f, 1, 1);
                case SOUTH -> Shapes.box(0, 0, 0, 1, 1, 2 * f);
                default -> Shapes.block();
            };
        }

        if (this == ModBlocks.STEEL_CORNER.get()) {
            float thickness = 2 * f;


            return switch (facing) {
                case WEST -> Shapes.or(
                        Shapes.box(0, 0, 1 - thickness, 1, 1, 1),
                        Shapes.box(1 - thickness, 0, 0, 1, 1, 1 - thickness)
                );
                case SOUTH -> Shapes.or(
                        Shapes.box(0, 0, 0, 1, 1, thickness),
                        Shapes.box(1 - thickness, 0, thickness, 1, 1, 1)
                );
                case EAST -> Shapes.or(
                        Shapes.box(0, 0, 0, thickness, 1, 1),
                        Shapes.box(0, 0, 0, 1, 1, thickness)
                );
                case NORTH -> Shapes.or(
                        Shapes.box(0, 0, 0, thickness, 1, 1),
                        Shapes.box(thickness, 0, 1 - thickness, 1, 1, 1)
                );
                default -> Shapes.block();
            };
        }

        if (this == ModBlocks.STEEL_ROOF.get()) {
            return Shapes.box(0, 0, 0, 1, 1 * f, 1);
        }

        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (placer != null) {
            Direction facing = placer.getDirection().getOpposite();
            level.setBlock(pos, state.setValue(FACING, facing), 2);
        }
    }

    @Override
    public BlockState transformState(BlockState state, int rotation) {
        Direction newFacing = state.getValue(FACING);
        for (int i = 0; i < rotation; i++) {
            newFacing = newFacing.getClockWise();
        }
        return state.setValue(FACING, newFacing);
    }
}