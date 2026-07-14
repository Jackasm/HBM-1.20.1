package com.hbm.hazard;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class HazardRegistration {

    public static final float co60 = 30.0F;
    public static final float sr90 = 15.0F;
    public static final float tc99 = 2.75F;
    public static final float i131 = 150.0F;
    public static final float xe135 = 1250.0F;
    public static final float cs137 = 20.0F;
    public static final float au198 = 500.0F;
    public static final float pb209 = 10000.0F;
    public static final float at209 = 7500.0F;
    public static final float po210 = 75.0F;
    public static final float ra226 = 7.5F;
    public static final float ac227 = 30.0F;
    public static final float th232 = 0.1F;
    public static final float thf = 1.75F;
    public static final float u = 0.35F;
    public static final float u233 = 5.0F;
    public static final float u235 = 1.0F;
    public static final float u238 = 0.25F;
    public static final float uf = 0.5F;
    public static final float np237 = 2.5F;
    public static final float npf = 1.5F;
    public static final float pu = 7.5F;
    public static final float purg = 6.25F;
    public static final float pu238 = 10.0F;
    public static final float pu239 = 5.0F;
    public static final float pu240 = 7.5F;
    public static final float pu241 = 25.0F;
    public static final float puf = 4.25F;
    public static final float am241 = 8.5F;
    public static final float am242 = 9.5F;
    public static final float amrg = 9.0F;
    public static final float amf = 4.75F;
    public static final float mox = 2.5F;
    public static final float sa326 = 15.0F;
    public static final float sa327 = 17.5F;
    public static final float saf = 5.85F;
    public static final float sas3 = 5F;
    public static final float gh336 = 5.0F;
    public static final float mud = 1.0F;
    public static final float radsource_mult = 3.0F;
    public static final float pobe = po210 * radsource_mult;
    public static final float rabe = ra226 * radsource_mult;
    public static final float pube = pu238 * radsource_mult;
    public static final float zfb_bi = u235 * 0.35F;
    public static final float zfb_pu241 = pu241 * 0.5F;
    public static final float zfb_am_mix = amrg * 0.5F;
    public static final float bf = 300_000.0F;
    public static final float bfb = 500_000.0F;
    public static final float b = 0.5F;

    public static final float sr = sa326 * 0.1F;
    public static final float sb = sa326 * 0.1F;
    public static final float trx = 25.0F;
    public static final float trn = 0.1F;
    public static final float wst = 15.0F;
    public static final float wstv = 7.5F;
    public static final float yc = u;
    public static final float fo = 10F;

    public static final float nugget = 0.1F;
    public static final float ingot = 1.0F;
    public static final float gem = 1.0F;
    public static final float plate = ingot;
    public static final float plateCast = plate * 3;
    public static final float powder_mult = 3.0F;
    public static final float powder = ingot * powder_mult;
    public static final float powder_small = nugget * powder_mult;
    public static final float ore = ingot;
    public static final float block = 10.0F;
    public static final float crystal = block;
    public static final float billet = 0.5F;
    public static final float rtg = billet * 3;
    public static final float rod = 0.5F;
    public static final float rod_dual = rod * 2;
    public static final float rod_quad = rod * 4;
    public static final float rod_rbmk = rod * 8;

    public static void registerAll() {
        registerOres();
        registerRawOres();
        registerCrystals();
        registerIngots();
        registerNuggets();
        registerBillets();
        registerFuelBillets();
        registerPlates();
        registerPowders();
        registerBlocks();
        registerWaste();
        registerRTGPellets();
        registerPileRods();
        registerDebris();
        registerSpecialItems();
        registerAsbestos();

    }

    private static void registerOres() {
        HazardBuilder.ofBlock(ModBlocks.ORE_URANIUM).rad(u * ore).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_URANIUM_DEEPSLATE).rad(u * ore).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_URANIUM_SCORCHED).rad(u * ore).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_GNEISS_URANIUM).rad(u * ore).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_GNEISS_URANIUM_SCORCHED).rad(u * ore).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_NETHER_URANIUM).rad(u * ore).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_NETHER_URANIUM_SCORCHED).rad(u * ore).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_SELLAFIELD_URANIUM_SCORCHED).rad(u * ore).register();

        HazardBuilder.ofBlock(ModBlocks.ORE_THORIUM).rad(th232 * ore).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_THORIUM_DEEPSLATE).rad(th232 * ore).register();

        HazardBuilder.ofBlock(ModBlocks.ORE_NETHER_PLUTONIUM).rad(pu * ore).register();

        HazardBuilder.ofBlock(ModBlocks.ORE_SCHRABIDIUM).rad(sa326 * ore).blinding(60F).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_GNEISS_SCHRABIDIUM).rad(sa326 * ore).blinding(60F).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_NETHER_SCHRABIDIUM).rad(sa326 * ore).blinding(60F).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_SELLAFIELD_SCHRABIDIUM).rad(sa326 * ore).blinding(60F).register();

        HazardBuilder.ofBlock(ModBlocks.ORE_ASBESTOS).asbestos(1F).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_ASBESTOS_DEEPSLATE).asbestos(1F).register();
        HazardBuilder.ofBlock(ModBlocks.ORE_GNEISS_ASBESTOS).asbestos(1F).register();
    }

    private static void registerRawOres() {
        HazardBuilder.ofItem(ModItems.RAW_URANIUM).rad(u * ore).register();
        HazardBuilder.ofItem(ModItems.RAW_THORIUM).rad(th232 * ore).register();
        HazardBuilder.ofItem(ModItems.RAW_PLUTONIUM).rad(pu * ore).register();
    }

    private static void registerCrystals() {
        HazardBuilder.ofItem(ModItems.CRYSTAL_URANIUM).rad(u * crystal).register();
        HazardBuilder.ofItem(ModItems.CRYSTAL_THORIUM).rad(th232 * crystal).register();
        HazardBuilder.ofItem(ModItems.CRYSTAL_PLUTONIUM).rad(pu * crystal).register();
        HazardBuilder.ofItem(ModItems.CRYSTAL_SCHRABIDIUM).rad(sa326 * crystal).blinding(60F).register();
        HazardBuilder.ofItem(ModItems.CRYSTAL_SCHRARANIUM).rad(sr * crystal).blinding(60F).register();
        HazardBuilder.ofItem(ModItems.CRYSTAL_TRIXITE).rad(trx * crystal).register();
        HazardBuilder.ofItem(ModItems.CRYSTAL_PHOSPHORUS).hot(20F).register();
        HazardBuilder.ofItem(ModItems.CRYSTAL_LITHIUM).hydro(crystal).register();
    }

    private static void registerIngots() {
        HazardBuilder.ofItem(ModItems.INGOT_URANIUM).rad(u * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_U233).rad(u233 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_U235).rad(u235 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_U238).rad(u238 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_URANIUM_FUEL).rad(uf * ingot).register();

        HazardBuilder.ofItem(ModItems.INGOT_THORIUM).rad(th232 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_THORIUM_FUEL).rad(thf * ingot).register();

        HazardBuilder.ofItem(ModItems.INGOT_PLUTONIUM).rad(pu * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_PLUTONIUM_FUEL).rad(puf * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_PU239).rad(pu239 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_PU238).rad(pu238 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_PU240).rad(pu240 * ingot).register();

        HazardBuilder.ofItem(ModItems.INGOT_NEPTUNIUM).rad(np237 * ingot).register();

        HazardBuilder.ofItem(ModItems.INGOT_SCHRABIDIUM).rad(sa326 * ingot).blinding(60F).register();
        HazardBuilder.ofItem(ModItems.INGOT_SCHRARANIUM).rad(sr * ingot).blinding(60F).register();
        HazardBuilder.ofItem(ModItems.INGOT_SCHRABIDATE).rad(sb * ingot).blinding(60F).register();

        HazardBuilder.ofItem(ModItems.INGOT_MOX_FUEL).rad(mox * ingot).register();

        HazardBuilder.ofItem(ModItems.INGOT_ASBESTOS).asbestos(1F).register();

        HazardBuilder.ofItem(ModItems.INGOT_ACTINIUM).rad(ac227 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_RA226).rad(ra226 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_PU241).rad(pu241 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_AM241).rad(am241 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_AM242).rad(am242 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_AM_MIX).rad(amrg * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_AMERICIUM_FUEL).rad(amf * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_GH336).rad(gh336 * ingot).register();
        HazardBuilder.ofItem(ModItems.INGOT_NEPTUNIUM_FUEL).rad(npf * ingot).register();
    }

    private static void registerNuggets() {

        HazardBuilder.ofItem(ModItems.NUGGET_ACTINIUM).rad(ac227 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_RA_226).rad(ra226 * nugget).register(); // уже есть
        HazardBuilder.ofItem(ModItems.NUGGET_ARSENIC).rad(0).register();
        HazardBuilder.ofItem(ModItems.NUGGET_PU241).rad(pu241 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_AM241).rad(am241 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_AM242).rad(am242 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_AM_MIX).rad(amrg * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_AMERICIUM_FUEL).rad(amf * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_GH336).rad(gh336 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_NEPTUNIUM_FUEL).rad(npf * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_BISMUTH).rad(0).register();
        HazardBuilder.ofItem(ModItems.NUGGET_DINEUTRONIUM).rad(0).register();
        HazardBuilder.ofItem(ModItems.NUGGET_NIOBIUM).rad(0).register();
        HazardBuilder.ofItem(ModItems.NUGGET_TANTALIUM).rad(0).register();
        HazardBuilder.ofItem(ModItems.NUGGET_ZIRCONIUM).rad(0).register();
        HazardBuilder.ofItem(ModItems.NUGGET_OSMIRIDIUM).rad(0).register();

        HazardBuilder.ofItem(ModItems.NUGGET_URANIUM).rad(u * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_URANIUM_FUEL).rad(uf * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_U233).rad(u233 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_U235).rad(u235 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_U238).rad(u238 * nugget).register();

        HazardBuilder.ofItem(ModItems.NUGGET_TH232).rad(th232 * nugget).register();

        HazardBuilder.ofItem(ModItems.NUGGET_PLUTONIUM).rad(pu * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_PLUTONIUM_FUEL).rad(puf * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_PU239).rad(pu239 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_PU238).rad(pu238 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_PU240).rad(pu240 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_PU_MIX).rad(purg * nugget).register();

        HazardBuilder.ofItem(ModItems.NUGGET_NEPTUNIUM).rad(np237 * nugget).register();

        HazardBuilder.ofItem(ModItems.NUGGET_SCHRABIDIUM).rad(sa326 * nugget).blinding(60F).register();
        HazardBuilder.ofItem(ModItems.NUGGET_SOLINIUM).rad(sa327 * nugget).blinding(60F).register();

        HazardBuilder.ofItem(ModItems.NUGGET_TECHNETIUM).rad(tc99 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_POLONIUM).rad(po210 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_RA_226).rad(ra226 * nugget).register();
        HazardBuilder.ofItem(ModItems.NUGGET_MOX_FUEL).rad(mox * nugget).register();
    }

    private static void registerBillets() {
        HazardBuilder.ofItem(ModItems.BILLET_URANIUM).rad(u * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_U233).rad(u233 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_U235).rad(u235 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_U238).rad(u238 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_TH232).rad(th232 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PLUTONIUM).rad(pu * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PU238).rad(pu238 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PU239).rad(pu239 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PU240).rad(pu240 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PU241).rad(pu241 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PU_MIX).rad(purg * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AM241).rad(am241 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AM242).rad(am242 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AM_MIX).rad(amrg * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_NEPTUNIUM).rad(np237 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_POLONIUM).rad(po210 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_TECHNETIUM).rad(tc99 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_COBALT).rad(0.1F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_CO60).rad(co60 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_SR90).rad(sr90 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AU198).rad(au198 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PB209).rad(pb209 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_RA226).rad(ra226 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_ACTINIUM).rad(ac227 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_SCHRABIDIUM).rad(sa326 * billet).blinding(60F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_SOLINIUM).rad(sa327 * billet).blinding(60F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_GH336).rad(gh336 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AUSTRALIUM).rad(5F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AUSTRALIUM_LESSER).rad(2.5F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AUSTRALIUM_GREATER).rad(7.5F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_BERYLLIUM).rad(0.1F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_BISMUTH).rad(0.1F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_ZIRCONIUM).rad(0.1F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_YHARONITE).rad(10F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_BALEFIRE_GOLD).rad(au198 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_FLASHLEAD).rad(pb209 * 1.25F * billet).hot(7F).register();
        HazardBuilder.ofItem(ModItems.BILLET_PO210BE).rad(pobe * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_RA226BE).rad(rabe * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PU238BE).rad(pube * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_NUCLEAR_WASTE).rad(wst * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AMERICIUM_FUEL).rad(amf * billet).register();
    }

    private static void registerFuelBillets() {
        HazardBuilder.ofItem(ModItems.BILLET_URANIUM_FUEL).rad(uf * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_THORIUM_FUEL).rad(thf * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_PLUTONIUM_FUEL).rad(puf * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_NEPTUNIUM_FUEL).rad(npf * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_MOX_FUEL).rad(mox * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_AMERICIUM_FUEL).rad(amf * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_LES).rad(saf * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_SCHRABIDIUM_FUEL).rad(saf * billet).blinding(60F * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_HES).rad(saf * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_ZFB_BISMUTH).rad(zfb_bi * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_ZFB_PU241).rad(zfb_pu241 * billet).register();
        HazardBuilder.ofItem(ModItems.BILLET_ZFB_AM_MIX).rad(zfb_am_mix * billet).register();
    }


    private static void registerPlates() {
        HazardBuilder.ofItem(ModItems.PLATE_SCHRABIDIUM).rad(sa326 * plate).blinding(60F).register();
    }

    private static void registerPowders() {
        HazardBuilder.ofItem(ModItems.POWDER_URANIUM).rad(u * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_URANIUM_TINY).rad(u * powder_small).register();
        HazardBuilder.ofItem(ModItems.POWDER_THORIUM).rad(th232 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_THORIUM_TINY).rad(th232 * powder_small).register();
        HazardBuilder.ofItem(ModItems.POWDER_PLUTONIUM).rad(pu * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_PLUTONIUM_TINY).rad(pu * powder_small).register();
        HazardBuilder.ofItem(ModItems.POWDER_SCHRABIDIUM).rad(sa326 * powder).blinding(60F).register();
        HazardBuilder.ofItem(ModItems.POWDER_SCHRABIDATE).rad(sb * powder).blinding(60F).register();
        HazardBuilder.ofItem(ModItems.POWDER_NEPTUNIUM).rad(np237 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_POLONIUM).rad(po210 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_ASTATINE).rad(at209 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_STRONTIUM).rad(sr90 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_YELLOWCAKE).rad(yc * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_IODINE).rad(i131 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_LITHIUM).hydro(powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_LITHIUM_TINY).hydro(powder_small).register();
        HazardBuilder.ofItem(ModItems.POWDER_COAL).coal(ingot).register();
        HazardBuilder.ofItem(ModItems.POWDER_COAL_TINY).coal(nugget).register();
        HazardBuilder.ofItem(ModItems.POWDER_LIGNITE).coal(ingot).register();
        HazardBuilder.ofItem(ModItems.POWDER_LIGNITE_TINY).coal(nugget).register();
        HazardBuilder.ofItem(ModItems.POWDER_ASBESTOS).asbestos(1F).register();
        HazardBuilder.ofItem(ModItems.POWDER_ASBESTOS_TINY).asbestos(1F).register();
        HazardBuilder.ofItem(ModItems.FALLOUT).rad(fo * powder).register();

        HazardBuilder.ofItem(ModItems.POWDER_BORON).rad(0).register();
        HazardBuilder.ofItem(ModItems.POWDER_BORON_TINY).rad(0).register();
        HazardBuilder.ofItem(ModItems.POWDER_SR90).rad(sr90 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_SR90_TINY).rad(sr90 * powder_small).register();
        HazardBuilder.ofItem(ModItems.POWDER_XE135).rad(xe135 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_XE135_TINY).rad(xe135 * powder_small).register();
        HazardBuilder.ofItem(ModItems.POWDER_CS137).rad(cs137 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_CS137_TINY).rad(cs137 * powder_small).register();
        HazardBuilder.ofItem(ModItems.POWDER_I131).rad(i131 * powder).register();
        HazardBuilder.ofItem(ModItems.POWDER_I131_TINY).rad(i131 * powder_small).register();

    }

    private static void registerBlocks() {

        HazardBuilder.ofBlock(ModBlocks.BLOCK_FALLOUT).rad(fo * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_YELLOWCAKE).rad(yc * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_MOX_FUEL).rad(mox * block).register(); // уже есть

        HazardBuilder.ofBlock(ModBlocks.BLOCK_PU238).rad(pu238 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_PU239).rad(pu239 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_PU240).rad(pu240 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_PU_MIX).rad(purg * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_URANIUM_FUEL).rad(uf * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_THORIUM_FUEL).rad(thf * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_PLUTONIUM_FUEL).rad(puf * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_MOX_FUEL).rad(mox * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_NEPTUNIUM).rad(np237 * block).register();

        HazardBuilder.ofBlock(ModBlocks.BLOCK_RA226).rad(ra226 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_ACTINIUM).rad(ac227 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_COKE).coal(block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_URANIUM).rad(u * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_U233).rad(u233 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_U235).rad(u235 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_U238).rad(u238 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_THORIUM).rad(th232 * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_PLUTONIUM).rad(pu * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_POLONIUM).rad(po210 * block).hot(20F).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_SCHRABIDIUM).rad(sa326 * block).blinding(60F).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_SCHRABIDIUM_FUEL).rad(saf * block).blinding(60F).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_SCHRARANIUM).rad(sr * block).blinding(60F).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_SCHRABIDATE).rad(sb * block).blinding(60F).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_SOLINIUM).rad(sa327 * block).blinding(60F).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_TRINITITE).rad(trn * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_WASTE).rad(wst * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_WASTE_PAINTED).rad(wst * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_WASTE_VITRIFIED).rad(wstv * block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_LITHIUM).hydro(block).register();
        HazardBuilder.ofBlock(ModBlocks.BLOCK_ASBESTOS).asbestos(1F * block).register();
        HazardBuilder.ofBlock(ModBlocks.TILE_LAB_BROKEN).asbestos(1F).register();
        HazardBuilder.ofBlock(ModBlocks.DECO_ASBESTOS).asbestos(1F).register();
        HazardBuilder.ofBlock(ModBlocks.BRICK_ASBESTOS).asbestos(1F).register();
        HazardBuilder.ofBlock(ModBlocks.CONCRETE_ASBESTOS).asbestos(1F).register();
        HazardBuilder.ofBlock(ModBlocks.GAS_ASBESTOS).asbestos(1F).register();
        HazardBuilder.ofBlock(ModBlocks.GAS_RADON).rad(0.1F).register();
        HazardBuilder.ofBlock(ModBlocks.GAS_RADON_DENSE).rad(0.5F).register();
    }

    private static void registerWaste() {
        HazardBuilder.ofItem(ModItems.WASTE_NATURAL_URANIUM).rad(wst * billet * 11.5F).register();
        HazardBuilder.ofItem(ModItems.WASTE_URANIUM).rad(wst * billet * 10F).register();
        HazardBuilder.ofItem(ModItems.WASTE_THORIUM).rad(wst * billet * 7.5F).register();
        HazardBuilder.ofItem(ModItems.WASTE_MOX).rad(wst * billet * 10F).register();
        HazardBuilder.ofItem(ModItems.WASTE_PLUTONIUM).rad(wst * billet * 12.5F).register();
        HazardBuilder.ofItem(ModItems.WASTE_U233).rad(wst * billet * 10F).register();
        HazardBuilder.ofItem(ModItems.WASTE_U235).rad(wst * billet * 11F).register();
        HazardBuilder.ofItem(ModItems.WASTE_SCHRABIDIUM).rad(wst * billet * 15F).register();
        HazardBuilder.ofItem(ModItems.WASTE_ZFB_MOX).rad(wst * billet * 5F).register();

        HazardBuilder.ofItem(ModItems.WASTE_PLATE_U233).rad(wst * ingot * 13F).register();
        HazardBuilder.ofItem(ModItems.WASTE_PLATE_U235).rad(wst * ingot * 10F).register();
        HazardBuilder.ofItem(ModItems.WASTE_PLATE_MOX).rad(wst * ingot * 16F).register();
        HazardBuilder.ofItem(ModItems.WASTE_PLATE_PU239).rad(wst * ingot * 13.5F).register();
        HazardBuilder.ofItem(ModItems.WASTE_PLATE_SA326).rad(wst * ingot * 10F).register();
        HazardBuilder.ofItem(ModItems.WASTE_PLATE_RA226BE).rad(pobe * nugget * 3).register();
        HazardBuilder.ofItem(ModItems.WASTE_PLATE_PU238BE).rad(pube * nugget * 1).register();

        HazardBuilder.ofItem(ModItems.NUCLEAR_WASTE).rad(wst * ingot).register();
        HazardBuilder.ofItem(ModItems.NUCLEAR_WASTE_TINY).rad(wst * nugget).register();
        HazardBuilder.ofItem(ModItems.TRINITITE).rad(trn * ingot).register();
        HazardBuilder.ofItem(ModItems.ANCIENT_SCRAP).rad(150F).register();
        HazardBuilder.ofItem(ModItems.BLOCK_CORIUM_COBBLE).rad(150F).register();
    }

    private static void registerRTGPellets() {
        HazardBuilder.ofItem(ModItems.PELLET_RTG).rad(rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_WEAK).rad(rtg * 0.5F).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_ACTINIUM).rad(ac227 * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_AMERICIUM).rad(am241 * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_AMERICIUM_TIER2).rad(amrg * rtg * 2).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_BERKELIUM).rad(b * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_COBALT).rad(co60 * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_GOLD).rad(au198 * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_LEAD).rad(pb209 * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_POLONIUM).rad(po210 * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_RADIUM).rad(ra226 * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_STRONTIUM).rad(sr90 * rtg).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_DEPLETED_BISMUTH).rad(0.1F).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_DEPLETED_LEAD).rad(0.1F).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_DEPLETED_MERCURY).rad(0.1F).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_DEPLETED_NEPTUNIUM).rad(np237 * 0.1F).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_DEPLETED_NICKEL).rad(0.1F).register();
        HazardBuilder.ofItem(ModItems.PELLET_RTG_DEPLETED_ZIRCONIUM).rad(0.1F).register();
    }

    private static void registerPileRods() {
        HazardBuilder.ofItem(ModItems.PILE_ROD_URANIUM).rad(u * billet * 3).register();
        HazardBuilder.ofItem(ModItems.PILE_ROD_PU239).rad(purg * billet + pu239 * billet + u * billet).register();
        HazardBuilder.ofItem(ModItems.PILE_ROD_PLUTONIUM).rad(purg * billet * 2 + u * billet).register();
        HazardBuilder.ofItem(ModItems.PILE_ROD_SOURCE).rad(rabe * billet * 3).register();
    }

    private static void registerDebris() {
        HazardBuilder.ofItem(ModItems.DEBRIS_GRAPHITE).rad(70F).hot(5F).register();
        HazardBuilder.ofItem(ModItems.DEBRIS_METAL).rad(5F).register();
        HazardBuilder.ofItem(ModItems.DEBRIS_FUEL).rad(500F).hot(5F).register();
        HazardBuilder.ofItem(ModItems.DEBRIS_CONCRETE).rad(30F).register();
        HazardBuilder.ofItem(ModItems.DEBRIS_EXCHANGER).rad(25F).register();
        HazardBuilder.ofItem(ModItems.DEBRIS_SHRAPNEL).rad(2.5F).register();
        HazardBuilder.ofItem(ModItems.DEBRIS_ELEMENT).rad(100F).register();
    }

    private static void registerSpecialItems() {
        HazardBuilder.ofItem(ModItems.EGG_BALEFIRE_SHARD).rad(bf * nugget).register();
        HazardBuilder.ofItem(ModItems.EGG_BALEFIRE).rad(bf * ingot).register();
        HazardBuilder.ofItem(ModItems.CELL_TRITIUM).rad(0.001F).register();
        HazardBuilder.ofItem(ModItems.CELL_SAS3).rad(sas3).blinding(60F).register();
        HazardBuilder.ofItem(ModItems.DEMON_CORE_CLOSED).rad(100_000F).register();
        HazardBuilder.ofItem(ModItems.SOLID_FUEL_BF).rad(1000).register();
        HazardBuilder.ofItem(ModItems.SOLID_FUEL_PRESTO_BF).rad(2000).register();
        HazardBuilder.ofItem(ModItems.SOLID_FUEL_PRESTO_TRIPLET_BF).rad(6000).register();
        HazardBuilder.ofItem(ModItems.GEM_RAD).rad(25F).register();
        HazardBuilder.ofItem(ModItems.LITHIUM).hydro(ingot).register();
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_TRITIUM).rad(0.001F * rod_dual).register();

        // Zirnox depleted rods
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_URANIUM_FUEL_DEPLETED).rad(wst * rod_dual * 10F).register();
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_THORIUM_FUEL_DEPLETED).rad(wst * rod_dual * 7.5F).register();
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_MOX_FUEL_DEPLETED).rad(wst * rod_dual * 10F).register();
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_PLUTONIUM_FUEL_DEPLETED).rad(wst * rod_dual * 12.5F).register();
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_U233_FUEL_DEPLETED).rad(wst * rod_dual * 10F).register();
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_U235_FUEL_DEPLETED).rad(wst * rod_dual * 11F).register();
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_LES_FUEL_DEPLETED).rad(wst * rod_dual * 15F).blinding(20F).register();
        HazardBuilder.ofItem(ModItems.ROD_ZIRNOX_ZFB_MOX_DEPLETED).rad(wst * rod_dual * 5F).register();

        HazardBuilder.ofItem(ModItems.BRICK_ASBESTOS).asbestos(1F).register();
    }

    private static void registerAsbestos() {






    }

    public static class HazardBuilder {
        private final Item item;
        private float radiation = 0;
        private float blinding = 0;
        private float hot = 0;
        private float coal = 0;
        private float asbestos = 0;
        private float digamma = 0;
        private float hydroactive = 0;
        private float explosive = 0;

        private HazardBuilder(Item item) {
            this.item = item;
        }

        private HazardBuilder(Block block) {
            this.item = block.asItem();
        }

        public static HazardBuilder of(Item item) {
            return new HazardBuilder(item);
        }

        public static HazardBuilder of(Block block) {
            return new HazardBuilder(block);
        }

        public static HazardBuilder ofBlock(RegistryObject<? extends Block> block) {
            return new HazardBuilder(block.get());
        }

        public static HazardBuilder ofItem(RegistryObject<? extends Item> item) {
            return new HazardBuilder(item.get());
        }

        public HazardBuilder rad(float level) {
            this.radiation = level;
            return this;
        }

        public HazardBuilder blinding(float level) {
            this.blinding = level;
            return this;
        }

        public HazardBuilder hot(float level) {
            this.hot = level;
            return this;
        }

        public HazardBuilder coal(float level) {
            this.coal = level;
            return this;
        }

        public HazardBuilder asbestos(float level) {
            this.asbestos = level;
            return this;
        }

        public HazardBuilder digamma(float level) {
            this.digamma = level;
            return this;
        }

        public HazardBuilder hydro(float level) {
            this.hydroactive = level;
            return this;
        }

        public HazardBuilder explosive(float level) {
            this.explosive = level;
            return this;
        }

        public void register() {
            HazardData data = new HazardData();
            if (radiation > 0) data.addEntry(new HazardEntry(HazardRegistry.RADIATION, radiation));
            if (blinding > 0) data.addEntry(new HazardEntry(HazardRegistry.BLINDING, blinding));
            if (hot > 0) data.addEntry(new HazardEntry(HazardRegistry.HOT, hot));
            if (asbestos > 0) data.addEntry(new HazardEntry(HazardRegistry.ASBESTOS, asbestos));
            if (digamma > 0) data.addEntry(new HazardEntry(HazardRegistry.DIGAMMA, digamma));
            if (hydroactive > 0) data.addEntry(new HazardEntry(HazardRegistry.HYDROACTIVE, hydroactive));
            if (explosive > 0) data.addEntry(new HazardEntry(HazardRegistry.EXPLOSIVE, explosive));
            if (coal > 0) data.addEntry(new HazardEntry(HazardRegistry.COAL, coal));

            HazardSystem.itemMap.put(item, data);
        }
    }
}