package com.hbm.inventory.container;

import com.hbm.inventory.SlotCraftingOutputItemHandler;
import com.hbm.tileentity.machine.TileEntityBlastFurnace;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerBlastFurnace extends AbstractContainerMenu {
    private final TileEntityBlastFurnace furnace;
    private final ContainerData data;

    public ContainerBlastFurnace(int containerId, Inventory playerInventory, TileEntityBlastFurnace furnace) {
        super(ModContainers.BLAST_FURNACE.get(), containerId);
        this.furnace = furnace;
        this.data = furnace.getContainerData();

        // ВАЖНО: регистрируем данные для синхронизации
        addDataSlots(data);

        initSlots(playerInventory);
    }

    private void initSlots(Inventory playerInventory) {
        // Добавляем слоты печи - теперь используем SlotItemHandler
        this.addSlot(new SlotItemHandler(furnace.getInventory(), TileEntityBlastFurnace.SLOT_FUEL, 8, 36));
        this.addSlot(new SlotItemHandler(furnace.getInventory(), TileEntityBlastFurnace.SLOT_UPPER, 80, 18));
        this.addSlot(new SlotItemHandler(furnace.getInventory(), TileEntityBlastFurnace.SLOT_LOWER, 80, 54));


        // Используем SlotCraftingOutputItemHandler для выхода
        this.addSlot(new SlotCraftingOutputItemHandler(playerInventory.player, furnace.getInventory(), TileEntityBlastFurnace.SLOT_OUTPUT, 134, 36));

        // Добавляем слоты игрока
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    // Геттеры для GUI
    public int getProgress() {
        return data.get(0); // progress
    }

    public int getFuel() {
        return data.get(1); // fuel
    }

    public int getMaxFuel() {
        return TileEntityBlastFurnace.MAX_FUEL;
    }

    public int getProcessingSpeed() {
        return TileEntityBlastFurnace.PROCESSING_SPEED;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return furnace.canPlayerAccessInventory(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if(slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            if(slotIndex < 4) {
                // Из слотов печи в инвентарь игрока
                if(!this.moveItemStackTo(stackInSlot, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Из инвентаря игрока в слоты печи
                boolean moved = false;

                if(isFuel(stackInSlot)) {
                    moved = this.moveItemStackTo(stackInSlot, TileEntityBlastFurnace.SLOT_FUEL, TileEntityBlastFurnace.SLOT_FUEL + 1, false);
                }

                if(!moved) {
                    // Пробуем в верхний или нижний слот
                    moved = this.moveItemStackTo(stackInSlot, TileEntityBlastFurnace.SLOT_UPPER, TileEntityBlastFurnace.SLOT_LOWER + 1, false);
                }

                if(!moved) {
                    return ItemStack.EMPTY;
                }
            }

            if(stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    private boolean isFuel(ItemStack stack) {
        // Проверка, является ли предмет топливом
        return stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL ||
                stack.getItem() == Items.COAL_BLOCK || stack.getItem() == Items.LAVA_BUCKET;
    }
}