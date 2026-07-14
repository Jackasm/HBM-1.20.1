package com.hbm.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

public interface IDepthRockTool {


    boolean canBreakRock(BlockGetter level, Player player, ItemStack tool, Block block, BlockPos pos);
}

