package com.hbm.items.tool;

import com.hbm.interfaces.IBomb;
import com.hbm.interfaces.IBomb.BombReturnCode;
import com.hbm.sound.ModSounds;
import com.hbm.util.ChatBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDetonator extends Item {

    public ItemDetonator(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.detonator.1"));
        tooltip.add(Component.translatable("tooltip.detonator.2"));

        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains("x") || !nbt.contains("y") || !nbt.contains("z")) {
            tooltip.add(Component.translatable("tooltip.detonator.no_position").withStyle(ChatFormatting.RED));
        } else {
            int x = nbt.getInt("x");
            int y = nbt.getInt("y");
            int z = nbt.getInt("z");
            tooltip.add(Component.translatable("tooltip.detonator.linked", x, y, z).withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();

        if (player == null) return InteractionResult.PASS;

        CompoundTag nbt = stack.getOrCreateTag();

        if (player.isShiftKeyDown()) {
            // Устанавливаем позицию
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());

            if (!level.isClientSide) {
                player.sendSystemMessage(
                        ChatBuilder.start("[")
                                .color(ChatFormatting.DARK_AQUA)
                                .nextTranslation(this.getDescriptionId() + ".name")
                                .color(ChatFormatting.DARK_AQUA)
                                .next("] ")
                                .color(ChatFormatting.DARK_AQUA)
                                .nextTranslation("chat.detonator.position_set")
                                .color(ChatFormatting.GREEN)
                                .flush()
                );
            }

            // HBM звук
            level.playSound(player, pos, ModSounds.TECH_BOOP.get(), SoundSource.PLAYERS, 2.0F, 1.0F);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag nbt = stack.getTag();

        if (nbt == null || !nbt.contains("x") || !nbt.contains("y") || !nbt.contains("z")) {
            if (!level.isClientSide) {
                player.sendSystemMessage(
                        ChatBuilder.start("[")
                                .color(ChatFormatting.DARK_AQUA)
                                .nextTranslation(this.getDescriptionId() + ".name")
                                .color(ChatFormatting.DARK_AQUA)
                                .next("] ")
                                .color(ChatFormatting.DARK_AQUA)
                                .nextTranslation("chat.detonator.no_position")
                                .color(ChatFormatting.RED)
                                .flush()
                );
            }
            return InteractionResultHolder.pass(stack);
        }

        int x = nbt.getInt("x");
        int y = nbt.getInt("y");
        int z = nbt.getInt("z");
        BlockPos pos = new BlockPos(x, y, z);

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IBomb bomb) {
            // HBM звук
            level.playSound(player, pos, ModSounds.TECH_BLEEP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

            if (!level.isClientSide) {
                BombReturnCode ret = bomb.explode(level, pos);

                player.sendSystemMessage(
                        ChatBuilder.start("[")
                                .color(ChatFormatting.DARK_AQUA)
                                .nextTranslation(this.getDescriptionId() + ".name")
                                .color(ChatFormatting.DARK_AQUA)
                                .next("] ")
                                .color(ChatFormatting.DARK_AQUA)
                                .nextTranslation(ret.getTranslationKey()) // <-- ИСПРАВЛЕНО
                                .color(ret.wasSuccessful() ? ChatFormatting.YELLOW : ChatFormatting.RED)
                                .flush()
                );
            }

            return InteractionResultHolder.success(stack);
        } else {
            if (!level.isClientSide) {
                player.sendSystemMessage(
                        ChatBuilder.start("[")
                                .color(ChatFormatting.DARK_AQUA)
                                .nextTranslation(this.getDescriptionId() + ".name")
                                .color(ChatFormatting.DARK_AQUA)
                                .next("] ")
                                .color(ChatFormatting.DARK_AQUA)
                                .nextTranslation(BombReturnCode.ERROR_NO_BOMB.getTranslationKey()) // <-- ИСПРАВЛЕНО
                                .color(ChatFormatting.RED)
                                .flush()
                );
            }
            return InteractionResultHolder.pass(stack);
        }
    }
}