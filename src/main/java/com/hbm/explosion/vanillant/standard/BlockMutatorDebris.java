package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMutatorDebris implements IBlockMutator {

    protected BlockState targetState;

    public BlockMutatorDebris(Block block) {
        this(block.defaultBlockState());
    }

    public BlockMutatorDebris(BlockState state) {
        this.targetState = state;
    }

    @Override
    public void mutatePre(ExplosionVNT explosion, Block block, int meta, BlockPos pos) {
        // В 1.20.1 метаданные включены в BlockState
    }

    @Override
    public void mutatePost(ExplosionVNT explosion, BlockPos pos) {
        Level world = explosion.world;

        // Проверяем соседние блоки
        for(Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            // Проверяем, является ли соседний блок нормальным кубом (полный блок)
            if(neighborState.isSolidRender(world, neighborPos) &&
                    !neighborState.is(targetState.getBlock())) {

                // Устанавливаем целевой блок
                world.setBlock(pos, targetState, 3);
                return;
            }
        }
    }

    // Вспомогательные методы для создания мутатора

    public static BlockMutatorDebris of(Block block) {
        return new BlockMutatorDebris(block);
    }

    public static BlockMutatorDebris of(BlockState state) {
        return new BlockMutatorDebris(state);
    }
}