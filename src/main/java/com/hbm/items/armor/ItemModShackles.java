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

public class ItemModShackles extends ItemArmorMod {

    public ItemModShackles(Properties properties) {
        super(ArmorModHandler.EXTRA, false, false, true, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.RED + "You will speak when I ask you to."));
        tooltip.add(Component.literal(ChatFormatting.RED + "You will eat when I tell you to."));
        tooltip.add(Component.literal(ChatFormatting.RED + "" + ChatFormatting.BOLD + "You will die when I allow you to."));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal(ChatFormatting.GOLD + "∞ revives left"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }
}