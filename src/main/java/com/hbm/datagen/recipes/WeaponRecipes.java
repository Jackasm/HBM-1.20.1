package com.hbm.datagen.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.recipes.ingredient.FluidBucketIngredient;
import com.hbm.datagen.recipes.ingredient.FluidTankIngredient;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.hbm.datagen.recipes.ModRecipeProvider.hasTag;
import static com.hbm.util.RefStrings.MODID;

public class WeaponRecipes {

    public static void generateWeaponRecipes(Consumer<FinishedRecipe> writer,
                                             Function<Item, InventoryChangeTrigger.TriggerInstance> has) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MACHINE_WEAPON_TABLE.get(), 1)
                .pattern("PPP")
                .pattern("TCT")
                .pattern("TST")
                .define('P', ModItems.PLATE_GUNMETAL.get())
                .define('T', ModItems.INGOT_STEEL.get())
                .define('C', Blocks.CRAFTING_TABLE)
                .define('S', ModBlocks.BLOCK_STEEL.get().asItem())
                .unlockedBy("has_gunmetal_plate", has.apply(ModItems.PLATE_GUNMETAL.get()))
                .save(writer, MODID + ":machine_weapon_table");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_WOOD.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ItemTags.PLANKS)
                .unlockedBy("has_planks", hasTag(ItemTags.PLANKS))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_WOOD.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ItemTags.PLANKS)
                .unlockedBy("has_planks", hasTag(ItemTags.PLANKS))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_POLYMER.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ModItems.INGOT_POLYMER.get())
                .unlockedBy("has_polymer_ingot", has.apply(ModItems.INGOT_POLYMER.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_POLYMER.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_POLYMER.get())
                .unlockedBy("has_polymer_ingot", has.apply(ModItems.INGOT_POLYMER.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_BAKELITE.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ModItems.INGOT_BAKELITE.get())
                .unlockedBy("has_bakelite_ingot", has.apply(ModItems.INGOT_BAKELITE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_BAKELITE.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_BAKELITE.get())
                .unlockedBy("has_bakelite_ingot", has.apply(ModItems.INGOT_BAKELITE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_POLYCARBONATE.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ModItems.INGOT_PC.get())
                .unlockedBy("has_hardplastic_ingot", has.apply(ModItems.INGOT_PC.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_POLYCARBONATE.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_PC.get())
                .unlockedBy("has_hardplastic_ingot", has.apply(ModItems.INGOT_PC.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_PVC.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ModItems.INGOT_PVC.get())
                .unlockedBy("has_pvc_ingot", has.apply(ModItems.INGOT_PVC.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_PVC.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_PVC.get())
                .unlockedBy("has_pvc_ingot", has.apply(ModItems.INGOT_PVC.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_RUBBER.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_RUBBER.get())
                .unlockedBy("has_rubber_ingot", has.apply(ModItems.INGOT_RUBBER.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_IVORY.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', Items.BONE)
                .unlockedBy("has_bone", has.apply(Items.BONE))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  ModItems.CASING_SHOTSHELL.get(), 2)
                .pattern("P")
                .pattern("C")
                .define('P', ModItems.PLATE_GUNMETAL.get())
                .define('C', ModItems.CASING_LARGE.get())
                .unlockedBy("has_gunmetal_plate", has.apply(ModItems.PLATE_GUNMETAL.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CASING_BUCKSHOT.get(), 2)
                .pattern("P")
                .pattern("C")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModItems.CASING_LARGE.get())
                .unlockedBy("has_plastic_ingot", hasTag(ModItemTags.ANY_PLASTIC_INGOT))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CASING_BUCKSHOT_ADVANCED.get(), 2)
                .pattern("P")
                .pattern("C")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModItems.CASING_LARGE_STEEL.get())
                .unlockedBy("has_plastic_ingot", hasTag(ModItemTags.ANY_PLASTIC_INGOT))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_PEPPERBOX.get(), 1)
                .pattern("IIW")
                .pattern("  C")
                .define('I', Items.IRON_INGOT)
                .define('W', ItemTags.PLANKS)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_iron_ingot", has.apply(Items.IRON_INGOT))
                .save(writer, MODID + ":gun_pepperbox");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LIGHT_REVOLVER.get(), 1)
                .pattern("BRM")
                .pattern("  G")
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .unlockedBy("has_steel_light_barrel", has.apply(ModItems.LIGHT_BARREL_STEEL.get()))
                .save(writer, MODID + ":gun_light_revolver");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LIGHT_REVOLVER_ATLAS.get(), 1)
                .pattern(" M ")
                .pattern("MAM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('A', ModGunItems.GUN_LIGHT_REVOLVER.get())
                .unlockedBy("has_weapon_steel_mechanism", has.apply(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_light_revolver_atlas");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_HENRY.get(), 1)
                .pattern("BRP")
                .pattern("BMS")
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_GUNMETAL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .define('P', ModItems.PLATE_GUNMETAL.get())
                .unlockedBy("has_gunmetal_plate", has.apply(ModItems.PLATE_GUNMETAL.get()))
                .save(writer, MODID + ":gun_henry");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_HENRY_LINCOLN.get(), 1)
                .pattern(" M ")
                .pattern("PGP")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('P', ModItems.PLATE_CAST_GOLD.get())
                .define('G', ModGunItems.GUN_HENRY.get())
                .unlockedBy("has_weapon_steel_mechanism", has.apply(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_henry_lincoln");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_GREASEGUN.get(), 1)
                .pattern("BRS")
                .pattern("SMG")
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_STEEL.get())
                .define('S', ModItems.BOLT_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_STEEL.get())
                .unlockedBy("has_steel_bolt", has.apply(ModItems.BOLT_STEEL.get()))
                .save(writer, MODID + ":gun_greasegun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_MARESLEG.get(), 1)
                .pattern("BRM")
                .pattern("BGS")
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.BOLT_STEEL.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_steel_bolt", has.apply(ModItems.BOLT_STEEL.get()))
                .save(writer, MODID + ":gun_maresleg");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FLAREGUN.get(), 1)
                .pattern("BRM")
                .pattern("  G")
                .define('B', ModItems.HEAVY_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_STEEL.get())
                .unlockedBy("has_steel_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_STEEL.get()))
                .save(writer, MODID + ":gun_flaregun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_AM180.get(), 1)
                .pattern("BRS")
                .pattern("GMG")
                .define('B', ModItems.LIGHT_BARREL_DURA_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_DURA_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_dura_steel_light_barrel", has.apply(ModItems.LIGHT_BARREL_DURA_STEEL.get()))
                .save(writer, MODID + ":gun_am180");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LIBERATOR.get(), 1)
                .pattern("BB ")
                .pattern("BBM")
                .pattern("G G")
                .define('B', ModItems.LIGHT_BARREL_DURA_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .unlockedBy("has_dura_steel_light_barrel", has.apply(ModItems.LIGHT_BARREL_DURA_STEEL.get()))
                .save(writer, MODID + ":gun_liberator");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CONGOLAKE.get(), 1)
                .pattern("BM ")
                .pattern("BRS")
                .pattern("G  ")
                .define('B', ModItems.HEAVY_BARREL_DURA_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('R', ModItems.LIGHT_RECEIVER_DURA_STEEL.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .unlockedBy("has_dura_steel_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_DURA_STEEL.get()))
                .save(writer, MODID + ":gun_congolake");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FLAMER.get(), 1)
                .pattern(" MG")
                .pattern("BBR")
                .pattern(" GM")
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_DURA_STEEL.get())
                .define('B', ModItems.HEAVY_BARREL_DURA_STEEL.get())
                .define('R', ModItems.HEAVY_RECEIVER_DURA_STEEL.get())
                .unlockedBy("has_dura_steel_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_DURA_STEEL.get()))
                .save(writer, MODID + ":gun_flamer");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FLAMER_TOPAZ.get(), 1)
                .pattern(" M ")
                .pattern("MFM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('F', ModGunItems.GUN_FLAMER.get())
                .unlockedBy("has_weapon_steel_mechanism", has.apply(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_flamer_topaz");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_HEAVY_REVOLVER.get(), 1)
                .pattern("BRM")
                .pattern("  G")
                .define('B', ModItems.LIGHT_BARREL_DESH.get())
                .define('R', ModItems.LIGHT_RECEIVER_DESH.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .unlockedBy("has_desh_light_barrel", has.apply(ModItems.LIGHT_BARREL_DESH.get()))
                .save(writer, MODID + ":gun_heavy_revolver");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CARBINE.get(), 1)
                .pattern("BRM")
                .pattern("G S")
                .define('B', ModItems.LIGHT_BARREL_DESH.get())
                .define('R', ModItems.LIGHT_RECEIVER_DESH.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_desh_light_barrel", has.apply(ModItems.LIGHT_BARREL_DESH.get()))
                .save(writer, MODID + ":gun_carbine");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_UZI.get(), 1)
                .pattern("BRS")
                .pattern(" GM")
                .define('B', ModItems.LIGHT_BARREL_DESH.get())
                .define('R', ModItems.LIGHT_RECEIVER_DESH.get())
                .define('S', ModItemTags.ANY_PLASTIC_STOCK)
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .unlockedBy("has_desh_light_barrel", has.apply(ModItems.LIGHT_BARREL_DESH.get()))
                .save(writer, MODID + ":gun_uzi");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_SPAS12.get(), 1)
                .pattern("BRM")
                .pattern("BGS")
                .define('B', ModItems.LIGHT_BARREL_DESH.get())
                .define('R', ModItems.LIGHT_RECEIVER_DESH.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .define('S', ModItems.PART_STOCK_DESH.get())
                .unlockedBy("has_desh_light_barrel", has.apply(ModItems.LIGHT_BARREL_DESH.get()))
                .save(writer, MODID + ":gun_spas12");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_PANZERSCHRECK.get(), 1)
                .pattern("BBB")
                .pattern("PGM")
                .define('B', ModItems.HEAVY_BARREL_DESH.get())
                .define('P', ModItems.PLATE_CAST_STEEL.get())
                .define('G', ModItems.PART_GRIP_DESH.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .unlockedBy("has_desh_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_DESH.get()))
                .save(writer, MODID + ":gun_panzerschreck");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_G3.get(), 1)
                .pattern("BRM")
                .pattern("WGS")
                .define('B', ModItems.LIGHT_BARREL_WEAPON_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('W', ModItems.PART_GRIP_WOOD.get())
                .define('G', ModItems.PART_GRIP_RUBBER.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_weapon_steel_light_barrel", has.apply(ModItems.LIGHT_BARREL_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_g3");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_G3_ZEBRA.get(), 1)
                .pattern(" M ")
                .pattern("MPM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('P', ModGunItems.GUN_G3.get())
                .unlockedBy("has_weapon_steel_mechanism", has.apply(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_g3_zebra");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_STINGER.get(), 1)
                .pattern("BBB")
                .pattern("PGM")
                .define('B', ModItems.HEAVY_BARREL_WEAPON_STEEL.get())
                .define('P', ModItems.CIRCUIT_ADVANCED.get())
                .define('G', ModItems.PART_GRIP_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .unlockedBy("has_weapon_steel_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_stinger");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CHEMTHROWER.get(), 1)
                .pattern("MHW")
                .pattern("PSS")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('H', ModItems.PIPE_RUBBER.get())
                .define('W', ModToolItems.WRENCH.get())
                .define('P', ModItems.HEAVY_BARREL_WEAPON_STEEL.get())
                .define('S', ModItems.SHELL_WEAPON_STEEL.get())
                .unlockedBy("has_weapon_steel_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_chemthrower");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_AMAT.get(), 1)
                .pattern(" C ")
                .pattern("BRS")
                .pattern(" MG")
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .define('B', ModItems.HEAVY_BARREL_FERROURANIUM.get())
                .define('R', ModItems.HEAVY_RECEIVER_FERROURANIUM.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('C', ModItems.WEAPON_MOD_SCOPE.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_ferrouranium_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_FERROURANIUM.get()))
                .save(writer, MODID + ":gun_amat");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_M2.get(), 1)
                .pattern("  G")
                .pattern("BRM")
                .pattern("  G")
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .define('B', ModItems.HEAVY_BARREL_FERROURANIUM.get())
                .define('R', ModItems.HEAVY_RECEIVER_FERROURANIUM.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .unlockedBy("has_ferrouranium_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_FERROURANIUM.get()))
                .save(writer, MODID + ":gun_m2");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_AUTOSHOTGUN.get(), 1)
                .pattern("BRM")
                .pattern("G G")
                .define('B', ModItems.HEAVY_BARREL_FERROURANIUM.get())
                .define('R', ModItems.HEAVY_RECEIVER_FERROURANIUM.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_ferrouranium_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_FERROURANIUM.get()))
                .save(writer, MODID + ":gun_autoshotgun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_AUTOSHOTGUN_SHREDDER.get(), 1)
                .pattern(" M ")
                .pattern("MAM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('A', ModGunItems.GUN_AUTOSHOTGUN.get())
                .unlockedBy("has_gunmetal_mechanism", has.apply(ModItems.GUN_MECHANISM_GUNMETAL.get()))
                .save(writer, MODID + ":gun_autoshotgun_shredder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_QUADRO.get(), 1)
                .pattern("BCB")
                .pattern("BMB")
                .pattern("GG ")
                .define('B', ModItems.HEAVY_BARREL_FERROURANIUM.get())
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_ferrouranium_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_FERROURANIUM.get()))
                .save(writer, MODID + ":gun_quadro");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LAG.get(), 1)
                .pattern("BRM")
                .pattern("  G")
                .define('B', ModItemTags.ANY_RESISTANTALLOY_LIGHT_BARREL)
                .define('R', ModItemTags.ANY_RESISTANTALLOY_LIGHT_RECEIVER)
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_weapon_steel_mechanism", has.apply(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_lag");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_MINIGUN.get(), 1)
                .pattern("BMG")
                .pattern("BRE")
                .pattern("BGM")
                .define('B', ModItemTags.ANY_RESISTANTALLOY_LIGHT_BARREL)
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .define('R', ModItemTags.ANY_RESISTANTALLOY_HEAVY_RECEIVER)
                .define('E', ModItems.MOTOR_DESH.get())
                .unlockedBy("has_desh_motor", has.apply(ModItems.MOTOR_DESH.get()))
                .save(writer, MODID + ":gun_minigun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_MISSILE_LAUNCHER.get(), 1)
                .pattern(" CM")
                .pattern("BBB")
                .pattern("G  ")
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('B', ModItemTags.ANY_RESISTANTALLOY_HEAVY_BARREL)
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_weapon_steel_mechanism", has.apply(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(writer, MODID + ":gun_missile_launcher");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_TESLA_CANNON.get(), 1)
                .pattern("CCC")
                .pattern("BRB")
                .pattern("MGE")
                .define('C', ModItems.COIL_ADVANCED_ALLOY.get())
                .define('B', ModItemTags.ANY_RESISTANTALLOY_HEAVY_BARREL)
                .define('R', ModItemTags.ANY_RESISTANTALLOY_HEAVY_RECEIVER)
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .define('E', ModItems.CIRCUIT_ADVANCED.get())
                .unlockedBy("has_advanced_alloy_coil", has.apply(ModItems.COIL_ADVANCED_ALLOY.get()))
                .save(writer, MODID + ":gun_tesla_cannon");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LASER_PISTOL.get(), 1)
                .pattern("CRM")
                .pattern("GG ")
                .define('C', ModItems.CRYSTAL_REDSTONE.get())
                .define('R', ModItems.LIGHT_RECEIVER_SATURNITE.get())
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .define('G', ModItemTags.ANY_HARDPLASTIC_GRIP)
                .unlockedBy("has_saturnite_light_receiver", has.apply(ModItems.LIGHT_RECEIVER_SATURNITE.get()))
                .save(writer, MODID + ":gun_laser_pistol");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LASER_PISTOL_PEW_PEW.get(), 1)
                .pattern(" M ")
                .pattern("MPM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .define('P', ModGunItems.GUN_LASER_PISTOL.get())
                .unlockedBy("has_saturnite_mechanism", has.apply(ModItems.GUN_MECHANISM_SATURNITE.get()))
                .save(writer, MODID + ":gun_laser_pistol_pew_pew");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_STG77.get(), 1)
                .pattern(" D ")
                .pattern("BRS")
                .pattern("GGM")
                .define('D', ModItems.WEAPON_MOD_SCOPE.get())
                .define('B', ModItems.LIGHT_BARREL_SATURNITE.get())
                .define('R', ModItems.LIGHT_RECEIVER_SATURNITE.get())
                .define('S', ModItemTags.ANY_HARDPLASTIC_STOCK)
                .define('G', ModItemTags.ANY_HARDPLASTIC_GRIP)
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .unlockedBy("has_saturnite_light_barrel", has.apply(ModItems.LIGHT_BARREL_SATURNITE.get()))
                .save(writer, MODID + ":gun_stg77");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FATMAN.get(), 1)
                .pattern("PPP")
                .pattern("BSR")
                .pattern("G M")
                .define('P', ModItems.PLATE_SATURNITE.get())
                .define('B', ModItems.HEAVY_BARREL_SATURNITE.get())
                .define('S', ModItems.SHELL_SATURNITE.get())
                .define('R', ModItems.HEAVY_RECEIVER_SATURNITE.get())
                .define('G', ModItemTags.ANY_HARDPLASTIC_GRIP)
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .unlockedBy("has_saturnite_heavy_barrel", has.apply(ModItems.HEAVY_BARREL_SATURNITE.get()))
                .save(writer, MODID + ":gun_fatman");


        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_TAU.get(), 1)
                .pattern(" RD")
                .pattern("CTT")
                .pattern("GMS")
                .define('D', ModItems.CIRCUIT_BISMOID.get())
                .define('C', ModItems.PIPE_COPPER.get())
                .define('T', ModItems.COIL_ADVANCED_ALLOY_TORUS.get())
                .define('G', ModItemTags.ANY_HARDPLASTIC_GRIP)
                .define('R', ModItems.LIGHT_RECEIVER_SATURNITE.get())
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .define('S', ModItemTags.ANY_HARDPLASTIC_STOCK)
                .unlockedBy("has_saturnite_light_receiver", has.apply(ModItems.LIGHT_RECEIVER_SATURNITE.get()))
                .save(writer, MODID + ":gun_tau");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LASRIFLE.get(), 1)
                .pattern("DLC")
                .pattern("BRS")
                .pattern("MG ")
                .define('D', ModItems.CRYSTAL_REDSTONE.get())
                .define('L', ModItems.WEAPON_MOD_SCOPE.get())
                .define('C', ModItems.CIRCUIT_BISMOID.get())
                .define('B', ModItemTags.ANY_BISMOID_BRONZE_LIGHT_BARREL)
                .define('R', ModItemTags.ANY_BISMOID_BRONZE_LIGHT_RECEIVER)
                .define('S', ModItemTags.ANY_HARDPLASTIC_STOCK)
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .define('G', ModItemTags.ANY_HARDPLASTIC_GRIP)
                .unlockedBy("has_bismuth_bronze_light_barrel", has.apply(ModItems.LIGHT_BARREL_BISMUTH_BRONZE.get()))
                .save(writer, MODID + ":gun_lasrifle");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModGunItems.GUN_DOUBLE_BARREL_SACRED_DRAGON.get(), 1)
                .requires(ModGunItems.GUN_DOUBLE_BARREL.get())
                .requires(ModItems.SECRET_SELENIUM_STEEL.get())
                .unlockedBy("has_double_barrel", has.apply(ModGunItems.GUN_DOUBLE_BARREL.get()))
                .unlockedBy("has_selenium_steel", has.apply(ModItems.SECRET_SELENIUM_STEEL.get()))
                .save(writer, MODID + ":gun_sacred_dragon");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CHARGE_THROWER.get(), 1)
                .pattern("MMM")
                .pattern("BBL")
                .pattern("GG ")
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('B', ModItems.HEAVY_BARREL_STEEL.get())
                .define('G', ModItems.PART_GRIP_STEEL.get())
                .define('L', Items.LEATHER)
                .unlockedBy("has_gunmetal_mechanism", has.apply(ModItems.GUN_MECHANISM_GUNMETAL.get()))
                .save(writer, MODID + ":gun_charge_thrower_leather");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CHARGE_THROWER.get(), 1)
                .pattern("MMM")
                .pattern("BBL")
                .pattern("GG ")
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('B', ModItems.HEAVY_BARREL_STEEL.get())
                .define('G', ModItems.PART_GRIP_STEEL.get())
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_gunmetal_mechanism", has.apply(ModItems.GUN_MECHANISM_GUNMETAL.get()))
                .save(writer, MODID + ":gun_charge_thrower_rubber");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_STONE.get(), 6)
                .pattern("C")
                .pattern("P")
                .pattern("G")
                .define('C', Items.COBBLESTONE)
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_gunpowder", has.apply(Items.GUNPOWDER))
                .save(writer, MODID + ":ammo_standard_stone");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_STONE_AP.get(), 6)
                .pattern("C")
                .pattern("P")
                .pattern("G")
                .define('C', Items.FLINT)
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_flint", has.apply(Items.FLINT))
                .save(writer, MODID + ":ammo_standard_stone_ap");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_STONE_SHOT.get(), 6)
                .pattern("C")
                .pattern("P")
                .pattern("G")
                .define('C', Items.GRAVEL.asItem())
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_gravel", has.apply(Items.GRAVEL.asItem()))
                .save(writer, MODID + ":ammo_standard_stone_shot");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_STONE_IRON.get(), 6)
                .pattern("C")
                .pattern("P")
                .pattern("G")
                .define('C', Items.IRON_INGOT)
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_iron_ingot", has.apply(Items.IRON_INGOT))
                .save(writer, MODID + ":ammo_standard_stone_iron");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_IRON_DAMAGE.get(), 1)
                .requires(ModItems.INGOT_GUNMETAL.get())
                .requires(Items.IRON_INGOT, 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_iron_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_IRON_DURA.get(), 1)
                .requires(ModItems.INGOT_GUNMETAL.get())
                .requires(Items.IRON_INGOT)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_iron_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_STEEL_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_STEEL.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_steel_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_STEEL_DURA.get(), 1)
                .requires(ModItems.PLATE_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_STEEL.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_steel_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_DURA_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_DURA.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_dura_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_DURA_DURA.get(), 1)
                .requires(ModItems.PLATE_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_DURA.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_dura_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_DESH_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_DESH.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_desh_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_DESH_DURA.get(), 1)
                .requires(ModItems.PLATE_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_DESH.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_desh_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_WSTEEL_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .requires(ModItems.PLATE_CAST_WEAPON_STEEL.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_wsteel_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_WSTEEL_DURA.get(), 1)
                .requires(ModItems.PLATE_WEAPON_STEEL.get())
                .requires(ModItems.PLATE_CAST_WEAPON_STEEL.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_wsteel_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_FERRO_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .requires(ModItems.PLATE_CAST_FERROURANIUM.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_ferro_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_FERRO_DURA.get(), 1)
                .requires(ModItems.PLATE_WEAPON_STEEL.get())
                .requires(ModItems.PLATE_CAST_FERROURANIUM.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_ferro_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_TCALLOY_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .requires(Ingredient.of(ModItemTags.ANY_RESISTANTALLOY_PLATE_CAST), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_tcalloy_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_TCALLOY_DURA.get(), 1)
                .requires(ModItems.PLATE_WEAPON_STEEL.get())
                .requires(ModItemTags.ANY_RESISTANTALLOY_PLATE_CAST)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_tcalloy_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BIGMT_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_SATURNITE.get())
                .requires(ModItems.PLATE_CAST_SATURNITE.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_bigmt_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BIGMT_DURA.get(), 1)
                .requires(ModItems.PLATE_SATURNITE.get())
                .requires(ModItems.PLATE_CAST_SATURNITE.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_bigmt_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BRONZE_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_SATURNITE.get())
                .requires(Ingredient.of(ModItemTags.ANY_BISMOID_BRONZE_PLATE_CAST), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_bronze_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BRONZE_DURA.get(), 1)
                .requires(ModItems.PLATE_SATURNITE.get())
                .requires(ModItemTags.ANY_BISMOID_BRONZE_PLATE_CAST)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has.apply(ModItems.DUCTTAPE.get()))
                .save(writer, MODID + ":weapon_mod_generic_bronze_dura");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SILENCER.get(), 1)
                .pattern("P")
                .pattern("B")
                .pattern("P")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .unlockedBy("has_steel_light_barrel", has.apply(ModItems.LIGHT_BARREL_STEEL.get()))
                .save(writer, MODID + ":weapon_mod_special_silencer");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SCOPE.get(), 1)
                .pattern("SPS")
                .pattern("G G")
                .pattern("SPS")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('S', ModItems.PLATE_STEEL.get())
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .unlockedBy("has_steel_plate", has.apply(ModItems.PLATE_STEEL.get()))
                .save(writer, MODID + ":weapon_mod_special_scope");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SAW.get(), 1)
                .pattern("BBS")
                .pattern("BHS")
                .define('B', ModItems.BOLT_STEEL.get())
                .define('S', ItemTags.PLANKS)
                .define('H', ModItems.PLATE_DURA_STEEL.get())
                .unlockedBy("has_dura_plate", has.apply(ModItems.PLATE_DURA_STEEL.get()))
                .save(writer, MODID + ":weapon_mod_special_saw");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SPEEDLOADER.get(), 1)
                .pattern(" B ")
                .pattern("BSB")
                .pattern(" B ")
                .define('B', ModItems.BOLT_STEEL.get())
                .define('S', ModItems.PLATE_WEAPON_STEEL.get())
                .unlockedBy("has_weapon_steel_plate", has.apply(ModItems.PLATE_WEAPON_STEEL.get()))
                .save(writer, MODID + ":weapon_mod_special_speedloader");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SLOWDOWN.get(), 1)
                .pattern(" I ")
                .pattern(" M ")
                .pattern("I I")
                .define('I', ModItems.INGOT_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .unlockedBy("has_weapon_steel_ingot", has.apply(ModItems.INGOT_WEAPON_STEEL.get()))
                .save(writer, MODID + ":weapon_mod_special_slowdown");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SPEEDUP.get(), 1)
                .pattern("PIP")
                .pattern("WWW")
                .pattern("PIP")
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('I', ModItems.INGOT_GUNMETAL.get())
                .define('W', ModItems.WIRE_DENSE_GOLD.get())
                .unlockedBy("has_gold_dense_wire", has.apply(ModItems.WIRE_DENSE_GOLD.get()))
                .save(writer, MODID + ":weapon_mod_special_speedup");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_GREASEGUN.get(), 1)
                .pattern("BRM")
                .pattern("P G")
                .define('B', ModItems.LIGHT_BARREL_WEAPON_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('P', ModItems.PLATE_DURA_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_weapon_steel_light_barrel", has.apply(ModItems.LIGHT_BARREL_WEAPON_STEEL.get()))
                .save(writer, MODID + ":weapon_mod_special_greasegun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_CHOKE.get(), 1)
                .pattern("P")
                .pattern("B")
                .pattern("P")
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('B', ModItems.LIGHT_BARREL_DURA_STEEL.get())
                .unlockedBy("has_dura_steel_light_barrel", has.apply(ModItems.LIGHT_BARREL_DURA_STEEL.get()))
                .save(writer, MODID + ":weapon_mod_special_choke");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_FURNITURE_GREEN.get(), 1)
                .pattern("PDS")
                .pattern("  G")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('D', Items.GREEN_DYE)
                .define('S', ModItemTags.ANY_PLASTIC_STOCK)
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_plastic_grip", hasTag(ModItemTags.ANY_PLASTIC_GRIP))
                .save(writer, MODID + ":weapon_mod_special_furniture_green");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_FURNITURE_BLACK.get(), 1)
                .pattern("PDS")
                .pattern("  G")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('D', Items.BLACK_DYE)
                .define('S', ModItemTags.ANY_PLASTIC_STOCK)
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_plastic_grip", hasTag(ModItemTags.ANY_PLASTIC_GRIP))
                .save(writer, MODID + ":weapon_mod_special_furniture_black");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SKIN_SATURNITE.get(), 1)
                .pattern("BRM")
                .pattern(" P ")
                .define('B', ModItems.LIGHT_BARREL_SATURNITE.get())
                .define('R', ModItems.LIGHT_RECEIVER_SATURNITE.get())
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .define('P', ModItems.PLATE_SATURNITE.get())
                .unlockedBy("has_saturnite_plate", has.apply(ModItems.PLATE_SATURNITE.get()))
                .save(writer, MODID + ":weapon_mod_special_skin_saturnite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_STACK_MAG.get(), 1)
                .pattern("P P")
                .pattern("P P")
                .pattern("PMP")
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .unlockedBy("has_saturnite_mechanism", has.apply(ModItems.GUN_MECHANISM_SATURNITE.get()))
                .save(writer, MODID + ":weapon_mod_special_stack_mag");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BAYONET.get(), 1)
                .pattern("  P")
                .pattern("BBB")
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('B', ModItems.BOLT_STEEL.get())
                .unlockedBy("has_weapon_steel_plate", has.apply(ModItems.PLATE_WEAPON_STEEL.get()))
                .save(writer, MODID + ":weapon_mod_special_bayonet");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_LAS_SHOTGUN.get(), 1)
                .pattern("PPP")
                .pattern("RCR")
                .pattern("PPP")
                .define('P', ModItemTags.ANY_HARDPLASTIC_INGOT)
                .define('R', ModItems.CRYSTAL_REDSTONE.get())
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .unlockedBy("has_advanced_circuit", has.apply(ModItems.CIRCUIT_ADVANCED.get()))
                .save(writer, MODID + ":weapon_mod_special_las_shotgun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_LAS_CAPACITOR.get(), 1)
                .pattern("CCC")
                .pattern("PIP")
                .define('C', ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get())
                .define('P', ModItemTags.ANY_HARDPLASTIC_INGOT)
                .define('I', ModItems.CIRCUIT_CHIP_BISMOID.get())
                .unlockedBy("has_tantalium_capacitor", has.apply(ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get()))
                .save(writer, MODID + ":weapon_mod_special_las_capacitor");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_LAS_AUTO.get(), 1)
                .pattern(" C ")
                .pattern("RFR")
                .pattern(" C ")
                .define('C', ModItems.CIRCUIT_CHIP_BISMOID.get())
                .define('R', ModItems.CRYSTAL_REDSTONE.get())
                .define('F', ModItemTags.ANY_BISMOID_BRONZE_HEAVY_RECEIVER)
                .unlockedBy("has_bismoid_chip", has.apply(ModItems.CIRCUIT_CHIP_BISMOID.get()))
                .save(writer, MODID + ":weapon_mod_special_las_auto");

        addNitraRecipe(writer, ModAmmoItems.AMMO_M357_SP.get(), 6);
        addNitraRecipe(writer, ModAmmoItems.AMMO_M44_SP.get(), 6);
        addNitraRecipe(writer, ModAmmoItems.AMMO_P9_SP.get(), 12);
        addNitraRecipe(writer, ModAmmoItems.AMMO_P22_SP.get(), 32);
        addNitraRecipe(writer, ModAmmoItems.AMMO_R556_SP.get(), 8);
        addNitraRecipe(writer, ModAmmoItems.AMMO_R762_SP.get(), 6);
        addNitraRecipe(writer, ModAmmoItems.AMMO_BMG50_SP.get(), 4);
        addNitraRecipe(writer, ModAmmoItems.AMMO_G40_HE.get(), 3);
        addNitraRecipe(writer, ModAmmoItems.AMMO_ROCKET_HE.get(), 2);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_M44_EQUESTRIAN.get(), 6)
                .requires(ModAmmoItems.AMMO_M44_JHP.get())
                .requires(ModItems.ITEM_SECRET_SELENIUM_STEEL.get())
                .unlockedBy("has_item_secret_selenium_steel", has.apply(ModItems.ITEM_SECRET_SELENIUM_STEEL.get()))
                .save(writer, MODID + ":ammo_m44_jhp");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_G12_EQUESTRIAN.get(), 3)
                .requires(ModAmmoItems.AMMO_G12.get())
                .requires(ModItems.ITEM_SECRET_SELENIUM_STEEL.get())
                .unlockedBy("has_item_secret_selenium_steel", has.apply(ModItems.ITEM_SECRET_SELENIUM_STEEL.get()))
                .save(writer, MODID + ":ammo_g12");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_BMG50_EQUESTRIAN.get(), 4)
                .requires(ModAmmoItems.AMMO_BMG50_FMJ.get())
                .requires(ModItems.ITEM_SECRET_SELENIUM_STEEL.get())
                .unlockedBy("has_item_secret_selenium_steel", has.apply(ModItems.ITEM_SECRET_SELENIUM_STEEL.get()))
                .save(writer, MODID + ":ammo_bmg50_fmj");


        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ROCKET_HE.get(), 2)
                .requires(ModAmmoItems.AMMO_ROCKET_HE.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has.apply(ModItems.NITRA.get()))
                .save(writer, MODID + ":ammo_rocket_he");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_TAINT.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(FluidBucketIngredient.of(Fluids.REDMUD.get()))
                .requires(ModItems.POWDER_SPARK_MIX.get())
                .requires(ModItems.POWDER_MAGIC.get())
                .unlockedBy("has_missile_assembly", has.apply(ModItems.MISSILE_ASSEMBLY.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_TAINT.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(FluidTankIngredient.of(Fluids.REDMUD.get()))
                .requires(ModItems.POWDER_SPARK_MIX.get())
                .requires(ModItems.POWDER_MAGIC.get())
                .unlockedBy("has_missile_assembly", has.apply(ModItems.MISSILE_ASSEMBLY.get()))
                .save(writer, MODID + ":missile_taint_tank");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_MICRO.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModAmmoItems.AMMO_NUKE_HIGH.get())
                .unlockedBy("has_missile_assembly", has.apply(ModItems.MISSILE_ASSEMBLY.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_BHOLE.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModItems.GRENADE_BLACK_HOLE.get())
                .unlockedBy("has_missile_assembly", has.apply(ModItems.MISSILE_ASSEMBLY.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_SCHRABIDIUM.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModItems.CELL_ANTI_SCHRABIDIUM.get())
                .requires(ModItemTags.ANY_HARDPLASTIC_INGOT)
                .unlockedBy("has_missile_assembly", has.apply(ModItems.MISSILE_ASSEMBLY.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_EMP.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModBlocks.EMP_BOMB.get().asItem())
                .unlockedBy("has_missile_assembly", has.apply(ModItems.MISSILE_ASSEMBLY.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_10_FLAT.get(), 1)
                .pattern("PSP")
                .pattern("P P")
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_steel_plate", has.apply(ModItems.PLATE_STEEL.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_10_CRUISE.get(), 1)
                .pattern("ASA")
                .pattern(" S ")
                .pattern("PSP")
                .define('A', ModItems.PLATE_TITANIUM.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_titanium_plate", has.apply(ModItems.PLATE_TITANIUM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_10_SPACE.get(), 1)
                .pattern("ASA")
                .pattern("PSP")
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('P', ModItems.INGOT_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_aluminium_plate", has.apply(ModItems.PLATE_ALUMINIUM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_15_FLAT.get(), 1)
                .pattern("ASA")
                .pattern("PSP")
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_aluminium_plate", has.apply(ModItems.PLATE_ALUMINIUM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_15_THIN.get(), 1)
                .pattern("A A")
                .pattern("PSP")
                .pattern("PSP")
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_aluminium_plate", has.apply(ModItems.PLATE_ALUMINIUM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_THRUSTER_15_BALEFIRE_LARGE_RAD.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItems.PLATE_CAST_COPPER.get())
                .define('T', ModItems.MP_THRUSTER_15_BALEFIRE_LARGE.get())
                .unlockedBy("has_copper_cast_plate", has.apply(ModItems.PLATE_CAST_COPPER.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_KEROSENE_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_10_KEROSENE.get())
                .unlockedBy("has_kerosene_fuselage", has.apply(ModItems.MP_FUSELAGE_10_KEROSENE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_LONG_KEROSENE_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get())
                .unlockedBy("has_long_kerosene_fuselage", has.apply(ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_15_KEROSENE_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_15_KEROSENE.get())
                .unlockedBy("has_kerosene_fuselage_15", has.apply(ModItems.MP_FUSELAGE_15_KEROSENE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_SOLID_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_10_SOLID.get())
                .unlockedBy("has_solid_fuselage", has.apply(ModItems.MP_FUSELAGE_10_SOLID.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_LONG_SOLID_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_10_LONG_SOLID.get())
                .unlockedBy("has_long_solid_fuselage", has.apply(ModItems.MP_FUSELAGE_10_LONG_SOLID.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_15_SOLID_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_15_SOLID.get())
                .unlockedBy("has_solid_fuselage_15", has.apply(ModItems.MP_FUSELAGE_15_SOLID.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_15_SOLID_DESH.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItems.INGOT_DESH.get())
                .define('T', ModItems.MP_FUSELAGE_15_SOLID.get())
                .unlockedBy("has_desh_ingot", has.apply(ModItems.INGOT_DESH.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_KEROSENE_METAL.get(), 1)
                .pattern("ICI")
                .pattern("CTC")
                .pattern("ICI")
                .define('C', ModItems.PLATE_STEEL.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItems.MP_FUSELAGE_10_KEROSENE.get())
                .unlockedBy("has_kerosene_fuselage", has.apply(ModItems.MP_FUSELAGE_10_KEROSENE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_LONG_KEROSENE_METAL.get(), 1)
                .pattern("ICI")
                .pattern("CTC")
                .pattern("ICI")
                .define('C', ModItems.PLATE_STEEL.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get())
                .unlockedBy("has_long_kerosene_fuselage", has.apply(ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_15_KEROSENE_METAL.get(), 1)
                .pattern("ICI")
                .pattern("CTC")
                .pattern("ICI")
                .define('C', ModItems.PLATE_STEEL.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItems.MP_FUSELAGE_15_KEROSENE.get())
                .unlockedBy("has_kerosene_fuselage_15", has.apply(ModItems.MP_FUSELAGE_15_KEROSENE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_WARHEAD_15_BOXCAR.get(), 1)
                .pattern("SNS")
                .pattern("CBC")
                .pattern("SFS")
                .define('S', ModItems.INGOT_STARMETAL.get())
                .define('N', ModBlocks.DET_NUKE.get().asItem())
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('B', ModBlocks.BOXCAR.get().asItem())
                .define('F', ModItems.TRITIUM_DEUTERIUM_CAKE.get())
                .unlockedBy("has_advanced_circuit", has.apply(ModItems.CIRCUIT_ADVANCED.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_1.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_VACUUM_TUBE.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_vacuum_tube", has.apply(ModItems.CIRCUIT_VACUUM_TUBE.get()))
                .save(writer, MODID + ":mp_chip_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_2.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_ANALOG.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_analog_circuit", has.apply(ModItems.CIRCUIT_ANALOG.get()))
                .save(writer, MODID + ":mp_chip_2");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_3.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_basic_circuit", has.apply(ModItems.CIRCUIT_BASIC.get()))
                .save(writer, MODID + ":mp_chip_3");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_4.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_advanced_circuit", has.apply(ModItems.CIRCUIT_ADVANCED.get()))
                .save(writer, MODID + ":mp_chip_4");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_5.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_BISMOID.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_bismoid_circuit", has.apply(ModItems.CIRCUIT_BISMOID.get()))
                .save(writer, MODID + ":mp_chip_5");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TURRET_SENTRY.get(), 1)
                .pattern("PPL")
                .pattern(" MD")
                .pattern(" SC")
                .define('P', ModItems.PLATE_STEEL.get())
                .define('M', ModItems.MOTOR.get())
                .define('L', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('D', ModItems.CRT_DISPLAY.get())
                .unlockedBy("has_basic_circuit", has.apply(ModItems.CIRCUIT_BASIC.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FIREEXT.get(), 1)
                .pattern("HB")
                .pattern(" T")
                .define('H', ModItems.PIPE_STEEL.get())
                .define('B', ModItems.BOLT_STEEL.get())
                .define('T', ModItems.TANK_STEEL.get())
                .unlockedBy("has_steel_tank", has.apply(ModItems.TANK_STEEL.get()))
                .save(writer, MODID + ":gun_fireext");



        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ASSEMBLY_NUKE.get(), 1)
                .pattern(" WP")
                .pattern("SEP")
                .pattern(" WP")
                .define('W', ModItems.WIRE_GOLD.get())
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('S', ModItems.SHELL_WEAPON_STEEL.get())
                .define('E', ModItems.BALL_TATB.get())
                .unlockedBy("has_tatb_ball", has.apply(ModItems.BALL_TATB.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL.get(), 4)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', Blocks.TNT.asItem())
                .define('G', Items.GUNPOWDER)
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_steel_shell", has.apply(ModItems.SHELL_STEEL.get()))
                .save(writer, MODID + ":ammo_shell_gunpowder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL.get(), 4)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', Blocks.TNT.asItem())
                .define('G', ModItems.BALLISTITE.get())
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has.apply(ModItems.BALLISTITE.get()))
                .save(writer, MODID + ":ammo_shell_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL.get(), 6)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', Blocks.TNT.asItem())
                .define('G', ModItems.CORDITE.get())
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has.apply(ModItems.CORDITE.get()))
                .save(writer, MODID + ":ammo_shell_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_EXPLOSIVE.get(), 4)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT)
                .define('G', Items.GUNPOWDER)
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_plastic_explosive", hasTag(ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT))
                .save(writer, MODID + ":ammo_shell_explosive_gunpowder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_EXPLOSIVE.get(), 4)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT)
                .define('G', ModItems.BALLISTITE.get())
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has.apply(ModItems.BALLISTITE.get()))
                .save(writer, MODID + ":ammo_shell_explosive_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_EXPLOSIVE.get(), 6)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT)
                .define('G', ModItems.CORDITE.get())
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has.apply(ModItems.CORDITE.get()))
                .save(writer, MODID + ":ammo_shell_explosive_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_T.get(), 4)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_TUNGSTEN.get())
                .define('G', Items.GUNPOWDER)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_tungsten_ingot", has.apply(ModItems.INGOT_TUNGSTEN.get()))
                .save(writer, MODID + ":ammo_shell_apfsds_t_gunpowder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_T.get(), 4)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_TUNGSTEN.get())
                .define('G', ModItems.BALLISTITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has.apply(ModItems.BALLISTITE.get()))
                .save(writer, MODID + ":ammo_shell_apfsds_t_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_T.get(), 6)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_TUNGSTEN.get())
                .define('G', ModItems.CORDITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has.apply(ModItems.CORDITE.get()))
                .save(writer, MODID + ":ammo_shell_apfsds_t_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_DU.get(), 4)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_U238.get())
                .define('G', Items.GUNPOWDER)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_u238_ingot", has.apply(ModItems.INGOT_U238.get()))
                .save(writer, MODID + ":ammo_shell_apfsds_du_gunpowder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_DU.get(), 4)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_U238.get())
                .define('G', ModItems.BALLISTITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has.apply(ModItems.BALLISTITE.get()))
                .save(writer, MODID + ":ammo_shell_apfsds_du_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_DU.get(), 6)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_U238.get())
                .define('G', ModItems.CORDITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has.apply(ModItems.CORDITE.get()))
                .save(writer, MODID + ":ammo_shell_apfsds_du_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_W9.get(), 1)
                .pattern(" P ")
                .pattern("NSN")
                .pattern(" P ")
                .define('P', ModItems.NUGGET_PU239.get())
                .define('N', ModItems.NEUTRON_REFLECTOR.get())
                .define('S', ModAmmoItems.AMMO_SHELL_EXPLOSIVE.get())
                .unlockedBy("has_plutonium_nugget", has.apply(ModItems.NUGGET_PU239.get()))
                .save(writer, MODID + ":ammo_shell_w9");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_STANDARD.get(), 1)
                .pattern("CIC")
                .pattern("CSC")
                .pattern("CCC")
                .define('C', ModItems.CORDITE.get())
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('S', ModItems.SHELL_COPPER.get())
                .unlockedBy("has_cordite", has.apply(ModItems.CORDITE.get()))
                .save(writer, MODID + ":ammo_arty_standard");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_HE.get(), 1)
                .pattern(" D ")
                .pattern("DSD")
                .pattern(" D ")
                .define('D', ModItems.BALL_DYNAMITE.get())
                .define('S', ModAmmoItems.AMMO_ARTY_STANDARD.get())
                .unlockedBy("has_dynamite_ball", has.apply(ModItems.BALL_DYNAMITE.get()))
                .save(writer, MODID + ":ammo_arty_he");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_HE2.get(), 1)
                .pattern("TTT")
                .pattern("TST")
                .pattern("TTT")
                .define('T', ModItems.BALL_TNT.get())
                .define('S', ModAmmoItems.AMMO_ARTY_STANDARD.get())
                .unlockedBy("has_tnt_ball", has.apply(ModItems.BALL_TNT.get()))
                .save(writer, MODID + ":ammo_arty_he2");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_WP.get(), 1)
                .pattern("D")
                .pattern("S")
                .pattern("D")
                .define('D', ModItems.INGOT_PHOSPHORUS.get())
                .define('S', ModAmmoItems.AMMO_ARTY_STANDARD.get())
                .unlockedBy("has_white_phosphorus", has.apply(ModItems.INGOT_PHOSPHORUS.get()))
                .save(writer, MODID + ":ammo_arty_wp");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_WP_ENHANCED.get(), 1)
                .pattern("DSD")
                .pattern("SCS")
                .pattern("DSD")
                .define('D', ModItems.INGOT_PHOSPHORUS.get())
                .define('S', ModAmmoItems.AMMO_ARTY_WP.get())
                .define('C', ModBlocks.DET_CORD.get().asItem())
                .unlockedBy("has_white_phosphorus", has.apply(ModItems.INGOT_PHOSPHORUS.get()))
                .save(writer, MODID + ":ammo_arty_wp_enhanced");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_NUKE.get(), 1)
                .pattern(" P ")
                .pattern("NSN")
                .pattern(" P ")
                .define('P', ModItems.NUGGET_PU239.get())
                .define('N', ModItems.NEUTRON_REFLECTOR.get())
                .define('S', ModAmmoItems.AMMO_ARTY_STANDARD.get())
                .unlockedBy("has_plutonium_nugget", has.apply(ModItems.NUGGET_PU239.get()))
                .save(writer, MODID + ":ammo_arty_nuke");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_NUKE_ENHANCED.get(), 1)
                .pattern("DSD")
                .pattern("SCS")
                .pattern("DSD")
                .define('D', ModItems.NEUTRON_REFLECTOR.get())
                .define('S', ModAmmoItems.AMMO_ARTY_NUKE.get())
                .define('C', ModBlocks.DET_CORD.get().asItem())
                .unlockedBy("has_plutonium_nugget", has.apply(ModItems.NUGGET_PU239.get()))
                .save(writer, MODID + ":ammo_arty_nuke_enhanced");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_SINKER.get(), 1)
                .requires(ModAmmoItems.AMMO_ARTY_HE2.get())
                .requires(ModItems.BOY_BULLET.get())
                .requires(ModItems.BOY_TARGET.get())
                .requires(ModItems.BOY_SHIELDING.get())
                .requires(ModItems.CIRCUIT_CONTROLLER.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_controller_circuit", has.apply(ModItems.CIRCUIT_CONTROLLER.get()))
                .save(writer, MODID + ":ammo_arty_sinker");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_MIRV.get(), 1)
                .pattern(" I ")
                .pattern(" S ")
                .pattern("CCC")
                .define('C', ModItems.CORDITE.get())
                .define('I', ModItems.SPHERE_STEEL.get())
                .define('S', ModItems.SHELL_COPPER.get())
                .unlockedBy("has_steel_sphere", has.apply(ModItems.SPHERE_STEEL.get()))
                .save(writer, MODID + ":ammo_arty_mirv");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_DGK.get(), 1)
                .pattern("LLL")
                .pattern("GGG")
                .pattern("CCC")
                .define('L', ModItems.PLATE_LEAD.get())
                .define('G', ModItems.BALLISTITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has.apply(ModItems.BALLISTITE.get()))
                .save(writer, MODID + ":ammo_dgk_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_DGK.get(), 1)
                .pattern("LLL")
                .pattern("GGG")
                .pattern("CCC")
                .define('L', ModItems.PLATE_LEAD.get())
                .define('G', ModItems.CORDITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has.apply(ModItems.CORDITE.get()))
                .save(writer, MODID + ":ammo_dgk_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.MINE_AP.get(), 4)
                .pattern("I")
                .pattern("C")
                .pattern("S")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('C', ModItemTags.ANY_SMOKELESS_DUST)
                .define('S', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_polymer_plate", has.apply(ModItems.PLATE_POLYMER.get()))
                .save(writer, MODID + ":mine_ap");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.MINE_SHRAP.get(), 1)
                .pattern("L")
                .pattern("M")
                .define('M', ModBlocks.MINE_AP.get().asItem())
                .define('L', ModItems.PELLET_BUCKSHOT.get())
                .unlockedBy("has_ap_mine", has.apply(ModBlocks.MINE_AP.get().asItem()))
                .save(writer, MODID + ":mine_shrap");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.MINE_HE.get(), 1)
                .pattern(" C ")
                .pattern("PTP")
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('T', ModItemTags.ANY_HIGHEXPLOSIVE)
                .unlockedBy("has_basic_circuit", has.apply(ModItems.CIRCUIT_BASIC.get()))
                .save(writer, MODID + ":mine_he");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.MINE_FAT.get(), 1)
                .pattern("CDN")
                .define('C', ModItems.CIRCUIT_ANALOG.get())
                .define('D', ModItems.DUCTTAPE.get())
                .define('N', ModAmmoItems.AMMO_NUKE_DEMO.get())
                .unlockedBy("has_analog_circuit", has.apply(ModItems.CIRCUIT_ANALOG.get()))
                .save(writer, MODID + ":mine_fat");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.N2_CHARGE.get(), 1)
                .pattern(" D ")
                .pattern("ERE")
                .pattern(" D ")
                .define('D', ModItems.DUCTTAPE.get())
                .define('E', ModBlocks.DET_CHARGE.get().asItem())
                .define('R', Blocks.REDSTONE_BLOCK.asItem())
                .unlockedBy("has_det_charge", has.apply(ModBlocks.DET_CHARGE.get().asItem()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_TNT.get(), 1)
                .pattern(" C ")
                .pattern("TIT")
                .pattern("TIT")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItemTags.ANY_HIGHEXPLOSIVE)
                .unlockedBy("has_high_explosive", hasTag(ModItemTags.ANY_HIGHEXPLOSIVE))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_NUKE.get(), 1)
                .pattern(" C ")
                .pattern("LUL")
                .pattern("LUL")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('U', ModItems.INGOT_U235.get())
                .unlockedBy("has_u235_ingot", has.apply(ModItems.INGOT_U235.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_HYDRO.get(), 1)
                .pattern(" C ")
                .pattern("LTL")
                .pattern("LIL")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItems.CELL_TRITIUM.get())
                .unlockedBy("has_tritium_cell", has.apply(ModItems.CELL_TRITIUM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_AMAT.get(), 1)
                .pattern(" C ")
                .pattern("MMM")
                .pattern("AAA")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('M', ModItems.CELL_ANTIMATTER.get())
                .unlockedBy("has_antimatter_cell", has.apply(ModItems.CELL_ANTIMATTER.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_DIRTY.get(), 1)
                .pattern(" C ")
                .pattern("WLW")
                .pattern("WLW")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('W', ModItems.NUCLEAR_WASTE.get())
                .unlockedBy("has_nuclear_waste", has.apply(ModItems.NUCLEAR_WASTE.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_SCHRAB.get(), 1)
                .pattern(" C ")
                .pattern("LUL")
                .pattern("LUL")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('U', ModItems.INGOT_SCHRABIDIUM.get())
                .unlockedBy("has_schrabidium_ingot", has.apply(ModItems.INGOT_SCHRABIDIUM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LAMP_DEMON.get(), 1)
                .pattern(" D ")
                .pattern("S S")
                .define('D', ModItems.DEMON_CORE_CLOSED.get())
                .define('S', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_demon_core", has.apply(ModItems.DEMON_CORE_CLOSED.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModToolItems.CRUCIBLE.get(), 1)
                .pattern("MEM")
                .pattern("YDY")
                .pattern("YCY")
                .define('M', ModItems.INGOT_METEORITE_FORGED.get())
                .define('E', ModItems.INGOT_EUPHEMIUM.get())
                .define('Y', ModItems.BILLET_YHARONITE.get())
                .define('D', ModItems.DEMON_CORE_CLOSED.get())
                .define('C', ModItems.INGOT_CHAINSTEEL.get())
                .unlockedBy("has_demon_core", has.apply(ModItems.DEMON_CORE_CLOSED.get()))
                .save(writer, MODID + ":crucible");
    }

    private static void addNitraRecipe(Consumer<FinishedRecipe> writer, net.minecraft.world.item.Item ammo, int count) {
        String ammoName = ammo.getDescriptionId().replace("item.hbm.", "");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ammo, count)
                .requires(ammo)
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_" + ammoName, has(ammo))
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(writer, "hbm:ammo/" + ammoName + "_nitra");
    }

    private static InventoryChangeTrigger.TriggerInstance has(Item item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }
}
