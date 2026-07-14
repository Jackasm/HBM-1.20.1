package com.hbm.hazard.type;

import com.hbm.config.RadiationConfig;
import com.hbm.hazard.modifier.HazardModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HazardTypeHydroactive extends HazardTypeBase {

    @Override
    public void onUpdate(@NotNull LivingEntity target, float level, @NotNull ItemStack stack) {
        if (RadiationConfig.disableHydro.get()) return;

        if (target.isInWaterOrRain() && !stack.isEmpty()) {
            Level world = target.level();
            stack.shrink(stack.getCount());
            world.explode(null,
                    target.getX(),
                    target.getY() + target.getEyeHeight() - target.getBbHeight() * 0.5,
                    target.getZ(),
                    level, Level.ExplosionInteraction.NONE);
        }
    }

    @Override
    public void updateEntity(@NotNull ItemEntity item, float level) {
        if (RadiationConfig.disableHydro.get()) return;

        Level world = item.level();
        BlockPos pos = item.blockPosition();
        boolean inWater = item.isInWaterOrRain() ||
                world.getFluidState(pos).getType() == Fluids.WATER ||
                world.getFluidState(pos.below()).getType() == Fluids.WATER;

        if (inWater) {
            item.discard();
            world.explode(null,
                    item.getX(),
                    item.getY() + item.getBbHeight() * 0.5,
                    item.getZ(),
                    level, Level.ExplosionInteraction.NONE);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHazardInformation(@NotNull Player player, @NotNull List<Component> list, float level,
                                     @NotNull ItemStack stack, @NotNull List<HazardModifier> modifiers) {
        list.add(Component.literal(ChatFormatting.RED + "[")
                .append(Component.translatable("trait.hydro").withStyle(ChatFormatting.RED))
                .append(Component.literal(ChatFormatting.RED + "]")));
    }
}