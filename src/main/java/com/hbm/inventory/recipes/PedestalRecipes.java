package com.hbm.inventory.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.items.food.ItemConserve;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedestalRecipes {

    public static List<PedestalRecipe> recipes = new ArrayList<>();
    public static Map<Integer, List<PedestalRecipe>> recipeSets = new HashMap<>();


    public static void register() {

        /*
        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_LIGHT_REVOLVER_DANI.get()),
                null, new ComparableStack(ModItems.PLATE_LEAD.get()), null,
                new ComparableStack(ModItems.PLATE_GOLD.get()), new ComparableStack(ModGunItems.GUN_LIGHT_REVOLVER.get()), new ComparableStack(ModItems.PLATE_GOLD.get()),
                null, new ComparableStack(ModItems.PLATE_LEAD.get()), null
        ));

         */

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_MARESLEG_BROKEN.get()),
                new ComparableStack(ModBlocks.BARBED_WIRE.get()), new ComparableStack(ModItems.PLATE_WEAPON_STEEL.get()), new ComparableStack(ModBlocks.BARBED_WIRE.get()),
                new ComparableStack(ModItems.PLATE_WEAPON_STEEL.get()), new ComparableStack(ModGunItems.GUN_MARESLEG.get()), new ComparableStack(ModItems.PLATE_WEAPON_STEEL.get()),
                new ComparableStack(ModBlocks.BARBED_WIRE.get()), new ComparableStack(ModItems.PLATE_WEAPON_STEEL.get()), new ComparableStack(ModBlocks.BARBED_WIRE.get())
        ));

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_HEAVY_REVOLVER_LILMAC.get()),
                null, new ComparableStack(ModItems.WEAPON_MOD_SPECIAL_SCOPE.get()), null,
                new ComparableStack(ModItems.POWDER_MAGIC.get()), new ComparableStack(ModGunItems.GUN_HEAVY_REVOLVER.get()), new ComparableStack(ModItems.PLATE_WEAPON_STEEL.get()),
                null, new ComparableStack(ModItems.PART_GRIP_IVORY.get()), new ComparableStack(Items.APPLE, 3)
        ));

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_HEAVY_REVOLVER_PROTEGE.get()),
                new ComparableStack(ModBlocks.STEEL_CHAIN.get(), 16), new ComparableStack(ModItems.CINNABAR.get()), new ComparableStack(ModBlocks.STEEL_CHAIN.get(), 16),
                new ComparableStack(ModItems.SCRAP_NUCLEAR.get()), new ComparableStack(ModGunItems.GUN_HEAVY_REVOLVER.get()), new ComparableStack(ModItems.SCRAP_NUCLEAR.get()),
                new ComparableStack(ModBlocks.STEEL_CHAIN.get(), 16), new ComparableStack(ModItems.CINNABAR.get()), new ComparableStack(ModBlocks.STEEL_CHAIN.get(), 16)
        ));

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_FLAMER_DAYBREAKER.get()),
                new ComparableStack(ModItems.PLATE_CAST_GOLD.get()), new ComparableStack(ModItems.CANNED_CONSERVE.get(), 1, ItemConserve.EnumFoodType.JIZZ.ordinal()), new ComparableStack(ModItems.PLATE_CAST_GOLD.get()),
                new ComparableStack(ModItems.INGOT_PHOSPHORUS.get()), new ComparableStack(ModGunItems.GUN_FLAMER.get()), new ComparableStack(ModItems.INGOT_PHOSPHORUS.get()),
                new ComparableStack(ModItems.PLATE_CAST_GOLD.get()), new ComparableStack(ModItems.STICK_DYNAMITE.get()), new ComparableStack(ModItems.PLATE_CAST_GOLD.get())
        ).extra(PedestalExtraCondition.SUN));

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_AUTOSHOTGUN_SEXY.get()),
                new ComparableStack(ModItems.BOLT_SPIKE.get(), 16), new ComparableStack(ModItems.WILD_P.get()), new ComparableStack(ModItems.BOLT_SPIKE.get(), 16),
                new ComparableStack(ModItems.CARD_QOS.get()), new ComparableStack(ModGunItems.GUN_AUTOSHOTGUN.get()), new ComparableStack(ModItems.CARD_AOS.get()),
                new ComparableStack(ModItems.BOLT_SPIKE.get(), 16), new ComparableStack(ModItems.INGOT_STARMETAL.get(), 16), new ComparableStack(ModItems.BOLT_SPIKE.get(), 16)
        ));

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_MINIGUN_LACUNAE.get()),
                null, new ComparableStack(ModItems.POWDER_MAGIC.get(), 4), null,
                new ComparableStack(ModItems.ITEM_SECRET_SELENIUM_STEEL.get(), 4), new ComparableStack(ModGunItems.GUN_MINIGUN.get()), new ComparableStack(ModItems.ITEM_SECRET_SELENIUM_STEEL.get(), 4),
                null, new ComparableStack(ModItems.POWDER_MAGIC.get(), 4), null
        ).extra(PedestalExtraCondition.FULL_MOON));

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_LASER_PISTOL_MORNING_GLORY.get()),
                null, new ComparableStack(ModItems.MORNING_GLORY.get()), null,
                new ComparableStack(ModItems.ITEM_SECRET_SELENIUM_STEEL.get(), 2), new ComparableStack(ModGunItems.GUN_LASER_PISTOL.get()), new ComparableStack(ModItems.ITEM_SECRET_SELENIUM_STEEL.get(), 2),
                null, new ComparableStack(Items.EMERALD, 16), null
        ));

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_FOLLY.get()),
                new ComparableStack(ModItems.ITEM_SECRET_FOLLY.get(), 4), new ComparableStack(ModItems.ITEM_SECRET_CONTROLLER.get(), 2), new ComparableStack(ModItems.ITEM_SECRET_FOLLY.get(), 4),
                new ComparableStack(ModItems.INGOT_BSCCO.get(), 16), new ComparableStack(ModItems.BLOCK_STARMETAL.get(), 64), new ComparableStack(ModItems.INGOT_BSCCO.get(), 16),
                new ComparableStack(ModItems.ITEM_SECRET_FOLLY.get(), 4), new ComparableStack(ModItems.ITEM_SECRET_CONTROLLER.get(), 2), new ComparableStack(ModItems.ITEM_SECRET_FOLLY.get(), 4)
        ).extra(PedestalExtraCondition.FULL_MOON).set(1));

        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_ABERRATOR.get()),
                null, new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), null,
                new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), new ComparableStack(ModItems.GUN_MECHANISM_SATURNITE.get(), 4), new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()),
                null, new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), null
        ).set(1));

        /*
        register(new PedestalRecipe(
                new ItemStack(ModGunItems.GUN_ABERRATOR_EOTT.get()),
                new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()),
                new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), new ComparableStack(ModItems.GUN_MECHANISM_SATURNITE.get(), 16), new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()),
                new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get())
        ).extra(PedestalExtraCondition.GOOD_KARMA).set(1));
         */

        register(new PedestalRecipe(
                new ItemStack(ModAmmoItems.AMMO_FOLLY_SM.get()),
                new ComparableStack(ModItems.INGOT_STARMETAL.get()), new ComparableStack(ModItems.POWDER_MAGIC.get()), new ComparableStack(ModItems.INGOT_STARMETAL.get()),
                new ComparableStack(ModItems.POWDER_MAGIC.get()), new ComparableStack(ModItems.CHUNK_ORE_MOONSTONE.get()), new ComparableStack(ModItems.POWDER_MAGIC.get()),
                new ComparableStack(ModItems.INGOT_STARMETAL.get()), new ComparableStack(ModItems.POWDER_MAGIC.get()), new ComparableStack(ModItems.INGOT_STARMETAL.get())
        ).extra(PedestalExtraCondition.FULL_MOON).set(1));

        register(new PedestalRecipe(
                new ItemStack(ModAmmoItems.AMMO_FOLLY_NUKE.get()),
                new ComparableStack(ModItems.INGOT_STARMETAL.get()), new ComparableStack(ModItems.POWDER_MAGIC.get()), new ComparableStack(ModItems.INGOT_STARMETAL.get()),
                new ComparableStack(ModItems.POWDER_MAGIC.get()), new ComparableStack(ModAmmoItems.AMMO_NUKE_HIGH.get(), 4), new ComparableStack(ModItems.POWDER_MAGIC.get()),
                new ComparableStack(ModItems.INGOT_STARMETAL.get()), new ComparableStack(ModItems.POWDER_MAGIC.get()), new ComparableStack(ModItems.INGOT_STARMETAL.get())
        ).extra(PedestalExtraCondition.FULL_MOON).set(1));

        register(new PedestalRecipe(
                new ItemStack(ModAmmoItems.AMMO_P35_800.get(), 5),
                null, null, null,
                null, new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get()), null,
                null, null, null
        ).set(1));

        register(new PedestalRecipe(
                new ItemStack(ModAmmoItems.AMMO_P35_800_BL.get(), 10),
                null, null, null,
                null, new ComparableStack(ModItems.ITEM_SECRET_ABERRATOR.get(), 3), null,
                null, null, null
        ).set(1));
    }

    public static void register(PedestalRecipe recipe) {
        recipes.add(recipe);
        int set = Math.abs(recipe.recipeSet) % 2;
        recipeSets.computeIfAbsent(set, k -> new ArrayList<>()).add(recipe);
    }

    public enum PedestalExtraCondition {
        NONE, FULL_MOON, NEW_MOON, SUN, GOOD_KARMA, BAD_KARMA
    }

    public static class PedestalRecipe {
        public ItemStack output;
        public AStack[] input;
        public int recipeSet = 0;
        public PedestalExtraCondition extra = PedestalExtraCondition.NONE;

        public PedestalRecipe(ItemStack output, AStack... input) {
            this.output = output;
            this.input = input;
        }

        public PedestalRecipe extra(PedestalExtraCondition extra) {
            this.extra = extra;
            return this;
        }

        public PedestalRecipe set(int set) {
            this.recipeSet = set;
            return this;
        }
    }
}