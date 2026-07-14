package com.hbm.items.tool;

import java.util.List;

import com.hbm.items.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemKeyPin extends Item {

    public ItemKeyPin(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int pins = getPins(stack);
        if (pins != 0) {
            tooltip.add(Component.translatable("Pin configuration: " + pins));
        } else {
            tooltip.add(Component.translatable("Pins not set!"));
        }

        if (this == ModItems.KEY_FAKE.get()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("Pins can neither be changed, nor copied."));
        }
    }

    public static int getPins(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return tag.getInt("pins");
    }

    public static void setPins(ItemStack stack, int pins) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("pins", pins);
    }

    public boolean canTransfer() {
        return this != ModItems.KEY_FAKE.get();
    }
}