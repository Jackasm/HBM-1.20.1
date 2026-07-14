package com.hbm.hazard.type;

import com.hbm.config.RadiationConfig;
import com.hbm.hazard.modifier.HazardModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HazardTypeExplosive extends HazardTypeBase {

    @Override
    public void onUpdate(@NotNull LivingEntity target, float level, @NotNull ItemStack stack) {
        if (RadiationConfig.disableExplosive.get()) return;

        if (!target.level().isClientSide && target.isOnFire() && !stack.isEmpty()) {
            Level world = target.level();
            stack.shrink(stack.getCount());
            world.explode(null, target.getX(), target.getY() + target.getEyeHeight() - target.getBbHeight() * 0.5,
                    target.getZ(), level, Level.ExplosionInteraction.NONE);
        }
    }

    @Override
    public void updateEntity(@NotNull ItemEntity item, float level) {
        if (RadiationConfig.disableExplosive.get()) return;

        if (item.isOnFire()) {
            Level world = item.level();
            item.discard();
            world.explode(null, item.getX(), item.getY() + item.getBbHeight() * 0.5, item.getZ(),
                    level, Level.ExplosionInteraction.NONE);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHazardInformation(@NotNull Player player, @NotNull List<Component> list, float level,
                                     @NotNull ItemStack stack, @NotNull List<HazardModifier> modifiers) {
        list.add(Component.literal(ChatFormatting.RED + "[")
                .append(Component.translatable("trait.explosive").withStyle(ChatFormatting.RED))
                .append(Component.literal(ChatFormatting.RED + "]")));
    }
}