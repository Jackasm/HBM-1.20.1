package com.hbm.blocks.network;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.tileentity.network.TileEntityPylonBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PylonBase extends BaseEntityBlock implements ITooltipProvider {

    protected PylonBase(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return (lvl, pos, st, tile) -> {
            if (tile instanceof TileEntityPylonBase pylon) {
                pylon.tick();
            }
        };
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityPylonBase pylon) {
                pylon.disconnectAll();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED; // или ENTITYBLOCK_ANIMATED для кастомного рендера
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (!player.isShiftKeyDown()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityPylonBase pylon) {
                return pylon.setColor(player.getItemInHand(hand), player) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }
}