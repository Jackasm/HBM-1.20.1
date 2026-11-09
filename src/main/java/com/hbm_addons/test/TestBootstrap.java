package com.hbm_addons.test;

import com.hbm.util.RefStrings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber(
    modid = RefStrings.MODID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class TestBootstrap {

    private static final int STARTUP_DELAY_TICKS = 200;

    private static final Map<String, BiFunction<ServerLevel, String, TestRunner>> SCENARIOS = new HashMap<>();

    static {
        SCENARIOS.put("hello", HelloTestRunner::new);

        SCENARIOS.put("encounter_full", EncounterFullTestRunner::new);
        SCENARIOS.put("encounter_behavior", EncounterBehaviorTestRunner::new);

        // Radiation
        SCENARIOS.put("radiation_spread", RadiationSpreadTestRunner::new);
        SCENARIOS.put("radiation_mass", RadiationMassTestRunner::new);
        SCENARIOS.put("radiation_decay", RadiationDecayTestRunner::new);
        SCENARIOS.put("radiation_nbt", RadiationNbtTestRunner::new);
        SCENARIOS.put("radiation_cost", RadiationCostTestRunner::new);

        // Fluids and networks
        SCENARIOS.put("fluids_registry", FluidsRegistryTestRunner::new);
        SCENARIOS.put("networks_blocks", NetworksBlocksTestRunner::new);

        // Armor
        SCENARIOS.put("armor_damage", ArmorDamageTestRunner::new);
        SCENARIOS.put("armor_classes", ArmorClassesTestRunner::new);

        // Версии: сравнение для уведомления об обновлении
        SCENARIOS.put("version_check", VersionCheckTestRunner::new);

        // Worldgen
        SCENARIOS.put("structures_scan", StructuresScanTestRunner::new);
        SCENARIOS.put("cleanup_overhang", CleanupOverhangTestRunner::new);
        SCENARIOS.put("iron_bars", IronBarsTestRunner::new);
        SCENARIOS.put("structure_doors", StructureDoorsTestRunner::new);
        SCENARIOS.put("silo_scan", SiloScanTestRunner::new);
        SCENARIOS.put("atom_scan", AtomScanTestRunner::new);
        SCENARIOS.put("atom_fit", AtomFitTestRunner::new);
        SCENARIOS.put("worldgen_bench", WorldgenBenchTestRunner::new);
        SCENARIOS.put("chunk_load_bench", ChunkLoadBenchTestRunner::new);
        SCENARIOS.put("structure_probe", StructureProbeTestRunner::new);
        SCENARIOS.put("atom_radiation", AtomRadiationTestRunner::new);
        SCENARIOS.put("locate_bench", LocateBenchTestRunner::new);
        SCENARIOS.put("reeds_scan", ReedsScanTestRunner::new);
        // Растения, кроме камыша: грунт, вода, воздух, целостность половин
        SCENARIOS.put("plants_scan", PlantsScanTestRunner::new);
        // #204: принудительная загрузка плитки и счёт осиротевших тегов DUMMY
        SCENARIOS.put("tile_probe", TileProbeTestRunner::new);

        // Сеть
        SCENARIOS.put("rtty_place", RttyPlaceTestRunner::new);
        SCENARIOS.put("rtty_torch", RttyTorchTestRunner::new);

        // Декоративные CT-блоки (#208)
        SCENARIOS.put("deco_ct", DecoCTTestRunner::new);
        // Остальные семейства CT: platemetal, катушки (#211)
        SCENARIOS.put("ct_families", CTFamiliesTestRunner::new);

        // Raw-руды: предмет, лут, плавка (#213)
        SCENARIOS.put("raw_ores", RawOresTestRunner::new);
        // Bobblehead: ровно один дроп и он с типом (хотфикс 0.1.1)
        SCENARIOS.put("bobble_loot", BobbleLootTestRunner::new);
        // Руины: wand_loot заменяется на целевой блок, а не остаётся заглушкой
        SCENARIOS.put("ruin_loot", RuinLootTestRunner::new);
        SCENARIOS.put("ruin_shape", RuinShapeTestRunner::new);
        // SCENARIOS.put("radiation_basic", RadiationBasicTestRunner::new);
        // SCENARIOS.put("radiation_nbt", RadiationNbtTestRunner::new);
        // SCENARIOS.put("radiation_player", RadiationPlayerTestRunner::new);
    }

    private static TestRunner activeRunner = null;
    private static boolean initialized = false;
    private static int startupTicks = 0;
    private static boolean debugLogged = false;
    private static boolean chunksForced = false;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        String scenario = System.getProperty("hbm.test");

        if (!debugLogged) {
            debugLogged = true;
            System.out.println("HBM_TEST_INFO: hbm.test=" + scenario);
        }

        if (scenario == null || scenario.isEmpty()) return;

        if (!initialized) {
            startupTicks++;
            if (startupTicks < STARTUP_DELAY_TICKS) return;

            initialized = true;
            MinecraftServer server = event.getServer();

            BiFunction<ServerLevel, String, TestRunner> factory = SCENARIOS.get(scenario);
            if (factory == null) {
                System.out.println("HBM_TEST_RESULT: FAIL " + scenario + " unknown_scenario");
                TestJsonWriter.writeFailure(scenario, "unknown_scenario",
                        "Available: " + SCENARIOS.keySet());
                flushAndHalt(1);
                return;
            }

            ServerLevel overworld = server.overworld();

            // Р В¤Р С•РЎР‚РЎРѓР С‘РЎР‚РЎС“Р ВµР С РЎвЂЎР В°Р Р…Р С”Р С‘: РЎРѓР С—Р В°Р Р†Р Р… (0,0) Р’В± 2 Р С‘ РЎС“Р Т‘Р В°Р В»РЎвЂР Р…Р Р…РЎвЂ№Р Вµ РЎвЂљР С•РЎвЂЎР С”Р С‘ (1000,1000), (5000,5000)
            if (!chunksForced) {
                chunksForced = true;
                for (int cx = -2; cx <= 2; cx++) {
                    for (int cz = -2; cz <= 2; cz++) {
                        overworld.setChunkForced(cx, cz, true);
                    }
                }
                overworld.setChunkForced(62, 62, true);
                overworld.setChunkForced(312, 312, true);
                System.out.println("HBM_TEST_INFO: chunks_forced=true");
            }

            activeRunner = factory.apply(overworld, scenario);
            System.out.println("HBM_TEST_INFO: starting_scenario=" + scenario);
            return;
        }

        if (activeRunner != null) {
            try {
                boolean finished = activeRunner.tick(event.getServer());
                if (finished) {
                    int exitCode = activeRunner.isPassed() ? 0 : 1;
                    flushAndHalt(exitCode);
                }
            } catch (Throwable t) {
                System.out.println("HBM_TEST_RESULT: FAIL " +
                        activeRunner.getScenarioName() + " exception_" + t.getClass().getSimpleName());
                TestJsonWriter.writeFailure(activeRunner.getScenarioName(),
                        "exception_" + t.getClass().getSimpleName(),
                        t.toString());
                t.printStackTrace(System.out);
                flushAndHalt(1);
            }
        }
    }

    private static void flushAndHalt(int exitCode) {
        System.out.flush();
        System.err.flush();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        System.out.println("HBM_TEST_INFO: halting_with_code=" + exitCode);
        System.out.flush();
        Runtime.getRuntime().halt(exitCode);
    }
}
