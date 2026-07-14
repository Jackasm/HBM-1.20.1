package com.hbm.hazard.type;

import com.hbm.config.GeneralConfig;
import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.items.ModItems;
import com.hbm.util.BobMathUtil;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
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

public class HazardTypeRadiation extends HazardTypeBase {

    private static final java.text.DecimalFormat RAD_FORMAT = new java.text.DecimalFormat("#.###");

    static {
        RAD_FORMAT.setMinimumFractionDigits(1);
    }

    @Override
    public void onUpdate(@NotNull LivingEntity target, float level, @NotNull ItemStack stack) {
        boolean reacher = false;

        if (target instanceof Player player) {
            reacher = player.getInventory().hasAnyMatching(s -> s.getItem() == ModItems.REACHER.get());
        }

        level *= stack.getCount();

        if (level > 0) {
            float rad = level / 20.0F;

            if (GeneralConfig.enable528.get() && reacher) {
                rad = rad / 49.0F; // More realistic function for 528: x / distance^2
            } else if (reacher) {
                rad = (float) BobMathUtil.squirt(rad); // Reworked radiation function: sqrt(x+1/(x+2)^2)-1/(x+2)
            }

            ContaminationUtil.contaminate(target, HazardType.RADIATION, ContaminationType.CREATIVE, rad);
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

        level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);

        if (level < 1e-5) return;

        list.add(Component.literal(ChatFormatting.GREEN + "[")
                .append(Component.translatable("trait.radioactive").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(ChatFormatting.GREEN + "]")));

        String rad = RAD_FORMAT.format(level);
        list.add(Component.literal(ChatFormatting.YELLOW + rad + "RAD/s"));

        if (stack.getCount() > 1) {
            float stackRad = level * stack.getCount();
            String stackRadStr = RAD_FORMAT.format(stackRad);
            list.add(Component.literal(ChatFormatting.YELLOW + "Stack: " + stackRadStr + "RAD/s"));
        }
    }
}