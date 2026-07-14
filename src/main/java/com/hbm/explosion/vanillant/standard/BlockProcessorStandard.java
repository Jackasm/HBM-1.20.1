package com.hbm.explosion.vanillant.standard;

import java.util.HashSet;
import java.util.Iterator;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import com.hbm.explosion.vanillant.interfaces.IBlockProcessor;
import com.hbm.explosion.vanillant.interfaces.IDropChanceMutator;
import com.hbm.explosion.vanillant.interfaces.IFortuneMutator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockProcessorStandard implements IBlockProcessor {

    protected IDropChanceMutator chance;
    protected IFortuneMutator fortune;
    protected IBlockMutator convert;

    public BlockProcessorStandard() { }

    public BlockProcessorStandard withChance(IDropChanceMutator chance) {
        this.chance = chance;
        return this;
    }

    public BlockProcessorStandard withFortune(IFortuneMutator fortune) {
        this.fortune = fortune;
        return this;
    }

    public BlockProcessorStandard withBlockEffect(IBlockMutator convert) {
        this.convert = convert;
        return this;
    }

    @Override
    public void process(ExplosionVNT explosion, Level world, double x, double y, double z, HashSet<BlockPos> affectedBlocks) {
        if(world.isClientSide()) return;

        Iterator<BlockPos> iterator = affectedBlocks.iterator();
        float dropChance = 1.0F / explosion.size;

        while(iterator.hasNext()) {
            BlockPos pos = iterator.next();
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if(!state.isAir()) {
                if(block.canDropFromExplosion(state, world, pos, explosion.compat)) {

                    if(chance != null) {
                        dropChance = chance.mutateDropChance(explosion, block, pos.getX(), pos.getY(), pos.getZ(), dropChance);
                    }

                    int dropFortune = fortune == null ? 0 : fortune.mutateFortune(explosion, block, pos.getX(), pos.getY(), pos.getZ());

                    if(world.random.nextFloat() <= dropChance) {
                        if(world instanceof ServerLevel serverLevel) {
                            BlockEntity tile = world.getBlockEntity(pos);
                            Block.dropResources(state, serverLevel, pos, tile, explosion.exploder, ItemStack.EMPTY);
                        }
                    }
                }

                block.onBlockExploded(state, world, pos, explosion.compat);

                if(this.convert != null) {
                    this.convert.mutatePre(explosion, block, 0, pos);
                }
            } else {
                iterator.remove();
            }
        }

        if(this.convert != null) {
            iterator = affectedBlocks.iterator();

            while(iterator.hasNext()) {
                BlockPos pos = iterator.next();
                BlockState state = world.getBlockState(pos);

                if(state.isAir()) {
                    this.convert.mutatePost(explosion, pos);
                }
            }
        }
    }

    public BlockProcessorStandard setNoDrop() {
        this.chance = new DropChanceMutatorStandard(0F);
        return this;
    }

    public BlockProcessorStandard setAllDrop() {
        this.chance = new DropChanceMutatorStandard(1F);
        return this;
    }

    public BlockProcessorStandard setFortune(int fortuneLevel) {
        this.fortune = new IFortuneMutator() {
            @Override
            public int mutateFortune(ExplosionVNT explosion, Block block, int x, int y, int z) {
                return fortuneLevel;
            }

            @Override
            public int mutateFortune(ExplosionVNT explosion, BlockState state, int x, int y, int z) {
                return fortuneLevel;
            }
        };
        return this;
    }
}