package com.hbm.datagen.worldgen.feature;

import com.hbm.datagen.worldgen.feature.config.OreLayer3DConfiguration;
import com.hbm.datagen.worldgen.feature.noise.PerlinNoise2D;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.Random;

/**
 * Порт 1.7.10 {@code OreLayer3D}: двумерные поля шума, проход 16x16x(y 64..6),
 * замена камня. Шум — 4-октавный симплекс с нормировкой 70 (см. {@link PerlinNoise2D}),
 * сиды {@code seed + 101/102 + id}, как в оригинале.
 *
 * <p>Пороги 230/300/275 из 1.7.10 рассчитаны именно на такую амплитуду поля,
 * поэтому менять их вместе с шумом нельзя.
 */
public class OreLayer3DFeature extends Feature<OreLayer3DConfiguration> {

    private static final int MIN_Y = 6;
    private static final int MAX_Y = 64;
    private static final int OCTAVES = 4;

    public OreLayer3DFeature(Codec<OreLayer3DConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<OreLayer3DConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        OreLayer3DConfiguration config = context.config();

        // Фича должна писать только в свой чанк: иначе WorldGenRegion
        // ругается "setBlock in a far chunk" и блоки теряются.
        // В 1.7.10 окно прохода шло на 8 блоков в соседний чанк; здесь шум
        // сэмплируется по мировым координатам, поэтому поле то же самое.
        int chunkX = origin.getX() & ~15;
        int chunkZ = origin.getZ() & ~15;

        long seed = level.getLevel().getSeed();
        double scaleH = config.scaleH();
        double scaleV = config.scaleV();

        // Третье поле (seed + 103) в 1.7.10 считается в cacheZ, который при чтении
        // подменён на cacheX, — то есть мёртвый код. Не переносим.
        PerlinNoise2D noiseX = new PerlinNoise2D(new Random(seed + 101L + config.id()), OCTAVES);
        PerlinNoise2D noiseY = new PerlinNoise2D(new Random(seed + 102L + config.id()), OCTAVES);

        // Кэш по y, как в 1.7.10: поле "X" зависит от (y, z), а не от x.
        double[][] cacheX = new double[16][MAX_Y + 1];
        for (int o = 0; o < 16; o++) {
            for (int y = MAX_Y; y >= MIN_Y; y--) {
                cacheX[o][y] = noiseX.sample(y * scaleV, (chunkZ + o) * scaleH);
            }
        }

        boolean placed = false;
        for (int ox = 0; ox < 16; ox++) {
            int x = chunkX + ox;
            for (int oz = 0; oz < 16; oz++) {
                int z = chunkZ + oz;
                double ny = noiseY.sample(x * scaleH, z * scaleH);

                for (int y = MAX_Y; y >= MIN_Y; y--) {
                    double nx = cacheX[oz][y];
                    // В 1.7.10 Z-поле читается из того же кэша, что и X (копипаста),
                    // а cacheZ, посчитанный рядом, не используется. Это не опечатка порта:
                    // у полей общий первый аргумент (y * scaleV), из-за чего они коррелируют
                    // по высоте, и плотность руды держится на этой корреляции.
                    // С честным cacheZ порог 300 даёт не руду, а пустоту (см. ADR-0002).
                    double nz = cacheX[ox][y];
                    if (nx * ny * nz <= config.threshold()) continue;

                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.getBlockState(pos).is(Blocks.STONE)) continue;
                    if (!level.ensureCanWrite(pos)) continue;

                    level.setBlock(pos, config.state(), 2);
                    placed = true;
                }
            }
        }

        return placed;
    }
}
