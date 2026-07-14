package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModItems;
import com.hbm.util.ModDamageSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModCharm extends ItemArmorMod {

    public ItemModCharm() {
        super(ArmorModHandler.HELMET_ONLY, true, true, false, false, new Properties());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("item.mod_charm.blessed").withStyle(ChatFormatting.AQUA));

        if (this == ModItems.PROTECTION_CHARM.get()) {
            list.add(Component.translatable("item.mod_charm.protection.desc1").withStyle(ChatFormatting.AQUA));
            list.add(Component.translatable("item.mod_charm.protection.desc2").withStyle(ChatFormatting.AQUA));
            list.add(Component.translatable("item.mod_charm.protection.desc3").withStyle(ChatFormatting.AQUA));
        }
        if (this == ModItems.METEOR_CHARM.get()) {
            list.add(Component.translatable("item.mod_charm.meteor.desc1").withStyle(ChatFormatting.AQUA));
            list.add(Component.translatable("item.mod_charm.meteor.desc2").withStyle(ChatFormatting.AQUA));
        }

        super.appendHoverText(stack, level, list, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(Component.literal("  ").append(stack.getHoverName()).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void modDamage(LivingHurtEvent event, ItemStack armor) {
        if (event.getSource().is(ModDamageSource.BROADCAST)) {
            if (this == ModItems.PROTECTION_CHARM.get()) {
                event.setAmount(event.getAmount() * 0.5F);
            }
            if (this == ModItems.METEOR_CHARM.get()) {
                event.setAmount(0.0F);
            }
        }
    }
}