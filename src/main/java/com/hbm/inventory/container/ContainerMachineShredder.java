package com.hbm.inventory.container;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemBlades;
import com.hbm.tileentity.machine.TileEntityMachineShredder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineShredder extends AbstractContainerMenu {

    private final TileEntityMachineShredder shredder;

    private final Player player;

    public ContainerMachineShredder(int id, Inventory inv, TileEntityMachineShredder shredder) {
        super(ModContainers.MACHINE_SHREDDER.get(), id);

        this.shredder = shredder;
        this.player = inv.player;

        IItemHandler handler = shredder.getInventory();
        // Входные слоты (0-8)
        int[] inputX = {44, 62, 80, 44, 62, 80, 44, 62, 80};
        int[] inputY = {18, 18, 18, 36, 36, 36, 54, 54, 54};
        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(handler, i, inputX[i], inputY[i]));
        }

        // Выходные слоты (9-26)
        int[] outputX = {116, 134, 152, 116, 134, 152, 116, 134, 152, 116, 134, 152, 116, 134, 152, 116, 134, 152};
        int[] outputY = {18, 18, 18, 36, 36, 36, 54, 54, 54, 72, 72, 72, 90, 90, 90, 108, 108, 108};
        for (int i = 0; i < 18; i++) {
            addSlot(new SlotItemHandler(handler, 9 + i, outputX[i], outputY[i]) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }
            });
        }

        // Слоты для лезвий
        addSlot(new SlotItemHandler(handler, 27, 44, 108));
        addSlot(new SlotItemHandler(handler, 28, 80, 108));

        // Слот для батареи
        addSlot(new SlotItemHandler(handler, 29, 8, 108) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get();
            }
        });

        // Инвентарь игрока
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 67));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inv, i, 8 + i * 18, 142 + 67));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < 30) {
                if (!moveItemStackTo(stack, 30, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stack.getItem() instanceof IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get()) {
                    if (!moveItemStackTo(stack, 29, 30, false)) return ItemStack.EMPTY;
                } else if (stack.getItem() instanceof ItemBlades) {
                    if (!moveItemStackTo(stack, 27, 29, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(stack, 0, 9, false)) return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public TileEntityMachineShredder getShredder() {
        return shredder;
    }
}