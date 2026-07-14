package com.hbm.inventory.container;

import com.hbm.tileentity.storage.TileEntityBarrel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerBarrel extends AbstractContainerMenu {

    public final TileEntityBarrel barrel;
    private final ContainerData data;
    private int lastFluidFill = -1;
    private int lastFluidType = -1;
    private short lastMode = -1;

    public ContainerBarrel(int windowId, Inventory playerInventory, TileEntityBarrel barrel) {
        super(ModContainers.BARREL.get(), windowId); // Замените null на ваш MenuType если нужно
        this.barrel = barrel;
        this.data = barrel.getContainerData();


        // Добавляем слоты бочки
        addSlot(new SlotItemHandler(barrel.getItemHandler(), 0, 8, 17)); // Идентификатор жидкости вход
        addSlot(new SlotItemHandler(barrel.getItemHandler(), 1, 8, 53)); // Идентификатор жидкости выход
        addSlot(new SlotItemHandler(barrel.getItemHandler(), 2, 35, 17)); // Заполнение вход
        addSlot(new SlotItemHandler(barrel.getItemHandler(), 3, 35, 53)); // Заполнение выход
        addSlot(new SlotItemHandler(barrel.getItemHandler(), 4, 125, 17)); // Опорожнение вход
        addSlot(new SlotItemHandler(barrel.getItemHandler(), 5, 125, 53)); // Опорожнение выход

        // Добавляем слоты инвентаря игрока
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Добавляем слоты горячей панели игрока
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        // Добавляем отслеживание данных
        addDataSlots(data);
    }



    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        // Проверяем изменения и принудительно обновляем если нужно
        int currentFill = data.get(0);
        int currentType = data.get(2);
        short currentMode = (short) data.get(3);

        if (currentFill != lastFluidFill || currentType != lastFluidType || currentMode != lastMode) {
            lastFluidFill = currentFill;
            lastFluidType = currentType;
            lastMode = currentMode;

            // Принудительно обновляем данные для клиента
            setData(0, currentFill);
            setData(2, currentType);
            setData(3, currentMode);
        }
    }

    // Геттеры для GUI
    public int getFluidFill() {
        return data.get(0);
    }

    public int getFluidCapacity() {
        return data.get(1);
    }

    public int getFluidType() {
        return data.get(2);
    }

    public short getMode() {
        return (short) data.get(3);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        BlockPos pos = barrel.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // Слоты бочки: 0-5
            if (index < 6) {
                // Перенос ИЗ бочки в инвентарь игрока - разрешаем для всех слотов
                if (!this.moveItemStackTo(itemstack1, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Перенос ИЗ инвентаря игрока в бочку - только в верхние слоты (0,2,4)
                if (!this.moveItemStackTo(itemstack1, 0, 6, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}