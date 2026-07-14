package com.hbm.items.machine;

import com.hbm.items.ISatChip;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemSatChip extends Item implements ISatChip {

    public ItemSatChip(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("satchip.frequency")
                .append(": ")
                .append(Component.literal(String.valueOf(getFreqFromStack(stack))))
                .withStyle(ChatFormatting.GRAY));

        if (this == ModItems.SAT_FOEQ.get()) {
            tooltip.add(Component.translatable("satchip.foeq").withStyle(ChatFormatting.GRAY));
        }

        if (this == ModItems.SAT_GERALD.get()) {
            String[] lines = Component.translatable("satchip.gerald.desc").getString().split("\\n");
            for (String line : lines) {
                tooltip.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
            }
        }

        if (this == ModItems.SAT_LASER.get()) {
            tooltip.add(Component.translatable("satchip.laser").withStyle(ChatFormatting.GRAY));
        }

        if (this == ModItems.SAT_MAPPER.get()) {
            tooltip.add(Component.translatable("satchip.mapper").withStyle(ChatFormatting.GRAY));
        }

        if (this == ModItems.SAT_MINER.get()) {
            tooltip.add(Component.translatable("satchip.miner").withStyle(ChatFormatting.GRAY));
        }

        if (this == ModItems.SAT_LUNAR_MINER.get()) {
            tooltip.add(Component.translatable("satchip.lunar_miner").withStyle(ChatFormatting.GRAY));
        }

        if (this == ModItems.SAT_RADAR.get()) {
            tooltip.add(Component.translatable("satchip.radar").withStyle(ChatFormatting.GRAY));
        }

        if (this == ModItems.SAT_RESONATOR.get()) {
            tooltip.add(Component.translatable("satchip.resonator").withStyle(ChatFormatting.GRAY));
        }

        if (this == ModItems.SAT_SCANNER.get()) {
            tooltip.add(Component.translatable("satchip.scanner").withStyle(ChatFormatting.GRAY));
        }
    }
}