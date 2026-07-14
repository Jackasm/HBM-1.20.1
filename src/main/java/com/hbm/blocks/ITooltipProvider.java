package com.hbm.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.List;
//TODO Заменить на встроенный appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
public interface ITooltipProvider {

    @OnlyIn(Dist.CLIENT)
    void addInformation(ItemStack stack, Player player, List<Component> tooltip, boolean advanced);

    @OnlyIn(Dist.CLIENT)
    default void addStandardInfo(ItemStack stack, Player player, List<Component> tooltip, boolean advanced) {
        if (GLFW.glfwGetKey(org.lwjgl.glfw.GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            String[] keys = getDescKeys(stack);
            for (String key : keys) {
                tooltip.add(Component.translatable(key).withStyle(ChatFormatting.YELLOW));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.hold.shift")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
        }
    }

    default String[] getDescKeys(ItemStack stack) {
        return new String[]{stack.getItem().getDescriptionId() + ".desc"};
    }

    default Rarity getRarity(ItemStack stack) {
        return Rarity.COMMON;
    }
}