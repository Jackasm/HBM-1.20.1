package com.hbm.inventory.recipes.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Аналог ItemStack с сравнением для рецептов - замена старого ComparableStack для 1.20.1
 */
public class ComparableStack extends AStack {
    public Item item;
    public int stacksize;
    public CompoundTag nbt;

    public ComparableStack(Item item) {
        this(item, 1);
    }

    public ComparableStack(Item item, int stacksize) {
        this(item, stacksize, null);
    }

    public ComparableStack(Item item, int stacksize, CompoundTag nbt) {
        this.item = item;
        this.stacksize = stacksize;
        this.nbt = nbt;
    }

    public ComparableStack(Item item, int stacksize, int customModelData) {
        this.item = item;
        this.stacksize = stacksize;
        if (customModelData > 0) {
            this.nbt = new CompoundTag();
            this.nbt.putInt("CustomModelData", customModelData);
        }
    }

    // Новый конструктор для Block
    public ComparableStack(Block block) {
        this(block.asItem(), 1);
    }

    // Новый конструктор для Block с количеством
    public ComparableStack(Block block, int stacksize) {
        this(block.asItem(), stacksize, null);
    }

    // Новый конструктор для Block с количеством и NBT
    public ComparableStack(Block block, int stacksize, CompoundTag nbt) {
        this(block.asItem(), stacksize, nbt);
    }

    public ComparableStack(ItemStack stack) {
        this(stack.getItem(), stack.getCount(), stack.getTag() != null ? stack.getTag().copy() : null);
    }

    public ComparableStack(ItemStack stack, int stacksize) {
        this(stack.getItem(), stacksize, stack.getTag() != null ? stack.getTag().copy() : null);
    }

    public Item getItem() {
        return this.item;
    }

    @Override
    public boolean matchesRecipe(ItemStack stack, boolean container) {
        if(stack.isEmpty()) return false;

        // Проверка предмета
        if(stack.getItem() != this.item) return false;

        // Проверка количества
        if(!container && stack.getCount() < this.stacksize) return false;

        // Проверка NBT
        if(this.nbt != null) {
            if(stack.getTag() == null) return false;
            return compareNBT(this.nbt, stack.getTag());
        }

        return true;
    }

    private boolean compareNBT(CompoundTag expected, CompoundTag actual) {
        // Простая проверка NBT - можно расширить при необходимости
        return expected.equals(actual);
    }

    @Override
    public List<ItemStack> extractForNEI() {
        List<ItemStack> list = new ArrayList<>();
        list.add(this.toStack());
        return list;
    }

    @Override
    public int getStackSize() {
        return stacksize;
    }

    @Override
    public AStack copy() {
        return new ComparableStack(item, stacksize, nbt != null ? nbt.copy() : null);
    }

    @Override
    public boolean isIngredientSame(AStack stack) {
        if(!(stack instanceof ComparableStack other)) return false;
        return this.item == other.item && Objects.equals(this.nbt, other.nbt);
    }

    public ItemStack toStack() {
        ItemStack stack = new ItemStack(item, stacksize);
        if(nbt != null) {
            stack.setTag(nbt.copy());
        }
        return stack;
    }

    public String getItemName() {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof ComparableStack other)) return false;

        return this.item == other.item &&
                this.stacksize == other.stacksize &&
                Objects.equals(this.nbt, other.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, stacksize, nbt);
    }

    @Override
    public String toString() {
        String nbtStr = nbt != null ? nbt.toString() : "null";
        return "ComparableStack{item=" + getItemName() + ", size=" + stacksize + ", nbt=" + nbtStr + "}";
    }

    public ComparableStack makeSingular() {
        stacksize = 1;
        return this;
    }
}