package com.hbm.inventory.container;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.tileentity.machine.TileEntityMachineElectricFurnace;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerElectricFurnace extends AbstractContainerMenu {

    private final TileEntityMachineElectricFurnace furnace;
    private final IItemHandler playerInventory;

    public ContainerElectricFurnace(int windowId, Inventory inv, TileEntityMachineElectricFurnace tile) {
        super(ModContainers.ELECTRIC_FURNACE.get(), windowId);
        this.furnace = tile;

        IItemHandler handler = tile.getItemHandler();
        this.playerInventory = new InvWrapper(inv);

        // Батарейка
        this.addSlot(new SlotItemHandler(handler, 0, 56, 53));
        // Сырьё
        this.addSlot(new SlotItemHandler(handler, 1, 56, 17));
        // Результат
        this.addSlot(new SlotItemHandler(handler, 2, 116, 35) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
        // Улучшения
        this.addSlot(new SlotItemHandler(handler, 3, 147, 34));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Хотбар игрока
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();

            if (index < 4) {
                // Из слотов печи в инвентарь игрока
                if (!this.moveItemStackTo(stack, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Из инвентаря игрока в слоты печи
                if (stack.getItem() instanceof IBatteryItem) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() instanceof ItemMachineUpgrade) {
                    if (!this.moveItemStackTo(stack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // Пробуем положить в слот сырья
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return Objects.requireNonNull(furnace.getLevel()).getBlockEntity(furnace.getBlockPos()) == furnace &&
                player.distanceToSqr(furnace.getBlockPos().getX() + 0.5,
                        furnace.getBlockPos().getY() + 0.5,
                        furnace.getBlockPos().getZ() + 0.5) <= 64;
    }

    public TileEntityMachineElectricFurnace getFurnace() {
        return furnace;
    }
}