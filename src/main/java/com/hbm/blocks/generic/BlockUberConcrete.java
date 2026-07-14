package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockUberConcrete extends Block {

    public static final IntegerProperty DAMAGE = IntegerProperty.create("damage", 0, 15);

    public BlockUberConcrete(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DAMAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DAMAGE);
    }

    @Override
    public void randomTick(BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, RandomSource random) {
        int damage = state.getValue(DAMAGE);

        if (random.nextInt(damage + 1) > 0) return;

        if (damage < 15) {
            level.setBlock(pos, state.setValue(DAMAGE, damage + 1), 3);
        } else {
            level.removeBlock(pos, false);

            if (level.getBlockState(pos.below()).isAir()) {
                level.setBlock(pos, ModBlocks.CONCRETE_SUPER_BROKEN.get().defaultBlockState(), 3);
                return;
            }

            List<Direction> sides = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
            Collections.shuffle(sides);

            for (Direction dir : sides) {
                BlockPos targetPos = pos.relative(dir);
                if (level.getBlockState(targetPos).isAir() && level.getBlockState(targetPos.below()).isAir()) {
                    FallingBlockEntity debris = FallingBlockEntity.fall(
                            level,
                            targetPos,
                            ModBlocks.CONCRETE_SUPER_BROKEN.get().defaultBlockState()
                    );
                    debris.setHurtsEntities(2.0f, 40);
                    debris.dropItem = false;
                    level.addFreshEntity(debris);
                    return;
                }
            }

            level.setBlock(pos, ModBlocks.CONCRETE_SUPER_BROKEN.get().defaultBlockState(), 3);
        }
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }
}