package com.hbm.util;

import com.hbm.inventory.recipes.common.AStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.Objects;

public class InventoryUtil {

    public static boolean doesPlayerHaveAStacks(Player player, List<AStack> stacks, boolean shouldRemove) {

        ItemStack[] original = player.getInventory().items.stream()
                .map(ItemStack::copy).toArray(ItemStack[]::new);
        ItemStack[] inventory = player.getInventory().items.stream()
                .map(ItemStack::copy).toArray(ItemStack[]::new);
        boolean[] modified = new boolean[original.length];
        AStack[] input = new AStack[stacks.size()];

        for (int i = 0; i < input.length; i++) {
            input[i] = stacks.get(i).copy();
        }

        for (int i = 0; i < input.length; i++) {
            AStack stack = input[i];
            if (stack == null) continue;

            for (int j = 0; j < inventory.length; j++) {
                ItemStack inv = inventory[j];
                if (inv == null || inv.isEmpty()) continue;

                if (stack.matchesRecipe(inv, true)) {
                    int size = Math.min(stack.getStackSize(), inv.getCount());
                    stack.stacksize -= size;
                    inv.shrink(size);
                    modified[j] = true;

                    if (stack.getStackSize() <= 0) {
                        input[i] = null;
                        break;
                    }

                    if (inv.isEmpty()) {
                        inventory[j] = ItemStack.EMPTY;
                    }
                }
            }
        }

        for (AStack stack : input) {
            if (stack != null) return false;
        }

        if (shouldRemove) {
            for (int i = 0; i < original.length; i++) {
                if (inventory[i] != null && inventory[i].isEmpty()) {
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                } else if (modified[i]) {
                    player.getInventory().items.set(i, inventory[i].copy());
                }
            }
        }

        return true;
    }

    public static boolean doesArrayHaveSpace(IItemHandler handler, int start, int end, ItemStack[] items) {
        int size = end - start + 1;
        ItemStack[] copy = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            ItemStack stack = handler.getStackInSlot(start + i);
            copy[i] = stack.isEmpty() ? null : stack.copy();
        }

        for (ItemStack item : items) {
            if (item == null || item.isEmpty()) continue;
            ItemStack remainder = tryAddItemToInventory(copy, item.copy());
            if (remainder != null && !remainder.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static ItemStack tryAddItemToInventory(ItemStack[] inv, ItemStack stack) {
        return tryAddItemToInventory(inv, 0, inv.length - 1, stack);
    }

    public static ItemStack tryAddItemToInventory(ItemStack[] inv, int start, int end, ItemStack stack) {
        ItemStack rem = tryAddItemToExistingStack(inv, start, end, stack);

        if (rem == null || rem.isEmpty())
            return null;

        boolean didAdd = tryAddItemToNewSlot(inv, start, end, rem);

        if (didAdd)
            return null;
        else
            return rem;
    }

    public static ItemStack tryAddItemToExistingStack(ItemStack[] inv, int start, int end, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        for (int i = start; i <= end; i++) {
            if (doesStackDataMatch(inv[i], stack)) {
                int transfer = Math.min(stack.getCount(), inv[i].getMaxStackSize() - inv[i].getCount());
                if (transfer > 0) {
                    inv[i].grow(transfer);
                    stack.shrink(transfer);
                    if (stack.isEmpty()) return null;
                }
            }
        }
        return stack;
    }

    public static boolean tryAddItemToNewSlot(ItemStack[] inv, int start, int end, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return true;

        for (int i = start; i <= end; i++) {
            if (inv[i] == null || inv[i].isEmpty()) {
                inv[i] = stack;
                return true;
            }
        }
        return false;
    }

    public static ItemStack tryAddItemToInventory(IItemHandler handler, int start, int end, ItemStack stack) {
        ItemStack rem = tryAddItemToExistingStack(handler, start, end, stack);

        if (rem == null || rem.isEmpty())
            return null;

        boolean didAdd = tryAddItemToNewSlot(handler, start, end, rem);

        if (didAdd)
            return null;
        else
            return rem;
    }

    public static ItemStack tryAddItemToExistingStack(IItemHandler handler, int start, int end, ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return null;

        for (int i = start; i <= end; i++) {
            ItemStack invStack = handler.getStackInSlot(i);

            if (doesStackDataMatch(invStack, stack)) {
                int transfer = Math.min(stack.getCount(), invStack.getMaxStackSize() - invStack.getCount());

                if (transfer > 0) {
                    if (handler instanceof net.minecraftforge.items.IItemHandlerModifiable modifiable) {
                        ItemStack newStack = invStack.copy();
                        newStack.grow(transfer);
                        modifiable.setStackInSlot(i, newStack);
                        stack.shrink(transfer);

                        if (stack.isEmpty())
                            return null;
                    }
                }
            }
        }

        return stack;
    }

    public static boolean tryAddItemToNewSlot(IItemHandler handler, int start, int end, ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return true;

        for (int i = start; i <= end; i++) {
            ItemStack invStack = handler.getStackInSlot(i);
            if (invStack.isEmpty()) {
                if (handler instanceof net.minecraftforge.items.IItemHandlerModifiable modifiable) {
                    modifiable.setStackInSlot(i, stack.copy());
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean doesStackDataMatch(ItemStack stack1, ItemStack stack2) {

        if(stack1 == null && stack2 == null) return true;
        if(stack1 == null) return false;
        if(stack2 == null) return false;
        if(stack1.getItem() != stack2.getItem()) return false;
        if(stack1.getDamageValue() != stack2.getDamageValue()) return false;
        if(!stack1.hasTag() && !stack2.hasTag()) return true;
        if(stack1.hasTag() && !stack2.hasTag()) return false;
        if(!stack1.hasTag() && stack2.hasTag()) return false;

        return Objects.requireNonNull(stack1.getTag()).equals(stack2.getTag());
    }

    public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int start, int end, boolean reverse) {
        boolean success = false;
        int index = start;

        if (reverse) {
            index = end - 1;
        }

        Slot slot;
        ItemStack current;

        if (stack.getCount() > 0 && stack.isStackable()) {
            while (stack.getCount() > 0 && (!reverse && index < end || reverse && index >= start)) {
                slot = slots.get(index);
                current = slot.getItem();

                if (!current.isEmpty()) {
                    int max = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    int toRemove = Math.min(stack.getCount(), max);

                    if (slot.mayPlace(copyWithSize(stack, toRemove)) &&
                            current.getItem() == stack.getItem() &&
                            ItemStack.isSameItemSameTags(stack, current)) {

                        int currentSize = current.getCount() + stack.getCount();
                        if (currentSize <= max) {
                            stack.setCount(0);
                            current.setCount(currentSize);
                            slot.set(current);
                            success = true;
                        } else if (current.getCount() < max) {
                            stack.shrink(max - current.getCount());
                            current.setCount(max);
                            slot.set(current);
                            success = true;
                        }
                    }
                }

                if (reverse) {
                    --index;
                } else {
                    ++index;
                }
            }
        }

        if (stack.getCount() > 0) {
            if (reverse) {
                index = end - 1;
            } else {
                index = start;
            }

            while ((!reverse && index < end || reverse && index >= start) && stack.getCount() > 0) {
                slot = slots.get(index);
                current = slot.getItem();

                if (current.isEmpty()) {
                    int max = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    int toRemove = Math.min(stack.getCount(), max);

                    if (slot.mayPlace(copyWithSize(stack, toRemove))) {
                        ItemStack copy = stack.copy();
                        copy.setCount(toRemove);
                        stack.shrink(toRemove);
                        slot.set(copy);
                        success = true;
                    }
                }

                if (reverse) {
                    --index;
                } else {
                    ++index;
                }
            }
        }

        return success;
    }

    private static ItemStack copyWithSize(ItemStack stack, int size) {
        ItemStack copy = stack.copy();
        copy.setCount(size);
        return copy;
    }

    /**
     * Counts how many items matching the AStack are in the inventory
     * @param inventory The inventory array
     * @param stack The stack to match
     * @param ignoreSize If true, returns total item count; if false, returns number of full stacks
     * @return Count of matching items
     */
    public static int countAStackMatches(ItemStack[] inventory, AStack stack, boolean ignoreSize) {
        if (stack == null || inventory == null) return 0;
        int count = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack != null && !itemStack.isEmpty()) {
                if (stack.matchesRecipe(itemStack, true)) {
                    count += itemStack.getCount();
                }
            }
        }
        return ignoreSize ? count : count / stack.getStackSize();
    }

    /**
     * Counts how many items matching the AStack are in the player's inventory
     * @param player The player
     * @param stack The stack to match
     * @param ignoreSize If true, returns total item count; if false, returns number of full stacks
     * @return Count of matching items
     */
    public static int countAStackMatches(Player player, AStack stack, boolean ignoreSize) {
        return countAStackMatches(player.getInventory().items.toArray(new ItemStack[0]), stack, ignoreSize);
    }

    /**
     * Tries to consume items matching the AStack from the inventory
     * @param inv The inventory array
     * @param start Start index (inclusive)
     * @param end End index (inclusive)
     * @param stack The stack to consume
     * @return true if all items were consumed, false otherwise
     */
    public static boolean tryConsumeAStack(ItemStack[] inv, int start, int end, AStack stack) {
        if (stack == null) return true;

        AStack copy = stack.copy();

        for (int i = start; i <= end; i++) {
            ItemStack in = inv[i];
            if (in == null || in.isEmpty()) continue;

            if (stack.matchesRecipe(in, true)) {
                int size = Math.min(copy.getStackSize(), in.getCount());
                in.shrink(size);
                copy.stacksize -= size;

                if (in.isEmpty()) {
                    inv[i] = ItemStack.EMPTY;
                }
                if (copy.getStackSize() <= 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
