package com.hbm.items.special;

import com.hbm.items.ModItems;
import com.hbm.util.ItemStackUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ItemKitCustom extends ItemKitNBT {

    public ItemKitCustom(Properties properties) {
        super(properties);
    }

    public static ItemStack create(String name, String lore, int color1, int color2, ItemStack... contents) {
        ItemStack stack = new ItemStack(ModItems.KIT_CUSTOM.get());

        CompoundTag tag = new CompoundTag();
        stack.setTag(tag);

        setColor(stack, color1, 1);
        setColor(stack, color2, 2);

        if (lore != null) {
            ItemStackUtil.addTooltipToStack(stack, lore.split("\\$"));
        }
        stack.setHoverName(Component.literal(ChatFormatting.RESET + name));
        ItemStackUtil.addStacksToNBT(stack, contents);

        return stack;
    }

    public static void setColor(ItemStack stack, int color, int index) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("color" + index, color);
    }

    public static int getColor(ItemStack stack, int index) {
        if (!stack.hasTag()) return 0;
        return Objects.requireNonNull(stack.getTag()).getInt("color" + index);
    }

}