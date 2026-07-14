package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SandbagBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    // Формы для разных направлений
    private static final VoxelShape SHAPE_NORTH = Shapes.box(0.125, 0, 0, 0.875, 1, 0.875);
    private static final VoxelShape SHAPE_SOUTH = Shapes.box(0.125, 0, 0.125, 0.875, 1, 1);
    private static final VoxelShape SHAPE_WEST = Shapes.box(0, 0, 0.125, 0.875, 1, 0.875);
    private static final VoxelShape SHAPE_EAST = Shapes.box(0.125, 0, 0.125, 1, 1, 0.875);

    // Формы с учётом соседей
    private static final VoxelShape SHAPE_NORTH_LEFT = Shapes.box(0, 0, 0, 0.875, 1, 0.875);
    private static final VoxelShape SHAPE_NORTH_RIGHT = Shapes.box(0.125, 0, 0, 1, 1, 0.875);
    private static final VoxelShape SHAPE_SOUTH_LEFT = Shapes.box(0, 0, 0.125, 0.875, 1, 1);
    private static final VoxelShape SHAPE_SOUTH_RIGHT = Shapes.box(0.125, 0, 0.125, 1, 1, 1);
    private static final VoxelShape SHAPE_WEST_LEFT = Shapes.box(0, 0, 0, 0.875, 1, 0.875);
    private static final VoxelShape SHAPE_WEST_RIGHT = Shapes.box(0, 0, 0.125, 0.875, 1, 1);
    private static final VoxelShape SHAPE_EAST_LEFT = Shapes.box(0.125, 0, 0, 1, 1, 0.875);
    private static final VoxelShape SHAPE_EAST_RIGHT = Shapes.box(0.125, 0, 0.125, 1, 1, 1);

    public SandbagBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(FACING);

        // Проверяем соседей для соединения
        boolean leftConnected = isSandbag(level, pos.relative(facing.getClockWise()));
        boolean rightConnected = isSandbag(level, pos.relative(facing.getCounterClockWise()));

        return switch (facing) {
            case NORTH -> getNorthShape(leftConnected, rightConnected);
            case SOUTH -> getSouthShape(leftConnected, rightConnected);
            case WEST -> getWestShape(leftConnected, rightConnected);
            case EAST -> getEastShape(leftConnected, rightConnected);
            default -> SHAPE_NORTH;
        };
    }

    private boolean isSandbag(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof SandbagBlock;
    }

    private VoxelShape getNorthShape(boolean left, boolean right) {
        if (left && right) return SHAPE_NORTH;
        if (left) return SHAPE_NORTH_LEFT;
        if (right) return SHAPE_NORTH_RIGHT;
        return SHAPE_NORTH;
    }

    private VoxelShape getSouthShape(boolean left, boolean right) {
        if (left && right) return SHAPE_SOUTH;
        if (left) return SHAPE_SOUTH_LEFT;
        if (right) return SHAPE_SOUTH_RIGHT;
        return SHAPE_SOUTH;
    }

    private VoxelShape getWestShape(boolean left, boolean right) {
        if (left && right) return SHAPE_WEST;
        if (left) return SHAPE_WEST_LEFT;
        if (right) return SHAPE_WEST_RIGHT;
        return SHAPE_WEST;
    }

    private VoxelShape getEastShape(boolean left, boolean right) {
        if (left && right) return SHAPE_EAST;
        if (left) return SHAPE_EAST_LEFT;
        if (right) return SHAPE_EAST_RIGHT;
        return SHAPE_EAST;
    }
}