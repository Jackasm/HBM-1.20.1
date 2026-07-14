package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModMorningGlory extends ItemArmorMod {

    public ItemModMorningGlory(Properties properties) {
        super(ArmorModHandler.EXTRA, true, true, true, true,properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.LIGHT_PURPLE + "5% chance to apply resistance when hit, wither immunity"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public void modDamage(LivingHurtEvent event, ItemStack armor) {
        if (!event.getEntity().level().isClientSide && event.getEntity().level().random.nextInt(20) == 0) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4));
        }
    }

    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!entity.level().isClientSide && entity.hasEffect(MobEffects.WITHER)) {
            entity.removeEffect(MobEffects.WITHER);
        }
    }
}