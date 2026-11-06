package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.WorldConfig;
import com.hbm.datagen.worldgen.feature.config.MeteoriteConfiguration;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MeteoriteFeature extends Feature<MeteoriteConfiguration> {

    public static final HashSet<Block> REPLACABLES = new HashSet<>();
    public static boolean safeMode = false;

    public MeteoriteFeature(com.mojang.serialization.Codec<MeteoriteConfiguration> codec) {
        super(codec);
    }

    // ==================== FEATURE ENTRY POINT ====================

    @Override
    public boolean place(FeaturePlaceContext<MeteoriteConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        MeteoriteConfiguration cfg = context.config();

        int x = origin.getX();
        int z = origin.getZ();
        int y;

        if (cfg.findSurface()) {
            y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - rand.nextInt(10);
        } else {
            y = origin.getY();
        }

        if (y < 2) return false;

        // Проверка: под метеоритом solid
        BlockState below = level.getBlockState(new BlockPos(x, y - 2, z));
        if (below.isAir() || !below.getFluidState().isEmpty()) return false;

        generate(level, rand, x, y, z,
                cfg.safe(), cfg.allowSpecials(), cfg.damagingImpact());

        return true;
    }

    // ==================== PUBLIC STATIC ENTRY POINT ====================

    /**
     * Публичный метод для генерации метеорита.
     * Работает и из Feature (WorldGenLevel), и из Entity (ServerLevel).
     */
    public static void generate(LevelAccessor level, RandomSource rand,
                                int x, int y, int z,
                                boolean safe, boolean allowSpecials, boolean damagingImpact) {
        safeMode = safe;

        if (REPLACABLES.isEmpty()) {
            generateReplacables();
        }

        // Урон сущностям
        if (damagingImpact && level instanceof Level realLevel) {
            AABB box = new AABB(x - 7.5, y - 7.5, z - 7.5, x + 7.5, y + 7.5, z + 7.5);
            for (Entity e : realLevel.getEntities(null, box)) {
                e.hurt(ModDamageSource.causeMeteoriteDamage(e.level()), 1000);
            }
        }

        // Специальные метеориты
        if (WorldConfig.ENABLE_SPECIAL_METEOR && allowSpecials) {
            switch (rand.nextInt(300)) {
                case 0 -> {
                    List<BlockState> set = new ArrayList<>();
                    set.add(ModBlocks.BLOCK_METEOR.get().defaultBlockState());
                    generateBox(level, rand, x, y, z, set);
                    return;
                }
                case 1 -> {
                    List<BlockState> set = new ArrayList<>(getRandomOre(rand));
                    int size = set.size();
                    for (int j = 0; j < size; j++) set.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                    generateSphere7x7(level, rand, x, y, z, set);
                    return;
                }
                case 2 -> {
                    List<BlockState> set = new ArrayList<>(getRandomOre(rand));
                    int half = set.size() / 2;
                    for (int j = 0; j < half; j++) set.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                    generateSphere5x5(level, rand, x, y, z, set);
                    return;
                }
                case 3 -> {
                    List<BlockState> set = new ArrayList<>(getRandomOre(rand));
                    generateBox(level, rand, x, y, z, set);
                    return;
                }
                case 4 -> {
                    if (level instanceof Level realLevel) {
                        realLevel.explode(null, x + 0.5, y + 0.5, z + 0.5, 15F,
                                safe ? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.TNT);
                    }
                    if (level instanceof Level realLevel) {
                        ExplosionLarge.spawnRubble(realLevel, x + 0.5, y + 0.5, z + 0.5, 25);
                    }
                    return;
                }
                case 5 -> {
                    List<BlockState> set = new ArrayList<>();
                    set.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
                    set.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                    generateSphere7x7(level, rand, x, y, z, set);
                    return;
                }
                case 6 -> {
                    List<BlockState> set = new ArrayList<>();
                    set.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
                    set.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
                    set.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                    generateSphere5x5(level, rand, x, y, z, set);
                    return;
                }
                case 7 -> {
                    List<BlockState> set = new ArrayList<>();
                    set.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
                    generateBox(level, rand, x, y, z, set);
                    return;
                }
                case 8 -> {
                    List<BlockState> set1 = new ArrayList<>();
                    set1.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
                    List<BlockState> set2 = new ArrayList<>();
                    set2.add(ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
                    generateSphere7x7(level, rand, x, y, z, set1);
                    generateSphere5x5(level, rand, x, y, z, set2);
                    return;
                }
                case 9 -> {
                    List<BlockState> set1 = new ArrayList<>();
                    set1.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                    generateSphere9x9(level, rand, x, y, z, set1);
                    generateSphere7x7(level, rand, x, y, z, getRandomOre(rand));
                    return;
                }
                case 10 -> {
                    List<BlockState> set = new ArrayList<>();
                    set.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                    generateSphere5x5(level, rand, x, y, z, set);
                    setBlock(level, x, y, z, ModBlocks.TAINT.get().defaultBlockState(), 2);
                    return;
                }
            }
        }

        switch (rand.nextInt(3)) {
            case 0 -> generateLarge(level, rand, x, y, z);
            case 1 -> generateMedium(level, rand, x, y, z);
            case 2 -> generateSmall(level, rand, x, y, z);
        }
    }

    // ==================== LARGE / MEDIUM / SMALL ====================

    private static void generateLarge(LevelAccessor level, RandomSource rand, int x, int y, int z) {
        int hull = rand.nextInt(4);
        int outerPadding = (hull == 2) ? 1 + rand.nextInt(2) : (hull == 3 ? 2 : 0);
        int innerPadding = rand.nextInt(hull == 0 ? 3 : 2);
        int core = (innerPadding > 0) ? 2 : rand.nextInt(2);

        List<BlockState> hullL = buildHull(hull);
        List<BlockState> opL   = buildOuterPadding(outerPadding);
        List<BlockState> ipL   = buildInnerPadding(innerPadding);
        List<BlockState> coreL = buildCore(core, rand);

        switch (rand.nextInt(5)) {
            case 0 -> genL1(level, rand, x, y, z, hullL, opL, ipL, coreL);
            case 1 -> genL2(level, rand, x, y, z, hullL, opL, ipL, coreL);
            case 2 -> genL3(level, rand, x, y, z, hullL, opL, ipL, coreL);
            case 3 -> genL4(level, rand, x, y, z, hullL, opL, ipL, coreL);
            case 4 -> genL5(level, rand, x, y, z, hullL, opL, ipL, coreL);
        }
    }

    private static void generateMedium(LevelAccessor level, RandomSource rand, int x, int y, int z) {
        int hull = rand.nextInt(4);
        int outerPadding = (hull == 2) ? 1 + rand.nextInt(2) : (hull == 3 ? 2 : 0);
        int innerPadding = rand.nextInt(hull == 0 ? 3 : 2);
        int core = (innerPadding > 0) ? 2 : rand.nextInt(2);

        List<BlockState> hullL = buildHull(hull);
        List<BlockState> opL   = buildOuterPadding(outerPadding);
        List<BlockState> ipL   = buildInnerPadding(innerPadding);
        List<BlockState> coreL = buildCore(core, rand);
        List<BlockState> sCore = buildSCore(core);

        switch (rand.nextInt(6)) {
            case 0 -> genM1(level, rand, x, y, z, hullL, opL, ipL, sCore);
            case 1 -> genM2(level, rand, x, y, z, hullL, opL, ipL, coreL);
            case 2 -> genM3(level, rand, x, y, z, hullL, opL, ipL, coreL);
            case 3 -> genM4(level, rand, x, y, z, hullL, opL, ipL, coreL);
            case 4 -> genM5(level, rand, x, y, z, hullL, opL, ipL, coreL);
            case 5 -> genM6(level, rand, x, y, z, hullL, opL, ipL, coreL);
        }
    }

    private static void generateSmall(LevelAccessor level, RandomSource rand, int x, int y, int z) {
        int hull = rand.nextInt(4);
        int core = rand.nextInt(3);

        List<BlockState> hullL = buildHull(hull);
        List<BlockState> sCore = buildSCore(core);

        generateBox(level, rand, x, y, z, hullL);
        BlockState state = sCore.get(rand.nextInt(sCore.size()));
        setBlock(level, x, y, z, state, 2);
    }

    // ==================== BUILDERS ====================

    private static List<BlockState> buildHull(int hull) {
        List<BlockState> list = new ArrayList<>();
        switch (hull) {
            case 0 -> list.add(ModBlocks.BLOCK_METEOR_MOLTEN.get().defaultBlockState());
            case 1 -> list.add(ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState());
            case 2 -> {
                for (int i = 0; i < 99; i++) list.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                list.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
            }
            case 3 -> {
                list.add(ModBlocks.BLOCK_METEOR_MOLTEN.get().defaultBlockState());
                list.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
            }
        }
        return list;
    }

    private static List<BlockState> buildOuterPadding(int outer) {
        List<BlockState> list = new ArrayList<>();
        switch (outer) {
            case 0 -> list.add(ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState());
            case 1 -> {
                for (int i = 0; i < 99; i++) list.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                list.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
            }
            case 2 -> {
                list.add(ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState());
                list.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
            }
        }
        return list;
    }

    private static List<BlockState> buildInnerPadding(int inner) {
        List<BlockState> list = new ArrayList<>();
        switch (inner) {
            case 0 -> {
                for (int i = 0; i < 99; i++) list.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
                list.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
            }
            case 1 -> list.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState());
            case 2 -> list.add(ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState());
        }
        return list;
    }

    private static List<BlockState> buildCore(int core, RandomSource rand) {
        List<BlockState> list = new ArrayList<>();
        switch (core) {
            case 0 -> list.add(ModBlocks.BLOCK_METEOR.get().defaultBlockState());
            case 1 -> list.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
            case 2 -> list.addAll(getRandomOre(rand));
        }
        return list;
    }

    private static List<BlockState> buildSCore(int core) {
        List<BlockState> list = new ArrayList<>();
        switch (core) {
            case 0 -> list.add(ModBlocks.BLOCK_METEOR.get().defaultBlockState());
            case 1 -> list.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
            case 2 -> {
                list.add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState());
                list.add(ModBlocks.BLOCK_METEOR.get().defaultBlockState());
            }
        }
        return list;
    }

    // ==================== GEN L1-L5 ====================

    private static void genL1(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere7x7(level, rand, x, y, z, hull);
        generateStar5x5(level, rand, x, y, z, op);
        generateStar3x3(level, rand, x, y, z, ip);
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genL2(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere7x7(level, rand, x, y, z, hull);
        generateSphere5x5(level, rand, x, y, z, op);
        generateStar3x3(level, rand, x, y, z, ip);
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genL3(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere7x7(level, rand, x, y, z, hull);
        generateSphere5x5(level, rand, x, y, z, op);
        generateBox(level, rand, x, y, z, ip);
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genL4(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere7x7(level, rand, x, y, z, hull);
        generateSphere5x5(level, rand, x, y, z, op);
        generateBox(level, rand, x, y, z, ip);
        generateStar3x3(level, rand, x, y, z, getRandomOre(rand));
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genL5(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere7x7(level, rand, x, y, z, hull);
        generateSphere5x5(level, rand, x, y, z, op);
        generateStar5x5(level, rand, x, y, z, ip);
        generateStar3x3(level, rand, x, y, z, getRandomOre(rand));
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    // ==================== GEN M1-M6 ====================

    private static void genM1(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere5x5(level, rand, x, y, z, hull);
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genM2(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere5x5(level, rand, x, y, z, hull);
        generateStar3x3(level, rand, x, y, z, op);
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genM3(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere5x5(level, rand, x, y, z, hull);
        generateBox(level, rand, x, y, z, op);
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genM4(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere5x5(level, rand, x, y, z, hull);
        generateBox(level, rand, x, y, z, op);
        generateStar3x3(level, rand, x, y, z, ip);
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genM5(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere5x5(level, rand, x, y, z, hull);
        generateBox(level, rand, x, y, z, ip);
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    private static void genM6(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
        generateSphere5x5(level, rand, x, y, z, hull);
        generateBox(level, rand, x, y, z, ip);
        generateStar3x3(level, rand, x, y, z, getRandomOre(rand));
        setBlock(level, x, y, z, core.get(rand.nextInt(core.size())), 2);
    }

    // ==================== SHAPES ====================

    private static void generateSphere7x7(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> set) {
        for (int a = -3; a < 4; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -1; a < 2; a++)
            for (int b = -3; b < 4; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -1; a < 2; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -3; c < 4; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);

        for (int a = -2; a < 3; a++)
            for (int b = -2; b < 3; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -1; a < 2; a++)
            for (int b = -2; b < 3; b++)
                for (int c = -2; c < 3; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -2; a < 3; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -2; c < 3; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
    }

    private static void generateSphere5x5(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> set) {
        for (int a = -2; a < 3; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -1; a < 2; a++)
            for (int b = -2; b < 3; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -1; a < 2; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -2; c < 3; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
    }

    private static void generateSphere9x9(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> set) {
        for (int a = -4; a < 5; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -1; a < 2; a++)
            for (int b = -4; b < 5; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -1; a < 2; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -4; c < 5; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);

        for (int a = -1; a < 2; a++)
            for (int b = -3; b < 4; b++)
                for (int c = -3; c < 4; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -3; a < 4; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -3; c < 4; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -3; a < 4; a++)
            for (int b = -3; b < 4; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);

        for (int a = -3; a < 4; a++)
            for (int b = -2; b < 3; b++)
                for (int c = -2; c < 3; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -2; a < 3; a++)
            for (int b = -3; b < 4; b++)
                for (int c = -2; c < 3; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
        for (int a = -2; a < 3; a++)
            for (int b = -2; b < 3; b++)
                for (int c = -3; c < 4; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
    }

    private static void generateBox(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> set) {
        for (int a = -1; a < 2; a++)
            for (int b = -1; b < 2; b++)
                for (int c = -1; c < 2; c++)
                    setBlock(level, x + a, y + b, z + c, set.get(rand.nextInt(set.size())), 2);
    }

    private static void generateStar5x5(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> set) {
        generateBox(level, rand, x, y, z, set);
        setBlock(level, x + 2, y, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x - 2, y, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x, y + 2, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x, y - 2, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x, y, z + 2, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x, y, z - 2, set.get(rand.nextInt(set.size())), 2);
    }

    private static void generateStar3x3(LevelAccessor level, RandomSource rand, int x, int y, int z, List<BlockState> set) {
        setBlock(level, x, y, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x + 1, y, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x - 1, y, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x, y + 1, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x, y - 1, z, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x, y, z + 1, set.get(rand.nextInt(set.size())), 2);
        setBlock(level, x, y, z - 1, set.get(rand.nextInt(set.size())), 2);
    }

    // ==================== UTILS ====================

    private static void setBlock(LevelAccessor level, int x, int y, int z, BlockState state, int flag) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState target = level.getBlockState(pos);

        if (safeMode) {
            if (!target.canBeReplaced() && !REPLACABLES.contains(target.getBlock())) return;
        }

        float hardness = target.getDestroySpeed(level, pos);
        if (hardness != -1 && hardness < 10_000) {
            level.setBlock(pos, state, flag);
        }
    }

    private static void generateReplacables() {
        REPLACABLES.add(ModBlocks.BLOCK_METEOR.get());
        REPLACABLES.add(ModBlocks.BLOCK_METEOR_BROKEN.get());
        REPLACABLES.add(ModBlocks.BLOCK_METEOR_COBBLE.get());
        REPLACABLES.add(ModBlocks.BLOCK_METEOR_MOLTEN.get());
        REPLACABLES.add(ModBlocks.BLOCK_METEOR_TREASURE.get());
        REPLACABLES.add(ModBlocks.ORE_METEOR_IRON.get());
        REPLACABLES.add(ModBlocks.ORE_METEOR_COPPER.get());
        REPLACABLES.add(ModBlocks.ORE_METEOR_ALUMINIUM.get());
        REPLACABLES.add(ModBlocks.ORE_METEOR_RARE_EARTH.get());
        REPLACABLES.add(ModBlocks.ORE_METEOR_COBALT.get());
    }

    private static List<BlockState> getRandomOre(RandomSource rand) {
        List<BlockState> ores = new ArrayList<>();
        ores.add(ModBlocks.ORE_METEOR_IRON.get().defaultBlockState());
        ores.add(ModBlocks.ORE_METEOR_COPPER.get().defaultBlockState());
        ores.add(ModBlocks.ORE_METEOR_ALUMINIUM.get().defaultBlockState());
        ores.add(ModBlocks.ORE_METEOR_RARE_EARTH.get().defaultBlockState());
        ores.add(ModBlocks.ORE_METEOR_COBALT.get().defaultBlockState());
        return ores;
    }
}