package com.hbm.blocks.generic;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockMetalFence extends BaseEntityBlock {

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final EnumProperty<FenceType> TYPE = EnumProperty.create("type", FenceType.class);

    public enum FenceType implements StringRepresentable {
        NORMAL("normal"),
        POST("post");

        private final String name;

        FenceType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    private static final VoxelShape POST_SHAPE = Block.box(6, 0, 6, 10, 16, 10);

    private static final VoxelShape EAST_SHAPE = Block.box(8, 0, 7.96875, 16, 16, 8.03125);

    private static final VoxelShape WEST_SHAPE = Block.box(0, 0, 7.96875, 8, 16, 8.03125);

    private static final VoxelShape NORTH_SHAPE = Block.box(7.96875, 0, 0, 8.03125, 16, 8);

    private static final VoxelShape SOUTH_SHAPE = Block.box(7.96875, 0, 8, 8.03125, 16, 16);

    public BlockMetalFence(FenceType type) {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(15.0F, 0.25F)
                .noOcclusion()
                .isValidSpawn((state, level, pos, entityType) -> false));

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(TYPE, type)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        boolean north = state.getValue(NORTH);
        boolean south = state.getValue(SOUTH);
        boolean east = state.getValue(EAST);
        boolean west = state.getValue(WEST);
        boolean isPost = state.getValue(TYPE) == FenceType.POST;

        boolean showPost;
        if (isPost) {
            showPost = true;
        } else {
            int count = (north ? 1 : 0) + (south ? 1 : 0) + (east ? 1 : 0) + (west ? 1 : 0);
            boolean straight = (north && south && !east && !west) || (!north && !south && east && west);
            if (count == 0) {
                showPost = true;
            } else if (count == 2 && straight) {
                showPost = false;
            } else {
                showPost = true;
            }
        }

        VoxelShape shape = Shapes.empty();

        if (showPost) {
            shape = POST_SHAPE;
        }

        if (north) shape = Shapes.or(shape, NORTH_SHAPE);
        if (south) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (east) shape = Shapes.or(shape, EAST_SHAPE);
        if (west) shape = Shapes.or(shape, WEST_SHAPE);

        return shape;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityMetalFence(pos, state);
    }

    public boolean connectsTo(BlockState state) {
        return state.getBlock() instanceof BlockMetalFence;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH, connectsTo(level.getBlockState(pos.north())))
                .setValue(SOUTH, connectsTo(level.getBlockState(pos.south())))
                .setValue(EAST, connectsTo(level.getBlockState(pos.east())))
                .setValue(WEST, connectsTo(level.getBlockState(pos.west())));
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            BlockState newState = state
                    .setValue(NORTH, connectsTo(level.getBlockState(pos.north())))
                    .setValue(SOUTH, connectsTo(level.getBlockState(pos.south())))
                    .setValue(EAST, connectsTo(level.getBlockState(pos.east())))
                    .setValue(WEST, connectsTo(level.getBlockState(pos.west())));
            level.setBlock(pos, newState, 3);
        }
    }

    public static class TileEntityMetalFence extends BlockEntity {
        public TileEntityMetalFence(BlockPos pos, BlockState state) {
            super(ModTileEntity.METAL_FENCE.get(), pos, state);
        }
    }
}