package com.hbm.items.machine;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemTurretBiometry extends Item {

    public ItemTurretBiometry(Properties properties) {
        super(properties);
    }

    public static Properties createProperties() {
        return new Properties()
                .stacksTo(1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String[] names = getNames(stack);
        if (names != null) {
            for (String name : names) {
                tooltip.add(Component.literal(name));
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            addName(stack, player.getGameProfile().getName());
            player.sendSystemMessage(Component.literal("Added player data!"));
        }

        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

        player.swing(hand);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static String[] getNames(ItemStack stack) {
        if (!stack.hasTag()) {
            return null;
        }

        int count = Objects.requireNonNull(stack.getTag()).getInt("playercount");
        if (count == 0) return null;

        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = stack.getTag().getString("player_" + i);
        }
        return names;
    }

    public static void addName(ItemStack stack, String name) {
        if (!stack.hasTag()) {
            stack.setTag(new net.minecraft.nbt.CompoundTag());
        }

        String[] existing = getNames(stack);
        if (existing != null && Arrays.asList(existing).contains(name)) {
            return;
        }

        int count = existing != null ? existing.length : 0;
        Objects.requireNonNull(stack.getTag()).putInt("playercount", count + 1);
        stack.getTag().putString("player_" + count, name);
    }

    public static void clearNames(ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new net.minecraft.nbt.CompoundTag());
        }
        Objects.requireNonNull(stack.getTag()).putInt("playercount", 0);
    }
}