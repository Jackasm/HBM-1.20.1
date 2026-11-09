package com.hbm.items.tool;

import com.hbm.items.ModToolItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModSword  extends SwordItem {

    public ModSword(Tier tier, Properties properties) {
        super(tier, (int) tier.getAttackDamageBonus(), tier.getSpeed(), properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (this == ModToolItems.SAW.get()) {
            tooltip.add(Component.translatable("hbm.text.prepare_for_your_examination").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.BAT.get()) {
            tooltip.add(Component.translatable("hbm.text.do_you_like_hurting_other_people").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.BAT_NAIL.get()) {
            tooltip.add(Component.translatable("hbm.text.or_is_it_a_classic").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.GOLF_CLUB.get()) {
            tooltip.add(Component.translatable("hbm.text.property_of_miami_beach_golf_club").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.PIPE_RUSTY.get()) {
            tooltip.add(Component.translatable("hbm.text.ouch_ouch_ouch").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.PIPE_LEAD.get()) {
            tooltip.add(Component.translatable("hbm.text.manually_override_anything_by_smashing_it_with_t").withStyle(ChatFormatting.GRAY));
        }
        if (this == ModToolItems.REER_GRAAR.get()) {
            tooltip.add(Component.translatable("hbm.text.call_now").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("hbm.text.555_10_3728_zx7_infinite").withStyle(ChatFormatting.GRAY));
        }
    }
}
