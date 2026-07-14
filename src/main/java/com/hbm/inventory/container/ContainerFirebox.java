package com.hbm.inventory.container;

import com.hbm.tileentity.machine.TileEntityFireboxBase;
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

public class ContainerFirebox extends AbstractContainerMenu {

    private final TileEntityFireboxBase firebox;
    private final ContainerData data;

    public ContainerFirebox(int windowId, Inventory playerInv, TileEntityFireboxBase te) {
        super(ModContainers.FIREBOX.get(), windowId); // Предполагается, что у вас есть ModContainers
        this.firebox = te;
        this.data = new SimpleContainerData(4); // Для хранения maxBurnTime, burnTime, burnHeat, heatEnergy

        IItemHandler handler = te.getItemHandler();

        // Слоты для топлива (0 и 1)
        this.addSlot(new SlotItemHandler(handler, 0, 44, 27));
        this.addSlot(new SlotItemHandler(handler, 1, 62, 27));

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 86 + i * 18));
            }
        }

        // Хотбар
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 144));
        }

        // Добавляем данные для синхронизации
        this.addDataSlots(data);

        // Инициализируем данные из TileEntity
        this.data.set(0, te.maxBurnTime);
        this.data.set(1, te.burnTime);
        this.data.set(2, te.burnHeat);
        this.data.set(3, te.heatEnergy);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            stack = originalStack.copy();

            // Если слот в топливных слотах (0-1)
            if (index <= 1) {
                // Пытаемся переместить в инвентарь игрока (слоты 2-37)
                if (!this.moveItemStackTo(originalStack, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(originalStack, stack);
            }
            // Если слот в инвентаре игрока (2-37)
            else if (!this.moveItemStackTo(originalStack, 0, 2, false)) {
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
        BlockPos pos = firebox.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.firebox.closeInventory();
    }

    // Геттеры для данных, которые будут использоваться в GUI
    public int getMaxBurnTime() {
        return data.get(0);
    }

    public int getBurnTime() {
        return data.get(1);
    }

    public int getBurnHeat() {
        return data.get(2);
    }

    public int getHeatEnergy() {
        return data.get(3);
    }

    public float getBurnProgress() {
        int maxBurn = getMaxBurnTime();
        if (maxBurn == 0) return 0;
        return (float) (maxBurn - getBurnTime()) / maxBurn;
    }

    public float getHeatProgress() {
        int maxHeat = firebox.getMaxHeat();
        if (maxHeat == 0) return 0;
        return (float) getHeatEnergy() / maxHeat;
    }

    public TileEntityFireboxBase getFirebox() {
        return firebox;
    }
}