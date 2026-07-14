package com.hbm.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IInsertable { //uwu

    boolean insertItem(Level level, BlockPos pos, Direction dir, ItemStack stack);
}
