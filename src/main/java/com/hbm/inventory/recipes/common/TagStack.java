package com.hbm.inventory.recipes.common;

import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Аналог OreDictStack для работы с тегами в Minecraft 1.20.1+
 */
public class TagStack extends AStack {
    public final TagKey<Item> tag;
    public final int stacksize;

    public TagStack(TagKey<Item> tag) {
        this(tag, 1);
    }

    public TagStack(TagKey<Item> tag, int stacksize) {
        this.tag = tag;
        this.stacksize = stacksize;
    }

    @Override
    public boolean matchesRecipe(ItemStack stack, boolean container) {
        if(stack.isEmpty()) return false;
        return stack.is(tag) && stack.getCount() >= stacksize;
    }

    @Override
    public List<ItemStack> extractForNEI() {
        return BuiltInRegistries.ITEM.getTag(tag)
                .stream()
                .flatMap(HolderSet.ListBacked::stream)
                .map(holder -> new ItemStack(holder.value(), stacksize))
                .collect(Collectors.toList());
    }

    public List<String> getAllDisplayNames() {
        return BuiltInRegistries.ITEM.getTag(tag)
                .stream()
                .flatMap(HolderSet.ListBacked::stream)
                .map(holder -> holder.value().getDescription().getString())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public int getStackSize() {
        return stacksize;
    }

    @Override
    public AStack copy() {
        return new TagStack(tag, stacksize);
    }

    @Override
    public boolean isIngredientSame(AStack stack) {
        return stack instanceof TagStack && ((TagStack) stack).tag.equals(this.tag);
    }

    public ResourceLocation getTagLocation() {
        return tag.location();
    }

    @Override
    public String toString() {
        return "TagStack{tag=" + tag.location() + ", size=" + stacksize + "}";
    }
}