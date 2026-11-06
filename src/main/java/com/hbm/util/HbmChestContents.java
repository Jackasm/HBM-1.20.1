package com.hbm.util;

import com.hbm.items.special.ItemBookLore;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public class HbmChestContents {

    /** ITEMBOOKLORE SHIT */
    public static ItemStack generateOfficeBook(RandomSource rand) {
        String key;
        int pages;
        switch (rand.nextInt(5)) {
            case 0 -> { key = "resignation_note"; pages = 3; }
            case 1 -> { key = "memo_stocks"; pages = 1; }
            case 2 -> { key = "memo_schrab_gsa"; pages = 2; }
            case 3 -> { key = "memo_schrab_rd"; pages = 4; }
            case 4 -> { key = "memo_schrab_nuke"; pages = 3; }
            default -> { return ItemStack.EMPTY; }
        }
        return ItemBookLore.createBook(key, pages, 0x6BC8FF, 0x0A0A0A);
    }

    public static ItemStack generateLabBook(RandomSource rand) {
        String key;
        int pages;
        switch (rand.nextInt(5)) {
            case 0 -> { key = "bf_bomb_1"; pages = 4; }
            case 1 -> { key = "bf_bomb_2"; pages = 6; }
            case 2 -> { key = "bf_bomb_3"; pages = 6; }
            case 3 -> { key = "bf_bomb_4"; pages = 5; }
            case 4 -> { key = "bf_bomb_5"; pages = 9; }
            default -> { return ItemStack.EMPTY; }
        }
        return ItemBookLore.createBook(key, pages, 0x1E1E1E, 0x46EA44);
    }
}