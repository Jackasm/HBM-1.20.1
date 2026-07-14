package com.hbm.blocks.generic;

import com.hbm.blocks.machine.ZirnoxDestroyed;
import com.hbm.blocks.machine.rbmk.RBMKDebris;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLayering extends Block {

    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);

    private static final VoxelShape[] SHAPES = new VoxelShape[9];

    static {
        for (int i = 1; i <= 8; i++) {
            SHAPES[i] = Shapes.box(0, 0, 0, 1, i / 16.0, 1);
        }
    }

    public BlockLayering(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    public static Properties createFoamProperties() {
        return Properties.of()
                .strength(0.1F)
                .noOcclusion()
                .replaceable()
                .pushReaction(PushReaction.DESTROY);
    }

    public static Properties createSandProperties() {
        return Properties.of()
                .strength(0.1F)
                .noOcclusion()
                .replaceable()
                .pushReaction(PushReaction.DESTROY);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        int layers = state.getValue(LAYERS);
        return SHAPES[layers];
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        int layers = state.getValue(LAYERS);
        return SHAPES[layers];
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        Block belowBlock = belowState.getBlock();

        if (belowBlock instanceof RBMKDebris || belowBlock instanceof ZirnoxDestroyed) {
            return true;
        }

        return belowState.isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity te, @NotNull ItemStack stack) {
        if (!level.isClientSide) {
            int layers = state.getValue(LAYERS);
            popResource(level, pos, new ItemStack(this, layers));
        }
        super.playerDestroy(level, player, pos, state, te, stack);
        level.removeBlock(pos, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(@NotNull BlockState state, @NotNull BlockState adjacentState, @NotNull Direction side) {
        if (side == Direction.UP) return true;
        return super.skipRendering(state, adjacentState, side);
    }

    @Override
    public boolean isCollisionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }
}