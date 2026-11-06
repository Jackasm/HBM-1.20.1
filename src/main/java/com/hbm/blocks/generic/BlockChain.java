package com.hbm.blocks.generic;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockChain extends BaseEntityBlock {

    public static final EnumProperty<ChainOrientation> ORIENTATION =
            EnumProperty.create("orientation", ChainOrientation.class);

    private static final VoxelShape SHAPE_VERTICAL   = Block.box(7, 0, 7, 9, 16, 9);
    private static final VoxelShape SHAPE_WALL_NORTH = Block.box(6.5, 0, 0, 9.5, 16, 1);
    private static final VoxelShape SHAPE_WALL_SOUTH = Block.box(6.5, 0, 15, 9.5, 16, 16);
    private static final VoxelShape SHAPE_WALL_EAST  = Block.box(15, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape SHAPE_WALL_WEST  = Block.box(0, 0, 6.5, 1, 16, 9.5);

    public BlockChain(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, ChainOrientation.VERTICAL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityChain(pos, state);
    }

    @Override
    public boolean isLadder(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos,
                            @NotNull LivingEntity entity) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        boolean hasSupport = hasSupportBelow(level, pos, state);

        if (!hasSupport) {
            return switch (state.getValue(ORIENTATION)) {
                case VERTICAL   -> Block.box(7, 5, 7, 9, 16, 9);
                case WALL_NORTH -> Block.box(6.5, 5, 0, 9.5, 16, 1);
                case WALL_SOUTH -> Block.box(6.5, 5, 15, 9.5, 16, 16);
                case WALL_EAST  -> Block.box(15, 5, 6.5, 16, 16, 9.5);
                case WALL_WEST  -> Block.box(0, 5, 6.5, 1, 16, 9.5);
            };
        }

        return switch (state.getValue(ORIENTATION)) {
            case VERTICAL   -> SHAPE_VERTICAL;
            case WALL_NORTH -> SHAPE_WALL_NORTH;
            case WALL_SOUTH -> SHAPE_WALL_SOUTH;
            case WALL_EAST  -> SHAPE_WALL_EAST;
            case WALL_WEST  -> SHAPE_WALL_WEST;
        };
    }

    public static boolean hasSupportBelow(BlockGetter level, BlockPos pos, BlockState state) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.is(state.getBlock())) return true;
        return belowState.isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                              @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();

        // Продолжение вниз от верхней цепи
        BlockState aboveState = level.getBlockState(pos.above());
        if (aboveState.is(this) && aboveState.getValue(ORIENTATION) == ChainOrientation.VERTICAL) {
            return defaultBlockState().setValue(ORIENTATION, ChainOrientation.VERTICAL);
        }

        // Потолок — клик по нижней грани блока сверху
        if (clickedFace == Direction.DOWN) {
            BlockPos abovePos = pos.above();
            BlockState above = level.getBlockState(abovePos);
            if (above.isFaceSturdy(level, abovePos, Direction.DOWN) || above.is(this)) {
                return defaultBlockState().setValue(ORIENTATION, ChainOrientation.VERTICAL);
            }
        }

        // Боковая стена
        if (clickedFace.getAxis().isHorizontal()) {
            BlockPos wallPos = pos.relative(clickedFace.getOpposite());
            BlockState wallState = level.getBlockState(wallPos);
            if (wallState.isFaceSturdy(level, wallPos, clickedFace)) {
                ChainOrientation orientation = switch (clickedFace) {
                    case NORTH -> ChainOrientation.WALL_SOUTH;
                    case SOUTH -> ChainOrientation.WALL_NORTH;
                    case EAST  -> ChainOrientation.WALL_WEST;
                    case WEST  -> ChainOrientation.WALL_EAST;
                    default    -> ChainOrientation.VERTICAL;
                };
                return defaultBlockState().setValue(ORIENTATION, orientation);
            }
        }

        return null;
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        ChainOrientation orientation = state.getValue(ORIENTATION);

        if (orientation == ChainOrientation.VERTICAL) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.is(this)) return true;
            return aboveState.isFaceSturdy(level, abovePos, Direction.DOWN);
        }

        Direction wallDir = switch (orientation) {
            case WALL_NORTH -> Direction.NORTH;
            case WALL_SOUTH -> Direction.SOUTH;
            case WALL_EAST  -> Direction.EAST;
            case WALL_WEST  -> Direction.WEST;
            default -> null;
        };
        if (wallDir == null) return false;

        BlockPos wallPos = pos.relative(wallDir);
        BlockState wallState = level.getBlockState(wallPos);
        if (wallState.is(this)) return true;
        return wallState.isFaceSturdy(level, wallPos, wallDir.getOpposite());
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide && !canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    public enum ChainOrientation implements StringRepresentable {
        VERTICAL("vertical"),
        WALL_NORTH("wall_north"),
        WALL_SOUTH("wall_south"),
        WALL_EAST("wall_east"),
        WALL_WEST("wall_west");

        private final String name;
        ChainOrientation(String name) { this.name = name; }

        @Override
        public @NotNull String getSerializedName() { return name; }
    }

    private static @Nullable Direction toDirection(ChainOrientation orientation) {
        return switch (orientation) {
            case WALL_NORTH -> Direction.NORTH;
            case WALL_SOUTH -> Direction.SOUTH;
            case WALL_EAST -> Direction.EAST;
            case WALL_WEST -> Direction.WEST;
            default -> null;
        };
    }

    private static ChainOrientation fromDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> ChainOrientation.WALL_NORTH;
            case SOUTH -> ChainOrientation.WALL_SOUTH;
            case EAST -> ChainOrientation.WALL_EAST;
            case WEST -> ChainOrientation.WALL_WEST;
            default -> ChainOrientation.VERTICAL;
        };
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        ChainOrientation orientation = state.getValue(ORIENTATION);
        Direction dir = toDirection(orientation);
        if (dir == null) return state;  // VERTICAL не вращается
        return state.setValue(ORIENTATION, fromDirection(rotation.rotate(dir)));
    }

    public static class TileEntityChain extends BlockEntity {
        public TileEntityChain(BlockPos pos, BlockState state) {
            super(ModTileEntity.CHAIN.get(), pos, state);
        }
    }
}