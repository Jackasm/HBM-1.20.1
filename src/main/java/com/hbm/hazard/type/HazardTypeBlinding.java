package com.hbm.hazard.type;

import com.hbm.config.RadiationConfig;
import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HazardTypeBlinding extends HazardTypeBase {

    @Override
    public void onUpdate(@NotNull LivingEntity target, float level, @NotNull ItemStack stack) {
        if (RadiationConfig.disableBlinding.get()) return;

        if (!ArmorRegistry.hasProtection(target, 3, HazardClass.LIGHT)) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int) Math.ceil(level * 20), 0));
        }
    }

    @Override
    public void updateEntity(@NotNull ItemEntity item, float level) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHazardInformation(@NotNull Player player, @NotNull List<Component> list, float level,
                                     @NotNull ItemStack stack, @NotNull List<HazardModifier> modifiers) {
        list.add(Component.literal(ChatFormatting.DARK_AQUA + "[")
                .append(Component.translatable("trait.blinding").withStyle(ChatFormatting.DARK_AQUA))
                .append(Component.literal(ChatFormatting.DARK_AQUA + "]")));
    }
}