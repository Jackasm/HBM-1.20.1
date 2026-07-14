package com.hbm.inventory;

import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.tileentity.IUpgradeInfoProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Arrays;
import java.util.HashMap;

/*
 Steps for use:
 1. TE implements IUpgradeInfoProvider
 2. TE creates a new instance of UpgradeManagerNT
 3. Upgrades and their levels can then be pulled from there.
 */

/**
 * Upgrade system, now with caching!
 * @author BallOfEnergy1
 */
public class UpgradeManagerNT {

    public BlockEntity owner;
    public ItemStack[] cachedSlots;

    private UpgradeType mutexType;
    public HashMap<UpgradeType, Integer> upgrades = new HashMap<>();

    public UpgradeManagerNT(BlockEntity te) {
        this.owner = te;
    }

    public UpgradeManagerNT() { }

    public void checkSlots(ItemStack[] slots, int start, int end) {
        checkSlotsInternal(owner, slots, start, end);
    }

    /**
     * Проверяет слоты апгрейдов, используя ItemStackHandler
     * @param te владелец
     * @param handler ItemStackHandler с инвентарём
     * @param start начальный слот
     * @param end конечный слот
     */
    public void checkSlots(BlockEntity te, ItemStackHandler handler, int start, int end) {
        // Конвертируем ItemStackHandler в массив ItemStack[]
        ItemStack[] slots = new ItemStack[end - start + 1];
        for (int i = 0; i <= end - start; i++) {
            slots[i] = handler.getStackInSlot(start + i);
        }
        checkSlotsInternal(te, slots, 0, slots.length - 1);
    }

    private void checkSlotsInternal(BlockEntity te, ItemStack[] slots, int start, int end) {

        if (!(te instanceof IUpgradeInfoProvider upgradable) || slots == null)
            return;

        ItemStack[] upgradeSlots = Arrays.copyOfRange(slots, start, end + 1);

        if (Arrays.equals(upgradeSlots, cachedSlots))
            return;

        cachedSlots = upgradeSlots.clone();

        upgrades.clear();
        mutexType = null; // Сбрасываем mutexType при очистке

        for (int i = 0; i <= end - start; i++) {

            if (upgradeSlots[i] != null && upgradeSlots[i].getItem() instanceof ItemMachineUpgrade item) {

                if (upgradable.getValidUpgrades() == null)
                    return;

                if (upgradable.getValidUpgrades().containsKey(item.type)) { // Check if upgrade can even be accepted by the machine.
                    if (item.type.mutex) {
                        if (mutexType == null) {
                            upgrades.put(item.type, 1);
                            mutexType = item.type;
                        } else if (item.type.ordinal() > mutexType.ordinal()) {
                            upgrades.remove(mutexType);
                            upgrades.put(item.type, 1);
                            mutexType = item.type;
                        }
                    } else {

                        Integer levelBefore = upgrades.get(item.type);
                        int upgradeLevel = (levelBefore == null ? 0 : levelBefore);
                        upgradeLevel += item.tier;
                        // Add additional check to make sure it doesn't go over the max.
                        upgrades.put(item.type, Math.min(upgradeLevel, upgradable.getValidUpgrades().get(item.type)));
                    }
                }
            }
        }
    }

    public Integer getLevel(UpgradeType type) {
        return upgrades.getOrDefault(type, 0);
    }
}