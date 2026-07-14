package com.hbm.items.armor;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

interface IDamageHandler {

    void handleDamage(LivingHurtEvent event, ItemStack stack);
}
