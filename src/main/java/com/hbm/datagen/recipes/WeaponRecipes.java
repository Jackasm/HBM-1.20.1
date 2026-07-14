package com.hbm.datagen.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.recipes.ingredient.FluidBucketIngredient;
import com.hbm.datagen.recipes.ingredient.FluidTankIngredient;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.*;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

import static com.hbm.util.RefStrings.MODID;

public class WeaponRecipes extends ModRecipeProvider {
    public WeaponRecipes(PackOutput pOutput) {super(pOutput);}

    public static void generateWeaponRecipes(Consumer<FinishedRecipe> pWriter) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MACHINE_WEAPON_TABLE.get(), 1)
                .pattern("PPP")
                .pattern("TCT")
                .pattern("TST")
                .define('P', ModItems.PLATE_GUNMETAL.get())
                .define('T', ModItems.INGOT_STEEL.get())
                .define('C', Blocks.CRAFTING_TABLE)
                .define('S', ModBlocks.BLOCK_STEEL.get().asItem())
                .unlockedBy("has_gunmetal_plate", has(ModItems.PLATE_GUNMETAL.get()))
                .save(pWriter, MODID + ":machine_weapon_table");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_WOOD.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ItemTags.PLANKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_WOOD.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ItemTags.PLANKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_POLYMER.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ModItems.INGOT_POLYMER.get())
                .unlockedBy("has_polymer_ingot", has(ModItems.INGOT_POLYMER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_POLYMER.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_POLYMER.get())
                .unlockedBy("has_polymer_ingot", has(ModItems.INGOT_POLYMER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_BAKELITE.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ModItems.INGOT_BAKELITE.get())
                .unlockedBy("has_bakelite_ingot", has(ModItems.INGOT_BAKELITE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_BAKELITE.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_BAKELITE.get())
                .unlockedBy("has_bakelite_ingot", has(ModItems.INGOT_BAKELITE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_POLYCARBONATE.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ModItems.INGOT_PC.get())
                .unlockedBy("has_hardplastic_ingot", has(ModItems.INGOT_PC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_POLYCARBONATE.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_PC.get())
                .unlockedBy("has_hardplastic_ingot", has(ModItems.INGOT_PC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_STOCK_PVC.get(), 1)
                .pattern("WWW")
                .pattern("  W")
                .define('W', ModItems.INGOT_PVC.get())
                .unlockedBy("has_pvc_ingot", has(ModItems.INGOT_PVC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_PVC.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_PVC.get())
                .unlockedBy("has_pvc_ingot", has(ModItems.INGOT_PVC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_RUBBER.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', ModItems.INGOT_RUBBER.get())
                .unlockedBy("has_rubber_ingot", has(ModItems.INGOT_RUBBER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PART_GRIP_IVORY.get(), 1)
                .pattern("W ")
                .pattern(" W")
                .pattern(" W")
                .define('W', Items.BONE)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  ModItems.CASING_SHOTSHELL.get(), 2)
                .pattern("P")
                .pattern("C")
                .define('P', ModItems.PLATE_GUNMETAL.get())
                .define('C', ModItems.CASING_LARGE.get())
                .unlockedBy("has_gunmetal_plate", has(ModItems.PLATE_GUNMETAL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CASING_BUCKSHOT.get(), 2)
                .pattern("P")
                .pattern("C")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModItems.CASING_LARGE.get())
                .unlockedBy("has_plastic_ingot", has(ModItemTags.ANY_PLASTIC_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CASING_BUCKSHOT_ADVANCED.get(), 2)
                .pattern("P")
                .pattern("C")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModItems.CASING_LARGE_STEEL.get())
                .unlockedBy("has_plastic_ingot", has(ModItemTags.ANY_PLASTIC_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_PEPPERBOX.get(), 1)
                .pattern("IIW")
                .pattern("  C")
                .define('I', Items.IRON_INGOT)
                .define('W', ItemTags.PLANKS)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(pWriter, MODID + ":gun_pepperbox");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LIGHT_REVOLVER.get(), 1)
                .pattern("BRM")
                .pattern("  G")
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .unlockedBy("has_steel_light_barrel", has(ModItems.LIGHT_BARREL_STEEL.get()))
                .save(pWriter, MODID + ":gun_light_revolver");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LIGHT_REVOLVER_ATLAS.get(), 1)
                .pattern(" M ")
                .pattern("MAM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('A', ModGunItems.GUN_LIGHT_REVOLVER.get())
                .unlockedBy("has_weapon_steel_mechanism", has(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_light_revolver_atlas");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_HENRY.get(), 1)
                .pattern("BRP")
                .pattern("BMS")
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_GUNMETAL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .define('P', ModItems.PLATE_GUNMETAL.get())
                .unlockedBy("has_gunmetal_plate", has(ModItems.PLATE_GUNMETAL.get()))
                .save(pWriter, MODID + ":gun_henry");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_HENRY_LINCOLN.get(), 1)
                .pattern(" M ")
                .pattern("PGP")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('P', ModItems.PLATE_CAST_GOLD.get())
                .define('G', ModGunItems.GUN_HENRY.get())
                .unlockedBy("has_weapon_steel_mechanism", has(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_henry_lincoln");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_GREASEGUN.get(), 1)
                .pattern("BRS")
                .pattern("SMG")
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_STEEL.get())
                .define('S', ModItems.BOLT_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_STEEL.get())
                .unlockedBy("has_steel_bolt", has(ModItems.BOLT_STEEL.get()))
                .save(pWriter, MODID + ":gun_greasegun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_MARESLEG.get(), 1)
                .pattern("BRM")
                .pattern("BGS")
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.BOLT_STEEL.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_steel_bolt", has(ModItems.BOLT_STEEL.get()))
                .save(pWriter, MODID + ":gun_maresleg");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FLAREGUN.get(), 1)
                .pattern("BRM")
                .pattern("  G")
                .define('B', ModItems.HEAVY_BARREL_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_STEEL.get())
                .unlockedBy("has_steel_heavy_barrel", has(ModItems.HEAVY_BARREL_STEEL.get()))
                .save(pWriter, MODID + ":gun_flaregun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_AM180.get(), 1)
                .pattern("BRS")
                .pattern("GMG")
                .define('B', ModItems.LIGHT_BARREL_DURA_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_DURA_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_dura_steel_light_barrel", has(ModItems.LIGHT_BARREL_DURA_STEEL.get()))
                .save(pWriter, MODID + ":gun_am180");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LIBERATOR.get(), 1)
                .pattern("BB ")
                .pattern("BBM")
                .pattern("G G")
                .define('B', ModItems.LIGHT_BARREL_DURA_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .unlockedBy("has_dura_steel_light_barrel", has(ModItems.LIGHT_BARREL_DURA_STEEL.get()))
                .save(pWriter, MODID + ":gun_liberator");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CONGOLAKE.get(), 1)
                .pattern("BM ")
                .pattern("BRS")
                .pattern("G  ")
                .define('B', ModItems.HEAVY_BARREL_DURA_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('R', ModItems.LIGHT_RECEIVER_DURA_STEEL.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .unlockedBy("has_dura_steel_heavy_barrel", has(ModItems.HEAVY_BARREL_DURA_STEEL.get()))
                .save(pWriter, MODID + ":gun_congolake");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FLAMER.get(), 1)
                .pattern(" MG")
                .pattern("BBR")
                .pattern(" GM")
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_DURA_STEEL.get())
                .define('B', ModItems.HEAVY_BARREL_DURA_STEEL.get())
                .define('R', ModItems.HEAVY_RECEIVER_DURA_STEEL.get())
                .unlockedBy("has_dura_steel_heavy_barrel", has(ModItems.HEAVY_BARREL_DURA_STEEL.get()))
                .save(pWriter, MODID + ":gun_flamer");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FLAMER_TOPAZ.get(), 1)
                .pattern(" M ")
                .pattern("MFM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('F', ModGunItems.GUN_FLAMER.get())
                .unlockedBy("has_weapon_steel_mechanism", has(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_flamer_topaz");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_HEAVY_REVOLVER.get(), 1)
                .pattern("BRM")
                .pattern("  G")
                .define('B', ModItems.LIGHT_BARREL_DESH.get())
                .define('R', ModItems.LIGHT_RECEIVER_DESH.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .unlockedBy("has_desh_light_barrel", has(ModItems.LIGHT_BARREL_DESH.get()))
                .save(pWriter, MODID + ":gun_heavy_revolver");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CARBINE.get(), 1)
                .pattern("BRM")
                .pattern("G S")
                .define('B', ModItems.LIGHT_BARREL_DESH.get())
                .define('R', ModItems.LIGHT_RECEIVER_DESH.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_desh_light_barrel", has(ModItems.LIGHT_BARREL_DESH.get()))
                .save(pWriter, MODID + ":gun_carbine");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_UZI.get(), 1)
                .pattern("BRS")
                .pattern(" GM")
                .define('B', ModItems.LIGHT_BARREL_DESH.get())
                .define('R', ModItems.LIGHT_RECEIVER_DESH.get())
                .define('S', ModItemTags.ANY_PLASTIC_STOCK)
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .unlockedBy("has_desh_light_barrel", has(ModItems.LIGHT_BARREL_DESH.get()))
                .save(pWriter, MODID + ":gun_uzi");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_SPAS12.get(), 1)
                .pattern("BRM")
                .pattern("BGS")
                .define('B', ModItems.LIGHT_BARREL_DESH.get())
                .define('R', ModItems.LIGHT_RECEIVER_DESH.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .define('S', ModItems.PART_STOCK_DESH.get())
                .unlockedBy("has_desh_light_barrel", has(ModItems.LIGHT_BARREL_DESH.get()))
                .save(pWriter, MODID + ":gun_spas12");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_PANZERSCHRECK.get(), 1)
                .pattern("BBB")
                .pattern("PGM")
                .define('B', ModItems.HEAVY_BARREL_DESH.get())
                .define('P', ModItems.PLATE_CAST_STEEL.get())
                .define('G', ModItems.PART_GRIP_DESH.get())
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .unlockedBy("has_desh_heavy_barrel", has(ModItems.HEAVY_BARREL_DESH.get()))
                .save(pWriter, MODID + ":gun_panzerschreck");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_G3.get(), 1)
                .pattern("BRM")
                .pattern("WGS")
                .define('B', ModItems.LIGHT_BARREL_WEAPON_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('W', ModItems.PART_GRIP_WOOD.get())
                .define('G', ModItems.PART_GRIP_RUBBER.get())
                .define('S', ModItems.PART_STOCK_WOOD.get())
                .unlockedBy("has_weapon_steel_light_barrel", has(ModItems.LIGHT_BARREL_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_g3");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_G3_ZEBRA.get(), 1)
                .pattern(" M ")
                .pattern("MPM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('P', ModGunItems.GUN_G3.get())
                .unlockedBy("has_weapon_steel_mechanism", has(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_g3_zebra");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_STINGER.get(), 1)
                .pattern("BBB")
                .pattern("PGM")
                .define('B', ModItems.HEAVY_BARREL_WEAPON_STEEL.get())
                .define('P', ModItems.CIRCUIT_ADVANCED.get())
                .define('G', ModItems.PART_GRIP_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .unlockedBy("has_weapon_steel_heavy_barrel", has(ModItems.HEAVY_BARREL_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_stinger");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CHEMTHROWER.get(), 1)
                .pattern("MHW")
                .pattern("PSS")
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('H', ModItems.PIPE_RUBBER.get())
                .define('W', ModToolItems.WRENCH.get())
                .define('P', ModItems.HEAVY_BARREL_WEAPON_STEEL.get())
                .define('S', ModItems.SHELL_WEAPON_STEEL.get())
                .unlockedBy("has_weapon_steel_heavy_barrel", has(ModItems.HEAVY_BARREL_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_chemthrower");

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
                .unlockedBy("has_ferrouranium_heavy_barrel", has(ModItems.HEAVY_BARREL_FERROURANIUM.get()))
                .save(pWriter, MODID + ":gun_amat");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_M2.get(), 1)
                .pattern("  G")
                .pattern("BRM")
                .pattern("  G")
                .define('G', ModItems.PART_GRIP_WOOD.get())
                .define('B', ModItems.HEAVY_BARREL_FERROURANIUM.get())
                .define('R', ModItems.HEAVY_RECEIVER_FERROURANIUM.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .unlockedBy("has_ferrouranium_heavy_barrel", has(ModItems.HEAVY_BARREL_FERROURANIUM.get()))
                .save(pWriter, MODID + ":gun_m2");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_AUTOSHOTGUN.get(), 1)
                .pattern("BRM")
                .pattern("G G")
                .define('B', ModItems.HEAVY_BARREL_FERROURANIUM.get())
                .define('R', ModItems.HEAVY_RECEIVER_FERROURANIUM.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_ferrouranium_heavy_barrel", has(ModItems.HEAVY_BARREL_FERROURANIUM.get()))
                .save(pWriter, MODID + ":gun_autoshotgun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_AUTOSHOTGUN_SHREDDER.get(), 1)
                .pattern(" M ")
                .pattern("MAM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('A', ModGunItems.GUN_AUTOSHOTGUN.get())
                .unlockedBy("has_gunmetal_mechanism", has(ModItems.GUN_MECHANISM_GUNMETAL.get()))
                .save(pWriter, MODID + ":gun_autoshotgun_shredder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_QUADRO.get(), 1)
                .pattern("BCB")
                .pattern("BMB")
                .pattern("GG ")
                .define('B', ModItems.HEAVY_BARREL_FERROURANIUM.get())
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_ferrouranium_heavy_barrel", has(ModItems.HEAVY_BARREL_FERROURANIUM.get()))
                .save(pWriter, MODID + ":gun_quadro");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LAG.get(), 1)
                .pattern("BRM")
                .pattern("  G")
                .define('B', ModItemTags.ANY_RESISTANTALLOY_LIGHT_BARREL)
                .define('R', ModItemTags.ANY_RESISTANTALLOY_LIGHT_RECEIVER)
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_weapon_steel_mechanism", has(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_lag");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_MINIGUN.get(), 1)
                .pattern("BMG")
                .pattern("BRE")
                .pattern("BGM")
                .define('B', ModItemTags.ANY_RESISTANTALLOY_LIGHT_BARREL)
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .define('R', ModItemTags.ANY_RESISTANTALLOY_HEAVY_RECEIVER)
                .define('E', ModItems.MOTOR_DESH.get())
                .unlockedBy("has_desh_motor", has(ModItems.MOTOR_DESH.get()))
                .save(pWriter, MODID + ":gun_minigun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_MISSILE_LAUNCHER.get(), 1)
                .pattern(" CM")
                .pattern("BBB")
                .pattern("G  ")
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('B', ModItemTags.ANY_RESISTANTALLOY_HEAVY_BARREL)
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_weapon_steel_mechanism", has(ModItems.GUN_MECHANISM_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":gun_missile_launcher");

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
                .unlockedBy("has_advanced_alloy_coil", has(ModItems.COIL_ADVANCED_ALLOY.get()))
                .save(pWriter, MODID + ":gun_tesla_cannon");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LASER_PISTOL.get(), 1)
                .pattern("CRM")
                .pattern("GG ")
                .define('C', ModItems.CRYSTAL_REDSTONE.get())
                .define('R', ModItems.LIGHT_RECEIVER_SATURNITE.get())
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .define('G', ModItemTags.ANY_HARDPLASTIC_GRIP)
                .unlockedBy("has_saturnite_light_receiver", has(ModItems.LIGHT_RECEIVER_SATURNITE.get()))
                .save(pWriter, MODID + ":gun_laser_pistol");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_LASER_PISTOL_PEW_PEW.get(), 1)
                .pattern(" M ")
                .pattern("MPM")
                .pattern(" M ")
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .define('P', ModGunItems.GUN_LASER_PISTOL.get())
                .unlockedBy("has_saturnite_mechanism", has(ModItems.GUN_MECHANISM_SATURNITE.get()))
                .save(pWriter, MODID + ":gun_laser_pistol_pew_pew");

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
                .unlockedBy("has_saturnite_light_barrel", has(ModItems.LIGHT_BARREL_SATURNITE.get()))
                .save(pWriter, MODID + ":gun_stg77");

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
                .unlockedBy("has_saturnite_heavy_barrel", has(ModItems.HEAVY_BARREL_SATURNITE.get()))
                .save(pWriter, MODID + ":gun_fatman");


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
                .unlockedBy("has_saturnite_light_receiver", has(ModItems.LIGHT_RECEIVER_SATURNITE.get()))
                .save(pWriter, MODID + ":gun_tau");

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
                .unlockedBy("has_bismuth_bronze_light_barrel", has(ModItems.LIGHT_BARREL_BISMUTH_BRONZE.get()))
                .save(pWriter, MODID + ":gun_lasrifle");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModGunItems.GUN_DOUBLE_BARREL_SACRED_DRAGON.get(), 1)
                .requires(ModGunItems.GUN_DOUBLE_BARREL.get())
                .requires(ModItems.SECRET_SELENIUM_STEEL.get())
                .unlockedBy("has_double_barrel", has(ModGunItems.GUN_DOUBLE_BARREL.get()))
                .unlockedBy("has_selenium_steel", has(ModItems.SECRET_SELENIUM_STEEL.get()))
                .save(pWriter, MODID + ":gun_sacred_dragon");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CHARGE_THROWER.get(), 1)
                .pattern("MMM")
                .pattern("BBL")
                .pattern("GG ")
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('B', ModItems.HEAVY_BARREL_STEEL.get())
                .define('G', ModItems.PART_GRIP_STEEL.get())
                .define('L', Items.LEATHER)
                .unlockedBy("has_gunmetal_mechanism", has(ModItems.GUN_MECHANISM_GUNMETAL.get()))
                .save(pWriter, MODID + ":gun_charge_thrower_leather");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_CHARGE_THROWER.get(), 1)
                .pattern("MMM")
                .pattern("BBL")
                .pattern("GG ")
                .define('M', ModItems.GUN_MECHANISM_GUNMETAL.get())
                .define('B', ModItems.HEAVY_BARREL_STEEL.get())
                .define('G', ModItems.PART_GRIP_STEEL.get())
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_gunmetal_mechanism", has(ModItems.GUN_MECHANISM_GUNMETAL.get()))
                .save(pWriter, MODID + ":gun_charge_thrower_rubber");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_STONE.get(), 6)
                .pattern("C")
                .pattern("P")
                .pattern("G")
                .define('C', Items.COBBLESTONE)
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .save(pWriter, MODID + ":ammo_standard_stone");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_STONE_AP.get(), 6)
                .pattern("C")
                .pattern("P")
                .pattern("G")
                .define('C', Items.FLINT)
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(pWriter, MODID + ":ammo_standard_stone_ap");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_STONE_SHOT.get(), 6)
                .pattern("C")
                .pattern("P")
                .pattern("G")
                .define('C', Items.GRAVEL.asItem())
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_gravel", has(Items.GRAVEL.asItem()))
                .save(pWriter, MODID + ":ammo_standard_stone_shot");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_STONE_IRON.get(), 6)
                .pattern("C")
                .pattern("P")
                .pattern("G")
                .define('C', Items.IRON_INGOT)
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(pWriter, MODID + ":ammo_standard_stone_iron");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_IRON_DAMAGE.get(), 1)
                .requires(ModItems.INGOT_GUNMETAL.get())
                .requires(Items.IRON_INGOT, 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_iron_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_IRON_DURA.get(), 1)
                .requires(ModItems.INGOT_GUNMETAL.get())
                .requires(Items.IRON_INGOT)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_iron_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_STEEL_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_STEEL.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_steel_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_STEEL_DURA.get(), 1)
                .requires(ModItems.PLATE_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_STEEL.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_steel_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_DURA_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_DURA.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_dura_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_DURA_DURA.get(), 1)
                .requires(ModItems.PLATE_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_DURA.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_dura_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_DESH_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_DESH.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_desh_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_DESH_DURA.get(), 1)
                .requires(ModItems.PLATE_GUNMETAL.get())
                .requires(ModItems.PLATE_CAST_DESH.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_desh_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_WSTEEL_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .requires(ModItems.PLATE_CAST_WEAPON_STEEL.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_wsteel_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_WSTEEL_DURA.get(), 1)
                .requires(ModItems.PLATE_WEAPON_STEEL.get())
                .requires(ModItems.PLATE_CAST_WEAPON_STEEL.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_wsteel_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_FERRO_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .requires(ModItems.PLATE_CAST_FERROURANIUM.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_ferro_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_FERRO_DURA.get(), 1)
                .requires(ModItems.PLATE_WEAPON_STEEL.get())
                .requires(ModItems.PLATE_CAST_FERROURANIUM.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_ferro_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_TCALLOY_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .requires(Ingredient.of(ModItemTags.ANY_RESISTANTALLOY_PLATE_CAST), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_tcalloy_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_TCALLOY_DURA.get(), 1)
                .requires(ModItems.PLATE_WEAPON_STEEL.get())
                .requires(ModItemTags.ANY_RESISTANTALLOY_PLATE_CAST)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_tcalloy_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BIGMT_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_SATURNITE.get())
                .requires(ModItems.PLATE_CAST_SATURNITE.get(), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_bigmt_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BIGMT_DURA.get(), 1)
                .requires(ModItems.PLATE_SATURNITE.get())
                .requires(ModItems.PLATE_CAST_SATURNITE.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_bigmt_dura");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BRONZE_DAMAGE.get(), 1)
                .requires(ModItems.GUN_MECHANISM_SATURNITE.get())
                .requires(Ingredient.of(ModItemTags.ANY_BISMOID_BRONZE_PLATE_CAST), 3)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_bronze_damage");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BRONZE_DURA.get(), 1)
                .requires(ModItems.PLATE_SATURNITE.get())
                .requires(ModItemTags.ANY_BISMOID_BRONZE_PLATE_CAST)
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter, MODID + ":weapon_mod_generic_bronze_dura");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SILENCER.get(), 1)
                .pattern("P")
                .pattern("B")
                .pattern("P")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('B', ModItems.LIGHT_BARREL_STEEL.get())
                .unlockedBy("has_steel_light_barrel", has(ModItems.LIGHT_BARREL_STEEL.get()))
                .save(pWriter, MODID + ":weapon_mod_special_silencer");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SCOPE.get(), 1)
                .pattern("SPS")
                .pattern("G G")
                .pattern("SPS")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('S', ModItems.PLATE_STEEL.get())
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(pWriter, MODID + ":weapon_mod_special_scope");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SAW.get(), 1)
                .pattern("BBS")
                .pattern("BHS")
                .define('B', ModItems.BOLT_STEEL.get())
                .define('S', ItemTags.PLANKS)
                .define('H', ModItems.PLATE_DURA_STEEL.get())
                .unlockedBy("has_dura_plate", has(ModItems.PLATE_DURA_STEEL.get()))
                .save(pWriter, MODID + ":weapon_mod_special_saw");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SPEEDLOADER.get(), 1)
                .pattern(" B ")
                .pattern("BSB")
                .pattern(" B ")
                .define('B', ModItems.BOLT_STEEL.get())
                .define('S', ModItems.PLATE_WEAPON_STEEL.get())
                .unlockedBy("has_weapon_steel_plate", has(ModItems.PLATE_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":weapon_mod_special_speedloader");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SLOWDOWN.get(), 1)
                .pattern(" I ")
                .pattern(" M ")
                .pattern("I I")
                .define('I', ModItems.INGOT_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .unlockedBy("has_weapon_steel_ingot", has(ModItems.INGOT_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":weapon_mod_special_slowdown");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SPEEDUP.get(), 1)
                .pattern("PIP")
                .pattern("WWW")
                .pattern("PIP")
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('I', ModItems.INGOT_GUNMETAL.get())
                .define('W', ModItems.WIRE_DENSE_GOLD.get())
                .unlockedBy("has_gold_dense_wire", has(ModItems.WIRE_DENSE_GOLD.get()))
                .save(pWriter, MODID + ":weapon_mod_special_speedup");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_GREASEGUN.get(), 1)
                .pattern("BRM")
                .pattern("P G")
                .define('B', ModItems.LIGHT_BARREL_WEAPON_STEEL.get())
                .define('R', ModItems.LIGHT_RECEIVER_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_WEAPON_STEEL.get())
                .define('P', ModItems.PLATE_DURA_STEEL.get())
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_weapon_steel_light_barrel", has(ModItems.LIGHT_BARREL_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":weapon_mod_special_greasegun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_CHOKE.get(), 1)
                .pattern("P")
                .pattern("B")
                .pattern("P")
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('B', ModItems.LIGHT_BARREL_DURA_STEEL.get())
                .unlockedBy("has_dura_steel_light_barrel", has(ModItems.LIGHT_BARREL_DURA_STEEL.get()))
                .save(pWriter, MODID + ":weapon_mod_special_choke");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_FURNITURE_GREEN.get(), 1)
                .pattern("PDS")
                .pattern("  G")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('D', Items.GREEN_DYE)
                .define('S', ModItemTags.ANY_PLASTIC_STOCK)
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_plastic_grip", has(ModItemTags.ANY_PLASTIC_GRIP))
                .save(pWriter, MODID + ":weapon_mod_special_furniture_green");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_FURNITURE_BLACK.get(), 1)
                .pattern("PDS")
                .pattern("  G")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('D', Items.BLACK_DYE)
                .define('S', ModItemTags.ANY_PLASTIC_STOCK)
                .define('G', ModItemTags.ANY_PLASTIC_GRIP)
                .unlockedBy("has_plastic_grip", has(ModItemTags.ANY_PLASTIC_GRIP))
                .save(pWriter, MODID + ":weapon_mod_special_furniture_black");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_SKIN_SATURNITE.get(), 1)
                .pattern("BRM")
                .pattern(" P ")
                .define('B', ModItems.LIGHT_BARREL_SATURNITE.get())
                .define('R', ModItems.LIGHT_RECEIVER_SATURNITE.get())
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .define('P', ModItems.PLATE_SATURNITE.get())
                .unlockedBy("has_saturnite_plate", has(ModItems.PLATE_SATURNITE.get()))
                .save(pWriter, MODID + ":weapon_mod_special_skin_saturnite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_STACK_MAG.get(), 1)
                .pattern("P P")
                .pattern("P P")
                .pattern("PMP")
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('M', ModItems.GUN_MECHANISM_SATURNITE.get())
                .unlockedBy("has_saturnite_mechanism", has(ModItems.GUN_MECHANISM_SATURNITE.get()))
                .save(pWriter, MODID + ":weapon_mod_special_stack_mag");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_BAYONET.get(), 1)
                .pattern("  P")
                .pattern("BBB")
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('B', ModItems.BOLT_STEEL.get())
                .unlockedBy("has_weapon_steel_plate", has(ModItems.PLATE_WEAPON_STEEL.get()))
                .save(pWriter, MODID + ":weapon_mod_special_bayonet");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_LAS_SHOTGUN.get(), 1)
                .pattern("PPP")
                .pattern("RCR")
                .pattern("PPP")
                .define('P', ModItemTags.ANY_HARDPLASTIC_INGOT)
                .define('R', ModItems.CRYSTAL_REDSTONE.get())
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .unlockedBy("has_advanced_circuit", has(ModItems.CIRCUIT_ADVANCED.get()))
                .save(pWriter, MODID + ":weapon_mod_special_las_shotgun");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_LAS_CAPACITOR.get(), 1)
                .pattern("CCC")
                .pattern("PIP")
                .define('C', ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get())
                .define('P', ModItemTags.ANY_HARDPLASTIC_INGOT)
                .define('I', ModItems.CIRCUIT_CHIP_BISMOID.get())
                .unlockedBy("has_tantalium_capacitor", has(ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get()))
                .save(pWriter, MODID + ":weapon_mod_special_las_capacitor");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WEAPON_MOD_LAS_AUTO.get(), 1)
                .pattern(" C ")
                .pattern("RFR")
                .pattern(" C ")
                .define('C', ModItems.CIRCUIT_CHIP_BISMOID.get())
                .define('R', ModItems.CRYSTAL_REDSTONE.get())
                .define('F', ModItemTags.ANY_BISMOID_BRONZE_HEAVY_RECEIVER)
                .unlockedBy("has_bismoid_chip", has(ModItems.CIRCUIT_CHIP_BISMOID.get()))
                .save(pWriter, MODID + ":weapon_mod_special_las_auto");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_M357_SP.get(), 6)
                .requires(ModAmmoItems.AMMO_M357_SP.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_m357_sp");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_M44_SP.get(), 6)
                .requires(ModAmmoItems.AMMO_M44_SP.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_m44_sp");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_P9_SP.get(), 12)
                .requires(ModAmmoItems.AMMO_P9_SP.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_p9_sp");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_P22_SP.get(), 32)
                .requires(ModAmmoItems.AMMO_P22_SP.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_p22_sp");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_R556_SP.get(), 8)
                .requires(ModAmmoItems.AMMO_R556_SP.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_r556_sp");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_R762_SP.get(), 6)
                .requires(ModAmmoItems.AMMO_R762_SP.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_r762_sp");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_BMG50_SP.get(), 4)
                .requires(ModAmmoItems.AMMO_BMG50_SP.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_bmg50_sp");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_G40_HE.get(), 3)
                .requires(ModAmmoItems.AMMO_G40_HE.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_g40_he");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ROCKET_HE.get(), 2)
                .requires(ModAmmoItems.AMMO_ROCKET_HE.get())
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":ammo_rocket_he");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_TAINT.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(FluidBucketIngredient.of(Fluids.REDMUD.get()))
                .requires(ModItems.POWDER_SPARK_MIX.get())
                .requires(ModItems.POWDER_MAGIC.get())
                .unlockedBy("has_missile_assembly", has(ModItems.MISSILE_ASSEMBLY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_TAINT.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(FluidTankIngredient.of(Fluids.REDMUD.get()))
                .requires(ModItems.POWDER_SPARK_MIX.get())
                .requires(ModItems.POWDER_MAGIC.get())
                .unlockedBy("has_missile_assembly", has(ModItems.MISSILE_ASSEMBLY.get()))
                .save(pWriter, MODID + ":missile_taint_tank");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_MICRO.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModAmmoItems.AMMO_NUKE_HIGH.get())
                .unlockedBy("has_missile_assembly", has(ModItems.MISSILE_ASSEMBLY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_BHOLE.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModItems.GRENADE_BLACK_HOLE.get())
                .unlockedBy("has_missile_assembly", has(ModItems.MISSILE_ASSEMBLY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_SCHRABIDIUM.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModItems.CELL_ANTI_SCHRABIDIUM.get())
                .requires(ModItemTags.ANY_HARDPLASTIC_INGOT)
                .unlockedBy("has_missile_assembly", has(ModItems.MISSILE_ASSEMBLY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.MISSILE_EMP.get(), 1)
                .requires(ModItems.MISSILE_ASSEMBLY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModBlocks.EMP_BOMB.get().asItem())
                .unlockedBy("has_missile_assembly", has(ModItems.MISSILE_ASSEMBLY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_10_FLAT.get(), 1)
                .pattern("PSP")
                .pattern("P P")
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_10_CRUISE.get(), 1)
                .pattern("ASA")
                .pattern(" S ")
                .pattern("PSP")
                .define('A', ModItems.PLATE_TITANIUM.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_titanium_plate", has(ModItems.PLATE_TITANIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_10_SPACE.get(), 1)
                .pattern("ASA")
                .pattern("PSP")
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('P', ModItems.INGOT_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_15_FLAT.get(), 1)
                .pattern("ASA")
                .pattern("PSP")
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_STABILITY_15_THIN.get(), 1)
                .pattern("A A")
                .pattern("PSP")
                .pattern("PSP")
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_THRUSTER_15_BALEFIRE_LARGE_RAD.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItems.PLATE_CAST_COPPER.get())
                .define('T', ModItems.MP_THRUSTER_15_BALEFIRE_LARGE.get())
                .unlockedBy("has_copper_cast_plate", has(ModItems.PLATE_CAST_COPPER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_KEROSENE_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_10_KEROSENE.get())
                .unlockedBy("has_kerosene_fuselage", has(ModItems.MP_FUSELAGE_10_KEROSENE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_LONG_KEROSENE_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get())
                .unlockedBy("has_long_kerosene_fuselage", has(ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_15_KEROSENE_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_15_KEROSENE.get())
                .unlockedBy("has_kerosene_fuselage_15", has(ModItems.MP_FUSELAGE_15_KEROSENE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_SOLID_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_10_SOLID.get())
                .unlockedBy("has_solid_fuselage", has(ModItems.MP_FUSELAGE_10_SOLID.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_LONG_SOLID_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_10_LONG_SOLID.get())
                .unlockedBy("has_long_solid_fuselage", has(ModItems.MP_FUSELAGE_10_LONG_SOLID.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_15_SOLID_INSULATION.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItemTags.ANY_RUBBER_INGOT)
                .define('T', ModItems.MP_FUSELAGE_15_SOLID.get())
                .unlockedBy("has_solid_fuselage_15", has(ModItems.MP_FUSELAGE_15_SOLID.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_15_SOLID_DESH.get(), 1)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', ModItems.INGOT_DESH.get())
                .define('T', ModItems.MP_FUSELAGE_15_SOLID.get())
                .unlockedBy("has_desh_ingot", has(ModItems.INGOT_DESH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_KEROSENE_METAL.get(), 1)
                .pattern("ICI")
                .pattern("CTC")
                .pattern("ICI")
                .define('C', ModItems.PLATE_STEEL.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItems.MP_FUSELAGE_10_KEROSENE.get())
                .unlockedBy("has_kerosene_fuselage", has(ModItems.MP_FUSELAGE_10_KEROSENE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_10_LONG_KEROSENE_METAL.get(), 1)
                .pattern("ICI")
                .pattern("CTC")
                .pattern("ICI")
                .define('C', ModItems.PLATE_STEEL.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get())
                .unlockedBy("has_long_kerosene_fuselage", has(ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_FUSELAGE_15_KEROSENE_METAL.get(), 1)
                .pattern("ICI")
                .pattern("CTC")
                .pattern("ICI")
                .define('C', ModItems.PLATE_STEEL.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItems.MP_FUSELAGE_15_KEROSENE.get())
                .unlockedBy("has_kerosene_fuselage_15", has(ModItems.MP_FUSELAGE_15_KEROSENE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_WARHEAD_15_BOXCAR.get(), 1)
                .pattern("SNS")
                .pattern("CBC")
                .pattern("SFS")
                .define('S', ModItems.INGOT_STARMETAL.get())
                .define('N', ModBlocks.DET_NUKE.get().asItem())
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('B', ModBlocks.BOXCAR.get().asItem())
                .define('F', ModItems.TRITIUM_DEUTERIUM_CAKE.get())
                .unlockedBy("has_advanced_circuit", has(ModItems.CIRCUIT_ADVANCED.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_1.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_VACUUM_TUBE.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_vacuum_tube", has(ModItems.CIRCUIT_VACUUM_TUBE.get()))
                .save(pWriter, MODID + ":mp_chip_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_2.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_ANALOG.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_analog_circuit", has(ModItems.CIRCUIT_ANALOG.get()))
                .save(pWriter, MODID + ":mp_chip_2");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_3.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_basic_circuit", has(ModItems.CIRCUIT_BASIC.get()))
                .save(pWriter, MODID + ":mp_chip_3");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_4.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_advanced_circuit", has(ModItems.CIRCUIT_ADVANCED.get()))
                .save(pWriter, MODID + ":mp_chip_4");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MP_CHIP_5.get(), 1)
                .pattern("P")
                .pattern("C")
                .pattern("S")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_BISMOID.get())
                .define('S', ModBlocks.STEEL_SCAFFOLD.get().asItem())
                .unlockedBy("has_bismoid_circuit", has(ModItems.CIRCUIT_BISMOID.get()))
                .save(pWriter, MODID + ":mp_chip_5");

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
                .unlockedBy("has_basic_circuit", has(ModItems.CIRCUIT_BASIC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModGunItems.GUN_FIREEXT.get(), 1)
                .pattern("HB")
                .pattern(" T")
                .define('H', ModItems.PIPE_STEEL.get())
                .define('B', ModItems.BOLT_STEEL.get())
                .define('T', ModItems.TANK_STEEL.get())
                .unlockedBy("has_steel_tank", has(ModItems.TANK_STEEL.get()))
                .save(pWriter, MODID + ":gun_fireext");



        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ASSEMBLY_NUKE.get(), 1)
                .pattern(" WP")
                .pattern("SEP")
                .pattern(" WP")
                .define('W', ModItems.WIRE_GOLD.get())
                .define('P', ModItems.PLATE_WEAPON_STEEL.get())
                .define('S', ModItems.SHELL_WEAPON_STEEL.get())
                .define('E', ModItems.BALL_TATB.get())
                .unlockedBy("has_tatb_ball", has(ModItems.BALL_TATB.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL.get(), 4)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', Blocks.TNT.asItem())
                .define('G', Items.GUNPOWDER)
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_steel_shell", has(ModItems.SHELL_STEEL.get()))
                .save(pWriter, MODID + ":ammo_shell_gunpowder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL.get(), 4)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', Blocks.TNT.asItem())
                .define('G', ModItems.BALLISTITE.get())
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has(ModItems.BALLISTITE.get()))
                .save(pWriter, MODID + ":ammo_shell_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL.get(), 6)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', Blocks.TNT.asItem())
                .define('G', ModItems.CORDITE.get())
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has(ModItems.CORDITE.get()))
                .save(pWriter, MODID + ":ammo_shell_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_EXPLOSIVE.get(), 4)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT)
                .define('G', Items.GUNPOWDER)
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_plastic_explosive", has(ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT))
                .save(pWriter, MODID + ":ammo_shell_explosive_gunpowder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_EXPLOSIVE.get(), 4)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT)
                .define('G', ModItems.BALLISTITE.get())
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has(ModItems.BALLISTITE.get()))
                .save(pWriter, MODID + ":ammo_shell_explosive_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_EXPLOSIVE.get(), 6)
                .pattern(" T ")
                .pattern("GHG")
                .pattern("CCC")
                .define('T', ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT)
                .define('G', ModItems.CORDITE.get())
                .define('H', ModItems.SHELL_STEEL.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has(ModItems.CORDITE.get()))
                .save(pWriter, MODID + ":ammo_shell_explosive_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_T.get(), 4)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_TUNGSTEN.get())
                .define('G', Items.GUNPOWDER)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_tungsten_ingot", has(ModItems.INGOT_TUNGSTEN.get()))
                .save(pWriter, MODID + ":ammo_shell_apfsds_t_gunpowder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_T.get(), 4)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_TUNGSTEN.get())
                .define('G', ModItems.BALLISTITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has(ModItems.BALLISTITE.get()))
                .save(pWriter, MODID + ":ammo_shell_apfsds_t_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_T.get(), 6)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_TUNGSTEN.get())
                .define('G', ModItems.CORDITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has(ModItems.CORDITE.get()))
                .save(pWriter, MODID + ":ammo_shell_apfsds_t_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_DU.get(), 4)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_U238.get())
                .define('G', Items.GUNPOWDER)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_u238_ingot", has(ModItems.INGOT_U238.get()))
                .save(pWriter, MODID + ":ammo_shell_apfsds_du_gunpowder");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_DU.get(), 4)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_U238.get())
                .define('G', ModItems.BALLISTITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has(ModItems.BALLISTITE.get()))
                .save(pWriter, MODID + ":ammo_shell_apfsds_du_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_APFSDS_DU.get(), 6)
                .pattern(" I ")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', ModItems.INGOT_U238.get())
                .define('G', ModItems.CORDITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has(ModItems.CORDITE.get()))
                .save(pWriter, MODID + ":ammo_shell_apfsds_du_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_SHELL_W9.get(), 1)
                .pattern(" P ")
                .pattern("NSN")
                .pattern(" P ")
                .define('P', ModItems.NUGGET_PU239.get())
                .define('N', ModItems.NEUTRON_REFLECTOR.get())
                .define('S', ModAmmoItems.AMMO_SHELL_EXPLOSIVE.get())
                .unlockedBy("has_plutonium_nugget", has(ModItems.NUGGET_PU239.get()))
                .save(pWriter, MODID + ":ammo_shell_w9");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_STANDARD.get(), 1)
                .pattern("CIC")
                .pattern("CSC")
                .pattern("CCC")
                .define('C', ModItems.CORDITE.get())
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('S', ModItems.SHELL_COPPER.get())
                .unlockedBy("has_cordite", has(ModItems.CORDITE.get()))
                .save(pWriter, MODID + ":ammo_arty_standard");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_HE.get(), 1)
                .pattern(" D ")
                .pattern("DSD")
                .pattern(" D ")
                .define('D', ModItems.BALL_DYNAMITE.get())
                .define('S', ModAmmoItems.AMMO_ARTY_STANDARD.get())
                .unlockedBy("has_dynamite_ball", has(ModItems.BALL_DYNAMITE.get()))
                .save(pWriter, MODID + ":ammo_arty_he");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_HE2.get(), 1)
                .pattern("TTT")
                .pattern("TST")
                .pattern("TTT")
                .define('T', ModItems.BALL_TNT.get())
                .define('S', ModAmmoItems.AMMO_ARTY_STANDARD.get())
                .unlockedBy("has_tnt_ball", has(ModItems.BALL_TNT.get()))
                .save(pWriter, MODID + ":ammo_arty_he2");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_WP.get(), 1)
                .pattern("D")
                .pattern("S")
                .pattern("D")
                .define('D', ModItems.INGOT_PHOSPHORUS.get())
                .define('S', ModAmmoItems.AMMO_ARTY_STANDARD.get())
                .unlockedBy("has_white_phosphorus", has(ModItems.INGOT_PHOSPHORUS.get()))
                .save(pWriter, MODID + ":ammo_arty_wp");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_WP_ENHANCED.get(), 1)
                .pattern("DSD")
                .pattern("SCS")
                .pattern("DSD")
                .define('D', ModItems.INGOT_PHOSPHORUS.get())
                .define('S', ModAmmoItems.AMMO_ARTY_WP.get())
                .define('C', ModBlocks.DET_CORD.get().asItem())
                .unlockedBy("has_white_phosphorus", has(ModItems.INGOT_PHOSPHORUS.get()))
                .save(pWriter, MODID + ":ammo_arty_wp_enhanced");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_NUKE.get(), 1)
                .pattern(" P ")
                .pattern("NSN")
                .pattern(" P ")
                .define('P', ModItems.NUGGET_PU239.get())
                .define('N', ModItems.NEUTRON_REFLECTOR.get())
                .define('S', ModAmmoItems.AMMO_ARTY_STANDARD.get())
                .unlockedBy("has_plutonium_nugget", has(ModItems.NUGGET_PU239.get()))
                .save(pWriter, MODID + ":ammo_arty_nuke");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_NUKE_ENHANCED.get(), 1)
                .pattern("DSD")
                .pattern("SCS")
                .pattern("DSD")
                .define('D', ModItems.NEUTRON_REFLECTOR.get())
                .define('S', ModAmmoItems.AMMO_ARTY_NUKE.get())
                .define('C', ModBlocks.DET_CORD.get().asItem())
                .unlockedBy("has_plutonium_nugget", has(ModItems.NUGGET_PU239.get()))
                .save(pWriter, MODID + ":ammo_arty_nuke_enhanced");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_SINKER.get(), 1)
                .requires(ModAmmoItems.AMMO_ARTY_HE2.get())
                .requires(ModItems.BOY_BULLET.get())
                .requires(ModItems.BOY_TARGET.get())
                .requires(ModItems.BOY_SHIELDING.get())
                .requires(ModItems.CIRCUIT_CONTROLLER.get())
                .requires(ModItems.DUCTTAPE.get())
                .unlockedBy("has_controller_circuit", has(ModItems.CIRCUIT_CONTROLLER.get()))
                .save(pWriter, MODID + ":ammo_arty_sinker");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_ARTY_MIRV.get(), 1)
                .pattern(" I ")
                .pattern(" S ")
                .pattern("CCC")
                .define('C', ModItems.CORDITE.get())
                .define('I', ModItems.SPHERE_STEEL.get())
                .define('S', ModItems.SHELL_COPPER.get())
                .unlockedBy("has_steel_sphere", has(ModItems.SPHERE_STEEL.get()))
                .save(pWriter, MODID + ":ammo_arty_mirv");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_DGK.get(), 1)
                .pattern("LLL")
                .pattern("GGG")
                .pattern("CCC")
                .define('L', ModItems.PLATE_LEAD.get())
                .define('G', ModItems.BALLISTITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_ballistite", has(ModItems.BALLISTITE.get()))
                .save(pWriter, MODID + ":ammo_dgk_ballistite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModAmmoItems.AMMO_DGK.get(), 1)
                .pattern("LLL")
                .pattern("GGG")
                .pattern("CCC")
                .define('L', ModItems.PLATE_LEAD.get())
                .define('G', ModItems.CORDITE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_cordite", has(ModItems.CORDITE.get()))
                .save(pWriter, MODID + ":ammo_dgk_cordite");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.MINE_AP.get(), 4)
                .pattern("I")
                .pattern("C")
                .pattern("S")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('C', ModItemTags.ANY_SMOKELESS_DUST)
                .define('S', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_polymer_plate", has(ModItems.PLATE_POLYMER.get()))
                .save(pWriter, MODID + ":mine_ap");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.MINE_SHRAP.get(), 1)
                .pattern("L")
                .pattern("M")
                .define('M', ModBlocks.MINE_AP.get().asItem())
                .define('L', ModItems.PELLET_BUCKSHOT.get())
                .unlockedBy("has_ap_mine", has(ModBlocks.MINE_AP.get().asItem()))
                .save(pWriter, MODID + ":mine_shrap");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.MINE_HE.get(), 1)
                .pattern(" C ")
                .pattern("PTP")
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('T', ModItemTags.ANY_HIGHEXPLOSIVE)
                .unlockedBy("has_basic_circuit", has(ModItems.CIRCUIT_BASIC.get()))
                .save(pWriter, MODID + ":mine_he");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.MINE_FAT.get(), 1)
                .pattern("CDN")
                .define('C', ModItems.CIRCUIT_ANALOG.get())
                .define('D', ModItems.DUCTTAPE.get())
                .define('N', ModAmmoItems.AMMO_NUKE_DEMO.get())
                .unlockedBy("has_analog_circuit", has(ModItems.CIRCUIT_ANALOG.get()))
                .save(pWriter, MODID + ":mine_fat");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.N2_CHARGE.get(), 1)
                .pattern(" D ")
                .pattern("ERE")
                .pattern(" D ")
                .define('D', ModItems.DUCTTAPE.get())
                .define('E', ModBlocks.DET_CHARGE.get().asItem())
                .define('R', Blocks.REDSTONE_BLOCK.asItem())
                .unlockedBy("has_det_charge", has(ModBlocks.DET_CHARGE.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_TNT.get(), 1)
                .pattern(" C ")
                .pattern("TIT")
                .pattern("TIT")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItemTags.ANY_HIGHEXPLOSIVE)
                .unlockedBy("has_high_explosive", has(ModItemTags.ANY_HIGHEXPLOSIVE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_NUKE.get(), 1)
                .pattern(" C ")
                .pattern("LUL")
                .pattern("LUL")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('U', ModItems.INGOT_U235.get())
                .unlockedBy("has_u235_ingot", has(ModItems.INGOT_U235.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_HYDRO.get(), 1)
                .pattern(" C ")
                .pattern("LTL")
                .pattern("LIL")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', ModItems.CELL_TRITIUM.get())
                .unlockedBy("has_tritium_cell", has(ModItems.CELL_TRITIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_AMAT.get(), 1)
                .pattern(" C ")
                .pattern("MMM")
                .pattern("AAA")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('M', ModItems.CELL_ANTIMATTER.get())
                .unlockedBy("has_antimatter_cell", has(ModItems.CELL_ANTIMATTER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_DIRTY.get(), 1)
                .pattern(" C ")
                .pattern("WLW")
                .pattern("WLW")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('W', ModItems.NUCLEAR_WASTE.get())
                .unlockedBy("has_nuclear_waste", has(ModItems.NUCLEAR_WASTE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CUSTOM_SCHRAB.get(), 1)
                .pattern(" C ")
                .pattern("LUL")
                .pattern("LUL")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('U', ModItems.INGOT_SCHRABIDIUM.get())
                .unlockedBy("has_schrabidium_ingot", has(ModItems.INGOT_SCHRABIDIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LAMP_DEMON.get(), 1)
                .pattern(" D ")
                .pattern("S S")
                .define('D', ModItems.DEMON_CORE_CLOSED.get())
                .define('S', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_demon_core", has(ModItems.DEMON_CORE_CLOSED.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModToolItems.CRUCIBLE.get(), 1)
                .pattern("MEM")
                .pattern("YDY")
                .pattern("YCY")
                .define('M', ModItems.INGOT_METEORITE_FORGED.get())
                .define('E', ModItems.INGOT_EUPHEMIUM.get())
                .define('Y', ModItems.BILLET_YHARONITE.get())
                .define('D', ModItems.DEMON_CORE_CLOSED.get())
                .define('C', ModItems.INGOT_CHAINSTEEL.get())
                .unlockedBy("has_demon_core", has(ModItems.DEMON_CORE_CLOSED.get()))
                .save(pWriter, MODID + ":crucible");
    }
}
