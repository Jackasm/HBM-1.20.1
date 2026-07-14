package com.hbm.blocks.network;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityCableBaseNT;
import com.hbm.util.Library;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCable extends BaseEntityBlock {

    public BlockCable(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityCableBaseNT(ModTileEntity.RED_CABLE.get(), pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.RED_CABLE.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityCableBaseNT cable) {
                    cable.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getDynamicShape(level, pos);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    private VoxelShape getDynamicShape(BlockGetter level, BlockPos pos) {
        boolean posX = Library.canConnect(level, pos.relative(Direction.EAST), Direction.EAST);
        boolean negX = Library.canConnect(level, pos.relative(Direction.WEST), Direction.WEST);
        boolean posY = Library.canConnect(level, pos.relative(Direction.UP), Direction.UP);
        boolean negY = Library.canConnect(level, pos.relative(Direction.DOWN), Direction.DOWN);
        boolean posZ = Library.canConnect(level, pos.relative(Direction.SOUTH), Direction.SOUTH);
        boolean negZ = Library.canConnect(level, pos.relative(Direction.NORTH), Direction.NORTH);

        float pixel = 0.0625F;
        float min = pixel * 5.5F;
        float max = pixel * 10.5F;

        float minX = negX ? 0F : min;
        float maxX = posX ? 1F : max;
        float minY = negY ? 0F : min;
        float maxY = posY ? 1F : max;
        float minZ = negZ ? 0F : min;
        float maxZ = posZ ? 1F : max;

        return box(minX * 16, minY * 16, minZ * 16, maxX * 16, maxY * 16, maxZ * 16);
    }
}