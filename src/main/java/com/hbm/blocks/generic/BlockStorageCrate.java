package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.storage.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockStorageCrate extends BaseEntityBlock {

    public BlockStorageCrate(Properties props) {
        super(props);
    }

    public static Properties createPropertiesMetal() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    public static Properties createPropertiesTungsten() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(7.5F, 300.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    public static Properties createPropertiesSafe() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(7.5F, 10000.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityCrateBase crate && crate.canAccess(player)) {
            NetworkHooks.openScreen((ServerPlayer) player, crate, pos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityCrateBase crate) {
                Containers.dropContents(level, pos, crate);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (this == ModBlocks.CRATE_IRON.get()) {
            return new TileEntityCrateIron(pos, state);
        }
        if (this == ModBlocks.CRATE_STEEL.get()) {
            return new TileEntityCrateSteel(pos, state);
        }
        if (this == ModBlocks.CRATE_DESH.get()) {
            return new TileEntityCrateDesh(pos, state);
        }
        if (this == ModBlocks.CRATE_TEMPLATE.get()) {
            return new TileEntityCrateTemplate(pos, state);
        }
        if (this == ModBlocks.CRATE_TUNGSTEN.get()) {
            return new TileEntityCrateTungsten(pos, state);
        }
        if (this == ModBlocks.SAFE.get()) {
            return new TileEntitySafe(pos, state);
        }
        return null;
    }
}