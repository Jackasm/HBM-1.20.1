package com.hbm.inventory.container;

import com.hbm.inventory.SlotNonRetarded;
import com.hbm.items.tool.ItemToolBox.InventoryToolBox;
import com.hbm.util.InventoryUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerToolBox extends AbstractContainerMenu {

    private final InventoryToolBox box;
    private final int boxSize;

    public ContainerToolBox(int id, Inventory invPlayer, InventoryToolBox box) {
        super(ModContainers.TOOLBOX.get(), id);
        this.box = box;
        this.boxSize = box.getContainerSize();
        this.box.startOpen(invPlayer.player);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                this.addSlot(new SlotNonRetarded(box, j + i * 8, 17 + j * 18, 49 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 129 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 187));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack resultStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            resultStack = stack.copy();

            if (index < boxSize) {
                if (!InventoryUtil.mergeItemStack(this.slots, stack, boxSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!InventoryUtil.mergeItemStack(this.slots, stack, 0, boxSize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(player, stack);
        }

        return resultStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.box.stopOpen(player);
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType action, @NotNull Player player) {
        // prevents the player from moving around the currently open box
        if (action == ClickType.QUICK_MOVE && button == player.getInventory().selected) {
            return;
        }
        if (slotId == player.getInventory().selected + 51) {
            return;
        }
        super.clicked(slotId, button, action, player);
    }

    public InventoryToolBox getInventoryBox() {
        return box;
    }
}