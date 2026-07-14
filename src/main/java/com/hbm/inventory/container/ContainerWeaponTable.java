package com.hbm.inventory.container;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
import com.hbm.util.InventoryUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerWeaponTable extends AbstractContainerMenu {

    public ItemStackHandler mods = new ItemStackHandler(7);
    public ItemStackHandler gun = new ItemStackHandler(1);
    public int index = 0;

    public ContainerWeaponTable(int windowId, Inventory inventory) {
        super(ModContainers.WEAPON_TABLE.get(), windowId);

        // Mod slots (7 штук)
        for (int i = 0; i < 7; i++) {
            final int slotIndex = i;
            this.addSlot(new ModSlot(mods, i, 44 + 18 * i, 108) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return gun.getStackInSlot(0) != null &&
                            WeaponModManager.isApplicable(gun.getStackInSlot(0), stack, slotIndex, true);
                }
            });
        }

        // Gun slot
        this.addSlot(new SlotItemHandler(gun, 0, 8, 108) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return gun.getStackInSlot(0).isEmpty() && stack.getItem() instanceof GunItem;
            }

            @Override
            public void set(@NotNull ItemStack stack) {
                ContainerWeaponTable.this.index = 0;

                if (!stack.isEmpty()) {
                    ItemStack[] mods = WeaponModManager.getUpgradeItems(stack, index);
                    if (mods != null) {
                        for (int i = 0; i < Math.min(mods.length, 7); i++) {
                            ContainerWeaponTable.this.mods.setStackInSlot(i, mods[i]);
                        }
                    }
                }
                super.set(stack);
            }

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                super.onTake(player, stack);

                WeaponModManager.install(
                        stack, index,
                        mods.getStackInSlot(0),
                        mods.getStackInSlot(1),
                        mods.getStackInSlot(2),
                        mods.getStackInSlot(3),
                        mods.getStackInSlot(4),
                        mods.getStackInSlot(5),
                        mods.getStackInSlot(6));

                for (int i = 0; i < 7; i++) {
                    ItemStack mod = ContainerWeaponTable.this.mods.getStackInSlot(i);
                    if (WeaponModManager.isApplicable(stack, mod, index, false)) {
                        ContainerWeaponTable.this.mods.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
                ContainerWeaponTable.this.index = 0;
            }
        });

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 158 + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 216));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();

            if (index < 8) {
                if (!InventoryUtil.mergeItemStack(this.slots, stack, 8, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(player, stack);
            } else {
                if (stack.getItem() instanceof GunItem) {
                    if (!InventoryUtil.mergeItemStack(this.slots, stack, 7, 8, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!InventoryUtil.mergeItemStack(this.slots, stack, 0, 7, false)) {
                        return ItemStack.EMPTY;
                    }
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
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            for (int i = 0; i < mods.getSlots(); i++) {
                ItemStack stack = mods.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    player.drop(stack, false);
                }
            }
            ItemStack gunStack = gun.getStackInSlot(0);
            if (!gunStack.isEmpty()) {
                WeaponModManager.uninstall(gunStack, index);
                player.drop(gunStack, false);
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public ItemStack getGun() {
        return gun.getStackInSlot(0);
    }

    public int getConfigCount() {
        ItemStack gunStack = getGun();
        if (gunStack != null && gunStack.getItem() instanceof GunItem gunItem) {
            return gunItem.getConfigCount();
        }
        return 0;
    }

    public void nextIndex() {
        int configs = getConfigCount();
        if (configs > 1) {
            index++;
            index %= configs;
            // Обновить моды для нового индекса
            ItemStack gunStack = getGun();
            if (!gunStack.isEmpty()) {
                ItemStack[] mods = WeaponModManager.getUpgradeItems(gunStack, index);
                if (mods != null) {
                    for (int i = 0; i < Math.min(mods.length, 7); i++) {
                        this.mods.setStackInSlot(i, mods[i]);
                    }
                }
            }
        }
    }

    public class ModSlot extends SlotItemHandler {
        private final int modIndex;

        public ModSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
            this.modIndex = index;
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            super.set(stack);
            refreshInstalledMods();
            ItemStack gunStack = gun.getStackInSlot(0);
            if (!gunStack.isEmpty()) {
                WeaponModManager.onInstallStack(gunStack, stack, modIndex);
            }
        }

        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            super.onTake(player, stack);
            refreshInstalledMods();
            ItemStack gunStack = gun.getStackInSlot(0);
            if (!gunStack.isEmpty()) {
                WeaponModManager.onUninstallStack(gunStack, stack, modIndex);
            }
        }

        private void refreshInstalledMods() {
            ItemStack gunStack = gun.getStackInSlot(0);
            if (gunStack == null || gunStack.isEmpty()) return;
            WeaponModManager.uninstall(gunStack, index);
            WeaponModManager.install(
                    gunStack, index,
                    mods.getStackInSlot(0),
                    mods.getStackInSlot(1),
                    mods.getStackInSlot(2),
                    mods.getStackInSlot(3),
                    mods.getStackInSlot(4),
                    mods.getStackInSlot(5),
                    mods.getStackInSlot(6));
        }
    }
}