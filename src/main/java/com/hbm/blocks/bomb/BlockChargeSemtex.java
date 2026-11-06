package com.hbm.blocks.bomb;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.particle.helper.ExplosionCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class BlockChargeSemtex extends BlockChargeBase {

    public BlockChargeSemtex(Properties properties) {
        super(properties);
    }

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        if (level.isClientSide) return BombReturnCode.UNDEFINED;

        safe = true;
        level.removeBlock(pos, false);
        safe = false;

        ExplosionVNT xnt = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10F);
        xnt.setBlockAllocator(new BlockAllocatorStandard(32));
        xnt.setBlockProcessor(new BlockProcessorStandard().setAllDrop().setFortune(3));
        xnt.explode();
        ExplosionCreator.composeEffectSmall(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);

        return BombReturnCode.DETONATED;
    }

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        List<String> keys = new java.util.ArrayList<>(java.util.Arrays.asList(super.getTooltipKeys(stack)));
        keys.add(this.getDescriptionId() + ".desc4");
        keys.add(this.getDescriptionId() + ".desc5");
        keys.add(this.getDescriptionId() + ".desc6");
        keys.add(this.getDescriptionId() + ".desc7");
        return keys.toArray(new String[0]);
    }
}