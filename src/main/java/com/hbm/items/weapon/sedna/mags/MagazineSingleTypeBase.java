package com.hbm.items.weapon.sedna.mags;

import java.util.ArrayList;
import java.util.List;

import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemAmmoBag.InventoryAmmoBag;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.particle.SpentCasing;
import com.hbm.util.BobMathUtil;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/** Base class for typical magazines, i.e. ones that hold bullets, shells, grenades, etc, any ammo item. Stores a single type of BulletConfigs */
public abstract class MagazineSingleTypeBase implements IMagazine<BulletConfig> {

    public static final String KEY_MAG_COUNT = "magcount";
    public static final String KEY_MAG_TYPE = "magtype";
    public static final String KEY_MAG_PREV = "magprev";
    public static final String KEY_MAG_AFTER = "magafter";

    public List<BulletConfig> acceptedBullets = new ArrayList<>();

    /** A number so the gun tell multiple mags apart */
    public int index;
    /** How much ammo this mag can hold */
    public int capacity;

    public MagazineSingleTypeBase(int index, int capacity) {
        this.index = index;
        this.capacity = capacity;
    }

    public MagazineSingleTypeBase addConfigs(BulletConfig... cfgs) {
        for(BulletConfig cfg : cfgs) acceptedBullets.add(cfg);
        return this;
    }

    @Override
    public BulletConfig getType(ItemStack stack, Container inventory) {
        int type = getMagType(stack, index);
        if(type >= 0 && type < BulletConfig.configs.size()) {
            BulletConfig cfg = BulletConfig.configs.get(type);
            if(acceptedBullets.contains(cfg)) return cfg;
            return acceptedBullets.get(0);
        }
        return null;
    }

    @Override
    public void setType(ItemStack stack, BulletConfig type) {
        int i = BulletConfig.configs.indexOf(type);
        if(i >= 0) setMagType(stack, index, i);
    }

    @Override
    public ItemStack getIconForHUD(ItemStack stack, Player player) {
        if (player == null) {
            // Возвращаем пустой стек или стек по умолчанию
            return ItemStack.EMPTY;
        }
        BulletConfig config = this.getType(stack, player.getInventory());
        if(config != null && config.ammo != null) return config.ammo;
        return ItemStack.EMPTY;
    }

    @Override
    public String reportAmmoStateForHUD(ItemStack stack, Player player) {
        return getAmount(stack, player.getInventory()) + " / " + getCapacity(stack);
    }

    @Override
    public SpentCasing getCasing(ItemStack stack, Container inventory) {
        BulletConfig type = this.getType(stack, inventory);
        return type != null ? type.casing : null;
    }

    @Override
    public void useUpAmmo(ItemStack stack, Container inventory, int amount) {
        if(!IMagazine.shouldUseUpTrenchie(inventory) && getCapacity(stack) != 1) return;
        this.setAmount(stack, this.getAmount(stack, inventory) - amount);
        IMagazine.handleAmmoBag(inventory, this.getType(stack, inventory), amount);
    }

    /** Returns true if the player has the same ammo if partially loaded, or any valid ammo if not */
    @Override
    public boolean canReload(ItemStack stack, Container inventory) {
        if(this.getAmount(stack,  inventory) >= this.getCapacity(stack)) return false;
        if(inventory == null) return true;
        BulletConfig nextConfig = getFirstConfig(stack, inventory);
        return nextConfig != null;
    }

    public void standardReload(ItemStack stack, Container inventory, int loadLimit) {
        if(inventory == null) {
            BulletConfig config = this.getType(stack, null);
            if(config == null) {
                config = this.acceptedBullets.get(0);
                this.setType(stack, config);
            }
            this.setAmount(stack, this.capacity);
            return;
        }

        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if(loadLimit <= 0) return;

            if(!slot.isEmpty()) {
                //mag is empty, assume next best type
                if(this.getAmount(stack,  null) == 0) {
                    for(BulletConfig config : this.acceptedBullets) {
                        if(isAmmoMatch(slot, config.ammo)) {
                            this.setType(stack, config);
                            int wantsToLoad = (int) Math.ceil((double) this.getCapacity(stack) / (double) config.ammoReloadCount);
                            int toLoad = BobMathUtil.min(wantsToLoad, slot.getCount(), loadLimit);
                            this.setAmount(stack, Math.min(toLoad * config.ammoReloadCount, this.capacity));
                            slot.shrink(toLoad);
                            loadLimit -= toLoad;
                            break;
                        }
                    }
                    //mag has a type set, only load that
                } else {
                    BulletConfig config = this.getType(stack, null);
                    if(config == null) {
                        config = this.acceptedBullets.get(0);
                        this.setType(stack, config);
                    }

                    if(isAmmoMatch(slot, config.ammo)) {
                        int alreadyLoaded = this.getAmount(stack,  null);
                        int wantsToLoad = (int) Math.ceil((double) (this.getCapacity(stack) - alreadyLoaded) / (double) config.ammoReloadCount);
                        int toLoad = BobMathUtil.min(wantsToLoad, slot.getCount(), loadLimit);
                        this.setAmount(stack, Math.min((toLoad * config.ammoReloadCount) + alreadyLoaded, this.capacity));
                        slot.shrink(toLoad);
                        loadLimit -= toLoad;
                    }
                }

                boolean infBag = slot.getItem() == ModItems.AMMO_BAG_INFINITE.get();
                if(slot.getItem() == ModItems.AMMO_BAG.get() || infBag) {
                    InventoryAmmoBag bag = new InventoryAmmoBag(slot);

                    for(int j = 0; j < bag.getContainerSize(); j++) {
                        ItemStack bagslot = bag.getItem(j);

                        if(!bagslot.isEmpty()) {
                            //mag is empty, assume next best type
                            if(this.getAmount(stack,  null) == 0) {
                                for(BulletConfig config : this.acceptedBullets) {
                                    if(isAmmoMatch(bagslot, config.ammo)) {
                                        this.setType(stack, config);
                                        int wantsToLoad = (int) Math.ceil((double) this.getCapacity(stack) / (double) config.ammoReloadCount);
                                        int toLoad = BobMathUtil.min(wantsToLoad, infBag ? 9_999 : bagslot.getCount(), loadLimit);
                                        this.setAmount(stack, Math.min(toLoad * config.ammoReloadCount, this.capacity));
                                        if(!infBag) bagslot.shrink(toLoad);
                                        loadLimit -= toLoad;
                                        break;
                                    }
                                }
                                //mag has a type set, only load that
                            } else {
                                BulletConfig config = this.getType(stack, null);
                                if(config == null) {
                                    config = this.acceptedBullets.get(0);
                                    this.setType(stack, config);
                                }

                                if(isAmmoMatch(bagslot, config.ammo)) {
                                    int alreadyLoaded = this.getAmount(stack, bag);
                                    int wantsToLoad = (int) Math.ceil((double) (this.getCapacity(stack) - alreadyLoaded) / (double) config.ammoReloadCount);
                                    int toLoad = BobMathUtil.min(wantsToLoad, infBag ? 9_999 : bagslot.getCount(), loadLimit);
                                    this.setAmount(stack, Math.min((toLoad * config.ammoReloadCount) + alreadyLoaded, this.capacity));
                                    if(!infBag) bagslot.shrink(toLoad);
                                    loadLimit -= toLoad;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Вспомогательный метод для сравнения боеприпасов
    private boolean isAmmoMatch(ItemStack stackToCheck, ItemStack requiredAmmo) {
        if(stackToCheck.isEmpty() || requiredAmmo.isEmpty()) {
            return false;
        }

        // Простое сравнение предметов
        return stackToCheck.getItem() == requiredAmmo.getItem();

        // Или если нужно учитывать метаданные/NBT:
        // return ItemStack.isSameItemSameTags(stackToCheck, requiredAmmo);
    }

    /** Returns the config of the first potential loadable round, either what's already chambered or the first valid one if empty */
    public BulletConfig getFirstConfig(ItemStack stack, Container inventory) {
        if(inventory == null) return null;

        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if(!slot.isEmpty()) {
                if(this.getAmount(stack,  null) == 0) {
                    for(BulletConfig config : this.acceptedBullets) {
                        if(isAmmoMatch(slot, config.ammo)) return config;
                    }
                } else {
                    BulletConfig config = this.getType(stack, null);
                    if(config == null) {
                        config = this.acceptedBullets.get(0);
                        this.setType(stack, config);
                    }
                    if(isAmmoMatch(slot, config.ammo)) return config;
                }

                if(slot.getItem() == ModItems.AMMO_BAG.get() || slot.getItem() == ModItems.AMMO_BAG_INFINITE.get()) {
                    InventoryAmmoBag bag = new InventoryAmmoBag(slot);

                    for(int j = 0; j < bag.getContainerSize(); j++) {
                        ItemStack bagslot = bag.getItem(j);

                        if(!bagslot.isEmpty()) {
                            if(this.getAmount(stack,  null) == 0) {
                                for(BulletConfig config : this.acceptedBullets) {
                                    if(isAmmoMatch(bagslot, config.ammo)) return config;
                                }
                            } else {
                                BulletConfig config = this.getType(stack, null);
                                if(config == null) {
                                    config = this.acceptedBullets.get(0);
                                    this.setType(stack, config);
                                }
                                if(isAmmoMatch(bagslot, config.ammo)) return config;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void initNewType(ItemStack stack, Container inventory) {
        if(inventory == null) return;
        BulletConfig nextConfig = getFirstConfig(stack, inventory);
        if(nextConfig != null) {
            int i = BulletConfig.configs.indexOf(nextConfig);
            this.setMagType(stack, index, i);
        }
    }

    @Override
    public int getCapacity(ItemStack stack) {
        return capacity;
    }

    @Override
    public int getAmount(ItemStack stack, Container inventory) {
        return getMagCount(stack, index);
    }

    @Override
    public void setAmount(ItemStack stack, int amount) {
        setMagCount(stack, index, Math.max(amount, 0));
    }

    @Override
    public void setAmountBeforeReload(ItemStack stack, int amount) {
        GunItem.setInt(stack, KEY_MAG_PREV + index, amount);
    }

    @Override
    public int getAmountBeforeReload(ItemStack stack) {
        return GunItem.getInt(stack, KEY_MAG_PREV + index);
    }

    @Override
    public void setAmountAfterReload(ItemStack stack, int amount) {
        GunItem.setInt(stack, KEY_MAG_AFTER + index, amount);
    }

    @Override
    public int getAmountAfterReload(ItemStack stack) {
        return GunItem.getInt(stack, KEY_MAG_AFTER + index);
    }

    // MAG TYPE //
    public static int getMagType(ItemStack stack, int index) {
        return GunItem.getInt(stack, KEY_MAG_TYPE + index);
    }

    public static void setMagType(ItemStack stack, int index, int value) {
        GunItem.setInt(stack, KEY_MAG_TYPE + index, value);
    }

    // MAG COUNT //
    public static int getMagCount(ItemStack stack, int index) {
        return GunItem.getInt(stack, KEY_MAG_COUNT + index);
    }

    public static void setMagCount(ItemStack stack, int index, int value) {
        GunItem.setInt(stack, KEY_MAG_COUNT + index, value);
    }
}