package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockNTMTrapdoor extends TrapDoorBlock {

    public BlockNTMTrapdoor(Properties properties) {
        super(properties, BlockSetType.IRON);
    }

    /** Аналог isLadder: открытый люк = лестница, если под ним тоже лестница. */
    @Override
    public boolean isLadder(@NotNull BlockState state, @NotNull LevelReader level,
                            @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        if (!state.getValue(BlockStateProperties.OPEN)) return false;

        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.getBlock().isLadder(belowState, level, below, entity);
    }

    /** Коллизия: если люк — лестница, тонкая полоска. */
    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.getValue(BlockStateProperties.OPEN)) {
            // Проверяем «лестничность» через BlockGetter — если снизу лестница
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            boolean isLadderBelow = belowState.getBlock() instanceof net.minecraft.world.level.block.LadderBlock
                    || belowState.getBlock() instanceof BlockNTMTrapdoor;

            if (isLadderBelow) {
                Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                float thickness = 0.125F;

                // В 1.20.1 VoxelShape зависит от направления
                return switch (facing) {
                    case NORTH -> Shapes.box(0, 0, 1F - thickness, 1, 1, 1);       // юг открыт
                    case SOUTH -> Shapes.box(0, 0, 0, 1, 1, thickness);             // север открыт
                    case WEST  -> Shapes.box(1F - thickness, 0, 0, 1, 1, 1);        // восток открыт
                    case EAST  -> Shapes.box(0, 0, 0, thickness, 1, 1);             // запад открыт
                    default    -> super.getCollisionShape(state, level, pos, context);
                };
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    /** Открываем/закрываем вручную, даже если люк железный. */
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        state = state.cycle(BlockStateProperties.OPEN);
        level.setBlock(pos, state, 2);
        level.playSound(player, pos, state.getValue(BlockStateProperties.OPEN)
                ? SoundEvents.IRON_TRAPDOOR_OPEN
                : SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1F, 1F);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                .strength(3.0F, 8.0F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .isValidSpawn((state, level, pos, type) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }
}