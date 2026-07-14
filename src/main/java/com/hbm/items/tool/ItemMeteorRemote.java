package com.hbm.items.tool;

import com.hbm.handler.BossSpawnHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ItemMeteorRemote extends Item {

    private final Random rand = new Random();

    public ItemMeteorRemote() {
        super(new Properties().stacksTo(1).durability(2));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("item.meteor_remote.desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, flag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Повреждаем предмет
        if (!level.isClientSide) {
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }

        if (!level.isClientSide) {
            BossSpawnHandler.spawnMeteorAtPlayer(player, false);
            player.sendSystemMessage(Component.translatable("item.meteor_remote.summon").withStyle(ChatFormatting.RED));
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_PLING.get(), SoundSource.PLAYERS,
                1.0F, 1.0F);

        player.swing(hand);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }
}