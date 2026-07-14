package com.hbm.items;

import com.hbm.datagen.models.ItemModelType;
import com.hbm.items.ammo.ItemAmmo;
import com.hbm.items.weapon.sedna.factory.GunFactory;
import com.hbm.util.HBMEnums;
import com.hbm.util.RefStrings;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.hbm.util.HBMEnums.CreativeTabRegistry.CONSUMABLE_TAB;

public class ModAmmoItems {

    public static final DeferredRegister<Item> AMMO =
            DeferredRegister.create(ForgeRegistries.ITEMS, RefStrings.MODID);

    public static final Map<RegistryObject<Item>, ItemModelType> AMMO_MODELS = new LinkedHashMap<>();
    public static final Map<RegistryObject<Item>, Object[]> AMMO_MODEL_DATA = new LinkedHashMap<>();

    public static RegistryObject<Item> register(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier) {
        return ItemRegistryHelper.registerAmmo(tab, name, supplier);
    }

    public static RegistryObject<Item> register(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier, 
                                                ItemModelType modelType, Object... modelData) {
        RegistryObject<Item> item = ItemRegistryHelper.registerAmmo(tab, name, supplier);
        AMMO_MODELS.put(item, modelType);
        if (modelData.length > 0) {
            AMMO_MODEL_DATA.put(item, modelData);
        }
        return item;
    }


    public static final RegistryObject<Item> AMMO_STONE = register(CONSUMABLE_TAB, "ammo/ammo_stone",
            () -> new ItemAmmo(GunFactory.EnumAmmo.STONE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_STONE_AP = register(CONSUMABLE_TAB, "ammo/ammo_stone_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.STONE_AP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_STONE_IRON = register(CONSUMABLE_TAB, "ammo/ammo_stone_iron",
            () -> new ItemAmmo(GunFactory.EnumAmmo.STONE_IRON, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_STONE_SHOT = register(CONSUMABLE_TAB, "ammo/ammo_stone_shot",
            () -> new ItemAmmo(GunFactory.EnumAmmo.STONE_SHOT, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12_BP = register(CONSUMABLE_TAB, "ammo/ammo_g12_bp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_BP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M357_BP = register(CONSUMABLE_TAB, "ammo/ammo_m357_bp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M357_BP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M357_SP = register(CONSUMABLE_TAB, "ammo/ammo_m357_sp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M357_SP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M357_FMJ = register(CONSUMABLE_TAB, "ammo/ammo_m357_fmj",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M357_FMJ, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M357_JHP = register(CONSUMABLE_TAB, "ammo/ammo_m357_jhp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M357_JHP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M357_AP = register(CONSUMABLE_TAB, "ammo/ammo_m357_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M357_AP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M357_EXPRESS = register(CONSUMABLE_TAB, "ammo/ammo_m357_express",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M357_EXPRESS, new Item.Properties()), ItemModelType.GENERATED);

    // .44 Magnum
    public static final RegistryObject<Item> AMMO_M44_BP = register(CONSUMABLE_TAB, "ammo/ammo_m44_bp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M44_BP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M44_SP = register(CONSUMABLE_TAB, "ammo/ammo_m44_sp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M44_SP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M44_FMJ = register(CONSUMABLE_TAB, "ammo/ammo_m44_fmj",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M44_FMJ, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M44_JHP = register(CONSUMABLE_TAB, "ammo/ammo_m44_jhp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M44_JHP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M44_AP = register(CONSUMABLE_TAB, "ammo/ammo_m44_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M44_AP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M44_EXPRESS = register(CONSUMABLE_TAB, "ammo/ammo_m44_express",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M44_EXPRESS, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_M44_EQUESTRIAN = register(CONSUMABLE_TAB, "ammo/ammo_m44_equestrian_secret",
            () -> new ItemAmmo(GunFactory.EnumAmmo.M44_EQUESTRIAN, new Item.Properties()), ItemModelType.GENERATED);

    // .22 LR
    public static final RegistryObject<Item> AMMO_P22_SP = register(CONSUMABLE_TAB, "ammo/ammo_p22_sp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P22_SP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P22_FMJ = register(CONSUMABLE_TAB, "ammo/ammo_p22_fmj",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P22_FMJ, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P22_JHP = register(CONSUMABLE_TAB, "ammo/ammo_p22_jhp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P22_JHP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P22_AP = register(CONSUMABLE_TAB, "ammo/ammo_p22_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P22_AP, new Item.Properties()), ItemModelType.GENERATED);

    // 9mm
    public static final RegistryObject<Item> AMMO_P9_SP = register(CONSUMABLE_TAB, "ammo/ammo_p9_sp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P9_SP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P9_FMJ = register(CONSUMABLE_TAB, "ammo/ammo_p9_fmj",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P9_FMJ, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P9_JHP = register(CONSUMABLE_TAB, "ammo/ammo_p9_jhp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P9_JHP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P9_AP = register(CONSUMABLE_TAB, "ammo/ammo_p9_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P9_AP, new Item.Properties()), ItemModelType.GENERATED);

    // .45 ACP
    public static final RegistryObject<Item> AMMO_P45_SP = register(CONSUMABLE_TAB, "ammo/ammo_p45_sp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P45_SP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P45_FMJ = register(CONSUMABLE_TAB, "ammo/ammo_p45_fmj",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P45_FMJ, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P45_JHP = register(CONSUMABLE_TAB, "ammo/ammo_p45_jhp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P45_JHP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P45_AP = register(CONSUMABLE_TAB, "ammo/ammo_p45_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P45_AP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P45_DU = register(CONSUMABLE_TAB, "ammo/ammo_p45_du",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P45_DU, new Item.Properties()), ItemModelType.GENERATED);

    // 5.56mm
    public static final RegistryObject<Item> AMMO_R556_SP = register(CONSUMABLE_TAB, "ammo/ammo_r556_sp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R556_SP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_R556_FMJ = register(CONSUMABLE_TAB, "ammo/ammo_r556_fmj",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R556_FMJ, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_R556_JHP = register(CONSUMABLE_TAB, "ammo/ammo_r556_jhp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R556_JHP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_R556_AP = register(CONSUMABLE_TAB, "ammo/ammo_r556_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R556_AP, new Item.Properties()), ItemModelType.GENERATED);

    // 7.62mm
    public static final RegistryObject<Item> AMMO_R762_SP = register(CONSUMABLE_TAB, "ammo/ammo_r762_sp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R762_SP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_R762_FMJ = register(CONSUMABLE_TAB, "ammo/ammo_r762_fmj",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R762_FMJ, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_R762_JHP = register(CONSUMABLE_TAB, "ammo/ammo_r762_jhp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R762_JHP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_R762_AP = register(CONSUMABLE_TAB, "ammo/ammo_r762_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R762_AP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_R762_DU = register(CONSUMABLE_TAB, "ammo/ammo_r762_du",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R762_DU, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_R762_HE = register(CONSUMABLE_TAB, "ammo/ammo_r762_he",
            () -> new ItemAmmo(GunFactory.EnumAmmo.R762_HE, new Item.Properties()), ItemModelType.GENERATED);

    // .50 BMG
    public static final RegistryObject<Item> AMMO_BMG50_SP = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_sp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_SP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_BMG50_FMJ = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_fmj",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_FMJ, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_BMG50_JHP = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_jhp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_JHP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_BMG50_AP = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_ap",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_AP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_BMG50_DU = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_du",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_DU, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_BMG50_SM = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_sm",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_SM, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_BMG50_HE = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_he",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_HE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_BMG50_EQUESTRIAN = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_equestrian_secret",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_EQUESTRIAN, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_BMG50_BLACK = register(CONSUMABLE_TAB, "ammo/ammo_bmg50_black_secret",
            () -> new ItemAmmo(GunFactory.EnumAmmo.BMG50_BLACK, new Item.Properties()), ItemModelType.GENERATED);

    // 75mm
    public static final RegistryObject<Item> AMMO_B75 = register(CONSUMABLE_TAB, "ammo/ammo_b75",
            () -> new ItemAmmo(GunFactory.EnumAmmo.B75, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_B75_INC = register(CONSUMABLE_TAB, "ammo/ammo_b75_inc",
            () -> new ItemAmmo(GunFactory.EnumAmmo.B75_INC, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_B75_EXP = register(CONSUMABLE_TAB, "ammo/ammo_b75_exp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.B75_EXP, new Item.Properties()), ItemModelType.GENERATED);

    // 12 Gauge

    public static final RegistryObject<Item> AMMO_G12_BP_MAGNUM = register(CONSUMABLE_TAB, "ammo/ammo_g12_bp_magnum",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_BP_MAGNUM, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12_BP_SLUG = register(CONSUMABLE_TAB, "ammo/ammo_g12_bp_slug",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_BP_SLUG, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12 = register(CONSUMABLE_TAB, "ammo/ammo_g12",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12_SLUG = register(CONSUMABLE_TAB, "ammo/ammo_g12_slug",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_SLUG, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12_FLECHETTE = register(CONSUMABLE_TAB, "ammo/ammo_g12_flechette",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_FLECHETTE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12_MAGNUM = register(CONSUMABLE_TAB, "ammo/ammo_g12_magnum",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_MAGNUM, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12_EXPLOSIVE = register(CONSUMABLE_TAB, "ammo/ammo_g12_explosive",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_EXPLOSIVE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12_PHOSPHORUS = register(CONSUMABLE_TAB, "ammo/ammo_g12_phosphorus",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_PHOSPHORUS, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G12_EQUESTRIAN = register(CONSUMABLE_TAB, "ammo/ammo_g12_equestrian_secret",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G12_EQUESTRIAN, new Item.Properties()), ItemModelType.GENERATED);

    // 10 Gauge
    public static final RegistryObject<Item> AMMO_G10 = register(CONSUMABLE_TAB, "ammo/ammo_g10",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G10, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G10_SHRAPNEL = register(CONSUMABLE_TAB, "ammo/ammo_g10_shrapnel",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G10_SHRAPNEL, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G10_DU = register(CONSUMABLE_TAB, "ammo/ammo_g10_du",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G10_DU, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G10_SLUG = register(CONSUMABLE_TAB, "ammo/ammo_g10_slug",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G10_SLUG, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G10_EXPLOSIVE = register(CONSUMABLE_TAB, "ammo/ammo_g10_explosive",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G10_EXPLOSIVE, new Item.Properties()), ItemModelType.GENERATED);

    // 26mm Flare
    public static final RegistryObject<Item> AMMO_G26_FLARE = register(CONSUMABLE_TAB, "ammo/ammo_g26_flare",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G26_FLARE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G26_FLARE_SUPPLY = register(CONSUMABLE_TAB, "ammo/ammo_g26_flare_supply",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G26_FLARE_SUPPLY, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G26_FLARE_WEAPON = register(CONSUMABLE_TAB, "ammo/ammo_g26_flare_weapon",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G26_FLARE_WEAPON, new Item.Properties()), ItemModelType.GENERATED);

    // 40mm Grenade
    public static final RegistryObject<Item> AMMO_G40_HE = register(CONSUMABLE_TAB, "ammo/ammo_g40_he",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G40_HE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G40_HEAT = register(CONSUMABLE_TAB, "ammo/ammo_g40_heat",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G40_HEAT, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G40_DEMO = register(CONSUMABLE_TAB, "ammo/ammo_g40_demo",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G40_DEMO, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G40_INC = register(CONSUMABLE_TAB, "ammo/ammo_g40_inc",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G40_INC, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_G40_PHOSPHORUS = register(CONSUMABLE_TAB, "ammo/ammo_g40_phosphorus",
            () -> new ItemAmmo(GunFactory.EnumAmmo.G40_PHOSPHORUS, new Item.Properties()), ItemModelType.GENERATED);

    // Rockets
    public static final RegistryObject<Item> AMMO_ROCKET_HE = register(CONSUMABLE_TAB, "ammo/ammo_rocket_he",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ROCKET_HE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ROCKET_HEAT = register(CONSUMABLE_TAB, "ammo/ammo_rocket_heat",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ROCKET_HEAT, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ROCKET_DEMO = register(CONSUMABLE_TAB, "ammo/ammo_rocket_demo",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ROCKET_DEMO, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ROCKET_INC = register(CONSUMABLE_TAB, "ammo/ammo_rocket_inc",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ROCKET_INC, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ROCKET_PHOSPHORUS = register(CONSUMABLE_TAB, "ammo/ammo_rocket_phosphorus",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ROCKET_PHOSPHORUS, new Item.Properties()), ItemModelType.GENERATED);

    // Flamethrower
    public static final RegistryObject<Item> AMMO_FLAME_DIESEL = register(CONSUMABLE_TAB, "ammo/ammo_flame_diesel",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FLAME_DIESEL, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_FLAME_GAS = register(CONSUMABLE_TAB, "ammo/ammo_flame_gas",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FLAME_GAS, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_FLAME_NAPALM = register(CONSUMABLE_TAB, "ammo/ammo_flame_napalm",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FLAME_NAPALM, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_FLAME_BALEFIRE = register(CONSUMABLE_TAB, "ammo/ammo_flame_balefire",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FLAME_BALEFIRE, new Item.Properties()), ItemModelType.GENERATED);

    // Energy
    public static final RegistryObject<Item> AMMO_CAPACITOR = register(CONSUMABLE_TAB, "ammo/ammo_capacitor",
            () -> new ItemAmmo(GunFactory.EnumAmmo.CAPACITOR, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_CAPACITOR_OVERCHARGE = register(CONSUMABLE_TAB, "ammo/ammo_capacitor_overcharge",
            () -> new ItemAmmo(GunFactory.EnumAmmo.CAPACITOR_OVERCHARGE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_CAPACITOR_IR = register(CONSUMABLE_TAB, "ammo/ammo_capacitor_ir",
            () -> new ItemAmmo(GunFactory.EnumAmmo.CAPACITOR_IR, new Item.Properties()), ItemModelType.GENERATED);

    // Tau
    public static final RegistryObject<Item> AMMO_TAU_URANIUM = register(CONSUMABLE_TAB, "ammo/ammo_tau_uranium",
            () -> new ItemAmmo(GunFactory.EnumAmmo.TAU_URANIUM, new Item.Properties()), ItemModelType.GENERATED);

    // Coilgun
    public static final RegistryObject<Item> AMMO_COIL_TUNGSTEN = register(CONSUMABLE_TAB, "ammo/ammo_coil_tungsten",
            () -> new ItemAmmo(GunFactory.EnumAmmo.COIL_TUNGSTEN, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_COIL_FERROURANIUM = register(CONSUMABLE_TAB, "ammo/ammo_coil_ferrouranium",
            () -> new ItemAmmo(GunFactory.EnumAmmo.COIL_FERROURANIUM, new Item.Properties()), ItemModelType.GENERATED);

    // Nuclear
    public static final RegistryObject<Item> AMMO_NUKE_STANDARD = register(CONSUMABLE_TAB, "ammo/ammo_nuke_standard",
            () -> new ItemAmmo(GunFactory.EnumAmmo.NUKE_STANDARD, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_NUKE_DEMO = register(CONSUMABLE_TAB, "ammo/ammo_nuke_demo",
            () -> new ItemAmmo(GunFactory.EnumAmmo.NUKE_DEMO, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_NUKE_HIGH = register(CONSUMABLE_TAB, "ammo/ammo_nuke_high",
            () -> new ItemAmmo(GunFactory.EnumAmmo.NUKE_HIGH, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_NUKE_TOTS = register(CONSUMABLE_TAB, "ammo/ammo_nuke_tots",
            () -> new ItemAmmo(GunFactory.EnumAmmo.NUKE_TOTS, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_NUKE_HIVE = register(CONSUMABLE_TAB, "ammo/ammo_nuke_hive",
            () -> new ItemAmmo(GunFactory.EnumAmmo.NUKE_HIVE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_NUKE_BALEFIRE = register(CONSUMABLE_TAB, "ammo/ammo_nuke_balefire",
            () -> new ItemAmmo(GunFactory.EnumAmmo.NUKE_BALEFIRE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> AMMO_P35_800 = register(CONSUMABLE_TAB, "ammo/ammo_p35_800_secret",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P35_800, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_P35_800_BL = register(CONSUMABLE_TAB, "ammo/ammo_p35_800_bl_secret",
            () -> new ItemAmmo(GunFactory.EnumAmmo.P35_800_BL, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> AMMO_FOLLY_SM = register(CONSUMABLE_TAB, "ammo/ammo_folly_sm_secret",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FOLLY_SM, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_FOLLY_NUKE = register(CONSUMABLE_TAB, "ammo/ammo_folly_nuke_secret",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FOLLY_NUKE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> AMMO_FEXT_SAND = register(CONSUMABLE_TAB, "ammo/ammo_fext_sand",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FEXT_SAND, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_FEXT_FOAM = register(CONSUMABLE_TAB, "ammo/ammo_fext_foam",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FEXT_FOAM, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_FEXT_CO2 = register(CONSUMABLE_TAB, "ammo/ammo_fext_co2",
            () -> new ItemAmmo(GunFactory.EnumAmmo.FEXT_CO2, new Item.Properties()), ItemModelType.GENERATED);

    // Cable Tool
    public static final RegistryObject<Item> AMMO_CT_HOOK = register(CONSUMABLE_TAB, "ammo/ammo_ct_hook",
            () -> new ItemAmmo(GunFactory.EnumAmmo.CT_HOOK, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_CT_MORTAR = register(CONSUMABLE_TAB, "ammo/ammo_ct_mortar",
            () -> new ItemAmmo(GunFactory.EnumAmmo.CT_MORTAR, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_CT_MORTAR_CHARGE = register(CONSUMABLE_TAB, "ammo/ammo_ct_mortar_charge",
            () -> new ItemAmmo(GunFactory.EnumAmmo.CT_MORTAR_CHARGE, new Item.Properties()), ItemModelType.GENERATED);


    // Shells
    public static final RegistryObject<Item> AMMO_SHELL = register(CONSUMABLE_TAB, "ammo/ammo_shell",
            () -> new ItemAmmo(GunFactory.EnumAmmo.SHELL, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_SHELL_EXPLOSIVE = register(CONSUMABLE_TAB, "ammo/ammo_shell_explosive",
            () -> new ItemAmmo(GunFactory.EnumAmmo.SHELL_EXPLOSIVE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_SHELL_APFSDS_T = register(CONSUMABLE_TAB, "ammo/ammo_shell_apfsds_t",
            () -> new ItemAmmo(GunFactory.EnumAmmo.SHELL_APFSDS_T, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_SHELL_APFSDS_DU = register(CONSUMABLE_TAB, "ammo/ammo_shell_apfsds_du",
            () -> new ItemAmmo(GunFactory.EnumAmmo.SHELL_APFSDS_DU, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_SHELL_W9 = register(CONSUMABLE_TAB, "ammo/ammo_shell_w9",
            () -> new ItemAmmo(GunFactory.EnumAmmo.SHELL_W9, new Item.Properties()), ItemModelType.GENERATED);

    // Artillery
    public static final RegistryObject<Item> AMMO_ARTY_STANDARD = register(CONSUMABLE_TAB, "ammo/ammo_arty_standard",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_STANDARD, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ARTY_HE = register(CONSUMABLE_TAB, "ammo/ammo_arty_he",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_HE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ARTY_HE2 = register(CONSUMABLE_TAB, "ammo/ammo_arty_he2",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_HE2, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ARTY_WP = register(CONSUMABLE_TAB, "ammo/ammo_arty_wp",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_WP, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ARTY_WP_ENHANCED = register(CONSUMABLE_TAB, "ammo/ammo_arty_wp_enhanced",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_WP_ENHANCED, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ARTY_NUKE = register(CONSUMABLE_TAB, "ammo/ammo_arty_nuke",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_NUKE, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ARTY_NUKE_ENHANCED = register(CONSUMABLE_TAB, "ammo/ammo_arty_nuke_enhanced",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_NUKE_ENHANCED, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ARTY_SINKER = register(CONSUMABLE_TAB, "ammo/ammo_arty_sinker",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_SINKER, new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> AMMO_ARTY_MIRV = register(CONSUMABLE_TAB, "ammo/ammo_arty_mirv",
            () -> new ItemAmmo(GunFactory.EnumAmmo.ARTY_MIRV, new Item.Properties()), ItemModelType.GENERATED);

    // DGK
    public static final RegistryObject<Item> AMMO_DGK = register(CONSUMABLE_TAB, "ammo/ammo_dgk",
            () -> new ItemAmmo(GunFactory.EnumAmmo.DGK, new Item.Properties()), ItemModelType.GENERATED);



    public static void register(IEventBus eventBus) {
        AMMO.register(eventBus);
    }
}
