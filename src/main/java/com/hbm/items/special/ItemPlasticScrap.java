package com.hbm.items.special;

import com.hbm.items.ItemEnumMulti;

public class ItemPlasticScrap extends ItemEnumMulti {

    public enum ScrapType {
        BOARD_BLANK,
        BOARD_TRANSISTOR,
        BOARD_CONVERTER,
        BRIDGE_NORTH,
        BRIDGE_SOUTH,
        BRIDGE_IO,
        BRIDGE_BUS,
        BRIDGE_CHIPSET,
        BRIDGE_CMOS,
        BRIDGE_BIOS,
        CPU_REGISTER,
        CPU_CLOCK,
        CPU_LOGIC,
        CPU_CACHE,
        CPU_EXT,
        CPU_SOCKET,
        MEM_SOCKET,
        MEM_16K_A,
        MEM_16K_B,
        MEM_16K_C,
        MEM_16K_D,
        CARD_BOARD,
        CARD_PROCESSOR
    }

    public ItemPlasticScrap(Properties properties) {
        super(properties, ScrapType.class, true, 0);
        // Не даём CreativeTab, чтобы не появлялся в Creative меню
    }
}