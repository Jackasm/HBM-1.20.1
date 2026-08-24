package com.hbm.inventory.container;

import com.hbm.inventory.recipes.AmmoPressRecipes;
import com.hbm.tileentity.machine.TileEntityMachineAmmoPress;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineAmmoPress extends AbstractContainerMenu {

    private final TileEntityMachineAmmoPress press;

    public ContainerMachineAmmoPress(int windowId, Inventory playerInv, TileEntityMachineAmmoPress tile) {
        super(ModContainers.MACHINE_AMMO_PRESS.get(), windowId);
        this.press = tile;

        IItemHandler handler = tile.getInventory();

        // Input slots (0-8)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(handler, i * 3 + j, 116 + j * 18, 18 + i * 18));
            }
        }

        // Output slot (9)
        this.addSlot(new SlotItemHandler(handler, 9, 134, 72) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 118 + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 176));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack rStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            rStack = stack.copy();

            if (index <= 9) {
                if (!this.moveItemStackTo(stack, 10, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (press.selectedRecipe < 0 || press.selectedRecipe >= AmmoPressRecipes.recipes.size()) {
                    return ItemStack.EMPTY;
                }

                AmmoPressRecipes.AmmoPressRecipe recipe = AmmoPressRecipes.recipes.get(press.selectedRecipe);

                for (int i = 0; i < 9; i++) {
                    if (recipe.input[i] == null) continue;
                    if (recipe.input[i].matchesRecipe(stack, true)) {
                        if (!this.moveItemStackTo(stack, i, i + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                        break;
                    }
                }
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return rStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return press.isUsableByPlayer(player);
    }

    public TileEntityMachineAmmoPress getAmmoPress() {
        return press;
    }
}