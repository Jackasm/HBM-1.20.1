package com.hbm.blocks.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IFuckingExplode {

    // Anything that can be detonated by another explosion should implement this and spawn an EntityTNTPrimedBase when hit by an explosion
    // This prevents chained explosions causing a stack overflow
    // Note that the block can still safely immediately explode, as long as the source isn't another explosion

    void explodeEntity(Level world, BlockPos pos, LivingEntity entity);

}
