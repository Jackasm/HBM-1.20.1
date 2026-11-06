package com.hbm.datagen.worldgen.feature.noise;

import java.util.Random;

/**
 * Точный порт {@code net.minecraft.world.gen.NoiseGeneratorPerlin} 1.7.10:
 * сумма октав {@link SimplexNoise2D} с частотой {@code d1} и амплитудой {@code 1 / d1}.
 *
 * <p>Именно накопление четырёх октав (амплитуды 1, 2, 4, 8) даёт ту амплитуду поля,
 * под которую подобраны пороги OreLayer3D.
 */
public final class PerlinNoise2D {

    private final SimplexNoise2D[] octaves;

    public PerlinNoise2D(Random random, int octaves) {
        this.octaves = new SimplexNoise2D[octaves];
        for (int i = 0; i < octaves; i++) {
            this.octaves[i] = new SimplexNoise2D(random);
        }
    }

    public double sample(double x, double z) {
        double sum = 0.0D;
        double d1 = 1.0D;
        for (SimplexNoise2D octave : this.octaves) {
            sum += octave.sample(x * d1, z * d1) / d1;
            d1 /= 2.0D;
        }
        return sum;
    }
}
