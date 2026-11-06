package com.hbm.inventory.container;

import com.hbm.tileentity.machine.TileEntityMachineFunnel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerFunnel extends AbstractContainerMenu {

    private final TileEntityMachineFunnel funnel;

    public ContainerFunnel(int windowId, Inventory inv, TileEntityMachineFunnel tile) {
        super(ModContainers.FUNNEL.get(), windowId);
        this.funnel = tile;

        IItemHandler handler = tile.getItemHandler();

        // Входные слоты (0-8) — верхний ряд
        for (int i = 0; i < 9; i++) {
            this.addSlot(new SlotItemHandler(handler, i, 8 + i * 18, 18));
        }

        // Выходные слоты (9-17) — нижний ряд, только для взятия
        for (int i = 0; i < 9; i++) {
            this.addSlot(new SlotItemHandler(handler, 9 + i, 8 + i * 18, 54) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false; // Нельзя положить в выходные слоты
                }
            });
        }

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 86 + i * 18));
            }
        }

        // Хотбар игрока
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 144));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();

            if (index < 18) {
                // Из слотов машины в инвентарь игрока
                if (!this.moveItemStackTo(stack, 18, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Из инвентаря игрока во входные слоты (0-8)
                if (!this.moveItemStackTo(stack, 0, 9, false)) {
                    return ItemStack.EMPTY;
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
        return Objects.requireNonNull(funnel.getLevel()).getBlockEntity(funnel.getBlockPos()) == funnel &&
                player.distanceToSqr(funnel.getBlockPos().getX() + 0.5,
                        funnel.getBlockPos().getY() + 0.5,
                        funnel.getBlockPos().getZ() + 0.5) <= 64;
    }

    public TileEntityMachineFunnel getFunnel() {
        return funnel;
    }
}