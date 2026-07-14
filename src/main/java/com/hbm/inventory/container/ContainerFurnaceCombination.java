package com.hbm.inventory.container;

import com.hbm.inventory.SlotCraftingOutput;
import com.hbm.inventory.SlotSmelting;
import com.hbm.tileentity.machine.TileEntityFurnaceCombination;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerFurnaceCombination extends AbstractContainerMenu {

    protected TileEntityFurnaceCombination furnace;
    private final ContainerData data;

    public ContainerFurnaceCombination(int windowId, Inventory invPlayer, Player player, TileEntityFurnaceCombination furnace) {
        super(ModContainers.FURNACE_COMBINATION.get(), windowId);
        this.furnace = furnace;
        this.data = new SimpleContainerData(4); // при необходимости можно расширить

        IItemHandler handler = furnace.getItemHandler();

        // Input slot
        this.addSlot(new SlotItemHandler(handler, 0, 26, 36));

        // Output slot (smelting)
        this.addSlot(new SlotSmelting(player, handler, 1, 89, 36));

        // Other slots
        this.addSlot(new SlotItemHandler(handler, 2, 136, 18));
        this.addSlot(new SlotCraftingOutput(player, handler, 3, 136, 54));

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 162));
        }

        // Initialize data
        // Здесь можно добавить синхронизацию данных, если нужно
        this.addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            stack = originalStack.copy();

            if (index <= 3) {
                // Из слотов печи в инвентарь игрока (4 - последний слот)
                if (!this.moveItemStackTo(originalStack, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, stack);
            } else if (!this.moveItemStackTo(originalStack, 0, 1, false)) {
                // Из инвентаря в слот ввода (0)
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (originalStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, originalStack);
        }

        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        BlockPos pos = furnace.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    public TileEntityFurnaceCombination getFurnace() {
        return furnace;
    }
}