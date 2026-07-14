package com.hbm.explosion.vanillant.standard;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMutatorFire implements IBlockMutator {

    private final Block fireBlock;
    private final int chance; // 1/x chance to place fire

    public BlockMutatorFire(Block fireBlock) {
        this(fireBlock, 3); // По умолчанию 1/3 как в оригинале
    }

    public BlockMutatorFire(Block fireBlock, int chance) {
        this.fireBlock = fireBlock;
        this.chance = chance;
    }

    @Override
    public void mutatePre(ExplosionVNT explosion, Block block, int meta, BlockPos pos) {
        // Ничего не делаем до взрыва
    }

    @Override
    public void mutatePost(ExplosionVNT explosion, BlockPos pos) {
        Level world = explosion.world;
        BlockPos belowPos = pos.below();

        BlockState state = world.getBlockState(pos);
        BlockState belowState = world.getBlockState(belowPos);

        // Проверяем: блок воздуха и под ним твёрдый блок
        if (state.isAir() && belowState.isSolid() && world.random.nextInt(chance) == 0) {
            world.setBlock(pos, fireBlock.defaultBlockState(), 3);
        }
    }

    // Фабричные методы для удобства
    public static BlockMutatorFire fire() {
        return new BlockMutatorFire(Blocks.FIRE);
    }

    public static BlockMutatorFire balefire() {
        return new BlockMutatorFire(ModBlocks.BALEFIRE.get());
    }

    public static BlockMutatorFire lava() {
        return new BlockMutatorFire(Blocks.LAVA);
    }

    public static BlockMutatorFire volcanicLava() {
        return new BlockMutatorFire(ModBlocks.VOLCANIC_LAVA_BLOCK.get());
    }

    public static BlockMutatorFire radioactiveLava() {
        return new BlockMutatorFire(ModBlocks.RAD_LAVA_BLOCK.get());
    }

    public static BlockMutatorFire fireWithChance(int chance) {
        return new BlockMutatorFire(Blocks.FIRE, chance);
    }
}