package com.hbm.items.machine;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemArcElectrode extends Item {

    private final int maxDurability;

    public ItemArcElectrode(Properties properties, int durability) {
        super(properties.stacksTo(1));
        this.maxDurability = durability;
    }

    public int getDurability(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return tag.getInt("durability");
    }

    public boolean damage(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int durability = tag.getInt("durability");
        durability++;
        tag.putInt("durability", durability);
        return durability >= getMaxDurability();
    }

    public int getMaxDurability() {
        return this.maxDurability;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getDurability(stack) > 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13.0F - (getDurability(stack) * 13.0F / getMaxDurability()));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00AA00;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        int durability = getDurability(stack);
        int maxDurability = getMaxDurability();

        tooltip.add(Component.translatable("item.arc_electrode.durability")
                .append(": ")
                .append(Component.literal(durability + "/" + maxDurability))
                .withStyle(ChatFormatting.GRAY));
    }
}