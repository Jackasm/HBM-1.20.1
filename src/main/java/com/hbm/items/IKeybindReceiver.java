package com.hbm.items;

import com.hbm.handler.HbmKeybinds.EnumKeybind;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IKeybindReceiver {

    /**
     * Проверяет, может ли предмет обработать данное нажатие клавиши
     */
    boolean canHandleKeybind(Player player, ItemStack stack, EnumKeybind key);

    /**
     * Обрабатывает нажатие клавиши
     */
    void handleKeybind(Player player, ItemStack stack, EnumKeybind key, boolean state);

    default void handleKeybindClient(Player player, ItemStack stack, EnumKeybind key, boolean state) {
        // Базовая реализация пустая, можно переопределить при необходимости
    }
}