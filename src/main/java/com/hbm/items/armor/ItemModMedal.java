package com.hbm.items.armor;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModMedal extends ItemArmorMod {

    public ItemModMedal(Properties properties) {
        super(ArmorModHandler.PLATE_ONLY, true, true, false, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.GOLD + "-10 RAD/s"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide) return;

        float rad = HbmLivingProps.getRadiation(entity);
        rad -= 0.5F;
        HbmLivingProps.setRadiation(entity, Math.max(rad, 0));
    }
}