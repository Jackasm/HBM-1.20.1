package com.hbm.items.weapon.sedna.factory;

import com.hbm.util.RefStrings;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

import static com.hbm.items.ModAmmoItems.*;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GunFactory {

    /* ENUMS */
    public enum EnumAmmo {
        STONE(AMMO_STONE),
        STONE_AP(AMMO_STONE_AP),
        STONE_IRON(AMMO_STONE_IRON),
        STONE_SHOT(AMMO_STONE_SHOT),

        // .357 Magnum
        M357_BP(AMMO_M357_BP),
        M357_SP(AMMO_M357_SP),
        M357_FMJ(AMMO_M357_FMJ),
        M357_JHP(AMMO_M357_JHP),
        M357_AP(AMMO_M357_AP),
        M357_EXPRESS(AMMO_M357_EXPRESS),

        // .44 Magnum
        M44_BP(AMMO_M44_BP),
        M44_SP(AMMO_M44_SP),
        M44_FMJ(AMMO_M44_FMJ),
        M44_JHP(AMMO_M44_JHP),
        M44_AP(AMMO_M44_AP),
        M44_EXPRESS(AMMO_M44_EXPRESS),

        // .22 LR
        P22_SP(AMMO_P22_SP),
        P22_FMJ(AMMO_P22_FMJ),
        P22_JHP(AMMO_P22_JHP),
        P22_AP(AMMO_P22_AP),

        // 9mm
        P9_SP(AMMO_P9_SP),
        P9_FMJ(AMMO_P9_FMJ),
        P9_JHP(AMMO_P9_JHP),
        P9_AP(AMMO_P9_AP),

        // .45 ACP
        P45_SP(AMMO_P45_SP),
        P45_FMJ(AMMO_P45_FMJ),
        P45_JHP(AMMO_P45_JHP),
        P45_AP(AMMO_P45_AP),
        P45_DU(AMMO_P45_DU),

        // 5.56mm
        R556_SP(AMMO_R556_SP),
        R556_FMJ(AMMO_R556_FMJ),
        R556_JHP(AMMO_R556_JHP),
        R556_AP(AMMO_R556_AP),

        // 7.62mm
        R762_SP(AMMO_R762_SP),
        R762_FMJ(AMMO_R762_FMJ),
        R762_JHP(AMMO_R762_JHP),
        R762_AP(AMMO_R762_AP),
        R762_DU(AMMO_R762_DU),
        R762_HE(AMMO_R762_HE),

        // .50 BMG
        BMG50_SP(AMMO_BMG50_SP),
        BMG50_FMJ(AMMO_BMG50_FMJ),
        BMG50_JHP(AMMO_BMG50_JHP),
        BMG50_AP(AMMO_BMG50_AP),
        BMG50_DU(AMMO_BMG50_DU),
        BMG50_SM(AMMO_BMG50_SM),
        BMG50_HE(AMMO_BMG50_HE),

        // 75mm
        B75(AMMO_B75),
        B75_INC(AMMO_B75_INC),
        B75_EXP(AMMO_B75_EXP),

        // 12 Gauge
        G12_BP(AMMO_G12_BP),
        G12_BP_MAGNUM(AMMO_G12_BP_MAGNUM),
        G12_BP_SLUG(AMMO_G12_BP_SLUG),
        G12(AMMO_G12),
        G12_SLUG(AMMO_G12_SLUG),
        G12_FLECHETTE(AMMO_G12_FLECHETTE),
        G12_MAGNUM(AMMO_G12_MAGNUM),
        G12_EXPLOSIVE(AMMO_G12_EXPLOSIVE),
        G12_PHOSPHORUS(AMMO_G12_PHOSPHORUS),

        // 10 Gauge
        G10(AMMO_G10),
        G10_SHRAPNEL(AMMO_G10_SHRAPNEL),
        G10_DU(AMMO_G10_DU),
        G10_SLUG(AMMO_G10_SLUG),
        G10_EXPLOSIVE(AMMO_G10_EXPLOSIVE),

        // 26mm Flare
        G26_FLARE(AMMO_G26_FLARE),
        G26_FLARE_SUPPLY(AMMO_G26_FLARE_SUPPLY),
        G26_FLARE_WEAPON(AMMO_G26_FLARE_WEAPON),

        // 40mm Grenade
        G40_HE(AMMO_G40_HE),
        G40_HEAT(AMMO_G40_HEAT),
        G40_DEMO(AMMO_G40_DEMO),
        G40_INC(AMMO_G40_INC),
        G40_PHOSPHORUS(AMMO_G40_PHOSPHORUS),

        // Rockets
        ROCKET_HE(AMMO_ROCKET_HE),
        ROCKET_HEAT(AMMO_ROCKET_HEAT),
        ROCKET_DEMO(AMMO_ROCKET_DEMO),
        ROCKET_INC(AMMO_ROCKET_INC),
        ROCKET_PHOSPHORUS(AMMO_ROCKET_PHOSPHORUS),

        // Flamethrower
        FLAME_DIESEL(AMMO_FLAME_DIESEL),
        FLAME_GAS(AMMO_FLAME_GAS),
        FLAME_NAPALM(AMMO_FLAME_NAPALM),
        FLAME_BALEFIRE(AMMO_FLAME_BALEFIRE),

        // Energy
        CAPACITOR(AMMO_CAPACITOR),
        CAPACITOR_OVERCHARGE(AMMO_CAPACITOR_OVERCHARGE),
        CAPACITOR_IR(AMMO_CAPACITOR_IR),

        // Tau
        TAU_URANIUM(AMMO_TAU_URANIUM),

        // Coilgun
        COIL_TUNGSTEN(AMMO_COIL_TUNGSTEN),
        COIL_FERROURANIUM(AMMO_COIL_FERROURANIUM),

        // Nuclear
        NUKE_STANDARD(AMMO_NUKE_STANDARD),
        NUKE_DEMO(AMMO_NUKE_DEMO),
        NUKE_HIGH(AMMO_NUKE_HIGH),
        NUKE_TOTS(AMMO_NUKE_TOTS),
        NUKE_HIVE(AMMO_NUKE_HIVE),
        NUKE_BALEFIRE(AMMO_NUKE_BALEFIRE),

        P35_800(AMMO_P35_800),
        P35_800_BL(AMMO_P35_800_BL),

        // Cable Tool
        CT_HOOK(AMMO_CT_HOOK),
        CT_MORTAR(AMMO_CT_MORTAR),
        CT_MORTAR_CHARGE(AMMO_CT_MORTAR_CHARGE),

        FEXT_SAND(AMMO_FEXT_SAND),
        FEXT_FOAM(AMMO_FEXT_FOAM),
        FEXT_CO2(AMMO_FEXT_CO2),

        //Secret Ammo
        FOLLY_SM(AMMO_FOLLY_SM),
        FOLLY_NUKE(AMMO_FOLLY_NUKE),
        M44_EQUESTRIAN(AMMO_M44_EQUESTRIAN),
        G12_EQUESTRIAN(AMMO_G12_EQUESTRIAN),
        BMG50_EQUESTRIAN(AMMO_BMG50_EQUESTRIAN),
        BMG50_BLACK(AMMO_BMG50_BLACK),

        // Shells
        SHELL(AMMO_SHELL),
        SHELL_EXPLOSIVE(AMMO_SHELL_EXPLOSIVE),
        SHELL_APFSDS_T(AMMO_SHELL_APFSDS_T),
        SHELL_APFSDS_DU(AMMO_SHELL_APFSDS_DU),
        SHELL_W9(AMMO_SHELL_W9),

        // Artillery
        ARTY_STANDARD(AMMO_ARTY_STANDARD),
        ARTY_HE(AMMO_ARTY_HE),
        ARTY_HE2(AMMO_ARTY_HE2),
        ARTY_WP(AMMO_ARTY_WP),
        ARTY_WP_ENHANCED(AMMO_ARTY_WP_ENHANCED),
        ARTY_NUKE(AMMO_ARTY_NUKE),
        ARTY_NUKE_ENHANCED(AMMO_ARTY_NUKE_ENHANCED),
        ARTY_SINKER(AMMO_ARTY_SINKER),
        ARTY_MIRV(AMMO_ARTY_MIRV),

        // DGK
        DGK(AMMO_DGK);



        // Поля для хранения ссылок на предметы
        private Supplier<Item> ammoSupplier = null;
        private RegistryObject<Item> ammoRegistryObject = null;
        private final RegistryObject<Item> ammoRegistry;

        EnumAmmo(RegistryObject<Item> registryObject) {
            this.ammoRegistry = registryObject;
        }
        /**
         * Устанавливает поставщик предмета для этого типа боеприпаса
         */
        public void setAmmoSupplier(Supplier<Item> supplier) {
            this.ammoSupplier = supplier;
        }

        /**
         * Устанавливает RegistryObject для этого типа боеприпаса
         */
        public void setAmmoRegistryObject(RegistryObject<Item> registryObject) {
            this.ammoRegistryObject = registryObject;
            this.ammoSupplier = registryObject;
        }

        /**
         * Получает предмет боеприпаса
         */
        @Nullable
        public Item getAmmoItem() {
            if (ammoRegistry == null) {
                return null; // или Items.AIR
            }
            // Только если RegistryObject доступен
            return ammoRegistry.isPresent() ? ammoRegistry.get() : null;
        }


        /**
         * Получает RegistryObject боеприпаса
         */
        public RegistryObject<Item> getAmmoRegistry() {
            return ammoRegistry;
        }

        /**
         * Получает имя для регистрации (в нижнем регистре с префиксом)
         */
        public String getRegistryName() {
            return "ammo_" + this.name().toLowerCase();
        }

        /**
         * Получает ключ локализации
         */
        public String getTranslationKey() {
            return "item.hbm.ammo." + this.name().toLowerCase();
        }

        /**
         * Проверяет, является ли боеприпас взрывным
         */
        public boolean isExplosive() {
            return switch (this) {
                case B75_EXP, G12_EXPLOSIVE, G10_EXPLOSIVE,
                     G40_HE, G40_HEAT, G40_DEMO, G40_INC, G40_PHOSPHORUS,
                     ROCKET_HE, ROCKET_HEAT, ROCKET_DEMO, ROCKET_INC, ROCKET_PHOSPHORUS,
                     NUKE_STANDARD, NUKE_DEMO, NUKE_HIGH, NUKE_TOTS, NUKE_HIVE, NUKE_BALEFIRE,
                     SHELL_EXPLOSIVE, ARTY_HE, ARTY_HE2, ARTY_NUKE, ARTY_NUKE_ENHANCED, DGK -> true;
                default -> false;
            };
        }

        /**
         * Проверяет, является ли боеприпас ядерным
         */
        public boolean isNuclear() {
            return switch (this) {
                case NUKE_STANDARD, NUKE_DEMO, NUKE_HIGH, NUKE_TOTS, NUKE_HIVE, NUKE_BALEFIRE, ARTY_NUKE, ARTY_NUKE_ENHANCED -> true;
                default -> false;
            };
        }

        /**
         * Проверяет, является ли боеприпас зажигательным
         */
        public boolean isIncendiary() {
            return switch (this) {
                case B75_INC, G12_PHOSPHORUS, G40_INC, G40_PHOSPHORUS,
                     ROCKET_INC, ROCKET_PHOSPHORUS, FLAME_DIESEL, FLAME_GAS,
                     FLAME_NAPALM, FLAME_BALEFIRE, ARTY_WP, ARTY_WP_ENHANCED -> true;
                default -> false;
            };
        }

        /**
         * Проверяет, является ли боеприпас бронебойным
         */
        public boolean isArmorPiercing() {
            return this.name().contains("_AP") ||
                    this.name().contains("_DU") ||
                    this == STONE_AP ||
                    this == STONE_IRON ||
                    this == COIL_TUNGSTEN ||
                    this == COIL_FERROURANIUM;
        }

        /**
         * Получает максимальный размер стека для этого боеприпаса
         */
        public int getMaxStackSize() {
            if (isNuclear()) {
                return 1; // Ядерные боеприпасы по 1 штуке
            } else if (this == B75 || this == B75_INC || this == B75_EXP) {
                return 16; // 75мм снаряды
            } else if (this.name().startsWith("ROCKET_")) {
                return 8; // Ракеты
            } else if (this.name().startsWith("G40_")) {
                return 16; // 40мм гранаты
            } else {
                return 64; // Стандартные боеприпасы
            }
        }

        /**
         * Получает категорию боеприпаса для сортировки
         */
        public AmmoCategory getCategory() {
            if (this.name().startsWith("STONE_")) return AmmoCategory.STONE;
            if (this.name().startsWith("M357_")) return AmmoCategory.M357;
            if (this.name().startsWith("M44_")) return AmmoCategory.M44;
            if (this.name().startsWith("P22_")) return AmmoCategory.P22;
            if (this.name().startsWith("P9_")) return AmmoCategory.P9;
            if (this.name().startsWith("P45_")) return AmmoCategory.P45;
            if (this.name().startsWith("R556_")) return AmmoCategory.R556;
            if (this.name().startsWith("R762_")) return AmmoCategory.R762;
            if (this.name().startsWith("BMG50_")) return AmmoCategory.BMG50;
            if (this.name().startsWith("B75")) return AmmoCategory.B75;
            if (this.name().startsWith("G12")) return AmmoCategory.G12;
            if (this.name().startsWith("G10")) return AmmoCategory.G10;
            if (this.name().startsWith("G26")) return AmmoCategory.G26;
            if (this.name().startsWith("G40")) return AmmoCategory.G40;
            if (this.name().startsWith("ROCKET_")) return AmmoCategory.ROCKET;
            if (this.name().startsWith("FLAME_")) return AmmoCategory.FLAME;
            if (this.name().startsWith("CAPACITOR")) return AmmoCategory.ENERGY;
            if (this.name().startsWith("TAU_")) return AmmoCategory.TAU;
            if (this.name().startsWith("COIL_")) return AmmoCategory.COILGUN;
            if (this.name().startsWith("NUKE_")) return AmmoCategory.NUCLEAR;
            if (this.name().startsWith("CT_")) return AmmoCategory.CABLE_TOOL;
            if (this.name().startsWith("SHELL")) return AmmoCategory.SHELL;
            if (this.name().startsWith("ARTY")) return AmmoCategory.ARTILLERY;
            if (this.name().startsWith("DGK")) return AmmoCategory.DGK;
            return AmmoCategory.OTHER;
        }

        /**
         * Получает все боеприпасы определенной категории
         */
        public static Set<EnumAmmo> getByCategory(AmmoCategory category) {
            Set<EnumAmmo> result = EnumSet.noneOf(EnumAmmo.class);
            for (EnumAmmo ammo : values()) {
                if (ammo.getCategory() == category) {
                    result.add(ammo);
                }
            }
            return result;
        }

        // Категории боеприпасов для организации
        public enum AmmoCategory {
            STONE("Stone Ammo"),
            M357(".357 Magnum"),
            M44(".44 Magnum"),
            P22(".22 LR"),
            P9("9mm"),
            P45(".45 ACP"),
            R556("5.56mm"),
            R762("7.62mm"),
            BMG50(".50 BMG"),
            B75("75mm Shells"),
            G12("12 Gauge"),
            G10("10 Gauge"),
            G26("26mm Flare"),
            G40("40mm Grenade"),
            ROCKET("Rockets"),
            FLAME("Flamethrower"),
            ENERGY("Energy"),
            TAU("Tau"),
            COILGUN("Coilgun"),
            NUCLEAR("Nuclear"),
            CABLE_TOOL("Cable Tool"),
            OTHER("Other"),
            SHELL("Shells"),
            ARTILLERY("Artillery Shells"),
            DGK("DGK"),;

            private final String displayName;

            AmmoCategory(String displayName) {
                this.displayName = displayName;
            }

            public String getDisplayName() {
                return displayName;
            }
        }
    }
}