package com.hbm.inventory.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;

import com.hbm.util.Tuple;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ArcWelderRecipes {

    public static List<ArcWelderRecipe> recipes = new ArrayList<>();
    public static HashSet<AStack> ingredients = new HashSet<>();

    public static void register() {
        // Parts
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MOTOR.get(), 2), 100, 200L,
                new ComparableStack(ModItems.PLATE_IRON.get(), 2), new ComparableStack(ModItems.COIL_COPPER.get()),
                new ComparableStack(ModItems.COIL_COPPER_TORUS.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MOTOR.get(), 2), 100, 400L,
                new ComparableStack(ModItems.PLATE_STEEL.get(), 1), new ComparableStack(ModItems.COIL_COPPER.get()),
                new ComparableStack(ModItems.COIL_COPPER_TORUS.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.LDE.get()), 200, 5_000L,
                new ComparableStack(ModItems.PLATE_ALUMINIUM.get(), 4),
                new ComparableStack(ModItems.INGOT_FIBERGLASS.get(), 4), new TagStack(ModItemTags.ANY_HARDPLASTIC_INGOT, 1)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.LDE.get()), 200, 10_000L,
                new ComparableStack(ModItems.PLATE_TITANIUM.get(), 2),
                new ComparableStack(ModItems.INGOT_FIBERGLASS.get(), 4), new TagStack(ModItemTags.ANY_HARDPLASTIC_INGOT, 1)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.HDE.get()), 600, 25_000_000L,
                new FluidStackHBM(Fluids.STELLAR_FLUX.get(), 4_000),
                new TagStack(ModItemTags.ANY_BISMOID_BRONZE_PLATE_CAST, 2),
                new ComparableStack(ModItems.PLATE_WELDED_CMB_STEEL.get(), 1),
                new ComparableStack(ModItems.INGOT_CFT.get())));

        // Dense Wires
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.WIRE_DENSE_COPPER.get()), 100, 10_000L,
                new ComparableStack(ModItems.WIRE_COPPER.get(), 8)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.WIRE_DENSE_ADVANCED_ALLOY.get()), 100, 10_000L,
                new ComparableStack(ModItems.WIRE_ADVANCED_ALLOY.get(), 8)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.WIRE_DENSE_GOLD.get()), 100, 10_000L,
                new ComparableStack(ModItems.WIRE_GOLD.get(), 8)));

        // Earlygame welded parts
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_IRON.get()), 100, 100L,
                new ComparableStack(ModItems.PLATE_CAST_IRON.get(), 2)));

        // High-demand mid-game parts
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_STEEL.get()), 100, 500L,
                new ComparableStack(ModItems.PLATE_CAST_STEEL.get(), 2)));

        // Literally just the combination oven
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_COPPER.get()), 200, 1_000L,
                new ComparableStack(ModItems.PLATE_CAST_COPPER.get(), 2)));

        // Mid-game, single combustion engine running on LPG
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_TITANIUM.get()), 600, 50_000L,
                new ComparableStack(ModItems.PLATE_CAST_TITANIUM.get(), 2)));

        // Mid-game PWR
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_ZIRCONIUM.get()), 600, 10_000L,
                new ComparableStack(ModItems.PLATE_CAST_ZIRCONIUM.get(), 2)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_ALUMINIUM.get()), 300, 10_000L,
                new ComparableStack(ModItems.PLATE_CAST_ALUMINIUM.get(), 2)));

        // Late-game fusion
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_TECHNETIUM.get()), 1_200, 1_000_000L,
                new FluidStackHBM(Fluids.OXYGEN.get(), 1_000),
                new ComparableStack(ModItems.PLATE_CAST_TECHNETIUM.get(), 2)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_CADMIUM_STEEL.get()), 1_200, 1_000_000L,
                new FluidStackHBM(Fluids.OXYGEN.get(), 1_000),
                new ComparableStack(ModItems.PLATE_CAST_CADMIUM_STEEL.get(), 2)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_TUNGSTEN.get()), 1_200, 250_000L,
                new FluidStackHBM(Fluids.OXYGEN.get(), 1_000),
                new ComparableStack(ModItems.PLATE_CAST_TUNGSTEN.get(), 2)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_CMB_STEEL.get()), 1_200, 10_000_000L,
                new FluidStackHBM(Fluids.REFORMGAS.get(), 1_000),
                new ComparableStack(ModItems.PLATE_CAST_CMB_STEEL.get(), 2)));

        // Pre-DFC
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.PLATE_WELDED_OSMIRIDIUM.get()), 6_000, 20_000_000L,
                new FluidStackHBM(Fluids.REFORMGAS.get(), 16_000),
                new ComparableStack(ModItems.PLATE_CAST_OSMIRIDIUM.get(), 2)));

        // Missile Parts
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.THRUSTER_SMALL.get()), 60, 1_000L,
                new ComparableStack(ModItems.PLATE_STEEL.get(), 4),
                new ComparableStack(ModItems.WIRE_ALUMINIUM.get(), 4),
                new ComparableStack(ModItems.PLATE_COPPER.get(), 4)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.THRUSTER_MEDIUM.get()), 100, 2_000L,
                new ComparableStack(ModItems.PLATE_STEEL.get(), 8),
                new ComparableStack(ModItems.MOTOR.get()),
                new ComparableStack(ModItems.INGOT_GRAPHITE.get(), 8)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.THRUSTER_LARGE.get()), 200, 5_000L,
                new ComparableStack(ModItems.INGOT_DURA_STEEL.get(), 10),
                new ComparableStack(ModItems.MOTOR.get()),
                new ComparableStack(ModItems.NEUTRON_REFLECTOR.get(), 12)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.FUEL_TANK_SMALL.get()), 60, 1_000L,
                new ComparableStack(ModItems.PLATE_ALUMINIUM.get(), 6),
                new ComparableStack(ModItems.PLATE_COPPER.get(), 4),
                new ComparableStack(ModBlocks.STEEL_SCAFFOLD.get().asItem(), 4)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.FUEL_TANK_MEDIUM.get()), 100, 2_000L,
                new ComparableStack(ModItems.PLATE_CAST_ALUMINIUM.get(), 4),
                new ComparableStack(ModItems.PLATE_TITANIUM.get(), 8),
                new ComparableStack(ModBlocks.STEEL_SCAFFOLD.get().asItem(), 12)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.FUEL_TANK_LARGE.get()), 200, 5_000L,
                new ComparableStack(ModItems.PLATE_WELDED_ALUMINIUM.get(), 8),
                new ComparableStack(ModItems.PLATE_SATURNITE.get(), 12),
                new ComparableStack(ModBlocks.STEEL_SCAFFOLD.get().asItem(), 16)));

        // Missiles
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_ANTI_BALLISTIC.get()), 100, 5_000L,
                new TagStack(ModItemTags.ANY_HIGHEXPLOSIVE, 3),
                new ComparableStack(ModItems.MISSILE_ASSEMBLY.get()), new ComparableStack(ModItems.THRUSTER_SMALL.get(), 4)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_GENERIC.get()), 100, 5_000L,
                new ComparableStack(ModItems.WARHEAD_GENERIC_SMALL.get()),
                new ComparableStack(ModItems.FUEL_TANK_SMALL.get()), new ComparableStack(ModItems.THRUSTER_SMALL.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_INCENDIARY.get()), 100, 5_000L,
                new ComparableStack(ModItems.WARHEAD_INCENDIARY_SMALL.get()), new ComparableStack(ModItems.FUEL_TANK_SMALL.get()),
                new ComparableStack(ModItems.THRUSTER_SMALL.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_CLUSTER.get()), 100, 5_000L,
                new ComparableStack(ModItems.WARHEAD_CLUSTER_SMALL.get()), new ComparableStack(ModItems.FUEL_TANK_SMALL.get()),
                new ComparableStack(ModItems.THRUSTER_SMALL.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_BUSTER.get()), 100, 5_000L,
                new ComparableStack(ModItems.WARHEAD_BUSTER_SMALL.get()), new ComparableStack(ModItems.FUEL_TANK_SMALL.get()),
                new ComparableStack(ModItems.THRUSTER_SMALL.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_DECOY.get()), 60, 2_500L,
                new ComparableStack(ModItems.INGOT_STEEL.get()),
                new ComparableStack(ModItems.FUEL_TANK_SMALL.get()),
                new ComparableStack(ModItems.THRUSTER_SMALL.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_STRONG.get()), 200, 10_000L,
                new ComparableStack(ModItems.WARHEAD_GENERIC_MEDIUM.get()), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get()),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_INCENDIARY_STRONG.get()), 200, 10_000L,
                new ComparableStack(ModItems.WARHEAD_INCENDIARY_MEDIUM.get()), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get()),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_CLUSTER_STRONG.get()), 200, 10_000L,
                new ComparableStack(ModItems.WARHEAD_CLUSTER_MEDIUM.get()), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get()),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_BUSTER_STRONG.get()), 200, 10_000L,
                new ComparableStack(ModItems.WARHEAD_BUSTER_MEDIUM.get()), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get()),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_EMP_STRONG.get()), 200, 10_000L,
                new ComparableStack(ModBlocks.EMP_BOMB.get().asItem(), 3), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get()),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_BURST.get()), 300, 25_000L,
                new ComparableStack(ModItems.WARHEAD_GENERIC_LARGE.get()), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get(), 2),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get(), 4)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_INFERNO.get()), 300, 25_000L,
                new ComparableStack(ModItems.WARHEAD_INCENDIARY_LARGE.get()), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get(), 2),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get(), 4)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_RAIN.get()), 300, 25_000L,
                new ComparableStack(ModItems.WARHEAD_CLUSTER_LARGE.get()), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get(), 2),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get(), 4)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_DRILL.get()), 300, 25_000L,
                new ComparableStack(ModItems.WARHEAD_BUSTER_LARGE.get()), new ComparableStack(ModItems.FUEL_TANK_MEDIUM.get(), 2),
                new ComparableStack(ModItems.THRUSTER_MEDIUM.get(), 4)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_NUCLEAR.get()), 600, 50_000L,
                new ComparableStack(ModItems.WARHEAD_NUCLEAR.get()), new ComparableStack(ModItems.FUEL_TANK_LARGE.get()),
                new ComparableStack(ModItems.THRUSTER_LARGE.get(), 3)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_NUCLEAR_CLUSTER.get()), 600, 50_000L,
                new ComparableStack(ModItems.WARHEAD_MIRV.get()), new ComparableStack(ModItems.FUEL_TANK_LARGE.get()),
                new ComparableStack(ModItems.THRUSTER_LARGE.get(), 3)));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.MISSILE_VOLCANO.get()), 600, 50_000L,
                new ComparableStack(ModItems.WARHEAD_VOLCANO.get()), new ComparableStack(ModItems.FUEL_TANK_LARGE.get()),
                new ComparableStack(ModItems.THRUSTER_LARGE.get(), 3)));

        // Satellites
        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.SAT_MAPPER.get()), 600, 10_000L,
                new ComparableStack(ModItems.SAT_BASE.get()), new ComparableStack(ModItems.SAT_HEAD_MAPPER.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.SAT_SCANNER.get()), 600, 10_000L,
                new ComparableStack(ModItems.SAT_BASE.get()), new ComparableStack(ModItems.SAT_HEAD_SCANNER.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.SAT_RADAR.get()), 600, 10_000L,
                new ComparableStack(ModItems.SAT_BASE.get()), new ComparableStack(ModItems.SAT_HEAD_RADAR.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.SAT_LASER.get()), 600, 50_000L,
                new ComparableStack(ModItems.SAT_BASE.get()), new ComparableStack(ModItems.SAT_HEAD_LASER.get())));

        recipes.add(new ArcWelderRecipe(new ItemStack(ModItems.SAT_RESONATOR.get()), 600, 50_000L,
                new ComparableStack(ModItems.SAT_BASE.get()), new ComparableStack(ModItems.SAT_HEAD_RESONATOR.get())));
    }


    public static ArcWelderRecipe getRecipe(ItemStack... inputs) {
        outer:
        for (ArcWelderRecipe recipe : recipes) {
            List<AStack> recipeList = new ArrayList<>();
            Collections.addAll(recipeList, recipe.ingredients);

            for (ItemStack inputStack : inputs) {
                if (inputStack != null && !inputStack.isEmpty()) {
                    boolean hasMatch = false;
                    Iterator<AStack> iterator = recipeList.iterator();

                    while (iterator.hasNext()) {
                        AStack recipeStack = iterator.next();
                        if (recipeStack.matchesRecipe(inputStack, true) && inputStack.getCount() >= recipeStack.getStackSize()) {
                            hasMatch = true;
                            iterator.remove();
                            break;
                        }
                    }

                    if (!hasMatch) {
                        continue outer;
                    }
                }
            }

            if (recipeList.isEmpty()) {
                return recipe;
            }
        }
        return null;
    }

    public static HashMap<Object, Object> getRecipes() {
        HashMap<Object, Object> recipesMap = new HashMap<>();

        for (ArcWelderRecipe recipe : ArcWelderRecipes.recipes) {
            int size = recipe.ingredients.length + (recipe.fluid != null ? 1 : 0);
            Object[] array = new Object[size];

            System.arraycopy(recipe.ingredients, 0, array, 0, recipe.ingredients.length);

            if (recipe.fluid != null) {
                array[size - 1] = recipe.fluid;
            }

            recipesMap.put(array, recipe.output);
        }

        return recipesMap;
    }

    public static void deleteRecipes() {
        recipes.clear();
    }

    public static class ArcWelderRecipe {
        public AStack[] ingredients;
        public FluidStackHBM fluid;
        public ItemStack output;
        public int duration;
        public long consumption;

        public ArcWelderRecipe(ItemStack output, int duration, long consumption, FluidStackHBM fluid, AStack... ingredients) {
            this.ingredients = ingredients;
            this.fluid = fluid;
            this.output = output;
            this.duration = duration;
            this.consumption = consumption;
            ArcWelderRecipes.ingredients.addAll(Arrays.asList(ingredients));
        }

        public ArcWelderRecipe(ItemStack output, int duration, long consumption, AStack... ingredients) {
            this(output, duration, consumption, null, ingredients);
        }
    }

    public static class ArcWelderRecipeWrapper {
        private final Object input;
        private final Tuple.Pair<ItemStack, FluidStackHBM> output;
        private final int duration;
        private final long consumption;

        public ArcWelderRecipeWrapper(Object input, Tuple.Pair<ItemStack, FluidStackHBM> output, int duration, long consumption) {
            this.input = input;
            this.output = output;
            this.duration = duration;
            this.consumption = consumption;
        }

        public Object getInput() {
            return input;
        }

        public ItemStack getOutputItem() {
            return output != null ? output.key() : null;
        }

        public FluidStackHBM getOutputFluid() {
            return output != null ? output.value() : null;
        }

        public int getDuration() {
            return duration;
        }

        public long getConsumption() {
            return consumption;
        }

    }
}