package com.hbm.inventory.container;

import com.hbm.tileentity.machine.TileEntityMachineRTG;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineRTG extends AbstractContainerMenu {

    private final TileEntityMachineRTG rtg;

    public ContainerMachineRTG(int windowId, Inventory playerInv, TileEntityMachineRTG tile) {
        super(ModContainers.MACHINE_RTG.get(), windowId);
        this.rtg = tile;

        // 15 слотов 5×3
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                this.addSlot(new SlotItemHandler(tile.getInventory(), row * 5 + col,
                        16 + col * 18, 18 + row * 18));
            }
        }

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 106 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 164));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if (index <= 14) {
                if (!this.moveItemStackTo(stack, 15, this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, 15, false)) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return rtg.getBlockPos().distSqr(player.blockPosition()) <= 64.0;
    }

    public TileEntityMachineRTG getRtg() {
        return rtg;
    }
}