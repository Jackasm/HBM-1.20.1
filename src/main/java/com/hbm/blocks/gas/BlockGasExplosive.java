package com.hbm.blocks.gas;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class BlockGasExplosive extends BlockGasFlammable {

    public BlockGasExplosive(Properties properties) {
        super(properties);
    }

    @Override
    protected void combust(Level world, BlockPos pos) {
        Set<BlockPos> gasBlocks = findConnectedGas(world, pos);

        for (BlockPos gasPos : gasBlocks) {
            world.setBlock(gasPos, Blocks.FIRE.defaultBlockState(), 3);
        }

        // Взрыв в центре пузыря
        BlockPos center = getBubbleCenter(gasBlocks);
        world.explode(null, center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                3.0F + (gasBlocks.size() * 0.1F), Level.ExplosionInteraction.TNT);
    }

    private BlockPos getBubbleCenter(Set<BlockPos> gasBlocks) {
        int sumX = 0, sumY = 0, sumZ = 0;
        for (BlockPos pos : gasBlocks) {
            sumX += pos.getX();
            sumY += pos.getY();
            sumZ += pos.getZ();
        }
        int size = gasBlocks.size();
        return new BlockPos(sumX / size, sumY / size, sumZ / size);
    }
}