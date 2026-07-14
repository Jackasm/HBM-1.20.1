package com.hbm.inventory.container;

import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.tileentity.storage.TileEntityMachineFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineFluidTank extends AbstractContainerMenu {

    private final TileEntityMachineFluidTank tank;
    private final ContainerData data;

    public ContainerMachineFluidTank(int id, Inventory playerInv, TileEntityMachineFluidTank te) {
        super(ModContainers.MACHINE_FLUID_TANK.get(), id);
        this.tank = te;
        this.data = te.getContainerData();

        IItemHandler handler = te.getItemHandler();

        // Слоты для жидкости (используем SlotItemHandler)
        this.addSlot(new SlotItemHandler(handler, 0, 8, 17));      // вход для жидкости
        this.addSlot(new SlotItemHandler(handler, 1, 8, 53));      // выход для жидкости
        this.addSlot(new SlotItemHandler(handler, 2, 53 - 18, 17)); // вход 2
        this.addSlot(new SlotItemHandler(handler, 3, 53 - 18, 53)); // выход 2
        this.addSlot(new SlotItemHandler(handler, 4, 125, 17));      // слот для идентификатора
        this.addSlot(new SlotItemHandler(handler, 5, 125, 53));      // слот для идентификатора

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Хотбар
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if (index < 6) {
                // Из слота танка в инвентарь
                if (!this.moveItemStackTo(slotStack, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Из инвентаря в слоты танка
                // Проверяем, можно ли положить предмет в слоты 0-3 или 4-5
                if (slotStack.getItem() instanceof IItemFluidIdentifier) {
                    // Сначала пробуем в слоты идентификаторов (4-5)
                    if (!this.moveItemStackTo(slotStack, 4, 6, false)) {
                        // Если не получилось, пробуем в слоты для жидкостей (0-3)
                        if (!this.moveItemStackTo(slotStack, 0, 4, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    // Обычные предметы только в слоты 0-3
                    if (!this.moveItemStackTo(slotStack, 0, 4, false)) {
                        return ItemStack.EMPTY;
                    }
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
        BlockPos pos = tank.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    public short getMode() {
        return (short) data.get(3);
    }

    public int getFluidFill() {
        return data.get(0);
    }

    public int getFluidCapacity() {
        return data.get(1);
    }

    public int getFluidType() {
        return data.get(2);
    }

    public TileEntityMachineFluidTank getTank() {
        return tank;
    }
}