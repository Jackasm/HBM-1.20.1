package com.hbm.datagen.worldgen.feature.noise;

import java.util.Random;

/**
 * Точный порт двумерного симплекс-шума 1.7.10
 * ({@code net.minecraft.world.gen.NoiseGeneratorSimplex}, метод {@code func_151605_a}).
 *
 * <p>Нормировка 70 сидит внутри самого шума, а не снаружи: именно поэтому в 1.7.10
 * амплитуда вызова — сотни, и пороги OreLayer3D (230/300/275) имеют смысл.
 */
public final class SimplexNoise2D {

    private static final int[][] GRAD = {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };
    private static final double SQRT3 = Math.sqrt(3.0D);
    private static final double F2 = 0.5D * (SQRT3 - 1.0D);
    private static final double G2 = (3.0D - SQRT3) / 6.0D;

    private final int[] perm = new int[512];
    private final double xo;
    private final double yo;

    public SimplexNoise2D(Random random) {
        this.xo = random.nextDouble() * 256.0D;
        this.yo = random.nextDouble() * 256.0D;
        random.nextDouble(); // в оригинале третья координата смещения тоже вытягивается из Random

        for (int i = 0; i < 256; i++) {
            this.perm[i] = i;
        }
        for (int l = 0; l < 256; ++l) {
            int j = random.nextInt(256 - l) + l;
            int k = this.perm[l];
            this.perm[l] = this.perm[j];
            this.perm[j] = k;
            this.perm[l + 256] = this.perm[l];
        }
    }

    private static int floor(double value) {
        return value > 0.0D ? (int) value : (int) value - 1;
    }

    private static double dot(int[] grad, double x, double y) {
        return (double) grad[0] * x + (double) grad[1] * y;
    }

    public double sample(double xin, double yin) {
        double s = (xin + yin) * F2;
        int i = floor(xin + s);
        int j = floor(yin + s);
        double t = (double) (i + j) * G2;
        double x0 = (double) i - t;
        double y0 = (double) j - t;
        double x1 = xin - x0;
        double y1 = yin - y0;

        int i1;
        int j1;
        if (x1 > y1) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        double x2 = x1 - (double) i1 + G2;
        double y2 = y1 - (double) j1 + G2;
        double x3 = x1 - 1.0D + 2.0D * G2;
        double y3 = y1 - 1.0D + 2.0D * G2;

        int ii = i & 255;
        int jj = j & 255;
        int gi0 = this.perm[ii + this.perm[jj]] % 12;
        int gi1 = this.perm[ii + i1 + this.perm[jj + j1]] % 12;
        int gi2 = this.perm[ii + 1 + this.perm[jj + 1]] % 12;

        double t0 = 0.5D - x1 * x1 - y1 * y1;
        double n0 = t0 < 0.0D ? 0.0D : (t0 *= t0) * t0 * dot(GRAD[gi0], x1, y1);
        double t1 = 0.5D - x2 * x2 - y2 * y2;
        double n1 = t1 < 0.0D ? 0.0D : (t1 *= t1) * t1 * dot(GRAD[gi1], x2, y2);
        double t2 = 0.5D - x3 * x3 - y3 * y3;
        double n2 = t2 < 0.0D ? 0.0D : (t2 *= t2) * t2 * dot(GRAD[gi2], x3, y3);

        return 70.0D * (n0 + n1 + n2);
    }
}
