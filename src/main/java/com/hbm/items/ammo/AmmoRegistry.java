package com.hbm.items.ammo;

import com.hbm.items.weapon.sedna.factory.GunFactory;
import com.hbm.main.MainRegistry;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class AmmoRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RefStrings.MODID);

    private static final Map<GunFactory.EnumAmmo, RegistryObject<Item>> AMMO_MAP = new EnumMap<>(GunFactory.EnumAmmo.class);

    // Инициализация
    public static void init() {
        for (GunFactory.EnumAmmo ammo : GunFactory.EnumAmmo.values()) {
            RegistryObject<Item> item = ITEMS.register(
                    "ammo_" + ammo.name().toLowerCase(),
                    () -> createAmmoItem(ammo)
            );
            AMMO_MAP.put(ammo, item);
        }
    }

    // Создание предмета боеприпаса
    private static Item createAmmoItem(GunFactory.EnumAmmo ammo) {
        // Здесь вы можете настроить свойства в зависимости от типа боеприпаса
        Item.Properties properties = new Item.Properties()
                .stacksTo(64); // Стандартный размер стека

        // Настраиваем особые свойства для определенных типов
        switch (ammo) {
            case NUKE_STANDARD:
            case NUKE_DEMO:
            case NUKE_HIGH:
            case NUKE_TOTS:
            case NUKE_HIVE:
            case NUKE_BALEFIRE:
                properties = properties.stacksTo(1); // Ядерные боеприпасы по 1 штуке
                break;
            case B75:
            case B75_INC:
            case B75_EXP:
                properties = properties.stacksTo(16); // 75мм снаряды
                break;
            case ROCKET_HE:
            case ROCKET_HEAT:
            case ROCKET_DEMO:
            case ROCKET_INC:
            case ROCKET_PHOSPHORUS:
                properties = properties.stacksTo(8); // Ракеты
                break;
            // Добавьте другие специальные настройки по необходимости
        }

        return new ItemAmmo(ammo, properties);
    }

    // Получение предмета по Enum
    public static Item getAmmoItem(GunFactory.EnumAmmo ammo) {
        RegistryObject<Item> item = AMMO_MAP.get(ammo);
        return item != null ? item.get() : null;
    }

    // Получение RegistryObject
    public static RegistryObject<Item> getAmmoRegistryObject(GunFactory.EnumAmmo ammo) {
        return AMMO_MAP.get(ammo);
    }

    // Регистрация свойств предметов (только на клиенте)
    @OnlyIn(Dist.CLIENT)
    public static void registerItemProperties() {
        for (GunFactory.EnumAmmo ammo : GunFactory.EnumAmmo.values()) {
            Item item = getAmmoItem(ammo);
            if (item != null) {
                // Регистрируем свойства для боеприпасов
                ItemProperties.register(item,
                        ResLocation.ResLocation(RefStrings.MODID, "ammo_type"),
                        (stack, world, entity, seed) -> ammo.ordinal());

                // Пример: свойство для определения, является ли боеприпас взрывным
                ItemProperties.register(item,
                        ResLocation.ResLocation(RefStrings.MODID, "is_explosive"),
                        (stack, world, entity, seed) -> isExplosiveAmmo(ammo) ? 1.0F : 0.0F);
            }
        }
    }

    // Вспомогательный метод для определения типа боеприпаса
    private static boolean isExplosiveAmmo(GunFactory.EnumAmmo ammo) {
        return switch (ammo) {
            case B75_EXP, G12_EXPLOSIVE, G10_EXPLOSIVE, G40_HE,
                 G40_HEAT, G40_DEMO, G40_INC, G40_PHOSPHORUS,
                 ROCKET_HE, ROCKET_HEAT, ROCKET_DEMO, ROCKET_INC, ROCKET_PHOSPHORUS,
                 NUKE_STANDARD, NUKE_DEMO, NUKE_HIGH, NUKE_TOTS, NUKE_HIVE, NUKE_BALEFIRE -> true;
            default -> false;
        };
    }
}