package com.hbm.items.armor;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

interface IAttackHandler {

    void handleAttack(LivingAttackEvent event, ItemStack armor);
}