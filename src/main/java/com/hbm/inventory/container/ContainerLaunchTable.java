package com.hbm.inventory.container;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.api.item.IDesignatorItem;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.items.ModItems;
import com.hbm.tileentity.bomb.TileEntityLaunchTable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerLaunchTable extends AbstractContainerMenu {

    private final TileEntityLaunchTable launcher;
    private final IItemHandler handler;

    public ContainerLaunchTable(int windowId, Inventory invPlayer, TileEntityLaunchTable launcher) {
        super(ModContainers.LAUNCH_TABLE.get(), windowId);
        this.launcher = launcher;
        this.handler = launcher.getInventory();

        // Слоты: 0 - ракета, 1 - дизайнатор, 2 - жидкое топливо в, 3 - окислитель в,
        // 4 - твёрдое топливо, 5 - батарея, 6 - жидкое топливо вых, 7 - окислитель вых
        this.addSlot(new SlotItemHandler(handler, 0, 26, 36));
        this.addSlot(new SlotItemHandler(handler, 1, 26, 72));
        this.addSlot(new SlotItemHandler(handler, 2, 116, 72));
        this.addSlot(new SlotItemHandler(handler, 3, 134, 72));
        this.addSlot(new SlotItemHandler(handler, 4, 152, 90));
        this.addSlot(new SlotItemHandler(handler, 5, 116, 108));
        this.addSlot(new SlotItemHandler(handler, 6, 116, 90));
        this.addSlot(new SlotItemHandler(handler, 7, 134, 90));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 56));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142 + 56));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 8) {
                if (!this.moveItemStackTo(stack, 8, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stack.getItem() instanceof IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get()) {
                    if (!this.moveItemStackTo(stack, 5, 6, false)) return ItemStack.EMPTY;
                } else if (launcher.isMissileValid() && stack.getItem() == ModItems.MISSILE_CUSTOM.get()) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
                } else if (stack.getItem() instanceof IDesignatorItem) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
                } else if (stack.getItem() == ModItems.ROCKET_FUEL.get()) {
                    if (!this.moveItemStackTo(stack, 4, 5, false)) return ItemStack.EMPTY;
                } else if (FluidContainerRegistry.getFluidContent(stack, launcher.tanks[0].getTankType()) > 0) {
                    if (!this.moveItemStackTo(stack, 2, 3, false) && !this.moveItemStackTo(stack, 6, 7, false))
                        return ItemStack.EMPTY;
                } else if (FluidContainerRegistry.getFluidContent(stack, launcher.tanks[1].getTankType()) > 0) {
                    if (!this.moveItemStackTo(stack, 3, 4, false) && !this.moveItemStackTo(stack, 7, 8, false))
                        return ItemStack.EMPTY;
                } else {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    public TileEntityLaunchTable getLauncher() {
        return launcher;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return launcher.isUsableByPlayer(player);
    }
}