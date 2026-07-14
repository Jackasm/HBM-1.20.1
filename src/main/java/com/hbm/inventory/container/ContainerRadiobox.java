package com.hbm.inventory.container;

import com.hbm.tileentity.machine.TileEntityRadiobox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerRadiobox extends AbstractContainerMenu {

    private final TileEntityRadiobox radiobox;

    public ContainerRadiobox(int id, Inventory inv, TileEntityRadiobox radiobox) {
        super(ModContainers.RADIOBOX.get(), id);
        this.radiobox = radiobox;

        // Инвентарь игрока (3 ряда x 9 слотов)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                int x = 8 + col * 18;
                int y = 84 + row * 18;
                this.addSlot(new Slot(inv, index, x, y));
            }
        }

        // Хотбар (9 слотов)
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            int y = 142;
            this.addSlot(new Slot(inv, col, x, y));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return radiobox.getBlockPos().distSqr(player.blockPosition()) <= 64.0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }
}