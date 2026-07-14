package com.hbm.render.item.weapon.sedna;

import com.hbm.items.ModGunItems;
import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;

public class RegistryGunRender {
    private static final Map<Item, BaseGunRenderer> RENDERERS = new HashMap<>();

    public static void registerRenderer(Item item, BaseGunRenderer renderer) {
        RENDERERS.put(item, renderer);
    }

    public static BaseGunRenderer getRenderer(Item item) {
        return RENDERERS.get(item);
    }

    public static void init() {
        registerRenderer(ModGunItems.GUN_PEPPERBOX.get(), new GunPepperboxRenderer());
        registerRenderer(ModGunItems.GUN_MARESLEG.get(), new GunMareslegRenderer("maresleg"));
        registerRenderer(ModGunItems.GUN_MARESLEG_BROKEN.get(), new GunMareslegRenderer("maresleg_broken"));
        registerRenderer(ModGunItems.GUN_SPAS12.get(), new GunSPAS12Renderer());
        registerRenderer(ModGunItems.GUN_DOUBLE_BARREL.get(), new GunDoubleBarrelRenderer("classic"));
        registerRenderer(ModGunItems.GUN_DOUBLE_BARREL_SACRED_DRAGON.get(), new GunDoubleBarrelRenderer("sacred_dragon"));
        registerRenderer(ModGunItems.GUN_LIBERATOR.get(), new GunLiberatorRenderer());
        registerRenderer(ModGunItems.GUN_AUTOSHOTGUN_SEXY.get(), new GunSexyRenderer());
        registerRenderer(ModGunItems.GUN_AUTOSHOTGUN_HERETIC.get(), new GunHereticRenderer());
        registerRenderer(ModGunItems.GUN_AUTOSHOTGUN.get(), new GunShredderRenderer("autoshotgun"));
        registerRenderer(ModGunItems.GUN_AUTOSHOTGUN_SHREDDER.get(), new GunShredderRenderer("autoshotgun_shredder"));
        registerRenderer(ModGunItems.GUN_GREASEGUN.get(), new GunGreasegunRenderer());
        registerRenderer(ModGunItems.GUN_UZI.get(), new GunUziRenderer());
        registerRenderer(ModGunItems.GUN_LAG.get(), new GunLAGRenderer());
        registerRenderer(ModGunItems.GUN_AM180.get(), new GunAm180Render());
        registerRenderer(ModGunItems.GUN_LIGHT_REVOLVER.get(), new GunLightRevolverRenderer("light_revolver"));
        registerRenderer(ModGunItems.GUN_LIGHT_REVOLVER_ATLAS.get(), new GunLightRevolverRenderer("light_revolver_atlas"));
        registerRenderer(ModGunItems.GUN_HENRY.get(), new GunHenryRenderer("henry"));
        registerRenderer(ModGunItems.GUN_HENRY_LINCOLN.get(), new GunHenryRenderer("henry_lincoln"));
        registerRenderer(ModGunItems.GUN_HEAVY_REVOLVER.get(), new GunHeavyRevolverRenderer("heavy_revolver"));
        registerRenderer(ModGunItems.GUN_HEAVY_REVOLVER_LILMAC.get(), new GunHeavyRevolverRenderer("lilmac"));
        registerRenderer(ModGunItems.GUN_HEAVY_REVOLVER_PROTEGE.get(), new GunHeavyRevolverRenderer("protege"));
        registerRenderer(ModGunItems.GUN_HANGMAN.get(), new GunHangmanRenderer());
        registerRenderer(ModGunItems.GUN_AMAT.get(), new GunAmatRenderer("amat"));
        registerRenderer(ModGunItems.GUN_AMAT_SUBTLETY.get(), new GunAmatRenderer("amat_subtlety"));
        registerRenderer(ModGunItems.GUN_AMAT_PENANCE.get(), new GunAmatRenderer("amat_penance"));
        registerRenderer(ModGunItems.GUN_M2.get(), new GunM2Renderer());
        registerRenderer(ModGunItems.GUN_G3.get(), new GunG3Renderer("g3"));
        registerRenderer(ModGunItems.GUN_G3_ZEBRA.get(), new GunG3Renderer("zebra"));
        registerRenderer(ModGunItems.GUN_STG77.get(), new GunSTG77Renderer());
        registerRenderer(ModGunItems.GUN_BOLTER.get(), new GunBolterRenderer());
        registerRenderer(ModGunItems.GUN_CARBINE.get(), new GunCarbineRenderer());
        registerRenderer(ModGunItems.GUN_MINIGUN.get(), new GunMinigunRenderer("minigun"));
        registerRenderer(ModGunItems.GUN_MINIGUN_LACUNAE.get(), new GunMinigunRenderer("minigun_lacunae"));
        registerRenderer(ModGunItems.GUN_MAS36.get(), new GunMAS36Renderer());
        registerRenderer(ModGunItems.GUN_ABERRATOR.get(), new GunAberratorRenderer());
        registerRenderer(ModGunItems.GUN_TESLA_CANNON.get(), new GunTeslaCannonRenderer());
        registerRenderer(ModGunItems.GUN_LASER_PISTOL.get(), new GunLaserPistolRenderer("laser_pistol"));
        registerRenderer(ModGunItems.GUN_LASER_PISTOL_PEW_PEW.get(), new GunLaserPistolRenderer("pew_pew"));
        registerRenderer(ModGunItems.GUN_LASER_PISTOL_MORNING_GLORY.get(), new GunLaserPistolRenderer("morning_glory"));
        registerRenderer(ModGunItems.GUN_LASRIFLE.get(), new GunLasRifleRenderer());
        registerRenderer(ModGunItems.GUN_CONGOLAKE.get(), new GunCongoLakeRenderer());
        registerRenderer(ModGunItems.GUN_FLAREGUN.get(), new GunFlaregunRenderer());
        registerRenderer(ModGunItems.GUN_TAU.get(), new GunTauRenderer());
        registerRenderer(ModGunItems.GUN_COILGUN.get(), new GunCoilgunRenderer());
        registerRenderer(ModGunItems.GUN_N_I_4_N_I.get(), new GunNI4NIRenderer());
        registerRenderer(ModGunItems.GUN_FLAMER.get(), new GunFlamethrowerRenderer("flamethrower"));
        registerRenderer(ModGunItems.GUN_FLAMER_TOPAZ.get(), new GunFlamethrowerRenderer("flamethrower_topaz"));
        registerRenderer(ModGunItems.GUN_FLAMER_DAYBREAKER.get(), new GunFlamethrowerRenderer("flamethrower_daybreaker"));
        registerRenderer(ModGunItems.GUN_CHEMTHROWER.get(), new GunChemthrowerRenderer());
        registerRenderer(ModGunItems.GUN_MISSILE_LAUNCHER.get(), new GunMissileLauncherRenderer());
        registerRenderer(ModGunItems.GUN_PANZERSCHRECK.get(), new GunPanzerschreckRenderer());
        registerRenderer(ModGunItems.GUN_QUADRO.get(), new GunQuadroRenderer());
        registerRenderer(ModGunItems.GUN_STINGER.get(), new GunStingerRenderer());
        registerRenderer(ModGunItems.GUN_FATMAN.get(), new GunFatManRenderer());
        registerRenderer(ModGunItems.GUN_FOLLY.get(), new GunFollyRenderer());
        registerRenderer(ModGunItems.GUN_CHARGE_THROWER.get(), new GunChargeThrowerRenderer());
        registerRenderer(ModGunItems.GUN_FIREEXT.get(), new GunFireExtRenderer());
    }
}