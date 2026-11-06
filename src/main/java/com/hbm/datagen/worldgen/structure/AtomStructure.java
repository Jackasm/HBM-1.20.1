package com.hbm.datagen.worldgen.structure;

import com.hbm.config.StructureConfig;
import com.hbm.datagen.worldgen.structure.pieces.AtomPiece;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Атомная станция — постановка по код-генератору {@link AtomPiece}.
 *
 * <p>Размер и опорная точка взяты из эталона 1.7.10
 * ({@code ref:world/gen/HbmWorldGen.java:220-224}): {@code x = i + rand.nextInt(16)},
 * {@code y = world.getHeightValue(x, z)} в центре постройки, габарит 41×34.
 * Точка постановки опускается на {@link #SINK} блоков: у станции есть подземная
 * часть, которая должна уйти в грунт.</p>
 */
public class AtomStructure extends Structure {

    public static final Codec<AtomStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    StructureSettings.CODEC.fieldOf("settings").forGetter(s -> s.settings)
            ).apply(instance, AtomStructure::new));

    private final StructureSettings settings;

    /** Высота не получена. */
    private static final int NO_HEIGHT = Integer.MIN_VALUE;

    /**
     * Допустимый перепад рельефа под постройкой.
     *
     * <p>Было 32 (как у крупных NBT-структур), но на дюнах атомка попадала в
     * котловину: замер 03.11.2026 на живом мире дал средний зазор 2,15 блока под
     * подушкой, до 11 в худшей колонке. Станция — плоскодонная постройка 41×34,
     * поэтому допуск как у зданий: 16 блоков. Остаток перепада закрывает засыпка
     * под подушкой в {@code AtomPiece}.</p>
     */
    private static final int MAX_SLOPE = 16;

    /**
     * Насколько пол зданий выше самой высокой точки подошвы.
     *
     * <p>История правки 03.11.2026: сначала станция висела — Beardifier вырезал
     * из пятислойной подушки три слоя (замер: средний зазор 2,15 блока в 88,6%
     * колонок). Потом постановка по минимуму подошвы утопила её в дюну: комплекс
     * оказался в котловине, корпуса в песке. Итог: пол зданий на уровне гребня
     * ({@code max + 1}), рельеф выше пола срезается, под подушкой подсыпается
     * грунт — и висящей, и утопленной станция быть перестаёт.</p>
     */
    private static final int PAD_ABOVE_GROUND = 1;

    public AtomStructure(StructureSettings settings) {
        super(settings);
        this.settings = settings;
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        // hbm-structures.toml: мастер-выключатель.
        if (!StructureConfig.isGenerationAllowed("desert_atom")) {
            return Optional.empty();
        }

        // Биом проверяет сам движок по тегу settings.biomes() — отдельная
        // проверка не нужна, в отличие от руин с их доп. условием по осадкам.

        ChunkPos chunkPos = context.chunkPos();
        WorldgenRandom random = context.random();

        for (int attempt = 0; attempt < 2; attempt++) {
            int x = chunkPos.getMinBlockX() + random.nextInt(16);
            int z = chunkPos.getMinBlockZ() + random.nextInt(16);

            int centerX = x + AtomPiece.SIZE_X / 2;
            int centerZ = z + AtomPiece.SIZE_Z / 2;

            int y = queryHeight(context, centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG);
            if (y == NO_HEIGHT) continue;

            // Станция ставится на сушу: в опорной колонке над грунтом не жидкость.
            int ground = queryHeight(context, centerX, centerZ, Heightmap.Types.OCEAN_FLOOR_WG);
            if (ground != NO_HEIGHT && y > ground) continue;

            // Перепад рельефа по углам и центру: на крутом склоне подушка
            // получается выше постройки.
            int min = y;
            int max = y;
            for (int gx = 0; gx < 3; gx++) {
                for (int gz = 0; gz < 3; gz++) {
                    int px = x + gx * (AtomPiece.SIZE_X - 1) / 2;
                    int pz = z + gz * (AtomPiece.SIZE_Z - 1) / 2;
                    int h = queryHeight(context, px, pz, Heightmap.Types.WORLD_SURFACE_WG);
                    if (h == NO_HEIGHT) continue;
                    if (h < min) min = h;
                    if (h > max) max = h;
                }
            }
            if (max - min > MAX_SLOPE) continue;

            // Постройка должна целиком влезть в мир: снизу подушка, сверху башня.
            int minBuild = context.heightAccessor().getMinBuildHeight();
            int maxBuild = context.heightAccessor().getMaxBuildHeight();
            // Высота — по середине между низом и верхом подошвы: половину
            // перепада срезаем (AtomPiece.carveSite), половину подсыпаем. Так
            // станция не висит над низким краем и не тонет в дюне.
            int originY = (min + max) / 2 + PAD_ABOVE_GROUND;
            if (originY - AtomPiece.BASE_DEPTH < minBuild || originY + AtomPiece.TOP_Y >= maxBuild) continue;

            final int placeY = originY;
            return Optional.of(new GenerationStub(new BlockPos(x, placeY, z),
                    builder -> builder.addPiece(new AtomPiece(x, placeY, z))));
        }

        return Optional.empty();
    }

    private static int queryHeight(GenerationContext context, int x, int z, Heightmap.Types heightmap) {
        try {
            return context.chunkGenerator().getBaseHeight(
                    x, z, heightmap, context.heightAccessor(), context.randomState());
        } catch (Exception ignored) {
            return NO_HEIGHT;
        }
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.ATOM.get();
    }
}
