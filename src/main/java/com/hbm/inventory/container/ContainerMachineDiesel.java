package com.hbm.inventory.container;

import com.hbm.tileentity.machine.TileEntityMachineDiesel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineDiesel extends AbstractContainerMenu {

    private final TileEntityMachineDiesel diesel;
    private final IItemHandler handler;

    public ContainerMachineDiesel(int windowId, Inventory invPlayer, TileEntityMachineDiesel te) {
        super(ModContainers.MACHINE_DIESEL.get(), windowId);
        this.diesel = te;
        this.handler = te.getItemHandler();

        // Слоты: 0 - вход жидкости, 1 - пустая канистра, 2 - батарея, 3 - идентификатор жидкости, 4 - пустая канистра (выход)
        this.addSlot(new SlotItemHandler(handler, 0, 44, 17));
        this.addSlot(new SlotItemHandler(handler, 1, 44, 53) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(handler, 2, 116, 53));
        this.addSlot(new SlotItemHandler(handler, 3, 8, 17));
        this.addSlot(new SlotItemHandler(handler, 4, 8, 53) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        // Хотбар
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }

    public TileEntityMachineDiesel getDiesel() {
        return diesel;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem().copy();

        if (index <= 4) {
            if (!this.moveItemStackTo(stack, 5, this.slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            // Попытка положить в слот 0 (вход жидкости)
            if (!this.moveItemStackTo(stack, 0, 1, false)) {
                // Или в слот 2 (батарея)
                if (!this.moveItemStackTo(stack, 2, 3, false)) {
                    // Или в слот 4 (идентификатор)
                    if (!this.moveItemStackTo(stack, 4, 5, false))
                        return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return diesel.getBlockPos().distSqr(player.blockPosition()) <= 64.0;
    }
}