package com.hbm.hazard.type;

import com.hbm.config.GeneralConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.items.ModItems;
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

public class HazardTypeHot extends HazardTypeBase {

    @Override
    public void onUpdate(@NotNull LivingEntity target, float level, @NotNull ItemStack stack) {
        if (RadiationConfig.disableHot.get()) return;

        boolean reacher = false;

        if (target instanceof Player player && !GeneralConfig.enable528.get()) {
            reacher = player.getInventory().hasAnyMatching(s -> s.getItem() == ModItems.REACHER.get());
        }

        if (!reacher && !target.isInWaterOrRain() && level > 0) {
            target.setRemainingFireTicks((int) Math.ceil(level * 20)); // 20 ticks per second
        }
    }

    @Override
    public void updateEntity(@NotNull ItemEntity item, float level) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHazardInformation(@NotNull Player player, @NotNull List<Component> list, float level,
                                     @NotNull ItemStack stack, @NotNull List<HazardModifier> modifiers) {

        level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);

        if (level > 0) {
            list.add(Component.literal(ChatFormatting.GOLD + "[")
                    .append(Component.translatable("trait.hot").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(ChatFormatting.GOLD + "]")));
        }
    }
}