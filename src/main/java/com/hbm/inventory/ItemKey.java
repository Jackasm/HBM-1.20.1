package com.hbm.inventory;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * Ключ для идентификации уникального типа предмета в грузовом контейнере.
 * Учитывает Item, повреждение (damage) и NBT-теги.
 */
public class ItemKey {
    private final String itemId;
    private final int damage;
    private final CompoundTag nbt;

    public ItemKey(String itemId, int damage, CompoundTag nbt) {
        this.itemId = itemId;
        this.damage = damage;
        this.nbt = nbt != null ? nbt.copy() : null;
    }

    @Nullable
    public static ItemKey of(ItemStack stack) {
        if (stack.isEmpty()) return null;
        CompoundTag nbt = stack.hasTag() ? stack.getTag().copy() : null;
        return new ItemKey(
                BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(),
                stack.getDamageValue(),
                nbt
        );
    }

    public ItemStack toItemStack() {
        Item item = BuiltInRegistries.ITEM.get(ResLocation(itemId));
        if (item == null) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(item, 1);
        stack.setDamageValue(damage);
        if (nbt != null) {
            stack.setTag(nbt.copy());
        }
        return stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemKey itemKey = (ItemKey) o;
        return damage == itemKey.damage &&
                itemId.equals(itemKey.itemId) &&
                Objects.equals(nbt, itemKey.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, damage, nbt);
    }

    public String getItemId() {
        return itemId;
    }

    public int getDamage() {
        return damage;
    }

    public CompoundTag getNbt() {
        return nbt != null ? nbt.copy() : null;
    }
}