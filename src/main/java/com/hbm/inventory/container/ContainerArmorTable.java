package com.hbm.inventory.container;

import com.hbm.handler.ArmorModHandler;
import com.hbm.items.armor.ItemArmorMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ResultContainer;
import org.jetbrains.annotations.NotNull;

public class ContainerArmorTable extends AbstractContainerMenu {

    public SimpleContainer upgrades = new SimpleContainer(ArmorModHandler.MOD_SLOTS);
    public ResultContainer armor = new ResultContainer();

    public ContainerArmorTable(int windowId, Inventory inventory) {
        super(ModContainers.ARMOR_TABLE.get(), windowId);
        Player player = inventory.player;

        // Upgrade slots
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.HELMET_ONLY, 26 + 22, 27));    // helmet only
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.PLATE_ONLY, 62 + 22, 27));     // chestplate only
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.LEGS_ONLY, 98 + 22, 27));      // leggings only
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.BOOTS_ONLY, 134 + 22, 45));    // boots only
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.SERVOS, 134 + 22, 81));        // servos/frame
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.CLADDING, 98 + 22, 99));       // radiation cladding
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.KEVLAR, 62 + 22, 99));         // kevlar/sapi
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.EXTRA, 26 + 22, 99));          // special parts
        this.addSlot(new UpgradeSlot(upgrades, ArmorModHandler.BATTERY, 8 + 22, 63));         // battery

        // Armor slot
        this.addSlot(new Slot(armor, 0, 44 + 22, 63) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof ArmorItem;
            }

            @Override
            public void set(@NotNull ItemStack stack) {
                // When inserting a new armor piece, unload all mods to display
                if (!stack.isEmpty()) {
                    ItemStack[] mods = ArmorModHandler.pryMods(stack);

                    for (int i = 0; i < ArmorModHandler.MOD_SLOTS; i++) {
                        if (mods != null && i < mods.length && mods[i] != null) {
                            upgrades.setItem(i, mods[i]);
                        } else {
                            upgrades.setItem(i, ItemStack.EMPTY);
                        }
                    }
                } else {
                    // Clear all mod slots when armor is removed
                    for (int i = 0; i < ArmorModHandler.MOD_SLOTS; i++) {
                        upgrades.setItem(i, ItemStack.EMPTY);
                    }
                }

                super.set(stack);
            }

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                super.onTake(player, stack);

                // If the armor piece is taken, absorb all armor pieces
                for (int i = 0; i < ArmorModHandler.MOD_SLOTS; i++) {
                    ItemStack mod = upgrades.getItem(i);

                    // Ideally, this should always return true so long as the mod slot is not null
                    if (ArmorModHandler.isApplicable(stack, mod)) {
                        upgrades.setItem(i, ItemStack.EMPTY);
                    }
                }
            }
        });

        // Player armor slots for easy accessibility
        int[] armorSlots = {39, 38, 37, 36}; // порядок: шлем, нагрудник, поножи, ботинки
        EquipmentSlot[] slotTypes = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

        for (int i = 0; i < 4; ++i) {
            final int slotIndex = armorSlots[i];
            final EquipmentSlot slotType = slotTypes[i];
            this.addSlot(new Slot(inventory, slotIndex, -17 + 22, 36 + i * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    if (stack.isEmpty()) return false;
                    return stack.getItem().canEquip(stack, slotType, player);
                }
            });
        }

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18 + 22, 84 + i * 18 + 56));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18 + 22, 142 + 56));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if (index <= ArmorModHandler.MOD_SLOTS) {
                // Try to move to player inventory
                if (!this.moveItemStackTo(slotStack, ArmorModHandler.MOD_SLOTS + 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }

                slot.onTake(player, slotStack);
            } else {
                if (slotStack.getItem() instanceof ArmorItem) {
                    if (!this.moveItemStackTo(slotStack, ArmorModHandler.MOD_SLOTS, ArmorModHandler.MOD_SLOTS + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.slots.get(ArmorModHandler.MOD_SLOTS).hasItem() &&
                        slotStack.getItem() instanceof ItemArmorMod mod) {

                    int targetSlot = mod.type;

                    if (this.slots.get(targetSlot).mayPlace(slotStack)) {
                        if (!this.moveItemStackTo(slotStack, targetSlot, targetSlot + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        if (!player.level().isClientSide) {
            for (int i = 0; i < upgrades.getContainerSize(); i++) {
                ItemStack itemstack = upgrades.getItem(i);

                if (!itemstack.isEmpty()) {
                    player.drop(itemstack, false);
                    ArmorModHandler.removeMod(armor.getItem(0), i);
                }
            }

            ItemStack itemstack = armor.getItem(0);
            if (!itemstack.isEmpty()) {
                player.drop(itemstack, false);
            }
        }
    }

    public class UpgradeSlot extends Slot {

        public UpgradeSlot(SimpleContainer inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            ItemStack armorStack = armor.getItem(0);
            return !armorStack.isEmpty() &&
                    ArmorModHandler.isApplicable(armorStack, stack) &&
                    stack.getItem() instanceof ItemArmorMod mod &&
                    mod.type == this.index;
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            super.set(stack);

            if (!stack.isEmpty()) {
                ItemStack armorStack = armor.getItem(0);
                if (ArmorModHandler.isApplicable(armorStack, stack)) {
                    ArmorModHandler.applyMod(armorStack, stack);
                }
            }
        }

        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            super.onTake(player, stack);

            ArmorModHandler.removeMod(armor.getItem(0), this.index);
        }
    }
}