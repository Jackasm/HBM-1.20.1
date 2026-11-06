package com.hbm.blocks.generic;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockSpeedy extends Block {

    private final double speed;

    public BlockSpeedy(Properties properties, double speed) {
        super(properties);
        this.speed = speed;
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (level.isClientSide && entity instanceof Player player) {
            // Проверяем, движется ли игрок
            if (player.zza != 0 || player.xxa != 0) {
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x * speed, motion.y, motion.z * speed);
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @org.jetbrains.annotations.Nullable net.minecraft.world.level.BlockGetter level,
                                @NotNull List<Component> tooltip, @NotNull net.minecraft.world.item.TooltipFlag flag) {
        int percent = (int) ((speed - 1.0D) * 100);
        tooltip.add(Component.literal(ChatFormatting.BLUE + "Increases speed by " + percent + "%"));
    }
}