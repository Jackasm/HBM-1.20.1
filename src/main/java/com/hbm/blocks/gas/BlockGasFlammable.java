package com.hbm.blocks.gas;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import com.hbm.util.Compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGasFlammable extends BlockGasBase {

    public static HashSet<Block> fireSources = new HashSet<>();

    public BlockGasFlammable(Properties properties) {
        super(properties, 0.8F, 0.8F, 0.2F);

        if(fireSources.isEmpty()) {
            fireSources.add(Blocks.FIRE);
            fireSources.add(Blocks.LAVA);
            fireSources.add(Blocks.TORCH);
            fireSources.add(Blocks.JACK_O_LANTERN);

            if(Compat.isModLoaded(Compat.MOD_TIC)) {
                Block stoneTorch = Compat.tryLoadBlock(Compat.MOD_TIC, "decoration.stonetorch");
                if(stoneTorch != null) {
                    fireSources.add(stoneTorch);
                }
            }
        }
    }

    @Override
    public Direction getFirstDirection(Level world, BlockPos pos) {
        RandomSource rand = world.getRandom();
        if(rand.nextInt(3) == 0)
            return Direction.getRandom(rand);

        return this.randomHorizontal(world.getRandom());
    }

    @Override
    public Direction getSecondDirection(Level world, BlockPos pos) {
        return this.randomHorizontal(world.getRandom());
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if(!world.isClientSide) {

            for(Direction dir : Direction.values()) {
                Block b = world.getBlockState(pos.relative(dir)).getBlock();

                if(isFireSource(b)) {
                    combust(world, pos);
                    return;
                }
            }

            if(rand.nextInt(20) == 0 && world.getBlockState(pos.below()).isAir()) {
                world.removeBlock(pos, false);
                return;
            }
        }

        super.tick(state, world, pos, rand);
    }

    @Override
    public void entityInside(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Entity entity) {
        if(!world.isClientSide && entity.isOnFire()) {
            this.combust(world, pos);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        for(Direction dir : Direction.values()) {
            Block b = world.getBlockState(pos.relative(dir)).getBlock();

            if(isFireSource(b)) {
                world.scheduleTick(pos, this, 2);
            }
        }
    }

    protected void combust(Level world, BlockPos pos) {
        // Находим все соединённые блоки газа
        Set<BlockPos> gasBlocks = findConnectedGas(world, pos);

        for (BlockPos gasPos : gasBlocks) {
            world.setBlock(gasPos, Blocks.FIRE.defaultBlockState(), 3);
        }
    }

    protected Set<BlockPos> findConnectedGas(Level world, BlockPos start) {
        Set<BlockPos> result = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        result.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.values()) {
                BlockPos next = current.relative(dir);
                if (!result.contains(next) && world.getBlockState(next).getBlock() == this) {
                    result.add(next);
                    queue.add(next);
                }
            }
        }

        return result;
    }

    public boolean isFireSource(Block b) {
        return fireSources.contains(b);
    }

    public boolean isFlammable(BlockState state, Level world, BlockPos pos, Direction face) {
        return true;
    }

    public int getDelay(Level world, BlockPos pos) {
        return world.getRandom().nextInt(5) + 16;
    }
}