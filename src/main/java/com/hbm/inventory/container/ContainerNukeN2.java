package com.hbm.inventory.container;

import com.hbm.tileentity.bomb.TileEntityNukeN2;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerNukeN2 extends AbstractContainerMenu {

    private final TileEntityNukeN2 nuke;

    public ContainerNukeN2(int windowId, Inventory invPlayer, TileEntityNukeN2 te) {
        super(ModContainers.NUKE_N2.get(), windowId);
        this.nuke = te;

        IItemHandler handler = te.getInventory();

        // 12 слотов для N2 зарядов (3x4 сетка)
        int[] xPos = {98, 116, 134};
        int[] yPos = {36, 54, 72, 90};
        int slotIndex = 0;
        for (int row = 0; row < yPos.length; row++) {
            for (int col = 0; col < xPos.length; col++) {
                this.addSlot(new SlotItemHandler(handler, slotIndex++, xPos[col], yPos[row]));
            }
        }

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 56));
            }
        }

        // Хотбар
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142 + 56));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            // Если слот принадлежит машине (0-11)
            if (index <= 11) {
                if (!this.moveItemStackTo(slotStack, 12, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        BlockPos pos = nuke.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    public TileEntityNukeN2 getNuke() {
        return nuke;
    }
}