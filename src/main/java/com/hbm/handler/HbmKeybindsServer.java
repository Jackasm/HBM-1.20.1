package com.hbm.handler;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.HbmKeybinds.EnumKeybind;
import com.hbm.items.IKeybindReceiver;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HbmKeybindsServer {

    /** Can't put this in HbmKeybinds because it's littered with clientonly stuff */
    public static void onPressedServer(Player player, EnumKeybind key, boolean state) {

        // EXTPROP HANDLING
        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
        props.setKeyPressed(key, state);

        // ITEM HANDLING
        ItemStack held = player.getMainHandItem();
        if(!held.isEmpty() && held.getItem() instanceof IKeybindReceiver rec) {
            if(rec.canHandleKeybind(player, held, key)) rec.handleKeybind(player, held, key, state);
        }

        // Также проверяем предмет во второй руке
        ItemStack offhand = player.getOffhandItem();
        if(!offhand.isEmpty() && offhand.getItem() instanceof IKeybindReceiver rec) {
            if(rec.canHandleKeybind(player, offhand, key)) rec.handleKeybind(player, offhand, key, state);
        }
    }
}