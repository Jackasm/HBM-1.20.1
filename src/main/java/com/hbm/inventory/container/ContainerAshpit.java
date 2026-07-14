package com.hbm.inventory.container;

import com.hbm.tileentity.machine.TileEntityAshpit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerAshpit extends AbstractContainerMenu {

    private final TileEntityAshpit ashpit;
    private final ContainerData data;

    public ContainerAshpit(int windowId, Inventory invPlayer, TileEntityAshpit ashpit) {
        super(ModContainers.ASH_PIT.get(), windowId);
        this.ashpit = ashpit;
        this.data = new SimpleContainerData(0);

        // Слоты пепельницы (только на выдачу)
        for (int i = 0; i < 5; i++) {
            this.addSlot(new SlotItemHandler(ashpit.getInventory(), i, 44 + i * 18, 27) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }
            });
        }

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 86 + i * 18));
            }
        }

        // Хотбар игрока
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 144));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            stack = originalStack.copy();

            if (index < 5) {
                // Из пепельницы в инвентарь
                if (!this.moveItemStackTo(originalStack, 5, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // В пепельницу класть нельзя (только на выдачу)
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        BlockPos pos = ashpit.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.ashpit.onClose();
    }
}