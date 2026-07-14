package com.hbm.blocks.network;

import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityFoundryOutlet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoundryOutlet extends FoundryCastingBase {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    public FoundryOutlet(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityFoundryOutlet(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.FOUNDRY_OUTLET.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityFoundryOutlet outlet) {
                    TileEntityFoundryOutlet.serverTick(lvl, pos, st, outlet);
                }
            };
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case SOUTH -> Shapes.box(0.3125, 0, 0, 0.6875, 0.5, 0.375);
            case NORTH -> Shapes.box(0.3125, 0, 0.625, 0.6875, 0.5, 1);
            case EAST -> Shapes.box(0, 0, 0.3125, 0.375, 0.5, 0.6875);
            case WEST -> Shapes.box(0.625, 0, 0.3125, 1, 0.5, 0.6875);
            default -> Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.5, 0.6875);
        };
    }

    @Override
    public boolean canAcceptPartialPour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        return false;
    }

    @Override
    public MaterialStack pour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        return stack;
    }
}