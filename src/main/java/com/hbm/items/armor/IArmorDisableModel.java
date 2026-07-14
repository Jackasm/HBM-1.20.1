package com.hbm.items.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IArmorDisableModel {

    boolean disablesPart(Player player, ItemStack stack, EnumPlayerPart part);

    enum EnumPlayerPart {
        HEAD,
        HAT,
        BODY,
        LEFT_ARM,
        RIGHT_ARM,
        LEFT_LEG,
        RIGHT_LEG
    }
}
