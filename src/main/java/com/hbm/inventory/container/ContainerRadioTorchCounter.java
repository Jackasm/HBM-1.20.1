package com.hbm.inventory.container;

import com.hbm.inventory.SlotPattern;
import com.hbm.module.ModulePatternMatcher;
import com.hbm.tileentity.network.TileEntityRadioTorchCounter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Контейнер RTTY-счётчика: три слота-образца и инвентарь игрока.
 *
 * <p>Порт {@code ref:inventory/container/ContainerCounterTorch.java}. Логика
 * клика по слоту та же: правый клик по образцу переключает режим сравнения
 * ({@link ModulePatternMatcher#nextMode}), любой другой клик кладёт предмет в
 * слот и заново инициализирует режим.</p>
 */
public class ContainerRadioTorchCounter extends AbstractContainerMenu {

    private final TileEntityRadioTorchCounter counter;

    public ContainerRadioTorchCounter(int id, Inventory inv, TileEntityRadioTorchCounter counter) {
        super(ModContainers.RADIO_TORCH_COUNTER.get(), id);
        this.counter = counter;

        for (int i = 0; i < 3; i++) {
            this.addSlot(new SlotPattern(counter, i, 138, 18 + 44 * i));
        }

        // Инвентарь игрока: 12/156, хотбар 214 — как в эталонном контейнере.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 12 + j * 18, 156 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 12 + i * 18, 214));
        }
    }

    public TileEntityRadioTorchCounter getCounter() { return counter; }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return counter.stillValid(player);
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType action, @NotNull Player player) {
        // Слоты образцов не отдают предмет обратно: правый клик — смена режима,
        // остальное — положить held-стек в слот (эталонный slotClick).
        if (slotId < 0 || slotId > 2) {
            super.clicked(slotId, button, action, player);
            return;
        }

        if (action != ClickType.PICKUP && action != ClickType.PICKUP_ALL) {
            return;
        }

        if (button == 1 && !this.counter.getItem(slotId).isEmpty()) {
            this.counter.nextMode(slotId);
            return;
        }

        this.counter.setItem(slotId, this.getCarried().copy());
    }
}
