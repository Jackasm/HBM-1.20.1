package com.hbm.inventory.fluid;

import com.hbm.handler.pollution.PollutionType;
import com.hbm.inventory.fluid.trait.*;
import com.hbm.inventory.fluid.trait.FT_Combustible.FuelGrade;
import com.hbm.potion.HbmPotion;
import com.hbm.util.HBMEnums;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingType;
import com.hbm.inventory.fluid.trait.FT_Coolable.CoolingType;
import com.hbm.inventory.fluid.trait.FT_Toxin.*;
import com.hbm.util.ArmorRegistry.HazardClass;

import com.hbm.util.ModDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.hbm.inventory.fluid.trait.FluidTraits.*;
import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class Fluids {

    // Deferred Register для регистрации FluidType
    public static final DeferredRegister<FluidType> HBM_FLUIDS =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MODID);

    // Карты для быстрого доступа
    private static final Map<String, RegistryObject<FluidTypeHBM>> NAME_MAP = new LinkedHashMap<>();
    public static final Map<Integer, FluidTypeHBM> ID_MAP = new HashMap<>();
    private static int nextId = 0;

    // === ОСНОВНЫЕ ЖИДКОСТИ ===
    public static final RegistryObject<FluidTypeHBM> NONE = register("none",
            new FluidTypeHBM("NONE", 0x888888, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/none.png")));

    public static final RegistryObject<FluidTypeHBM> AIR = register("air",
            new FluidTypeHBM("AIR", 0xE7EAEB, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/air.png")).addTraits(GASEOUS));

    public static final RegistryObject<FluidTypeHBM> WATER = register("water",
            new FluidTypeHBM("WATER", 0x3333FF, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/water.png"))
                    .setTemperature(20).addTraits(LIQUID, UNSIPHONABLE));

    public static final RegistryObject<FluidTypeHBM> STEAM = register("steam",
            new FluidTypeHBM("STEAM", 0xE5E5E5, 3, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/steam.png"))
                    .setTemperature(100).addTraits(GASEOUS, UNSIPHONABLE));

    public static final RegistryObject<FluidTypeHBM> HOTSTEAM = register("hotsteam",
            new FluidTypeHBM("HOTSTEAM", 0xE7D6D6, 4, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/hotsteam.png"))
                    .setTemperature(300).addTraits(GASEOUS, UNSIPHONABLE));

    public static final RegistryObject<FluidTypeHBM> SUPERHOTSTEAM = register("superhotsteam",
            new FluidTypeHBM("SUPERHOTSTEAM", 0xE7B7B7, 4, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/superhotsteam.png"))
                    .setTemperature(450).addTraits(GASEOUS, UNSIPHONABLE));

    public static final RegistryObject<FluidTypeHBM> ULTRAHOTSTEAM = register("ultrahotsteam",
            new FluidTypeHBM("ULTRAHOTSTEAM", 0xE39393, 4, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/ultrahotsteam.png"))
                    .setTemperature(600).addTraits(GASEOUS, UNSIPHONABLE));

    public static final RegistryObject<FluidTypeHBM> COOLANT = register("coolant",
            new FluidTypeHBM("COOLANT", 0xD8FCFF, 1, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/coolant.png"))
                    .setTemperature(20).addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> COOLANT_HOT = register("coolant_hot",
            new FluidTypeHBM("COOLANT_HOT", 0x99525E, 1, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/coolant_hot.png"))
                    .setTemperature(600).addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> LAVA = register("lava",
            new FluidTypeHBM("LAVA", 0xFF3300, 4, 0, 0, HBMEnums.EnumSymbol.NOWATER,
                    ResLocation(MODID, "textures/gui/fluids/lava.png"))
                    .setTemperature(1200).addTraits(LIQUID, VISCOUS));

    // === ГАЗЫ И КРИОГЕННЫЕ ЖИДКОСТИ ===
    public static final RegistryObject<FluidTypeHBM> DEUTERIUM = register("deuterium",
            new FluidTypeHBM("DEUTERIUM", 0x0000FF, 3, 4, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/deuterium.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(5_000), new FT_Combustible(FuelGrade.HIGH, 10_000), GASEOUS));

    public static final RegistryObject<FluidTypeHBM> TRITIUM = register("tritium",
            new FluidTypeHBM("TRITIUM", 0x000099, 3, 4, 0, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/tritium.png"))
                    .setTemperature(20)
                    .addTraits(new FT_Flammable(5_000), new FT_Combustible(FuelGrade.HIGH, 10_000), GASEOUS, new FT_VentRadiation(0.001F)));

    public static final RegistryObject<FluidTypeHBM> HYDROGEN = register("hydrogen",
            new FluidTypeHBM("HYDROGEN", 0x4286F4, 3, 4, 0, HBMEnums.EnumSymbol.CROYGENIC,
                    ResLocation(MODID, "textures/gui/fluids/hydrogen.png"))
                    .setTemperature(-260).addTraits(new FT_Flammable(5_000), new FT_Combustible(FuelGrade.HIGH, 10_000), LIQUID, EVAP));

    public static final RegistryObject<FluidTypeHBM> OXYGEN = register("oxygen",
            new FluidTypeHBM("OXYGEN", 0x98BDF9, 3, 0, 0, HBMEnums.EnumSymbol.CROYGENIC,
                    ResLocation(MODID, "textures/gui/fluids/oxygen.png"))
                    .setTemperature(-100).addTraits(LIQUID, EVAP));

    public static final RegistryObject<FluidTypeHBM> HELIUM3 = register("helium3",
            new FluidTypeHBM("HELIUM3", 0xFCF0C4, 0, 0, 0, HBMEnums.EnumSymbol.ASPHYXIANT,
                    ResLocation(MODID, "textures/gui/fluids/helium3.png"))
                    .setTemperature(20).addTraits(GASEOUS));

    public static final RegistryObject<FluidTypeHBM> SMOKE = register("smoke",
            new FluidTypeHBM("SMOKE", 0x808080, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/smoke.png"))
                    .setTemperature(20).addTraits(GASEOUS, NOID, NOCON));

    public static final RegistryObject<FluidTypeHBM> SMOKE_LEADED = register("smoke_leaded",
            new FluidTypeHBM("SMOKE", 0x808080, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/smoke_leaded.png"))
                    .setTemperature(20).addTraits(GASEOUS, NOID, NOCON));

    public static final RegistryObject<FluidTypeHBM> SMOKE_POISON = register("smoke_poison",
            new FluidTypeHBM("SMOKE", 0x808080, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/smoke_poison.png"))
                    .setTemperature(20).addTraits(GASEOUS, NOID, NOCON));

    public static final RegistryObject<FluidTypeHBM> HELIUM4 = register("helium4",
            new FluidTypeHBM("HELIUM4", 0xE54B0A, 0, 0, 0, HBMEnums.EnumSymbol.ASPHYXIANT,
                    ResLocation(MODID, "textures/gui/fluids/helium4.png"))
                    .setTemperature(20).addTraits(GASEOUS));

    public static final RegistryObject<FluidTypeHBM> XENON = register("xenon",
            new FluidTypeHBM("XENON", 0xBA45E8, 0, 0, 0, HBMEnums.EnumSymbol.ASPHYXIANT,
                    ResLocation(MODID, "textures/gui/fluids/xenon.png"))
                    .setTemperature(20).addTraits(GASEOUS));

    // === НЕФТЬ И ТОПЛИВА ===
    public static final RegistryObject<FluidTypeHBM> OIL = register("oil",
            new FluidTypeHBM("OIL", 0x020202, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/oil.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(10_000), LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> OIL_DS = register("oil_ds",
            new FluidTypeHBM("OIL_DS", 0x121212, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/oil_ds.png"))
                    .setTemperature(20).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> HOTOIL = register("hotoil",
            new FluidTypeHBM("HOTOIL", 0x300900, 2, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/hotoil.png"))
                    .setTemperature(350).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> HOTOIL_DS = register("hotoil_ds",
            new FluidTypeHBM("HOTOIL_DS", 0x3F180F, 2, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/hotoil_ds.png"))
                    .setTemperature(350).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> CRACKOIL = register("crackoil",
            new FluidTypeHBM("CRACKOIL", 0x020202, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/crackoil.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(10_000), LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> CRACKOIL_DS = register("crackoil_ds",
            new FluidTypeHBM("CRACKOIL_DS", 0x2A1C11, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/crackoil_ds.png"))
                    .setTemperature(20).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> HOTCRACKOIL = register("hotcrackoil",
            new FluidTypeHBM("HOTCRACKOIL", 0x300900, 2, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/hotcrackoil.png"))
                    .setTemperature(350).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> HOTCRACKOIL_DS = register("hotcrackoil_ds",
            new FluidTypeHBM("HOTCRACKOIL_DS", 0x3A1A28, 2, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/hotcrackoil_ds.png"))
                    .setTemperature(350).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> HEAVYOIL = register("heavyoil",
            new FluidTypeHBM("HEAVYOIL", 0x141312, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/heavyoil.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(50_000), new FT_Combustible(FuelGrade.LOW, 25_000), LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> HEAVYOIL_VACUUM = register("heavyoil_vacuum",
            new FluidTypeHBM("HEAVYOIL_VACUUM", 0x131214, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/heavyoil_vacuum.png"))
                    .setTemperature(20).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> BITUMEN = register("bitumen",
            new FluidTypeHBM("BITUMEN", 0x1F2426, 2, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/bitumen.png"))
                    .setTemperature(20).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> SMEAR = register("smear",
            new FluidTypeHBM("SMEAR", 0x190F01, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/smear.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(50_000), LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> HEATINGOIL = register("heatingoil",
            new FluidTypeHBM("HEATINGOIL", 0x211806, 2, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/heatingoil.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(150_000), new FT_Combustible(FuelGrade.LOW, 100_000), LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> HEATINGOIL_VACUUM = register("heatingoil_vacuum",
            new FluidTypeHBM("HEATINGOIL_VACUUM", 0x211D06, 2, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/heatingoil_vacuum.png"))
                    .setTemperature(20).addTraits(LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> RECLAIMED = register("reclaimed",
            new FluidTypeHBM("RECLAIMED", 0x332B22, 2, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/reclaimed.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(100_000), new FT_Combustible(FuelGrade.LOW, 200_000), LIQUID, VISCOUS, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> PETROIL = register("petroil",
            new FluidTypeHBM("PETROIL", 0x44413D, 1, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/petroil.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(125_000), new FT_Combustible(FuelGrade.MEDIUM, 300_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> PETROIL_LEADED = register("petroil_leaded",
            new FluidTypeHBM("PETROIL_LEADED", 0x44413D, 1, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/petroil_leaded.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(125_000), new FT_Combustible(FuelGrade.MEDIUM, 450_000), LIQUID, P_FUEL_LEADED));

    public static final RegistryObject<FluidTypeHBM> LUBRICANT = register("lubricant",
            new FluidTypeHBM("LUBRICANT", 0x606060, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/lubricant.png"))
                    .setTemperature(20).addTraits(LIQUID, P_OIL));

    // === НАФТА И ПРОДУКТЫ ПЕРЕРАБОТКИ ===
    public static final RegistryObject<FluidTypeHBM> NAPHTHA = register("naphtha",
            new FluidTypeHBM("NAPHTHA", 0x595744, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/naphtha.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(125_000), new FT_Combustible(FuelGrade.MEDIUM, 200_000), LIQUID, VISCOUS, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> NAPALM = register("napalm",
            new FluidTypeHBM("NAPALM", 0x9D4C33, 2, 4, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/napalm.png"))
                    .setTemperature(20));

    public static final RegistryObject<FluidTypeHBM> NAPHTHA_DS = register("naphtha_ds",
            new FluidTypeHBM("NAPHTHA_DS", 0x63614E, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/naphtha_ds.png"))
                    .setTemperature(20).addTraits(LIQUID, VISCOUS, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> NAPHTHA_CRACK = register("naphtha_crack",
            new FluidTypeHBM("NAPHTHA_CRACK", 0x595744, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/naphtha_crack.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(125_000), new FT_Combustible(FuelGrade.MEDIUM, 200_000), LIQUID, VISCOUS, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> NAPHTHA_COKER = register("naphtha_coker",
            new FluidTypeHBM("NAPHTHA_COKER", 0x495944, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/naphtha_coker.png"))
                    .setTemperature(20).addTraits(LIQUID, VISCOUS, P_OIL));

    // === ТОПЛИВА И ГОРЮЧИЕ ===
    public static final RegistryObject<FluidTypeHBM> DIESEL = register("diesel",
            new FluidTypeHBM("DIESEL", 0xF2EED5, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/diesel.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(200_000), new FT_Combustible(FuelGrade.HIGH, 500_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> DIESEL_CRACK = register("diesel_crack",
            new FluidTypeHBM("DIESEL_CRACK", 0xF2EED5, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/diesel_crack.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(200_000), new FT_Combustible(FuelGrade.HIGH, 450_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> DIESEL_REFORM = register("diesel_reform",
            new FluidTypeHBM("DIESEL_REFORM", 0xCDC3C6, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/diesel_reform.png"))
                    .setTemperature(20).addTraits(LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> DIESEL_CRACK_REFORM = register("diesel_crack_reform",
            new FluidTypeHBM("DIESEL_CRACK_REFORM", 0xCDC3CC, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/diesel_crack_reform.png"))
                    .setTemperature(20).addTraits(LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> LIGHTOIL = register("lightoil",
            new FluidTypeHBM("LIGHTOIL", 0x8C7451, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/lightoil.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(200_000), new FT_Combustible(FuelGrade.MEDIUM, 500_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> LIGHTOIL_DS = register("lightoil_ds",
            new FluidTypeHBM("LIGHTOIL_DS", 0x63543E, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/lightoil_ds.png"))
                    .setTemperature(20).addTraits(LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> LIGHTOIL_CRACK = register("lightoil_crack",
            new FluidTypeHBM("LIGHTOIL_CRACK", 0x8C7451, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/lightoil_crack.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(200_000), new FT_Combustible(FuelGrade.MEDIUM, 500_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> LIGHTOIL_VACUUM = register("lightoil_vacuum",
            new FluidTypeHBM("LIGHTOIL_VACUUM", 0x8C8851, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/lightoil_vacuum.png"))
                    .setTemperature(20).addTraits(LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> KEROSENE = register("kerosene",
            new FluidTypeHBM("KEROSENE", 0xFFA5D2, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/kerosene.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(300_000), new FT_Combustible(FuelGrade.AERO, 1_250_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> KEROSENE_REFORM = register("kerosene_reform",
            new FluidTypeHBM("KEROSENE_REFORM", 0xFFA5F3, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/kerosene_reform.png"))
                    .setTemperature(20).addTraits(LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> GAS = register("gas",
            new FluidTypeHBM("GAS", 0xFFFEED, 1, 4, 1, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/gas.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(10_000), GASEOUS, P_GAS));

    public static final RegistryObject<FluidTypeHBM> GAS_COKER = register("gas_coker",
            new FluidTypeHBM("GAS_COKER", 0xDEF4CA, 1, 4, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/gas_coker.png"))
                    .setTemperature(20).addTraits(GASEOUS, P_GAS));

    public static final RegistryObject<FluidTypeHBM> PETROLEUM = register("petroleum",
            new FluidTypeHBM("PETROLEUM", 0x7CB7C9, 1, 4, 1, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/petroleum.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(25_000), GASEOUS, P_GAS));

    public static final RegistryObject<FluidTypeHBM> LPG = register("lpg",
            new FluidTypeHBM("LPG", 0x4747EA, 1, 3, 1, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/lpg.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(200_000), new FT_Combustible(FuelGrade.HIGH, 400_000), LIQUID, P_LIQUID_GAS));

    public static final RegistryObject<FluidTypeHBM> BIOGAS = register("biogas",
            new FluidTypeHBM("BIOGAS", 0xBFD37C, 1, 4, 1, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/biogas.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(25_000), GASEOUS, P_GAS));

    public static final RegistryObject<FluidTypeHBM> BIOFUEL = register("biofuel",
            new FluidTypeHBM("BIOFUEL", 0xEEF274, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/biofuel.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(150_000), new FT_Combustible(FuelGrade.HIGH, 400_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> GASOLINE = register("gasoline",
            new FluidTypeHBM("GASOLINE", 0x445772, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/gasoline.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(400_000), new FT_Combustible(FuelGrade.HIGH, 1_000_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> GASOLINE_LEADED = register("gasoline_leaded",
            new FluidTypeHBM("GASOLINE_LEADED", 0x445772, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/gasoline_leaded.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(400_000), new FT_Combustible(FuelGrade.HIGH, 1_500_000), LIQUID, P_FUEL_LEADED));

    public static final RegistryObject<FluidTypeHBM> COALGAS = register("coalgas",
            new FluidTypeHBM("COALGAS", 0x445772, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/coalgas.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(75_000), new FT_Combustible(FuelGrade.MEDIUM, 150_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> COALGAS_LEADED = register("coalgas_leaded",
            new FluidTypeHBM("COALGAS_LEADED", 0x445772, 1, 2, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/coalgas_leaded.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(75_000), new FT_Combustible(FuelGrade.MEDIUM, 250_000), LIQUID, P_FUEL_LEADED));

    // === КИСЛОТЫ И ХИМИКАТЫ ===
    public static final RegistryObject<FluidTypeHBM> SULFURIC_ACID = register("sulfuric_acid",
            new FluidTypeHBM("SULFURIC_ACID", 0xB0AA64, 3, 0, 2, HBMEnums.EnumSymbol.ACID,
                    ResLocation(MODID, "textures/gui/fluids/sulfuric_acid.png"))
                    .setTemperature(20).addTraits(new FT_Corrosive(50), LIQUID));

    public static final RegistryObject<FluidTypeHBM> NITRIC_ACID = register("nitric_acid",
            new FluidTypeHBM("NITRIC_ACID", 0xBB7A1E, 3, 0, 2, HBMEnums.EnumSymbol.OXIDIZER,
                    ResLocation(MODID, "textures/gui/fluids/nitric_acid.png"))
                    .setTemperature(20).addTraits(LIQUID, new FT_Corrosive(60), new FT_Polluting().release(PollutionType.POISON, POISON_EXTREME)));

    public static final RegistryObject<FluidTypeHBM> SOLVENT = register("solvent",
            new FluidTypeHBM("SOLVENT", 0xE4E3EF, 2, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/solvent.png"))
                    .setTemperature(20).addTraits(LIQUID, new FT_Corrosive(30)));

    public static final RegistryObject<FluidTypeHBM> RADIOSOLVENT = register("radiosolvent",
            new FluidTypeHBM("RADIOSOLVENT", 0xA4D7DD, 3, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/radiosolvent.png"))
                    .setTemperature(20).addTraits(LIQUID, new FT_Corrosive(50)));

    public static final RegistryObject<FluidTypeHBM> PEROXIDE = register("peroxide",
            new FluidTypeHBM("PEROXIDE", 0xFFF7AA, 3, 0, 3, HBMEnums.EnumSymbol.OXIDIZER,
                    ResLocation(MODID, "textures/gui/fluids/peroxide.png"))
                    .setTemperature(20).addTraits(new FT_Corrosive(40), LIQUID));

    // === ЯДЕРНЫЕ МАТЕРИАЛЫ ===
    public static final RegistryObject<FluidTypeHBM> UF6 = register("uf6",
            new FluidTypeHBM("UF6", 0xD1CEBE, 4, 0, 2, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/uf6.png"))
                    .setTemperature(20).addTraits(new FT_VentRadiation(0.2F), new FT_Corrosive(15), GASEOUS));

    public static final RegistryObject<FluidTypeHBM> PUF6 = register("puf6",
            new FluidTypeHBM("PUF6", 0x4C4C4C, 4, 0, 4, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/puf6.png"))
                    .setTemperature(20).addTraits(new FT_VentRadiation(0.1F), new FT_Corrosive(15), GASEOUS));

    public static final RegistryObject<FluidTypeHBM> SAS3 = register("sas3",
            new FluidTypeHBM("SAS3", 0x4FFFFC, 5, 0, 4, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/sas3.png"))
                    .setTemperature(20).addTraits(new FT_VentRadiation(1F), new FT_Corrosive(30), LIQUID));

    public static final RegistryObject<FluidTypeHBM> SCHRABIDIC = register("schrabidic",
            new FluidTypeHBM("SCHRABIDIC", 0x006B6B, 5, 0, 5, HBMEnums.EnumSymbol.ACID,
                    ResLocation(MODID, "textures/gui/fluids/schrabidic.png"))
                    .setTemperature(20).addTraits(new FT_VentRadiation(1F), new FT_Corrosive(75), new FT_Poison(true, 2), LIQUID));

    public static final RegistryObject<FluidTypeHBM> THORIUM_SALT = register("thorium_salt",
            new FluidTypeHBM("THORIUM_SALT", 0x7A5542, 2, 0, 3, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/thorium_salt.png"))
                    .setTemperature(800).addTraits(LIQUID, VISCOUS, new FT_Corrosive(65)));

    public static final RegistryObject<FluidTypeHBM> THORIUM_SALT_HOT = register("thorium_salt_hot",
            new FluidTypeHBM("THORIUM_SALT_HOT", 0x3E3627, 2, 0, 3, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/thorium_salt_hot.png"))
                    .setTemperature(1600).addTraits(LIQUID, VISCOUS, new FT_Corrosive(65)));

    public static final RegistryObject<FluidTypeHBM> THORIUM_SALT_DEPLETED = register("thorium_salt_depleted",
            new FluidTypeHBM("THORIUM_SALT_DEPLETED", 0x302D1C, 2, 0, 3, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/thorium_salt_depleted.png"))
                    .setTemperature(800).addTraits(LIQUID, VISCOUS, new FT_Corrosive(65)));

    // === СПЕЦИАЛЬНЫЕ И УНИКАЛЬНЫЕ ЖИДКОСТИ ===
    public static final RegistryObject<FluidTypeHBM> NITAN = register("nitan",
            new FluidTypeHBM("NITAN", 0x8018AD, 2, 4, 1, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/nitan.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(2_000_000), new FT_Combustible(FuelGrade.HIGH, 5_000_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> BALEFIRE = register("balefire",
            new FluidTypeHBM("BALEFIRE", 0x28E02E, 4, 4, 3, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/balefire.png"))
                    .setTemperature(1500).addTraits(new FT_Corrosive(50), new FT_Flammable(1_000_000), new FT_Combustible(FuelGrade.HIGH, 2_500_000), LIQUID, VISCOUS, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> PAIN = register("pain",
            new FluidTypeHBM("PAIN", 0x938541, 2, 0, 1, HBMEnums.EnumSymbol.ACID,
                    ResLocation(MODID, "textures/gui/fluids/pain.png"))
                    .setTemperature(300).addTraits(new FT_Corrosive(30), new FT_Poison(true, 2), LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> DEATH = register("death",
            new FluidTypeHBM("DEATH", 0x717A88, 2, 0, 1, HBMEnums.EnumSymbol.ACID,
                    ResLocation(MODID, "textures/gui/fluids/death.png"))
                    .setTemperature(300).addTraits(new FT_Corrosive(80), new FT_Poison(true, 4), LEADCON, LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> WATZ = register("watz",
            new FluidTypeHBM("WATZ", 0x86653E, 4, 0, 3, HBMEnums.EnumSymbol.ACID,
                    ResLocation(MODID, "textures/gui/fluids/watz.png"))
                    .setTemperature(20).addTraits(new FT_Corrosive(60), new FT_VentRadiation(0.1F), LIQUID, VISCOUS, new FT_Polluting().release(PollutionType.POISON, POISON_EXTREME)));

    public static final RegistryObject<FluidTypeHBM> CRYOGEL = register("cryogel",
            new FluidTypeHBM("CRYOGEL", 0x32FFFF, 2, 0, 0, HBMEnums.EnumSymbol.CROYGENIC,
                    ResLocation(MODID, "textures/gui/fluids/cryogel.png"))
                    .setTemperature(-170).addTraits(LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> MERCURY = register("mercury",
            new FluidTypeHBM("MERCURY", 0x808080, 2, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/mercury.png"))
                    .setTemperature(20).addTraits(LIQUID, new FT_Poison(false, 2)));

    public static final RegistryObject<FluidTypeHBM> SPENTSTEAM = register("spentsteam",
            new FluidTypeHBM("SPENTSTEAM", 0x445772, 2, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/spentsteam.png"))
                    .setTemperature(20).addTraits(NOCON, GASEOUS));

    // === ПЛАЗМА ===
    public static final RegistryObject<FluidTypeHBM> PLASMA_DT = register("plasma_dt",
            new FluidTypeHBM("PLASMA_DT", 0xF7AFDE, 0, 4, 0, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/plasma_dt.png"))
                    .setTemperature(3250).addTraits(NOCON, NOID, PLASMA));

    public static final RegistryObject<FluidTypeHBM> PLASMA_HD = register("plasma_hd",
            new FluidTypeHBM("PLASMA_HD", 0xF0ADF4, 0, 4, 0, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/plasma_hd.png"))
                    .setTemperature(2500).addTraits(NOCON, NOID, PLASMA));

    public static final RegistryObject<FluidTypeHBM> PLASMA_HT = register("plasma_ht",
            new FluidTypeHBM("PLASMA_HT", 0xD1ABF2, 0, 4, 0, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/plasma_ht.png"))
                    .setTemperature(3000).addTraits(NOCON, NOID, PLASMA));

    public static final RegistryObject<FluidTypeHBM> PLASMA_DH3 = register("plasma_dh3",
            new FluidTypeHBM("PLASMA_DH3", 0xFF83AA, 0, 4, 0, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/plasma_dh3.png"))
                    .setTemperature(3480).addTraits(NOCON, NOID, PLASMA));

    public static final RegistryObject<FluidTypeHBM> PLASMA_XM = register("plasma_xm",
            new FluidTypeHBM("PLASMA_XM", 0xC6A5FF, 0, 4, 1, HBMEnums.EnumSymbol.RADIATION,
                    ResLocation(MODID, "textures/gui/fluids/plasma_xm.png"))
                    .setTemperature(4250).addTraits(NOCON, NOID, PLASMA));

    public static final RegistryObject<FluidTypeHBM> PLASMA_BF = register("plasma_bf",
            new FluidTypeHBM("PLASMA_BF", 0xA7F1A3, 4, 5, 4, HBMEnums.EnumSymbol.ANTIMATTER,
                    ResLocation(MODID, "textures/gui/fluids/plasma_bf.png"))
                    .setTemperature(8500).addTraits(NOCON, NOID, PLASMA));

    // === ОСТАЛЬНЫЕ ВАЖНЫЕ ЖИДКОСТИ ===
    public static final RegistryObject<FluidTypeHBM> CARBONDIOXIDE = register("carbondioxide",
            new FluidTypeHBM("CARBONDIOXIDE", 0x404040, 3, 0, 0, HBMEnums.EnumSymbol.ASPHYXIANT,
                    ResLocation(MODID, "textures/gui/fluids/carbondioxide.png"))
                    .setTemperature(20).addTraits(GASEOUS, new FT_Polluting().release(PollutionType.POISON, POISON_MINOR)));

    public static final RegistryObject<FluidTypeHBM> HEAVYWATER = register("heavywater",
            new FluidTypeHBM("HEAVYWATER", 0x00A0B0, 1, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/heavywater.png"))
                    .setTemperature(20).addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> HEAVYWATER_HOT = register("heavywater_hot",
            new FluidTypeHBM("HEAVYWATER_HOT", 0x4D007B, 1, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/heavywater_hot.png"))
                    .setTemperature(600).addTraits(LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> ETHANOL = register("ethanol",
            new FluidTypeHBM("ETHANOL", 0xE0FFFF, 2, 3, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/ethanol.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(75_000), new FT_Combustible(FuelGrade.HIGH, 200_000), LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> COALOIL = register("coaloil",
            new FluidTypeHBM("COALOIL", 0x020202, 2, 1, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/coaloil.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(10_000), LIQUID, VISCOUS, P_OIL));

    public static final RegistryObject<FluidTypeHBM> AROMATICS = register("aromatics",
            new FluidTypeHBM("AROMATICS", 0x68A09A, 1, 4, 1, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/aromatics.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(25_000), LIQUID, VISCOUS, P_GAS));

    public static final RegistryObject<FluidTypeHBM> UNSATURATEDS = register("unsaturateds",
            new FluidTypeHBM("UNSATURATEDS", 0x628FAE, 1, 4, 1, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/unsaturateds.png"))
                    .setTemperature(20).addTraits(new FT_Flammable(1_000_000), GASEOUS, P_GAS));

    public static final RegistryObject<FluidTypeHBM> SALIENT = register("salient",
            new FluidTypeHBM("SALIENT", 0x457F2D, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/salient.png"))
                    .setTemperature(20).addTraits(DELICIOUS, LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> XPJUICE = register("xpjuice",
            new FluidTypeHBM("XPJUICE", 0xBBFF09, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/xpjuice.png"))
                    .setTemperature(20).addTraits(LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> ENDERJUICE = register("enderjuice",
            new FluidTypeHBM("ENDERJUICE", 0x127766, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/enderjuice.png"))
                    .setTemperature(20).addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> CHLORINE = register("chlorine",
            new FluidTypeHBM("CHLORINE", 0xBAB572, 3, 0, 0, HBMEnums.EnumSymbol.OXIDIZER,
                    ResLocation(MODID, "textures/gui/fluids/chlorine.png"))
                    .setTemperature(20).addTraits(GASEOUS, new FT_Corrosive(25)));

    public static final RegistryObject<FluidTypeHBM> COALCREOSOTE = register("coalcreosote",
            new FluidTypeHBM("COALCREOSOTE", 0x51694F, 3, 2, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/coalcreosote.png"))
                    .addTraits(LIQUID, VISCOUS, P_OIL)); // .addContainers(new CD_Canister(0x285A3F))

    public static final RegistryObject<FluidTypeHBM> WOODOIL = register("woodoil",
            new FluidTypeHBM("WOODOIL", 0x847D54, 2, 2, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/woodoil.png"))
                    .addTraits(new FT_Flammable(35_000), new FT_Combustible(FuelGrade.LOW, 20_000), LIQUID, VISCOUS, P_OIL)); // .addContainers(new CD_Canister(0xBF7E4F))

    public static final RegistryObject<FluidTypeHBM> SODIUM = register("sodium",
            new FluidTypeHBM("SODIUM", 0xCCD4D5, 1, 2, 3, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/sodium.png"))
                    .setTemperature(400).addTraits(LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> LYE = register("lye",
            new FluidTypeHBM("LYE", 0xFFECCC, 3, 0, 1, HBMEnums.EnumSymbol.ACID, ResLocation(MODID, "textures/gui/fluids/lye.png"))
                    .addTraits(new FT_Corrosive(40), LIQUID));

    public static final RegistryObject<FluidTypeHBM> PHOSGENE = register("phosgene",
            new FluidTypeHBM("PHOSGENE", 0xCFC4A4, 4, 0, 1, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/phosgene.png"))
                    .addTraits(GASEOUS, new FT_Polluting().release(PollutionType.POISON, POISON_EXTREME))); // .addContainers(new CD_Gastank(0xCFC4A4, 0x361414))

    public static final RegistryObject<FluidTypeHBM> MUSTARDGAS = register("mustardgas",
            new FluidTypeHBM("MUSTARDGAS", 0xBAB572, 4, 1, 1, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/mustardgas.png"))
                    .addTraits(GASEOUS, new FT_Polluting().release(PollutionType.POISON, POISON_EXTREME))); // .addContainers(new CD_Gastank(0xBAB572, 0x361414))

    public static final RegistryObject<FluidTypeHBM> IONGEL = register("iongel",new FluidTypeHBM("IONGEL", 0xB8FFFF, 1, 0, 4, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/iongel.png")).addTraits(LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> ESTRADIOL = register("estradiol",
            new FluidTypeHBM("ESTRADIOL", 0xCDD5D8, 0, 0, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/estradiol.png"))
                    .addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> REDMUD = register("redmud",
            new FluidTypeHBM("REDMUD", 0xD85638, 3, 0, 4, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/redmud.png"))
                    .addTraits(LIQUID, VISCOUS, LEADCON, new FT_Corrosive(60), new FT_Flammable(1_000), new FT_Polluting().release(PollutionType.POISON, POISON_EXTREME)));

    public static final RegistryObject<FluidTypeHBM> PERFLUOROMETHYL_COLD = register("perfluoromethyl_cold",
            new FluidTypeHBM("PERFLUOROMETHYL_COLD",0x99DADE, 1, 0, 1, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/perfluoromethyl_cold.png"))
                    .setTemperature(-150).addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> PERFLUOROMETHYL = register("perfluoromethyl",
            new FluidTypeHBM("PERFLUOROMETHYL",	0xBDC8DC, 1, 0, 1, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/perfluoromethyl.png"))
                    .setTemperature(15).addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> PERFLUOROMETHYL_HOT = register("perfluoromethyl_hot",
            new FluidTypeHBM("PERFLUOROMETHYL_HOT",0xB899DE, 1, 0, 1, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/perfluoromethyl_hot.png"))
                    .setTemperature(250).addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> MUG = register("mug",
            new FluidTypeHBM("MUG", 0x4B2D28, 0, 0, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/mug.png"))
                    .addTraits(DELICIOUS, LIQUID));

    public static final RegistryObject<FluidTypeHBM> MUG_HOT = register("mug_hot",
            new FluidTypeHBM("MUG_HOT", 0x6B2A20, 0, 0, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/mug_hot.png"))
                    .setTemperature(500).addTraits(DELICIOUS, LIQUID));

    public static final RegistryObject<FluidTypeHBM> BLOOD = register("blood",
            new FluidTypeHBM("BLOOD", 0xB22424, 0, 0, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/blood.png"))
                    .addTraits(LIQUID, VISCOUS, DELICIOUS));

    public static final RegistryObject<FluidTypeHBM> BLOOD_HOT = register("blood_hot",
            new FluidTypeHBM("BLOOD_HOT", 0xF22419, 3, 0, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/blood_hot.png"))
                    .addTraits(LIQUID, VISCOUS).setTemperature(666));

    public static final RegistryObject<FluidTypeHBM> SODIUM_HOT = register("sodium_hot",
            new FluidTypeHBM("SODIUM_HOT", 0xE2ADC1, 1, 2, 3, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/sodium_hot.png"))
                    .setTemperature(1200).addTraits(LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> LEAD = register("lead",
            new FluidTypeHBM("LEAD", 0x666672, 4, 0, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/lead.png"))
                    .setTemperature(350).addTraits(LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> LEAD_HOT = register("lead_hot",
            new FluidTypeHBM("LEAD_HOT", 0x776563, 4, 0, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/lead_hot.png"))
                    .setTemperature(1500).addTraits(LIQUID, VISCOUS));

    public static final RegistryObject<FluidTypeHBM> FISHOIL = register("fishoil",
            new FluidTypeHBM("FISHOIL", 0x4B4A45, 0, 1, 0, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/fishoil.png")).addTraits(LIQUID, P_FUEL));

    public static final RegistryObject<FluidTypeHBM> REFORMGAS = register("reformgas",
            new FluidTypeHBM("REFORMGAS", 0x6362AE, 1, 4, 1, HBMEnums.EnumSymbol.NONE, ResLocation(MODID, "textures/gui/fluids/reformgas.png")).addTraits(GASEOUS, P_GAS));

    public static final RegistryObject<FluidTypeHBM> SODIUM_ALUMINATE = register("sodium_aluminate",
            new FluidTypeHBM("SODIUM_ALUMINATE", 0xFFD191, 3, 0, 1, HBMEnums.EnumSymbol.ACID, ResLocation(MODID, "textures/gui/fluids/sodium_aluminate.png")).addTraits(new FT_Corrosive(30), LIQUID));

    public static final RegistryObject<FluidTypeHBM> STELLAR_FLUX = register("stellar_flux",
            new FluidTypeHBM("STELLAR_FLUX", 0xE300FF, 0, 4, 4, HBMEnums.EnumSymbol.ANTIMATTER, ResLocation(MODID, "textures/gui/fluids/stellar_flux.png")).addTraits(ANTI, GASEOUS));

    public static final RegistryObject<FluidTypeHBM> AMAT = register("amat",
            new FluidTypeHBM("AMAT", 0x010101, 5, 0, 5, HBMEnums.EnumSymbol.ANTIMATTER, ResLocation(MODID, "textures/gui/fluids/amat.png")).addTraits(ANTI, GASEOUS));

    public static final RegistryObject<FluidTypeHBM> ASCHRAB = register("aschrab",
            new FluidTypeHBM("ASCHRAB", 0xb50000, 5, 0, 5, HBMEnums.EnumSymbol.ANTIMATTER, ResLocation(MODID, "textures/gui/fluids/ashrab.png")).addTraits(ANTI, GASEOUS));

    public static final RegistryObject<FluidTypeHBM> PHEROMONE = register("pheromone",
            new FluidTypeHBM("PHEROMONE", 0x5FA6E8, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/pheromone.png"))
                    .addTraits(LIQUID, new FT_Pheromone(1)));

    public static final RegistryObject<FluidTypeHBM> NITROGLYCERIN = register("nitroglycerin",
            new FluidTypeHBM("NITROGLYCERIN",	0x92ACA6, 0, 4, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/nitroglycerin.png")).addTraits(LIQUID));

    public static final RegistryObject<FluidTypeHBM> CONCRETE = register("concrete",
            new FluidTypeHBM( "CONCRETE",		0xA2A2A2, 0, 0, 0, HBMEnums.EnumSymbol.NONE,
                    ResLocation(MODID, "textures/gui/fluids/concrete.png")).addTraits(LIQUID));

    public static void init()
    {
        CHLORINE.get().addTraits(new FT_Toxin().addEntry(new ToxinDirectDamage(ModDamageSource.CLOUD, 2F, 20, HazardClass.GAS_LUNG, false)));
        PHOSGENE.get().addTraits(new FT_Toxin().addEntry(new ToxinDirectDamage(ModDamageSource.CLOUD, 4F, 20, HazardClass.GAS_LUNG, false)));
        MUSTARDGAS.get().addTraits(new FT_Toxin().addEntry(new ToxinDirectDamage(ModDamageSource.CLOUD, 4F, 10, HazardClass.GAS_BLISTERING, false))
                .addEntry(new ToxinEffects(HazardClass.GAS_BLISTERING, true).add(new MobEffectInstance(MobEffects.WITHER, 100, 1), new MobEffectInstance(MobEffects.CONFUSION, 100, 0))));
        ESTRADIOL.get().addTraits(new FT_Toxin().addEntry(new ToxinEffects(HazardClass.PARTICLE_FINE, false).add(new MobEffectInstance(HbmPotion.DEATH.get(), 60 * 60 * 20, 0))));
        REDMUD.get().addTraits(new FT_Toxin().addEntry(new ToxinEffects(HazardClass.GAS_BLISTERING, false).add(new MobEffectInstance(MobEffects.WITHER, 30 * 20, 2))));

        double eff_steam_boil = 1.0D;
        double eff_steam_heatex = 0.25D;

        WATER.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.BOILER, eff_steam_boil).setEfficiency(HeatingType.HEATEXCHANGER, eff_steam_heatex)
                .addStep(200, 1, STEAM.get(), 100)
                .addStep(220, 1, HOTSTEAM.get(), 10)
                .addStep(238, 1, SUPERHOTSTEAM.get(), 1)
                .addStep(2500, 10, ULTRAHOTSTEAM.get(), 1));

        STEAM.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.BOILER, eff_steam_boil).setEfficiency(HeatingType.HEATEXCHANGER, eff_steam_heatex).addStep(2, 10, HOTSTEAM.get(), 1));
        HOTSTEAM.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.BOILER, eff_steam_boil).setEfficiency(HeatingType.HEATEXCHANGER, eff_steam_heatex).addStep(18, 10, SUPERHOTSTEAM.get(), 1));
        SUPERHOTSTEAM.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.BOILER, eff_steam_boil).setEfficiency(HeatingType.HEATEXCHANGER, eff_steam_heatex).addStep(120, 10, ULTRAHOTSTEAM.get(), 1));

        double eff_steam_turbine = 1.0D;
        double eff_steam_cool = 0.5D;
        STEAM.get().addTraits(new FT_Coolable(SPENTSTEAM.get(), 100, 1, 200).setEfficiency(CoolingType.TURBINE, eff_steam_turbine).setEfficiency(CoolingType.HEATEXCHANGER, eff_steam_cool));
        HOTSTEAM.get().addTraits(new FT_Coolable(STEAM.get(), 1, 10, 2).setEfficiency(CoolingType.TURBINE, eff_steam_turbine).setEfficiency(CoolingType.HEATEXCHANGER, eff_steam_cool));
        SUPERHOTSTEAM.get().addTraits(new FT_Coolable(HOTSTEAM.get(), 1, 10, 18).setEfficiency(CoolingType.TURBINE, eff_steam_turbine).setEfficiency(CoolingType.HEATEXCHANGER, eff_steam_cool));
        ULTRAHOTSTEAM.get().addTraits(new FT_Coolable(SUPERHOTSTEAM.get(), 1, 10, 120).setEfficiency(CoolingType.TURBINE, eff_steam_turbine).setEfficiency(CoolingType.HEATEXCHANGER, eff_steam_cool));

        OIL.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.BOILER, 1.0D).setEfficiency(HeatingType.HEATEXCHANGER, 1.0D).addStep(10, 1, HOTOIL.get(), 1));
        OIL_DS.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.BOILER, 1.0D).setEfficiency(HeatingType.HEATEXCHANGER, 1.0D).addStep(10, 1, HOTOIL_DS.get(), 1));
        CRACKOIL.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.BOILER, 1.0D).setEfficiency(HeatingType.HEATEXCHANGER, 1.0D).addStep(10, 1, HOTCRACKOIL.get(), 1));
        CRACKOIL_DS.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.BOILER, 1.0D).setEfficiency(HeatingType.HEATEXCHANGER, 1.0D).addStep(10, 1, HOTCRACKOIL_DS.get(), 1));

        HOTOIL.get().addTraits(new FT_Coolable(OIL.get(), 1, 1, 10).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));
        HOTOIL_DS.get().addTraits(new FT_Coolable(OIL_DS.get(), 1, 1, 10).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));
        HOTCRACKOIL.get().addTraits(new FT_Coolable(CRACKOIL.get(), 1, 1, 10).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));
        HOTCRACKOIL_DS.get().addTraits(new FT_Coolable(CRACKOIL_DS.get(), 1, 1, 10).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));

        COOLANT.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.HEATEXCHANGER, 1.0D).setEfficiency(HeatingType.PWR, 1.0D).setEfficiency(HeatingType.ICF, 1.0D).addStep(300, 1, COOLANT_HOT.get(), 1));
        COOLANT_HOT.get().addTraits(new FT_Coolable(COOLANT.get(), 1, 1, 300).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));

        PERFLUOROMETHYL_COLD.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.PA, 1.0D).addStep(300, 1, PERFLUOROMETHYL.get(), 1));
        PERFLUOROMETHYL.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.HEATEXCHANGER, 1.0D).setEfficiency(HeatingType.PWR, 1.0D).setEfficiency(HeatingType.ICF, 1.0D).addStep(300, 1, PERFLUOROMETHYL_HOT.get(), 1));
        PERFLUOROMETHYL_HOT.get().addTraits(new FT_Coolable(PERFLUOROMETHYL.get(), 1, 1, 300).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));

        MUG.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.HEATEXCHANGER, 1.0D).setEfficiency(HeatingType.PWR, 1.0D).setEfficiency(HeatingType.ICF, 1.25D).addStep(400, 1, MUG_HOT.get(), 1), new FT_PWRModerator(1.15D));
        MUG_HOT.get().addTraits(new FT_Coolable(MUG.get(), 1, 1, 400).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));

        BLOOD.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.HEATEXCHANGER, 1.0D).setEfficiency(HeatingType.ICF, 1.25D).addStep(500, 1, BLOOD_HOT.get(), 1));
        BLOOD_HOT.get().addTraits(new FT_Coolable(BLOOD.get(), 1, 1, 500).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));

        HEAVYWATER.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.PWR, 1.0D).addStep(300, 1, HEAVYWATER_HOT.get(), 1), new FT_PWRModerator(1.25D));
        HEAVYWATER_HOT.get().addTraits(new FT_Coolable(HEAVYWATER.get(), 1, 1, 300).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));

        SODIUM.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.PWR, 2.5D).setEfficiency(HeatingType.ICF, 3D).addStep(400, 1, SODIUM_HOT.get(), 1));
        SODIUM_HOT.get().addTraits(new FT_Coolable(SODIUM.get(), 1, 1, 400).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));
        /* Fuck you, this is final now. If you had any concerns, you could have told me like a normal person instead of shitting on in-dev values that change every other day */
        LEAD.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.PWR, 0.75D).setEfficiency(HeatingType.ICF, 4D).addStep(800, 1, LEAD_HOT.get(), 1), new FT_PWRModerator(0.75D));
        /* Or maybe not, because I blocked your sorry ass. Guess why that is? */
        LEAD_HOT.get().addTraits(new FT_Coolable(LEAD.get(), 1, 1, 680).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));
        /* Maybe shittalking me in some corner where you thought I wouldn't listen was not that bright of an idea afterall? */

        THORIUM_SALT.get().addTraits(new FT_Heatable().setEfficiency(HeatingType.PWR, 1.0D).addStep(400, 1, THORIUM_SALT_HOT.get(), 1), new FT_PWRModerator(2.5D));
        THORIUM_SALT_HOT.get().addTraits(new FT_Coolable(THORIUM_SALT_DEPLETED.get(), 1, 1, 400).setEfficiency(CoolingType.HEATEXCHANGER, 1.0D));

    }



    // Метод регистрации
    private static RegistryObject<FluidTypeHBM> register(String name, FluidTypeHBM fluidType) {
        RegistryObject<FluidTypeHBM> registered = HBM_FLUIDS.register(name, () -> fluidType);
        NAME_MAP.put(name, registered);
        ID_MAP.put(nextId++, fluidType);
        return registered;
    }

    // Методы доступа
    public static FluidTypeHBM fromName(String name) {
        RegistryObject<FluidTypeHBM> fluid = NAME_MAP.get(name.toLowerCase());
        return fluid != null ? fluid.get() : NONE.get();
    }

    public static FluidTypeHBM fromID(int id) {
        return ID_MAP.getOrDefault(id, NONE.get());
    }

    public static FluidTypeHBM[] getAllFluids() {
        return NAME_MAP.values().stream()
                .map(RegistryObject::get)
                .toArray(FluidTypeHBM[]::new);
    }

    public static int getID(FluidTypeHBM fluid) {
        for (Map.Entry<Integer, FluidTypeHBM> entry : ID_MAP.entrySet()) {
            if (entry.getValue() == fluid) {
                return entry.getKey();
            }
        }
        return 0; // ID для NONE
    }
}