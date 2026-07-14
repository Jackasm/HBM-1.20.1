package com.hbm.inventory.fluid.trait;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class FT_Gaseous extends FluidTrait {
    @Override
    public void addInfoHidden(List<Component> info) {
        info.add(Component.literal(ChatFormatting.BLUE + "[Gaseous]"));
    }
}