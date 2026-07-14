package com.hbm.util;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import net.minecraft.world.item.ItemStack;
import java.util.List;


/**
 * General handler for compatibility with other mods.
 */
public class CompatHelper {

    /**
     * Used for converting a steam type to an integer (compression levels).
     * @param type Steam type.
     * @return Object[] array containing an int with the "compression level"
     */
    public static Object[] steamTypeToInt(FluidTypeHBM type) {
        final int typeId = Fluids.getID(type);
        if(typeId == Fluids.getID(Fluids.HOTSTEAM.get())) {
            return new Object[]{1};
        } else if(typeId == Fluids.getID(Fluids.SUPERHOTSTEAM.get())) {
            return new Object[]{2};
        } else if(typeId == Fluids.getID(Fluids.ULTRAHOTSTEAM.get())) {
            return new Object[]{3};
        }
        return new Object[] {0};
    }

    /**
     * Used for converting a compression level to a steam type.
     * @param arg Steam compression level.
     * @return FluidType of the steam type based on the compression level.
     */
    public static FluidTypeHBM intToSteamType(int arg) {
        switch(arg) {
            case(1):
                return Fluids.HOTSTEAM.get();
            case(2):
                return Fluids.SUPERHOTSTEAM.get();
            case(3):
                return Fluids.ULTRAHOTSTEAM.get();
            default:
                return Fluids.STEAM.get();
        }
    }

    // Null component name, для обратной совместимости если где-то используется
    public static final String nullComponent = "ntm_null";

    /**
     * Simple enum for mapping colors.
     */
    public enum Colors {
        BLACK(0x444444, "dyeBlack"),
        RED(0xB3312C, "dyeRed"),
        GREEN(0x339911, "dyeGreen"),
        BROWN(0x51301A, "dyeBrown"),
        BLUE(0x6666FF, "dyeBlue"),
        PURPLE(0x7B2FBE, "dyePurple"),
        CYAN(0x66FFFF, "dyeCyan"),
        LIGHTGRAY(0xABABAB, "dyeLightGray"),
        GRAY(0x666666, "dyeGray"),
        PINK(0xD88198, "dyePink"),
        LIME(0x66FF66, "dyeLime"),
        YELLOW(0xFFFF66, "dyeYellow"),
        LIGHTBLUE(0xAAAAFF, "dyeLightBlue"),
        MAGENTA(0xC354CD, "dyeMagenta"),
        ORANGE(0xEB8844, "dyeOrange"),
        WHITE(0xF0F0F0, "dyeWhite"),
        NONE(0x0, "");

        private final int color;
        private final String dictName;

        Colors(int color, String dictName) {
            this.color = color;
            this.dictName = dictName;
        }

        public int getColor() {
            return color;
        }

        public static Colors fromInt(int intColor) {
            for (Colors iColor : Colors.values()) {
                if (intColor == iColor.getColor())
                    return iColor;
            }
            return Colors.NONE;
        }

        public static Colors fromDye(ItemStack stack) {
            List<String> oreNames = ItemStackUtil.getTagNames(stack);

            for(String dict : oreNames) {
                if(!(dict.length() > 3) || !dict.startsWith("dye"))
                    continue;

                for (Colors color : Colors.values()) {
                    if(!color.dictName.equals(dict))
                        continue;

                    return color;
                }
            }

            return Colors.NONE;
        }
    }
}