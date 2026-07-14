package com.hbm.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface IControlReceiver {

    boolean hasPermission(Player player);

    void receiveControl(CompoundTag data);
    /* this was the easiest way of doing this without needing to change all 7 quadrillion implementors */
    default void receiveControl(Player player, CompoundTag data) { }
}

