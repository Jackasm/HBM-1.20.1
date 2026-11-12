package com.hbm.blocks.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Shared block-shot hook for NTM projectiles.
 *
 * Unlike BlockDetonatable, this contract does not imply TNT priming,
 * fire ignition, a particular superclass or a new explosion implementation.
 * Implementations are responsible for server-only, single-use detonation.
 */
public interface IShotDetonatable {

    void onShot(Level level, BlockPos pos);
}
