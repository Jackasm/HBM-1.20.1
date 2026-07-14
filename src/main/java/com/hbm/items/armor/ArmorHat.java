package com.hbm.items.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmorHat extends ArmorModel implements IAttackHandler, IDamageHandler {

    public ArmorHat(Properties properties) {
        super(ModArmorMaterials.ADVANCED_ALLOY, Type.HELMET, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("+2 DT").withStyle(ChatFormatting.BLUE));
    }

    @Override
    public void handleDamage(LivingHurtEvent event, ItemStack stack) {
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        float amount = event.getAmount() - 2F;
        if (amount < 0) amount = 0;
        event.setAmount(amount);
    }

    @Override
    public void handleAttack(LivingAttackEvent event, ItemStack armor) {
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        if (event.getAmount() <= 2F) {
            event.getEntity().level().playSound(null,
                    event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(),
                    SoundEvents.ITEM_BREAK,
                    event.getEntity().getSoundSource(),
                    5.0F, 1.0F + event.getEntity().getRandom().nextFloat() * 0.5F);
            event.setCanceled(true);
        }
    }
}