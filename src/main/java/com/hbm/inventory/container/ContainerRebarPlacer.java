package com.hbm.inventory.container;

import com.hbm.inventory.SlotPattern;
import com.hbm.items.tool.ItemRebarPlacer.InventoryRebarPlacer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerRebarPlacer extends AbstractContainerMenu {

    private final InventoryRebarPlacer rebar;

    public ContainerRebarPlacer(int id, Inventory invPlayer, InventoryRebarPlacer rebar) {
        super(ModContainers.REBAR_PLACER.get(), id);
        this.rebar = rebar;

        this.addSlot(new SlotPattern(rebar, 0, 53, 36));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 100 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 158));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType action, @NotNull Player player) {
        if (action == ClickType.QUICK_MOVE && button == player.getInventory().selected) {
            return;
        }
        if (slotId == player.getInventory().selected + 47) {
            return;
        }

        if (slotId != 0) {
            super.clicked(slotId, button, action, player);
            return;
        }

        Slot slot = this.getSlot(slotId);
        ItemStack held = player.containerMenu.getCarried();

        // Просто устанавливаем предмет в слот
        slot.set(held);
        rebar.setChanged();
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
    }

    public InventoryRebarPlacer getInventory() {
        return rebar;
    }
}