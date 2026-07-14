package com.hbm.inventory.container;

import com.hbm.inventory.recipes.MagicRecipes;
import com.hbm.items.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContainerBook extends AbstractContainerMenu {

    private final CraftingContainer craftMatrix;
    private final ResultContainer craftResult;
    private final Player player;

    public ContainerBook(int id, Inventory invPlayer) {
        super(ModContainers.BOOK.get(), id);
        this.player = invPlayer.player;
        this.craftMatrix = new TransientCraftingContainer(this, 2, 2);
        this.craftResult = new ResultContainer();

        // Слот результата
        this.addSlot(new Slot(this.craftResult, 0, 124, 35));

        // Слоты крафта 2x2 (4 слота)
        for (int row = 0; row < 2; ++row) {
            for (int col = 0; col < 2; ++col) {
                this.addSlot(new Slot(this.craftMatrix, col + row * 2, 30 + col * 36, 17 + row * 36));
            }
        }

        // Инвентарь игрока
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(invPlayer, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Хотбар игрока
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(invPlayer, col, 8 + col * 18, 142));
        }

        this.slotsChanged(this.craftMatrix);
    }

    @Override
    public void slotsChanged(@NotNull Container inventory) {
        List<ItemStack> matrix = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            matrix.add(this.craftMatrix.getItem(i));
        }
        this.craftResult.setItem(0, MagicRecipes.getRecipe(matrix));
        super.slotsChanged(inventory);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        if (!player.level().isClientSide) {
            for (int i = 0; i < 4; ++i) {
                ItemStack stack = this.craftMatrix.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {
                    player.drop(stack, false);
                }
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack resultStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            resultStack = stack.copy();

            int slotCount = 1 + 4; // результат + 4 слота крафта

            if (index == 0) {
                // Из слота результата в инвентарь
                if (!this.moveItemStackTo(stack, slotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, resultStack);
            } else if (index < slotCount) {
                // Из слотов крафта в инвентарь
                if (!this.moveItemStackTo(stack, slotCount, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 1, slotCount, false)) {
                // Из инвентаря в слоты крафта
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == resultStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return resultStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player.getInventory().hasAnyMatching(
                s -> s.getItem() == ModItems.BOOK_OF_.get()
        );
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        return slot.container != this.craftResult && super.canTakeItemForPickAll(stack, slot);
    }
}