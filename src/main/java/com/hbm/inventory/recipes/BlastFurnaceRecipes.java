package com.hbm.inventory.recipes;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import com.hbm.items.fluid.ItemFluidCanister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;



public class BlastFurnaceRecipes {

    protected static final List<BlastFurnaceRecipe> recipes = new ArrayList<>();
    private static final List<ResourceLocation> hiddenRecipes = new ArrayList<>();

    public record BlastFurnaceRecipe(Object input1, Object input2, ItemStack output) {}

    public static ItemStack getOutput(ItemStack in1, ItemStack in2) {
        for (BlastFurnaceRecipe recipe : recipes) {
            if ((matchesInput(in1, recipe.input1) && matchesInput(in2, recipe.input2)) ||
                    (matchesInput(in1, recipe.input2) && matchesInput(in2, recipe.input1))) {
                return recipe.output.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("unchecked")
    private static boolean matchesInput(ItemStack input, Object recipeInput) {
        if(recipeInput instanceof TagKey) {
            return input.is((TagKey<Item>) recipeInput);
        } else if(recipeInput instanceof ResourceLocation) {
            return BuiltInRegistries.ITEM.getKey(input.getItem()).equals(recipeInput);
        } else if(recipeInput instanceof Item) {
            return input.getItem() == recipeInput;
        } else if (recipeInput instanceof ItemStack requiredStack) {
            return ItemStack.isSameItemSameTags(input, requiredStack);
        }

        return false;
    }

    public static List<BlastFurnaceRecipe> getRecipes() {
        return recipes;
    }

    public static List<ResourceLocation> getHiddenRecipes() {
        return hiddenRecipes;
    }

    public static boolean isRecipeHidden(ItemStack output) {
        return hiddenRecipes.contains(BuiltInRegistries.ITEM.getKey(output.getItem()));
    }

    static {
        initializeRecipes();
    }

    private static void initializeRecipes() {
        // === STEEL RECIPES ===
        // Железный слиток + уголь/кокс = 1 сталь
        addRecipe(Items.IRON_INGOT, Items.COAL, new ItemStack(ModItems.INGOT_STEEL.get()));
        addRecipe(Items.IRON_INGOT, ModItemTags.ANY_COKE, new ItemStack(ModItems.INGOT_STEEL.get()));

        // Железная руда + уголь/кокс = больше стали
        addRecipe(ModItemTags.ORES_IRON, Items.COAL, new ItemStack(ModItems.INGOT_STEEL.get(), 2));
        addRecipe(ModItemTags.ORES_IRON, ModItemTags.ANY_COKE, new ItemStack(ModItems.INGOT_STEEL.get(), 3));
        addRecipe(ModItemTags.ORES_IRON, ModItems.POWDER_FLUX.get(), new ItemStack(ModItems.INGOT_STEEL.get(), 3));

        // === ADVANCED ALLOYS ===
        addRecipe(Items.COPPER_INGOT, Items.REDSTONE, new ItemStack(ModItems.INGOT_RED_COPPER.get(), 2));
        addRecipe(ModItems.INGOT_STEEL.get(), ModItems.INGOT_RED_COPPER.get(), new ItemStack(ModItems.INGOT_ADVANCED_ALLOY.get(), 2));

        // === TUNGSTEN RECIPES ===
        addRecipe(ModItems.INGOT_TUNGSTEN.get(), Items.COAL, new ItemStack(ModItems.NEUTRON_REFLECTOR.get(), 2));
        addRecipe(ModItems.INGOT_TUNGSTEN.get(), ModItemTags.ANY_COKE, new ItemStack(ModItems.NEUTRON_REFLECTOR.get(), 2));
        addRecipe(ModItems.INGOT_TUNGSTEN.get(), ModItems.NUGGET_SCHRABIDIUM.get(),
                new ItemStack(ModItems.INGOT_MAGNETIZED_TUNGSTEN.get()));

        // === OTHER ALLOYS ===
        addRecipe(ModItems.INGOT_STEEL.get(), ModItems.NUGGET_TECHNETIUM.get(),
                new ItemStack(ModItems.INGOT_TCALLOY.get()));

        addRecipe(ModItems.PLATE_GOLD.get(), ModItems.PLATE_MIXED.get(),
                new ItemStack(ModItems.PLATE_PAA.get(), 2));

        addRecipe(ModItems.INGOT_SATURNITE.get(), ModItems.INGOT_METEORITE.get(),
                new ItemStack(ModItems.INGOT_STARMETAL.get(), 2));

        addRecipe(ModItems.INGOT_COBALT.get(), ModItems.POWDER_METEORITE.get(),
                new ItemStack(ModItems.INGOT_METEORITE.get()));

        // === SPECIAL ITEMS ===
        addRecipe(ModToolItems.METEORITE_SWORD_HARDENED.get(), ModItems.INGOT_COBALT.get(),
                new ItemStack(ModToolItems.METEORITE_SWORD_ALLOYED.get()));

        addRecipe(ItemFluidCanister.createForFluid(Fluids.GASOLINE.get()), Items.SLIME_BALL,
                ItemFluidCanister.createForFluid(Fluids.NAPALM.get()));

        // Скрытый рецепт
        addHiddenRecipe(BuiltInRegistries.ITEM.getKey(ModToolItems.METEORITE_SWORD_ALLOYED.get()));
    }

    // Перегрузки для удобства добавления рецептов
    private static void addRecipe(TagKey<Item> input1, TagKey<Item> input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(TagKey<Item> input1, ResourceLocation input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(ResourceLocation input1, TagKey<Item> input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(ResourceLocation input1, ResourceLocation input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(ItemStack input1, Item input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(Item input1, Item input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(Item input1, TagKey<Item> input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(TagKey<Item> input1, Item input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(Item input1, ResourceLocation input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }

    private static void addRecipe(ResourceLocation input1, Item input2, ItemStack output) {
        recipes.add(new BlastFurnaceRecipe(input1, input2, output));
    }


    private static void addHiddenRecipe(ResourceLocation item) {
        hiddenRecipes.add(item);
    }

}