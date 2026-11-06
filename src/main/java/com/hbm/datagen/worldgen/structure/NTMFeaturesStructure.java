package com.hbm.datagen.worldgen.structure;

import com.hbm.config.StructureConfig;
import com.hbm.datagen.worldgen.component.*;
import com.hbm.datagen.worldgen.structure.pieces.NTMFeaturesPiece;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NTMFeaturesStructure extends Structure {

    public static final Codec<NTMFeaturesStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    StructureSettings.CODEC.fieldOf("settings").forGetter(s -> s.settings)
            ).apply(instance, NTMFeaturesStructure::new));

    private final StructureSettings settings;

    public NTMFeaturesStructure(StructureSettings settings) {
        super(settings);
        this.settings = settings;
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // hbm-structures.toml: мастер-выключатель гасит и ntm_features.
        if (!StructureConfig.isGenerationAllowed("features")) {
            return Optional.empty();
        }

        int chunkX = context.chunkPos().x;
        int chunkZ = context.chunkPos().z;
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;

        WorldgenRandom rand = context.random();
        Holder<Biome> biomeHolder = context.chunkGenerator().getBiomeSource().getNoiseBiome(
                QuartPos.fromBlock(x),
                QuartPos.fromBlock(64),
                QuartPos.fromBlock(z),
                context.randomState().sampler());
        Biome biome = biomeHolder.value();

        if (biomeHolder.is(net.minecraft.tags.BiomeTags.IS_OCEAN) || biomeHolder.is(net.minecraft.tags.BiomeTags.IS_RIVER)) {
            return Optional.empty();
        }

        // === Бункер: 1 из 15 ===
        if (rand.nextInt(15) == 0) {
            if (!areCornersOnLand(context, x, z, 10, 10)) return Optional.empty();
            return createBunker(context, rand, chunkX, chunkZ, x, z);
        }

        // === Silo: 1 из 10 ===
        if (rand.nextInt(10) == 0) {
            if (!areCornersOnLand(context, x, z, 42, 26)) return Optional.empty();
            SiloComponent silo = new SiloComponent(rand, x, z);
            silo.generationSeed = rand.nextLong();

            // Высоту ставим здесь, до создания куска: бокс должен быть
            // окончательным. Раньше он двигался уже в postProcess по высоте,
            // посчитанной от одного чанка, — отсюда срезы и утопленная
            // площадка (#152).
            if (!placeSiloOnSurface(context, silo)) return Optional.empty();

            return Optional.of(new GenerationStub(new BlockPos(x, silo.getBoundingBox().minY(), z),
                    builder -> builder.addPiece(new NTMFeaturesPiece(silo))));
        }

        // === Civilian: жаркий сухой биом ===
        if (biome.getBaseTemperature() >= 1.0F && !biome.hasPrecipitation()) {
            if (!areCornersOnLand(context, x, z, 15, 9)) return Optional.empty();
            NTMComponent house = rand.nextBoolean()
                    ? new NTMHouse1(rand, x, z)
                    : new NTMHouse2(rand, x, z);
            house.generationSeed = rand.nextLong();
            return Optional.of(new GenerationStub(new BlockPos(x, 64, z),
                    builder -> builder.addPiece(new NTMFeaturesPiece(house))));
        }

        // === Остальные: switch по rand.nextInt(6) ===
        int roll = rand.nextInt(6);

        int checkW, checkH;
        switch (roll) {
            case 0, 1 -> { checkW = 15; checkH = 13; }
            case 2 -> { checkW = 12; checkH = 8; }
            case 3 -> { checkW = 9;  checkH = 7; }
            default -> { checkW = 14; checkH = 14; }
        }

        if (!areCornersOnLand(context, x, z, checkW, checkH)) return Optional.empty();

        NTMComponent component = switch (roll) {
            case 0 -> new OfficeFeatures.LargeOffice(rand, x, z);
            case 1 -> new OfficeFeatures.LargeOfficeCorner(rand, x, z);
            case 2 -> new NTMLab2(rand, x, z);
            case 3 -> new NTMLab1(rand, x, z);
            default -> new RuralHouse1(rand, x, z);
        };

        component.generationSeed = rand.nextLong();

        return Optional.of(new GenerationStub(new BlockPos(x, 64, z),
                builder -> builder.addPiece(new NTMFeaturesPiece(component))));
    }

    // ==================== SILO ====================

    /**
     * Ставит шахту на уровень поверхности площадки.
     *
     * <p>Высота берётся по подошве самой площадки (29×18), а не средняя по
     * куску: средняя на склоне садится ниже земли у люка. Склоны с перепадом
     * больше {@link #SILO_MAX_SLOPE} не берём — площадка должна стоять ровно,
     * иначе один край повисает, а другой зарывается.</p>
     *
     * <p>Отдельно проверяется кольцо вокруг площадки: если рельеф за её краем
     * выше самой подошвы больше чем на {@link #SILO_MAX_RING}, площадка сядет
     * в яму — визуально это «утоплено», хотя формально стоит на своей высоте.
     * Именно так вышло на точке `3288, 4696` (#152).</p>
     *
     * @return false, если площадку на этом месте ставить нельзя
     */
    private boolean placeSiloOnSurface(GenerationContext context, SiloComponent silo) {
        BoundingBox area = silo.platformArea();
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int ringMax = Integer.MIN_VALUE;

        for (int z = area.minZ() - SILO_RING; z <= area.maxZ() + SILO_RING; z++) {
            for (int x = area.minX() - SILO_RING; x <= area.maxX() + SILO_RING; x++) {
                int h = context.chunkGenerator().getBaseHeight(
                        x, z, Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(), context.randomState());

                boolean insidePad = x >= area.minX() && x <= area.maxX()
                        && z >= area.minZ() && z <= area.maxZ();

                if (insidePad) {
                    if (h > maxY) maxY = h;
                    if (h < minY) minY = h;
                } else if (h > ringMax) {
                    ringMax = h;
                }
            }
        }

        if (maxY == Integer.MIN_VALUE) return false;
        if (maxY - minY > SILO_MAX_SLOPE) return false;

        // Площадка на склоне садится в яму, если рельеф за её краем выше.
        // Поднимаем её до уровня кольца и ещё на SILO_LIFT_BIAS: по осмотру
        // владельца (05.11.2026) на склонах этого не хватало — асфальт ложился
        // вровень с ближним краем, но всё равно читался как «в яме».
        // Цоколь под низким краем достраивает placeFoundationUnderneath.
        if (ringMax != Integer.MIN_VALUE) {
            int lift = ringMax - maxY;
            if (lift > SILO_MAX_LIFT) return false;
            if (lift > 0) maxY = ringMax + SILO_LIFT_BIAS;
        }

        silo.applySurfaceHeight(maxY);
        return true;
    }

    /** Перепад по подошве площадки, при котором место ещё годится. */
    private static final int SILO_MAX_SLOPE = 4;

    /** Насколько площадку можно поднять до уровня рельефа за её краем. */
    private static final int SILO_MAX_LIFT = 6;

    /** Добавка к подъёму на склоне: с ней площадка не читается как яма. */
    private static final int SILO_LIFT_BIAS = 2;

    /** Ширина кольца вокруг площадки для проверки «не в яме ли». */
    private static final int SILO_RING = 4;

    // ==================== BUNKER ====================

    private Optional<GenerationStub> createBunker(GenerationContext context, WorldgenRandom rand,
                                                  int chunkX, int chunkZ, int x, int z) {
        ProceduralStructure.setContext(new ProceduralStructure.GenContext(
                context.chunkGenerator(), context.heightAccessor(), context.randomState()));

        BunkerStart bunker;
        try {
            bunker = BunkerStart.create(rand, chunkX, chunkZ);
        } finally {
            ProceduralStructure.clearContext();
        }

        if (bunker.components.isEmpty()) return Optional.empty();

        StartingHub hub = null;
        for (NTMComponent comp : bunker.components) {
            if (comp instanceof StartingHub sh) { hub = sh; break; }
        }
        if (hub == null) return Optional.empty();

        int hatchX = hub.getXWithOffset(2, 5);
        int hatchZ = hub.getZWithOffset(2, 5);

        int surfaceY = context.chunkGenerator().getBaseHeight(
                hatchX, hatchZ, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());

        if (!isValidBunkerSite(context, hatchX, surfaceY, hatchZ)) {
            return Optional.empty();
        }

        int descentDepth = 10 + rand.nextInt(6);
        int targetMinY = surfaceY - descentDepth;

        int dy = targetMinY - hub.getBoundingBox().minY();
        for (NTMComponent comp : bunker.components) {
            comp.setBoundingBox(comp.getBoundingBox().move(0, dy, 0));
        }

        hub.setHpos(descentDepth);
        hub.skipHeightCalculation();

        // === Отсеиваем комнаты, которые торчат из склона или в воде ===
        List<NTMComponent> valid = new ArrayList<>();
        for (NTMComponent comp : bunker.components) {
            if (comp instanceof StartingHub || isRoomFullyUnderground(context, comp.getBoundingBox())) {
                valid.add(comp);
            } else {
            }
        }

        if (valid.isEmpty()) return Optional.empty();


        return Optional.of(new GenerationStub(new BlockPos(x, surfaceY, z), builder -> {
            for (NTMComponent comp : valid) {
                builder.addPiece(new NTMFeaturesPiece(comp));
            }
        }));
    }

    /** Все 4 верхних угла box — под землёй (не вода, не воздух). */
    private boolean isRoomFullyUnderground(GenerationContext context, BoundingBox box) {
        var chunkGen = context.chunkGenerator();
        var heightAccessor = context.heightAccessor();
        var randomState = context.randomState();

        int y = box.maxY();
        int[][] corners = {
                {box.minX(), box.minZ()},
                {box.maxX(), box.minZ()},
                {box.minX(), box.maxZ()},
                {box.maxX(), box.maxZ()}
        };

        for (int[] c : corners) {
            int wx = c[0], wz = c[1];
            int floorY = chunkGen.getBaseHeight(wx, wz, Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState);
            int surfaceY = chunkGen.getBaseHeight(wx, wz, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);

            // вода в диапазоне [floorY, surfaceY)
            if (floorY <= y && y < surfaceY) return false;
            // воздух на y (торчит из склона)
            if (y >= surfaceY) return false;
        }
        return true;
    }

    /** Квадрат 5x5 на surfaceY — твёрдый (не вода, не воздух). На surfaceY+1 — воздух. */
    private boolean isValidBunkerSite(GenerationContext context, int cx, int surfaceY, int cz) {
        var chunkGen = context.chunkGenerator();
        var heightAccessor = context.heightAccessor();
        var randomState = context.randomState();

        int minY = heightAccessor.getMinBuildHeight();
        int maxY = heightAccessor.getMaxBuildHeight();

        if (surfaceY - 1 < minY || surfaceY + 1 >= maxY) return false;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int wx = cx + dx;
                int wz = cz + dz;

                var column = chunkGen.getBaseColumn(wx, wz, heightAccessor, randomState);

                var stateAtSurface = column.getBlock(surfaceY - 1);
                if (stateAtSurface.isAir() || !stateAtSurface.getFluidState().isEmpty()) {
                    return false;
                }

                var stateAbove = column.getBlock(surfaceY);
                if (!stateAbove.isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    // ==================== AREA CHECK ====================

    private boolean areCornersOnLand(GenerationContext context, int x, int z, int sizeX, int sizeZ) {
        var chunkGen = context.chunkGenerator();
        var heightAccessor = context.heightAccessor();
        var randomState = context.randomState();

        int[][] corners = {
                {0, 0}, {sizeX, 0}, {0, sizeZ}, {sizeX, sizeZ}
        };

        int landCorners = 0;
        for (int[] c : corners) {
            int worldX = x + c[0];
            int worldZ = z + c[1];

            int h = chunkGen.getBaseHeight(worldX, worldZ,
                    Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
            var topState = chunkGen.getBaseColumn(worldX, worldZ, heightAccessor, randomState)
                    .getBlock(h - 1);

            if (topState.getFluidState().isEmpty()) {
                landCorners++;
            }
        }

        return landCorners >= 3;
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.NTM_FEATURES.get();
    }
}
