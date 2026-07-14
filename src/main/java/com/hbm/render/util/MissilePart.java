package com.hbm.render.util;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemCustomMissilePart.PartType;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.HashMap;

public class MissilePart {

    public static HashMap<Integer, MissilePart> parts = new HashMap<>();

    public Item part;
    public PartType type;
    public double height;
    public double guiheight;
    public HFRWavefrontObject model;
    public ResourceLocation texture;

    private MissilePart(Item item, PartType type, double height, double guiheight, HFRWavefrontObject model, ResourceLocation texture) {
        this.part = item;
        this.type = type;
        this.height = height;
        this.guiheight = guiheight;
        this.model = model;
        this.texture = texture;
    }

    public static void registerAllParts() {
        parts.clear();

        // ==================== THRUSTERS - SIZE 10 ====================
        registerPart(ModItems.MP_THRUSTER_10_KEROSENE.get(), PartType.THRUSTER, 1, 1,
                HBMResourceManager.mp_t_10_kerosene, HBMResourceManager.mp_t_10_kerosene_tex);
        registerPart(ModItems.MP_THRUSTER_10_SOLID.get(), PartType.THRUSTER, 0.5, 1,
                HBMResourceManager.mp_t_10_solid, HBMResourceManager.mp_t_10_solid_tex);
        registerPart(ModItems.MP_THRUSTER_10_XENON.get(), PartType.THRUSTER, 0.5, 1,
                HBMResourceManager.mp_t_10_xenon, HBMResourceManager.mp_t_10_xenon_tex);

        // ==================== THRUSTERS - SIZE 15 ====================
        registerPart(ModItems.MP_THRUSTER_15_KEROSENE.get(), PartType.THRUSTER, 1.5, 1.5,
                HBMResourceManager.mp_t_15_kerosene, HBMResourceManager.mp_t_15_kerosene_tex);
        registerPart(ModItems.MP_THRUSTER_15_KEROSENE_DUAL.get(), PartType.THRUSTER, 1, 1.5,
                HBMResourceManager.mp_t_15_kerosene_dual, HBMResourceManager.mp_t_15_kerosene_dual_tex);
        registerPart(ModItems.MP_THRUSTER_15_KEROSENE_TRIPLE.get(), PartType.THRUSTER, 1, 1.5,
                HBMResourceManager.mp_t_15_kerosene_triple, HBMResourceManager.mp_t_15_kerosene_dual_tex);
        registerPart(ModItems.MP_THRUSTER_15_SOLID.get(), PartType.THRUSTER, 0.5, 1,
                HBMResourceManager.mp_t_15_solid, HBMResourceManager.mp_t_15_solid_tex);
        registerPart(ModItems.MP_THRUSTER_15_SOLID_HEXADECUPLE.get(), PartType.THRUSTER, 0.5, 1,
                HBMResourceManager.mp_t_15_solid_hexadecuple, HBMResourceManager.mp_t_15_solid_hexadecuple_tex);
        registerPart(ModItems.MP_THRUSTER_15_HYDROGEN.get(), PartType.THRUSTER, 1.5, 1.5,
                HBMResourceManager.mp_t_15_kerosene, HBMResourceManager.mp_t_15_hydrogen_tex);
        registerPart(ModItems.MP_THRUSTER_15_HYDROGEN_DUAL.get(), PartType.THRUSTER, 1, 1.5,
                HBMResourceManager.mp_t_15_kerosene_dual, HBMResourceManager.mp_t_15_hydrogen_dual_tex);
        registerPart(ModItems.MP_THRUSTER_15_BALEFIRE_SHORT.get(), PartType.THRUSTER, 2, 2,
                HBMResourceManager.mp_t_15_balefire_short, HBMResourceManager.mp_t_15_balefire_short_tex);
        registerPart(ModItems.MP_THRUSTER_15_BALEFIRE.get(), PartType.THRUSTER, 3, 2.5,
                HBMResourceManager.mp_t_15_balefire, HBMResourceManager.mp_t_15_balefire_tex);
        registerPart(ModItems.MP_THRUSTER_15_BALEFIRE_LARGE.get(), PartType.THRUSTER, 3, 2.5,
                HBMResourceManager.mp_t_15_balefire_large, HBMResourceManager.mp_t_15_balefire_large_tex);
        registerPart(ModItems.MP_THRUSTER_15_BALEFIRE_LARGE_RAD.get(), PartType.THRUSTER, 3, 2.5,
                HBMResourceManager.mp_t_15_balefire_large, HBMResourceManager.mp_t_15_balefire_large_rad_tex);

        // ==================== THRUSTERS - SIZE 20 ====================
        registerPart(ModItems.MP_THRUSTER_20_KEROSENE.get(), PartType.THRUSTER, 3, 2.5,
                HBMResourceManager.mp_t_20_kerosene, HBMResourceManager.mp_t_20_kerosene_tex);
        registerPart(ModItems.MP_THRUSTER_20_KEROSENE_DUAL.get(), PartType.THRUSTER, 2, 2,
                HBMResourceManager.mp_t_20_kerosene_dual, HBMResourceManager.mp_t_20_kerosene_dual_tex);
        registerPart(ModItems.MP_THRUSTER_20_KEROSENE_TRIPLE.get(), PartType.THRUSTER, 2, 2,
                HBMResourceManager.mp_t_20_kerosene_triple, HBMResourceManager.mp_t_20_kerosene_dual_tex);
        registerPart(ModItems.MP_THRUSTER_20_SOLID.get(), PartType.THRUSTER, 1, 1.75,
                HBMResourceManager.mp_t_20_solid, HBMResourceManager.mp_t_20_solid_tex);
        registerPart(ModItems.MP_THRUSTER_20_SOLID_MULTI.get(), PartType.THRUSTER, 0.5, 1.5,
                HBMResourceManager.mp_t_20_solid_multi, HBMResourceManager.mp_t_20_solid_multi_tex);
        registerPart(ModItems.MP_THRUSTER_20_SOLID_MULTIER.get(), PartType.THRUSTER, 0.5, 1.5,
                HBMResourceManager.mp_t_20_solid_multi, HBMResourceManager.mp_t_20_solid_multier_tex);

        // ==================== STABILITY (FINS) ====================
        registerPart(ModItems.MP_STABILITY_10_FLAT.get(), PartType.FINS, 0, 2,
                HBMResourceManager.mp_s_10_flat, HBMResourceManager.mp_s_10_flat_tex);
        registerPart(ModItems.MP_STABILITY_10_CRUISE.get(), PartType.FINS, 0, 3,
                HBMResourceManager.mp_s_10_cruise, HBMResourceManager.mp_s_10_cruise_tex);
        registerPart(ModItems.MP_STABILITY_10_SPACE.get(), PartType.FINS, 0, 2,
                HBMResourceManager.mp_s_10_space, HBMResourceManager.mp_s_10_space_tex);

        registerPart(ModItems.MP_STABILITY_15_FLAT.get(), PartType.FINS, 0, 3,
                HBMResourceManager.mp_s_15_flat, HBMResourceManager.mp_s_15_flat_tex);
        registerPart(ModItems.MP_STABILITY_15_THIN.get(), PartType.FINS, 0, 3,
                HBMResourceManager.mp_s_15_thin, HBMResourceManager.mp_s_15_thin_tex);
        registerPart(ModItems.MP_STABILITY_15_SOYUZ.get(), PartType.FINS, 0, 3,
                HBMResourceManager.mp_s_15_soyuz, HBMResourceManager.mp_s_15_soyuz_tex);

        registerPart(ModItems.MP_STABILITY_20_FLAT.get(), PartType.FINS, 0, 3,
                HBMResourceManager.mp_s_20, HBMResourceManager.universal_tex);

        // ==================== FUSELAGE - SIZE 10 KEROSENE ====================
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_tex);
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE_CAMO.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_camo_tex);
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE_DESERT.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_desert_tex);
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE_SKY.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_sky_tex);
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE_FLAMES.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_flames_tex);
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE_INSULATION.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_insulation_tex);
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE_SLEEK.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_sleek_tex);
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE_METAL.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_metal_tex);
        registerPart(ModItems.MP_FUSELAGE_10_KEROSENE_TAINT.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_kerosene_taint_tex);

        // ==================== FUSELAGE - SIZE 10 SOLID ====================
        registerPart(ModItems.MP_FUSELAGE_10_SOLID.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_tex);
        registerPart(ModItems.MP_FUSELAGE_10_SOLID_FLAMES.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_flames_tex);
        registerPart(ModItems.MP_FUSELAGE_10_SOLID_INSULATION.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_insulation_tex);
        registerPart(ModItems.MP_FUSELAGE_10_SOLID_SLEEK.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_sleek_tex);
        registerPart(ModItems.MP_FUSELAGE_10_SOLID_SOVIET_GLORY.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_soviet_glory_tex);
        registerPart(ModItems.MP_FUSELAGE_10_SOLID_CATHEDRAL.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_cathedral_tex);
        registerPart(ModItems.MP_FUSELAGE_10_SOLID_MOONLIT.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_moonlit_tex);
        registerPart(ModItems.MP_FUSELAGE_10_SOLID_BATTERY.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_battery_tex);
        registerPart(ModItems.MP_FUSELAGE_10_SOLID_DURACELL.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_solid_duracell_tex);

        // ==================== FUSELAGE - SIZE 10 XENON ====================
        registerPart(ModItems.MP_FUSELAGE_10_XENON.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_xenon_tex);
        registerPart(ModItems.MP_FUSELAGE_10_XENON_BHOLE.get(), PartType.FUSELAGE, 4, 3,
                HBMResourceManager.mp_f_10_kerosene, HBMResourceManager.mp_f_10_xenon_bhole_tex);

        // ==================== FUSELAGE - SIZE 10 LONG KEROSENE ====================
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_CAMO.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_camo_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_DESERT.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_desert_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_SKY.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_sky_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_FLAMES.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_flames_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_INSULATION.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_insulation_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_SLEEK.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_sleek_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_METAL.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_metal_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_DASH.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_dash_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_TAINT.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_taint_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_KEROSENE_VAP.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_kerosene_vap_tex);

        // ==================== FUSELAGE - SIZE 10 LONG SOLID ====================
        registerPart(ModItems.MP_FUSELAGE_10_LONG_SOLID.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_solid_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_SOLID_FLAMES.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_solid_flames_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_SOLID_INSULATION.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_solid_insulation_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_SOLID_SLEEK.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_solid_sleek_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_SOLID_SOVIET_GLORY.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_solid_soviet_glory_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_SOLID_BULLET.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_solid_bullet_tex);
        registerPart(ModItems.MP_FUSELAGE_10_LONG_SOLID_SILVERMOONLIGHT.get(), PartType.FUSELAGE, 7, 5,
                HBMResourceManager.mp_f_10_long_kerosene, HBMResourceManager.mp_f_10_long_solid_silvermoonlight_tex);

        // ==================== FUSELAGE - SIZE 10 TO 15 ====================
        registerPart(ModItems.MP_FUSELAGE_10_15_KEROSENE.get(), PartType.FUSELAGE, 9, 5.5,
                HBMResourceManager.mp_f_10_15_kerosene, HBMResourceManager.mp_f_10_15_kerosene_tex);
        registerPart(ModItems.MP_FUSELAGE_10_15_SOLID.get(), PartType.FUSELAGE, 9, 5.5,
                HBMResourceManager.mp_f_10_15_kerosene, HBMResourceManager.mp_f_10_15_solid_tex);
        registerPart(ModItems.MP_FUSELAGE_10_15_HYDROGEN.get(), PartType.FUSELAGE, 9, 5.5,
                HBMResourceManager.mp_f_10_15_kerosene, HBMResourceManager.mp_f_10_15_hydrogen_tex);
        registerPart(ModItems.MP_FUSELAGE_10_15_BALEFIRE.get(), PartType.FUSELAGE, 9, 5.5,
                HBMResourceManager.mp_f_10_15_kerosene, HBMResourceManager.mp_f_10_15_balefire_tex);

        // ==================== FUSELAGE - SIZE 15 KEROSENE ====================
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_CAMO.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_camo_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_DESERT.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_desert_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_SKY.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_sky_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_INSULATION.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_insulation_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_METAL.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_metal_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_DECORATED.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_decorated_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_STEAMPUNK.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_steampunk_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_POLITE.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_polite_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_BLACKJACK.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_blackjack_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_LAMBDA.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_lambda_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_MINUTEMAN.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_minuteman_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_PIP.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_pip_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_TAINT.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_taint_tex);
        registerPart(ModItems.MP_FUSELAGE_15_KEROSENE_YUCK.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_kerosene_yuck_tex);

        // ==================== FUSELAGE - SIZE 15 SOLID ====================
        registerPart(ModItems.MP_FUSELAGE_15_SOLID.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_INSULATION.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_insulation_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_DESH.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_desh_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_SOVIET_GLORY.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_soviet_glory_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_SOVIET_STANK.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_soviet_stank_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_FAUST.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_faust_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_SILVERMOONLIGHT.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_silvermoonlight_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_SNOWY.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_snowy_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_PANORAMA.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_panorama_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_ROSES.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_roses_tex);
        registerPart(ModItems.MP_FUSELAGE_15_SOLID_MIMI.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_kerosene, HBMResourceManager.mp_f_15_solid_mimi_tex);

        // ==================== FUSELAGE - SIZE 15 HYDROGEN ====================
        registerPart(ModItems.MP_FUSELAGE_15_HYDROGEN.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_hydrogen, HBMResourceManager.mp_f_15_hydrogen_tex);
        registerPart(ModItems.MP_FUSELAGE_15_HYDROGEN_CATHEDRAL.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_hydrogen, HBMResourceManager.mp_f_15_hydrogen_cathedral_tex);

        // ==================== FUSELAGE - SIZE 15 BALEFIRE ====================
        registerPart(ModItems.MP_FUSELAGE_15_BALEFIRE.get(), PartType.FUSELAGE, 10, 6,
                HBMResourceManager.mp_f_15_hydrogen, HBMResourceManager.mp_f_15_balefire_tex);

        // ==================== FUSELAGE - SIZE 15 TO 20 ====================
        registerPart(ModItems.MP_FUSELAGE_15_20_KEROSENE.get(), PartType.FUSELAGE, 16, 10,
                HBMResourceManager.mp_f_15_20_kerosene, HBMResourceManager.mp_f_15_20_kerosene_tex);
        registerPart(ModItems.MP_FUSELAGE_15_20_KEROSENE_MAGNUSSON.get(), PartType.FUSELAGE, 16, 10,
                HBMResourceManager.mp_f_15_20_kerosene, HBMResourceManager.mp_f_15_20_kerosene_magnusson_tex);
        registerPart(ModItems.MP_FUSELAGE_15_20_SOLID.get(), PartType.FUSELAGE, 16, 10,
                HBMResourceManager.mp_f_15_20_kerosene, HBMResourceManager.mp_f_15_20_solid_tex);

        // ==================== WARHEADS - SIZE 10 ====================
        registerPart(ModItems.MP_WARHEAD_10_HE.get(), PartType.WARHEAD, 2, 1.5,
                HBMResourceManager.mp_w_10_he, HBMResourceManager.mp_w_10_he_tex);
        registerPart(ModItems.MP_WARHEAD_10_INCENDIARY.get(), PartType.WARHEAD, 2.5, 2,
                HBMResourceManager.mp_w_10_incendiary, HBMResourceManager.mp_w_10_incendiary_tex);
        registerPart(ModItems.MP_WARHEAD_10_BUSTER.get(), PartType.WARHEAD, 0.5, 1,
                HBMResourceManager.mp_w_10_buster, HBMResourceManager.mp_w_10_buster_tex);
        registerPart(ModItems.MP_WARHEAD_10_NUCLEAR.get(), PartType.WARHEAD, 2, 1.5,
                HBMResourceManager.mp_w_10_nuclear, HBMResourceManager.mp_w_10_nuclear_tex);
        registerPart(ModItems.MP_WARHEAD_10_NUCLEAR_LARGE.get(), PartType.WARHEAD, 2.5, 1.5,
                HBMResourceManager.mp_w_10_nuclear_large, HBMResourceManager.mp_w_10_nuclear_large_tex);
        registerPart(ModItems.MP_WARHEAD_10_TAINT.get(), PartType.WARHEAD, 2.25, 1.5,
                HBMResourceManager.mp_w_10_taint, HBMResourceManager.mp_w_10_taint_tex);
        registerPart(ModItems.MP_WARHEAD_10_CLOUD.get(), PartType.WARHEAD, 2.25, 1.5,
                HBMResourceManager.mp_w_10_taint, HBMResourceManager.mp_w_10_cloud_tex);

        // ==================== WARHEADS - SIZE 15 ====================
        registerPart(ModItems.MP_WARHEAD_15_HE.get(), PartType.WARHEAD, 2, 1.5,
                HBMResourceManager.mp_w_15_he, HBMResourceManager.mp_w_15_he_tex);
        registerPart(ModItems.MP_WARHEAD_15_INCENDIARY.get(), PartType.WARHEAD, 2, 1.5,
                HBMResourceManager.mp_w_15_incendiary, HBMResourceManager.mp_w_15_incendiary_tex);
        registerPart(ModItems.MP_WARHEAD_15_NUCLEAR.get(), PartType.WARHEAD, 3.5, 2,
                HBMResourceManager.mp_w_15_nuclear, HBMResourceManager.mp_w_15_nuclear_tex);
        registerPart(ModItems.MP_WARHEAD_15_NUCLEAR_SHARK.get(), PartType.WARHEAD, 3.5, 2,
                HBMResourceManager.mp_w_15_nuclear, HBMResourceManager.mp_w_15_nuclear_shark_tex);
        registerPart(ModItems.MP_WARHEAD_15_NUCLEAR_MIMI.get(), PartType.WARHEAD, 3.5, 2,
                HBMResourceManager.mp_w_15_nuclear, HBMResourceManager.mp_w_15_nuclear_mimi_tex);
        registerPart(ModItems.MP_WARHEAD_15_BOXCAR.get(), PartType.WARHEAD, 2.25, 7.5,
                HBMResourceManager.mp_w_15_boxcar, HBMResourceManager.boxcar_tex);
        registerPart(ModItems.MP_WARHEAD_15_N2.get(), PartType.WARHEAD, 3, 2,
                HBMResourceManager.mp_w_15_n2, HBMResourceManager.mp_w_15_n2_tex);
        registerPart(ModItems.MP_WARHEAD_15_BALEFIRE.get(), PartType.WARHEAD, 2.75, 2,
                HBMResourceManager.mp_w_15_balefire, HBMResourceManager.mp_w_15_balefire_tex);
        registerPart(ModItems.MP_WARHEAD_15_TURBINE.get(), PartType.WARHEAD, 2.25, 2,
                HBMResourceManager.mp_w_15_turbine, HBMResourceManager.mp_w_15_turbine_tex);
    }

    public static void registerPart(Item item, PartType type, double height, double guiheight,
                                    HFRWavefrontObject model, ResourceLocation texture) {
        MissilePart part = new MissilePart(item, type, height, guiheight, model, texture);
        parts.put(item.hashCode(), part);
    }

    public static MissilePart getPart(Item item) {
        if (item == null) return null;
        return parts.get(item.hashCode());
    }
}