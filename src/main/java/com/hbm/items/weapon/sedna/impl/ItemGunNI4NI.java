package com.hbm.items.weapon.sedna.impl;

import java.util.List;

import com.hbm.items.ICustomizable;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;

import com.hbm.util.ChatBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.hbm.sound.ModSounds.TECH_BLEEP;

public class ItemGunNI4NI extends GunItem implements ICustomizable {

    public ItemGunNI4NI(WeaponQuality quality, GunConfig... cfg) {
        super(quality, cfg);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean isHeld) {
        super.inventoryTick(stack, world, entity, slot, isHeld);

        if(!world.isClientSide && entity instanceof Player player) {

            int maxCoin = 4;
            if(WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.NI4NI_NICKEL)) maxCoin += 2;
            if(WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.NI4NI_DOUBLOONS)) maxCoin += 2;

            if(getCoinCount(stack) < maxCoin) {
                setCoinCharge(stack, getCoinCharge(stack) + 1);

                if(getCoinCharge(stack) >= 80) {
                    setCoinCharge(stack, 0);
                    int newCount = getCoinCount(stack) + 1;
                    setCoinCount(stack, newCount);

                    if(isHeld) {
                        world.playSound(null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                TECH_BLEEP.get(),  // Используем кастомный звук
                                SoundSource.PLAYERS,
                                1.0F,
                                1F + newCount / (float) maxCoin);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, List<Component> list, @NotNull TooltipFlag ext) {
        list.add(Component.literal("Now, don't get the wrong idea."));
        list.add(Component.literal("I ")
                .append(Component.literal("fucking hate ").withStyle(ChatFormatting.RED))
                .append(Component.literal("this game.").withStyle(ChatFormatting.GRAY)));
        list.add(Component.literal("I didn't do this for you, I did it for sea."));
        super.appendHoverText(stack, world, list, ext);
    }

    @Override
    public void customize(Player player, ItemStack stack, String... args) {

        if(args.length == 0) {
            resetColors(stack);
            player.sendSystemMessage(ChatBuilder.start("Colors reset!").color(ChatFormatting.GREEN).build());
            return;
        }

        if(args.length != 3) {
            resetColors(stack);
            player.sendSystemMessage(ChatBuilder.start("Requires three hexadecimal colors!").color(ChatFormatting.RED).build());
            return;
        }

        try {
            int dark = Integer.parseInt(args[0], 16);
            int light = Integer.parseInt(args[1], 16);
            int grip = Integer.parseInt(args[2], 16);

            if(dark < 0 || dark > 0xffffff || light < 0 || light > 0xffffff || grip < 0 || grip > 0xffffff) {
                player.sendSystemMessage(ChatBuilder.start("Colors must range from 0 to FFFFFF!").color(ChatFormatting.RED).build());
                return;
            }

            setColors(stack, dark, light, grip);
            player.sendSystemMessage(ChatBuilder.start("Colors set!").color(ChatFormatting.GREEN).build());

        } catch(Throwable ex) {
            player.sendSystemMessage(ChatBuilder.start(ex.getLocalizedMessage()).color(ChatFormatting.RED).build());
        }
    }

    public static void resetColors(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag != null) {
            tag.remove("colors");
            if(tag.isEmpty()) {
                stack.setTag(null);
            }
        }
    }

    public static void setColors(ItemStack stack, int dark, int light, int grip) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("colors", new IntArrayTag(new int[] {dark, light, grip}));
    }

    @Nullable
    public static int[] getColors(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null || !tag.contains("colors")) return null;

        int[] colors = tag.getIntArray("colors");
        if(colors.length != 3) return null;
        return colors;
    }

    public static final String KEY_COIN_COUNT = "coincount";
    public static final String KEY_COIN_CHARGE = "coincharge";

    public static int getCoinCount(ItemStack stack) {
        return getValueInt(stack, KEY_COIN_COUNT);
    }

    public static void setCoinCount(ItemStack stack, int value) {
        setValueInt(stack, KEY_COIN_COUNT, value);
    }

    public static int getCoinCharge(ItemStack stack) {
        return getValueInt(stack, KEY_COIN_CHARGE);
    }

    public static void setCoinCharge(ItemStack stack, int value) {
        setValueInt(stack, KEY_COIN_CHARGE, value);
    }

    // Вспомогательные методы для работы с NBT
    private static int getValueInt(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        if(tag != null && tag.contains(key)) {
            return tag.getInt(key);
        }
        return 0;
    }

    private static void setValueInt(ItemStack stack, String key, int value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(key, value);
    }
}