package com.hbm.items;

import com.hbm.network.PacketDispatcher;

import com.hbm.network.client.HbmAnimationPacket;
import com.hbm.render.anim.BusAnimation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IAnimatedItem<T extends Enum<?>> {

    /** Fetch the animation for a given type */
    BusAnimation getAnimation(T type, ItemStack stack);

    /** Should a player holding this item aim it like a gun/bow? */
    boolean shouldPlayerModelAim(ItemStack stack);

    // Runtime erasure means we have to explicitly give the class a second time :(
    Class<T> getEnum();

    // Run a specified animation
    default void playAnimation(Player player, T type) {
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDispatcher.sendTo(new HbmAnimationPacket(type.ordinal(), 0, 0), serverPlayer);
        }
    }
}