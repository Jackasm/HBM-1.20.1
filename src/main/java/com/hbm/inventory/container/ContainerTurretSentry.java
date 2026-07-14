package com.hbm.inventory.container;

import com.hbm.items.ModItems;
import com.hbm.tileentity.turret.TileEntityTurretSentry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerTurretSentry extends AbstractContainerMenu {

    public final TileEntityTurretSentry turret;

    public ContainerTurretSentry(int id, Inventory inv, TileEntityTurretSentry turret) {
        super(ModContainers.TURRET_SENTRY.get(), id);
        this.turret = turret;

        turret.openInventory(inv.player);

        IItemHandler handler = turret.getInventory();

        // Слот для чипа (слот 0)
        this.addSlot(new SlotItemHandler(handler, 0, 98, 27));

        // 3x3 слоты для патронов (слоты 1-9)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(handler, 1 + i * 3 + j, 80 + j * 18, 63 + i * 18));
            }
        }

        // Слот для батареи (слот 10)
        this.addSlot(new SlotItemHandler(handler, 10, 152, 99));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + (18 * 3) + 2));
            }
        }

        // Хотбар
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142 + (18 * 3) + 2));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            // Если кликнули по слоту турели (0-10)
            if (index <= 10) {
                if (!this.moveItemStackTo(slotStack, 11, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Если кликнули по инвентарю игрока
            else {
                // Чип
                if (slotStack.getItem() == ModItems.TURRET_CHIP.get()) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Батарея
                else if (slotStack.getItem() instanceof com.hbm.api.energy.IBatteryItem) {
                    if (!this.moveItemStackTo(slotStack, 10, 11, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Патроны
                else {
                    if (!this.moveItemStackTo(slotStack, 1, 10, false)) {
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
        return turret.stillValid(player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        turret.closeInventory(player);
    }
}