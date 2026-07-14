package com.hbm.inventory.recipes;

import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.OreDictStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


import java.util.*;

import static com.hbm.inventory.OreDictManager.IRON;
import static com.hbm.inventory.material.Mats.*;
import static com.hbm.inventory.material.MaterialShapes.*;
import static com.hbm.items.ModItemTags.ANY_COKE;


public class RotaryFurnaceRecipes{

    public static List<RotaryFurnaceRecipe> recipes = new ArrayList<>();

    public static void register() {
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_STEEL, INGOT.q(1)), 100, 100,
                new ComparableStack(Items.IRON_INGOT), new ComparableStack(Items.COAL)));
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_STEEL, INGOT.q(1)), 100, 100,
                new ComparableStack(Items.IRON_INGOT), new TagStack(ANY_COKE)));

        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_STEEL, INGOT.q(2)), 200, 25,
                new OreDictStack(IRON.fragment(), 9), new ComparableStack(Items.COAL)));
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_STEEL, INGOT.q(3)), 200, 25,
                new OreDictStack(IRON.fragment(), 9), new TagStack(ANY_COKE)));
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_STEEL, INGOT.q(4)), 400, 25,
                new OreDictStack(IRON.fragment(), 9), new TagStack(ANY_COKE),
                new ComparableStack(ModItems.POWDER_FLUX.get())));

        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_DESH, INGOT.q(1)), 100, 200,
                new FluidStackHBM(Fluids.LIGHTOIL.get(), 100),
                new ComparableStack(ModItems.POWDER_DESH_READY.get())));

        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_GUNMETAL, INGOT.q(4)), 200, 100,
                new ComparableStack(Items.COPPER_INGOT, 3), new ComparableStack(ModItems.INGOT_ALUMINIUM.get(), 1)));
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_WEAPONSTEEL, INGOT.q(1)), 200, 400,
                new FluidStackHBM(Fluids.GAS_COKER.get(), 100),
                new ComparableStack(ModItems.INGOT_STEEL.get(), 1), new ComparableStack(ModItems.POWDER_FLUX.get(), 2)));
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_SATURN, INGOT.q(2)), 200, 400,
                new FluidStackHBM(Fluids.REFORMGAS.get(), 250),
                new ComparableStack(ModItems.POWDER_DURA_STEEL.get(), 4), new ComparableStack(ModItems.POWDER_COPPER.get())));
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_SATURN, INGOT.q(4)), 200, 300,
                new FluidStackHBM(Fluids.REFORMGAS.get(), 250),
                new ComparableStack(ModItems.POWDER_DURA_STEEL.get(), 4), new ComparableStack(ModItems.POWDER_COPPER.get()),
                new ComparableStack(ModItems.POWDER_BORAX.get())));
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_ALUMINIUM, INGOT.q(2)), 100, 400,
                new FluidStackHBM(Fluids.SODIUM_ALUMINATE.get(), 150)));
        recipes.add(new RotaryFurnaceRecipe(new MaterialStack(MAT_ALUMINIUM, INGOT.q(3)), 40, 200,
                new FluidStackHBM(Fluids.SODIUM_ALUMINATE.get(), 150),
                new ComparableStack(ModItems.POWDER_FLUX.get(), 2)));
    }

    public static RotaryFurnaceRecipe getRecipe(ItemStack... inputs) {
        outer:
        for (RotaryFurnaceRecipe recipe : recipes) {
            List<AStack> recipeList = new ArrayList<>();
            Collections.addAll(recipeList, recipe.ingredients);

            for (ItemStack inputStack : inputs) {
                if (inputStack != null && !inputStack.isEmpty()) {
                    boolean hasMatch = false;
                    Iterator<AStack> iterator = recipeList.iterator();

                    while (iterator.hasNext()) {
                        AStack recipeStack = iterator.next();
                        if (recipeStack.matchesRecipe(inputStack, true) && inputStack.getCount() >= recipeStack.stacksize) {
                            hasMatch = true;
                            iterator.remove();
                            break;
                        }
                    }

                    if (!hasMatch) continue outer;
                }
            }

            if (recipeList.isEmpty()) return recipe;
        }

        return null;
    }

    public static class RotaryFurnaceRecipe {
        public AStack[] ingredients;
        public FluidStackHBM fluid;
        public MaterialStack output;
        public int duration;
        public int steam;

        public RotaryFurnaceRecipe(MaterialStack output, int duration, int steam, FluidStackHBM fluid, AStack... ingredients) {
            this.ingredients = ingredients;
            this.fluid = fluid;
            this.output = output;
            this.duration = duration;
            this.steam = steam;
        }

        public RotaryFurnaceRecipe(MaterialStack output, int duration, int steam, AStack... ingredients) {
            this(output, duration, steam, null, ingredients);
        }
    }
}