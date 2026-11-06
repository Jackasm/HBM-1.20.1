package com.hbm.blocks.bomb;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.particle.helper.ExplosionSmallCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class BlockChargeMiner extends BlockChargeBase {

    public BlockChargeMiner(Properties properties) {
        super(properties);
    }

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        if (level.isClientSide) return BombReturnCode.UNDEFINED;

        safe = true;
        level.removeBlock(pos, false);
        safe = false;

        ExplosionVNT exp = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4F);
        exp.addAllAttrib(ExplosionVNT.ExAttrib.NOHURT, ExplosionVNT.ExAttrib.ALLDROP);
        exp.explode();
        ExplosionSmallCreator.composeEffect(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15, 3F, 1.25F);

        return BombReturnCode.DETONATED;
    }

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        List<String> keys = new java.util.ArrayList<>(java.util.Arrays.asList(super.getTooltipKeys(stack)));
        keys.add(this.getDescriptionId() + ".desc4");
        keys.add(this.getDescriptionId() + ".desc5");
        return keys.toArray(new String[0]);
    }
}