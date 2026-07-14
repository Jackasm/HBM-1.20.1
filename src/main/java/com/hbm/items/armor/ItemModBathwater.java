package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModBathwater extends ItemArmorMod {

    private final boolean isMk2;

    public ItemModBathwater(Properties properties, boolean isMk2) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
        this.isMk2 = isMk2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String color = System.currentTimeMillis() % 1000 < 500
                ? (isMk2 ? ChatFormatting.GREEN.toString() : ChatFormatting.BLUE.toString())
                : (isMk2 ? ChatFormatting.YELLOW.toString() : ChatFormatting.LIGHT_PURPLE.toString());

        tooltip.add(Component.literal(color + "Inflicts poison on the attacker"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public void modDamage(LivingHurtEvent event, ItemStack armor) {
        if (event.getEntity().level().isClientSide) return;

        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (attacker instanceof LivingEntity livingAttacker) {
            if (!isMk2) {
                livingAttacker.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
            } else {
                livingAttacker.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 4));
            }
        }
    }
}