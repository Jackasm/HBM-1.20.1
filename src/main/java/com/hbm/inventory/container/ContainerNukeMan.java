package com.hbm.inventory.container;

import com.hbm.tileentity.bomb.TileEntityNukeMan;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerNukeMan extends AbstractContainerMenu {

    private final TileEntityNukeMan nukeMan;

    public ContainerNukeMan(int id, Inventory inv, TileEntityNukeMan te) {
        super(ModContainers.NUKE_MAN.get(), id);
        this.nukeMan = te;

        this.addSlot(new Slot(te, 0, 26, 35));
        this.addSlot(new Slot(te, 1, 8, 17));
        this.addSlot(new Slot(te, 2, 44, 17));
        this.addSlot(new Slot(te, 3, 8, 53));
        this.addSlot(new Slot(te, 4, 44, 53));
        this.addSlot(new Slot(te, 5, 98, 35));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();

            if (index <= 5) {
                if (!this.moveItemStackTo(stack, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return nukeMan.stillValid(player);
    }

    public TileEntityNukeMan getNukeMan() { return nukeMan; }
}