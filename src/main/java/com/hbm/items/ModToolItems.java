package com.hbm.items;

import com.hbm.api.block.IToolable;
import com.hbm.datagen.models.ItemModelType;
import com.hbm.handler.ability.*;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.tool.*;
import com.hbm.items.weapon.ItemCrucible;
import com.hbm.items.weapon.ItemSwordAbility;
import com.hbm.util.HBMEnums;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.hbm.util.HBMEnums.CreativeTabRegistry.*;
import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class ModToolItems {
    public static final DeferredRegister<Item> TOOLS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final Map<RegistryObject<Item>, ItemModelType> TOOLS_MODELS = new LinkedHashMap<>();
    public static final Map<RegistryObject<Item>, Object[]> TOOLS_MODEL_DATA = new LinkedHashMap<>();

    public static RegistryObject<Item> register(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier) {
        return com.hbm.items.ItemRegistryHelper.registerTools(tab, name, supplier);
    }

    // ============ СТАЛЬНЫЕ ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> STEEL_SWORD = swordAbility("weapon/steel_sword", ModToolMaterials.STEEL, 5, -2.4F);
    public static final RegistryObject<Item> STEEL_PICKAXE = toolAbility("tools/steel_pickaxe", ModToolMaterials.STEEL, BlockTags.MINEABLE_WITH_PICKAXE, 4.0F, -2.8F);
    public static final RegistryObject<Item> STEEL_AXE = toolAbility("tools/steel_axe", ModToolMaterials.STEEL, BlockTags.MINEABLE_WITH_AXE, 5.0F, -3.0F, IWeaponAbility.BEHEADER, 0);
    public static final RegistryObject<Item> STEEL_SHOVEL = toolAbility("tools/steel_shovel", ModToolMaterials.STEEL, BlockTags.MINEABLE_WITH_SHOVEL, 3.0F, -3.0F);

    // ============ ТИТАНОВЫЕ ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> TITANIUM_SWORD = swordAbility("weapon/titanium_sword", ModToolMaterials.TITANIUM, 6, -2.4F);
    public static final RegistryObject<Item> TITANIUM_PICKAXE = toolAbility("tools/titanium_pickaxe", ModToolMaterials.TITANIUM, BlockTags.MINEABLE_WITH_PICKAXE, 4.5F, -2.8F);
    public static final RegistryObject<Item> TITANIUM_AXE = toolAbility("tools/titanium_axe", ModToolMaterials.TITANIUM, BlockTags.MINEABLE_WITH_AXE, 5.5F, -3.0F, IWeaponAbility.BEHEADER, 0);
    public static final RegistryObject<Item> TITANIUM_SHOVEL = toolAbility("tools/titanium_shovel", ModToolMaterials.TITANIUM, BlockTags.MINEABLE_WITH_SHOVEL, 3.5F, -3.0F);

    // ============ СПЛАВНЫЕ ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> ADVANCED_ALLOY_SWORD = swordAbility("weapon/advanced_alloy_sword", ModToolMaterials.ALLOY, 7, -2.4F, IWeaponAbility.STUN, 0);
    public static final RegistryObject<Item> ADVANCED_ALLOY_PICKAXE = toolAbility("tools/advanced_alloy_pickaxe", ModToolMaterials.ALLOY, BlockTags.MINEABLE_WITH_PICKAXE, 5.0F, -2.8F, IToolAreaAbility.RECURSION, 0);
    public static final RegistryObject<Item> ADVANCED_ALLOY_AXE = toolAbility("tools/advanced_alloy_axe", ModToolMaterials.ALLOY, BlockTags.MINEABLE_WITH_AXE, 7.0F, -3.0F, IToolAreaAbility.RECURSION, 0, IWeaponAbility.BEHEADER, 0);
    public static final RegistryObject<Item> ADVANCED_ALLOY_SHOVEL = toolAbility("tools/advanced_alloy_shovel", ModToolMaterials.ALLOY, BlockTags.MINEABLE_WITH_SHOVEL, 4.0F, -3.0F, IToolAreaAbility.RECURSION, 0);

    // ============ CMB ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> CMB_SWORD = swordAbility("weapon/cmb_steel_sword", ModToolMaterials.CMB, 10, -2.4F, IWeaponAbility.STUN, 0, IWeaponAbility.VAMPIRE, 0);
    public static final RegistryObject<Item> CMB_PICKAXE = toolAbility("tools/cmb_steel_pickaxe", ModToolMaterials.CMB, BlockTags.MINEABLE_WITH_PICKAXE, 10.0F, -2.8F, IToolAreaAbility.RECURSION, 2, IToolHarvestAbility.SMELTER, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 2);
    public static final RegistryObject<Item> CMB_AXE = toolAbility("tools/cmb_steel_axe", ModToolMaterials.CMB, BlockTags.MINEABLE_WITH_AXE, 30.0F, -3.0F, IToolAreaAbility.RECURSION, 2, IToolHarvestAbility.SMELTER, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 2, IWeaponAbility.BEHEADER, 0);
    public static final RegistryObject<Item> CMB_SHOVEL = toolAbility("tools/cmb_steel_shovel", ModToolMaterials.CMB, BlockTags.MINEABLE_WITH_SHOVEL, 8.0F, -3.0F, IToolAreaAbility.RECURSION, 2, IToolHarvestAbility.SMELTER, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 2);

    // ============ DESH ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> DESH_SWORD = swordAbility("weapon/desh_sword", ModToolMaterials.DESH, 6, -2.4F, IWeaponAbility.STUN, 0);
    public static final RegistryObject<Item> DESH_PICKAXE = toolAbility("tools/desh_pickaxe", ModToolMaterials.DESH, BlockTags.MINEABLE_WITH_PICKAXE, 5.0F, -2.85F, IToolAreaAbility.HAMMER, 0, IToolAreaAbility.HAMMER_FLAT, 0, IToolAreaAbility.RECURSION, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 1);
    public static final RegistryObject<Item> DESH_AXE = toolAbility("tools/desh_axe", ModToolMaterials.DESH, BlockTags.MINEABLE_WITH_AXE, 7.5F, -2.85F, IToolAreaAbility.HAMMER, 0, IToolAreaAbility.HAMMER_FLAT, 0, IToolAreaAbility.RECURSION, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 1, IWeaponAbility.BEHEADER, 0);
    public static final RegistryObject<Item> DESH_SHOVEL = toolAbility("tools/desh_shovel", ModToolMaterials.DESH, BlockTags.MINEABLE_WITH_SHOVEL, 4.0F, -2.85F, IToolAreaAbility.HAMMER, 0, IToolAreaAbility.HAMMER_FLAT, 0, IToolAreaAbility.RECURSION, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 1);

    // ============ КОБАЛЬТОВЫЕ ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> COBALT_SWORD = swordAbility("weapon/cobalt_sword", ModToolMaterials.COBALT, 5, -2.4F);
    public static final RegistryObject<Item> COBALT_PICKAXE = toolAbility("tools/cobalt_pickaxe", ModToolMaterials.COBALT, BlockTags.MINEABLE_WITH_PICKAXE, 4.0F, -2.8F, IToolAreaAbility.RECURSION, 1, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 0);
    public static final RegistryObject<Item> COBALT_AXE = toolAbility("tools/cobalt_axe", ModToolMaterials.COBALT, BlockTags.MINEABLE_WITH_AXE, 6.0F, -3.0F, IToolAreaAbility.RECURSION, 1, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 0, IWeaponAbility.BEHEADER, 0);
    public static final RegistryObject<Item> COBALT_SHOVEL = toolAbility("tools/cobalt_shovel", ModToolMaterials.COBALT, BlockTags.MINEABLE_WITH_SHOVEL, 3.5F, -3.0F, IToolAreaAbility.RECURSION, 1, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 0);

    // ============ УКРАШЕННЫЕ КОБАЛЬТОВЫЕ ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> COBALT_DECORATED_SWORD = swordAbility("weapon/cobalt_decorated_sword", ModToolMaterials.COBALT_DECORATED, 8, -2.4F, IWeaponAbility.BOBBLE, 0);
    public static final RegistryObject<Item> COBALT_DECORATED_PICKAXE = toolAbility("tools/cobalt_decorated_pickaxe", ModToolMaterials.COBALT_DECORATED, BlockTags.MINEABLE_WITH_PICKAXE, 6.0F, -2.8F, IToolAreaAbility.RECURSION, 1, IToolAreaAbility.HAMMER, 0, IToolAreaAbility.HAMMER_FLAT, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 2);
    public static final RegistryObject<Item> COBALT_DECORATED_AXE = toolAbility("tools/cobalt_decorated_axe", ModToolMaterials.COBALT_DECORATED, BlockTags.MINEABLE_WITH_AXE, 8.0F, -3.0F, IToolAreaAbility.RECURSION, 1, IToolAreaAbility.HAMMER, 0, IToolAreaAbility.HAMMER_FLAT, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 2, IWeaponAbility.BEHEADER, 0);
    public static final RegistryObject<Item> COBALT_DECORATED_SHOVEL = toolAbility("tools/cobalt_decorated_shovel", ModToolMaterials.COBALT_DECORATED, BlockTags.MINEABLE_WITH_SHOVEL, 5.0F, -3.0F, IToolAreaAbility.RECURSION, 1, IToolAreaAbility.HAMMER, 0, IToolAreaAbility.HAMMER_FLAT, 0, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 2);

    // ============ ЗВЕЗДНО-МЕТАЛЛИЧЕСКИЕ ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> STARMETAL_SWORD = swordAbility("weapon/starmetal_sword", ModToolMaterials.STARMETAL, 12, -2.4F, IWeaponAbility.BEHEADER, 0, IWeaponAbility.STUN, 1, IWeaponAbility.BOBBLE, 0);
    public static final RegistryObject<Item> STARMETAL_PICKAXE = toolAbility("tools/starmetal_pickaxe", ModToolMaterials.STARMETAL, BlockTags.MINEABLE_WITH_PICKAXE, 8.0F, -2.8F, IToolAreaAbility.RECURSION, 3, IToolAreaAbility.HAMMER, 1, IToolAreaAbility.HAMMER_FLAT, 1, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 4, IWeaponAbility.STUN, 1);
    public static final RegistryObject<Item> STARMETAL_AXE = toolAbility("tools/starmetal_axe", ModToolMaterials.STARMETAL, BlockTags.MINEABLE_WITH_AXE, 12.0F, -3.0F, IToolAreaAbility.RECURSION, 3, IToolAreaAbility.HAMMER, 1, IToolAreaAbility.HAMMER_FLAT, 1, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 4, IWeaponAbility.BEHEADER, 0, IWeaponAbility.STUN, 1);
    public static final RegistryObject<Item> STARMETAL_SHOVEL = toolAbility("tools/starmetal_shovel", ModToolMaterials.STARMETAL, BlockTags.MINEABLE_WITH_SHOVEL, 7.0F, -3.0F, IToolAreaAbility.RECURSION, 3, IToolAreaAbility.HAMMER, 1, IToolAreaAbility.HAMMER_FLAT, 1, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 4, IWeaponAbility.STUN, 1);

    // ============ ШРАБИДИЕВЫЕ ИНСТРУМЕНТЫ ============
    public static final RegistryObject<Item> SCHRABIDIUM_SWORD = swordAbility("weapon/schrabidium_sword", ModToolMaterials.SCHRABIDIUM, 20, -2.4F, Rarity.EPIC, IWeaponAbility.RADIATION, 1, IWeaponAbility.VAMPIRE, 0);
    public static final RegistryObject<Item> SCHRABIDIUM_PICKAXE = toolAbilityExtended("tools/schrabidium_pickaxe", ModToolMaterials.SCHRABIDIUM, BlockTags.MINEABLE_WITH_PICKAXE, 20.0F, -2.8F, Rarity.EPIC, true, IWeaponAbility.RADIATION, 0, IToolAreaAbility.HAMMER, 1, IToolAreaAbility.HAMMER_FLAT, 1, IToolAreaAbility.RECURSION, 6, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 4, IToolHarvestAbility.SMELTER, 0, IToolHarvestAbility.SHREDDER, 0);
    public static final RegistryObject<Item> SCHRABIDIUM_AXE = toolAbilityExtended("tools/schrabidium_axe", ModToolMaterials.SCHRABIDIUM, BlockTags.MINEABLE_WITH_AXE, 25.0F, -3.0F, Rarity.EPIC, false, IWeaponAbility.RADIATION, 0, IToolAreaAbility.HAMMER, 1, IToolAreaAbility.HAMMER_FLAT, 1, IToolAreaAbility.RECURSION, 6, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 4, IToolHarvestAbility.SMELTER, 0, IToolHarvestAbility.SHREDDER, 0, IWeaponAbility.BEHEADER, 0);
    public static final RegistryObject<Item> SCHRABIDIUM_SHOVEL = toolAbilityExtended("tools/schrabidium_shovel", ModToolMaterials.SCHRABIDIUM, BlockTags.MINEABLE_WITH_SHOVEL, 15.0F, -3.0F, Rarity.EPIC, false, IWeaponAbility.RADIATION, 0, IToolAreaAbility.HAMMER, 1, IToolAreaAbility.HAMMER_FLAT, 1, IToolAreaAbility.RECURSION, 6, IToolHarvestAbility.SILK, 0, IToolHarvestAbility.LUCK, 4, IToolHarvestAbility.SMELTER, 0, IToolHarvestAbility.SHREDDER, 0);

    // ============ МОТЫГИ ============
    public static final RegistryObject<Item> TITANIUM_HOE = hoe("tools/titanium_hoe", ModToolMaterials.TITANIUM, 750);
    public static final RegistryObject<Item> STEEL_HOE = hoe("tools/steel_hoe", ModToolMaterials.STEEL, 500);
    public static final RegistryObject<Item> ADVANCED_ALLOY_HOE = hoe("tools/advanced_alloy_hoe", ModToolMaterials.ALLOY, 2000);
    public static final RegistryObject<Item> CMB_HOE = hoe("tools/cmb_steel_hoe", ModToolMaterials.CMB, 8500);
    public static final RegistryObject<Item> DESH_HOE = hoe("tools/desh_hoe", ModToolMaterials.DESH, 0);
    public static final RegistryObject<Item> COBALT_HOE = hoe("tools/cobalt_hoe", ModToolMaterials.COBALT, 750);
    public static final RegistryObject<Item> COBALT_DECORATED_HOE = hoe("tools/cobalt_decorated_hoe", ModToolMaterials.COBALT_DECORATED, 2500);
    public static final RegistryObject<Item> STARMETAL_HOE = hoe("tools/starmetal_hoe", ModToolMaterials.STARMETAL, 3000);
    public static final RegistryObject<Item> SCHRABIDIUM_HOE = register(WEAPON_TAB, "tools/schrabidium_hoe", () -> HoeSchrabidium.builder(ModToolMaterials.SCHRABIDIUM).attackDamage(-2).attackSpeed(-1.0F).rarity(Rarity.EPIC).build());
    public static final RegistryObject<Item> CROWBAR = swordAbility("weapon/crowbar", ModToolMaterials.STEEL, 5, -2.4F);

    // ============ ПРОЧИЕ ОРУЖИЯ ============
    public static final RegistryObject<Item> PIPE_LEAD = sword("weapon/pipe_lead", ModToolMaterials.PIPE_LEAD);
    public static final RegistryObject<Item> REER_GRAAR = sword("weapon/reer_graar", ModToolMaterials.TITANIUM);
    public static final RegistryObject<Item> STOPSIGN = weaponSpecial("weapon/stopsign", ModToolMaterials.ALLOY, 2000);
    public static final RegistryObject<Item> SOPSIGN = weaponSpecial("weapon/sopign", ModToolMaterials.ALLOY, 2000);
    public static final RegistryObject<Item> CHERNOBYLSIGN = weaponSpecial("weapon/chernobylsign", ModToolMaterials.ALLOY, 2000);
    public static final RegistryObject<Item> WRENCH = register(WEAPON_TAB, "weapon/wrench", () -> new ItemWrench(ModToolMaterials.STEEL, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAW = sword("weapon/saw", ModToolMaterials.SAW);
    public static final RegistryObject<Item> BAT = sword("weapon/bat", ModToolMaterials.BAT);
    public static final RegistryObject<Item> BAT_NAIL = sword("weapon/bat_nail", ModToolMaterials.BAT_NAIL);
    public static final RegistryObject<Item> GOLF_CLUB = sword("weapon/golf_club", ModToolMaterials.GOLF_CLUB);
    public static final RegistryObject<Item> PIPE_RUSTY = sword("weapon/pipe_rusty", ModToolMaterials.PIPE_RUSTY);
    public static final RegistryObject<Item> SCHRABIDIUM_HAMMER = weaponSpecial("weapon/schrabidium_hammer", ModToolMaterials.SCHRABIDIUM_HAMMER, 10000);
    public static final RegistryObject<Item> SHIMMER_SLEDGE = weaponSpecialZero("weapon/shimmer_sledge", ModToolMaterials.SHIMMER);
    public static final RegistryObject<Item> SHIMMER_AXE = weaponSpecialZero("weapon/shimmer_axe", ModToolMaterials.SHIMMER);
    public static final RegistryObject<Item> BOTTLE_OPENER = register(CONSUMABLE_TAB, "weapon/bottle_opener", () -> new WeaponSpecial(ModToolMaterials.BOTTLE_OPENER, new Item.Properties().stacksTo(1).durability(250)));
    public static final RegistryObject<Item> WRENCH_FLIPPED = weaponSpecialZero("weapon/wrench_flipped", ModToolMaterials.ELECTRIC);
    public static final RegistryObject<Item> MEMESPOON = weaponSpecial("weapon/memespoon", ModToolMaterials.STEEL, 500);
    public static final RegistryObject<Item> WOOD_GAVEL = weaponSpecial("weapon/wood_gavel", Tiers.WOOD, 59);
    public static final RegistryObject<Item> LEAD_GAVEL = weaponSpecial("weapon/lead_gavel", ModToolMaterials.STEEL, 500);
    public static final RegistryObject<Item> DIAMOND_GAVEL = weaponSpecial("weapon/diamond_gavel", Tiers.DIAMOND, 1561);
    public static final RegistryObject<Item> ULLAPOOL_CABER = weaponSpecial("weapon/ullapool_caber", ModToolMaterials.STEEL, 500);
    public static final RegistryObject<Item> REDSTONE_SWORD = weaponSpecial("weapon/redstone_sword", Tiers.STONE, 6);
    public static final RegistryObject<Item> BIG_SWORD = weaponSpecial("weapon/big_sword", Tiers.DIAMOND, 8);

    public static final RegistryObject<Item> BOLTGUN = register(WEAPON_TAB, "boltgun",
            () -> new ItemBoltgun(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "boltgun");

    public static final RegistryObject<Item> CRUCIBLE = register(WEAPON_TAB, "weapon/crucible",
            () -> new ItemCrucible(ModToolMaterials.CRUCIBLE, 100, 1.0F, new Item.Properties().durability(3)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> METEORITE_SWORD = register(WEAPON_TAB, "meteorite_sword",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 9.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_SEARED = register(WEAPON_TAB, "meteorite_sword_seared",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 10.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_REFORGED = register(WEAPON_TAB, "meteorite_sword_reforged",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 12.5F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_HARDENED = register(WEAPON_TAB, "meteorite_sword_hardened",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 15.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_ALLOYED = register(WEAPON_TAB, "meteorite_sword_alloyed",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 17.5F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_MACHINED = register(WEAPON_TAB, "meteorite_sword_machined",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 20.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_TREATED = register(WEAPON_TAB, "meteorite_sword_treated",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 22.5F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_ETCHED = register(WEAPON_TAB, "meteorite_sword_etched",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 25.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_BRED = register(WEAPON_TAB, "meteorite_sword_bred",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 30.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_IRRADIATED = register(WEAPON_TAB, "meteorite_sword_irradiated",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 35.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_FUSED = register(WEAPON_TAB, "meteorite_sword_fused",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 50.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> METEORITE_SWORD_BALEFUL = register(WEAPON_TAB, "meteorite_sword_baleful",
            () -> new ItemSwordMeteorite(new Item.Properties().durability(0), ModToolMaterials.METEORITE, 75.0F, 0.0F),
            ItemModelType.GENERATED, "meteorite_sword");

    public static final RegistryObject<Item> ELEC_SWORD = register(WEAPON_TAB, "elec_sword",
            () -> new ItemSwordAbilityPower(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), ModToolMaterials.ELEC,
                    12.5F, 0.0F, 500000, 1000, 100)
                    .addWeaponAbility(IWeaponAbility.STUN, 2),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ELEC_PICKAXE = register(WEAPON_TAB, "elec_pickaxe",
            () -> new ItemToolAbilityPower(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), ModToolMaterials.ELEC,
                    BlockTags.MINEABLE_WITH_PICKAXE, 6.0F, 0.0F, 500000, 1000, 100)
                    .addToolAreaAbility(IToolAreaAbility.HAMMER, 1)
                    .addToolAreaAbility(IToolAreaAbility.HAMMER_FLAT, 1)
                    .addToolAreaAbility(IToolAreaAbility.RECURSION, 2)
                    .addToolHarvestAbility(IToolHarvestAbility.SILK, 0)
                    .addToolHarvestAbility(IToolHarvestAbility.LUCK, 1),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ELEC_AXE = register(WEAPON_TAB, "elec_axe",
            () -> new ItemToolAbilityPower(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), ModToolMaterials.ELEC,
                    BlockTags.MINEABLE_WITH_AXE, 10.0F, 0.0F, 500000, 1000, 100)
                    .addToolAreaAbility(IToolAreaAbility.HAMMER, 1)
                    .addToolAreaAbility(IToolAreaAbility.HAMMER_FLAT, 1)
                    .addToolAreaAbility(IToolAreaAbility.RECURSION, 2)
                    .addToolHarvestAbility(IToolHarvestAbility.SILK, 0)
                    .addToolHarvestAbility(IToolHarvestAbility.LUCK, 1)
                    .addWeaponAbility(IWeaponAbility.CHAINSAW, 0)
                    .addWeaponAbility(IWeaponAbility.BEHEADER, 0)
                    .setShears(),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ELEC_SHOVEL = register(WEAPON_TAB, "elec_shovel",
            () -> new ItemToolAbilityPower(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), ModToolMaterials.ELEC,
                    BlockTags.MINEABLE_WITH_SHOVEL, 5.0F, 0.0F, 500000, 1000, 100)
                    .addToolAreaAbility(IToolAreaAbility.HAMMER, 1)
                    .addToolAreaAbility(IToolAreaAbility.HAMMER_FLAT, 1)
                    .addToolAreaAbility(IToolAreaAbility.RECURSION, 2)
                    .addToolHarvestAbility(IToolHarvestAbility.SILK, 0)
                    .addToolHarvestAbility(IToolHarvestAbility.LUCK, 1),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CENTRI_STICK = register(WEAPON_TAB, "centri_stick",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolHarvestAbility.CENTRIFUGE, 0);
                return new ItemToolAbility(3.0F, 0.0F, ModToolMaterials.ELEC,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        new Item.Properties().durability(50),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SMASHING_HAMMER = register(WEAPON_TAB, "smashing_hammer",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolHarvestAbility.SHREDDER, 0);
                return new ItemToolAbility(12.0F, -0.1F, ModToolMaterials.STEEL,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        new Item.Properties().durability(2500),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DWARVEN_PICKAXE = register(WEAPON_TAB, "dwarven_pickaxe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 0);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 0);
                return new ItemToolAbility(5.0F, -0.1F, ModToolMaterials.DWARVEN,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        new Item.Properties().durability(250),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRAX = register(WEAPON_TAB, "drax",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolHarvestAbility.SMELTER, 0);
                abilities.addAbility(IToolHarvestAbility.SHREDDER, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 1);
                abilities.addAbility(IToolAreaAbility.HAMMER, 1);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 1);
                abilities.addAbility(IToolAreaAbility.RECURSION, 2);
                return new ItemToolAbilityPower(new Item.Properties(), ModToolMaterials.ELEC,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        10.0F, -0.05F,
                        500000000, 100000, 5000,
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRAX_MK2 = register(WEAPON_TAB, "drax_mk2",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolHarvestAbility.SMELTER, 0);
                abilities.addAbility(IToolHarvestAbility.SHREDDER, 0);
                abilities.addAbility(IToolHarvestAbility.CENTRIFUGE, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 2);
                abilities.addAbility(IToolAreaAbility.HAMMER, 2);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 2);
                abilities.addAbility(IToolAreaAbility.RECURSION, 4);
                return new ItemToolAbilityPower(new Item.Properties(), ModToolMaterials.ELEC,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        15.0F, -0.05F,
                        1000000000, 250000, 7500,
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRAX_MK3 = register(WEAPON_TAB, "drax_mk3",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolHarvestAbility.SMELTER, 0);
                abilities.addAbility(IToolHarvestAbility.SHREDDER, 0);
                abilities.addAbility(IToolHarvestAbility.CENTRIFUGE, 0);
                abilities.addAbility(IToolHarvestAbility.CRYSTALLIZER, 0);
                abilities.addAbility(IToolHarvestAbility.SILK, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 3);
                abilities.addAbility(IToolAreaAbility.HAMMER, 3);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 3);
                abilities.addAbility(IToolAreaAbility.RECURSION, 5);
                return new ItemToolAbilityPower(new Item.Properties(), ModToolMaterials.ELEC,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        20.0F, -0.05F,
                        2500000000L, 500000, 10000,
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BISMUTH_PICKAXE = register(WEAPON_TAB, "bismuth_pickaxe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 1);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 1);
                abilities.addAbility(IToolAreaAbility.RECURSION, 1);
                abilities.addAbility(IToolHarvestAbility.SHREDDER, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 1);
                abilities.addAbility(IToolHarvestAbility.SILK, 0);
                abilities.addAbility(IWeaponAbility.STUN, 2);
                abilities.addAbility(IWeaponAbility.VAMPIRE, 0);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                return new ItemToolAbility(15.0F, 0.0F, ModToolMaterials.BISMUTH,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        new Item.Properties().durability(0),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BISMUTH_AXE = register(WEAPON_TAB, "bismuth_axe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 1);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 1);
                abilities.addAbility(IToolAreaAbility.RECURSION, 1);
                abilities.addAbility(IToolHarvestAbility.SHREDDER, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 1);
                abilities.addAbility(IToolHarvestAbility.SILK, 0);
                abilities.addAbility(IWeaponAbility.STUN, 3);
                abilities.addAbility(IWeaponAbility.VAMPIRE, 1);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                return new ItemToolAbility(25.0F, 0.0F, ModToolMaterials.BISMUTH,
                        BlockTags.MINEABLE_WITH_AXE,
                        new Item.Properties().durability(0),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> VOLCANIC_PICKAXE = register(WEAPON_TAB, "volcanic_pickaxe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 1);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 1);
                abilities.addAbility(IToolAreaAbility.RECURSION, 1);
                abilities.addAbility(IToolHarvestAbility.SMELTER, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 2);
                abilities.addAbility(IToolHarvestAbility.SILK, 0);
                abilities.addAbility(IWeaponAbility.FIRE, 0);
                abilities.addAbility(IWeaponAbility.VAMPIRE, 0);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                return new ItemToolAbility(15.0F, 0.0F, ModToolMaterials.VOLCANIC,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        new Item.Properties().durability(0),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> VOLCANIC_AXE = register(WEAPON_TAB, "volcanic_axe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 1);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 1);
                abilities.addAbility(IToolAreaAbility.RECURSION, 1);
                abilities.addAbility(IToolHarvestAbility.SMELTER, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 2);
                abilities.addAbility(IToolHarvestAbility.SILK, 0);
                abilities.addAbility(IWeaponAbility.FIRE, 1);
                abilities.addAbility(IWeaponAbility.VAMPIRE, 1);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                return new ItemToolAbility(25.0F, 0.0F, ModToolMaterials.VOLCANIC,
                        BlockTags.MINEABLE_WITH_AXE,
                        new Item.Properties().durability(0),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHLOROPHYTE_PICKAXE = register(WEAPON_TAB, "chlorophyte_pickaxe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 1);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 1);
                abilities.addAbility(IToolAreaAbility.RECURSION, 1);
                abilities.addAbility(IToolHarvestAbility.LUCK, 3);
                abilities.addAbility(IToolHarvestAbility.CENTRIFUGE, 0);
                abilities.addAbility(IToolHarvestAbility.MERCURY, 0);
                abilities.addAbility(IWeaponAbility.STUN, 3);
                abilities.addAbility(IWeaponAbility.VAMPIRE, 2);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                return new ItemToolAbility(20.0F, 0.0F, ModToolMaterials.CHLOROPHYTE,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        new Item.Properties().durability(0),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHLOROPHYTE_AXE = register(WEAPON_TAB, "chlorophyte_axe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 1);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 1);
                abilities.addAbility(IToolAreaAbility.RECURSION, 1);
                abilities.addAbility(IToolHarvestAbility.LUCK, 3);
                abilities.addAbility(IWeaponAbility.STUN, 4);
                abilities.addAbility(IWeaponAbility.VAMPIRE, 3);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                return new ItemToolAbility(50.0F, 0.0F, ModToolMaterials.CHLOROPHYTE,
                        BlockTags.MINEABLE_WITH_AXE,
                        new Item.Properties().durability(0),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MESE_PICKAXE = register(WEAPON_TAB, "mese_pickaxe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 2);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 2);
                abilities.addAbility(IToolAreaAbility.RECURSION, 2);
                abilities.addAbility(IToolHarvestAbility.CRYSTALLIZER, 0);
                abilities.addAbility(IToolHarvestAbility.SILK, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 5);
                abilities.addAbility(IToolAreaAbility.EXPLOSION, 3);
                abilities.addAbility(IWeaponAbility.STUN, 3);
                abilities.addAbility(IWeaponAbility.PHOSPHORUS, 0);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                return new ItemToolAbility(35.0F, 0.0F, ModToolMaterials.MESE,
                        BlockTags.MINEABLE_WITH_PICKAXE,
                        new Item.Properties().durability(0),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MESE_AXE = register(WEAPON_TAB, "mese_axe",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolAreaAbility.HAMMER, 2);
                abilities.addAbility(IToolAreaAbility.HAMMER_FLAT, 2);
                abilities.addAbility(IToolAreaAbility.RECURSION, 2);
                abilities.addAbility(IToolHarvestAbility.SILK, 0);
                abilities.addAbility(IToolHarvestAbility.LUCK, 5);
                abilities.addAbility(IToolAreaAbility.EXPLOSION, 3);
                abilities.addAbility(IWeaponAbility.STUN, 4);
                abilities.addAbility(IWeaponAbility.PHOSPHORUS, 1);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                return new ItemToolAbility(75.0F, 0.0F, ModToolMaterials.MESE,
                        BlockTags.MINEABLE_WITH_AXE,
                        new Item.Properties().durability(0),
                        abilities);
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHAINSAW = register(WEAPON_TAB, "weapon/chainsaw",
            () -> {
                AvailableAbilities abilities = new AvailableAbilities();
                abilities.addAbility(IToolHarvestAbility.SILK, 0);
                abilities.addAbility(IToolAreaAbility.RECURSION, 2);
                abilities.addAbility(IWeaponAbility.CHAINSAW, 1);
                abilities.addAbility(IWeaponAbility.BEHEADER, 0);
                ItemChainsaw chainsaw = new ItemChainsaw(
                        new Item.Properties().stacksTo(1),
                        ModToolMaterials.CHAINSAW,
                        25.0F, -0.05F,
                        5000, 1, 250,
                        Fluids.DIESEL,
                        Fluids.DIESEL_CRACK,
                        Fluids.KEROSENE,
                        Fluids.BIOFUEL,
                        Fluids.GASOLINE,
                        Fluids.GASOLINE_LEADED,
                        Fluids.PETROIL,
                        Fluids.PETROIL_LEADED,
                        Fluids.COALGAS,
                        Fluids.COALGAS_LEADED
                );
                chainsaw.availableAbilities = abilities;
                chainsaw.isShears = true;
                return chainsaw;
            },
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAND_DRILL = register(WEAPON_TAB, "hand_drill",
            () -> new ItemTooling(IToolable.ToolType.HAND_DRILL, 100),
            ItemModelType.GENERATED,
            "hand_drill");

    public static final RegistryObject<Item> HAND_DRILL_DESH = register(WEAPON_TAB, "hand_drill_desh",
            () -> new ItemTooling(IToolable.ToolType.HAND_DRILL, 0),
            ItemModelType.GENERATED,
            "hand_drill_desh");

    public static final RegistryObject<Item> REBAR_PLACER = register(CONTROL_TAB, "rebar_placer",
            () -> new ItemRebarPlacer(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "rebar_placer");

    public static final RegistryObject<Item> ROD_OF_DISCORD = register(WEAPON_TAB, "rod_of_discord",
            () -> new ItemDiscord(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "rod_of_discord");

    public static final RegistryObject<Item> MESE_GAVEL = register(WEAPON_TAB, "mese_gavel",
            () -> {
                ItemSwordAbility sword = new ItemSwordAbility(
                        ModToolMaterials.MESE_GAVEL,
                        250,
                        250.0F,
                        new Item.Properties().stacksTo(1).durability(0)
                );
                sword.addAbility(IWeaponAbility.PHOSPHORUS, 0)
                        .addAbility(IWeaponAbility.RADIATION, 2)
                        .addAbility(IWeaponAbility.STUN, 3)
                        .addAbility(IWeaponAbility.VAMPIRE, 4)
                        .addAbility(IWeaponAbility.BEHEADER, 0);
                return sword;
            },
            ItemModelType.GENERATED,
            "mese_gavel");

    // ============ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ============
    private static RegistryObject<Item> sword(String name, Tier tier) {
        return register(WEAPON_TAB, name, () -> new ModSword(tier, new Item.Properties().stacksTo(1)));
    }

    private static RegistryObject<Item> swordAbility(String name, Tier tier) {
        return register(WEAPON_TAB, name, () -> ItemSwordAbility.builder(tier).build());
    }

    private static RegistryObject<Item> swordAbility(String name, Tier tier, int damage, float speed) {
        return register(WEAPON_TAB, name, () -> ItemSwordAbility.builder(tier).attackDamage(damage).attackSpeed(speed).build());
    }

    private static RegistryObject<Item> swordAbility(String name, Tier tier, int damage, float speed, Object... abilities) {
        var builder = ItemSwordAbility.builder(tier).attackDamage(damage).attackSpeed(speed);
        for (int i = 0; i < abilities.length; i += 2) {
            builder.addAbility((IWeaponAbility) abilities[i], (int) abilities[i + 1]);
        }
        return register(WEAPON_TAB, name, builder::build);
    }

    private static RegistryObject<Item> swordAbility(String name, Tier tier, int damage, float speed, Rarity rarity, Object... abilities) {
        var builder = ItemSwordAbility.builder(tier).attackDamage(damage).attackSpeed(speed).rarity(rarity);
        for (int i = 0; i < abilities.length; i += 2) {
            builder.addAbility((IWeaponAbility) abilities[i], (int) abilities[i + 1]);
        }
        return register(WEAPON_TAB, name, builder::build);
    }

    private static RegistryObject<Item> weaponSpecial(String name, Tier tier, int durability) {
        return register(WEAPON_TAB, name, () -> new WeaponSpecial(tier, new Item.Properties().stacksTo(1).durability(durability)));
    }

    private static RegistryObject<Item> weaponSpecialZero(String name, Tier tier) {
        return register(WEAPON_TAB, name, () -> new WeaponSpecial(tier, new Item.Properties().stacksTo(1).durability(0)));
    }

    private static RegistryObject<Item> toolAbility(String name, Tier tier, TagKey<Block> tag, float damage, float speed, Object... abilities) {
        var builder = ItemToolAbility.builder(tier, tag).attackDamage(damage).attackSpeed(speed);
        for (int i = 0; i < abilities.length; i += 2) {
            builder.addAbility((IBaseAbility) abilities[i], (int) abilities[i + 1]);
        }
        return register(WEAPON_TAB, name, builder::build);
    }

    private static RegistryObject<Item> toolAbilityExtended(String name, Tier tier, TagKey<Block> tag, float damage, float speed, Rarity rarity, boolean rockBreaker, Object... abilities) {
        var builder = ItemToolAbility.builder(tier, tag).attackDamage(damage).attackSpeed(speed).rarity(rarity);
        for (int i = 0; i < abilities.length; i += 2) {
            builder.addAbility((IBaseAbility) abilities[i], (int) abilities[i + 1]);
        }
        if (rockBreaker) {
            builder.rockBreaker();
        }
        return register(WEAPON_TAB, name, builder::build);
    }

    private static RegistryObject<Item> hoe(String name, Tier tier, int durability) {
        return register(WEAPON_TAB, name, () -> new ModHoe(tier, -2, -1.0F, durability > 0 ? new Item.Properties().durability(durability) : new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        TOOLS.register(eventBus);
    }

    public static RegistryObject<Item> register(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier, ItemModelType modelType, Object... modelData) {
        RegistryObject<Item> item = ItemRegistryHelper.register(tab, name, supplier);
        TOOLS_MODELS.put(item, modelType);
        if (modelData.length > 0) {
            TOOLS_MODEL_DATA.put(item, modelData);
        }
        return item;
    }

    public static void register() {
        // Регистрируем кастомные модели для предметов
        registerItemModel(ModToolItems.SHIMMER_SLEDGE.get(), ResLocation(MODID, "weapon/shimmer_sledge"));
        registerItemModel(ModToolItems.SHIMMER_AXE.get(), ResLocation(MODID, "weapon/shimmer_axe"));
        registerItemModel(ModToolItems.STOPSIGN.get(), ResLocation(MODID, "weapon/stopsign"));
        registerItemModel(ModToolItems.SOPSIGN.get(), ResLocation(MODID, "weapon/sopsign"));
        registerItemModel(ModToolItems.CHERNOBYLSIGN.get(), ResLocation(MODID, "weapon/chernobylsign"));
        registerItemModel(ModToolItems.WOOD_GAVEL.get(), ResLocation(MODID, "weapon/wood_gavel"));
        registerItemModel(ModToolItems.LEAD_GAVEL.get(), ResLocation(MODID, "weapon/lead_gavel"));
        registerItemModel(ModToolItems.DIAMOND_GAVEL.get(), ResLocation(MODID, "weapon/diamond_gavel"));
        registerItemModel(ModToolItems.REDSTONE_SWORD.get(), ResLocation(MODID, "weapon/redstone_sword"));
        registerItemModel(ModToolItems.BIG_SWORD.get(), ResLocation(MODID, "weapon/big_sword"));
        registerItemModel(ModToolItems.CHAINSAW.get(), ResLocation(MODID, "weapon/chainsaw"));
        registerItemModel(ModToolItems.BOLTGUN.get(), ResLocation(MODID, "weapon/boltgun"));
    }

    private static void registerItemModel(net.minecraft.world.item.Item item, ResourceLocation modelLocation) {
        ItemProperties.register(item, ResLocation("model"), (stack, level, entity, seed) -> 0);
    }
}