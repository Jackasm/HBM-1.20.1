package com.hbm.items.special;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemHot extends Item {

    public static int maxHeat;

    public ItemHot(Properties properties, int heat) {
        super(properties);
        maxHeat = heat;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide && stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            int h = Objects.requireNonNull(tag).getInt("heat");

            if (h > 0) {
                tag.putInt("heat", h - 1);
            } else {
                stack.setTag(null);
            }
        }
    }

    public static int getMaxHeat(ItemStack stack) {
        return maxHeat;
    }

    public static ItemStack heatUp(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemHot)) return stack;

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        tag.putInt("heat", getMaxHeat(stack));
        return stack;
    }

    public static ItemStack heatUp(ItemStack stack, double d) {
        if (!(stack.getItem() instanceof ItemHot)) return stack;

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        tag.putInt("heat", (int) (d * getMaxHeat(stack)));
        return stack;
    }

    public static double getHeat(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemHot)) return 0;
        if (!stack.hasTag()) return 0;

        int h = Objects.requireNonNull(stack.getTag()).getInt("heat");
        return (double) h / (double) getMaxHeat(stack);
    }
}