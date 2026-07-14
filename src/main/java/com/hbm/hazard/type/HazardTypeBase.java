package com.hbm.hazard.type;

import com.hbm.hazard.modifier.HazardModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class HazardTypeBase {


    public abstract void onUpdate(LivingEntity target, float level, ItemStack stack);


    public abstract void updateEntity(ItemEntity item, float level);


    public abstract void addHazardInformation(Player player, List<Component> tooltip, float level, ItemStack stack, List<HazardModifier> modifiers);
}