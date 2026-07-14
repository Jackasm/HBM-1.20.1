package com.hbm.inventory.container;


import com.hbm.tileentity.storage.TileEntityMachineBattery;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineBattery extends AbstractContainerMenu {

    private final TileEntityMachineBattery battery;
    private final IItemHandler handler;
    private final ContainerData data;

    public ContainerMachineBattery(int windowId, Inventory invPlayer, TileEntityMachineBattery te) {
        super(ModContainers.MACHINE_BATTERY.get(), windowId);
        this.battery = te;
        this.handler = te.getItemHandler();
        this.data = te.getContainerData();

        // Слоты: 0 - приём батарей (заряд), 1 - выдача батарей (разряд)
        this.addSlot(new SlotItemHandler(handler, 0, 26, 17));
        this.addSlot(new SlotItemHandler(handler, 1, 26, 53));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142));
        }

        this.addDataSlots(data);
    }

    public long getPower() { return data.get(0) | ((long) data.get(1) << 32); }
    public long getMaxPower() { return data.get(2) | ((long) data.get(3) << 32); }
    public short getRedLow() { return (short) data.get(4); }
    public short getRedHigh() { return (short) data.get(5); }
    public int getPriority() { return data.get(6); }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem().copy();

        if (index <= 1) {
            if (!this.moveItemStackTo(stack, 2, this.slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            // Пытаемся положить в слот 0 или 1
            if (!this.moveItemStackTo(stack, 0, 1, false)) {
                if (!this.moveItemStackTo(stack, 1, 2, false))
                    return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return battery.getBlockPos().distSqr(player.blockPosition()) <= 64.0;
    }

    public TileEntityMachineBattery getBattery() {
        return battery;
    }
}