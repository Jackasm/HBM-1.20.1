package com.hbm.util;

import org.jetbrains.annotations.NotNull;

public class HBMEnums {


    public enum CreativeTabRegistry {
        PARTS_TAB,
        BLOCK_TAB,
        CONSUMABLE_TAB,
        CONTROL_TAB,
        MACHINE_TAB,
        MISSILE_TAB,
        NUKE_TAB,
        TEMPLATE_TAB,
        WEAPON_TAB
    }

    public enum EnumPages {
        PAGE1, PAGE2, PAGE3, PAGE4, PAGE5, PAGE6, PAGE7, PAGE8
    }

    public enum EnumSymbol {
        NONE(0, 0),
        RADIATION(195, 2),
        NOWATER(195, 63),
        ACID(195, 124),
        ASPHYXIANT(195, 185),
        CROYGENIC(134, 185),
        ANTIMATTER(73, 185),
        OXIDIZER(12, 185);

        public final int x;
        public final int y;

        EnumSymbol(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public enum LightType implements net.minecraft.util.StringRepresentable {
        INCANDESCENT("incandescent"),
        FLUORESCENT("fluorescent"),
        HALOGEN("halogen");

        private final String name;

        LightType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public enum EnumCasingType {
        SMALL, LARGE, SMALL_STEEL, LARGE_STEEL, SHOTSHELL, BUCKSHOT, BUCKSHOT_ADVANCED
    }

    public enum EnumChunkType {
        RARE,
        MALACHITE,
        CRYOLITE,
        MOONSTONE
    }

    public enum ConnectionPriority {
        LOWEST,
        LOW,
        NORMAL,
        HIGH,
        HIGHEST
    }

    public enum EnumAshType {
        WOOD,
        COAL,
        MISC,
        FLY,
        SOOT,
        FULLERENE
    }

    public enum EnumCokeType {
        COAL,
        LIGNITE,
        PETROLEUM
    }

    public enum EnumBriquetteType {
        COAL,
        LIGNITE,
        WOOD
    }

    public enum ScrapType {
        //GENERAL BOARD
        BOARD_BLANK,
        BOARD_TRANSISTOR,
        BOARD_CONVERTER,

        //CHIPSET
        BRIDGE_NORTH,
        BRIDGE_SOUTH,
        BRIDGE_IO,
        BRIDGE_BUS,
        BRIDGE_CHIPSET,
        BRIDGE_CMOS,
        BRIDGE_BIOS,

        //CPU
        CPU_REGISTER,
        CPU_CLOCK,
        CPU_LOGIC,
        CPU_CACHE,
        CPU_EXT,
        CPU_SOCKET,

        //RAM
        MEM_SOCKET,
        MEM_16K_A,
        MEM_16K_B,
        MEM_16K_C,
        MEM_16K_D,

        //EXTENSION CARD
        CARD_BOARD,
        CARD_PROCESSOR
    }
}