package com.hbm.inventory.container;

import com.hbm.tileentity.machine.TileEntityMachineKeyForge;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineKeyForge extends AbstractContainerMenu {

    private final TileEntityMachineKeyForge forge;

    public ContainerMachineKeyForge(int id, Inventory inv, TileEntityMachineKeyForge te) {
        super(ModContainers.MACHINE_KEY_FORGE.get(), id);
        this.forge = te;

        this.addSlot(new Slot(te, 0, 44, 36));
        this.addSlot(new Slot(te, 1, 80, 36));
        this.addSlot(new Slot(te, 2, 116, 36));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 162));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();

            if (index <= 0) {
                if (!this.moveItemStackTo(stack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return forge.stillValid(player);
    }
}