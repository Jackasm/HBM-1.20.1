package com.hbm.hazard.type;

import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.util.ContaminationUtil;
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

public class HazardTypeDigamma extends HazardTypeBase {

    @Override
    public void onUpdate(@NotNull LivingEntity target, float level, @NotNull ItemStack stack) {
        ContaminationUtil.applyDigammaData(target, level / 20.0F);
    }

    @Override
    public void updateEntity(@NotNull ItemEntity item, float level) {
        // No effect on dropped items
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHazardInformation(@NotNull Player player, @NotNull List<Component> list, float level,
                                     @NotNull ItemStack stack, @NotNull List<HazardModifier> modifiers) {

        level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);

        float d = (float) (Math.floor(level * 10000.0F)) / 10.0F;

        list.add(Component.literal(ChatFormatting.RED + "[")
                .append(Component.translatable("trait.digamma").withStyle(ChatFormatting.RED))
                .append(Component.literal(ChatFormatting.RED + "]")));
        list.add(Component.literal(ChatFormatting.DARK_RED.toString() + d + "mDRX/s"));

        if (stack.getCount() > 1) {
            float stackValue = (float) (Math.floor(level * 10000.0F * stack.getCount()) / 10.0F);
            list.add(Component.literal(ChatFormatting.DARK_RED + "Stack: " + stackValue + "mDRX/s"));
        }
    }
}