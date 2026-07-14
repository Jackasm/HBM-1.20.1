package com.hbm.create.registry;

import com.hbm.create.blockentity.ItemRequirement;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface SpecialBlockItemRequirement {
    ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity);
}
