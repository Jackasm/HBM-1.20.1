package com.hbm.inventory.container;

import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerNukeCustom extends AbstractContainerMenu {

    private final TileEntityNukeCustom nukeBoy;
    private final ContainerData data;

    public ContainerNukeCustom(int windowId, Inventory invPlayer, TileEntityNukeCustom te) {
        super(ModContainers.NUKE_CUSTOM.get(), windowId);
        this.nukeBoy = te;

        IItemHandler handler = te.getItemHandler();

        // 27 слотов (3 ряда по 9)
        int slotIndex = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                this.addSlot(new SlotItemHandler(handler, slotIndex++, x, y));
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

        // Синхронизация данных
        this.data = new SimpleContainerData(7);
        this.data.set(0, (int) te.tnt);
        this.data.set(1, (int) te.nuke);
        this.data.set(2, (int) te.hydro);
        this.data.set(3, (int) te.amat);
        this.data.set(4, (int) te.dirty);
        this.data.set(5, (int) te.schrab);
        this.data.set(6, (int) te.euph);
        this.addDataSlots(data);
    }

    public float getTnt() { return data.get(0); }
    public float getNuke() { return data.get(1); }
    public float getHydro() { return data.get(2); }
    public float getAmat() { return data.get(3); }
    public float getDirty() { return data.get(4); }
    public float getSchrab() { return data.get(5); }
    public float getEuph() { return data.get(6); }

    public TileEntityNukeCustom getNukeBoy() {
        return nukeBoy;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if (index <= 26) {
                if (!this.moveItemStackTo(slotStack, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
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
        BlockPos pos = nukeBoy.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }
}