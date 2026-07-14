package com.hbm.inventory.recipes;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.items.ModItems;
import net.minecraft.world.item.ItemStack;

public class CrucibleRecipes {

    public static List<CrucibleRecipe> recipes = new ArrayList<>();

    public static void register() {
        int n = MaterialShapes.NUGGET.q(1);
        int i = MaterialShapes.INGOT.q(1);

        // Steel (Iron + Carbon)
        recipes.add(new CrucibleRecipe("crucible.steel", 2, Mats.MAT_STEEL.make(ModItems.INGOT_STEEL.get()))
                .inputs(new MaterialStack(Mats.MAT_IRON, n * 2), new MaterialStack(Mats.MAT_CARBON, n))
                .outputs(new MaterialStack(Mats.MAT_STEEL, n * 2)));

        // Red Copper (Copper + Redstone)
        recipes.add(new CrucibleRecipe("crucible.redcopper", 2, Mats.MAT_RED_COPPER.make(ModItems.INGOT_RED_COPPER.get()))
                .inputs(new MaterialStack(Mats.MAT_COPPER, n), new MaterialStack(Mats.MAT_REDSTONE, n))
                .outputs(new MaterialStack(Mats.MAT_RED_COPPER, n * 2)));

        // Advanced Alloy (Steel + Mingrade)
        recipes.add(new CrucibleRecipe("crucible.aa", 2, Mats.MAT_ALLOY.make(ModItems.INGOT_ADVANCED_ALLOY.get()))
                .inputs(new MaterialStack(Mats.MAT_STEEL, n), new MaterialStack(Mats.MAT_RED_COPPER, n))
                .outputs(new MaterialStack(Mats.MAT_ALLOY, n * 2)));

        // Dura Steel (Steel + Tungsten + Cobalt)
        recipes.add(new CrucibleRecipe("crucible.hss", 9, Mats.MAT_DURA.make(ModItems.INGOT_DURA_STEEL.get()))
                .inputs(new MaterialStack(Mats.MAT_STEEL, n * 5), new MaterialStack(Mats.MAT_TUNGSTEN, n * 3), new MaterialStack(Mats.MAT_COBALT, n * 1))
                .outputs(new MaterialStack(Mats.MAT_DURA, n * 9)));

        // Ferro (Steel + U238)
        recipes.add(new CrucibleRecipe("crucible.ferro", 3, Mats.MAT_FERRO.make(ModItems.INGOT_FERROURANIUM.get()))
                .inputs(new MaterialStack(Mats.MAT_STEEL, n * 2), new MaterialStack(Mats.MAT_U238, n))
                .outputs(new MaterialStack(Mats.MAT_FERRO, n * 3)));

        // TCalloy (Steel + Technetium)
        recipes.add(new CrucibleRecipe("crucible.tcalloy", 9, Mats.MAT_TCALLOY.make(ModItems.INGOT_TCALLOY.get()))
                .inputs(new MaterialStack(Mats.MAT_STEEL, n * 8), new MaterialStack(Mats.MAT_TECHNETIUM, n))
                .outputs(new MaterialStack(Mats.MAT_TCALLOY, i)));

        // Hematite
        recipes.add(new CrucibleRecipe("crucible.hematite", 6, new ItemStack(ModBlocks.STONE_RESOURCE.get()))
                .inputs(new MaterialStack(Mats.MAT_HEMATITE, i * 2), new MaterialStack(Mats.MAT_FLUX, n * 2))
                .outputs(new MaterialStack(Mats.MAT_IRON, i), new MaterialStack(Mats.MAT_SLAG, n * 3)));

        // Malachite
        recipes.add(new CrucibleRecipe("crucible.malachite", 6, new ItemStack(ModBlocks.STONE_RESOURCE.get()))
                .inputs(new MaterialStack(Mats.MAT_MALACHITE, i * 2), new MaterialStack(Mats.MAT_FLUX, n * 2))
                .outputs(new MaterialStack(Mats.MAT_COPPER, i), new MaterialStack(Mats.MAT_SLAG, n * 3)));

        // Bismuth Bronze
        recipes.add(new CrucibleRecipe("crucible.bbronze", 9, Mats.MAT_BBRONZE.make(ModItems.INGOT_BISMUTH_BRONZE.get()))
                .inputs(new MaterialStack(Mats.MAT_COPPER, n * 8), new MaterialStack(Mats.MAT_BISMUTH, n), new MaterialStack(Mats.MAT_FLUX, n * 3))
                .outputs(new MaterialStack(Mats.MAT_BBRONZE, i), new MaterialStack(Mats.MAT_SLAG, n * 3)));

        // Arsenic Bronze
        recipes.add(new CrucibleRecipe("crucible.abronze", 9, Mats.MAT_ABRONZE.make(ModItems.INGOT_ARSENIC_BRONZE.get()))
                .inputs(new MaterialStack(Mats.MAT_COPPER, n * 8), new MaterialStack(Mats.MAT_ARSENIC, n), new MaterialStack(Mats.MAT_FLUX, n * 3))
                .outputs(new MaterialStack(Mats.MAT_ABRONZE, i), new MaterialStack(Mats.MAT_SLAG, n * 3)));

        // CMB
        recipes.add(new CrucibleRecipe("crucible.cmb", 3, Mats.MAT_CMB.make(ModItems.INGOT_COMBINE_STEEL.get()))
                .inputs(new MaterialStack(Mats.MAT_MAGTUNG, n * 6), new MaterialStack(Mats.MAT_MUD, n * 3))
                .outputs(new MaterialStack(Mats.MAT_CMB, i)));

        // MagTung
        recipes.add(new CrucibleRecipe("crucible.magtung", 3, Mats.MAT_MAGTUNG.make(ModItems.INGOT_MAGNETIZED_TUNGSTEN.get()))
                .inputs(new MaterialStack(Mats.MAT_TUNGSTEN, i), new MaterialStack(Mats.MAT_SCHRABIDIUM, n * 1))
                .outputs(new MaterialStack(Mats.MAT_MAGTUNG, i)));

        // BSCCO
        recipes.add(new CrucibleRecipe("crucible.bscco", 3, Mats.MAT_BSCCO.make(ModItems.INGOT_BSCCO.get()))
                .inputs(new MaterialStack(Mats.MAT_BISMUTH, n * 2), new MaterialStack(Mats.MAT_STRONTIUM, n * 2),
                        new MaterialStack(Mats.MAT_CALCIUM, n * 2), new MaterialStack(Mats.MAT_COPPER, n * 3))
                .outputs(new MaterialStack(Mats.MAT_BSCCO, i)));

        // Cdalloy
        recipes.add(new CrucibleRecipe("crucible.cdalloy", 9, Mats.MAT_CDALLOY.make(ModItems.INGOT_CDALLOY.get()))
                .inputs(new MaterialStack(Mats.MAT_STEEL, n * 8), new MaterialStack(Mats.MAT_CADMIUM, n))
                .outputs(new MaterialStack(Mats.MAT_CDALLOY, i)));
    }

    public static CrucibleRecipe getRecipe(List<MaterialStack> input) {
        outer:
        for (CrucibleRecipe recipe : recipes) {
            List<MaterialStack> recipeInput = new ArrayList<>();
            for (MaterialStack stack : recipe.input) {
                recipeInput.add(new MaterialStack(stack.material, stack.amount));
            }

            for (MaterialStack inStack : input) {
                if (inStack.amount <= 0) continue;

                boolean hasMatch = false;
                for (MaterialStack recipeStack : recipeInput) {
                    if (recipeStack.material == inStack.material && recipeStack.amount <= inStack.amount) {
                        hasMatch = true;
                        recipeInput.remove(recipeStack);
                        break;
                    }
                }

                if (!hasMatch) continue outer;
            }

            if (recipeInput.isEmpty()) return recipe;
        }

        return null;
    }

    public static class CrucibleRecipe {
        public final String name;
        public MaterialStack[] input;
        public MaterialStack[] output;
        public int frequency;
        public ItemStack icon;

        public CrucibleRecipe(String name, int frequency, ItemStack icon) {
            this.name = name;
            this.frequency = frequency;
            this.icon = icon;
        }

        public CrucibleRecipe inputs(MaterialStack... input) { this.input = input; return this; }
        public CrucibleRecipe outputs(MaterialStack... output) { this.output = output; return this; }

        public int getInputAmount() {
            int content = 0;
            for (MaterialStack stack : input) content += stack.amount;
            return content;
        }
    }
}