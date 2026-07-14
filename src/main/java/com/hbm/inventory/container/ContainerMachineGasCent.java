package com.hbm.inventory.container;

import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.tileentity.machine.TileEntityMachineGasCent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineGasCent extends AbstractContainerMenu {

    public final TileEntityMachineGasCent centrifuge;
    private final IItemHandler handler;

    public ContainerMachineGasCent(int windowId, Inventory invPlayer, TileEntityMachineGasCent te) {
        super(ModContainers.GAS_CENTRIFUGE.get(), windowId);
        this.centrifuge = te;
        this.handler = te.getItemHandler();

        // 4 выходных слота (0-3) - только для извлечения
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int slotIdx = j + i * 2;
                this.addSlot(new SlotItemHandler(handler, slotIdx, 71 + j * 18, 53 + i * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) { return false; }
                });
            }
        }

        // Слот батареи (4)
        this.addSlot(new SlotItemHandler(handler, 4, 182, 71));
        // Слот идентификатора жидкости (5)
        this.addSlot(new SlotItemHandler(handler, 5, 91, 15));
        // Слот улучшения скорости (6)
        this.addSlot(new SlotItemHandler(handler, 6, 69, 15));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 122 + i * 18));
            }
        }
        // Хотбар
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 180));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem().copy();
        if (index < 7) {
            // из слотов центрифуги в инвентарь игрока
            if (!this.moveItemStackTo(stack, 7, this.slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            // из инвентаря в слоты центрифуги
            if (stack.getItem() instanceof net.minecraft.world.item.Item && isBattery(stack)) {
                if (!this.moveItemStackTo(stack, 4, 5, false)) return ItemStack.EMPTY;
            } else if (stack.getItem() instanceof IItemFluidIdentifier) {
                if (!this.moveItemStackTo(stack, 5, 6, false)) return ItemStack.EMPTY;
            } else if (stack.getItem() instanceof ItemMachineUpgrade) {
                if (!this.moveItemStackTo(stack, 6, 7, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return stack;
    }

    private boolean isBattery(ItemStack stack) {
        return stack.getItem() instanceof com.hbm.api.energy.IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return centrifuge.getBlockPos().distSqr(player.blockPosition()) <= 64.0;
    }
}