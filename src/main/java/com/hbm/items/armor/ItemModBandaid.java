package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModBandaid extends ItemArmorMod {

    public ItemModBandaid(Properties properties) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("3% chance for full heal when damaged").withStyle(ChatFormatting.RED));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(Component.literal("  " + stack.getHoverName().getString() + " (3% chance for full heal)")
                .withStyle(ChatFormatting.RED));
    }

    @Override
    public void modDamage(LivingHurtEvent event, ItemStack armor) {
        if (event.getEntity().level().random.nextInt(100) < 3) {
            event.setAmount(0);
            event.getEntity().heal(event.getEntity().getMaxHealth());
        }
    }
}