package com.hbm.inventory.container;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.items.ModItems;
import com.hbm.tileentity.machine.TileEntityMachineRadarNT;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineRadarNT extends AbstractContainerMenu {

    private final TileEntityMachineRadarNT radar;
    private final IItemHandler handler;

    public ContainerMachineRadarNT(int windowId, Inventory invPlayer, TileEntityMachineRadarNT tedf) {
        super(ModContainers.RADAR_NT.get(), windowId);
        this.radar = tedf;
        this.handler = tedf.getInventory();

        // 8 слотов для предметов (0-7)
        for (int i = 0; i < 8; i++) {
            this.addSlot(new SlotItemHandler(handler, i, 26 + i * 18, 17));
        }

        // Слот 8 - линкер
        this.addSlot(new SlotItemHandler(handler, 8, 26, 44));
        // Слот 9 - батарея
        this.addSlot(new SlotItemHandler(handler, 9, 152, 44));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 103 + i * 18));
            }
        }

        // Хотбар игрока
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 161));
        }
    }

    public TileEntityMachineRadarNT getRadar() {
        return radar;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack resultStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            resultStack = stack.copy();

            if (index <= 9) {
                if (!this.moveItemStackTo(stack, 10, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stack.getItem() instanceof IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get()) {
                    if (!this.moveItemStackTo(stack, 9, 10, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(stack, 0, 9, false)) {
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

        return resultStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return radar.isUsableByPlayer(player);
    }
}