package com.hbm.blocks.generic;

import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

public class BlockDecoCTModelData {
    public static final ModelProperty<Boolean> HAS_DIAGONAL_NE = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_NW = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_SE = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_SW = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_UE = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_UW = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_DE = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_DW = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_UN = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_US = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_DN = new ModelProperty<>();
    public static final ModelProperty<Boolean> HAS_DIAGONAL_DS = new ModelProperty<>();

    @NotNull
    public static ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState currentState) {
        boolean hasDiagonalNE = checkDiagonalBlock(level, pos, Direction.EAST, Direction.NORTH, currentState);
        boolean hasDiagonalNW = checkDiagonalBlock(level, pos, Direction.WEST, Direction.NORTH, currentState);
        boolean hasDiagonalSE = checkDiagonalBlock(level, pos, Direction.EAST, Direction.SOUTH, currentState);
        boolean hasDiagonalSW = checkDiagonalBlock(level, pos, Direction.WEST, Direction.SOUTH, currentState);
        boolean hasDiagonalUE = checkDiagonalBlock(level, pos, Direction.EAST, Direction.UP, currentState);
        boolean hasDiagonalUW = checkDiagonalBlock(level, pos, Direction.WEST, Direction.UP, currentState);
        boolean hasDiagonalDE = checkDiagonalBlock(level, pos, Direction.EAST, Direction.DOWN, currentState);
        boolean hasDiagonalDW = checkDiagonalBlock(level, pos, Direction.WEST, Direction.DOWN, currentState);
        boolean hasDiagonalUN = checkDiagonalBlock(level, pos, Direction.UP, Direction.NORTH, currentState);
        boolean hasDiagonalUS = checkDiagonalBlock(level, pos, Direction.UP, Direction.SOUTH, currentState);
        boolean hasDiagonalDN = checkDiagonalBlock(level, pos, Direction.DOWN, Direction.NORTH, currentState);
        boolean hasDiagonalDS = checkDiagonalBlock(level, pos, Direction.DOWN, Direction.SOUTH, currentState);

        return ModelData.builder()
                .with(HAS_DIAGONAL_NE, hasDiagonalNE)
                .with(HAS_DIAGONAL_NW, hasDiagonalNW)
                .with(HAS_DIAGONAL_SE, hasDiagonalSE)
                .with(HAS_DIAGONAL_SW, hasDiagonalSW)
                .with(HAS_DIAGONAL_UE, hasDiagonalUE)
                .with(HAS_DIAGONAL_UW, hasDiagonalUW)
                .with(HAS_DIAGONAL_DE, hasDiagonalDE)
                .with(HAS_DIAGONAL_DW, hasDiagonalDW)
                .with(HAS_DIAGONAL_UN, hasDiagonalUN)
                .with(HAS_DIAGONAL_US, hasDiagonalUS)
                .with(HAS_DIAGONAL_DN, hasDiagonalDN)
                .with(HAS_DIAGONAL_DS, hasDiagonalDS)
                .build();
    }


    private static boolean checkDiagonalBlock(BlockAndTintGetter level, BlockPos pos, Direction dir1, Direction dir2, BlockState currentState) {
        BlockPos diagonalPos = pos.relative(dir1).relative(dir2);
        BlockState diagonalState = level.getBlockState(diagonalPos);

        if (!isSameDecoBlock(diagonalState, currentState)) {
            return false;
        }

        boolean hasConnection1 = diagonalState.getValue(getPropertyForDirection(dir1.getOpposite()));
        boolean hasConnection2 = diagonalState.getValue(getPropertyForDirection(dir2.getOpposite()));

        BlockState intermediate1 = level.getBlockState(pos.relative(dir1));
        BlockState intermediate2 = level.getBlockState(pos.relative(dir2));

        boolean hasIntermediate1 = isSameDecoBlock(intermediate1, currentState);
        boolean hasIntermediate2 = isSameDecoBlock(intermediate2, currentState);

        return hasConnection1 && hasConnection2 && hasIntermediate1 && hasIntermediate2;
    }

    private static BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockDecoCT.NORTH;
            case SOUTH -> BlockDecoCT.SOUTH;
            case EAST -> BlockDecoCT.EAST;
            case WEST -> BlockDecoCT.WEST;
            case UP -> BlockDecoCT.UP;
            case DOWN -> BlockDecoCT.DOWN;
        };
    }

    private static boolean isSameDecoBlock(BlockState state, BlockState currentState) {
        return state.getBlock() == currentState.getBlock();
    }
}