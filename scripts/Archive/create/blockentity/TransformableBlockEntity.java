package com.hbm.create.blockentity;

import com.hbm.create.blocks.StructureTransform;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface TransformableBlockEntity {
    void transform(BlockEntity blockEntity, StructureTransform transform);
}
