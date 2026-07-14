package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModTwoKick extends ItemArmorMod {

    public ItemModTwoKick(Properties properties) {
        super(ArmorModHandler.SERVOS, false, true, false, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.ITALIC + "\"I've had worse\""));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Punches fire 12 gauge shells"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }
}