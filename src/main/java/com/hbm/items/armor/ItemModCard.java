package com.hbm.items.armor;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModCard extends ItemArmorMod {

    public ItemModCard(Properties properties) {
        super(ArmorModHandler.HELMET_ONLY, true, true, false, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.CARD_AOS.get()) {
            tooltip.add(Component.literal("Top of the line!").withStyle(ChatFormatting.RED));
            tooltip.add(Component.literal("Guns now have a 33% chance to not consume ammo.").withStyle(ChatFormatting.RED));
        }
        if (this == ModItems.CARD_QOS.get()) {
            tooltip.add(Component.literal("Power!").withStyle(ChatFormatting.RED));
            tooltip.add(Component.literal("Adds a 33% chance to tank damage with no cap.").withStyle(ChatFormatting.RED));
        }
        tooltip.add(Component.literal(""));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(stack.getHoverName().copy().withStyle(ChatFormatting.RED));
    }

    @Override
    public void modDamage(LivingHurtEvent event, ItemStack armor) {
        if (this == ModItems.CARD_QOS.get() &&
                event.getEntity().getRandom().nextInt(3) == 0 &&
                event.getEntity() instanceof Player player) {

            HbmPlayerProps.plink(player, "random.break", 0.5F, 1.0F + event.getEntity().getRandom().nextFloat() * 0.5F);
            event.setAmount(0);
            event.setCanceled(true);
        }
    }
}