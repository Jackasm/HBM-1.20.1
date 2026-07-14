package com.hbm.explosion.vanillant.standard;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMutatorBalefire implements IBlockMutator {

    @Override
    public void mutatePre(ExplosionVNT explosion, Block block, int meta, BlockPos pos) {
        // Ничего не делаем
    }

    @Override
    public void mutatePost(ExplosionVNT explosion, BlockPos pos) {
        var level = explosion.world;
        var rand = level.random;

        Block block = level.getBlockState(pos).getBlock();
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (block == Blocks.AIR && belowState.isSolid() && rand.nextInt(3) == 0) {
            level.setBlock(pos, ModBlocks.BALEFIRE.get().defaultBlockState(), 3);
        }
    }
}