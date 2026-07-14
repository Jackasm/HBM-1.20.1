package com.hbm.items.tool;

import com.hbm.items.ModToolItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModSword  extends SwordItem {

    public ModSword(Tier tier, Properties properties) {
        super(tier, (int) tier.getAttackDamageBonus(), tier.getSpeed(), properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (this == ModToolItems.SAW.get()) {
            tooltip.add(Component.literal("Prepare for your examination!").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.BAT.get()) {
            tooltip.add(Component.literal("Do you like hurting other people?").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.BAT_NAIL.get()) {
            tooltip.add(Component.literal("Or is it a classic?").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.GOLF_CLUB.get()) {
            tooltip.add(Component.literal("Property of Miami Beach Golf Club.").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.PIPE_RUSTY.get()) {
            tooltip.add(Component.literal("Ouch! Ouch! Ouch!").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.PIPE_LEAD.get()) {
            tooltip.add(Component.literal("Manually override anything by smashing it with this pipe.").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.REER_GRAAR.get()) {
            tooltip.add(Component.literal("Call now!").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("555-10-3728-ZX7-INFINITE").withStyle(ChatFormatting.GRAY));
        }
    }
}
