package com.hbm_addons.main;

import com.hbm.util.RefStrings;
import com.hbm_addons.entity.mob.EntityEncounterVillager;
import com.hbm_addons.inventory.container.EncounterTradeMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Меню аддона. Регистр собственный, но неймспейс общий с модом. */
public final class AddonMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, RefStrings.MODID);

    public static final RegistryObject<MenuType<EncounterTradeMenu>> ENCOUNTER_TRADE = MENUS.register("encounter_trade",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                int entityId = data.readVarInt();
                var level = inv.player.level();
                var entity = level.getEntity(entityId);
                if (entity instanceof EntityEncounterVillager ev) {
                    return new EncounterTradeMenu(windowId, inv, ev);
                }
                return null;
            }));

    private AddonMenus() {
    }
}
