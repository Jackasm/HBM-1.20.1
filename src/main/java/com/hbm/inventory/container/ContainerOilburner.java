package com.hbm.inventory.container;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.trait.FT_Combustible;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.tileentity.machine.TileEntityHeaterOilburner;
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

public class ContainerOilburner extends AbstractContainerMenu {

    private final TileEntityHeaterOilburner heater;
    private final ContainerData data;

    public ContainerOilburner(int windowId, Inventory invPlayer, TileEntityHeaterOilburner te) {
        super(ModContainers.OILBURNER.get(), windowId);
        this.heater = te;
        this.data = new SimpleContainerData(4); // для данных, если нужны

        IItemHandler handler = te.getItemHandler();

        // Вход (слот 0)
        this.addSlot(new SlotItemHandler(handler, 0, 26, 17));
        // Выход (слот 1)
        this.addSlot(new SlotItemHandler(handler, 1, 26, 53) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
        // Идентификатор жидкости (слот 2)
        this.addSlot(new SlotItemHandler(handler, 2, 44, 71) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (!(stack.getItem() instanceof IItemFluidIdentifier)) return false;

                FluidTypeHBM fluidType = ((IItemFluidIdentifier) stack.getItem()).getType(heater.getLevel(),
                        heater.getBlockPos(), stack);

                return fluidType != null && fluidType.hasTrait(FT_Combustible.class);
            }
        });

        int offset = 37;

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + offset));
            }
        }

        // Хотбар
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142 + offset));
        }

        this.addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            // Слоты 0-2 (вход, выход, идентификатор)
            if (index <= 2) {
                // Перемещаем из слотов топки в инвентарь игрока (слоты 3-38)
                if (!this.moveItemStackTo(slotStack, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Из инвентаря игрока (слоты >=3) в слоты топки
            else {
                if (slotStack.getItem() instanceof IItemFluidIdentifier) {
                    // Идентификаторы сначала в слот 2
                    if (!this.moveItemStackTo(slotStack, 2, 3, false)) {
                        // Если не получилось, пробуем в слот 0
                        if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    // Обычные предметы только в слот 0
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        BlockPos pos = heater.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    public TileEntityHeaterOilburner getHeater() {
        return heater;
    }
}