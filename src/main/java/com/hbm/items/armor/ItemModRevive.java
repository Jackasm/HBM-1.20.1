package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModRevive extends ItemArmorMod {

    private final int maxRevives;

    public ItemModRevive(Properties properties, int durability) {
        super(ArmorModHandler.EXTRA, false, false, true, false, properties);
        this.maxRevives = durability;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.SCRUMPY.get()) {
            tooltip.add(Component.literal("But how did you survive?").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.literal("I was drunk.").withStyle(ChatFormatting.RED));
        }
        if (this == ModItems.WILD_P.get()) {
            tooltip.add(Component.literal("Explosive ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("Reactive ").withStyle(ChatFormatting.RED))
                    .append(Component.literal("Plot ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal("Armor").withStyle(ChatFormatting.RED)));
        }

        tooltip.add(Component.empty());
        int revivesLeft = getRevivesLeft(stack);
        tooltip.add(Component.literal(revivesLeft + " revives left").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.empty());

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        int revivesLeft = getRevivesLeft(stack);
        list.add(Component.literal("  " + stack.getHoverName().getString() + " (" + revivesLeft + " revives left)")
                .withStyle(ChatFormatting.GOLD));
    }

    private int getRevivesLeft(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    public int getMaxRevives() {
        return maxRevives;
    }
}