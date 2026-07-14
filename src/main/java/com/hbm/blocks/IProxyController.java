package com.hbm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IProxyController {

    BlockEntity getCore(Level world, BlockPos pos);
}