package com.hbm.blocks.generic;

import com.hbm.entity.DecoCTBlockEntity;
import com.hbm.network.PacketDispatcher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockDecoCT extends Block implements EntityBlock {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public BlockDecoCT() {
        super(Properties.of()
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops());

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (!world.isClientSide()) {
            boolean isSameBlock = neighborState.is(this);
            BooleanProperty property = getPropertyForDirection(direction);
            if (state.getValue(property) != isSameBlock) {
                state = state.setValue(property, isSameBlock);
                if (world instanceof Level level) {
                    level.setBlock(pos, state, 3);
                    updateAreaModelData(level, pos);
                    level.scheduleTick(pos, this, 1);
                }
            }
        }
        return state;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onPlace(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        if (!world.isClientSide()) {
            world.scheduleTick(pos, this, 2);
            world.scheduleTick(pos, this, 4);
        }
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);
        if (!world.isClientSide() && !state.is(newState.getBlock())) {
            world.scheduleTick(pos, this, 2);
            world.scheduleTick(pos, this, 4);
        }
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        updateAreaConnections(world, pos);
        updateAreaModelData(world, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DecoCTBlockEntity(pos, state);
    }

    private void updateAreaConnections(Level world, BlockPos center) {
        List<BlockPos> blocksToUpdate = getBlocksInArea(world, center);
        for (BlockPos updatePos : blocksToUpdate) {
            updateAllConnections(world, updatePos);
        }
    }

    private void updateAreaModelData(Level world, BlockPos center) {
        List<BlockPos> blocksToUpdate = getBlocksInArea(world, center);
        for (BlockPos updatePos : blocksToUpdate) {
            forceModelDataUpdate(world, updatePos);
            PacketDispatcher.sendModelDataUpdate(world, updatePos);
        }
    }

    private List<BlockPos> getBlocksInArea(Level world, BlockPos center) {
        List<BlockPos> blocks = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = center.offset(x, y, z);
                    if (!world.isLoaded(checkPos)) continue;

                    BlockState checkState = world.getBlockState(checkPos);
                    if (isSameDecoBlock(checkState)) {
                        blocks.add(checkPos);
                    }
                }
            }
        }

        return blocks;
    }

    private boolean isSameDecoBlock(BlockState state) {
        return state.is(this);
    }

    private void updateAllConnections(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean changed = false;

        for (Direction direction : Direction.values()) {
            BooleanProperty property = getPropertyForDirection(direction);
            BlockPos neighborPos = pos.relative(direction);

            if (!world.isLoaded(neighborPos)) continue;

            BlockState neighborState = world.getBlockState(neighborPos);
            boolean isSameBlock = isSameDecoBlock(neighborState);

            if (state.getValue(property) != isSameBlock) {
                state = state.setValue(property, isSameBlock);
                changed = true;
            }
        }

        if (changed) {
            world.setBlock(pos, state, 3);
        }
    }

    private void forceModelDataUpdate(Level world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null) {
            be.requestModelDataUpdate();
            BlockState state = world.getBlockState(pos);
            world.sendBlockUpdated(pos, state, state, 3);
        }
    }

    private BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }
}