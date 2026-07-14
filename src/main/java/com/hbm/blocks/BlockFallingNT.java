package com.hbm.blocks;

import com.hbm.entity.item.EntityFallingBlockNT;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockFallingNT extends FallingBlock {

    public BlockFallingNT(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        world.scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        world.scheduleTick(pos, this, this.getDelayAfterPlace());
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    // ИСПРАВЛЕНИЕ: Используем RandomSource вместо Random
    @Override
    public void tick(@NotNull BlockState state, ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if(!world.isClientSide) {
            this.checkFallable(world, pos);
        }
    }

    private void checkFallable(Level world, BlockPos pos) {
        if(isFree(world.getBlockState(pos.below())) && pos.getY() >= world.getMinBuildHeight()) {
            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(world, pos, world.getBlockState(pos));
            this.falling(fallingblockentity);
            world.addFreshEntity(fallingblockentity);
        }
    }

    @Override
    protected void falling(@NotNull FallingBlockEntity fallingEntity) {
        // Базовая реализация - можно переопределить в дочерних классах
    }

    public void onLand(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState fallingState, @NotNull BlockState hitState, @NotNull Entity fallingBlock) {
        // Базовая реализация
    }

    @OnlyIn(Dist.CLIENT)
    public void overrideRenderer(EntityFallingBlockNT entity, PoseStack poseStack,
                                 VertexConsumer vertexConsumer, int packedLight) {
        // Пустая реализация по умолчанию
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldOverrideRenderer() {
        return false;
    }
}