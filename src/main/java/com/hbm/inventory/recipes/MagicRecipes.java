package com.hbm.inventory.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagicRecipes {

    private static final List<MagicRecipe> recipes = new ArrayList<>();

    public static ItemStack getRecipe(List<ItemStack> matrix) {
        List<ComparableStack> comps = new ArrayList<>();

        for (int i = 0; i < 4 && i < matrix.size(); i++) {
            ItemStack stack = matrix.get(i);
            if (stack != null && !stack.isEmpty()) {
                comps.add(new ComparableStack(stack).makeSingular());
            }
        }

        for (MagicRecipe recipe : recipes) {
            if (recipe.matches(comps)) {
                return recipe.getResult();
            }
        }

        return null;
    }

    public static void register() {
        recipes.add(new MagicRecipe(new ItemStack(ModItems.INGOT_U238M2.get()),
                new ComparableStack(ModItems.INGOT_U238M2.get(), 1, 1),
                new ComparableStack(ModItems.INGOT_U238M2.get(), 1, 2),
                new ComparableStack(ModItems.INGOT_U238M2.get(), 1, 3)));

        recipes.add(new MagicRecipe(new ItemStack(ModToolItems.ROD_OF_DISCORD.get()),
                new ComparableStack(Items.ENDER_PEARL),
                new ComparableStack(Items.BLAZE_ROD),
                new ComparableStack(ModItems.NUGGET_EUPHEMIUM.get())));

        recipes.add(new MagicRecipe(new ItemStack(ModItems.BALEFIRE_AND_STEEL.get()),
                new ComparableStack(ModItems.INGOT_STEEL.get()),
                new ComparableStack(ModItems.EGG_BALEFIRE_SHARD.get())));

        recipes.add(new MagicRecipe(new ItemStack(ModItems.MYSTERYSHOVEL.get()),
                new ComparableStack(Items.IRON_SHOVEL),
                new ComparableStack(Items.BONE),
                new ComparableStack(ModItems.INGOT_STARMETAL.get()),
                new ComparableStack(ModItems.DUCTTAPE.get())));

        recipes.add(new MagicRecipe(new ItemStack(ModItems.INGOT_ELECTRONIUM.get()),
                new ComparableStack(ModItems.PELLET_CHARGED.get()),
                new ComparableStack(ModItems.PELLET_CHARGED.get()),
                new ComparableStack(ModItems.INGOT_DINEUTRONIUM.get()),
                new ComparableStack(ModItems.INGOT_DINEUTRONIUM.get())));

        recipes.add(new MagicRecipe(new ItemStack(ModToolItems.DIAMOND_GAVEL.get()),
                new ComparableStack(ModBlocks.GRAVEL_DIAMOND.get().asItem()),
                new ComparableStack(ModBlocks.GRAVEL_DIAMOND.get().asItem()),
                new ComparableStack(ModBlocks.GRAVEL_DIAMOND.get().asItem()),
                new ComparableStack(ModToolItems.LEAD_GAVEL.get())));

        recipes.add(new MagicRecipe(new ItemStack(ModToolItems.MESE_GAVEL.get()),
                new ComparableStack(ModItems.SHIMMER_HANDLE.get()),
                new ComparableStack(ModItems.POWDER_DINEUTRONIUM.get()),
                new ComparableStack(ModItems.BLADES_DESH.get()),
                new ComparableStack(ModToolItems.DIAMOND_GAVEL.get())));
    }

    public static List<MagicRecipe> getRecipes() {
        return recipes;
    }

    public static class MagicRecipe {

        public List<AStack> in;
        public ItemStack out;

        public MagicRecipe(ItemStack out, AStack... in) {
            this.out = out;
            this.in = Arrays.asList(in);
        }

        public boolean matches(List<ComparableStack> comps) {
            if (comps.size() != in.size()) {
                return false;
            }

            for (int i = 0; i < in.size(); i++) {
                ComparableStack comp = comps.get(i);
                ItemStack stack = comp.toStack();
                if (!in.get(i).matchesRecipe(stack, false)) {
                    return false;
                }
            }

            return true;
        }

        public ItemStack getResult() {
            return out.copy();
        }
    }
}