package com.hbm.client;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class ClientBulletRenderers {

    private static final Map<BulletConfig, BiConsumer<EntityBulletBaseMK4, Float>> RENDERERS = new HashMap<>();
    private static final Map<BulletConfig, BiConsumer<EntityBulletBeamBase, Float>> BEAM_RENDERERS = new HashMap<>();
    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        registered = true;

        // Bullet renderers
        put(BulletConfigRegistry.STONE, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.FLINT, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.IRON, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.SHOT, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g12_bp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g12_bp_magnum, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g12_bp_slug, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g12, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g12_slug, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g12_flechette, LegoClient.RENDER_FLECHETTE_BULLET);
        put(BulletConfigRegistry.g12_magnum, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g12_explosive, LegoClient.RENDER_EXPRESS_BULLET);
        put(BulletConfigRegistry.g12_phosphorus, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.g12_equestrian_bj, LegoClient.RENDER_LEGENDARY_BULLET);
        put(BulletConfigRegistry.g12_equestrian_tkr, LegoClient.RENDER_LEGENDARY_BULLET);
        put(BulletConfigRegistry.g10, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g10_shrapnel, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g10_du, LegoClient.RENDER_DU_BULLET);
        put(BulletConfigRegistry.g10_slug, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.g10_explosive, LegoClient.RENDER_HE_BULLET);
        put(BulletConfigRegistry.p9_sp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p9_fmj, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p9_jhp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p9_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.p22_sp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p22_fmj, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p22_jhp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p22_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.m357_bp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.m357_sp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.m357_fmj, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.m357_jhp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.m357_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.m357_express, LegoClient.RENDER_EXPRESS_BULLET);
        put(BulletConfigRegistry.m44_bp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.m44_sp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.m44_fmj, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.m44_jhp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.m44_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.m44_express, LegoClient.RENDER_EXPRESS_BULLET);
        put(BulletConfigRegistry.bmg50_sp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.bmg50_fmj, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.bmg50_jhp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.bmg50_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.bmg50_du, LegoClient.RENDER_DU_BULLET);
        put(BulletConfigRegistry.bmg50_he, LegoClient.RENDER_HE_BULLET);
        put(BulletConfigRegistry.bmg50_sm, LegoClient.RENDER_SM_BULLET);
        put(BulletConfigRegistry.r556_sp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.r556_fmj, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.r556_jhp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.r556_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.r556_inc_sp, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.r556_inc_fmj, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.r556_inc_jhp, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.r556_inc_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.b75, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.b75_inc, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.b75_exp, LegoClient.RENDER_EXPRESS_BULLET);
        put(BulletConfigRegistry.r762_sp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.r762_fmj, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.r762_jhp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.r762_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.r762_du, LegoClient.RENDER_DU_BULLET);
        put(BulletConfigRegistry.r762_he, LegoClient.RENDER_EXPRESS_BULLET);
        put(BulletConfigRegistry.g26_flare, LegoClient.RENDER_FLARE);
        put(BulletConfigRegistry.g26_flare_supply, LegoClient.RENDER_FLARE_SUPPLY);
        put(BulletConfigRegistry.g26_flare_weapon, LegoClient.RENDER_FLARE_WEAPON);
        put(BulletConfigRegistry.g40_base, LegoClient.RENDER_GRENADE);
        put(BulletConfigRegistry.p45_sp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p45_fmj, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p45_jhp, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.p45_ap, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.p45_du, LegoClient.RENDER_DU_BULLET);
        put(BulletConfigRegistry.coil_tungsten, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.coil_ferrouranium, LegoClient.RENDER_AP_BULLET);
        put(BulletConfigRegistry.rocket_rpzb_he, LegoClient.RENDER_RPZB);
        put(BulletConfigRegistry.rocket_rpzb_heat, LegoClient.RENDER_RPZB);
        put(BulletConfigRegistry.rocket_rpzb_demo, LegoClient.RENDER_RPZB);
        put(BulletConfigRegistry.rocket_rpzb_inc, LegoClient.RENDER_RPZB);
        put(BulletConfigRegistry.rocket_rpzb_phosphorus, LegoClient.RENDER_RPZB);
        put(BulletConfigRegistry.rocket_qd_he, LegoClient.RENDER_QD);
        put(BulletConfigRegistry.rocket_qd_heat, LegoClient.RENDER_QD);
        put(BulletConfigRegistry.rocket_qd_demo, LegoClient.RENDER_QD);
        put(BulletConfigRegistry.rocket_qd_inc, LegoClient.RENDER_QD);
        put(BulletConfigRegistry.rocket_qd_phosphorus, LegoClient.RENDER_QD);
        put(BulletConfigRegistry.rocket_ml_he, LegoClient.RENDER_ML);
        put(BulletConfigRegistry.rocket_ml_heat, LegoClient.RENDER_ML);
        put(BulletConfigRegistry.rocket_ml_demo, LegoClient.RENDER_ML);
        put(BulletConfigRegistry.rocket_ml_inc, LegoClient.RENDER_ML);
        put(BulletConfigRegistry.rocket_ml_phosphorus, LegoClient.RENDER_ML);
        put(BulletConfigRegistry.nuke_standard, LegoClient.RENDER_NUKE);
        put(BulletConfigRegistry.nuke_demo, LegoClient.RENDER_NUKE);
        put(BulletConfigRegistry.nuke_high, LegoClient.RENDER_NUKE);
        put(BulletConfigRegistry.nuke_tots, LegoClient.RENDER_NUKE);
        put(BulletConfigRegistry.nuke_hive, LegoClient.RENDER_HIVE);
        put(BulletConfigRegistry.nuke_balefire, LegoClient.RENDER_NUKE_BALEFIRE);
        put(BulletConfigRegistry.ct_hook, LegoClient.RENDER_CT_HOOK);
        put(BulletConfigRegistry.ct_mortar, LegoClient.RENDER_CT_MORTAR);
        put(BulletConfigRegistry.ct_mortar_charge, LegoClient.RENDER_CT_MORTAR_CHARGE);
        put(BulletConfigRegistry.MASKMAN_BULLET, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.MASKMAN_TRACER, LegoClient.RENDER_FLECHETTE_BULLET);
        put(BulletConfigRegistry.MASKMAN_BOLT, LegoClient.RENDER_GRENADE);
        put(BulletConfigRegistry.MASKMAN_ORB, LegoClient.RENDER_STANDARD_BULLET);
        put(BulletConfigRegistry.MASKMAN_ROCKET, LegoClient.RENDER_QD);

        // Beam renderers
        putBeam(BulletConfigRegistry.g12_shredder, LegoClient.RENDER_LASER_CYAN);
        putBeam(BulletConfigRegistry.g12_shredder_slug, LegoClient.RENDER_LASER_CYAN);
        putBeam(BulletConfigRegistry.g12_shredder_flechette, LegoClient.RENDER_LASER_CYAN);
        putBeam(BulletConfigRegistry.g12_shredder_magnum, LegoClient.RENDER_LASER_CYAN);
        putBeam(BulletConfigRegistry.g12_shredder_explosive, LegoClient.RENDER_LASER_CYAN);
        putBeam(BulletConfigRegistry.g12_shredder_phosphorus, LegoClient.RENDER_LASER_CYAN);
        putBeam(BulletConfigRegistry.energy_lacunae, LegoClient.RENDER_LASER_PURPLE);
        putBeam(BulletConfigRegistry.energy_lacunae_overcharge, LegoClient.RENDER_LASER_PURPLE);
        putBeam(BulletConfigRegistry.energy_lacunae_ir, LegoClient.RENDER_LASER_PURPLE);
        putBeam(BulletConfigRegistry.p35800, LegoClient.RENDER_CRACKLE_SIMPLE);
        putBeam(BulletConfigRegistry.p35800_bl, LegoClient.RENDER_BLACK_LIGHTNING);
        putBeam(BulletConfigRegistry.energy_las, LegoClient.RENDER_LASER_RED);
        putBeam(BulletConfigRegistry.energy_las_overcharge, LegoClient.RENDER_LASER_RED);
        putBeam(BulletConfigRegistry.energy_las_ir, LegoClient.RENDER_LASER_RED);
        putBeam(BulletConfigRegistry.energy_emerald, LegoClient.RENDER_LASER_EMERALD);
        putBeam(BulletConfigRegistry.energy_emerald_overcharge, LegoClient.RENDER_LASER_EMERALD);
        putBeam(BulletConfigRegistry.energy_emerald_ir, LegoClient.RENDER_LASER_EMERALD);
        putBeam(BulletConfigRegistry.energy_tesla, LegoClient.RENDER_LIGHTNING);
        putBeam(BulletConfigRegistry.energy_tesla_overcharge, LegoClient.RENDER_LIGHTNING);
        putBeam(BulletConfigRegistry.energy_tesla_ir, LegoClient.RENDER_LIGHTNING);
        putBeam(BulletConfigRegistry.energy_tesla_ir_sub, LegoClient.RENDER_LIGHTNING_SUB);
        putBeam(BulletConfigRegistry.tau_uranium, LegoClient.RENDER_TAU);
        putBeam(BulletConfigRegistry.tau_uranium_charge, LegoClient.RENDER_TAU_CHARGE);
        putBeam(BulletConfigRegistry.ni4ni_arc, LegoClient.RENDER_NI4NI_BOLT);
        putBeam(BulletConfigRegistry.folly_sm, LegoClient.RENDER_FOLLY);
        putBeam(BulletConfigRegistry.folly_nuke, LegoClient.RENDER_FOLLY);
        putBeam(BulletConfigRegistry.WORM_LASER, LegoClient.RENDER_LASER_RED);
    }

    public static BiConsumer<EntityBulletBaseMK4, Float> get(BulletConfig config) {
        BulletConfig c = config;
        while (c != null) {
            BiConsumer<EntityBulletBaseMK4, Float> r = RENDERERS.get(c);
            if (r != null) return r;
            c = c.cloneParent;
        }
        return null;
    }

    public static BiConsumer<EntityBulletBeamBase, Float> getBeam(BulletConfig config) {
        BulletConfig c = config;
        while (c != null) {
            BiConsumer<EntityBulletBeamBase, Float> r = BEAM_RENDERERS.get(c);
            if (r != null) return r;
            c = c.cloneParent;
        }
        return null;
    }

    private static void put(BulletConfig config, BiConsumer<EntityBulletBaseMK4, Float> renderer) {
        if (config != null) RENDERERS.put(config, renderer);
    }

    private static void putBeam(BulletConfig config, BiConsumer<EntityBulletBeamBase, Float> renderer) {
        if (config != null) BEAM_RENDERERS.put(config, renderer);
    }
}