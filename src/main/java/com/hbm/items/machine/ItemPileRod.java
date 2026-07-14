package com.hbm.items.machine;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPileRod extends Item {

    public ItemPileRod(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        // Основное описание
        Component defaultComp = Component.translatable("desc.item.pileRod");
        String defaultStr = defaultComp.getString();
        if (defaultStr != null && !defaultStr.isEmpty()) {
            String[] defaultLocs = defaultStr.split("\\$");
            for (String loc : defaultLocs) {
                list.add(Component.literal(loc).withStyle(ChatFormatting.GRAY));
            }
        }

        // Дополнительное описание для конкретного стержня
        String descKey = this.getDescriptionId() + ".desc";
        Component descComp = Component.translatable(descKey);
        String descStr = descComp.getString();
        if (descStr != null && !descStr.isEmpty()) {
            String[] descLocs = descStr.split("\\$");
            for (String loc : descLocs) {
                list.add(Component.literal(loc).withStyle(ChatFormatting.GOLD));
            }
        }

        super.appendHoverText(stack, level, list, flag);
    }
}