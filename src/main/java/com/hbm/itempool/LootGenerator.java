package com.hbm.itempool;

import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;

import com.hbm.tileentity.deco.TileEntityLoot;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;

import java.util.Arrays;
import java.util.List;

public class LootGenerator {

    public static final String LOOT_BOOKLET = "LOOT_BOOKLET";
    public static final String LOOT_CAPNUKE = "LOOT_CAPNUKE";
    public static final String LOOT_MEDICINE = "LOOT_MEDICINE";
    public static final String LOOT_CAPSTASH = "LOOT_CAPSTASH";
    public static final String LOOT_MAKESHIFT_GUN = "LOOT_MAKESHIFT_GUN";
    public static final String LOOT_NUKE_STORAGE = "LOOT_NUKE_STORAGE";
    public static final String LOOT_BONES = "LOOT_BONES";
    public static final String LOOT_GLYPHID_HIVE = "LOOT_GLYPHID_HIVE";
    public static final String LOOT_METEOR = "LOOT_METEOR";
    public static final String LOOT_FLAREGUN = "LOOT_FLAREGUN";
    public static final String LOOT_SHIT = "LOOT_SHIT";
    public static final String LOOT_MECHANICAL = "LOOT_MECHANICAL";
    public static final String LOOT_GEAR = "LOOT_GEAR";
    public static final String LOOT_SUPPLIES = "LOOT_SUPPLIES";

    public static void applyLoot(LevelAccessor level, BlockPos pos, String name) {
        TileEntityLoot loot = (TileEntityLoot) level.getBlockEntity(pos);
        if (loot == null || !loot.items.isEmpty()) return;

        switch (name) {
            case LOOT_BOOKLET -> lootBooklet(loot, level.getRandom());
            case LOOT_CAPNUKE -> lootCapNuke(loot, level.getRandom());
            case LOOT_MEDICINE -> lootMedicine(loot, level.getRandom());
            case LOOT_CAPSTASH -> lootCapStash(loot, level.getRandom());
            case LOOT_MAKESHIFT_GUN -> lootMakeshiftGun(loot, level.getRandom());
            case LOOT_NUKE_STORAGE -> lootNukeStorage(loot, level.getRandom());
            case LOOT_BONES -> lootBones(loot, level.getRandom());
            case LOOT_GLYPHID_HIVE -> lootGlyphidHive(loot, level.getRandom());
            case LOOT_METEOR -> lootBookMeteor(loot, level, level.getRandom());
            case LOOT_FLAREGUN -> lootFlareGun(loot, level.getRandom());
            case LOOT_SHIT -> lootShit(loot, level.getRandom());
            case LOOT_MECHANICAL -> lootMechanical(loot, level.getRandom());
            case LOOT_GEAR -> lootGear(loot, level.getRandom());
            case LOOT_SUPPLIES -> lootSupplies(loot, level.getRandom());
            default -> lootBones(loot, level.getRandom());
        }
    }

    public static List<String> getLootNames() {
        return Arrays.asList(
                LOOT_BOOKLET,
                LOOT_CAPNUKE,
                LOOT_MEDICINE,
                LOOT_CAPSTASH,
                LOOT_MAKESHIFT_GUN,
                LOOT_NUKE_STORAGE,
                LOOT_BONES,
                LOOT_GLYPHID_HIVE,
                LOOT_METEOR,
                LOOT_FLAREGUN,
                LOOT_MECHANICAL,
                LOOT_GEAR,
                LOOT_SUPPLIES
        );
    }

    private static void addItemWithDeviation(TileEntityLoot loot, RandomSource rand, ItemStack stack, double xOff, double yOff, double zOff) {
        if (stack != null && !stack.isEmpty()) {
            loot.addItem(stack, xOff + rand.nextGaussian() * 0.02, yOff, zOff + rand.nextGaussian() * 0.02);
        }
    }

    public static void lootBooklet(TileEntityLoot loot, RandomSource rand) {
        // ItemBookLore.createBook("beacon", 12, 0x404040, 0xD637B3)
        // Пока оставляем заглушку
        loot.addItem(new ItemStack(Items.BOOK), 0, 0, 0);
    }

    public static void lootCapNuke(TileEntityLoot loot, RandomSource rand) {
        if (rand.nextInt(5) == 0) {
            addItemWithDeviation(loot, rand, new ItemStack(ModAmmoItems.AMMO_NUKE_STANDARD.get()), -0.25, 0, -0.125);
        } else {
            addItemWithDeviation(loot, rand, new ItemStack(ModAmmoItems.AMMO_ROCKET_HEAT.get()), -0.25, 0, -0.25);
        }
        for (int i = 0; i < 4; i++) {
            addItemWithDeviation(loot, rand, new ItemStack(ModItems.CAP_NUKA.get(), 2), 0.125, i * 0.03125, 0.25);
        }
        for (int i = 0; i < 2; i++) {
            addItemWithDeviation(loot, rand, new ItemStack(ModItems.SYRINGE_METAL_STIMPAK.get()), -0.25, i * 0.03125, 0.25);
        }
        for (int i = 0; i < 6; i++) {
            addItemWithDeviation(loot, rand, new ItemStack(ModItems.CAP_NUKA.get(), 2), 0.125, i * 0.03125, -0.25);
        }
    }

    public static void lootMedicine(TileEntityLoot loot, RandomSource rand) {
        for (int i = 0; i < 4; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_MED_SYRINGE, rand), 0.125, i * 0.03125, 0.25);
        }
        addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_MED_PILLS, rand), -0.25, 0, -0.125);
    }

    public static void lootCapStash(TileEntityLoot loot, RandomSource rand) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int count = rand.nextInt(5) + 3;
                for (int k = 0; k < count; k++) {
                    addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_CAPS, rand), i * 0.3125, k * 0.03125, j * 0.3125);
                }
            }
        }
    }

    public static void lootMakeshiftGun(TileEntityLoot loot, RandomSource rand) {
        boolean r = rand.nextBoolean();
        if (r) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_MAKESHIFT_GUN, rand), 0.125, 0.025, 0.25);
        }
        if (!r || rand.nextBoolean()) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_MAKESHIFT_WRENCH, rand), -0.25, 0, -0.28125);
        }
        int count = rand.nextInt(2) + 1;
        for (int i = 0; i < count; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_MAKESHIFT_PLATES, rand), -0.25, i * 0.03125, 0.3125);
        }
        count = rand.nextInt(2) + 2;
        for (int i = 0; i < count; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_MAKESHIFT_WIRE, rand), 0.25, i * 0.03125, 0.1875);
        }
    }

    public static void lootNukeStorage(TileEntityLoot loot, RandomSource rand) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (rand.nextBoolean()) {
                    loot.addItem(ItemPool.getStack(ItemPoolsPile.POOL_PILE_NUKE_STORAGE, rand), -0.375 + i * 0.25, 0, -0.375 + j * 0.25);
                }
            }
        }
    }

    public static void lootBones(TileEntityLoot loot, RandomSource rand) {
        int limit = rand.nextInt(3) + 3;
        for (int i = 0; i < limit; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_BONES, rand), rand.nextDouble() - 0.5, i * 0.03125, rand.nextDouble() - 0.5);
        }
    }

    public static void lootGlyphidHive(TileEntityLoot loot, RandomSource rand) {
        int limit = rand.nextInt(3) + 3;
        for (int i = 0; i < limit; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_HIVE, rand), rand.nextDouble() - 0.5, i * 0.03125, rand.nextDouble() - 0.5);
        }
    }

    public static void lootBookMeteor(TileEntityLoot loot, LevelAccessor level, RandomSource rand) {
        // MKUCraftingHandler.getMKUItem(level) и generateBook
        // Пока заглушка
        loot.addItem(new ItemStack(ModItems.INGOT_STARMETAL.get()), 0, 0, 0.25);
        loot.addItem(new ItemStack(Items.BOOK), 0, 0, -0.25);
    }

    public static void lootFlareGun(TileEntityLoot loot, RandomSource rand) {
        addItemWithDeviation(loot, rand, new ItemStack(ModGunItems.GUN_FLAREGUN.get()), 0, 0, -0.25);

        int count = rand.nextInt(3) + 2;
        for (int k = 0; k < count; k++) {
            addItemWithDeviation(loot, rand, new ItemStack(ModAmmoItems.AMMO_G26_FLARE.get()), -0.25, k * 0.03125, 0.25);
        }

        count = rand.nextInt(1) + 1;
        for (int k = 0; k < count; k++) {
            addItemWithDeviation(loot, rand, new ItemStack(ModAmmoItems.AMMO_G26_FLARE_SUPPLY.get()), 0.25, k * 0.03125, 0.125);
        }
    }

    public static void lootShit(TileEntityLoot loot, RandomSource rand) {
        int limit = rand.nextInt(3) + 3;
        for (int i = 0; i < limit; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_OF_GARBAGE, rand), rand.nextDouble() - 0.5, i * 0.03125, rand.nextDouble() - 0.5);
        }
    }

    public static void lootMechanical(TileEntityLoot loot, RandomSource rand) {
        int limit = rand.nextInt(6) + 1;
        for (int i = 0; i < limit; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_MECHANICAL, rand),
                    rand.nextDouble() - 0.5, i * 0.03125, rand.nextDouble() - 0.5);
        }
    }

    public static void lootGear(TileEntityLoot loot, RandomSource rand) {
        int limit = rand.nextInt(6) + 1;
        for (int i = 0; i < limit; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_GEAR, rand),
                    rand.nextDouble() - 0.5, i * 0.03125, rand.nextDouble() - 0.5);
        }
    }

    public static void lootSupplies(TileEntityLoot loot, RandomSource rand) {
        int limit = rand.nextInt(3) + 4;
        for (int i = 0; i < limit; i++) {
            addItemWithDeviation(loot, rand, ItemPool.getStack(ItemPoolsPile.POOL_PILE_SUPPLIES, rand),
                    rand.nextDouble() - 0.5, i * 0.03125, rand.nextDouble() - 0.5);
        }
    }
}