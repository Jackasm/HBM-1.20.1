package com.hbm.create.registry;

import com.hbm.create.blockentity.ItemRequirement;
import net.minecraft.world.level.block.state.BlockState;

public interface SpecialBlockEntityItemRequirement {
    ItemRequirement getRequiredItems(BlockState state);
}
