package com.hbm.integration.jei.combinationfurnace;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.recipes.CombinationRecipes;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.util.Tuple.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.hbm.util.ResLocation.ResLocation;

public class CombinationFurnaceRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        for (var entry : CombinationRecipes.recipes.entrySet()) {
            Object key = entry.getKey();
            Pair<ItemStack, FluidStackHBM> outputPair = entry.getValue();

            List<ItemStack> inputVariants = getInputVariants(key);
            if (inputVariants.isEmpty()) continue;

            ItemStack outputItem = outputPair.key();
            FluidStackHBM fluidHBM = outputPair.value();

            FluidColorIngredient fluidIngredient = null;
            if (fluidHBM != null && fluidHBM.type != null && fluidHBM.fill > 0) {
                String registryName = fluidHBM.type.getName().toLowerCase(Locale.ROOT);
                fluidIngredient = new FluidColorIngredient(
                        fluidHBM.type.getLocalizedName(),
                        registryName,
                        fluidHBM.fill,
                        fluidHBM.type.getColor(),
                        fluidHBM.type.getTexture()
                );
            }

            recipes.add(new CombinationFurnaceRecipeWrapper(inputVariants, outputItem, fluidIngredient));
        }

        return recipes;
    }

    private static List<ItemStack> getInputVariants(Object key) {
        List<ItemStack> list = new ArrayList<>();

        if (key instanceof ComparableStack comp) {
            list.add(comp.toStack());
        } else if (key instanceof String tagName) {
            ResourceLocation loc = tagName.contains(":")
                    ? ResLocation(tagName)
                    : ResLocation("forge", tagName);
            TagKey<Item> tag = ItemTags.create(loc);
            var items = BuiltInRegistries.ITEM.getTagOrEmpty(tag);
            for (var holder : items) {
                list.add(new ItemStack(holder.value()));
            }
        } else if (key instanceof TagKey) {
            @SuppressWarnings("unchecked")
            TagKey<Item> tag = (TagKey<Item>) key;
            var items = BuiltInRegistries.ITEM.getTagOrEmpty(tag);
            for (var holder : items) {
                list.add(new ItemStack(holder.value()));
            }
        }

        return list;
    }
}