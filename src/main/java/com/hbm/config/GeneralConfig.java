package com.hbm.config;

import com.hbm.util.RefStrings;
import net.minecraftforge.common.ForgeConfigSpec;

public class GeneralConfig {

    // Quests
    public static ForgeConfigSpec.BooleanValue enableQuests;
    public static ForgeConfigSpec.BooleanValue enableQuestOverlay;
    public static ForgeConfigSpec.IntValue maxActiveQuests;

    // Config values
    public static ForgeConfigSpec.BooleanValue enableThermosPreventer;
    public static ForgeConfigSpec.BooleanValue enablePacketThreading;
    public static ForgeConfigSpec.IntValue packetThreadingCoreCount;
    public static ForgeConfigSpec.IntValue packetThreadingMaxCount;
    public static ForgeConfigSpec.BooleanValue packetThreadingErrorBypass;

    public static ForgeConfigSpec.BooleanValue enableDebugMode;
    public static ForgeConfigSpec.BooleanValue enableMycelium;
    public static ForgeConfigSpec.BooleanValue enablePlutoniumOre;
    public static ForgeConfigSpec.ConfigValue<String> enableDungeons;
    public static ForgeConfigSpec.BooleanValue enableMDOres;
    public static ForgeConfigSpec.BooleanValue enableMines;
    public static ForgeConfigSpec.BooleanValue enableRad;
    public static ForgeConfigSpec.BooleanValue enableNITAN;
    public static ForgeConfigSpec.BooleanValue enableBomberShortMode;
    public static ForgeConfigSpec.BooleanValue enableVaults;
    public static ForgeConfigSpec.BooleanValue enableCataclysm;
    public static ForgeConfigSpec.BooleanValue enableExtendedLogging;
    public static ForgeConfigSpec.BooleanValue enableGuns;
    public static ForgeConfigSpec.BooleanValue enableVirus;
    public static ForgeConfigSpec.BooleanValue enableCrosshairs;
    public static ForgeConfigSpec.BooleanValue enableReflectorCompat;
    public static ForgeConfigSpec.BooleanValue enableRenderDistCheck;
    public static ForgeConfigSpec.BooleanValue enableSilentCompStackErrors;
    public static ForgeConfigSpec.BooleanValue enableSkyboxes;
    public static ForgeConfigSpec.BooleanValue enableImpactWorldProvider;
    public static ForgeConfigSpec.BooleanValue enableStatReRegistering;
    public static ForgeConfigSpec.BooleanValue enableKeybindOverlap;
    public static ForgeConfigSpec.BooleanValue enableFluidContainerCompat;
    public static ForgeConfigSpec.BooleanValue enableMOTD;
    public static ForgeConfigSpec.BooleanValue enableGuideBook;
    public static ForgeConfigSpec.BooleanValue enableSoundExtension;
    public static ForgeConfigSpec.BooleanValue enableMekanismChanges;
    public static ForgeConfigSpec.BooleanValue enableServerRecipeSync;
    public static ForgeConfigSpec.BooleanValue enableLoadScreenReplacement;
    public static ForgeConfigSpec.IntValue normalSoundChannels;

    public static ForgeConfigSpec.BooleanValue enableExpensiveMode;

    public static ForgeConfigSpec.BooleanValue enable528;
    public static ForgeConfigSpec.BooleanValue enable528ReasimBoilers;
    public static ForgeConfigSpec.BooleanValue enable528ColtanDeposit;
    public static ForgeConfigSpec.BooleanValue enable528ColtanSpawn;
    public static ForgeConfigSpec.BooleanValue enable528BedrockDeposit;
    public static ForgeConfigSpec.BooleanValue enable528BedrockSpawn;
    public static ForgeConfigSpec.BooleanValue enable528BosniaSimulator;
    public static ForgeConfigSpec.BooleanValue enable528BedrockReplacement;
    public static ForgeConfigSpec.BooleanValue enable528NetherBurn;
    public static ForgeConfigSpec.IntValue coltanRate;
    public static ForgeConfigSpec.IntValue bedrockRate;

    public static ForgeConfigSpec.BooleanValue enableLBSM;
    public static ForgeConfigSpec.BooleanValue enableLBSMFullSchrab;
    public static ForgeConfigSpec.BooleanValue enableLBSMShorterDecay;
    public static ForgeConfigSpec.BooleanValue enableLBSMSimpleArmorRecipes;
    public static ForgeConfigSpec.BooleanValue enableLBSMSimpleToolRecipes;
    public static ForgeConfigSpec.BooleanValue enableLBSMSimpleAlloy;
    public static ForgeConfigSpec.BooleanValue enableLBSMSimpleChemsitry;
    public static ForgeConfigSpec.BooleanValue enableLBSMSimpleCentrifuge;
    public static ForgeConfigSpec.BooleanValue enableLBSMUnlockAnvil;
    public static ForgeConfigSpec.BooleanValue enableLBSMSimpleCrafting;
    public static ForgeConfigSpec.BooleanValue enableLBSMSimpleMedicineRecipes;
    public static ForgeConfigSpec.BooleanValue enableLBSMSafeCrates;
    public static ForgeConfigSpec.BooleanValue enableLBSMSafeMEDrives;
    public static ForgeConfigSpec.BooleanValue enableLBSMIGen;
    public static ForgeConfigSpec.IntValue schrabRate;
    public static ForgeConfigSpec.ConfigValue<String> preferredOutputMod;

    // Static fields for runtime access (mirroring old system)
    public static boolean enableThermosPreventerValue = true;
    public static boolean enablePacketThreadingValue = true;
    public static int packetThreadingCoreCountValue = 1;
    public static int packetThreadingMaxCountValue = 1;
    public static boolean packetThreadingErrorBypassValue = false;
    public static boolean enableDebugModeValue = true;
    public static boolean enableMyceliumValue = false;
    public static boolean ENABLE_PLUTONIUM_ORE = true;
    public static int enableDungeonsValue = 2;
    public static boolean enableMDOresValue = true;
    public static boolean enableMinesValue = true;
    public static boolean enableRadValue = true;
    public static boolean enableNITANValue = true;
    public static boolean enableBomberShortModeValue = false;
    public static boolean enableVaultsValue = true;
    public static boolean enableCataclysmValue = false;
    public static boolean enableExtendedLoggingValue = false;
    public static boolean enableGunsValue = true;
    public static boolean enableVirusValue = true;
    public static boolean enableCrosshairsValue = true;
    public static boolean enableReflectorCompatValue = false;
    public static boolean enableRenderDistCheckValue = true;
    public static boolean enableSilentCompStackErrorsValue = true;
    public static boolean enableSkyboxesValue = true;
    public static boolean enableImpactWorldProviderValue = true;
    public static boolean enableStatReRegisteringValue = true;
    public static boolean enableKeybindOverlapValue = true;
    public static boolean enableFluidContainerCompatValue = true;
    public static boolean enableMOTDValue = true;
    public static boolean enableGuideBookValue = true;
    public static boolean enableSoundExtensionValue = true;
    public static boolean enableMekanismChangesValue = true;
    public static boolean enableServerRecipeSyncValue = false;
    public static boolean enableLoadScreenReplacementValue = true;
    public static int normalSoundChannelsValue = 200;
    public static boolean enableExpensiveModeValue = false;
    public static boolean enable528Value = false;
    public static boolean enable528ReasimBoilersValue = true;
    public static boolean enable528ColtanDepositValue = true;
    public static boolean enable528ColtanSpawnValue = false;
    public static boolean enable528BedrockDepositValue = true;
    public static boolean enable528BedrockSpawnValue = false;
    public static boolean enable528BosniaSimulatorValue = true;
    public static boolean enable528BedrockReplacementValue = true;
    public static boolean enable528NetherBurnValue = true;
    public static int coltanRateValue = 2;
    public static int bedrockRateValue = 50;
    public static boolean ENABLE_LBSM = false;
    public static boolean enableLBSMFullSchrabValue = true;
    public static boolean enableLBSMShorterDecayValue = true;
    public static boolean ENABLE_LBSM_SIMPLE_ARMOR_RECIPES = true;
    public static boolean ENABLE_LBSM_SIMPLE_TOOL_RECIPES = true;
    public static boolean enableLBSMSimpleAlloyValue = true;
    public static boolean enableLBSMSimpleChemsitryValue = true;
    public static boolean enableLBSMSimpleCentrifugeValue = true;
    public static boolean ENABLE_LBSM_UNLOCK_ANVIL = true;
    public static boolean ENABLE_LBSM_SIMPLE_CRAFTING = true;
    public static boolean ENABLE_LBSM_SIMPLE_MEDICINE_RECIPES = true;
    public static boolean enableLBSMSafeCratesValue = true;
    public static boolean enableLBSMSafeMEDrivesValue = true;
    public static boolean enableLBSMIGenValue = true;
    public static int schrabRateValue = 20;
    public static String[] preferredOutputModValue = new String[] {RefStrings.MODID};

    // Quests
    public static boolean enableQuestsValue = true;
    public static boolean enableQuestOverlayValue = true;
    public static int maxActiveQuestsValue = 10;

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("General Configuration").push("general");

        enableThermosPreventer = BUILDER
                .comment("When set to true, will prevent the mods to launch on Thermos servers. Only disable this if you understand what \"tileentities.yml\" is, and how it severely cripples the mods.")
                .define("enableThermosPreventer", true);

        enablePacketThreading = BUILDER
                .comment("Enables creation of a separate thread to increase packet processing speed on servers. Disable this if you are having anomalous crashes related to memory connections.")
                .define("enablePacketThreading", true);

        packetThreadingCoreCount = BUILDER
                .comment("Number of core threads to create for packets (recommended 1).")
                .defineInRange("packetThreadingCoreCount", 1, 1, 16);

        packetThreadingMaxCount = BUILDER
                .comment("Maximum number of threads to create for packet threading. Must be greater than or equal to packetThreadingCoreCount.")
                .defineInRange("packetThreadingMaxCount", 1, 1, 16);

        packetThreadingErrorBypass = BUILDER
                .comment("Forces the bypassing of most packet threading errors, only enable this if directed to or if you know what you're doing.")
                .define("packetThreadingErrorBypass", false);

        enableServerRecipeSync = BUILDER
                .comment("Syncs any recipes customised via JSON to clients connecting to the server.")
                .define("enableServerRecipeSync", false);

        enableDebugMode = BUILDER
                .comment("Enable debugging mode")
                .define("enableDebugMode", false);

        // Quests
        enableQuests = BUILDER
                .comment("Enables the quest system")
                .define("enableQuests", true);

        enableQuestOverlay = BUILDER
                .comment("Shows quest overlay on screen")
                .define("enableQuestOverlay", true);

        maxActiveQuests = BUILDER
                .comment("Maximum number of active quests a player can have")
                .defineInRange("maxActiveQuests", 10, 1, 50);

        enableMycelium = BUILDER
                .comment("Allows glowing mycelium to spread")
                .define("enableMycelium", false);

        enablePlutoniumOre = BUILDER
                .comment("Enables plutonium ore generation in the nether")
                .define("enablePlutoniumOre", false);

        enableDungeons = BUILDER
                .comment("Allows structures and dungeons to spawn. Valid values are true|false|flag - flag will respect the \"Generate Structures\" world flag.")
                .define("enableDungeonSpawn", "flag");

        enableMDOres = BUILDER
                .comment("Allows NTM ores to generate in modded dimensions")
                .define("enableOresInModdedDimensions", true);

        enableMines = BUILDER
                .comment("Allows landmines to generate")
                .define("enableLandmineSpawn", true);

        enableRad = BUILDER
                .comment("Allows radiation hotspots to generate")
                .define("enableRadHotspotSpawn", true);

        enableNITAN = BUILDER
                .comment("Allows chests to spawn at specific coordinates full of powders")
                .define("enableNITANChestSpawn", true);

        enableBomberShortMode = BUILDER
                .comment("Has bomber planes spawn in closer to the target for use with smaller render distances")
                .define("enableBomberShortMode", false);

        enableVaults = BUILDER
                .comment("Allows locked safes to spawn")
                .define("enableVaultSpawn", true);

        enableCataclysm = BUILDER
                .comment("Causes satellites to fall whenever a mob dies")
                .define("enableCataclysm", false);

        enableExtendedLogging = BUILDER
                .comment("Logs uses of the detonator, nuclear explosions, missile launches, grenades, etc.")
                .define("enableExtendedLogging", false);

        enableGuns = BUILDER
                .comment("Prevents new system guns to be fired")
                .define("enableGuns", true);

        enableVirus = BUILDER
                .comment("Allows virus block to spread")
                .define("enableVirus", false);

        enableCrosshairs = BUILDER
                .comment("Shows custom crosshairs when an NTM gun is being held")
                .define("enableCrosshairs", true);

        enableReflectorCompat = BUILDER
                .comment("Enable old reflector oredict name (\"plateDenseLead\") instead of new \"plateTungCar\"")
                .define("enableReflectorCompat", false);

        enableRenderDistCheck = BUILDER
                .comment("Check invalid render distances (over 16, without OptiFine) and fix it")
                .define("enableRenderDistCheck", true);

        enableSilentCompStackErrors = BUILDER
                .comment("Enabling this will disable log spam created by unregistered items in ComparableStack instances.")
                .define("enableSilentCompStackErrors", false);

        enableSkyboxes = BUILDER
                .comment("If enabled, will try to use NTM's custom skyboxes.")
                .define("enableSkyboxes", true);

        enableImpactWorldProvider = BUILDER
                .comment("If enabled, registers custom world provider which modifies lighting and sky colors for post impact effects.")
                .define("enableImpactWorldProvider", true);

        enableStatReRegistering = BUILDER
                .comment("If enabled, will re-register item crafting/breaking/usage stats in order to fix a forge bug where modded items just won't show up.")
                .define("enableStatReRegistering", true);

        enableKeybindOverlap = BUILDER
                .comment("If enabled, will handle keybinds that would otherwise be ignored due to overlapping.")
                .define("enableKeybindOverlap", true);

        enableFluidContainerCompat = BUILDER
                .comment("If enabled, fluid containers will be oredicted and interchangable in recipes with other mods' containers, as well as TrainCraft's diesel being considered a valid diesel canister.")
                .define("enableFluidContainerCompat", true);

        enableMOTD = BUILDER
                .comment("If enabled, shows the 'Loaded mods!' chat message as well as update notifications when joining a world")
                .define("enableMOTD", true);

        enableGuideBook = BUILDER
                .comment("If enabled, gives players the guide book when joining the world for the first time")
                .define("enableGuideBook", true);

        enableSoundExtension = BUILDER
                .comment("If enabled, will change the limit for how many sounds can play at once.")
                .define("enableSoundExtension", true);

        enableMekanismChanges = BUILDER
                .comment("If enabled, will change some of Mekanism's recipes.")
                .define("enableMekanismChanges", true);

        normalSoundChannels = BUILDER
                .comment("The amount of channels to create while enableSoundExtension is enabled. Note that a value below 28 or above 200 can cause buggy sounds and issues with other mods running out of sound memory.")
                .defineInRange("normalSoundChannels", 100, 28, 500);

        enableLoadScreenReplacement = BUILDER
                .comment("Tries to replace the vanilla load screen with the 'tip of the day' one, may clash with other mods trying to do the same.")
                .define("enableLoadScreenReplacement", true);

        enableExpensiveMode = BUILDER
                .comment("It does what the name implies.")
                .define("enableExpensiveMode", false);

        BUILDER.pop();

        // 528 Mode Configuration
        BUILDER.comment("528 Mode Configuration - CAUTION! Proceed with caution!").push("528_mode");

        enable528 = BUILDER
                .comment("The central toggle for 528 mode.")
                .define("enable528Mode", false);

        enable528ReasimBoilers = BUILDER
                .comment("Keeps the RBMK dial for ReaSim boilers on, preventing use of non-ReaSim boiler columns and forcing the use of steam in-/outlets")
                .define("forceReasimBoilers", true);

        enable528ColtanDeposit = BUILDER
                .comment("Enables the coltan deposit. A large amount of coltan will spawn around a single random location in the world.")
                .define("enableColtanDeposit", true);

        enable528ColtanSpawn = BUILDER
                .comment("Enables coltan ore as a random spawn in the world. Unlike the deposit option, coltan will not just spawn in one central location.")
                .define("enableColtanSpawning", false);

        enable528BedrockDeposit = BUILDER
                .comment("Enables bedrock coltan ores in the coltan deposit. These ores can be drilled to extract infinite coltan, albeit slowly.")
                .define("enableBedrockDeposit", true);

        enable528BedrockSpawn = BUILDER
                .comment("Enables the bedrock coltan ores as a rare spawn. These will be rarely found anywhere in the world.")
                .define("enableBedrockSpawning", false);

        enable528BosniaSimulator = BUILDER
                .comment("Enables anti tank mines spawning all over the world.")
                .define("enableBosniaSimulator", true);

        enable528BedrockReplacement = BUILDER
                .comment("Replaces certain bedrock ores with ones that require additional processing.")
                .define("enableBedrockReplacement", true);

        enable528NetherBurn = BUILDER
                .comment("Whether players burn in the nether")
                .define("enableNetherBurn", true);

        coltanRate = BUILDER
                .comment("Determines how many coltan ore veins are to be expected in a chunk. These values do not affect the frequency in deposits, and only apply if random coltan spanwing is enabled.")
                .defineInRange("oreColtanFrequency", 2, 1, 100);

        bedrockRate = BUILDER
                .comment("Determines how often (1 in X) bedrock coltan ores spawn. Applies for both the bedrock ores in the coltan deposit (if applicable) and the random bedrock ores (if applicable)")
                .defineInRange("bedrockColtanFrequency", 50, 1, 1000);

        BUILDER.pop();

        // LBSM Configuration
        BUILDER.comment("LBSM Configuration - Will most likely break standard progression! However, the game gets generally easier and more enjoyable for casual players.").push("lbsm_mode");

        enableLBSM = BUILDER
                .comment("The central toggle for LBS mode. Forced OFF when 528 is enabled!")
                .define("enableLessBullshitMode", false);

        enableLBSMFullSchrab = BUILDER
                .comment("When enabled, this will replace schraranium with full schrabidium ingots in the transmutator's output")
                .define("fullSchrab", true);

        enableLBSMShorterDecay = BUILDER
                .comment("When enabled, this will highly accelerate the speed at which nuclear waste disposal drums decay their contents. 60x faster than 528 mode and 5-12x faster than on normal mode.")
                .define("shortDecay", true);

        enableLBSMSimpleArmorRecipes = BUILDER
                .comment("When enabled, simplifies the recipe for armor sets like starmetal or schrabidium.")
                .define("recipeSimpleArmor", true);

        enableLBSMSimpleToolRecipes = BUILDER
                .comment("When enabled, simplifies the recipe for tool sets like starmetal or scrhabidium")
                .define("recipeSimpleTool", true);

        enableLBSMSimpleAlloy = BUILDER
                .comment("When enabled, adds some blast furnace recipes to make certain things cheaper")
                .define("recipeSimpleAlloy", true);

        enableLBSMSimpleChemsitry = BUILDER
                .comment("When enabled, simplifies some chemical plant recipes")
                .define("recipeSimpleChemistry", true);

        enableLBSMSimpleCentrifuge = BUILDER
                .comment("When enabled, enhances centrifuge outputs to make rare materials more common")
                .define("recipeSimpleCentrifuge", true);

        enableLBSMUnlockAnvil = BUILDER
                .comment("When enabled, all anvil recipes are available at tier 1")
                .define("recipeUnlockAnvil", true);

        enableLBSMSimpleCrafting = BUILDER
                .comment("When enabled, some uncraftable or more expansive items get simple crafting recipes. Scorched uranium also becomes washable")
                .define("recipeSimpleCrafting", true);

        enableLBSMSimpleMedicineRecipes = BUILDER
                .comment("When enabled, makes some medicine recipes (like ones that require bismuth) much more affordable")
                .define("recipeSimpleMedicine", true);

        enableLBSMSafeCrates = BUILDER
                .comment("When enabled, prevents crates from becoming radioactive")
                .define("safeCrates", true);

        enableLBSMSafeMEDrives = BUILDER
                .comment("When enabled, prevents ME Drives and Portable Cells from becoming radioactive")
                .define("safeMEDrives", true);

        enableLBSMIGen = BUILDER
                .comment("When enabled, restores the industrial generator to pre-nerf power")
                .define("iGen", true);

        schrabRate = BUILDER
                .comment("Changes the amount of uranium ore needed on average to create one schrabidium ore using nukes. Standard mode value is 100")
                .defineInRange("schrabOreRate", 20, 1, 1000);

        preferredOutputMod = BUILDER
                .comment("The mods which is preferred as output when certain machines autogenerate recipes. Currently used for the shredder")
                .define("preferredOutputMod", RefStrings.MODID);

        BUILDER.pop();

        return BUILDER.build();
    }

    public static void load() {
        // Quests
        enableQuestsValue = enableQuests.get();
        enableQuestOverlayValue = enableQuestOverlay.get();
        maxActiveQuestsValue = maxActiveQuests.get();

        enableThermosPreventerValue = enableThermosPreventer.get();
        enablePacketThreadingValue = enablePacketThreading.get();
        packetThreadingCoreCountValue = packetThreadingCoreCount.get();
        packetThreadingMaxCountValue = packetThreadingMaxCount.get();
        packetThreadingErrorBypassValue = packetThreadingErrorBypass.get();
        enableDebugModeValue = enableDebugMode.get();
        enableMyceliumValue = enableMycelium.get();
        ENABLE_PLUTONIUM_ORE = enablePlutoniumOre.get();

        // Parse dungeon flag
        String dungeonFlag = enableDungeons.get();
        enableDungeonsValue = parseStructureFlag(dungeonFlag);

        enableMDOresValue = enableMDOres.get();
        enableMinesValue = enableMines.get();
        enableRadValue = enableRad.get();
        enableNITANValue = enableNITAN.get();
        enableBomberShortModeValue = enableBomberShortMode.get();
        enableVaultsValue = enableVaults.get();
        enableCataclysmValue = enableCataclysm.get();
        enableExtendedLoggingValue = enableExtendedLogging.get();
        enableGunsValue = enableGuns.get();
        enableVirusValue = enableVirus.get();
        enableCrosshairsValue = enableCrosshairs.get();
        enableReflectorCompatValue = enableReflectorCompat.get();
        enableRenderDistCheckValue = enableRenderDistCheck.get();
        enableSilentCompStackErrorsValue = enableSilentCompStackErrors.get();
        enableSkyboxesValue = enableSkyboxes.get();
        enableImpactWorldProviderValue = enableImpactWorldProvider.get();
        enableStatReRegisteringValue = enableStatReRegistering.get();
        enableKeybindOverlapValue = enableKeybindOverlap.get();
        enableFluidContainerCompatValue = enableFluidContainerCompat.get();
        enableMOTDValue = enableMOTD.get();
        enableGuideBookValue = enableGuideBook.get();
        enableSoundExtensionValue = enableSoundExtension.get();
        enableMekanismChangesValue = enableMekanismChanges.get();
        enableServerRecipeSyncValue = enableServerRecipeSync.get();
        enableLoadScreenReplacementValue = enableLoadScreenReplacement.get();
        normalSoundChannelsValue = normalSoundChannels.get();
        enableExpensiveModeValue = enableExpensiveMode.get();

        // 528 Mode
        enable528Value = enable528.get();
        enable528ReasimBoilersValue = enable528ReasimBoilers.get();
        enable528ColtanDepositValue = enable528ColtanDeposit.get();
        enable528ColtanSpawnValue = enable528ColtanSpawn.get();
        enable528BedrockDepositValue = enable528BedrockDeposit.get();
        enable528BedrockSpawnValue = enable528BedrockSpawn.get();
        enable528BosniaSimulatorValue = enable528BosniaSimulator.get();
        enable528BedrockReplacementValue = enable528BedrockReplacement.get();
        enable528NetherBurnValue = enable528NetherBurn.get();
        coltanRateValue = coltanRate.get();
        bedrockRateValue = bedrockRate.get();

        // LBSM
        ENABLE_LBSM = enableLBSM.get();
        enableLBSMFullSchrabValue = enableLBSMFullSchrab.get();
        enableLBSMShorterDecayValue = enableLBSMShorterDecay.get();
        ENABLE_LBSM_SIMPLE_ARMOR_RECIPES = enableLBSMSimpleArmorRecipes.get();
        ENABLE_LBSM_SIMPLE_TOOL_RECIPES = enableLBSMSimpleToolRecipes.get();
        enableLBSMSimpleAlloyValue = enableLBSMSimpleAlloy.get();
        enableLBSMSimpleChemsitryValue = enableLBSMSimpleChemsitry.get();
        enableLBSMSimpleCentrifugeValue = enableLBSMSimpleCentrifuge.get();
        ENABLE_LBSM_UNLOCK_ANVIL = enableLBSMUnlockAnvil.get();
        ENABLE_LBSM_SIMPLE_CRAFTING = enableLBSMSimpleCrafting.get();
        ENABLE_LBSM_SIMPLE_MEDICINE_RECIPES = enableLBSMSimpleMedicineRecipes.get();
        enableLBSMSafeCratesValue = enableLBSMSafeCrates.get();
        enableLBSMSafeMEDrivesValue = enableLBSMSafeMEDrives.get();
        enableLBSMIGenValue = enableLBSMIGen.get();
        schrabRateValue = schrabRate.get();
        preferredOutputModValue = new String[] { preferredOutputMod.get() };

        // Cross-compatibility
        if(enable528Value) ENABLE_LBSM = false;
    }

    // Helper method for parsing structure flags
    private static int parseStructureFlag(String flag) {
        if("true".equalsIgnoreCase(flag)) return 1;
        if("false".equalsIgnoreCase(flag)) return 0;
        return 2; // "flag"
    }
}