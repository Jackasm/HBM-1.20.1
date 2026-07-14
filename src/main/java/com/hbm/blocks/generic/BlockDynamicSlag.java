package com.hbm.blocks.generic;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntitySlag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockDynamicSlag extends BaseEntityBlock {

    public BlockDynamicSlag(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySlag(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.SLAG.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntitySlag slag) {
                    TileEntitySlag.serverTick(lvl, pos, st, slag);
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntitySlag slag) {
            float height = (float) slag.amount / slag.maxAmount * 0.875f + 0.125f;
            if (slag.amount <= 0) height = 0.125f;
            return box(0, 0, 0, 16, height * 16, 16);
        }
        return box(0, 0, 0, 16, 2, 16);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}