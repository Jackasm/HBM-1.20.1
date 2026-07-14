package com.hbm.inventory.recipes;

import com.hbm.config.GeneralConfig;
import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;


public class SolderingRecipes {

    public static List<SolderingRecipe> recipes = new ArrayList<>();
    public static HashSet<AStack> toppings = new HashSet<>();
    public static HashSet<AStack> pcb = new HashSet<>();
    public static HashSet<AStack> solder = new HashSet<>();

    public static void register() {
        boolean lbsm = GeneralConfig.enableLBSM.get() && GeneralConfig.enableLBSMSimpleCrafting.get();

        /*
         * CIRCUITS
         */

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_ANALOG.get()), 100, 100,
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 3),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get(), 2)},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_PCB.get(), 4)},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 4)}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_BASIC.get()), 200, 250,
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CHIP.get(), 4)},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_PCB.get(), 4)},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 4)}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_ADVANCED.get()), 300, 1000,
                new FluidStackHBM(Fluids.SULFURIC_ACID.get(), 1000),
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CHIP.get(), lbsm? 4 :16),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get(), 4)},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_PCB.get(), 8),
                        new ComparableStack(ModItems.INGOT_RUBBER.get(), 2)},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 8)}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_CAPACITOR_BOARD.get()), 200, 300,
                new FluidStackHBM(Fluids.PEROXIDE.get(), 250),
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get(), 3)},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_PCB.get())},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 3)}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_BISMOID.get()), 400, 10000,
                new FluidStackHBM(Fluids.SOLVENT.get(), 1000),
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CHIP_BISMOID.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CHIP.get(), lbsm? 4 : 16),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get(), lbsm? 8 : 24)},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_PCB.get()),
                        new TagStack(ModItemTags.ANY_HARDPLASTIC_INGOT, 2)},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 12)}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_QUANTUM.get()), 400, 100000,
                new FluidStackHBM(Fluids.HELIUM4.get(), 1000),
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CHIP_QUANTUM.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CHIP_BISMOID.get(), lbsm? 4 : 16),
                        new ComparableStack(ModItems.CIRCUIT_ATOMIC_CLOCK.get(), lbsm? 1 : 4)},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_PCB.get(), 16),
                        new TagStack(ModItemTags.ANY_HARDPLASTIC_INGOT, 4)},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 16)}
        ));

        /*
         * COMPUTERS
         */

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_CONTROLLER.get()), 400, 15000,
                new FluidStackHBM(Fluids.PERFLUOROMETHYL.get(), 1000),
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CHIP.get(), lbsm ? 8 : 32),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get(), lbsm? 8 : 32),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get(), lbsm? 4 : 16)},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CONTROLLER_CHASSIS.get()),
                        new ComparableStack(ModItems.UPGRADE_SPEED_1.get())},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 16)}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_CONTROLLER_ADVANCED.get()), 600, 25000,
                new FluidStackHBM(Fluids.PERFLUOROMETHYL.get(), 4000),
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CHIP_BISMOID.get(), lbsm? 8 : 16),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get(), lbsm? 16 : 48),
                        new ComparableStack(ModItems.CIRCUIT_ATOMIC_CLOCK.get())},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CONTROLLER_CHASSIS.get()),
                        new ComparableStack(ModItems.UPGRADE_SPEED_3.get())},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 24)}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.CIRCUIT_CONTROLLER_QUANTUM.get()), 600, 250000,
                new FluidStackHBM(Fluids.PERFLUOROMETHYL_COLD.get(), 6000),
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CHIP_QUANTUM.get(), lbsm? 8 : 16),
                        new ComparableStack(ModItems.CIRCUIT_CHIP_BISMOID.get(), lbsm? 16 : 48),
                        new ComparableStack(ModItems.CIRCUIT_ATOMIC_CLOCK.get(), lbsm? 1 : 8)},
                new AStack[]{
                        new ComparableStack(ModItems.CIRCUIT_CONTROLLER_ADVANCED.get(), 2),
                        new ComparableStack(ModItems.UPGRADE_OVERDRIVE_1.get())},
                new AStack[]{
                        new ComparableStack(ModItems.WIRE_LEAD.get(), 32)}
        ));

        /*
         * UPGRADES
         */

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.UPGRADE_SPEED_1.get()), 200, 1000,
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get())},
                new AStack[]{new ComparableStack(ModItems.UPGRADE_TEMPLATE.get()),
                        new ComparableStack(ModItems.POWDER_RED_COPPER.get(), 4)},
                new AStack[]{}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.UPGRADE_EFFECT_1.get()), 200, 1000,
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get())},
                new AStack[]{new ComparableStack(ModItems.UPGRADE_TEMPLATE.get()),
                        new ComparableStack(ModItems.POWDER_EMERALD.get(), 4)},
                new AStack[]{}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.UPGRADE_POWER_1.get()), 200, 1000,
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get())},
                new AStack[]{new ComparableStack(ModItems.UPGRADE_TEMPLATE.get()),
                        new ComparableStack(ModItems.POWDER_GOLD.get(), 4)},
                new AStack[]{}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.UPGRADE_FORTUNE_1.get()), 200, 1000,
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get())},
                new AStack[]{new ComparableStack(ModItems.UPGRADE_TEMPLATE.get()),
                        new ComparableStack(ModItems.POWDER_NIOBIUM.get(), 4)},
                new AStack[]{}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.UPGRADE_AFTERBURN_1.get()), 200, 1000,
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get())},
                new AStack[]{new ComparableStack(ModItems.UPGRADE_TEMPLATE.get()),
                        new ComparableStack(ModItems.POWDER_TUNGSTEN.get(), 4)},
                new AStack[]{}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.UPGRADE_RADIUS.get()), 200, 1000,
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_CHIP.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get(), 4)},
                new AStack[]{new ComparableStack(ModItems.UPGRADE_TEMPLATE.get()),
                        new ComparableStack(Items.GLOWSTONE_DUST, 4)},
                new AStack[]{}
        ));

        recipes.add(new SolderingRecipe(new ItemStack(ModItems.UPGRADE_HEALTH.get()), 200, 1000,
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_CHIP.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get(), 4)},
                new AStack[]{new ComparableStack(ModItems.UPGRADE_TEMPLATE.get()),
                        new ComparableStack(ModItems.POWDER_LITHIUM.get(), 4)},
                new AStack[]{}
        ));

        // Upgrade chains
        addFirstUpgrade(ModItems.UPGRADE_SPEED_1.get(), ModItems.UPGRADE_SPEED_2.get());
        addSecondUpgrade(ModItems.UPGRADE_SPEED_2.get(), ModItems.UPGRADE_SPEED_3.get());
        addFirstUpgrade(ModItems.UPGRADE_EFFECT_1.get(), ModItems.UPGRADE_EFFECT_2.get());
        addSecondUpgrade(ModItems.UPGRADE_EFFECT_2.get(), ModItems.UPGRADE_EFFECT_3.get());
        addFirstUpgrade(ModItems.UPGRADE_POWER_1.get(), ModItems.UPGRADE_POWER_2.get());
        addSecondUpgrade(ModItems.UPGRADE_POWER_2.get(), ModItems.UPGRADE_POWER_3.get());
        addFirstUpgrade(ModItems.UPGRADE_FORTUNE_1.get(), ModItems.UPGRADE_FORTUNE_2.get());
        addSecondUpgrade(ModItems.UPGRADE_FORTUNE_2.get(), ModItems.UPGRADE_FORTUNE_3.get());
        addFirstUpgrade(ModItems.UPGRADE_AFTERBURN_1.get(), ModItems.UPGRADE_AFTERBURN_2.get());
        addSecondUpgrade(ModItems.UPGRADE_AFTERBURN_2.get(), ModItems.UPGRADE_AFTERBURN_3.get());
    }

    private static void addFirstUpgrade(Item lower, Item higher) {
        boolean lbsm = GeneralConfig.enableLBSM.get() && GeneralConfig.enableLBSMSimpleCrafting.get();
        recipes.add(new SolderingRecipe(new ItemStack(higher), 300, 10000,
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_CHIP.get(), lbsm ? 4 : 8),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get(), lbsm ? 2 : 4),},
                new AStack[]{new ComparableStack(lower), new TagStack(ModItemTags.ANY_HARDPLASTIC_INGOT, 4)},
                new AStack[]{}
        ));
    }

    private static void addSecondUpgrade(Item lower, Item higher) {
        boolean lbsm = GeneralConfig.enableLBSM.get() && GeneralConfig.enableLBSMSimpleCrafting.get();
        recipes.add(new SolderingRecipe(new ItemStack(higher), 400, 25000,
                new FluidStackHBM(Fluids.SOLVENT.get(), 500),
                new AStack[]{new ComparableStack(ModItems.CIRCUIT_CHIP.get(), lbsm ? 6 : 16),
                        new ComparableStack(ModItems.CIRCUIT_CAPACITOR.get(), lbsm ? 4 : 16),},
                new AStack[]{new ComparableStack(lower), new ComparableStack(ModItems.INGOT_RUBBER.get(), 4)},
                new AStack[]{}
        ));
    }

    public static SolderingRecipe getRecipe(ItemStack[] inputs) {
        for (SolderingRecipe recipe : recipes) {
            if (matchesIngredients(new ItemStack[]{inputs[0], inputs[1], inputs[2]}, recipe.toppings) &&
                    matchesIngredients(new ItemStack[]{inputs[3], inputs[4]}, recipe.pcb) &&
                    matchesIngredients(new ItemStack[]{inputs[5]}, recipe.solder)) {
                return recipe;
            }
        }
        return null;
    }

    private static boolean matchesIngredients(ItemStack[] inputStacks, AStack[] recipeStacks) {
        if (recipeStacks == null || recipeStacks.length == 0) {
            // Если рецепт не требует ингредиентов, то слоты должны быть пустыми
            for (ItemStack stack : inputStacks) {
                if (stack != null && !stack.isEmpty()) return false;
            }
            return true;
        }

        // Проверяем, что все рецептурные стеки соответствуют входным
        boolean[] used = new boolean[recipeStacks.length];

        for (ItemStack input : inputStacks) {
            if (input == null || input.isEmpty()) continue;

            boolean found = false;
            for (int j = 0; j < recipeStacks.length; j++) {
                if (!used[j] && recipeStacks[j] != null && recipeStacks[j].matchesRecipe(input, true)) {
                    used[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        // Проверяем, что все рецептурные стеки были найдены
        for (int j = 0; j < recipeStacks.length; j++) {
            if (!used[j] && recipeStacks[j] != null) return false;
        }

        return true;
    }

    public static HashMap<Object, Object> getRecipes() {
        HashMap<Object, Object> recipesMap = new HashMap<>();

        for (SolderingRecipe recipe : SolderingRecipes.recipes) {
            List<Object> ingredients = new ArrayList<>();
            Collections.addAll(ingredients, recipe.toppings);
            Collections.addAll(ingredients, recipe.pcb);
            Collections.addAll(ingredients, recipe.solder);
            if (recipe.fluid != null) ingredients.add(recipe.fluid);

            recipesMap.put(ingredients.toArray(), recipe.output);
        }

        return recipesMap;
    }

    public static void deleteRecipes() {
        recipes.clear();
        toppings.clear();
        pcb.clear();
        solder.clear();
    }

    public static class SolderingRecipe {
        public AStack[] toppings;
        public AStack[] pcb;
        public AStack[] solder;
        public FluidStackHBM fluid;
        public ItemStack output;
        public int duration;
        public long consumption;

        public SolderingRecipe(ItemStack output, int duration, long consumption, FluidStackHBM fluid,
                               AStack[] toppings, AStack[] pcb, AStack[] solder) {
            this.toppings = toppings != null ? toppings : new AStack[0];
            this.pcb = pcb != null ? pcb : new AStack[0];
            this.solder = solder != null ? solder : new AStack[0];
            this.fluid = fluid;
            this.output = output;
            this.duration = duration;
            this.consumption = consumption;

            Collections.addAll(SolderingRecipes.toppings, this.toppings);
            Collections.addAll(SolderingRecipes.pcb, this.pcb);
            Collections.addAll(SolderingRecipes.solder, this.solder);
        }

        public SolderingRecipe(ItemStack output, int duration, long consumption,
                               AStack[] toppings, AStack[] pcb, AStack[] solder) {
            this(output, duration, consumption, null, toppings, pcb, solder);
        }
    }
}