package com.hbm.blocks.network;

import com.hbm.tileentity.network.TileEntityConnector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConnectorRedWire extends PylonBase {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());

    public ConnectorRedWire(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityConnector(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        float pixel = 0.0625F;
        float min = pixel * 5F;
        float max = pixel * 11F;

        Direction dir = state.getValue(FACING).getOpposite();

        float minX = dir == Direction.WEST ? 0F : min;
        float maxX = dir == Direction.EAST ? 1F : max;
        float minY = dir == Direction.DOWN ? 0F : min;
        float maxY = dir == Direction.UP ? 1F : max;
        float minZ = dir == Direction.NORTH ? 0F : min;
        float maxZ = dir == Direction.SOUTH ? 1F : max;

        return box(minX * 16, minY * 16, minZ * 16, maxX * 16, maxY * 16, maxZ * 16);
    }

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        return new String[]{
                "tooltip.hbm.connection.type.single",
                "tooltip.hbm.connection.range.10m"
        };
    }
}