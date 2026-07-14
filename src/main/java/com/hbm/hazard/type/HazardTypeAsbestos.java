package com.hbm.hazard.type;

import com.hbm.config.RadiationConfig;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ArmorRegistry.HazardClass;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HazardTypeAsbestos extends HazardTypeBase {

    @Override
    public void onUpdate(@NotNull LivingEntity target, float level, @NotNull ItemStack stack) {
        if (RadiationConfig.disableAsbestos.get()) return;

        if (!ArmorRegistry.hasProtection(target, 3, HazardClass.PARTICLE_FINE)) {
            HbmLivingProps.incrementAsbestos(target, (int) Math.min(level, 10));
        } else {
            ArmorUtil.damageGasMaskFilter(target, (int) level);
        }
    }

    @Override
    public void updateEntity(@NotNull ItemEntity item, float level) {
        // No effect on dropped items
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHazardInformation(@NotNull Player player, @NotNull List<Component> list, float level,
                                     @NotNull ItemStack stack, @NotNull List<HazardModifier> modifiers) {
        list.add(Component.literal(ChatFormatting.WHITE + "[")
                .append(Component.translatable("trait.asbestos").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(ChatFormatting.WHITE + "]")));
    }
}