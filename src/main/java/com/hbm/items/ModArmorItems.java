package com.hbm.items;

import com.hbm.datagen.models.ItemModelType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.armor.*;
import com.hbm.potion.HbmPotion;
import com.hbm.sound.ModSounds;
import com.hbm.util.HBMEnums;
import com.hbm.util.RefStrings;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.hbm.items.armor.MaskItem.*;
import static com.hbm.util.HBMEnums.CreativeTabRegistry.PARTS_TAB;
import static com.hbm.util.HBMEnums.CreativeTabRegistry.WEAPON_TAB;


public class ModArmorItems {
    public static final DeferredRegister<Item> ARMOR =
            DeferredRegister.create(ForgeRegistries.ITEMS, RefStrings.MODID);

    public static final Map<RegistryObject<Item>, ItemModelType> ARMOR_MODELS = new LinkedHashMap<>();
    public static final Map<RegistryObject<Item>, Object[]> ARMOR_MODEL_DATA = new LinkedHashMap<>();


    public static RegistryObject<Item> register(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier, ItemModelType modelType, Object... modelData) {
        RegistryObject<Item> item = ItemRegistryHelper.registerArmor(tab, name, supplier);
        ARMOR_MODELS.put(item, modelType);
        if (modelData.length > 0) {
            ARMOR_MODEL_DATA.put(item, modelData);
        }
        return item;
    }

    public static final RegistryObject<Item> EUPHEMIUM_HELMET = register(
            WEAPON_TAB,
            "armor/euphemium_helmet",
            () -> new ArmorEuphemium(ModArmorMaterials.EUPHEMIUM, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> EUPHEMIUM_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/euphemium_chestplate",
            () -> new ArmorEuphemium(ModArmorMaterials.EUPHEMIUM, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> EUPHEMIUM_LEGGINGS = register(
            WEAPON_TAB,
            "armor/euphemium_leggings",
            () -> new ArmorEuphemium(ModArmorMaterials.EUPHEMIUM, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> EUPHEMIUM_BOOTS = register(
            WEAPON_TAB,
            "armor/euphemium_boots",
            () -> new ArmorEuphemium(ModArmorMaterials.EUPHEMIUM, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> TITANIUM_HELMET = register(
            WEAPON_TAB,
            "armor/titanium_helmet",
            () -> new ArmorFSB(ModArmorMaterials.TITANIUM, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(275)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> TITANIUM_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/titanium_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.TITANIUM, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(400))
                    .cloneStats((ArmorFSB) TITANIUM_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> TITANIUM_LEGGINGS = register(
            WEAPON_TAB,
            "armor/titanium_leggings",
            () -> new ArmorFSB(ModArmorMaterials.TITANIUM, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(375))
                    .cloneStats((ArmorFSB) TITANIUM_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> TITANIUM_BOOTS = register(
            WEAPON_TAB,
            "armor/titanium_boots",
            () -> new ArmorFSB(ModArmorMaterials.TITANIUM, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(325))
                    .cloneStats((ArmorFSB) TITANIUM_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STEEL_HELMET = register(
            WEAPON_TAB,
            "armor/steel_helmet",
            () -> new ArmorFSB(ModArmorMaterials.STEEL, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(220)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STEEL_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/steel_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(320))
                    .cloneStats((ArmorFSB) STEEL_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STEEL_LEGGINGS = register(
            WEAPON_TAB,
            "armor/steel_leggings",
            () -> new ArmorFSB(ModArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(300))
                    .cloneStats((ArmorFSB) STEEL_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STEEL_BOOTS = register(
            WEAPON_TAB,
            "armor/steel_boots",
            () -> new ArmorFSB(ModArmorMaterials.STEEL, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(260))
                    .cloneStats((ArmorFSB) STEEL_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ADVANCED_ALLOY_HELMET = register(
            WEAPON_TAB,
            "armor/advanced_alloy_helmet",
            () -> new ArmorFSB(ModArmorMaterials.ADVANCED_ALLOY, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(440)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ADVANCED_ALLOY_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/advanced_alloy_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.ADVANCED_ALLOY, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(640))
                    .cloneStats((ArmorFSB) ADVANCED_ALLOY_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ADVANCED_ALLOY_LEGGINGS = register(
            WEAPON_TAB,
            "armor/advanced_alloy_leggings",
            () -> new ArmorFSB(ModArmorMaterials.ADVANCED_ALLOY, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(600))
                    .cloneStats((ArmorFSB) ADVANCED_ALLOY_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ADVANCED_ALLOY_BOOTS = register(
            WEAPON_TAB,
            "armor/advanced_alloy_boots",
            () -> new ArmorFSB(ModArmorMaterials.ADVANCED_ALLOY, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(520))
                    .cloneStats((ArmorFSB) ADVANCED_ALLOY_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCHRABIDIUM_HELMET = register(
            WEAPON_TAB,
            "armor/schrabidium_helmet",
            () -> new ArmorFSB(ModArmorMaterials.SCHRABIDIUM, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1300))
                    .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCHRABIDIUM_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/schrabidium_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.SCHRABIDIUM, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(1600))
                    .cloneStats((ArmorFSB) SCHRABIDIUM_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCHRABIDIUM_LEGGINGS = register(
            WEAPON_TAB,
            "armor/schrabidium_leggings",
            () -> new ArmorFSB(ModArmorMaterials.SCHRABIDIUM, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(1500))
                    .cloneStats((ArmorFSB) SCHRABIDIUM_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCHRABIDIUM_BOOTS = register(
            WEAPON_TAB,
            "armor/schrabidium_boots",
            () -> new ArmorFSB(ModArmorMaterials.SCHRABIDIUM, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1300))
                    .cloneStats((ArmorFSB) SCHRABIDIUM_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BISMUTH_HELMET = register(
            WEAPON_TAB,
            "armor/bismuth_helmet",
            () -> new ArmorFSB(ModArmorMaterials.BISMUTH, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1300))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 6))
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 6))
                    .addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0))
                    .setDashCount(3),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BISMUTH_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/bismuth_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.BISMUTH, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(1600))
                    .cloneStats((ArmorFSB) BISMUTH_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BISMUTH_LEGGINGS = register(
            WEAPON_TAB,
            "armor/bismuth_leggings",
            () -> new ArmorFSB(ModArmorMaterials.BISMUTH, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(1500))
                    .cloneStats((ArmorFSB) BISMUTH_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BISMUTH_BOOTS = register(
            WEAPON_TAB,
            "armor/bismuth_boots",
            () -> new ArmorFSB(ModArmorMaterials.BISMUTH, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1300))
                    .cloneStats((ArmorFSB) BISMUTH_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CMB_HELMET = register(
            WEAPON_TAB,
            "armor/cmb_steel_helmet",
            () -> new ArmorFSB(ModArmorMaterials.CMB, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(780))
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 4)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CMB_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/cmb_steel_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.CMB, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(960))
                    .cloneStats((ArmorFSB) CMB_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CMB_LEGGINGS = register(
            WEAPON_TAB,
            "armor/cmb_steel_leggings",
            () -> new ArmorFSB(ModArmorMaterials.CMB, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(900))
                    .cloneStats((ArmorFSB) CMB_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CMB_BOOTS = register(
            WEAPON_TAB,
            "armor/cmb_steel_boots",
            () -> new ArmorFSB(ModArmorMaterials.CMB, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(780))
                    .cloneStats((ArmorFSB) CMB_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PAA_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/paa_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.PAA, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(1200))
                    .setNoHelmet(true)
                    .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 0)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PAA_LEGGINGS = register(
            WEAPON_TAB,
            "armor/paa_leggings",
            () -> new ArmorFSB(ModArmorMaterials.PAA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(1125))
                    .cloneStats((ArmorFSB) PAA_CHESTPLATE.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PAA_BOOTS = register(
            WEAPON_TAB,
            "armor/paa_boots",
            () -> new ArmorFSB(ModArmorMaterials.PAA, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(975))
                    .cloneStats((ArmorFSB) PAA_CHESTPLATE.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ASBESTOS_HELMET = register(
            WEAPON_TAB,
            "armor/asbestos_helmet",
            () -> new ArmorFSB(ModArmorMaterials.ASBESTOS, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(220))
                    .setOverlay("textures/misc/overlay_asbestos.png"),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ASBESTOS_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/asbestos_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.ASBESTOS, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(320)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ASBESTOS_LEGGINGS = register(
            WEAPON_TAB,
            "armor/asbestos_leggings",
            () -> new ArmorFSB(ModArmorMaterials.ASBESTOS, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(300)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ASBESTOS_BOOTS = register(
            WEAPON_TAB,
            "armor/asbestos_boots",
            () -> new ArmorFSB(ModArmorMaterials.ASBESTOS, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(220)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECURITY_HELMET = register(
            WEAPON_TAB,
            "armor/security_helmet",
            () -> new ArmorFSB(ModArmorMaterials.SECURITY, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1100)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECURITY_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/security_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.SECURITY, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(1600))
                    .cloneStats((ArmorFSB) SECURITY_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECURITY_LEGGINGS = register(
            WEAPON_TAB,
            "armor/security_leggings",
            () -> new ArmorFSB(ModArmorMaterials.SECURITY, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(1500))
                    .cloneStats((ArmorFSB) SECURITY_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECURITY_BOOTS = register(
            WEAPON_TAB,
            "armor/security_boots",
            () -> new ArmorFSB(ModArmorMaterials.SECURITY, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1300))
                    .cloneStats((ArmorFSB) SECURITY_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> COBALT_HELMET = register(
            WEAPON_TAB,
            "armor/cobalt_helmet",
            () -> new ArmorFSB(ModArmorMaterials.COBALT, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(910)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> COBALT_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/cobalt_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.COBALT, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(1120))
                    .cloneStats((ArmorFSB) COBALT_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> COBALT_LEGGINGS = register(
            WEAPON_TAB,
            "armor/cobalt_leggings",
            () -> new ArmorFSB(ModArmorMaterials.COBALT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(1050))
                    .cloneStats((ArmorFSB) COBALT_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> COBALT_BOOTS = register(
            WEAPON_TAB,
            "armor/cobalt_boots",
            () -> new ArmorFSB(ModArmorMaterials.COBALT, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(910))
                    .cloneStats((ArmorFSB) COBALT_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STARMETAL_HELMET = register(
            WEAPON_TAB,
            "armor/starmetal_helmet",
            () -> new ArmorFSB(ModArmorMaterials.STARMETAL, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STARMETAL_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/starmetal_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.STARMETAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400))
                    .cloneStats((ArmorFSB) STARMETAL_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STARMETAL_LEGGINGS = register(
            WEAPON_TAB,
            "armor/starmetal_leggings",
            () -> new ArmorFSB(ModArmorMaterials.STARMETAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250))
                    .cloneStats((ArmorFSB) STARMETAL_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STARMETAL_BOOTS = register(
            WEAPON_TAB,
            "armor/starmetal_boots",
            () -> new ArmorFSB(ModArmorMaterials.STARMETAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1650))
                    .cloneStats((ArmorFSB) STARMETAL_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_HELMET = register(
            WEAPON_TAB,
            "armor/hazmat_helmet",
            () -> new ArmorHazmatMask(ModArmorMaterials.HAZMAT, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(660))
                    .setOverlay("textures/misc/overlay_hazmat.png"),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/hazmat_chestplate",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(960))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_LEGGINGS = register(
            WEAPON_TAB,
            "armor/hazmat_leggings",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(900))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_BOOTS = register(
            WEAPON_TAB,
            "armor/hazmat_boots",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(780))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_HELMET_RED = register(
            WEAPON_TAB,
            "armor/hazmat_helmet_red",
            () -> new ArmorHazmatMask(ModArmorMaterials.HAZMAT2, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(660))
                    .setOverlay("textures/misc/overlay_hazmat.png"),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_CHESTPLATE_RED = register(
            WEAPON_TAB,
            "armor/hazmat_chestplate_red",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT2, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(960))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET_RED.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_LEGGINGS_RED = register(
            WEAPON_TAB,
            "armor/hazmat_leggings_red",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(900))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET_RED.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_BOOTS_RED = register(
            WEAPON_TAB,
            "armor/hazmat_boots_red",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT2, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(780))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET_RED.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_HELMET_GREY = register(
            WEAPON_TAB,
            "armor/hazmat_helmet_grey",
            () -> new ArmorHazmatMask(ModArmorMaterials.HAZMAT3, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(660))
                    .setOverlay("textures/misc/overlay_hazmat.png"),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_CHESTPLATE_GREY = register(
            WEAPON_TAB,
            "armor/hazmat_chestplate_grey",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT3, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(960))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET_GREY.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_LEGGINGS_GREY = register(
            WEAPON_TAB,
            "armor/hazmat_leggings_grey",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(900))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET_GREY.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_BOOTS_GREY = register(
            WEAPON_TAB,
            "armor/hazmat_boots_grey",
            () -> new ArmorHazmat(ModArmorMaterials.HAZMAT3, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(780))
                    .cloneStats((ArmorFSB) HAZMAT_HELMET_GREY.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_PAA_HELMET = register(
            WEAPON_TAB,
            "armor/hazmat_paa_helmet",
            () -> new ArmorHazmatMask(ModArmorMaterials.PAA, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(825))
                    .setOverlay("textures/misc/overlay_hazmat.png"),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_PAA_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/hazmat_paa_chestplate",
            () -> new ArmorHazmat(ModArmorMaterials.PAA, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(1200))
                    .cloneStats((ArmorFSB) HAZMAT_PAA_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_PAA_LEGGINGS = register(
            WEAPON_TAB,
            "armor/hazmat_paa_leggings",
            () -> new ArmorHazmat(ModArmorMaterials.PAA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(1125))
                    .cloneStats((ArmorFSB) HAZMAT_PAA_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_PAA_BOOTS = register(
            WEAPON_TAB,
            "armor/hazmat_paa_boots",
            () -> new ArmorHazmat(ModArmorMaterials.PAA, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(975))
                    .cloneStats((ArmorFSB) HAZMAT_PAA_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROBES_HELMET = register(
            WEAPON_TAB,
            "armor/robes_helmet",
            () -> new ArmorFSB(ModArmorMaterials.ROBES, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(110)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROBES_CHESTPLATE = register(
            WEAPON_TAB,
            "armor/robes_chestplate",
            () -> new ArmorFSB(ModArmorMaterials.ROBES, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(160))
                    .cloneStats((ArmorFSB) ROBES_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROBES_LEGGINGS = register(
            WEAPON_TAB,
            "armor/robes_leggings",
            () -> new ArmorFSB(ModArmorMaterials.ROBES, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(150))
                    .cloneStats((ArmorFSB) ROBES_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROBES_BOOTS = register(
            WEAPON_TAB,
            "armor/robes_boots",
            () -> new ArmorFSB(ModArmorMaterials.ROBES, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(130))
                    .cloneStats((ArmorFSB) ROBES_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIQUIDATOR_HELMET = register(WEAPON_TAB,"armor/liquidator_helmet",
            () -> new ArmorLiquidatorMask(ModArmorMaterials.LIQUIDATOR, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(440)) // 40 * 11 = 440
                    .setStep(ModSounds.STEP_METAL.get())
                    .setJump(ModSounds.STEP_IRON_JUMP.get())
                    .setFall(ModSounds.STEP_IRON_LAND.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIQUIDATOR_CHESTPLATE = register(WEAPON_TAB,"armor/liquidator_chestplate",
            () -> new ArmorLiquidator(ModArmorMaterials.LIQUIDATOR, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(640)) // 40 * 16 = 640
                    .cloneStats((ArmorFSB) LIQUIDATOR_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIQUIDATOR_LEGGINGS = register(WEAPON_TAB,"armor/liquidator_leggings",
            () -> new ArmorLiquidator(ModArmorMaterials.LIQUIDATOR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(600)) // 40 * 15 = 600
                    .cloneStats((ArmorFSB) LIQUIDATOR_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIQUIDATOR_BOOTS = register(WEAPON_TAB,"armor/liquidator_boots",
            () -> new ArmorLiquidator(ModArmorMaterials.LIQUIDATOR, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(520)) // 40 * 13 = 520
                    .cloneStats((ArmorFSB) LIQUIDATOR_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> T45_HELMET = register(WEAPON_TAB, "armor/t45_helmet",
            () -> new ArmorT45(ModArmorMaterials.T45, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650), // 150 * 11 = 1650
                    1000000, 10000, 1000, 5)
                    .enableVATS(true)
                    .setHasGeigerSound(true)
                    .setHasHardLanding(true)
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 0))
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> T45_CHESTPLATE = register(WEAPON_TAB, "armor/t45_chestplate",
            () -> new ArmorT45(ModArmorMaterials.T45, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400), // 150 * 16 = 2400
                    1000000, 10000, 1000, 5)
                    .cloneStats((ArmorFSB) T45_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> T45_LEGGINGS = register(WEAPON_TAB, "armor/t45_leggings",
            () -> new ArmorT45(ModArmorMaterials.T45, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250), // 150 * 15 = 2250
                    1000000, 10000, 1000, 5)
                    .cloneStats((ArmorFSB) T45_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> T45_BOOTS = register(WEAPON_TAB, "armor/t45_boots",
            () -> new ArmorT45(ModArmorMaterials.T45, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950), // 150 * 13 = 1950
                    1000000, 10000, 1000, 5)
                    .cloneStats((ArmorFSB) T45_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> T51_HELMET = register(WEAPON_TAB, "armor/t51_helmet",
            () -> new ArmorT51(ModArmorMaterials.T51, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    1000000, 10000, 1000, 5)
                    .enableVATS(true)
                    .setHasGeigerSound(true)
                    .setHasHardLanding(true)
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 0))
                    .setStep(ModSounds.STEP_METAL.get())
                    .setJump(ModSounds.STEP_IRON_JUMP.get())
                    .setFall(ModSounds.STEP_IRON_LAND.get())
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> T51_CHESTPLATE = register(WEAPON_TAB, "armor/t51_chestplate",
            () -> new ArmorT51(ModArmorMaterials.T51, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    1000000, 10000, 1000, 5)
                    .cloneStats((ArmorFSB) T51_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> T51_LEGGINGS = register(WEAPON_TAB, "armor/t51_leggings",
            () -> new ArmorT51(ModArmorMaterials.T51, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    1000000, 10000, 1000, 5)
                    .cloneStats((ArmorFSB) T51_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> T51_BOOTS = register(WEAPON_TAB, "armor/t51_boots",
            () -> new ArmorT51(ModArmorMaterials.T51, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    1000000, 10000, 1000, 5)
                    .cloneStats((ArmorFSB) T51_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> AJR_HELMET = register(WEAPON_TAB, "armor/ajr_helmet",
            () -> new ArmorAJR(ModArmorMaterials.AJR, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    2500000, 10000, 2000, 25)
                    .enableVATS(true)
                    .setHasGeigerSound(true)
                    .setHasHardLanding(true)
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 0))
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 0))
                    .setStep(ModSounds.STEP_METAL.get())
                    .setJump(ModSounds.STEP_IRON_JUMP.get())
                    .setFall(ModSounds.STEP_IRON_LAND.get())
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> AJR_CHESTPLATE = register(WEAPON_TAB, "armor/ajr_chestplate",
            () -> new ArmorAJR(ModArmorMaterials.AJR, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) AJR_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> AJR_LEGGINGS = register(WEAPON_TAB, "armor/ajr_leggings",
            () -> new ArmorAJR(ModArmorMaterials.AJR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) AJR_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> AJR_BOOTS = register(WEAPON_TAB, "armor/ajr_boots",
            () -> new ArmorAJR(ModArmorMaterials.AJR, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) AJR_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> AJRO_HELMET = register(WEAPON_TAB, "armor/ajro_helmet",
            () -> new ArmorAJRO(ModArmorMaterials.AJR, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    2500000, 10000, 2000, 25)
                    .enableVATS(true)
                    .setHasGeigerSound(true)
                    .setHasHardLanding(true)
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 0))
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 0))
                    .setStep(ModSounds.STEP_METAL.get())
                    .setJump(ModSounds.STEP_IRON_JUMP.get())
                    .setFall(ModSounds.STEP_IRON_LAND.get())
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> AJRO_CHESTPLATE = register(WEAPON_TAB, "armor/ajro_chestplate",
            () -> new ArmorAJRO(ModArmorMaterials.AJR, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) AJRO_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> AJRO_LEGGINGS = register(WEAPON_TAB, "armor/ajro_leggings",
            () -> new ArmorAJRO(ModArmorMaterials.AJR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) AJRO_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> AJRO_BOOTS = register(WEAPON_TAB, "armor/ajro_boots",
            () -> new ArmorAJRO(ModArmorMaterials.AJR, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) AJRO_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> STEAMSUIT_HELMET = register(WEAPON_TAB, "armor/steamsuit_helmet",
            () -> new ArmorDesh(ModArmorMaterials.DESH, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    Fluids.STEAM, 64000, 500, 50, 1)
                    .setHasHardLanding(true)
                    .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 4))
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> STEAMSUIT_CHESTPLATE = register(WEAPON_TAB, "armor/steamsuit_chestplate",
            () -> new ArmorDesh(ModArmorMaterials.DESH, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    Fluids.STEAM, 64000, 500, 50, 1)
                    .cloneStats((ArmorFSB) STEAMSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> STEAMSUIT_LEGGINGS = register(WEAPON_TAB, "armor/steamsuit_leggings",
            () -> new ArmorDesh(ModArmorMaterials.DESH, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    Fluids.STEAM, 64000, 500, 50, 1)
                    .cloneStats((ArmorFSB) STEAMSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> STEAMSUIT_BOOTS = register(WEAPON_TAB, "armor/steamsuit_boots",
            () -> new ArmorDesh(ModArmorMaterials.DESH, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    Fluids.STEAM, 64000, 500, 50, 1)
                    .cloneStats((ArmorFSB) STEAMSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> DIESELSUIT_HELMET = register(WEAPON_TAB, "armor/dieselsuit_helmet",
            () -> new ArmorDiesel(ModArmorMaterials.DIESEL, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    Fluids.DIESEL, 64000, 500, 50, 1)
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 2))
                    .enableThermalSight(true)
                    .enableVATS(true),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> DIESELSUIT_CHESTPLATE = register(WEAPON_TAB, "armor/dieselsuit_chestplate",
            () -> new ArmorDiesel(ModArmorMaterials.DIESEL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    Fluids.DIESEL, 64000, 500, 50, 1)
                    .cloneStats((ArmorDiesel) DIESELSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> DIESELSUIT_LEGGINGS = register(WEAPON_TAB, "armor/dieselsuit_leggings",
            () -> new ArmorDiesel(ModArmorMaterials.DIESEL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    Fluids.DIESEL, 64000, 500, 50, 1)
                    .cloneStats((ArmorDiesel) DIESELSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> DIESELSUIT_BOOTS = register(WEAPON_TAB, "armor/dieselsuit_boots",
            () -> new ArmorDiesel(ModArmorMaterials.DIESEL, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    Fluids.DIESEL, 64000, 500, 50, 1)
                    .cloneStats((ArmorDiesel) DIESELSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> HEV_HELMET = register(WEAPON_TAB, "armor/hev_helmet",
            () -> new ArmorHEV(ModArmorMaterials.HEV, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    1000000, 10000, 2500, 0)
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 0))
                    .setHasGeigerSound(true)
                    .setHasCustomGeiger(true)
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> HEV_CHESTPLATE = register(WEAPON_TAB, "armor/hev_chestplate",
            () -> new ArmorHEV(ModArmorMaterials.HEV, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    1000000, 10000, 2500, 0)
                    .cloneStats((ArmorFSB) HEV_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> HEV_LEGGINGS = register(WEAPON_TAB, "armor/hev_leggings",
            () -> new ArmorHEV(ModArmorMaterials.HEV, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    1000000, 10000, 2500, 0)
                    .cloneStats((ArmorFSB) HEV_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> HEV_BOOTS = register(WEAPON_TAB, "armor/hev_boots",
            () -> new ArmorHEV(ModArmorMaterials.HEV, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    1000000, 10000, 2500, 0)
                    .cloneStats((ArmorFSB) HEV_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> FAU_HELMET = register(WEAPON_TAB, "armor/fau_helmet",
            () -> new ArmorFAU(ModArmorMaterials.DIGAMMA, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    10000000, 10000, 2500, 0)
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 1))
                    .setHasGeigerSound(true)
                    .enableThermalSight(true)
                    .setHasHardLanding(true)
                    .setStep(ModSounds.STEP_METAL.get())
                    .setJump(ModSounds.STEP_IRON_JUMP.get())
                    .setFall(ModSounds.STEP_IRON_LAND.get())
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> FAU_CHESTPLATE = register(WEAPON_TAB, "armor/fau_chestplate",
            () -> new ArmorFAU(ModArmorMaterials.DIGAMMA, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    10000000, 10000, 2500, 0)
                    .cloneStats((ArmorFSB) FAU_HELMET.get())
                    .setFullSetForHide(),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> FAU_LEGGINGS = register(WEAPON_TAB, "armor/fau_leggings",
            () -> new ArmorFAU(ModArmorMaterials.DIGAMMA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    10000000, 10000, 2500, 0)
                    .cloneStats((ArmorFSB) FAU_HELMET.get())
                    .hides(IArmorDisableModel.EnumPlayerPart.LEFT_LEG, IArmorDisableModel.EnumPlayerPart.RIGHT_LEG)
                    .setFullSetForHide(),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> FAU_BOOTS = register(WEAPON_TAB, "armor/fau_boots",
            () -> new ArmorFAU(ModArmorMaterials.DIGAMMA, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    10000000, 10000, 2500, 0)
                    .cloneStats((ArmorFSB) FAU_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> DNT_HELMET = register(WEAPON_TAB, "armor/dnt_helmet",
            () -> new ArmorDNT(ModArmorMaterials.DNT, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    1000000000, 1000000, 100000, 115)
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 9))
                    .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 7))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 2))
                    .setHasGeigerSound(true)
                    .enableVATS(true)
                    .enableThermalSight(true)
                    .setHasHardLanding(true)
                    .setStep(ModSounds.STEP_METAL.get())
                    .setJump(ModSounds.STEP_IRON_JUMP.get())
                    .setFall(ModSounds.STEP_IRON_LAND.get())
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> DNT_CHESTPLATE = register(WEAPON_TAB, "armor/dnt_chestplate",
            () -> new ArmorDNT(ModArmorMaterials.DNT, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    1000000000, 1000000, 100000, 115)
                    .cloneStats((ArmorFSB) DNT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> DNT_LEGGINGS = register(WEAPON_TAB, "armor/dnt_leggings",
            () -> new ArmorDNT(ModArmorMaterials.DNT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    1000000000, 1000000, 100000, 115)
                    .cloneStats((ArmorFSB) DNT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> DNT_BOOTS = register(WEAPON_TAB, "armor/dnt_boots",
            () -> new ArmorDNT(ModArmorMaterials.DNT, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    1000000000, 1000000, 100000, 115)
                    .cloneStats((ArmorFSB) DNT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> RPA_HELMET = register(WEAPON_TAB, "armor/rpa_helmet",
            () -> new ArmorRPA(ModArmorMaterials.AJR, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    2500000, 10000, 2000, 25)
                    .enableVATS(true)
                    .setHasGeigerSound(true)
                    .setHasHardLanding(true)
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 3))
                    .setStep(ModSounds.STEP_POWERED.get())
                    .setJump(ModSounds.STEP_POWERED.get())
                    .setFall(ModSounds.STEP_POWERED.get())
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> RPA_CHESTPLATE = register(WEAPON_TAB, "armor/rpa_chestplate",
            () -> new ArmorRPA(ModArmorMaterials.AJR, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) RPA_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> RPA_LEGGINGS = register(WEAPON_TAB, "armor/rpa_leggings",
            () -> new ArmorRPA(ModArmorMaterials.AJR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) RPA_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> RPA_BOOTS = register(WEAPON_TAB, "armor/rpa_boots",
            () -> new ArmorRPA(ModArmorMaterials.AJR, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    2500000, 10000, 2000, 25)
                    .cloneStats((ArmorFSB) RPA_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> ENVSUIT_HELMET = register(WEAPON_TAB, "armor/envsuit_helmet",
            () -> new ArmorEnvsuit(ModArmorMaterials.ENV, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    100000, 1000, 250, 0)
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 0))
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> ENVSUIT_CHESTPLATE = register(WEAPON_TAB, "armor/envsuit_chestplate",
            () -> new ArmorEnvsuit(ModArmorMaterials.ENV, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    100000, 1000, 250, 0)
                    .cloneStats((ArmorFSB) ENVSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> ENVSUIT_LEGGINGS = register(WEAPON_TAB, "armor/envsuit_leggings",
            () -> new ArmorEnvsuit(ModArmorMaterials.ENV, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    100000, 1000, 250, 0)
                    .cloneStats((ArmorFSB) ENVSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> ENVSUIT_BOOTS = register(WEAPON_TAB, "armor/envsuit_boots",
            () -> new ArmorEnvsuit(ModArmorMaterials.ENV, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950),
                    100000, 1000, 250, 0)
                    .cloneStats((ArmorFSB) ENVSUIT_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> TRENCHMASTER_HELMET = register(WEAPON_TAB, "armor/trenchmaster_helmet",
            () -> new ArmorTrenchmaster(ModArmorMaterials.TRENCH, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650))
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0))
                    .enableVATS(true)
                    .setStepSize(1)
                    .hides(IArmorDisableModel.EnumPlayerPart.HAT),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> TRENCHMASTER_CHESTPLATE = register(WEAPON_TAB, "armor/trenchmaster_chestplate",
            () -> new ArmorTrenchmaster(ModArmorMaterials.TRENCH, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400))
                    .cloneStats((ArmorFSB) TRENCHMASTER_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> TRENCHMASTER_LEGGINGS = register(WEAPON_TAB, "armor/trenchmaster_leggings",
            () -> new ArmorTrenchmaster(ModArmorMaterials.TRENCH, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250))
                    .cloneStats((ArmorFSB) TRENCHMASTER_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> TRENCHMASTER_BOOTS = register(WEAPON_TAB, "armor/trenchmaster_boots",
            () -> new ArmorTrenchmaster(ModArmorMaterials.TRENCH, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).durability(1950))
                    .cloneStats((ArmorFSB) TRENCHMASTER_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> BJ_HELMET = register(WEAPON_TAB, "armor/bj_helmet",
            () -> new ArmorBJ(ModArmorMaterials.BJ, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(1650),
                    10000000, 10000, 1000, 100)
                    .enableVATS(true)
                    .enableThermalSight(true)
                    .setHasGeigerSound(true)
                    .setHasHardLanding(true)
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 0))
                    .addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 0))
                    .addEffect(new MobEffectInstance(HbmPotion.RADX.get(), 20, 0))
                    .setStep(ModSounds.STEP_METAL.get())
                    .setJump(ModSounds.STEP_IRON_JUMP.get())
                    .setFall(ModSounds.STEP_IRON_LAND.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> BJ_CHESTPLATE = register(WEAPON_TAB, "armor/bj_chestplate",
            () -> new ArmorBJ(ModArmorMaterials.BJ, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    10000000, 10000, 1000, 100)
                    .cloneStats((ArmorFSB) BJ_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> BJ_CHESTPLATE_JETPACK = register(WEAPON_TAB, "armor/bj_chestplate_jetpack",
            () -> new ArmorBJJetpack(ModArmorMaterials.BJ, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(2400),
                    10000000, 10000, 1000, 100)
                    .cloneStats((ArmorFSB) BJ_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> BJ_LEGGINGS = register(WEAPON_TAB, "armor/bj_leggings",
            () -> new ArmorBJ(ModArmorMaterials.BJ, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).durability(2250),
                    10000000, 10000, 1000, 100)
                    .cloneStats((ArmorFSB) BJ_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> BJ_BOOTS = register(WEAPON_TAB, "armor/bj_boots",
            () -> new ArmorBJ(ModArmorMaterials.BJ, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1),
                    10000000, 10000, 1000, 100)
                    .cloneStats((ArmorFSB) BJ_HELMET.get()),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> GAS_MASK = register(WEAPON_TAB, "armor/gas_mask",
            () -> new ArmorGasMask(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_MASK_M65 = register(WEAPON_TAB, "armor/gas_mask_m65",
            () -> new ArmorGasMask(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_MASK_MONO = register(WEAPON_TAB, "armor/gas_mask_mono",
            () -> new ArmorGasMask(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_MASK_OLDE = register(WEAPON_TAB, "armor/gas_mask_olde",
            () -> new ArmorGasMask(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> GOGGLES = register(WEAPON_TAB, "armor/goggles",
            () -> new ArmorModel(ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ASHGLASSES = register(WEAPON_TAB, "armor/ashglasses",
            () -> new ArmorAshGlasses(ArmorMaterials.IRON, new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAPE_RADIATION = register(WEAPON_TAB, "armor/cape_radiation",
            () -> new ArmorModel(ArmorMaterials.CHAIN, ArmorItem.Type.CHESTPLATE, new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAPE_GASMASK = register(WEAPON_TAB, "armor/cape_gasmask",
            () -> new ArmorModel(ArmorMaterials.CHAIN, ArmorItem.Type.CHESTPLATE, new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAPE_SCHRABIDIUM = register(WEAPON_TAB, "armor/cape_schrabidium",
            () -> new ArmorModel(ModArmorMaterials.SCHRABIDIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAPE_HIDDEN = register(WEAPON_TAB, "armor/cape_hidden",
            () -> new ArmorModel(ArmorMaterials.CHAIN, ArmorItem.Type.CHESTPLATE, new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> JACKET = register(WEAPON_TAB, "armor/jacket",
            () -> new ModArmor(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> JACKET_2 = register(WEAPON_TAB, "armor/jacket_2",
            () -> new ModArmor(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MASK_DRY = register(WEAPON_TAB,"armor/mask_dry",
            () -> new MaskItem(MASK_DRY_MATERIAL, new Item.Properties().durability(256),false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MASK_PISS = register(WEAPON_TAB,"armor/mask_piss",
            () -> new MaskItem(MASK_PISS_MATERIAL, new Item.Properties().durability(256),true),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MASK_WET = register(WEAPON_TAB,"armor/mask_wet",
            () -> new MaskItem(MASK_WET_MATERIAL, new Item.Properties().durability(256), true),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MASK_OF_INFAMY = register(WEAPON_TAB, "armor/mask_of_infamy",
            () -> new MaskOfInfamy(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> NO9 = register(WEAPON_TAB,"armor/armor_no9",
            () -> new ArmorNo9(ModArmorMaterials.STEEL, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).durability(0)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAT = register(WEAPON_TAB, "armor/hat",
            () -> new ArmorHat(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ZIRCONIUM_LEGGINGS = register(WEAPON_TAB,"armor/zirconium_leggings",
            () -> new ArmorFSB(ModArmorMaterials.ZIRCONIUM, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1))
                    .cloneStats((ArmorFSB) ROBES_HELMET.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ATTACHMENT_MASK = register(WEAPON_TAB, "armor/attachment_mask",
            () -> new ItemModGasmask(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ATTACHMENT_MASK_MONO = register(WEAPON_TAB, "armor/attachment_mask_mono",
            () -> new ItemModGasmask(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> JETPACK_FLY = register(WEAPON_TAB, "armor/jetpack_fly",
            () -> new JetpackRegular(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(100),
                    Fluids.KEROSENE, 12000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> JETPACK_BREAK = register(WEAPON_TAB, "armor/jetpack_break",
            () -> new JetpackBreak(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(100),
                    Fluids.KEROSENE, 12000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> JETPACK_VECTOR = register(WEAPON_TAB, "armor/jetpack_vector",
            () -> new JetpackVectorized(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(100),
                    Fluids.KEROSENE, 16000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> JETPACK_BOOST = register(WEAPON_TAB, "armor/jetpack_boost",
            () -> new JetpackBooster(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(100),
                    Fluids.BALEFIRE, 32000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BACK_TESLA = register(WEAPON_TAB, "back_tesla",
            () -> new ItemModTesla(new Item.Properties().stacksTo(1)),
            ItemModelType.BUILTIN_ENTITY);

    public static void register(IEventBus eventBus) {
        ARMOR.register(eventBus);
    }
}