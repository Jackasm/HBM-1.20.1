package com.hbm.inventory.recipes.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class OreDictStack extends AStack {

    public String name;
    private TagKey<Item> cachedTag;

    public OreDictStack(String name) {
        this.name = name;
        this.stacksize = 1;
    }

    public OreDictStack(String name, int stacksize) {
        this(name);
        this.stacksize = stacksize;
    }

    private TagKey<Item> getTag() {
        if (cachedTag == null) {
            ResourceLocation loc;
            if (name.startsWith("forge:")) {
                loc =ResLocation(name);
            } else {
                loc = ResLocation("forge", name.replaceFirst("forge:", ""));
            }
            cachedTag = ItemTags.create(loc);
        }
        return cachedTag;
    }

    public List<ItemStack> toStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(getTag()).forEach(holder -> {
            ItemStack stack = new ItemStack(holder.value());
            stacks.add(stack);
        });
        return stacks;
    }

    @Override
    public AStack copy() {
        return new OreDictStack(name, stacksize);
    }

    public OreDictStack copy(int stacksize) {
        return new OreDictStack(name, stacksize);
    }

    @Override
    public boolean matchesRecipe(ItemStack stack, boolean ignoreSize) {
        if (stack == null || stack.isEmpty())
            return false;

        if (!ignoreSize && stack.getCount() < this.stacksize)
            return false;

        return stack.is(getTag());
    }

    @Override
    public List<ItemStack> extractForNEI() {
        List<ItemStack> fromDict = toStacks();
        List<ItemStack> ores = new ArrayList<>();

        for (ItemStack stack : fromDict) {
            ItemStack copy = stack.copy();
            copy.setCount(this.stacksize);
            ores.add(copy);
        }

        return ores;
    }

    @Override
    public int getStackSize() {
        return this.stacksize;
    }

    @Override
    public boolean isIngredientSame(AStack stack) {
        if (this == stack) return true;
        if (stack == null || getClass() != stack.getClass()) return false;
        OreDictStack that = (OreDictStack) stack;
        return this.stacksize == that.stacksize && Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, stacksize);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OreDictStack other = (OreDictStack) obj;
        return Objects.equals(name, other.name) && this.stacksize == other.stacksize;
    }
}