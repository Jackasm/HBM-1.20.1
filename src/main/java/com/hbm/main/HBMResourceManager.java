package com.hbm.main;

import com.hbm.render.anim.AnimationLoader;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.hbm.util.ResLocation.ResLocation;

public class HBMResourceManager {

    private static final Logger LOGGER = LogManager.getLogger("HBM-HBMResourceManager");
    private static final Map<String, HFRWavefrontObject> MODEL_CACHE = new HashMap<>();
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    public static final Map<String, Map<String, BusAnimation>> weaponAnimations = new HashMap<>();

    // Текстуры для file_cabinet
    public static ResourceLocation file_cabinet_tex;
    public static ResourceLocation file_cabinet_steel_tex;
    public static ResourceLocation steel_beam_tex;
    public static ResourceLocation steel_grate_tex;
    public static ResourceLocation steel_grate_wide_tex;
    public static ResourceLocation steel_scaffold_tex;
    public static ResourceLocation anvil_iron_tex;
    public static ResourceLocation anvil_lead_tex;
    public static ResourceLocation anvil_steel_tex;
    public static ResourceLocation anvil_arsenic_bronze_tex;
    public static ResourceLocation anvil_bismuth_bronze_tex;
    public static ResourceLocation anvil_desh_tex;
    public static ResourceLocation anvil_dnt_tex;
    public static ResourceLocation anvil_ferrouranium_tex;
    public static ResourceLocation anvil_murky_tex;
    public static ResourceLocation anvil_osmiridium_tex;
    public static ResourceLocation anvil_saturnite_tex;
    public static ResourceLocation anvil_schrabidate_tex;
    public static ResourceLocation press_body_tex;
    public static ResourceLocation press_head_tex;
    public static ResourceLocation barrel_iron_tex;
    public static ResourceLocation barrel_steel_tex;
    public static ResourceLocation barrel_plastic_tex;
    public static ResourceLocation barrel_corroded_tex;
    public static ResourceLocation barrel_tcalloy_tex;
    public static ResourceLocation barrel_antimatter_tex;
    public static ResourceLocation barrel_red_tex;
    public static ResourceLocation barrel_pink_tex;
    public static ResourceLocation barrel_yellow_tex;
    public static ResourceLocation barrel_lox_tex;
    public static ResourceLocation barrel_taint_tex;
    public static ResourceLocation pepperbox_tex;
    public static ResourceLocation maresleg_tex;
    public static ResourceLocation maresleg_broken_tex;
    public static ResourceLocation spas_12_tex;
    public static ResourceLocation casings_tex;
    public static ResourceLocation casings_base_tex;
    public static ResourceLocation double_barrel_tex;
    public static ResourceLocation sacred_dragon_tex;
    public static ResourceLocation liberator_tex;
    public static ResourceLocation sexy_tex;
    public static ResourceLocation whiskey_tex;
    public static ResourceLocation heretic_tex;
    public static ResourceLocation shredder_tex;
    public static ResourceLocation autoshotgun_shredder_tex;
    public static ResourceLocation greasegun_tex;
    public static ResourceLocation greasegun_clean_tex;
    public static ResourceLocation uzi_tex;
    public static ResourceLocation uzi_saturnite_tex;
    public static ResourceLocation lag_tex;
    public static ResourceLocation am180_tex;
    public static ResourceLocation light_revolver_atlas_tex;
    public static ResourceLocation light_revolver_tex;
    public static ResourceLocation henry_tex;
    public static ResourceLocation henry_lincoln_tex;
    public static ResourceLocation heavy_revolver_tex;
    public static ResourceLocation lilmac_tex;
    public static ResourceLocation heavy_revolver_protege_tex;
    public static ResourceLocation lilmac_scope_tex;
    public static ResourceLocation hangman_tex;
    public static ResourceLocation amat_tex;
    public static ResourceLocation amat_penance_tex;
    public static ResourceLocation amat_subtlety_tex;
    public static ResourceLocation g3_attachments_tex;
    public static ResourceLocation m2_tex;
    public static ResourceLocation g3_tex;
    public static ResourceLocation g3_zebra_tex;
    public static ResourceLocation g3_green_tex;
    public static ResourceLocation g3_black_tex;
    public static ResourceLocation stg77_tex;
    public static ResourceLocation bolter_tex;
    public static ResourceLocation carbine_tex;
    public static ResourceLocation carbine_bayonet_tex;
    public static ResourceLocation minigun_tex;
    public static ResourceLocation minigun_lacunae_tex;
    public static ResourceLocation mas36_tex;
    public static ResourceLocation aberrator_tex;
    public static ResourceLocation tesla_cannon_tex;
    public static ResourceLocation yomi_tex;
    public static ResourceLocation hundun_tex;
    public static ResourceLocation laser_pistol_tex;
    public static ResourceLocation laser_pistol_pew_pew_tex;
    public static ResourceLocation laser_pistol_morning_glory_tex;
    public static ResourceLocation lasrifle_tex;
    public static ResourceLocation lasrifle_mods_tex;
    public static ResourceLocation congolake_tex;
    public static ResourceLocation flamethrower_tex;
    public static ResourceLocation flamethrower_topaz_tex;
    public static ResourceLocation flamethrower_daybreaker_tex;
    public static ResourceLocation flaregun_tex;
    public static ResourceLocation grenade_tex;
    public static ResourceLocation tau_tex;
    public static ResourceLocation coilgun_tex;
    public static ResourceLocation n_i_4_n_i_tex;
    public static ResourceLocation n_i_4_n_i_greyscale_tex;
    public static ResourceLocation chemthrower_tex;
    public static ResourceLocation rocket_tex;
    public static ResourceLocation missile_launcher_tex;
    public static ResourceLocation panzerschreck_tex;
    public static ResourceLocation quadro_tex;
    public static ResourceLocation stinger_tex;
    public static ResourceLocation quadro_rocket_tex;
    public static ResourceLocation chip_gold_tex;
    public static ResourceLocation c130_0_tex;
    public static ResourceLocation supply_crate_tex;
    public static ResourceLocation soyuz_chute_tex;
    public static ResourceLocation soyuz_lander_tex;
    public static ResourceLocation grenade_mk2_tex;
    public static ResourceLocation grenade_aschrab_tex;
    public static ResourceLocation blast_fleija_tex;
    public static ResourceLocation ash_glasses_tex;
    public static ResourceLocation fluid_tank_tex;
    public static ResourceLocation fluid_tank_inner_tex;
    public static ResourceLocation ashpit_tex;
    public static ResourceLocation heater_firebox_tex;
    public static ResourceLocation heater_oven_tex;
    public static ResourceLocation heater_oilburner_tex;
    public static ResourceLocation furnace_steel_tex;
    public static ResourceLocation furnace_iron_tex;
    public static ResourceLocation furnace_combination_tex;
    public static ResourceLocation sawmill_tex;
    public static ResourceLocation heat_boiler_tex;
    public static ResourceLocation autosaw_tex;
    public static ResourceLocation boat_tex;
    public static ResourceLocation boxcar_tex;
    public static ResourceLocation mine_ap_grass_tex;
    public static ResourceLocation mine_ap_stone_tex;
    public static ResourceLocation mine_ap_snow_tex;
    public static ResourceLocation mine_ap_desert_tex;
    public static ResourceLocation mine_he_tex;
    public static ResourceLocation mine_shrap_tex;
    public static ResourceLocation mine_fat_tex;
    public static ResourceLocation mine_naval_tex;
    public static ResourceLocation pole_tex;
    public static ResourceLocation taperecorder_tex;
    public static ResourceLocation no9_tex;
    public static ResourceLocation no9_insignia_tex;
    public static ResourceLocation shimmer_sledge_tex;
    public static ResourceLocation shimmer_axe_tex;
    public static ResourceLocation stopsign_tex;
    public static ResourceLocation sopsign_tex;
    public static ResourceLocation chernobylsign_tex;
    public static ResourceLocation gavel_wood_tex;
    public static ResourceLocation gavel_lead_tex;
    public static ResourceLocation gavel_diamond_tex;
    public static ResourceLocation nuke_custom_tex;
    public static ResourceLocation det_cord_tex;
    public static ResourceLocation floodlight_tex;
    public static ResourceLocation spotlight_incandescent_tex;
    public static ResourceLocation spotlight_incandescent_off_tex;
    public static ResourceLocation fluorescent_lamp_tex;
    public static ResourceLocation fluorescent_lamp_off_tex;
    public static ResourceLocation flood_lamp_tex;
    public static ResourceLocation flood_lamp_off_tex;
    public static ResourceLocation cargo_container_tex;
    public static ResourceLocation diesel_gen_tex;
    public static ResourceLocation gascent_tex;
    public static ResourceLocation centrifuge_tex;
    public static ResourceLocation red_cable_tex;
    public static ResourceLocation pump_steam_tex;
    public static ResourceLocation pump_electric_tex;
    public static ResourceLocation stirling_tex;
    public static ResourceLocation stirling_creative_tex;
    public static ResourceLocation stirling_steel_tex;
    public static ResourceLocation steam_engine_tex;
    public static ResourceLocation soldering_station_tex;
    public static ResourceLocation arc_welder_tex;
    public static ResourceLocation emp_blast_tex;
    public static ResourceLocation fatman_tex;
    public static ResourceLocation fatman_mininuke_tex;
    public static ResourceLocation fatman_balefire_tex;
    public static ResourceLocation folly_tex;
    public static ResourceLocation charge_thrower_tex;
    public static ResourceLocation charge_thrower_hook_tex;
    public static ResourceLocation charge_thrower_mortar_tex;
    public static ResourceLocation fireext_foam_tex;
    public static ResourceLocation fireext_sand_tex;
    public static ResourceLocation fireext_tex;
    public static ResourceLocation n2_tex;

    // Missile Parts - Thrusters Size 10
    public static ResourceLocation mp_t_10_kerosene_tex;
    public static ResourceLocation mp_t_10_solid_tex;
    public static ResourceLocation mp_t_10_xenon_tex;

    // Missile Parts - Thrusters Size 15
    public static ResourceLocation mp_t_15_kerosene_tex;
    public static ResourceLocation mp_t_15_kerosene_dual_tex;
    public static ResourceLocation mp_t_15_solid_tex;
    public static ResourceLocation mp_t_15_solid_hexadecuple_tex;
    public static ResourceLocation mp_t_15_hydrogen_tex;
    public static ResourceLocation mp_t_15_hydrogen_dual_tex;
    public static ResourceLocation mp_t_15_balefire_short_tex;
    public static ResourceLocation mp_t_15_balefire_tex;
    public static ResourceLocation mp_t_15_balefire_large_tex;
    public static ResourceLocation mp_t_15_balefire_large_rad_tex;

    // Missile Parts - Thrusters Size 20
    public static ResourceLocation mp_t_20_kerosene_tex;
    public static ResourceLocation mp_t_20_kerosene_dual_tex;
    public static ResourceLocation mp_t_20_solid_tex;
    public static ResourceLocation mp_t_20_solid_multi_tex;
    public static ResourceLocation mp_t_20_solid_multier_tex;

    // Missile Parts - Stability
    public static ResourceLocation mp_s_10_flat_tex;
    public static ResourceLocation mp_s_10_cruise_tex;
    public static ResourceLocation mp_s_10_space_tex;
    public static ResourceLocation mp_s_15_flat_tex;
    public static ResourceLocation mp_s_15_thin_tex;
    public static ResourceLocation mp_s_15_soyuz_tex;

    // Missile Parts - Fuselages
    public static ResourceLocation mp_f_10_kerosene_tex;
    public static ResourceLocation mp_f_10_kerosene_camo_tex;
    public static ResourceLocation mp_f_10_kerosene_desert_tex;
    public static ResourceLocation mp_f_10_kerosene_sky_tex;
    public static ResourceLocation mp_f_10_kerosene_flames_tex;
    public static ResourceLocation mp_f_10_kerosene_insulation_tex;
    public static ResourceLocation mp_f_10_kerosene_sleek_tex;
    public static ResourceLocation mp_f_10_kerosene_metal_tex;
    public static ResourceLocation mp_f_10_kerosene_taint_tex;
    public static ResourceLocation mp_f_10_solid_tex;
    public static ResourceLocation mp_f_10_solid_flames_tex;
    public static ResourceLocation mp_f_10_solid_insulation_tex;
    public static ResourceLocation mp_f_10_solid_sleek_tex;
    public static ResourceLocation mp_f_10_solid_soviet_glory_tex;
    public static ResourceLocation mp_f_10_solid_moonlit_tex;
    public static ResourceLocation mp_f_10_solid_cathedral_tex;
    public static ResourceLocation mp_f_10_solid_battery_tex;
    public static ResourceLocation mp_f_10_solid_duracell_tex;
    public static ResourceLocation mp_f_10_xenon_tex;
    public static ResourceLocation mp_f_10_xenon_bhole_tex;
    public static ResourceLocation mp_f_10_long_kerosene_tex;
    public static ResourceLocation mp_f_10_long_kerosene_camo_tex;
    public static ResourceLocation mp_f_10_long_kerosene_desert_tex;
    public static ResourceLocation mp_f_10_long_kerosene_sky_tex;
    public static ResourceLocation mp_f_10_long_kerosene_flames_tex;
    public static ResourceLocation mp_f_10_long_kerosene_insulation_tex;
    public static ResourceLocation mp_f_10_long_kerosene_sleek_tex;
    public static ResourceLocation mp_f_10_long_kerosene_metal_tex;
    public static ResourceLocation mp_f_10_long_kerosene_dash_tex;
    public static ResourceLocation mp_f_10_long_kerosene_taint_tex;
    public static ResourceLocation mp_f_10_long_kerosene_vap_tex;
    public static ResourceLocation mp_f_10_long_solid_tex;
    public static ResourceLocation mp_f_10_long_solid_flames_tex;
    public static ResourceLocation mp_f_10_long_solid_insulation_tex;
    public static ResourceLocation mp_f_10_long_solid_sleek_tex;
    public static ResourceLocation mp_f_10_long_solid_soviet_glory_tex;
    public static ResourceLocation mp_f_10_long_solid_bullet_tex;
    public static ResourceLocation mp_f_10_long_solid_silvermoonlight_tex;
    public static ResourceLocation mp_f_10_15_kerosene_tex;
    public static ResourceLocation mp_f_10_15_solid_tex;
    public static ResourceLocation mp_f_10_15_hydrogen_tex;
    public static ResourceLocation mp_f_10_15_balefire_tex;
    public static ResourceLocation mp_f_15_kerosene_tex;
    public static ResourceLocation mp_f_15_kerosene_camo_tex;
    public static ResourceLocation mp_f_15_kerosene_desert_tex;
    public static ResourceLocation mp_f_15_kerosene_sky_tex;
    public static ResourceLocation mp_f_15_kerosene_insulation_tex;
    public static ResourceLocation mp_f_15_kerosene_metal_tex;
    public static ResourceLocation mp_f_15_kerosene_decorated_tex;
    public static ResourceLocation mp_f_15_kerosene_steampunk_tex;
    public static ResourceLocation mp_f_15_kerosene_polite_tex;
    public static ResourceLocation mp_f_15_kerosene_blackjack_tex;
    public static ResourceLocation mp_f_15_kerosene_lambda_tex;
    public static ResourceLocation mp_f_15_kerosene_minuteman_tex;
    public static ResourceLocation mp_f_15_kerosene_pip_tex;
    public static ResourceLocation mp_f_15_kerosene_taint_tex;
    public static ResourceLocation mp_f_15_kerosene_yuck_tex;
    public static ResourceLocation mp_f_15_solid_tex;
    public static ResourceLocation mp_f_15_solid_insulation_tex;
    public static ResourceLocation mp_f_15_solid_desh_tex;
    public static ResourceLocation mp_f_15_solid_soviet_glory_tex;
    public static ResourceLocation mp_f_15_solid_soviet_stank_tex;
    public static ResourceLocation mp_f_15_solid_faust_tex;
    public static ResourceLocation mp_f_15_solid_silvermoonlight_tex;
    public static ResourceLocation mp_f_15_solid_snowy_tex;
    public static ResourceLocation mp_f_15_solid_panorama_tex;
    public static ResourceLocation mp_f_15_solid_roses_tex;
    public static ResourceLocation mp_f_15_solid_mimi_tex;
    public static ResourceLocation mp_f_15_hydrogen_tex;
    public static ResourceLocation mp_f_15_hydrogen_cathedral_tex;
    public static ResourceLocation mp_f_15_balefire_tex;
    public static ResourceLocation mp_f_15_20_kerosene_tex;
    public static ResourceLocation mp_f_15_20_kerosene_magnusson_tex;
    public static ResourceLocation mp_f_15_20_solid_tex;

    // Missile Parts - Warheads
    public static ResourceLocation mp_w_10_he_tex;
    public static ResourceLocation mp_w_10_incendiary_tex;
    public static ResourceLocation mp_w_10_buster_tex;
    public static ResourceLocation mp_w_10_nuclear_tex;
    public static ResourceLocation mp_w_10_nuclear_large_tex;
    public static ResourceLocation mp_w_10_taint_tex;
    public static ResourceLocation mp_w_10_cloud_tex;
    public static ResourceLocation mp_w_15_he_tex;
    public static ResourceLocation mp_w_15_incendiary_tex;
    public static ResourceLocation mp_w_15_nuclear_tex;
    public static ResourceLocation mp_w_15_nuclear_shark_tex;
    public static ResourceLocation mp_w_15_nuclear_mimi_tex;
    public static ResourceLocation mp_w_15_n2_tex;
    public static ResourceLocation mp_w_15_balefire_tex;
    public static ResourceLocation mp_w_15_turbine_tex;

    public static ResourceLocation universal_tex;

    public static ResourceLocation dud_balefire_tex;
    public static ResourceLocation dud_conventional_tex;
    public static ResourceLocation dud_nuke_tex;
    public static ResourceLocation dud_salted_tex;

    public static ResourceLocation turret_sentry_tex;
    public static ResourceLocation turret_sentry_damaged_tex;

    public static ResourceLocation maskman_tex;
    public static ResourceLocation maskman_iou_tex;
    public static ResourceLocation ufo_tex;
    public static ResourceLocation crystallizer_tex;

    public static ResourceLocation deco_crt_clean_tex;
    public static ResourceLocation deco_crt_broken_tex;
    public static ResourceLocation deco_crt_blinking_tex;
    public static ResourceLocation deco_crt_bsod_tex;
    public static ResourceLocation dornier_1_tex;
    public static ResourceLocation dornier_2_tex;
    public static ResourceLocation dornier_4_tex;
    public static ResourceLocation b29_0_tex;
    public static ResourceLocation b29_1_tex;
    public static ResourceLocation b29_2_tex;
    public static ResourceLocation b29_3_tex;
    public static ResourceLocation building_tex;
    public static ResourceLocation torpedo_tex;
    public static ResourceLocation sat_foeq_tex;
    public static ResourceLocation sat_foeq_burning_tex;
    public static ResourceLocation sat_radar_tex;
    public static ResourceLocation sat_resonator_tex;
    public static ResourceLocation sat_scanner_tex;
    public static ResourceLocation sat_mapper_tex;
    public static ResourceLocation sat_laser_tex;
    public static ResourceLocation sat_base_tex;
    public static ResourceLocation sat_dock_tex;
    public static ResourceLocation miner_rocket_tex;
    public static ResourceLocation tomblast_tex;
    public static ResourceLocation tom_flame_tex;
    public static ResourceLocation tom_main_tex;
    public static ResourceLocation zirnox_destroyed_tex;
    public static ResourceLocation tesla_tex;
    public static ResourceLocation snowglobe_socket_tex;
    public static ResourceLocation snowglobe_glass_tex;
    public static ResourceLocation snowglobe_features_tex;
    // Missiles Tier 0
    public static ResourceLocation missileMicroTest_tex;
    public static ResourceLocation missileMicroTaint_tex;
    public static ResourceLocation missileMicro_tex;
    public static ResourceLocation missileMicroBHole_tex;
    public static ResourceLocation missileMicroSchrab_tex;
    public static ResourceLocation missileMicroEMP_tex;

    // Missiles Tier 1
    public static ResourceLocation missileV2_HE_tex;
    public static ResourceLocation missileV2_IN_tex;
    public static ResourceLocation missileV2_CL_tex;
    public static ResourceLocation missileV2_BU_tex;
    public static ResourceLocation missileV2_decoy_tex;
    public static ResourceLocation missileAA_tex;

    // Missiles Tier 2
    public static ResourceLocation missileStrong_HE_tex;
    public static ResourceLocation missileStrong_IN_tex;
    public static ResourceLocation missileStrong_CL_tex;
    public static ResourceLocation missileStrong_BU_tex;
    public static ResourceLocation missileStrong_EMP_tex;

    // Missiles Tier 3
    public static ResourceLocation missileHuge_HE_tex;
    public static ResourceLocation missileHuge_IN_tex;
    public static ResourceLocation missileHuge_CL_tex;
    public static ResourceLocation missileHuge_BU_tex;

    // Missiles Tier 4
    public static ResourceLocation missileNuclear_tex;
    public static ResourceLocation missileThermo_tex;
    public static ResourceLocation missileVolcano_tex;
    public static ResourceLocation missileDoomsday_tex;
    public static ResourceLocation missileDoomsdayRusted_tex;

    public static ResourceLocation missileStealth_tex;
    public static ResourceLocation missileShuttle_tex;
    public static ResourceLocation launch_pad_tex;
    public static ResourceLocation solar_boiler_tex;
    public static ResourceLocation solar_mirror_tex;
    public static ResourceLocation compact_launcher_tex;

    public static ResourceLocation launch_table_base_tex;
    public static ResourceLocation launch_table_small_pad_tex;
    public static ResourceLocation launch_table_large_pad_tex;

    public static ResourceLocation launch_table_small_scaffold_base_tex;
    public static ResourceLocation launch_table_small_scaffold_connector_tex;

    public static ResourceLocation launch_table_large_scaffold_base_tex;
    public static ResourceLocation launch_table_large_scaffold_connector_tex;
    public static ResourceLocation launch_pad_silo_tex;
    public static ResourceLocation radar_base_tex;
    public static ResourceLocation radar_dish_tex;
    public static ResourceLocation radar_screen_tex;
    public static ResourceLocation difurnace_top_off_alt;
    public static ResourceLocation difurnace_top_on_alt;
    public static ResourceLocation brick_fire;
    public static ResourceLocation difurnace_extension_tex;
    public static ResourceLocation machine_blast_furnace_tex;
    public static ResourceLocation machine_blast_furnace_lit_tex;


    // Модели
    public static HFRWavefrontObject file_cabinet;
    public static HFRWavefrontObject steel_beam;
    public static HFRWavefrontObject steel_grate;
    public static HFRWavefrontObject steel_grate_wide;
    public static HFRWavefrontObject steel_scaffold;
    public static HFRWavefrontObject anvil;
    public static HFRWavefrontObject press_body;
    public static HFRWavefrontObject press_head;
    public static HFRWavefrontObject barrel;
    public static HFRWavefrontObject pepperbox;
    public static HFRWavefrontObject maresleg;
    public static HFRWavefrontObject spas_12;
    public static HFRWavefrontObject casings;
    public static HFRWavefrontObject double_barrel;
    public static HFRWavefrontObject liberator;
    public static HFRWavefrontObject sexy;
    public static HFRWavefrontObject whiskey;
    public static HFRWavefrontObject shredder;
    public static HFRWavefrontObject heretic;
    public static HFRWavefrontObject greasegun;
    public static HFRWavefrontObject uzi;
    public static HFRWavefrontObject lag;
    public static HFRWavefrontObject am180;
    public static HFRWavefrontObject bio_revolver;
    public static HFRWavefrontObject henry;
    public static HFRWavefrontObject lilmac;
    public static HFRWavefrontObject hangman;
    public static HFRWavefrontObject amat;
    public static HFRWavefrontObject g3;
    public static HFRWavefrontObject m2;
    public static HFRWavefrontObject stg77;
    public static HFRWavefrontObject bolter;
    public static HFRWavefrontObject carbine;
    public static HFRWavefrontObject minigun;
    public static HFRWavefrontObject mas36;
    public static HFRWavefrontObject aberrator;
    public static HFRWavefrontObject tesla_cannon;
    public static HFRWavefrontObject yomi;
    public static HFRWavefrontObject horse;
    public static HFRWavefrontObject hundun;
    public static HFRWavefrontObject laser_pistol;
    public static HFRWavefrontObject lasrifle;
    public static HFRWavefrontObject lasrifle_mods;
    public static HFRWavefrontObject congolake;
    public static HFRWavefrontObject flamethrower;
    public static HFRWavefrontObject flaregun;
    public static HFRWavefrontObject projectiles;
    public static HFRWavefrontObject tau;
    public static HFRWavefrontObject coilgun;
    public static HFRWavefrontObject n_i_4_n_i;
    public static HFRWavefrontObject chemthrower;
    public static HFRWavefrontObject missile_launcher;
    public static HFRWavefrontObject panzerschreck;
    public static HFRWavefrontObject stinger;
    public static HFRWavefrontObject quadro;
    public static HFRWavefrontObject chip;
    public static HFRWavefrontObject c130;
    public static HFRWavefrontObject supply_crate;
    public static HFRWavefrontObject soyuz_lander;
    public static HFRWavefrontObject grenade_frag;
    public static HFRWavefrontObject grenade_aschrab;
    public static HFRWavefrontObject sphere;
    public static HFRWavefrontObject armor_goggles;
    public static HFRWavefrontObject armor_bj;
    public static HFRWavefrontObject fluid_tank;
    public static HFRWavefrontObject fluid_tank_exploded;
    public static HFRWavefrontObject fluid_duct;
    public static HFRWavefrontObject heater_oven;
    public static HFRWavefrontObject heater_firebox;
    public static HFRWavefrontObject heater_oilburner;
    public static HFRWavefrontObject furnace_steel;
    public static HFRWavefrontObject furnace_iron;
    public static HFRWavefrontObject furnace_combination;
    public static HFRWavefrontObject sawmill;
    public static HFRWavefrontObject heat_boiler;
    public static HFRWavefrontObject heat_boiler_burst;
    public static HFRWavefrontObject autosaw;
    public static HFRWavefrontObject boat;
    public static HFRWavefrontObject boxcar;
    public static HFRWavefrontObject mine_ap;
    public static HFRWavefrontObject mine_he;
    public static HFRWavefrontObject mine_fat;
    public static HFRWavefrontObject mine_naval;
    public static HFRWavefrontObject taperecorder;
    public static HFRWavefrontObject pole;
    public static HFRWavefrontObject armor_no9;
    public static HFRWavefrontObject armor_t51;
    public static HFRWavefrontObject armor_ajr;
    public static HFRWavefrontObject armor_steamsuit;
    public static HFRWavefrontObject armor_dieselsuit;
    public static HFRWavefrontObject armor_hev;
    public static HFRWavefrontObject armor_fau;
    public static HFRWavefrontObject armor_dnt;
    public static HFRWavefrontObject armor_remnant;
    public static HFRWavefrontObject armor_envsuit;
    public static HFRWavefrontObject armor_trenchmaster;
    public static HFRWavefrontObject armor_hat;
    public static HFRWavefrontObject shimmer_sledge;
    public static HFRWavefrontObject shimmer_axe;
    public static HFRWavefrontObject stopsign;
    public static HFRWavefrontObject gavel;
    public static HFRWavefrontObject nuke_custom;
    public static HFRWavefrontObject cable_neo;
    public static HFRWavefrontObject cage_lamp;
    public static HFRWavefrontObject fluorescent_lamp;
    public static HFRWavefrontObject flood_lamp;
    public static HFRWavefrontObject floodlight;
    public static HFRWavefrontObject cargo_container;
    public static HFRWavefrontObject dieselgen;
    public static HFRWavefrontObject gascent;
    public static HFRWavefrontObject centrifuge;
    public static HFRWavefrontObject pump;
    public static HFRWavefrontObject stirling;
    public static HFRWavefrontObject steam_engine;
    public static HFRWavefrontObject connector;
    public static HFRWavefrontObject rotary_furnace;
    public static HFRWavefrontObject machine_crucible;
    public static HFRWavefrontObject foundry_mold;
    public static HFRWavefrontObject foundry_basin;
    public static HFRWavefrontObject foundry_channel;
    public static HFRWavefrontObject foundry_outlet;
    public static HFRWavefrontObject soldering_station;
    public static HFRWavefrontObject arc_welder;
    public static HFRWavefrontObject ring;
    public static HFRWavefrontObject fatman;
    public static HFRWavefrontObject folly;
    public static HFRWavefrontObject charge_thrower;
    public static HFRWavefrontObject fireext;
    public static HFRWavefrontObject n2;

    // Missile Parts - Thrusters Size 10
    public static HFRWavefrontObject mp_t_10_kerosene;
    public static HFRWavefrontObject mp_t_10_solid;
    public static HFRWavefrontObject mp_t_10_xenon;

    // Missile Parts - Thrusters Size 15
    public static HFRWavefrontObject mp_t_15_kerosene;
    public static HFRWavefrontObject mp_t_15_kerosene_dual;
    public static HFRWavefrontObject mp_t_15_kerosene_triple;
    public static HFRWavefrontObject mp_t_15_solid;
    public static HFRWavefrontObject mp_t_15_solid_hexadecuple;
    public static HFRWavefrontObject mp_t_15_balefire_short;
    public static HFRWavefrontObject mp_t_15_balefire;
    public static HFRWavefrontObject mp_t_15_balefire_large;

    // Missile Parts - Thrusters Size 20
    public static HFRWavefrontObject mp_t_20_kerosene;
    public static HFRWavefrontObject mp_t_20_kerosene_dual;
    public static HFRWavefrontObject mp_t_20_kerosene_triple;
    public static HFRWavefrontObject mp_t_20_solid;
    public static HFRWavefrontObject mp_t_20_solid_multi;

    // Missile Parts - Stability (Fins)
    public static HFRWavefrontObject mp_s_10_flat;
    public static HFRWavefrontObject mp_s_10_cruise;
    public static HFRWavefrontObject mp_s_10_space;
    public static HFRWavefrontObject mp_s_15_flat;
    public static HFRWavefrontObject mp_s_15_thin;
    public static HFRWavefrontObject mp_s_15_soyuz;
    public static HFRWavefrontObject mp_s_20;

    // Missile Parts - Fuselages
    public static HFRWavefrontObject mp_f_10_kerosene;
    public static HFRWavefrontObject mp_f_10_long_kerosene;
    public static HFRWavefrontObject mp_f_10_15_kerosene;
    public static HFRWavefrontObject mp_f_15_kerosene;
    public static HFRWavefrontObject mp_f_15_hydrogen;
    public static HFRWavefrontObject mp_f_15_20_kerosene;

    // Missile Parts - Warheads Size 10
    public static HFRWavefrontObject mp_w_10_he;
    public static HFRWavefrontObject mp_w_10_incendiary;
    public static HFRWavefrontObject mp_w_10_buster;
    public static HFRWavefrontObject mp_w_10_nuclear;
    public static HFRWavefrontObject mp_w_10_nuclear_large;
    public static HFRWavefrontObject mp_w_10_taint;

    // Missile Parts - Warheads Size 15
    public static HFRWavefrontObject mp_w_15_he;
    public static HFRWavefrontObject mp_w_15_incendiary;
    public static HFRWavefrontObject mp_w_15_nuclear;
    public static HFRWavefrontObject mp_w_15_boxcar;
    public static HFRWavefrontObject mp_w_15_n2;
    public static HFRWavefrontObject mp_w_15_balefire;
    public static HFRWavefrontObject mp_w_15_turbine;

    public static HFRWavefrontObject dud_balefire;
    public static HFRWavefrontObject dud_conventional;
    public static HFRWavefrontObject dud_nuke;
    public static HFRWavefrontObject dud_salted;

    public static HFRWavefrontObject turret_sentry;

    public static HFRWavefrontObject maskman;
    public static HFRWavefrontObject ufo;
    public static HFRWavefrontObject drone;
    public static HFRWavefrontObject glyphid;
    public static HFRWavefrontObject crystallizer;
    public static HFRWavefrontObject deco_crt;
    public static HFRWavefrontObject dornier;
    public static HFRWavefrontObject b29;
    public static HFRWavefrontObject bomblet;
    public static HFRWavefrontObject building;
    public static HFRWavefrontObject torpedo;
    public static HFRWavefrontObject sat_foeq;
    public static HFRWavefrontObject sat_foeq_burning;
    public static HFRWavefrontObject sat_foeq_fire;
    public static HFRWavefrontObject sat_base;
    public static HFRWavefrontObject sat_dock;
    public static HFRWavefrontObject sat_radar;
    public static HFRWavefrontObject sat_resonator;
    public static HFRWavefrontObject sat_scanner;
    public static HFRWavefrontObject sat_mapper;
    public static HFRWavefrontObject sat_laser;
    public static HFRWavefrontObject miner_rocket;
    public static HFRWavefrontObject tom_flame;
    public static HFRWavefrontObject tom_main;
    public static HFRWavefrontObject zirnox_destroyed;
    public static HFRWavefrontObject capacitor;
    public static HFRWavefrontObject tesla;
    public static HFRWavefrontObject armor_mod_tesla;
    public static HFRWavefrontObject snowglobe;
    public static HFRWavefrontObject missileV2;
    public static HFRWavefrontObject missileABM;
    public static HFRWavefrontObject missileStealth;
    public static HFRWavefrontObject missileStrong;
    public static HFRWavefrontObject missileHuge;
    public static HFRWavefrontObject missileNuclear;
    public static HFRWavefrontObject missileMicro;
    public static HFRWavefrontObject missileShuttle;
    public static HFRWavefrontObject launch_pad;
    public static HFRWavefrontObject solar_boiler;
    public static HFRWavefrontObject solar_mirror;
    public static HFRWavefrontObject compact_launcher;

    public static HFRWavefrontObject launch_table_base;
    public static HFRWavefrontObject launch_table_small_pad;
    public static HFRWavefrontObject launch_table_large_pad;

    public static HFRWavefrontObject launch_table_small_scaffold_base;
    public static HFRWavefrontObject launch_table_small_scaffold_connector;
    public static HFRWavefrontObject launch_table_small_scaffold_empty;

    public static HFRWavefrontObject launch_table_large_scaffold_base;
    public static HFRWavefrontObject launch_table_large_scaffold_connector;
    public static HFRWavefrontObject launch_table_large_scaffold_empty;
    public static HFRWavefrontObject launch_pad_silo;
    public static HFRWavefrontObject radar;
    public static HFRWavefrontObject radar_screen;
    public static HFRWavefrontObject difurnace_extension;
    public static HFRWavefrontObject blast_furnace;
    /**
     * Инициализация всех ресурсов
     */
    public static void init() {
        initTextures();
        initModels();
    }

    /**
     * Загрузка всех текстур
     */
    private static void initTextures() {
        file_cabinet_tex = getTexture("block/storage/filing_cabinet");
        file_cabinet_steel_tex = getTexture("block/storage/filing_cabinet_steel");
        steel_beam_tex = getTexture("block/deco/steel_beam");
        steel_grate_tex = getTexture("block/deco/steel_grate");
        steel_grate_wide_tex = getTexture("block/deco/steel_grate_wide");
        steel_scaffold_tex = getTexture("block/deco/steel_scaffold");
        anvil_iron_tex = getTexture("block/machines/anvil_iron");
        anvil_lead_tex = getTexture("block/machines/anvil_lead");
        anvil_steel_tex = getTexture("block/machines/anvil_steel");
        anvil_arsenic_bronze_tex = getTexture("block/machines/anvil_arsenic_bronze");
        anvil_bismuth_bronze_tex = getTexture("block/machines/anvil_bismuth_bronze");
        anvil_desh_tex = getTexture("block/machines/anvil_desh");
        anvil_dnt_tex = getTexture("block/machines/anvil_dnt");
        anvil_ferrouranium_tex = getTexture("block/machines/anvil_ferrouranium");
        anvil_murky_tex = getTexture("block/machines/anvil_murky");
        anvil_osmiridium_tex = getTexture("block/machines/anvil_osmiridium");
        anvil_saturnite_tex = getTexture("block/machines/anvil_saturnite");
        anvil_schrabidate_tex = getTexture("block/machines/anvil_schrabidate");
        press_body_tex = getTexture("block/machines/press_body");
        press_head_tex = getTexture("block/machines/press_head");
        barrel_iron_tex = getTexture("block/storage/barrel_iron");
        barrel_steel_tex = getTexture("block/storage/barrel_steel");
        barrel_plastic_tex = getTexture("block/storage/barrel_plastic");
        barrel_corroded_tex = getTexture("block/storage/barrel_corroded");
        barrel_tcalloy_tex = getTexture("block/storage/barrel_tcalloy");
        barrel_antimatter_tex = getTexture("block/storage/barrel_antimatter");
        barrel_lox_tex = getTexture("block/lox_barrel");
        barrel_taint_tex = getTexture("block/taint_barrel");
        barrel_pink_tex = getTexture("block/pink_barrel");
        barrel_red_tex = getTexture("block/red_barrel");
        barrel_yellow_tex = getTexture("block/yellow_barrel");
        pepperbox_tex = getTexture("item/weapon/gun_pepperbox");
        maresleg_tex = getTexture("item/weapon/gun_maresleg");
        maresleg_broken_tex = getTexture("item/weapon/gun_maresleg_broken");
        spas_12_tex = getTexture("item/weapon/gun_spas12");
        casings_tex = getTexture("particles/casings");
        casings_base_tex = getTexture("particles/casings_base");
        double_barrel_tex = getTexture("item/weapon/gun_double_barrel");
        sacred_dragon_tex = getTexture("item/weapon/gun_double_barrel_sacred_dragon");
        liberator_tex = getTexture("item/weapon/gun_liberator");
        sexy_tex = getTexture("item/weapon/gun_autoshotgun_sexy");
        whiskey_tex = getTexture("item/weapon/whiskey");
        heretic_tex = getTexture("item/weapon/gun_autoshotgun_heretic");
        shredder_tex = getTexture("item/weapon/gun_autoshotgun_shredder");
        autoshotgun_shredder_tex = getTexture("item/weapon/gun_autoshotgun_shredder_orig");
        greasegun_tex = getTexture("item/weapon/gun_greasegun");
        greasegun_clean_tex = getTexture("item/weapon/gun_greasegun_clean");
        uzi_tex = getTexture("item/weapon/gun_uzi");
        uzi_saturnite_tex = getTexture("item/weapon/gun_uzi_saturnite");
        lag_tex = getTexture("item/weapon/gun_lag");
        am180_tex = getTexture("item/weapon/gun_am180");
        light_revolver_tex = getTexture("item/weapon/gun_light_revolver");
        light_revolver_atlas_tex = getTexture("item/weapon/gun_light_revolver_atlas");
        henry_tex = getTexture("item/weapon/gun_henry");
        henry_lincoln_tex = getTexture("item/weapon/gun_henry_lincoln");
        heavy_revolver_tex = getTexture("item/weapon/gun_heavy_revolver");
        lilmac_tex = getTexture("item/weapon/gun_lilmac");
        heavy_revolver_protege_tex = getTexture("item/weapon/gun_heavy_revolver_protege");
        lilmac_scope_tex = getTexture("item/weapon/lilmac_scope");
        hangman_tex = getTexture("item/weapon/gun_hangman");
        amat_tex = getTexture("item/weapon/gun_amat");
        amat_subtlety_tex = getTexture("item/weapon/gun_amat_subtlety");
        amat_penance_tex = getTexture("item/weapon/gun_amat_penance");
        g3_attachments_tex = getTexture("item/weapon/g3_attachments");
        m2_tex = getTexture("item/weapon/gun_m2");
        g3_tex = getTexture("item/weapon/gun_g3");
        g3_zebra_tex = getTexture("item/weapon/gun_g3_zebra");
        g3_green_tex = getTexture("item/weapon/gun_g3_green");
        g3_black_tex = getTexture("item/weapon/gun_g3_black");
        stg77_tex = getTexture("item/weapon/gun_stg77");
        bolter_tex = getTexture("item/weapon/gun_bolter");
        carbine_tex = getTexture("item/weapon/gun_carbine");
        carbine_bayonet_tex = getTexture("item/weapon/carbine_bayonet");
        minigun_tex = getTexture("item/weapon/gun_minigun");
        minigun_lacunae_tex = getTexture("item/weapon/gun_minigun_lacunae");
        mas36_tex = getTexture("item/weapon/gun_mas36");
        aberrator_tex = getTexture("item/weapon/gun_aberrator");
        yomi_tex = getTexture("models/trinkets/yomi");
        hundun_tex = getTexture("models/trinkets/hundun");
        tesla_cannon_tex = getTexture("item/weapon/gun_tesla_cannon");
        laser_pistol_tex = getTexture("item/weapon/gun_laser_pistol");
        laser_pistol_pew_pew_tex = getTexture("item/weapon/gun_laser_pistol_pew_pew");
        laser_pistol_morning_glory_tex = getTexture("item/weapon/gun_laser_pistol_morning_glory");
        lasrifle_tex = getTexture("item/weapon/gun_lasrifle");
        lasrifle_mods_tex = getTexture("item/weapon/lasrifle_mods");
        congolake_tex = getTexture("item/weapon/gun_congolake");
        flamethrower_tex = getTexture("item/weapon/gun_flamethrower");
        flamethrower_topaz_tex = getTexture("item/weapon/gun_flamethrower_topaz");
        flamethrower_daybreaker_tex = getTexture("item/weapon/gun_flamethrower_daybreaker");
        flaregun_tex = getTexture("item/weapon/gun_flaregun");
        grenade_tex = getTexture("item/weapon/grenade");
        tau_tex = getTexture("item/weapon/gun_tau");
        coilgun_tex = getTexture("item/weapon/gun_coilgun");
        n_i_4_n_i_tex = getTexture("item/weapon/gun_n_i_4_n_i");
        n_i_4_n_i_greyscale_tex = getTexture("item/weapon/gun_n_i_4_n_i_greyscale");
        chemthrower_tex = getTexture("item/weapon/gun_chemthrower");
        missile_launcher_tex = getTexture("item/weapon/gun_missile_launcher");
        quadro_tex = getTexture("item/weapon/gun_quadro");
        quadro_rocket_tex = getTexture("item/weapon/quadro_rocket");
        stinger_tex = getTexture("item/weapon/gun_stinger");
        panzerschreck_tex = getTexture("item/weapon/gun_panzerschreck");
        rocket_tex = getTexture("item/weapon/rocket");
        chip_gold_tex = getTexture("models/trinkets/chip_gold");
        c130_0_tex = getTexture("models/aircraft/c130_0");
        supply_crate_tex = getTexture("block/block_supply_crate");
        soyuz_chute_tex = getTexture("models/soyuz_capsule/soyuz_chute");
        soyuz_lander_tex = getTexture("models/soyuz_capsule/soyuz_lander");
        grenade_mk2_tex = getTexture("item/weapon/grenade/grenade_mk2_model");
        grenade_aschrab_tex = getTexture("item/weapon/grenade/grenade_aschrab_model");
        blast_fleija_tex = getTexture("entity/blast_fleija");
        ash_glasses_tex = getTexture("models/armor/ash_glasses");
        fluid_tank_tex = getTexture("block/storage/fluid_tank");
        fluid_tank_inner_tex = getTexture("block/storage/fluid_tank_inner");
        ashpit_tex = getTexture("block/machines/ashpit");
        heater_firebox_tex = getTexture("block/machines/heater_firebox");
        heater_oven_tex = getTexture("block/machines/heater_oven");
        heater_oilburner_tex = getTexture("block/machines/heater_oilburner");
        furnace_steel_tex = getTexture("block/machines/furnace_steel");
        furnace_iron_tex = getTexture("block/machines/furnace_iron");
        furnace_combination_tex = getTexture("block/machines/furnace_combination");
        sawmill_tex = getTexture("block/machines/sawmill");
        heat_boiler_tex = getTexture("block/machines/heat_boiler");
        autosaw_tex = getTexture("block/machines/autosaw");
        boat_tex = getTexture("block/boat");
        boxcar_tex = getTexture("block/boxcar");
        mine_ap_grass_tex = getTexture("block/bomb/mine/mine_ap_grass");
        mine_ap_stone_tex = getTexture("block/bomb/mine/mine_ap_stone");
        mine_ap_snow_tex = getTexture("block/bomb/mine/mine_ap_snow");
        mine_ap_desert_tex = getTexture("block/bomb/mine/mine_ap_desert");
        mine_he_tex = getTexture("block/bomb/mine/mine_he");
        mine_shrap_tex = getTexture("block/bomb/mine/mine_shrap");
        mine_fat_tex = getTexture("block/bomb/mine/mine_fat");
        mine_naval_tex = getTexture("block/bomb/mine/mine_naval");
        taperecorder_tex = getTexture("block/deco/tape_recorder");
        pole_tex = getTexture("block/deco/steel_beam");
        no9_tex = getTexture("models/armor/armor_no9");
        no9_insignia_tex = getTexture("models/armor/armor_no9_insignia");
        shimmer_sledge_tex = getTexture("models/weapon/shimmer_axe");
        shimmer_axe_tex = getTexture("models/weapon/shimmer_axe");
        stopsign_tex = getTexture("models/weapon/stopsign");
        sopsign_tex = getTexture("models/weapon/sopsign");
        chernobylsign_tex = getTexture("models/weapon/chernobylsign");
        gavel_wood_tex = getTexture("models/weapon/gavel_wood");
        gavel_lead_tex = getTexture("models/weapon/gavel_lead");
        gavel_diamond_tex = getTexture("models/weapon/gavel_diamond");
        nuke_custom_tex = getTexture("block/bomb/nuke_custom");
        n2_tex = getTexture("block/bomb/n2");
        det_cord_tex = getTexture("block/det_cord");
        floodlight_tex = getTexture("block/floodlight");
        spotlight_incandescent_tex = getTexture("block/spotlight_incandescent");
        spotlight_incandescent_off_tex = getTexture("block/spotlight_incandescent_off");
        fluorescent_lamp_tex = getTexture("block/spotlight_fluoro");
        fluorescent_lamp_off_tex = getTexture("block/spotlight_fluoro_off");
        flood_lamp_tex = getTexture("block/spotlight_halogen");
        flood_lamp_off_tex = getTexture("block/spotlight_halogen_off");
        cargo_container_tex = getTexture("block/storage/cargo_container");
        diesel_gen_tex = getTexture("block/machines/machine_diesel");
        gascent_tex = getTexture("block/machines/machine_gas_cent");
        centrifuge_tex = getTexture("block/machines/machine_centrifuge");
        red_cable_tex = getTexture("block/red_cable");
        pump_steam_tex = getTexture("block/machines/pump_steam");
        pump_electric_tex = getTexture("block/machines/pump_electric");
        stirling_tex = getTexture("block/machines/stirling");
        stirling_steel_tex = getTexture("block/machines/stirling_steel");
        stirling_creative_tex = getTexture("block/machines/stirling_creative");
        steam_engine_tex = getTexture("block/machines/machine_steam_engine");
        soldering_station_tex = getTexture("block/machines/machine_soldering_station");
        arc_welder_tex = getTexture("block/machines/machine_arc_welder");
        emp_blast_tex = getTexture("entity/emp_blast");
        fatman_tex = getTexture("item/weapon/gun_fatman");
        fatman_mininuke_tex = getTexture("item/weapon/gun_fatman_mininuke");
        fatman_balefire_tex = getTexture("item/weapon/gun_fatman_balefire");
        folly_tex = getTexture("item/weapon/gun_folly");
        charge_thrower_tex = getTexture("item/weapon/gun_charge_thrower");
        charge_thrower_hook_tex = getTexture("item/weapon/charge_thrower_hook");
        charge_thrower_mortar_tex = getTexture("item/weapon/charge_thrower_mortar");
        fireext_tex = getTexture("item/weapon/fireext");
        fireext_foam_tex = getTexture("item/weapon/fireext_foam");
        fireext_sand_tex = getTexture("item/weapon/fireext_sand");

        // Thrusters Size 10
        mp_t_10_kerosene_tex = getTexture("models/missile_parts/thrusters/mp_t_10_kerosene");
        mp_t_10_solid_tex = getTexture("models/missile_parts/thrusters/mp_t_10_solid");
        mp_t_10_xenon_tex = getTexture("models/missile_parts/thrusters/mp_t_10_xenon");

        // Thrusters Size 15
        mp_t_15_kerosene_tex = getTexture("models/missile_parts/thrusters/mp_t_15_kerosene");
        mp_t_15_kerosene_dual_tex = getTexture("models/missile_parts/thrusters/mp_t_15_kerosene_dual");
        mp_t_15_solid_tex = getTexture("models/missile_parts/thrusters/mp_t_15_solid");
        mp_t_15_solid_hexadecuple_tex = getTexture("models/missile_parts/thrusters/mp_t_15_solid_hexdecuple");
        mp_t_15_hydrogen_tex = getTexture("models/missile_parts/thrusters/mp_t_15_hydrogen");
        mp_t_15_hydrogen_dual_tex = getTexture("models/missile_parts/thrusters/mp_t_15_hydrogen_dual");
        mp_t_15_balefire_short_tex = getTexture("models/missile_parts/thrusters/mp_t_15_balefire_short");
        mp_t_15_balefire_tex = getTexture("models/missile_parts/thrusters/mp_t_15_balefire");
        mp_t_15_balefire_large_tex = getTexture("models/missile_parts/thrusters/mp_t_15_balefire_large");
        mp_t_15_balefire_large_rad_tex = getTexture("models/missile_parts/thrusters/mp_t_15_balefire_large_rad");

        // Thrusters Size 20
        mp_t_20_kerosene_tex = getTexture("models/missile_parts/thrusters/mp_t_20_kerosene");
        mp_t_20_kerosene_dual_tex = getTexture("models/missile_parts/thrusters/mp_t_20_kerosene_dual");
        mp_t_20_solid_tex = getTexture("models/missile_parts/thrusters/mp_t_20_solid");
        mp_t_20_solid_multi_tex = getTexture("models/missile_parts/thrusters/mp_t_20_solid_multi");
        mp_t_20_solid_multier_tex = getTexture("models/missile_parts/thrusters/mp_t_20_solid_multier");

        // Stability
        mp_s_10_flat_tex = getTexture("models/missile_parts/stability/mp_s_10_flat");
        mp_s_10_cruise_tex = getTexture("models/missile_parts/stability/mp_s_10_cruise");
        mp_s_10_space_tex = getTexture("models/missile_parts/stability/mp_s_10_space");
        mp_s_15_flat_tex = getTexture("models/missile_parts/stability/mp_s_15_flat");
        mp_s_15_thin_tex = getTexture("models/missile_parts/stability/mp_s_15_thin");
        mp_s_15_soyuz_tex = getTexture("models/missile_parts/stability/mp_s_15_soyuz");

        // Fuselages
        mp_f_10_kerosene_tex = getTexture("models/missile_parts/fuselages/mp_f_10_kerosene");
        mp_f_10_kerosene_camo_tex = getTexture("models/missile_parts/fuselages/mp_f_10_kerosene_camo");
        mp_f_10_kerosene_desert_tex = getTexture("models/missile_parts/fuselages/mp_f_10_kerosene_desert");
        mp_f_10_kerosene_sky_tex = getTexture("models/missile_parts/fuselages/mp_f_10_kerosene_sky");
        mp_f_10_kerosene_flames_tex = getTexture("models/missile_parts/fuselages/mp_f_10_kerosene_flames");
        mp_f_10_kerosene_insulation_tex = getTexture("models/missile_parts/fuselages/mp_f_10_kerosene_insulation");
        mp_f_10_kerosene_sleek_tex = getTexture("models/missile_parts/fuselages/mp_f_10_kerosene_sleek");
        mp_f_10_kerosene_metal_tex = getTexture("models/missile_parts/fuselages/mp_f_10_kerosene_metal");
        mp_f_10_kerosene_taint_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_kerosene_taint");
        mp_f_10_solid_tex = getTexture("models/missile_parts/fuselages/mp_f_10_solid");
        mp_f_10_solid_flames_tex = getTexture("models/missile_parts/fuselages/mp_f_10_solid_flames");
        mp_f_10_solid_insulation_tex = getTexture("models/missile_parts/fuselages/mp_f_10_solid_insulation");
        mp_f_10_solid_sleek_tex = getTexture("models/missile_parts/fuselages/mp_f_10_solid_sleek");
        mp_f_10_solid_soviet_glory_tex = getTexture("models/missile_parts/fuselages/mp_f_10_solid_soviet_glory");
        mp_f_10_solid_moonlit_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_solid_moonlit");
        mp_f_10_solid_cathedral_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_solid_cathedral");
        mp_f_10_solid_battery_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_solid_battery");
        mp_f_10_solid_duracell_tex = getTexture("models/missile_parts/fuselages/mp_f_10_solid_duracell");
        mp_f_10_xenon_tex = getTexture("models/missile_parts/fuselages/mp_f_10_xenon");
        mp_f_10_xenon_bhole_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_xenon_bhole");
        mp_f_10_long_kerosene_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_kerosene");
        mp_f_10_long_kerosene_camo_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_kerosene_camo");
        mp_f_10_long_kerosene_desert_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_kerosene_desert");
        mp_f_10_long_kerosene_sky_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_kerosene_sky");
        mp_f_10_long_kerosene_flames_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_kerosene_flames");
        mp_f_10_long_kerosene_insulation_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_kerosene_insulation");
        mp_f_10_long_kerosene_sleek_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_kerosene_sleek");
        mp_f_10_long_kerosene_metal_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_kerosene_metal");
        mp_f_10_long_kerosene_dash_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_long_kerosene_dash");
        mp_f_10_long_kerosene_taint_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_long_kerosene_taint");
        mp_f_10_long_kerosene_vap_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_long_kerosene_vap");
        mp_f_10_long_solid_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_solid");
        mp_f_10_long_solid_flames_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_solid_flames");
        mp_f_10_long_solid_insulation_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_solid_insulation");
        mp_f_10_long_solid_sleek_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_solid_sleek");
        mp_f_10_long_solid_soviet_glory_tex = getTexture("models/missile_parts/fuselages/mp_f_10_long_solid_soviet_glory");
        mp_f_10_long_solid_bullet_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_long_solid_bullet");
        mp_f_10_long_solid_silvermoonlight_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_10_long_solid_silvermoonlight");
        mp_f_10_15_kerosene_tex = getTexture("models/missile_parts/fuselages/mp_f_10_15_kerosene");
        mp_f_10_15_solid_tex = getTexture("models/missile_parts/fuselages/mp_f_10_15_solid");
        mp_f_10_15_hydrogen_tex = getTexture("models/missile_parts/fuselages/mp_f_10_15_hydrogen");
        mp_f_10_15_balefire_tex = getTexture("models/missile_parts/fuselages/mp_f_10_15_balefire");
        mp_f_15_kerosene_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene");
        mp_f_15_kerosene_camo_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_camo");
        mp_f_15_kerosene_desert_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_desert");
        mp_f_15_kerosene_sky_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_sky");
        mp_f_15_kerosene_insulation_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_insulation");
        mp_f_15_kerosene_metal_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_metal");
        mp_f_15_kerosene_decorated_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_decorated");
        mp_f_15_kerosene_steampunk_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_steampunk");
        mp_f_15_kerosene_polite_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_polite");
        mp_f_15_kerosene_blackjack_tex = getTexture("models/missile_parts/fuselages/base/mp_f_15_kerosene_blackjack");
        mp_f_15_kerosene_lambda_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_15_kerosene_lambda");
        mp_f_15_kerosene_minuteman_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_15_kerosene_minuteman");
        mp_f_15_kerosene_pip_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_15_kerosene_pip");
        mp_f_15_kerosene_taint_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_15_kerosene_taint");
        mp_f_15_kerosene_yuck_tex = getTexture("models/missile_parts/fuselages/mp_f_15_kerosene_yuck");
        mp_f_15_solid_tex = getTexture("models/missile_parts/fuselages/mp_f_15_solid");
        mp_f_15_solid_insulation_tex = getTexture("models/missile_parts/fuselages/mp_f_15_solid_insulation");
        mp_f_15_solid_desh_tex = getTexture("models/missile_parts/fuselages/mp_f_15_solid_desh");
        mp_f_15_solid_soviet_glory_tex = getTexture("models/missile_parts/fuselages/mp_f_15_solid_soviet_glory");
        mp_f_15_solid_soviet_stank_tex = getTexture("models/missile_parts/fuselages/mp_f_15_solid_soviet_stank");
        mp_f_15_solid_faust_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_15_solid_faust");
        mp_f_15_solid_silvermoonlight_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_15_solid_silvermoonlight");
        mp_f_15_solid_snowy_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_15_solid_snowy");
        mp_f_15_solid_panorama_tex = getTexture("models/missile_parts/fuselages/mp_f_15_solid_panorama");
        mp_f_15_solid_roses_tex = getTexture("models/missile_parts/fuselages/mp_f_15_solid_roses");
        mp_f_15_solid_mimi_tex = getTexture("models/missile_parts/fuselages/mp_f_15_solid_mimi");
        mp_f_15_hydrogen_tex = getTexture("models/missile_parts/fuselages/mp_f_15_hydrogen");
        mp_f_15_hydrogen_cathedral_tex = getTexture("models/missile_parts/fuselages/contest/mp_f_15_hydrogen_cathedral");
        mp_f_15_balefire_tex = getTexture("models/missile_parts/fuselages/mp_f_15_balefire");
        mp_f_15_20_kerosene_tex = getTexture("models/missile_parts/fuselages/mp_f_15_20_kerosene");
        mp_f_15_20_kerosene_magnusson_tex = getTexture("models/missile_parts/fuselages/mp_f_15_20_kerosene_magnusson");
        mp_f_15_20_solid_tex = getTexture("models/missile_parts/fuselages/mp_f_15_20_solid");

        // Warheads
        mp_w_10_he_tex = getTexture("models/missile_parts/warheads/mp_w_10_he");
        mp_w_10_incendiary_tex = getTexture("models/missile_parts/warheads/mp_w_10_incendiary");
        mp_w_10_buster_tex = getTexture("models/missile_parts/warheads/mp_w_10_buster");
        mp_w_10_nuclear_tex = getTexture("models/missile_parts/warheads/mp_w_10_nuclear");
        mp_w_10_nuclear_large_tex = getTexture("models/missile_parts/warheads/mp_w_10_nuclear_large");
        mp_w_10_taint_tex = getTexture("models/missile_parts/warheads/mp_w_10_taint");
        mp_w_10_cloud_tex = getTexture("models/missile_parts/warheads/mp_w_10_cloud");
        mp_w_15_he_tex = getTexture("models/missile_parts/warheads/mp_w_15_he");
        mp_w_15_incendiary_tex = getTexture("models/missile_parts/warheads/mp_w_15_incendiary");
        mp_w_15_nuclear_tex = getTexture("models/missile_parts/warheads/mp_w_15_nuclear");
        mp_w_15_nuclear_shark_tex = getTexture("models/missile_parts/warheads/mp_w_15_nuclear_shark");
        mp_w_15_nuclear_mimi_tex = getTexture("models/missile_parts/warheads/mp_w_15_nuclear_mimi");
        mp_w_15_n2_tex = getTexture("models/missile_parts/warheads/mp_w_15_n2");
        mp_w_15_balefire_tex = getTexture("models/missile_parts/warheads/mp_w_15_balefire");
        mp_w_15_turbine_tex = getTexture("models/missile_parts/warheads/mp_w_15_turbine");

        universal_tex = getTexture("models/thegadget3");

        dud_balefire_tex = getTexture("block/bomb/dud_balefire");
        dud_nuke_tex = getTexture("block/bomb/dud_nuke");
        dud_conventional_tex = getTexture("block/bomb/conventional");
        dud_salted_tex = getTexture("block/bomb/dud_salted");
        turret_sentry_tex = getTexture("block/turrets/turret_sentry");
        turret_sentry_damaged_tex = getTexture("block/turrets/turret_sentry_damaged");

        maskman_tex = getTexture("entity/maskman");
        maskman_iou_tex = getTexture("entity/maskman_iou");
        ufo_tex = getTexture("entity/ufo");
        crystallizer_tex = getTexture("block/machines/machine_crystallizer");
        deco_crt_clean_tex = getTexture( "block/deco_crt");
        deco_crt_broken_tex = getTexture( "block/deco_crt_broken");
        deco_crt_blinking_tex = getTexture( "block/deco_crt_blinking");
        deco_crt_bsod_tex = getTexture( "block/deco_crt_bsod");
        dornier_1_tex = getTexture( "models/aircraft/dornier_1");
        dornier_2_tex = getTexture( "models/aircraft/dornier_2");
        dornier_4_tex = getTexture( "models/aircraft/dornier_4");
        b29_0_tex = getTexture( "models/aircraft/b29_0");
        b29_1_tex = getTexture( "models/aircraft/b29_1");
        b29_2_tex = getTexture( "models/aircraft/b29_2");
        b29_3_tex = getTexture( "models/aircraft/b29_3");
        building_tex = getTexture("item/weapon/building");
        torpedo_tex = getTexture("item/weapon/torpedo");
        sat_foeq_tex = getTexture("block/sat/sat_foeq");
        sat_foeq_burning_tex = getTexture("models/satellite/sat_foeq_burning");
        sat_radar_tex = getTexture("block/sat/sat_radar");
        sat_resonator_tex = getTexture("block/sat/sat_resonator");
        sat_scanner_tex = getTexture("block/sat/sat_scanner");
        sat_mapper_tex = getTexture("block/sat/sat_mapper");
        sat_laser_tex = getTexture("block/sat/sat_laser");
        sat_base_tex = getTexture("block/sat/sat_base");
        sat_dock_tex = getTexture("block/sat/sat_dock");
        miner_rocket_tex = getTexture("models/satellite/miner_rocket");
        tomblast_tex = getTexture("models/projectiles/tomblast");
        tom_flame_tex = getTexture("models/projectiles/tom_flame");
        tom_main_tex = getTexture("models/projectiles/tom_main");
        zirnox_destroyed_tex = getTexture("block/zirnox_destroyed");
        tesla_tex = getTexture("block/tesla");
        snowglobe_socket_tex = getTexture("block/snowglobe");
        snowglobe_glass_tex = getTexture("block/snowglobe_glass");
        snowglobe_features_tex = getTexture("block/snowglobe_features");
        // Tier 0
        missileMicroTest_tex = getTexture("models/missiles/missile_micro_test");
        missileMicroTaint_tex = getTexture("models/missiles/missile_micro_taint");
        missileMicro_tex = getTexture("models/missiles/missile_micro");
        missileMicroBHole_tex = getTexture("models/missiles/missile_micro_bhole");
        missileMicroSchrab_tex = getTexture("models/missiles/missile_micro_schrab");
        missileMicroEMP_tex = getTexture("models/missiles/missile_micro_emp");

        // Tier 1
        missileV2_HE_tex = getTexture("models/missiles/missile_v2_he");
        missileV2_IN_tex = getTexture("models/missiles/missile_v2_in");
        missileV2_CL_tex = getTexture("models/missiles/missile_v2_cl");
        missileV2_BU_tex = getTexture("models/missiles/missile_v2_bu");
        missileV2_decoy_tex = getTexture("models/missiles/missile_v2_decoy");
        missileAA_tex = getTexture("models/missiles/missile_abm");

        // Tier 2
        missileStrong_HE_tex = getTexture("models/missiles/missile_strong_he");
        missileStrong_IN_tex = getTexture("models/missiles/missile_strong_in");
        missileStrong_CL_tex = getTexture("models/missiles/missile_strong_cl");
        missileStrong_BU_tex = getTexture("models/missiles/missile_strong_bu");
        missileStrong_EMP_tex = getTexture("models/missiles/missile_strong_emp");

        // Tier 3
        missileHuge_HE_tex = getTexture("models/missiles/missile_huge_he");
        missileHuge_IN_tex = getTexture("models/missiles/missile_huge_in");
        missileHuge_CL_tex = getTexture("models/missiles/missile_huge_cl");
        missileHuge_BU_tex = getTexture("models/missiles/missile_huge_bu");

        // Tier 4
        missileNuclear_tex = getTexture("models/missiles/missile_nuclear");
        missileThermo_tex = getTexture("models/missiles/missile_thermo");
        missileVolcano_tex = getTexture("models/missiles/missile_volcano");
        missileDoomsday_tex = getTexture("models/missiles/missile_doomsday");
        missileDoomsdayRusted_tex = getTexture("models/missiles/missile_doomsday_rusted");

        // Special
        missileStealth_tex = getTexture("models/missiles/missile_stealth");
        missileShuttle_tex = getTexture("models/missiles/missile_shuttle");

        launch_pad_tex = getTexture("block/launch_pad");
        solar_boiler_tex = getTexture("block/machines/machine_solar_boiler");
        solar_mirror_tex = getTexture("block/machines/solar_mirror");
        compact_launcher_tex = getTexture("block/machines/compact_launcher");
        launch_table_base_tex = getTexture("block/machines/launch_table");
        launch_table_small_pad_tex = getTexture("block/machines/launch_table_small_pad");
        launch_table_large_pad_tex = getTexture("block/machines/launch_table_large_pad");

        launch_table_small_scaffold_base_tex = getTexture("block/machines/launch_table_small_scaffold_base");
        launch_table_small_scaffold_connector_tex = getTexture("block/machines/launch_table_small_scaffold_connector");

        launch_table_large_scaffold_base_tex = getTexture("block/machines/launch_table_large_scaffold_base");
        launch_table_large_scaffold_connector_tex = getTexture("block/machines/launch_table_large_scaffold_connector");
        launch_pad_silo_tex = getTexture("block/machines/launch_pad_rusted");
        radar_base_tex = getTexture("block/machines/machine_radar");
        radar_dish_tex = getTexture("block/machines/radar_dish");
        radar_screen_tex = getTexture("block/machines/radar_screen");

        difurnace_top_off_alt = getTexture("block/machines/machine_difurnace_top_off_alt");
        difurnace_top_on_alt = getTexture("block/machines/machine_difurnace_top_on_alt");
        brick_fire = getTexture("block/machines/brick_fire");
        difurnace_extension_tex = getTexture("block/machines/machine_difurnace_extension");
        machine_blast_furnace_lit_tex = getTexture("block/machines/machine_blast_furnace_lit");
        machine_blast_furnace_tex = getTexture("block/machines/machine_blast_furnace");
    }

    /**
     * Загрузка всех моделей
     */
    private static void initModels() {
        file_cabinet = getModel("models/block/storage/filing_cabinet.obj");
        steel_beam = getModel("models/block/deco/steel_beam.obj");
        steel_grate = getModel("models/block/deco/steel_grate.obj");
        steel_grate_wide = getModel("models/block/deco/steel_grate_wide.obj");
        steel_scaffold = getModel("models/block/deco/steel_scaffold.obj");
        anvil = getModel("models/block/machines/anvil.obj");
        press_body = getModel("models/block/machines/press.obj");
        press_head = getModel("models/block/machines/press_head.obj");
        barrel = getModel("models/block/storage/barrel.obj");
        pepperbox = getModel("models/item/weapon/gun_pepperbox.obj");
        maresleg = getModel("models/item/weapon/gun_maresleg.obj");
        spas_12 = getModel("models/item/weapon/gun_spas12.obj");
        casings = getModel("models/item/weapon/casings.obj");
        double_barrel = getModel("models/item/weapon/gun_double_barrel.obj");
        liberator = getModel("models/item/weapon/gun_liberator.obj");
        sexy = getModel("models/item/weapon/gun_autoshotgun_sexy.obj");
        whiskey = getModel("models/item/weapon/whiskey.obj");
        shredder = getModel("models/item/weapon/gun_shredder.obj");
        heretic = getModel("models/item/weapon/gun_autoshotgun_heretic.obj");
        greasegun = getModel("models/item/weapon/gun_greasegun.obj");
        uzi = getModel("models/item/weapon/gun_uzi.obj");
        lag = getModel("models/item/weapon/gun_lag.obj");
        am180 = getModel("models/item/weapon/gun_am180.obj");
        bio_revolver = getModel("models/item/weapon/gun_bio_revolver.obj");
        henry = getModel("models/item/weapon/gun_henry.obj");
        lilmac = getModel("models/item/weapon/gun_lilmac.obj");
        hangman = getModel("models/item/weapon/gun_hangman.obj");
        amat = getModel("models/item/weapon/gun_amat.obj");
        g3 = getModel("models/item/weapon/gun_g3.obj");
        m2 = getModel("models/item/weapon/gun_m2.obj");
        stg77 = getModel("models/item/weapon/gun_stg77.obj");
        bolter = getModel("models/item/weapon/gun_bolter.obj");
        carbine = getModel("models/item/weapon/gun_carbine.obj");
        minigun = getModel("models/item/weapon/gun_minigun.obj");
        mas36 = getModel("models/item/weapon/gun_mas36.obj");
        aberrator = getModel("models/item/weapon/gun_aberrator.obj");
        yomi = getModel("models/block/yomi.obj");
        horse = getModel("models/block/numbernine.obj");
        hundun = getModel("models/block/hundun.obj");
        laser_pistol = getModel("models/item/weapon/gun_laser_pistol.obj");
        lasrifle = getModel("models/item/weapon/gun_lasrifle.obj");
        lasrifle_mods = getModel("models/item/weapon/lasrifle_mods.obj");
        tesla_cannon = getModel("models/item/weapon/gun_tesla_cannon.obj");
        congolake = getModel("models/item/weapon/gun_congolake.obj");
        flamethrower = getModel("models/item/weapon/gun_flamethrower.obj");
        flaregun = getModel("models/item/weapon/gun_flaregun.obj");
        projectiles = getModel("models/projectiles/projectiles.obj");
        tau = getModel("models/item/weapon/gun_tau.obj");
        coilgun = getModel("models/item/weapon/gun_coilgun.obj");
        n_i_4_n_i = getModel("models/item/weapon/gun_n_i_4_n_i.obj");
        chemthrower = getModel("models/item/weapon/gun_chemthrower.obj");
        missile_launcher = getModel("models/item/weapon/gun_missile_launcher.obj");
        panzerschreck = getModel("models/item/weapon/gun_panzerschreck.obj");
        quadro = getModel("models/item/weapon/gun_quadro.obj");
        stinger = getModel("models/item/weapon/gun_stinger.obj");
        chip = getModel("models/item/trinkets/chip.obj");
        c130 = getModel("models/item/weapon/c130.obj");
        supply_crate = getModel("models/block/conserve_crate.obj");
        soyuz_lander = getModel("models/soyuz/soyuz_lander.obj");
        grenade_frag = getModel("models/item/weapon/grenade/grenade_frag.obj");
        grenade_aschrab = getModel("models/item/weapon/grenade/grenade_aschrab.obj");
        sphere = getModel("models/entity/sphere.obj");
        armor_goggles = getModel("models/item/armor/goggles.obj");
        armor_bj = getModel("models/item/armor/bj.obj");
        fluid_tank = getModel("models/block/storage/fluid_tank.obj");
        fluid_tank_exploded = getModel("models/block/storage/fluid_tank_exploded.obj");
        fluid_duct = getModel("models/block/storage/fluid_duct.obj");
        heater_oven = getModel("models/block/machines/heater_oven.obj");
        heater_firebox = getModel("models/block/machines/heater_firebox.obj");
        heater_oilburner = getModel("models/block/machines/heater_oilburner.obj");
        furnace_steel = getModel("models/block/machines/furnace_steel.obj");
        furnace_iron = getModel("models/block/machines/furnace_iron.obj");
        furnace_combination = getModel("models/block/machines/furnace_combination.obj");
        sawmill = getModel("models/block/machines/sawmill.obj");
        heat_boiler = getModel("models/block/machines/heat_boiler.obj");
        heat_boiler_burst = getModel("models/block/machines/heat_boiler_burst.obj");
        autosaw = getModel("models/block/machines/autosaw.obj");
        boat = getModel("models/block/boat.obj");
        boxcar = getModel("models/block/boxcar.obj");
        mine_ap = getModel("models/block/bomb/mine/mine_ap.obj");
        mine_he = getModel("models/block/bomb/mine/mine_he.obj");
        mine_fat = getModel("models/block/bomb/mine/mine_fat.obj");
        mine_naval = getModel("models/block/bomb/mine/mine_naval.obj");
        pole = getModel("models/block/deco/steel_poles.obj");
        taperecorder = getModel("models/block/deco/tape_recorder.obj");
        armor_no9 = getModel("models/item/armor/armor_no9.obj");
        armor_t51 = getModel("models/item/armor/armor_t51.obj");
        armor_ajr = getModel("models/item/armor/armor_ajr.obj");
        armor_steamsuit = getModel("models/item/armor/armor_steamsuit.obj");
        armor_dieselsuit = getModel("models/item/armor/armor_dieselsuit.obj");
        armor_hev = getModel("models/item/armor/armor_hev.obj");
        armor_fau = getModel("models/item/armor/armor_fau.obj");
        armor_dnt = getModel("models/item/armor/armor_dnt.obj");
        armor_remnant = getModel("models/item/armor/armor_remnant.obj");
        armor_envsuit = getModel("models/item/armor/armor_envsuit.obj");
        armor_trenchmaster = getModel("models/item/armor/armor_trenchmaster.obj");
        armor_hat = getModel("models/item/armor/armor_hat.obj");
        shimmer_sledge = getModel("models/item/weapon/shimmer_sledge.obj");
        shimmer_axe =  getModel("models/item/weapon/shimmer_axe.obj");
        stopsign = getModel("models/item/weapon/stopsign.obj");
        gavel = getModel("models/item/weapon/gavel.obj");
        nuke_custom = getModel("models/block/bomb/nuke_custom.obj");
        n2 = getModel("models/block/bomb/n2.obj");
        cable_neo = getModel("models/block/cable_neo.obj");
        cage_lamp = getModel("models/block/cage_lamp.obj");
        fluorescent_lamp = getModel("models/block/fluorescent_lamp.obj");
        flood_lamp = getModel("models/block/flood_lamp.obj");
        floodlight = getModel("models/block/floodlight.obj");
        cargo_container = getModel("models/block/storage/cargo_container.obj");
        dieselgen = getModel("models/block/machines/machine_diesel.obj");
        gascent = getModel("models/block/machines/machine_gas_cent.obj");
        centrifuge = getModel("models/block/machines/machine_centrifuge.obj");
        pump = getModel("models/block/machines/pump.obj");
        stirling = getModel("models/block/machines/stirling.obj");
        steam_engine = getModel("models/block/machines/machine_steam_engine.obj");
        connector = getModel("models/block/network/red_connector.obj");
        rotary_furnace = getModel("models/block/machines/rotary_furnace.obj");
        machine_crucible = getModel("models/block/machines/machine_crucible.obj");
        foundry_mold = getModel("models/block/network/foundry_mold.obj");
        foundry_basin = getModel("models/block/network/foundry_basin.obj");
        foundry_channel = getModel("models/block/network/foundry_channel.obj");
        foundry_outlet = getModel("models/block/network/foundry_outlet.obj");
        soldering_station = getModel("models/block/machines/machine_soldering_station.obj");
        arc_welder = getModel("models/block/machines/machine_arc_welder.obj");
        ring = getModel("models/effects/ring.obj");
        fatman = getModel("models/item/weapon/gun_fatman.obj");
        folly = getModel("models/item/weapon/gun_folly.obj");
        charge_thrower = getModel("models/item/weapon/gun_charge_thrower.obj");
        fireext = getModel("models/item/weapon/fireext.obj");
        // Инициализация моделей missile parts - Thrusters Size 10
        mp_t_10_kerosene = getModel("models/item/missile_parts/mp_t_10_kerosene.obj");
        mp_t_10_solid = getModel("models/item/missile_parts/mp_t_10_solid.obj");
        mp_t_10_xenon = getModel("models/item/missile_parts/mp_t_10_xenon.obj");

        // Thrusters Size 15
        mp_t_15_kerosene = getModel("models/item/missile_parts/mp_t_15_kerosene.obj");
        mp_t_15_kerosene_dual = getModel("models/item/missile_parts/mp_t_15_kerosene_dual.obj");
        mp_t_15_kerosene_triple = getModel("models/item/missile_parts/mp_t_15_kerosene_triple.obj");
        mp_t_15_solid = getModel("models/item/missile_parts/mp_t_15_solid.obj");
        mp_t_15_solid_hexadecuple = getModel("models/item/missile_parts/mp_t_15_solid_hexdecuple.obj");
        mp_t_15_balefire_short = getModel("models/item/missile_parts/mp_t_15_balefire_short.obj");
        mp_t_15_balefire = getModel("models/item/missile_parts/mp_t_15_balefire.obj");
        mp_t_15_balefire_large = getModel("models/item/missile_parts/mp_t_15_balefire_large.obj");

        // Thrusters Size 20
        mp_t_20_kerosene = getModel("models/item/missile_parts/mp_t_20_kerosene.obj");
        mp_t_20_kerosene_dual = getModel("models/item/missile_parts/mp_t_20_kerosene_dual.obj");
        mp_t_20_kerosene_triple = getModel("models/item/missile_parts/mp_t_20_kerosene_triple.obj");
        mp_t_20_solid = getModel("models/item/missile_parts/mp_t_20_solid.obj");
        mp_t_20_solid_multi = getModel("models/item/missile_parts/mp_t_20_solid_multi.obj");

        // Stability
        mp_s_10_flat = getModel("models/item/missile_parts/mp_s_10_flat.obj");
        mp_s_10_cruise = getModel("models/item/missile_parts/mp_s_10_cruise.obj");
        mp_s_10_space = getModel("models/item/missile_parts/mp_s_10_space.obj");
        mp_s_15_flat = getModel("models/item/missile_parts/mp_s_15_flat.obj");
        mp_s_15_thin = getModel("models/item/missile_parts/mp_s_15_thin.obj");
        mp_s_15_soyuz = getModel("models/item/missile_parts/mp_s_15_soyuz.obj");
        mp_s_20 = getModel("models/item/missile_parts/mp_s_20.obj");

        // Fuselages
        mp_f_10_kerosene = getModel("models/item/missile_parts/mp_f_10_kerosene.obj");
        mp_f_10_long_kerosene = getModel("models/item/missile_parts/mp_f_10_long_kerosene.obj");
        mp_f_10_15_kerosene = getModel("models/item/missile_parts/mp_f_10_15_kerosene.obj");
        mp_f_15_kerosene = getModel("models/item/missile_parts/mp_f_15_kerosene.obj");
        mp_f_15_hydrogen = getModel("models/item/missile_parts/mp_f_15_hydrogen.obj");
        mp_f_15_20_kerosene = getModel("models/item/missile_parts/mp_f_15_20_kerosene.obj");

        // Warheads
        mp_w_10_he = getModel("models/item/missile_parts/mp_w_10_he.obj");
        mp_w_10_incendiary = getModel("models/item/missile_parts/mp_w_10_incendiary.obj");
        mp_w_10_buster = getModel("models/item/missile_parts/mp_w_10_buster.obj");
        mp_w_10_nuclear = getModel("models/item/missile_parts/mp_w_10_nuclear.obj");
        mp_w_10_nuclear_large = getModel("models/item/missile_parts/mp_w_10_nuclear_large.obj");
        mp_w_10_taint = getModel("models/item/missile_parts/mp_w_10_taint.obj");
        mp_w_15_he = getModel("models/item/missile_parts/mp_w_15_he.obj");
        mp_w_15_incendiary = getModel("models/item/missile_parts/mp_w_15_incendiary.obj");
        mp_w_15_nuclear = getModel("models/item/missile_parts/mp_w_15_nuclear.obj");
        mp_w_15_boxcar = getModel("models/item/missile_parts/mp_w_15_boxcar.obj");
        mp_w_15_n2 = getModel("models/item/missile_parts/mp_w_15_n2.obj");
        mp_w_15_balefire = getModel("models/item/missile_parts/mp_w_15_balefire.obj");
        mp_w_15_turbine = getModel("models/item/missile_parts/mp_w_15_turbine.obj");

        dud_balefire = getModel("models/block/bomb/dud_balefire.obj");
        dud_nuke = getModel("models/block/bomb/dud_nuke.obj");
        dud_conventional = getModel("models/block/bomb/dud_conventional.obj");
        dud_salted = getModel("models/block/bomb/dud_salted.obj");
        turret_sentry = getModel("models/block/turrets/turret_sentry.obj");

        maskman = getModel("models/mobs/maskman.obj");
        ufo = getModel("models/mobs/ufo.obj");
        drone = getModel("models/mobs/drone.obj");
        glyphid = getModel("models/mobs/glyphid.obj");
        crystallizer = getModel("models/block/machines/machine_crystallizer.obj");
        deco_crt = getModel("models/block/deco_crt.obj");
        dornier = getModel("models/entity/dornier.obj");
        b29 = getModel("models/entity/b29.obj");
        bomblet = getModel("models/entity/bomblet.obj");
        building = getModel("models/item/weapon/building.obj");
        torpedo = getModel("models/item/weapon/torpedo.obj");
        sat_foeq = getModel("models/block/sat/sat_foeq.obj");
        sat_foeq_burning = getModel("models/entity/sat_foeq_burning.obj");
        sat_foeq_fire = getModel("models/entity/sat_foeq_fire.obj");
        sat_base = getModel("models/entity/sat_base.obj");
        sat_dock = getModel("models/block/sat/sat_dock.obj");
        sat_radar = getModel("models/block/sat/sat_radar.obj");
        sat_resonator = getModel("models/block/sat/sat_resonator.obj");
        sat_scanner = getModel("models/block/sat/sat_scanner.obj");
        sat_mapper = getModel("models/block/sat/sat_mapper.obj");
        sat_laser = getModel("models/block/sat/sat_laser.obj");
        miner_rocket = getModel("models/entity/miner_rocket.obj");
        tom_flame = getModel("models/entity/tom_flame.obj");
        tom_main = getModel("models/entity/tom_main.obj");
        zirnox_destroyed = getModel("models/block/zirnox_destroyed.obj");
        capacitor = getModel("models/block/capacitor.obj");
        tesla = getModel("models/block/tesla.obj");
        armor_mod_tesla = getModel("models/item/armor/armor_mod_tesla.obj");
        snowglobe = getModel("models/block/snowglobe.obj");
        missileV2 = getModel("models/missile/missile_v2.obj");
        missileABM = getModel("models/missile/missile_abm.obj");
        missileStealth = getModel("models/missile/missile_stealth.obj");
        missileStrong = getModel("models/missile/missile_strong.obj");
        missileHuge = getModel("models/missile/missile_huge.obj");
        missileNuclear = getModel("models/missile/missile_atlas.obj");
        missileMicro = getModel("models/missile/missile_micro.obj");
        missileShuttle = getModel("models/missile/missile_shuttle.obj");

        launch_pad = getModel("models/block/launch_pad.obj");
        solar_boiler = getModel("models/block/machines/machine_solar_boiler.obj");
        solar_mirror = getModel("models/block/machines/solar_mirror.obj");
        compact_launcher = getModel("models/block/machines/compact_launcher.obj");
        launch_table_base = getModel("models/block/machines/launch_table_base.obj");
        launch_table_small_pad = getModel("models/block/machines/launch_table_small_pad.obj");
        launch_table_large_pad = getModel("models/block/machines/launch_table_large_pad.obj");

        launch_table_small_scaffold_base = getModel("models/block/machines/launch_table_small_scaffold_base.obj");
        launch_table_small_scaffold_connector = getModel("models/block/machines/launch_table_small_scaffold_connector.obj");
        launch_table_small_scaffold_empty = getModel("models/block/machines/launch_table_small_scaffold_empty.obj");

        launch_table_large_scaffold_base = getModel("models/block/machines/launch_table_large_scaffold_base.obj");
        launch_table_large_scaffold_connector = getModel("models/block/machines/launch_table_large_scaffold_connector.obj");
        launch_table_large_scaffold_empty = getModel("models/block/machines/launch_table_large_scaffold_empty.obj");
        launch_pad_silo = getModel("models/block/machines/launch_pad_silo.obj");
        radar = getModel("models/block/machines/machine_radar.obj");
        radar_screen = getModel("models/block/machines/radar_screen.obj");

        difurnace_extension = getModel("models/block/machines/machine_difurnace_extension.obj");
        blast_furnace = getModel("models/block/machines/machine_blast_furnace.obj");
    }

    /**
     * Получение текстуры по имени с кэшированием
     * @param name Имя текстуры (без расширения и пути textures/)
     * @return ResourceLocation текстуры
     */
    public static ResourceLocation getTexture(String name) {
        return TEXTURE_CACHE.computeIfAbsent(name, key -> {
            ResourceLocation location = ResLocation(RefStrings.MODID, "textures/" + key + ".png");
            LOGGER.debug("Caching texture: {}", location);
            return location;
        });
    }

    /**
     * Получение модели OBJ по имени с кэшированием
     * @param path Путь к модели (относительно models/)
     * @return Загруженная модель или null при ошибке
     */
    public static HFRWavefrontObject getModel(String path) {
        return MODEL_CACHE.computeIfAbsent(path, key -> {
            try {
                ResourceLocation location = ResLocation(RefStrings.MODID, key);
                LOGGER.info("Loading OBJ model: {}", location);
                return new HFRWavefrontObject(location);
            } catch (Exception e) {
                LOGGER.error("Failed to load model: {}", path, e);
                return null;
            }
        });
    }



    public static void initAnimations() {
        ResourceLocation spas12AnimFile = ResLocation(RefStrings.MODID, "animations/weapon/spas12.json");
        ResourceLocation lagAnimFile = ResLocation(RefStrings.MODID, "animations/weapon/lag.json");
        ResourceLocation am180AnimFile = ResLocation(RefStrings.MODID, "animations/weapon/am180.json");
        ResourceLocation stg77AnimFile = ResLocation(RefStrings.MODID, "animations/weapon/stg77.json");
        ResourceLocation congolakeAnimFile = ResLocation(RefStrings.MODID, "animations/weapon/congolake.json");
        ResourceLocation flamethrowerAnimFile = ResLocation(RefStrings.MODID, "animations/weapon/flamethrower.json");

        Map<String, BusAnimation> spas12Anims = AnimationLoader.load(
                Minecraft.getInstance().getResourceManager(),
                spas12AnimFile
        );
        Map<String, BusAnimation> lagAnims = AnimationLoader.load(
                Minecraft.getInstance().getResourceManager(),
                lagAnimFile
        );
        Map<String, BusAnimation> am180Anims = AnimationLoader.load(
                Minecraft.getInstance().getResourceManager(),
                am180AnimFile
        );
        Map<String, BusAnimation> stg77Anims = AnimationLoader.load(
                Minecraft.getInstance().getResourceManager(),
                stg77AnimFile
        );
        Map<String, BusAnimation> congolakeAnims = AnimationLoader.load(
                Minecraft.getInstance().getResourceManager(),
                congolakeAnimFile
        );
        Map<String, BusAnimation> flamethrowerAnims = AnimationLoader.load(
                Minecraft.getInstance().getResourceManager(),
                flamethrowerAnimFile
        );

        weaponAnimations.put("spas12", spas12Anims);
        weaponAnimations.put("lag", lagAnims);
        weaponAnimations.put("am180", am180Anims);
        weaponAnimations.put("stg77", stg77Anims);
        weaponAnimations.put("congolake", congolakeAnims);
        weaponAnimations.put("flamethrower", flamethrowerAnims);

    }
    public static BusAnimation getWeaponAnimation(String weaponName, String animationName) {
        Map<String, BusAnimation> anims = weaponAnimations.get(weaponName);
        if (anims != null) {
            return anims.get(animationName);
        }
        return null;
    }
}