package com.hbm.create.blocks;

import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface TransformableBlock {
    BlockState transform(BlockState state, StructureTransform transform);
}
