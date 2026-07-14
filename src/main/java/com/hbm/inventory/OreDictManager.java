package com.hbm.inventory;

import com.hbm.items.DictFrame;
import com.hbm.util.Compat;

public class OreDictManager {

    public static final DictFrame WOOD = new DictFrame("wood");
    public static final DictFrame BONE = new DictFrame("bone");
    public static final DictFrame COAL = new DictFrame("coal");
    public static final DictFrame IRON = new DictFrame("iron");
    public static final DictFrame GOLD = new DictFrame("gold");
    public static final DictFrame LAPIS = new DictFrame("lapis");
    public static final DictFrame REDSTONE = new DictFrame("redstone");
    public static final DictFrame NETHERQUARTZ = new DictFrame("nether_quartz");
    public static final DictFrame QUARTZ = new DictFrame("quartz");
    public static final DictFrame DIAMOND = new DictFrame("diamond");
    public static final DictFrame EMERALD = new DictFrame("emerald");

    /*
     * RADIOACTIVE
     */
    public static final DictFrame U = new DictFrame(Compat.isModLoaded(Compat.MOD_GT6) ? "uraninite" : "uranium");
    public static final DictFrame U233 = new DictFrame("uranium233", "u233");
    public static final DictFrame U235 = new DictFrame("uranium235", "u235");
    public static final DictFrame U238 = new DictFrame("uranium238", "u238");
    public static final DictFrame TH232 = new DictFrame("thorium232", "th232", "thorium");
    public static final DictFrame PU = new DictFrame("plutonium");
    public static final DictFrame PURG = new DictFrame("plutoniumrg");
    public static final DictFrame PU238 = new DictFrame("plutonium238", "pu238");
    public static final DictFrame PU239 = new DictFrame("plutonium239", "pu239");
    public static final DictFrame PU240 = new DictFrame("plutonium240", "pu240");
    public static final DictFrame PU241 = new DictFrame("plutonium241", "pu241");
    public static final DictFrame AM241 = new DictFrame("americium241", "am241");
    public static final DictFrame AM242 = new DictFrame("americium242", "am242");
    public static final DictFrame AMRG = new DictFrame("americiumrg");
    public static final DictFrame NP237 = new DictFrame("neptunium237", "np237", "neptunium");
    public static final DictFrame PO210 = new DictFrame("polonium210", "po210", "polonium");
    public static final DictFrame TC99 = new DictFrame("technetium99", "tc99");
    public static final DictFrame RA226 = new DictFrame("radium226", "ra226");
    public static final DictFrame AC227 = new DictFrame("actinium227", "ac227");
    public static final DictFrame CO60 = new DictFrame("cobalt60", "co60");
    public static final DictFrame AU198 = new DictFrame("gold198", "au198");
    public static final DictFrame PB209 = new DictFrame("lead209", "pb209");
    public static final DictFrame SA326 = new DictFrame("schrabidium");
    public static final DictFrame SA327 = new DictFrame("solinium");
    public static final DictFrame SBD = new DictFrame("schrabidate");
    public static final DictFrame SRN = new DictFrame("schraranium");
    public static final DictFrame GH336 = new DictFrame("ghiorsium336", "gh336");
    public static final DictFrame MUD = new DictFrame("watz_mud");
    /*
     * STABLE
     */
    /** TITANIUM */
    public static final DictFrame TI = new DictFrame("titanium");
    /** COPPER */
    public static final DictFrame CU = new DictFrame("copper");
    public static final DictFrame RED_COPPER = new DictFrame("red_copper");
    public static final DictFrame ALLOY = new DictFrame("advanced_alloy");
    /** TUNGSTEN */
    public static final DictFrame W = new DictFrame("tungsten");
    /** ALUMINUM */
    public static final DictFrame AL = new DictFrame("aluminium");
    public static final DictFrame STEEL = new DictFrame("steel");
    /** TECHNETIUM STEEL */
    public static final DictFrame TCALLOY = new DictFrame("tc_alloy");
    /** CADMIUM STEEL */
    public static final DictFrame CDALLOY = new DictFrame("cd_alloy");
    /** BISMUTH BRONZE */
    public static final DictFrame BBRONZE = new DictFrame("bismuth_bronze");
    /** ARSENIC BRONZE */
    public static final DictFrame ABRONZE = new DictFrame("arsenic_bronze");
    /** BISMUTH STRONTIUM CALCIUM COPPER OXIDE */
    public static final DictFrame BSCCO = new DictFrame("bscco");
    /** LEAD */
    public static final DictFrame PB = new DictFrame("lead");
    public static final DictFrame BI = new DictFrame("bismuth");
    public static final DictFrame AS = new DictFrame("arsenic");
    public static final DictFrame CA = new DictFrame("calcium");
    public static final DictFrame CD = new DictFrame("cadmium");
    /** TANTALUM */
    public static final DictFrame TA = new DictFrame("tantalum");
    public static final DictFrame COLTAN = new DictFrame("coltan");
    /** NIOBIUM */
    public static final DictFrame NB = new DictFrame("niobium");
    /** BERYLLIUM */
    public static final DictFrame BE = new DictFrame("beryllium");
    /** COBALT */
    public static final DictFrame CO = new DictFrame("cobalt");
    /** BORON */
    public static final DictFrame B = new DictFrame("boron");
    /** SILICON */
    public static final DictFrame SI = new DictFrame("silicon");
    public static final DictFrame GRAPHITE = new DictFrame("graphite");
    public static final DictFrame CARBON = new DictFrame("carbon");
    public static final DictFrame DURA = new DictFrame("dura_steel");
    public static final DictFrame POLYMER = new DictFrame("polymer");
    public static final DictFrame BAKELITE = new DictFrame("bakelite");
    public static final DictFrame PET = new DictFrame("pet");
    public static final DictFrame PC = new DictFrame("polycarbonate");
    public static final DictFrame PVC = new DictFrame("pvc");
    public static final DictFrame LATEX = new DictFrame("latex");
    public static final DictFrame RUBBER = new DictFrame("rubber");
    public static final DictFrame MAGTUNG = new DictFrame("magnetized_tungsten");
    public static final DictFrame CMB = new DictFrame("cmb_steel");
    public static final DictFrame DESH = new DictFrame("desh");
    public static final DictFrame STAR = new DictFrame("starmetal");
    public static final DictFrame GUNMETAL = new DictFrame("gunmetal");
    public static final DictFrame WEAPONSTEEL = new DictFrame("weapon_steel");
    public static final DictFrame BIGMT = new DictFrame("saturnite");
    public static final DictFrame FERRO = new DictFrame("ferrouranium");
    public static final DictFrame EUPH = new DictFrame("euphemium");
    public static final DictFrame DNT = new DictFrame("dnt");
    public static final DictFrame FIBER = new DictFrame("fiberglass");
    public static final DictFrame ASBESTOS = new DictFrame("asbestos");
    public static final DictFrame OSMIRIDIUM = new DictFrame("osmiridium");
    /*
     * DUST AND GEM ORES
     */
    /** SULFUR */
    public static final DictFrame S = new DictFrame("sulfur");
    /** SALTPETER/NITER */
    public static final DictFrame KNO = new DictFrame("saltpeter");
    /** FLUORITE */
    public static final DictFrame F = new DictFrame("fluorite");
    public static final DictFrame LIGNITE = new DictFrame("lignite");
    public static final DictFrame COALCOKE = new DictFrame("coal_coke");
    public static final DictFrame PETCOKE = new DictFrame("pet_coke");
    public static final DictFrame LIGCOKE = new DictFrame("lignite_coke");
    public static final DictFrame CINNABAR = new DictFrame("cinnabar");
    public static final DictFrame BORAX = new DictFrame("borax");
    public static final DictFrame CHLOROCALCITE = new DictFrame("chlorocalcite");
    public static final DictFrame MOLYSITE = new DictFrame("molysite");
    public static final DictFrame SODALITE = new DictFrame("sodalite");
    public static final DictFrame VOLCANIC = new DictFrame("volcanic");
    public static final DictFrame HEMATITE = new DictFrame("hematite");
    public static final DictFrame MALACHITE = new DictFrame("malachite");
    public static final DictFrame LIMESTONE = new DictFrame("limestone");
    public static final DictFrame SLAG = new DictFrame("slag");
    public static final DictFrame BAUXITE = new DictFrame("bauxite");
    public static final DictFrame CRYOLITE = new DictFrame("cryolite");
    /*
     * HAZARDS, MISC
     */
    /** LITHIUM */
    public static final DictFrame LI = new DictFrame("lithium");
    /** SODIUM */
    public static final DictFrame NA = new DictFrame("sodium");
    /*
     * PHOSPHORUS
     */
    public static final DictFrame P_WHITE = new DictFrame("white_phosphorus");
    public static final DictFrame P_RED = new DictFrame("red_phosphorus");
    /*
     * RARE METALS
     */
    public static final DictFrame AUSTRALIUM = new DictFrame("australium");
    public static final DictFrame REIIUM = new DictFrame("reiium");
    public static final DictFrame WEIDANIUM = new DictFrame("weidanium");
    public static final DictFrame UNOBTAINIUM = new DictFrame("unobtainium");
    public static final DictFrame VERTICIUM = new DictFrame("verticium");
    public static final DictFrame DAFFERGON = new DictFrame("daffergon");
    /*
     * RARE EARTHS
     */
    public static final DictFrame RAREEARTH = new DictFrame("rare_earth");
    /** LANTHANUM */
    public static final DictFrame LA = new DictFrame("lanthanum");
    /** ZIRCONIUM */
    public static final DictFrame ZR = new DictFrame("zirconium");
    /** NEODYMIUM */
    public static final DictFrame ND = new DictFrame("neodymium");
    /** CERIUM */
    public static final DictFrame CE = new DictFrame("cerium");
    /*
     * NITAN
     */
    /** IODINE */
    public static final DictFrame I = new DictFrame("iodine");
    /** ASTATINE */
    public static final DictFrame AT = new DictFrame("astatine");
    /** CAESIUM */
    public static final DictFrame CS = new DictFrame("caesium");
    /** STRONTIUM */
    public static final DictFrame ST = new DictFrame("strontium");
    /** BROMINE */
    public static final DictFrame BR = new DictFrame("bromine");
    /** TENNESSINE */
    public static final DictFrame TS = new DictFrame("tennessine") ;
    /*
     * FISSION FRAGMENTS
     */
    public static final DictFrame SR = new DictFrame("strontium");
    public static final DictFrame SR90 = new DictFrame("strontium90", "sr90");
    public static final DictFrame I131 = new DictFrame("iodine131", "i131");
    public static final DictFrame XE135 = new DictFrame("xenon135", "xe135");
    public static final DictFrame CS137 = new DictFrame("caesium137", "cs137");
    public static final DictFrame AT209 = new DictFrame("astatine209", "at209");
}
