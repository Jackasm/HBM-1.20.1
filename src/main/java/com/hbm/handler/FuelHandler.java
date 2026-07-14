package com.hbm.handler;

import com.hbm.blocks.ModBlocks;

import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

import java.util.HashMap;
import java.util.Objects;

public class FuelHandler {

    private static final HashMap<ComparableStack, Integer> burnCache = new HashMap<>();

    public static int getBurnTimeFromCache(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;

        ComparableStack comp = new ComparableStack(stack).makeSingular();

        if (burnCache.containsKey(comp)) {
            return burnCache.get(comp);
        }

        // В 1.20.1 используем ForgeHooks для получения времени горения
        int burnTime = ForgeHooks.getBurnTime(stack, null);
        burnCache.put(comp, burnTime);

        return burnTime;
    }

    public static int getBurnTime(ItemStack fuel) {
        if (fuel == null || fuel.isEmpty()) return 0;

        int single = 200;

        if (fuel.getItem() == ModItems.SOLID_FUEL.get()) return single * 16;
        if (fuel.getItem() == ModItems.SOLID_FUEL_PRESTO.get()) return single * 40;
        if (fuel.getItem() == ModItems.SOLID_FUEL_PRESTO_TRIPLET.get()) return single * 200;
        if (fuel.getItem() == ModItems.SOLID_FUEL_BF.get()) return single * 160;
        if (fuel.getItem() == ModItems.SOLID_FUEL_PRESTO_BF.get()) return single * 400;
        if (fuel.getItem() == ModItems.SOLID_FUEL_PRESTO_TRIPLET_BF.get()) return single * 2000;
        if (fuel.getItem() == ModItems.ROCKET_FUEL.get()) return single * 32;

        if (fuel.getItem() == ModItems.BIOMASS.get()) return single * 2;
        if (fuel.getItem() == ModItems.BIOMASS_COMPRESSED.get()) return single * 4;
        if (fuel.getItem() == ModItems.POWDER_COAL.get()) return single * 8;
        if (fuel.getItem() == ModItems.SCRAP.get()) return single / 4;
        if (fuel.getItem() == ModItems.DUST.get()) return single / 8;
        if (fuel.getItem() == ModBlocks.BLOCK_SCRAP.get().asItem()) return single * 2;
        if (fuel.getItem() == ModItems.POWDER_FIRE.get()) return 6400;
        if (fuel.getItem() == ModItems.LIGNITE.get()) return 1200;
        if (fuel.getItem() == ModItems.POWDER_LIGNITE.get()) return 1200;
        if (fuel.getItem() == ModItems.COKE.get()) return single * 16;
        if (fuel.getItem() == ModBlocks.BLOCK_COKE.get().asItem()) return single * 160;
        if (fuel.getItem() == ModItems.BOOK_GUIDE.get()) return single;
        if (fuel.getItem() == ModItems.COAL_INFERNAL.get()) return 4800;
        if (fuel.getItem() == ModItems.CRYSTAL_COAL.get()) return 6400;
        if (fuel.getItem() == ModItems.POWDER_SAWDUST.get()) return single / 2;

        if (fuel.getItem() == ModItems.BRIQUETTE.get()) {
            int customModelData = 0;
            if (fuel.hasTag() && Objects.requireNonNull(fuel.getTag()).contains("CustomModelData")) {
                customModelData = fuel.getTag().getInt("CustomModelData");
            }
            return switch (customModelData) {
                case 0 -> single * 10;
                case 1 -> single * 8;
                case 2 -> single * 2;
                default -> 0;
            };
        }

        if (fuel.getItem() == ModItems.POWDER_ASH.get()) {
            int customModelData = 0;
            if (fuel.hasTag() && Objects.requireNonNull(fuel.getTag()).contains("CustomModelData")) {
                customModelData = fuel.getTag().getInt("CustomModelData");
            }
            return switch (customModelData) {
                case 0 -> single / 2;  // WOOD
                case 1 -> single;       // COAL
                case 2 -> single / 2;   // MISC
                case 3 -> single;       // FLY
                case 4 -> single / 2;   // SOOT
                default -> 0;
            };
        }

        return 0;
    }

}