package com.hbm.inventory.container;

import com.hbm.inventory.SlotCraftingOutputItemHandler;
import com.hbm.items.machine.ItemStamp;
import com.hbm.tileentity.machine.TileEntityMachinePress;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachinePress extends AbstractContainerMenu {
    private final TileEntityMachinePress press;
    private final ContainerData data;


    public ContainerMachinePress(int windowId, Inventory invPlayer, TileEntityMachinePress press) {
        super(ModContainers.MACHINE_PRESS.get(), windowId);
        this.press = press;
        this.data = press.getContainerData();
        addDataSlots(data);
        initSlots(invPlayer);
    }

    private void initSlots(Inventory invPlayer) {
        IItemHandler handler = press.getInventory(); // Убрали проверку на null

        this.addSlot(new SlotItemHandler(handler, 0, 26, 53));
        this.addSlot(new SlotStamp(handler, 1, 80, 17));
        this.addSlot(new SlotItemHandler(handler, 2, 80, 53));
        this.addSlot(new SlotCraftingOutputItemHandler(invPlayer.player, handler, 3, 140, 35));

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }


    public int getMachineSpeed() {
        return data.get(0);
    }

    public int getMachineBurnTime() {
        return data.get(1);
    }

    public int getMachinePress() {
        return data.get(2);
    }


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if(slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            if(slotIndex < 4) {
                if(!this.moveItemStackTo(stackInSlot, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                boolean moved;

                if(ForgeHooks.getBurnTime(stackInSlot, RecipeType.SMELTING) > 0) {
                    moved = this.moveItemStackTo(stackInSlot, 0, 1, false);
                } else if(stackInSlot.getItem() instanceof ItemStamp) {
                    moved = this.moveItemStackTo(stackInSlot, 1, 2, false);
                } else {
                    moved = this.moveItemStackTo(stackInSlot, 2, 3, false);
                }

                if(!moved) {
                    return ItemStack.EMPTY;
                }
            }

            if(stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return press.canPlayerAccessInventory(player); // Убрали проверку на null
    }

    private static class SlotStamp extends SlotItemHandler {
        public SlotStamp(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ItemStamp;
        }
    }
}