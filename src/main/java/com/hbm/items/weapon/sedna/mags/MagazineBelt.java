package com.hbm.items.weapon.sedna.mags;

import java.util.ArrayList;
import java.util.List;

import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemAmmoBag.InventoryAmmoBag;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.particle.SpentCasing;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MagazineBelt implements IMagazine<BulletConfig> {

    // Инициализируем список сразу, чтобы избежать NPE
    public List<BulletConfig> acceptedBullets = new ArrayList<>();

    public MagazineBelt addConfigs(BulletConfig... cfgs) {
        if (cfgs == null) return this;

        for(BulletConfig cfg : cfgs) {
            if (cfg != null) {
                acceptedBullets.add(cfg);
            }
        }
        return this;
    }

    @Override
    public BulletConfig getType(ItemStack stack, Container inventory) {
        BulletConfig config = getFirstConfig(stack, inventory);

        if (config == null) {
            // Возвращаем первый не-null конфиг из списка
            for (BulletConfig bulletConfig : acceptedBullets) {
                if (bulletConfig != null) {
                    if (this.getMagType(stack) != bulletConfig.id) {
                        this.setMagType(stack, bulletConfig.id);
                    }
                    return bulletConfig;
                }
            }
            return null; // или создай дефолтный BulletConfig
        }

        if (this.getMagType(stack) != config.id) {
            this.setMagType(stack, config.id);
        }

        return config;
    }

    @Override
    public void setType(ItemStack stack, BulletConfig type) {
        if (type != null) {
            this.setMagType(stack, type.id);
        }
    }

    @Override
    public void useUpAmmo(ItemStack stack, Container inventory, int amount) {
        if(inventory == null) return;
        if(!IMagazine.shouldUseUpTrenchie(inventory)) return;

        BulletConfig first = this.getFirstConfig(stack, inventory);
        if (first == null || first.ammo == null || first.ammo.isEmpty()) {
            return; // Нет аммо для использования
        }

        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if(!slot.isEmpty()) {
                if(ItemStack.isSameItemSameTags(slot, first.ammo)) {
                    int toRemove = Math.min(slot.getCount(), amount);
                    amount -= toRemove;
                    slot.shrink(toRemove);
                    if(slot.isEmpty()) {
                        inventory.setItem(i, ItemStack.EMPTY);
                    }
                    IMagazine.handleAmmoBag(inventory, first, toRemove);
                    if(amount <= 0) return;
                }

                boolean infBag = slot.getItem() == ModItems.AMMO_BAG_INFINITE.get();
                if(slot.getItem() == ModItems.AMMO_BAG.get() || infBag) {
                    InventoryAmmoBag bag = new InventoryAmmoBag(slot);
                    for(int j = 0; j < bag.getContainerSize(); j++) {
                        ItemStack bagslot = bag.getItem(j);

                        if(!bagslot.isEmpty()) {
                            if(ItemStack.isSameItemSameTags(bagslot, first.ammo)) {
                                int toRemove = Math.min(bagslot.getCount(), amount);
                                amount -= toRemove;
                                if(!infBag) {
                                    bagslot.shrink(toRemove);
                                    if(bagslot.isEmpty()) {
                                        bag.setItem(j, ItemStack.EMPTY);
                                    }
                                }
                                IMagazine.handleAmmoBag(inventory, first, toRemove);
                                if(amount <= 0) return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override public int getCapacity(ItemStack stack) { return 0; }
    @Override public void setAmount(ItemStack stack, int amount) { }
    @Override public boolean canReload(ItemStack stack, Container inventory) { return false; }
    @Override public void initNewType(ItemStack stack, Container inventory) { }
    @Override public void reloadAction(ItemStack stack, Container inventory) { }
    @Override public void setAmountBeforeReload(ItemStack stack, int amount) { }
    @Override public int getAmountBeforeReload(ItemStack stack) { return 0; }
    @Override public void setAmountAfterReload(ItemStack stack, int amount) { }
    @Override public int getAmountAfterReload(ItemStack stack) { return 0; }

    @Override
    public int getAmount(ItemStack stack, Container inventory) {
        if(inventory == null) return 1; // for EntityAIFireGun

        BulletConfig first = this.getFirstConfig(stack, inventory);
        if (first == null || first.ammo == null || first.ammo.isEmpty()) {
            return 0;
        }

        int count = 0;
        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if(!slot.isEmpty()) {
                if(ItemStack.isSameItemSameTags(slot, first.ammo)) count += slot.getCount();

                boolean infBag = slot.getItem() == ModItems.AMMO_BAG_INFINITE.get();
                if(slot.getItem() == ModItems.AMMO_BAG.get() || infBag) {
                    InventoryAmmoBag bag = new InventoryAmmoBag(slot);
                    for(int j = 0; j < bag.getContainerSize(); j++) {
                        ItemStack bagslot = bag.getItem(j);

                        if(!bagslot.isEmpty()) {
                            if(ItemStack.isSameItemSameTags(bagslot, first.ammo)) {
                                if(infBag) return 9_999;
                                count += bagslot.getCount();
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    @Override
    public ItemStack getIconForHUD(ItemStack stack, Player player) {
        BulletConfig first = this.getFirstConfig(stack, player.getInventory());
        if (first == null || first.ammo == null || first.ammo.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return first.ammo.copy();
    }

    @Override
    public String reportAmmoStateForHUD(ItemStack stack, Player player) {
        if (acceptedBullets.isEmpty()) {
            return "x0";
        }
        return "x" + getAmount(stack, player.getInventory());
    }

    @Override
    public SpentCasing getCasing(ItemStack stack, Container inventory) {
        BulletConfig config = getFirstConfig(stack, inventory);
        return config != null ? config.casing : null;
    }

    public BulletConfig getFirstConfig(ItemStack stack, Container inventory) {
        // Если список пуст, возвращаем null
        if (acceptedBullets.isEmpty()) {
            return null;
        }

        if(inventory == null) {
            // Возвращаем первый не-null конфиг
            for (BulletConfig config : acceptedBullets) {
                if (config != null) {
                    return config;
                }
            }
            return null;
        }

        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if(!slot.isEmpty()) {
                for(BulletConfig config : this.acceptedBullets) {
                    if (config == null || config.ammo == null) continue;
                    if(ItemStack.isSameItemSameTags(slot, config.ammo)) return config;
                }

                if(slot.getItem() == ModItems.AMMO_BAG.get() || slot.getItem() == ModItems.AMMO_BAG_INFINITE.get()) {
                    InventoryAmmoBag bag = new InventoryAmmoBag(slot);
                    for(int j = 0; j < bag.getContainerSize(); j++) {
                        ItemStack bagslot = bag.getItem(j);

                        if(!bagslot.isEmpty()) {
                            for(BulletConfig config : this.acceptedBullets) {
                                if (config == null || config.ammo == null) continue;
                                if(ItemStack.isSameItemSameTags(bagslot, config.ammo)) return config;
                            }
                        }
                    }
                }
            }
        }

        BulletConfig cached = BulletConfig.configs.get(this.getMagType(stack));
        if (cached != null && acceptedBullets.contains(cached)) {
            return cached;
        }

        // Возвращаем первый не-null конфиг
        for (BulletConfig config : acceptedBullets) {
            if (config != null) {
                return config;
            }
        }

        return null;
    }

    public static final String KEY_MAG_TYPE = "magtype";

    public static int getMagType(ItemStack stack) {

        return GunItem.getInt(stack, KEY_MAG_TYPE);
    }

    public static void setMagType(ItemStack stack, int value) {
        GunItem.setInt(stack, KEY_MAG_TYPE, value);
    }
}