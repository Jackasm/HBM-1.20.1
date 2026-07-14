package com.hbm.inventory.container;

import com.hbm.inventory.recipes.SolderingRecipes;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.tileentity.machine.TileEntityMachineSolderingStation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineSolderingStation extends AbstractContainerMenu {

    private final TileEntityMachineSolderingStation solderer;

    public ContainerMachineSolderingStation(int windowId, Inventory playerInv, TileEntityMachineSolderingStation tile) {
        super(ModContainers.MACHINE_SOLDERING_STATION.get(), windowId);
        this.solderer = tile;

        IItemHandler handler = tile.getItemHandler();

        // Input slots (0-5)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(handler, i * 3 + j, 17 + j * 18, 18 + i * 18));
            }
        }

        this.addSlot(new SlotItemHandler(handler, 6, 107, 27) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        this.addSlot(new SlotItemHandler(handler, 7, 152, 72));

        this.addSlot(new SlotItemHandler(handler, 8, 17, 63) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof IItemFluidIdentifier;
            }
        });

        this.addSlot(new SlotItemHandler(handler, 9, 89, 63) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof ItemMachineUpgrade;
            }
        });
        this.addSlot(new SlotItemHandler(handler, 10, 107, 63) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof ItemMachineUpgrade;
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 122 + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 180));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack rStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            rStack = stack.copy();

            // Если слот принадлежит машине (0-10)
            if (index <= 10) {
                if (!this.moveItemStackTo(stack, 11, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Попытка положить в соответствующий слот машины
                boolean moved = false;

                // Батареи
                if (rStack.getMaxStackSize() == 1) { // простое определение батарей
                    if (this.moveItemStackTo(stack, 7, 8, false)) moved = true;
                }
                // Идентификаторы жидкостей
                else if (rStack.getItem() instanceof IItemFluidIdentifier) {
                    if (this.moveItemStackTo(stack, 8, 9, false)) moved = true;
                }
                // Апгрейды
                else if (rStack.getItem() instanceof ItemMachineUpgrade) {
                    if (this.moveItemStackTo(stack, 9, 11, false)) moved = true;
                }
                // Компоненты рецептов
                else {
                    for (var t : SolderingRecipes.toppings) {
                        if (t.matchesRecipe(stack, false)) {
                            if (this.moveItemStackTo(stack, 0, 3, false)) moved = true;
                            break;
                        }
                    }
                    if (!moved) {
                        for (var t : SolderingRecipes.pcb) {
                            if (t.matchesRecipe(stack, false)) {
                                if (this.moveItemStackTo(stack, 3, 5, false)) moved = true;
                                break;
                            }
                        }
                    }
                    if (!moved) {
                        for (var t : SolderingRecipes.solder) {
                            if (t.matchesRecipe(stack, false)) {
                                if (this.moveItemStackTo(stack, 5, 6, false)) moved = true;
                                break;
                            }
                        }
                    }
                }

                if (!moved) {
                    return ItemStack.EMPTY;
                }
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
        return solderer.isUsableByPlayer(player);
    }

    public TileEntityMachineSolderingStation getSolderingStation() {
        return solderer;
    }
}