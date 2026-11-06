package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class StairBlockNTM extends StairBlock {

    public StairBlockNTM(Supplier<BlockState> baseState, Properties properties) {
        super(baseState, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(SHAPE, StairsShape.STRAIGHT)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction,
                                           @NotNull BlockState neighborState, @NotNull LevelAccessor level,
                                           @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            // Пересчитываем форму ступеньки
            StairsShape newShape = calculateShape(state, level, pos);
            if (newShape != state.getValue(SHAPE)) {
                state = state.setValue(SHAPE, newShape);
            }
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private StairsShape calculateShape(BlockState state, LevelAccessor level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Half half = state.getValue(HALF);

        // Проверяем левого соседа
        BlockPos leftPos = pos.relative(facing.getCounterClockWise());
        BlockState leftState = level.getBlockState(leftPos);

        // Проверяем правого соседа
        BlockPos rightPos = pos.relative(facing.getClockWise());
        BlockState rightState = level.getBlockState(rightPos);

        // Проверяем соседа спереди
        BlockPos frontPos = pos.relative(facing);
        BlockState frontState = level.getBlockState(frontPos);

        // Проверяем соседа сзади
        BlockPos backPos = pos.relative(facing.getOpposite());
        BlockState backState = level.getBlockState(backPos);

        // Логика определения формы (как в ванильном StairBlock)
        boolean leftIsStair = leftState.getBlock() instanceof StairBlockNTM;
        boolean rightIsStair = rightState.getBlock() instanceof StairBlockNTM;
        boolean frontIsStair = frontState.getBlock() instanceof StairBlockNTM;
        boolean backIsStair = backState.getBlock() instanceof StairBlockNTM;

        // Если левый сосед — ступенька с противоположным направлением и той же половиной
        if (leftIsStair && leftState.getValue(FACING) == facing.getOpposite() &&
                leftState.getValue(HALF) == half) {
            return StairsShape.OUTER_LEFT;
        }

        // Если правый сосед — ступенька с противоположным направлением и той же половиной
        if (rightIsStair && rightState.getValue(FACING) == facing.getOpposite() &&
                rightState.getValue(HALF) == half) {
            return StairsShape.OUTER_RIGHT;
        }

        // Если передний сосед — ступенька с противоположным направлением и той же половиной
        if (frontIsStair && frontState.getValue(FACING) == facing.getOpposite() &&
                frontState.getValue(HALF) == half) {
            return StairsShape.INNER_RIGHT;
        }

        // Если задний сосед — ступенька с таким же направлением и той же половиной
        if (backIsStair && backState.getValue(FACING) == facing &&
                backState.getValue(HALF) == half) {
            return StairsShape.INNER_LEFT;
        }

        return StairsShape.STRAIGHT;
    }

    public static Properties createProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(2.0F, 15.0F)
                .lightLevel(state -> 15)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE)
                .noOcclusion();
    }
}