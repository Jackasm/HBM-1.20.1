package com.hbm.items.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRenderableMod {

    @OnlyIn(Dist.CLIENT)
    HumanoidModel<?> getRenderModel(ItemStack mod, LivingEntity entity);

    @OnlyIn(Dist.CLIENT)
    default net.minecraft.resources.ResourceLocation getRenderTexture(ItemStack mod, LivingEntity entity) {
        return null;
    }
}
