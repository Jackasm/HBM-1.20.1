package com.hbm.blocks.generic;

import com.hbm.blocks.IRadResistantBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockRadResistant extends Block implements IRadResistantBlock {

    public BlockRadResistant(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                        @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        //ChunkRadiationHandlerNT.markChunkForRebuild(level, pos);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        //ChunkRadiationHandlerNT.markChunkForRebuild(level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public int getResistance() {
        return 1;
    }
}