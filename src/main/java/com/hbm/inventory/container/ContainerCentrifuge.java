package com.hbm.inventory.container;

import com.hbm.inventory.SlotCraftingOutput;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.tileentity.machine.TileEntityMachineCentrifuge;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerCentrifuge extends AbstractContainerMenu {

    public final TileEntityMachineCentrifuge centrifuge;
    private final IItemHandler handler;

    public ContainerCentrifuge(int windowId, Inventory invPlayer, TileEntityMachineCentrifuge te) {
        super(ModContainers.CENTRIFUGE.get(), windowId);
        this.centrifuge = te;
        this.handler = te.getItemHandler();

        // Слот входа (0)
        this.addSlot(new SlotItemHandler(handler, 0, 36, 50));
        // Слот батареи (1)
        this.addSlot(new SlotItemHandler(handler, 1, 9, 50));
        // Слоты выхода (2-5)
        this.addSlot(new SlotCraftingOutput(invPlayer.player, handler, 2, 63, 50));
        this.addSlot(new SlotCraftingOutput(invPlayer.player, handler, 3, 83, 50));
        this.addSlot(new SlotCraftingOutput(invPlayer.player, handler, 4, 103, 50));
        this.addSlot(new SlotCraftingOutput(invPlayer.player, handler, 5, 123, 50));
        // Слоты улучшений (6-7)
        this.addSlot(new SlotItemHandler(handler, 6, 149, 22));
        this.addSlot(new SlotItemHandler(handler, 7, 149, 40));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }
        // Хотбар
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 162));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem().copy();

        if (index <= 7) {
            if (!this.moveItemStackTo(stack, 8, this.slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            if (stack.getItem() instanceof com.hbm.api.energy.IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get()) {
                if (!this.moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
            } else if (stack.getItem() instanceof ItemMachineUpgrade) {
                if (!this.moveItemStackTo(stack, 6, 8, false)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return centrifuge.getBlockPos().distSqr(player.blockPosition()) <= 64.0;
    }
}