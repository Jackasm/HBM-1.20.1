package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMutatorBulkie implements IBlockMutator {

    protected BlockState targetState;

    public BlockMutatorBulkie(BlockState state) {
        this.targetState = state;
    }

    public BlockMutatorBulkie(Block block) {
        this(block.defaultBlockState());
    }

    public BlockMutatorBulkie(Block block, int meta) {
        this(block.defaultBlockState());
    }

    @Override
    public void mutatePre(ExplosionVNT explosion, Block block, int meta, BlockPos pos) {
        // Не используем pre
    }

    @Override
    public void mutatePre(ExplosionVNT explosion, BlockState state, BlockPos pos) {
        // Не используем pre
    }

    @Override
    public void mutatePost(ExplosionVNT explosion, BlockPos pos) {
        // Проверяем, что блок был разрушен (теперь воздух)
        if (explosion.world.getBlockState(pos).isAir()) {
            explosion.world.setBlock(pos, targetState, 3);
        }
    }
}