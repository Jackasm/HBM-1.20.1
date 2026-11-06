package com.hbm.blocks.generic;

import com.hbm.blocks.machine.BlockPillar;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockWriting extends BlockPillar {

    private static final int MESSAGE_COUNT = 10;

    public BlockWriting(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            player.sendSystemMessage(Component
                    .translatable("block.hbm.brick_concrete_marked.msg." + i)
                    .withStyle(ChatFormatting.RED));
        }
        return InteractionResult.CONSUME;
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(15.0F, 160.0F)
                .requiresCorrectToolForDrops();
    }
}