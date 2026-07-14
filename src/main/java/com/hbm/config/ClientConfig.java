package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ClientConfig {

    // Конфигурационные значения
    public static ForgeConfigSpec.IntValue geigerOffsetHorizontal;
    public static ForgeConfigSpec.IntValue geigerOffsetVertical;
    public static ForgeConfigSpec.IntValue infoOffsetHorizontal;
    public static ForgeConfigSpec.IntValue infoOffsetVertical;
    public static ForgeConfigSpec.IntValue infoPosition;

    public static ForgeConfigSpec.BooleanValue gunAnimsLegacy;
    public static ForgeConfigSpec.BooleanValue gunModelFov;
    public static ForgeConfigSpec.BooleanValue gunVisualRecoil;
    public static ForgeConfigSpec.DoubleValue gunAnimationSpeed;

    public static ForgeConfigSpec.BooleanValue itemTooltipShowOredict;
    public static ForgeConfigSpec.BooleanValue itemTooltipShowCustomNuke;

    public static ForgeConfigSpec.BooleanValue mainMenuWackySplashes;
    public static ForgeConfigSpec.BooleanValue doddRbmkDiagnostic;
    public static ForgeConfigSpec.BooleanValue renderCableHang;
    public static ForgeConfigSpec.BooleanValue nukeHudFlash;
    public static ForgeConfigSpec.BooleanValue nukeHudShake;
    public static ForgeConfigSpec.BooleanValue renderReeds;
    public static ForgeConfigSpec.BooleanValue neiHideSecrets;
    public static ForgeConfigSpec.BooleanValue coolingTowerParticles;
    public static ForgeConfigSpec.BooleanValue renderRebarSimple;

    public static ForgeConfigSpec.IntValue renderHeliostatBeamLimit;
    public static ForgeConfigSpec.IntValue renderRebarLimit;
    public static ForgeConfigSpec.IntValue toolHudIndicatorX;
    public static ForgeConfigSpec.IntValue toolHudIndicatorY;

    // Публичные переменные для доступа
    public static int GEIGER_OFFSET_HORIZONTAL = 0;
    public static int GEIGER_OFFSET_VERTICAL = 0;
    public static int INFO_OFFSET_HORIZONTAL = 0;
    public static int INFO_OFFSET_VERTICAL = 0;
    public static int INFO_POSITION = 0;

    public static boolean GUN_ANIMS_LEGACY = false;
    public static boolean GUN_MODEL_FOV = false;
    public static boolean GUN_VISUAL_RECOIL = true;
    public static double GUN_ANIMATION_SPEED = 1.0;

    public static boolean ITEM_TOOLTIP_SHOW_OREDICT = true;
    public static boolean ITEM_TOOLTIP_SHOW_CUSTOM_NUKE = true;

    public static boolean MAIN_MENU_WACKY_SPLASHES = true;
    public static boolean DODD_RBMK_DIAGNOSTIC = true;
    public static boolean RENDER_CABLE_HANG = true;
    public static boolean NUKE_HUD_FLASH = true;
    public static boolean NUKE_HUD_SHAKE = true;
    public static boolean RENDER_REEDS = true;
    public static boolean NEI_HIDE_SECRETS = true;
    public static boolean COOLING_TOWER_PARTICLES = true;
    public static boolean RENDER_REBAR_SIMPLE = false;

    public static int RENDER_HELIOSTAT_BEAM_LIMIT = 250;
    public static int RENDER_REBAR_LIMIT = 250;
    public static int TOOL_HUD_INDICATOR_X = 0;
    public static int TOOL_HUD_INDICATOR_Y = 0;

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("HBM Nuclear Tech Mod Client Configuration")
                .push("client");

        BUILDER.comment("HUD and UI settings")
                .push("hud");

        geigerOffsetHorizontal = BUILDER
                .comment("Horizontal offset for Geiger counter HUD")
                .defineInRange("geigerOffsetHorizontal", 0, -1000, 1000);

        geigerOffsetVertical = BUILDER
                .comment("Vertical offset for Geiger counter HUD")
                .defineInRange("geigerOffsetVertical", 0, -1000, 1000);

        infoOffsetHorizontal = BUILDER
                .comment("Horizontal offset for information HUD")
                .defineInRange("infoOffsetHorizontal", 0, -1000, 1000);

        infoOffsetVertical = BUILDER
                .comment("Vertical offset for information HUD")
                .defineInRange("infoOffsetVertical", 0, -1000, 1000);

        infoPosition = BUILDER
                .comment("Position for information HUD (0-2)")
                .defineInRange("infoPosition", 0, 0, 2);

        BUILDER.pop();

        BUILDER.comment("Gun and weapon settings")
                .push("guns");

        gunAnimsLegacy = BUILDER
                .comment("Use legacy gun animations")
                .define("gunAnimsLegacy", false);

        gunModelFov = BUILDER
                .comment("Use FOV scaling for gun models")
                .define("gunModelFov", false);

        gunVisualRecoil = BUILDER
                .comment("Enable visual recoil for guns")
                .define("gunVisualRecoil", true);

        gunAnimationSpeed = BUILDER
                .comment("Animation speed multiplier for guns")
                .defineInRange("gunAnimationSpeed", 1.0, 0.1, 5.0);

        BUILDER.pop();

        BUILDER.comment("Item and tooltip settings")
                .push("items");

        itemTooltipShowOredict = BUILDER
                .comment("Show ore dictionary info in tooltips")
                .define("itemTooltipShowOredict", true);

        itemTooltipShowCustomNuke = BUILDER
                .comment("Show custom nuke info in tooltips")
                .define("itemTooltipShowCustomNuke", true);

        BUILDER.pop();

        BUILDER.comment("Visual and rendering settings")
                .push("visual");

        mainMenuWackySplashes = BUILDER
                .comment("Enable wacky splash texts in main menu")
                .define("mainMenuWackySplashes", true);

        doddRbmkDiagnostic = BUILDER
                .comment("Enable RBMK diagnostic display")
                .define("doddRbmkDiagnostic", true);

        renderCableHang = BUILDER
                .comment("Render hanging cables")
                .define("renderCableHang", true);

        nukeHudFlash = BUILDER
                .comment("Enable HUD flash effects for nukes")
                .define("nukeHudFlash", true);

        nukeHudShake = BUILDER
                .comment("Enable HUD shake effects for nukes")
                .define("nukeHudShake", true);

        // Проверка мода ANG делается в методе load()
        renderReeds = BUILDER
                .comment("Render reeds (set to false if ANG mod is loaded)")
                .define("renderReeds", true);

        neiHideSecrets = BUILDER
                .comment("Hide secret recipes in JEI/REI")
                .define("neiHideSecrets", true);

        coolingTowerParticles = BUILDER
                .comment("Enable cooling tower particles")
                .define("coolingTowerParticles", true);

        renderRebarSimple = BUILDER
                .comment("Use simple rebar rendering")
                .define("renderRebarSimple", false);

        renderHeliostatBeamLimit = BUILDER
                .comment("Maximum number of heliostat beams to render")
                .defineInRange("renderHeliostatBeamLimit", 250, 0, 10000);

        renderRebarLimit = BUILDER
                .comment("Maximum number of rebars to render")
                .defineInRange("renderRebarLimit", 250, 0, 10000);

        BUILDER.pop();

        BUILDER.comment("Tool HUD settings")
                .push("toolhud");

        toolHudIndicatorX = BUILDER
                .comment("X position for tool HUD indicator")
                .defineInRange("toolHudIndicatorX", 0, -1000, 1000);

        toolHudIndicatorY = BUILDER
                .comment("Y position for tool HUD indicator")
                .defineInRange("toolHudIndicatorY", 0, -1000, 1000);

        BUILDER.pop();

        BUILDER.pop();

        return BUILDER.build();
    }

    public static void load() {
        // Загружаем значения из конфигурации в статические поля
        GEIGER_OFFSET_HORIZONTAL = geigerOffsetHorizontal.get();
        GEIGER_OFFSET_VERTICAL = geigerOffsetVertical.get();
        INFO_OFFSET_HORIZONTAL = infoOffsetHorizontal.get();
        INFO_OFFSET_VERTICAL = infoOffsetVertical.get();
        INFO_POSITION = infoPosition.get();

        GUN_ANIMS_LEGACY = gunAnimsLegacy.get();
        GUN_MODEL_FOV = gunModelFov.get();
        GUN_VISUAL_RECOIL = gunVisualRecoil.get();
        GUN_ANIMATION_SPEED = gunAnimationSpeed.get();

        ITEM_TOOLTIP_SHOW_OREDICT = itemTooltipShowOredict.get();
        ITEM_TOOLTIP_SHOW_CUSTOM_NUKE = itemTooltipShowCustomNuke.get();

        MAIN_MENU_WACKY_SPLASHES = mainMenuWackySplashes.get();
        DODD_RBMK_DIAGNOSTIC = doddRbmkDiagnostic.get();
        RENDER_CABLE_HANG = renderCableHang.get();
        NUKE_HUD_FLASH = nukeHudFlash.get();
        NUKE_HUD_SHAKE = nukeHudShake.get();

        // Проверяем, загружен ли мод ANG для renderReeds
        boolean angLoaded = net.minecraftforge.fml.ModList.get().isLoaded("ang");
        RENDER_REEDS = renderReeds.get() && !angLoaded;

        NEI_HIDE_SECRETS = neiHideSecrets.get();
        COOLING_TOWER_PARTICLES = coolingTowerParticles.get();
        RENDER_REBAR_SIMPLE = renderRebarSimple.get();

        RENDER_HELIOSTAT_BEAM_LIMIT = renderHeliostatBeamLimit.get();
        RENDER_REBAR_LIMIT = renderRebarLimit.get();
        TOOL_HUD_INDICATOR_X = toolHudIndicatorX.get();
        TOOL_HUD_INDICATOR_Y = toolHudIndicatorY.get();
    }

    // Методы для обратной совместимости со старым кодом
    public static boolean getGunAnimsLegacy() {
        return GUN_ANIMS_LEGACY;
    }

    public static boolean getGunModelFov() {
        return GUN_MODEL_FOV;
    }

    public static boolean getGunVisualRecoil() {
        return GUN_VISUAL_RECOIL;
    }

    public static double getGunAnimationSpeed() {
        return GUN_ANIMATION_SPEED;
    }


}