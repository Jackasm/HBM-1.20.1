package com.hbm.inventory.container;

import com.hbm.inventory.SlotNonRetarded;
import com.hbm.items.tool.ItemAmmoBag.InventoryAmmoBag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class ContainerAmmoBag extends AbstractContainerMenu {

    private final InventoryAmmoBag bag;
    private static final int BAG_SIZE = 8;

    public ContainerAmmoBag(int windowId, Inventory playerInv, InventoryAmmoBag bag) {
        this(windowId, playerInv, bag, ContainerLevelAccess.NULL);
    }

    public ContainerAmmoBag(int windowId, Inventory playerInv, InventoryAmmoBag bag, ContainerLevelAccess access) {
        super(null, windowId); // Замените null на ваш MenuType
        this.bag = bag;

        // Открываем инвентарь
        bag.startOpen(playerInv.player);

        // Слоты сумки (2x4 = 8 слотов)
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 4; j++) {
                this.addSlot(new SlotNonRetarded(bag, j + i * 4, 53 + j * 18, 18 + i * 18));
            }
        }

        // Слоты инвентаря игрока
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 82 + i * 18));
            }
        }

        // Слоты горячей панели
        for(int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 140));
        }
    }

    // Старый конструктор для обратной совместимости
    public ContainerAmmoBag(Inventory playerInv, InventoryAmmoBag bag) {
        this(0, playerInv, bag);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < BAG_SIZE) {
                // Перемещение из сумки в инвентарь игрока
                if (!this.moveItemStackTo(itemstack1, BAG_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Перемещение из инвентаря игрока в сумку
                if (!this.moveItemStackTo(itemstack1, 0, BAG_SIZE, false)) {
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

    @Override
    public void clicked(int index, int button, @NotNull net.minecraft.world.inventory.ClickType clickType, @NotNull Player player) {
        // Предотвращает перемещение открытой сумки
        if (clickType == net.minecraft.world.inventory.ClickType.SWAP && button == player.getInventory().selected) {
            return;
        }
        if (index == player.getInventory().selected + 27 + BAG_SIZE) {
            return;
        }
        super.clicked(index, button, clickType, player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return bag.stillValid(player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        bag.stopOpen(player);
    }
}