package com.hbm.inventory.container;

import com.hbm.tileentity.storage.TileEntitySoyuzCapsule;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerSoyuzCapsule extends AbstractContainerMenu {

    private final TileEntitySoyuzCapsule capsule;

    public ContainerSoyuzCapsule(int id, Inventory inv, TileEntitySoyuzCapsule te) {
        super(ModContainers.SOYUZ_CAPSULE.get(), id);
        this.capsule = te;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                this.addSlot(new Slot(te, j + i * 6, 26 + j * 18 + 18 * 2, 18 + i * 18));
            }
        }
        this.addSlot(new Slot(te, 18, 17, 36));

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

            if (index < capsule.getContainerSize()) {
                if (!this.moveItemStackTo(stack, capsule.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stack, 0, capsule.getContainerSize(), false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return capsule.stillValid(player);
    }
}