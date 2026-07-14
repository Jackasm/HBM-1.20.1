package com.hbm.blocks.machine.pile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockGraphiteDrilledTE extends BlockGraphiteDrilledBase implements EntityBlock {

    public BlockGraphiteDrilledTE(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        // Дополнительная логика при установке блока
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
            level.removeBlockEntity(pos);
        }
    }

    // Заменяет ITileEntityProvider
    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);
}