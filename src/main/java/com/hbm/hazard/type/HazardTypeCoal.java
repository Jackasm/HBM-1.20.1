package com.hbm.hazard.type;


import com.hbm.extprop.HbmLivingProps;
import com.hbm.hazard.modifier.HazardModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HazardTypeCoal extends HazardTypeBase {

    @Override
    public void onUpdate(LivingEntity target, float level, ItemStack stack) {
        if (target.level().isClientSide()) return;
        HbmLivingProps.incrementBlackLung(target, (int) Math.min(level * stack.getCount(), 10));
    }

    @Override
    public void updateEntity(ItemEntity item, float level) {
    }

    @Override
    public void addHazardInformation(Player player, List<Component> list, float level,
                                     ItemStack stack, List<HazardModifier> modifiers) {
        list.add(Component.literal(ChatFormatting.DARK_GRAY + "[")
                .append(Component.translatable("trait.coal").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(ChatFormatting.DARK_GRAY + "]")));
    }
}