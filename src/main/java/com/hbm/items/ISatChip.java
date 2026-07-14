package com.hbm.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ISatChip {

    static int getFreq(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ISatChip) {
            return ((ISatChip) stack.getItem()).getFreqFromStack(stack);
        }
        return 0;
    }

    static void setFreq(ItemStack stack, int freq) {
        if (stack != null && stack.getItem() instanceof ISatChip) {
            ((ISatChip) stack.getItem()).setFreqToStack(stack, freq);
        }
    }

    default int getFreqFromStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
            return 0;
        }
        return tag.getInt("freq");
    }

    default void setFreqToStack(ItemStack stack, int freq) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        tag.putInt("freq", freq);
    }
}