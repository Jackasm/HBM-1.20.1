package com.hbm.blocks.bomb;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.particle.helper.ExplosionSmallCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BlockChargeDynamite extends BlockChargeBase {

    public BlockChargeDynamite(Properties properties) {
        super(properties);
    }

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        if (level.isClientSide) return BombReturnCode.UNDEFINED;

        safe = true;
        level.removeBlock(pos, false);
        safe = false;

        ExplosionVNT exp = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4F);
        exp.explode();
        ExplosionSmallCreator.composeEffect(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15, 3F, 1.25F);

        return BombReturnCode.DETONATED;
    }
}