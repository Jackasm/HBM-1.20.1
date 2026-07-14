package com.hbm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

public interface IAnalyzable {

    List<String> getDebugInfo(Level level, BlockPos pos);
}
