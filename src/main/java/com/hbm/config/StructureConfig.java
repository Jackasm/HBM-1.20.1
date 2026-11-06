package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Set;

/**
 * Настройки генерации структур HBM.
 *
 * <p>Здесь только то, что действительно читается в рантайме: выключатели и
 * множитель лута. Веса структур и сетка размещения ({@code spacing},
 * {@code separation}, соли) задаются датагеном в {@code structure_set} и в
 * 1.20.1 живут в замороженном реестре: поменять их конфигом на старте мира
 * нельзя. Ключи, которые это обещали, удалены — конфиг, который врёт, хуже,
 * чем его отсутствие. Плотность и пустые веса — задача #66.</p>
 *
 * <p>Выключатели проверяются в {@code Structure#findGenerationPoint}: там же,
 * где эталон 1.7.10 обнулял вес структуры
 * ({@code ref:world/gen/NTMWorldGenerator.java:400-427}).</p>
 */
public class StructureConfig {

    /** Мастер-выключатель: наземные NBT-структуры и {@code ntm_features}. */
    public static ForgeConfigSpec.BooleanValue ENABLE_STRUCTURES;

    /** Руины {@code ntmruins_a} … {@code ntmruins_j}. */
    public static ForgeConfigSpec.BooleanValue ENABLE_RUINS;

    /** Океанские структуры: авианосец, нефтевышка, маяк — как в эталоне. */
    public static ForgeConfigSpec.BooleanValue ENABLE_OCEAN_STRUCTURES;

    /**
     * Чистка габарита NBT-структур после декорации (#102): деревья, листва и трава,
     * выросшие внутри структуры, удаляются, блоки структуры возвращаются на место.
     * Выключено — поведение 0.0.24: декор остаётся, как решили в ADR-0010.
     */
    public static ForgeConfigSpec.BooleanValue CLEAN_DECOR;

    public static ForgeConfigSpec.DoubleValue LOOT_AMOUNT_FACTOR;

    /** Зеркало значения: живёт до загрузки конфига и хранит дефолт. */
    public static double LOOT_AMOUNT_FACTOR_VALUE = 1.0D;

    /** Ровно те ключи, которые эталон гасил через {@code enableOceanStructures}. */
    private static final Set<String> OCEAN_STRUCTURES = Set.of(
            "aircraft_carrier", "oil_rig", "lighthouse");

    public static ForgeConfigSpec register() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("structures");

        ENABLE_STRUCTURES = builder
                .comment("Master switch for HBM structures: surface NBT structures and ntm_features (houses, bunkers, offices)")
                .define("enable_structures", true);

        ENABLE_RUINS = builder
                .comment("Toggle for ruin structures (ntmruins_a through ntmruins_j). Ignored when enable_structures is false")
                .define("enable_ruins", true);

        ENABLE_OCEAN_STRUCTURES = builder
                .comment("Toggle for ocean structures (aircraft carrier, oil rig, lighthouse). Ignored when enable_structures is false")
                .define("enable_ocean_structures", true);

        CLEAN_DECOR = builder
                .comment("Remove trees, leaves and grass that grow inside NBT structures after decoration (#102). " +
                        "false restores 0.0.24 behaviour: decor grows over structures")
                .define("clean_decor", true);

        LOOT_AMOUNT_FACTOR = builder
                .comment("General factor for loot spawns. Applies to spawned IInventories, not loot blocks.")
                .defineInRange("loot_amount_factor", 1.0D, 0.0D, 10.0D);

        builder.pop();

        return builder.build();
    }

    /**
     * Можно ли пытаться поставить структуру с этим ключом в текущем чанке.
     * Ключ — имя NBT-файла структуры ({@code nbt_file} в JSON) либо
     * {@code "features"} для {@code ntm_features}.
     */
    public static boolean isGenerationAllowed(String key) {
        if (!isEnableStructures()) return false;
        if (key.startsWith("ntmruins_") && !isEnableRuins()) return false;
        if (OCEAN_STRUCTURES.contains(key) && !isEnableOceanStructures()) return false;
        return true;
    }

    public static boolean isEnableStructures() {
        return ENABLE_STRUCTURES != null && ENABLE_STRUCTURES.get();
    }

    public static boolean isEnableRuins() {
        return ENABLE_RUINS != null && ENABLE_RUINS.get();
    }

    public static boolean isEnableOceanStructures() {
        return ENABLE_OCEAN_STRUCTURES != null && ENABLE_OCEAN_STRUCTURES.get();
    }

    /** Чистка декора внутри структур. До загрузки конфига считаем включённой — это дефолт. */
    public static boolean isCleanDecor() {
        return CLEAN_DECOR == null || CLEAN_DECOR.get();
    }

    public static void load() {
        LOOT_AMOUNT_FACTOR_VALUE = LOOT_AMOUNT_FACTOR.get();
    }
}
