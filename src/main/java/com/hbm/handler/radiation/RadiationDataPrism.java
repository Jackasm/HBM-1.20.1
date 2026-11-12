package com.hbm.handler.radiation;

import com.hbm.config.RadiationConfig;
import com.hbm.main.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Сеточное радиационное поле чанка.
 *
 * <p>Ячейка — 4×16×4 блоков; по вертикали сетка покрывает весь диапазон высот
 * измерения. Хранение — по ячейкам, чтение — трилинейная интерполяция по центрам
 * ячеек, включая ячейки соседних чанков, поэтому поле не имеет ступенек ни
 * внутри чанка, ни на границе.
 *
 * <p>Перенос — обмен по трём «положительным» направлениям (+X, +Y, +Z): каждая
 * пара ячеек обрабатывается ровно один раз, масса сохраняется.
 *
 * <p>Спад ленивый: чанк помнит игровое время последней симуляции и применяет
 * затухание за весь пропущенный интервал, поэтому выгруженный чанк не «замерзает».
 */
public class RadiationDataPrism implements IRadiationData {

    public static final int CELL_H = 4;
    public static final int CELL_V = 16;
    private static final int GRID_XZ = 16 / CELL_H;
    private static final float MAX_RADIATION = 100_000F;

    private static final int SPREAD_INTERVAL = 10;
    /** Доля разницы, которой обменивается пара ячеек за шаг. */
    private static final float DIFFUSION = 0.1F;
    private static final float DECAY_PER_TICK = 0.998F;
    private static final float DECAY_SUBTRACT = 0.005F;
    /**
     * Во сколько раз ослабляется перенос через полностью непрозрачную ячейку
     * (e^-10 ≈ 4.5e-5). Камень даёт ~0.3 непрозрачности, свинец — ~0.95.
     */
    private static final float SHIELD_STRENGTH = 10F;
    /** Ниже этого значения перенос не считаем. */
    private static final float MIN_TRANSFER = 0.001F;
    /** Порог, ниже которого чанк перестаёт участвовать в симуляции. */
    public static final float ACTIVE_THRESHOLD = 0.01F;

    private static final int NBT_VERSION = 3;

    private final Level level;
    private final ChunkPos chunkPos;
    private final int minY;
    private final int gridY;

    private final float[] radiation;
    /** Снимок поля на шаг диффузии и буфер приращений. */
    private final float[] prev;
    private final float[] next;
    /** Средняя непрозрачность ячейки (0 — воздух, 1 — глухой щит), считается лениво. */
    private final float[] opacity;
    private final boolean[] opacityReady;

    private long lastTick = -1;
    private long lastSpread = -1;
    private boolean dirty;

    // Bound only for the real chunk capability. Standalone PRISM instances
    // used by deterministic tests do not affect an unrelated loaded chunk.
    private Runnable markChunkUnsaved = () -> {};

    void bindChunkSaveMarker(Runnable marker) {
        this.markChunkUnsaved = java.util.Objects.requireNonNull(marker);
    }

    public RadiationDataPrism(Level level, ChunkPos pos, int minY, int height) {
        this.level = level;
        this.chunkPos = pos;
        this.minY = minY;
        this.gridY = Math.max(1, height / CELL_V);
        int size = GRID_XZ * this.gridY * GRID_XZ;
        this.radiation = new float[size];
        this.prev = new float[size];
        this.next = new float[size];
        this.opacity = new float[size];
        this.opacityReady = new boolean[size];
    }

    // ======== индексация ========

    private int index(int gx, int gy, int gz) {
        return (gx * gridY + gy) * GRID_XZ + gz;
    }

    private int gridYOf(int y) {
        int g = Math.floorDiv(y - minY, CELL_V);
        return g < 0 || g >= gridY ? -1 : g;
    }

    // ======== чтение ========

    @Override
    public float getRawCell(int localX, int localY, int localZ) {
        if (localX < 0 || localX >= GRID_XZ || localY < 0 || localY >= gridY
                || localZ < 0 || localZ >= GRID_XZ) {
            return 0;
        }
        return radiation[index(localX, localY, localZ)];
    }

    /**
     * Мировые координаты → ячейка. Индекс ячейки по X/Z — абсолютный (может уходить
     * в соседний чанк), по Y — локальный для чанка.
     */
    private float sampleCell(int cellX, int cellY, int cellZ) {
        if (cellY < 0 || cellY >= gridY) return 0;

        int cx = cellX >> 2;
        int cz = cellZ >> 2;
        int lx = cellX & 3;
        int lz = cellZ & 3;

        if (cx == chunkPos.x && cz == chunkPos.z) {
            return radiation[index(lx, cellY, lz)];
        }
        IRadiationData neighbor = RadiationCapability.getChunkData(level, new ChunkPos(cx, cz));
        return neighbor == null ? 0 : neighbor.getRawCell(lx, cellY, lz);
    }

    @Override
    public float getRadiation(int x, int y, int z) {
        // Координаты центров ячеек: по X/Z — (block - 2) / 4, по Y — (block - minY - 8) / 16.
        double ux = (x - 2) / (double) CELL_H;
        double uy = (y - minY - CELL_V / 2) / (double) CELL_V;
        double uz = (z - 2) / (double) CELL_H;

        int x0 = (int) Math.floor(ux);
        int y0 = (int) Math.floor(uy);
        int z0 = (int) Math.floor(uz);
        double tx = ux - x0;
        double ty = uy - y0;
        double tz = uz - z0;

        float c000 = sampleCell(x0, y0, z0);
        float c100 = sampleCell(x0 + 1, y0, z0);
        float c010 = sampleCell(x0, y0 + 1, z0);
        float c110 = sampleCell(x0 + 1, y0 + 1, z0);
        float c001 = sampleCell(x0, y0, z0 + 1);
        float c101 = sampleCell(x0 + 1, y0, z0 + 1);
        float c011 = sampleCell(x0, y0 + 1, z0 + 1);
        float c111 = sampleCell(x0 + 1, y0 + 1, z0 + 1);

        double c00 = c000 + (c100 - c000) * tx;
        double c10 = c010 + (c110 - c010) * tx;
        double c01 = c001 + (c101 - c001) * tx;
        double c11 = c011 + (c111 - c011) * tx;
        double c0 = c00 + (c10 - c00) * ty;
        double c1 = c01 + (c11 - c01) * ty;

        return (float) Mth.clamp(c0 + (c1 - c0) * tz, 0, MAX_RADIATION);
    }

    // ======== запись ========

    @Override
    public void addRadiation(int x, int y, int z, float amount) {
        int gy = gridYOf(y);
        // NaN и бесконечность отсекаем на входе: Mth.clamp их пропускает насквозь
        // (Math.max/min в Java возвращают NaN, если он есть в аргументах), и одна
        // деление на ноль в источнике отравило бы ячейку навсегда. PRISM в эталоне
        // от этого защищался, Simple — нет.
        if (gy < 0 || amount == 0 || !Float.isFinite(amount)) return;
        int i = index((x & 15) >> 2, gy, (z & 15) >> 2);
        float value = Mth.clamp(radiation[i] + amount, 0, MAX_RADIATION);
        if (value != radiation[i]) {
            radiation[i] = value;
            markDirty();
        }
    }

    @Override
    public float maxRadiation() {
        float max = 0;
        for (float value : radiation) {
            if (value > max) max = value;
        }
        return max;
    }

    @Override
    public void clear() {
        java.util.Arrays.fill(radiation, 0F);
        markDirty();
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    // ======== симуляция ========

    @Override
    public void tick(Level level, ChunkPos pos) {
        long now = level.getGameTime();
        if (lastTick < 0) {
            lastTick = now;
            // Empty halo chunks do not need a disk write just to persist a clock.
            if (maxRadiation() > 0F) markDirty();
        } else if (now > lastTick) {
            // Decay already visits every cell. Reuse that pass to detect
            // radiating chunks instead of scanning the entire grid a second
            // time on every tick of every active chunk.
            // Preserve field and lastTick together for save/unload/reload.
            boolean hadRadiation = decay((int) Math.min(now - lastTick, 1_000_000L));
            lastTick = now;
            if (hadRadiation) markDirty();
        }

        if (lastSpread < 0) lastSpread = now;
        if (now - lastSpread >= SPREAD_INTERVAL) {
            spread(level, pos);
            trySpawnRadFog(pos);
            lastSpread = now;
            markDirty();
        }
    }

    /** Затухание за dt тиков: r' = a^dt·r − b·(a^dt − 1)/(a − 1). */
    private boolean decay(int dt) {
        if (dt <= 0) return false;
        double factor = Math.pow(DECAY_PER_TICK, dt);
        double subtract = DECAY_SUBTRACT * (factor - 1.0D) / (DECAY_PER_TICK - 1.0D);
        boolean hadRadiation = false;

        for (int i = 0; i < radiation.length; i++) {
            float value = radiation[i];
            if (value <= 0) continue;
            hadRadiation = true;
            radiation[i] = Mth.clamp((float) (value * factor - subtract), 0, MAX_RADIATION);
        }
        return hadRadiation;
    }

    private void spread(Level level, ChunkPos pos) {
        System.arraycopy(radiation, 0, prev, 0, radiation.length);
        System.arraycopy(radiation, 0, next, 0, radiation.length);

        int baseX = pos.getMinBlockX();
        int baseZ = pos.getMinBlockZ();

        IRadiationData east = null;
        IRadiationData south = null;
        ChunkPos eastPos = new ChunkPos(pos.x + 1, pos.z);
        ChunkPos southPos = new ChunkPos(pos.x, pos.z + 1);

        for (int gx = 0; gx < GRID_XZ; gx++) {
            int x = baseX + gx * CELL_H;
            for (int gy = 0; gy < gridY; gy++) {
                int y = minY + gy * CELL_V;
                for (int gz = 0; gz < GRID_XZ; gz++) {
                    int z = baseZ + gz * CELL_H;
                    int i = index(gx, gy, gz);
                    float a = prev[i];

                    // Приращения считаются по снимку поля, а не по «живым» значениям:
                    // иначе порядок обхода (gx -> gy -> gz) даёт перекос и поле
                    // течёт в одну сторону сильнее, чем в другую.
                    if (gx + 1 < GRID_XZ) {
                        int j = index(gx + 1, gy, gz);
                        float b = prev[j];
                        if (a > MIN_TRANSFER || b > MIN_TRANSFER) {
                            float t = transfer(a, b, DIFFUSION * attenuation(i, x, y, z));
                            next[i] -= t;
                            next[j] += t;
                        }
                    } else {
                        if (east == null) east = RadiationCapability.getChunkData(level, eastPos);
                        if (east != null) {
                            int nx = x + CELL_H;
                            float b = east.getRawCell((nx & 15) >> 2, Math.floorDiv(y - minY, CELL_V), (z & 15) >> 2);
                            if (a > MIN_TRANSFER || b > MIN_TRANSFER) {
                                float t = transfer(a, b, DIFFUSION * attenuation(i, x, y, z));
                                if (t != 0) {
                                    next[i] -= t;
                                    east.addRadiation(nx, y, z, t);
                                    RadiationTicker.markRadiationArrived(level, eastPos);
                                }
                            }
                        }
                    }

                    if (gy + 1 < gridY) {
                        int j = index(gx, gy + 1, gz);
                        float b = prev[j];
                        if (a > MIN_TRANSFER || b > MIN_TRANSFER) {
                            float t = transfer(a, b, DIFFUSION * attenuation(i, x, y, z));
                            next[i] -= t;
                            next[j] += t;
                        }
                    }

                    if (gz + 1 < GRID_XZ) {
                        int j = index(gx, gy, gz + 1);
                        float b = prev[j];
                        if (a > MIN_TRANSFER || b > MIN_TRANSFER) {
                            float t = transfer(a, b, DIFFUSION * attenuation(i, x, y, z));
                            next[i] -= t;
                            next[j] += t;
                        }
                    } else {
                        if (south == null) south = RadiationCapability.getChunkData(level, southPos);
                        if (south != null) {
                            int nz = z + CELL_H;
                            float b = south.getRawCell((x & 15) >> 2, Math.floorDiv(y - minY, CELL_V), (nz & 15) >> 2);
                            if (a > MIN_TRANSFER || b > MIN_TRANSFER) {
                                float t = transfer(a, b, DIFFUSION * attenuation(i, x, y, z));
                                if (t != 0) {
                                    next[i] -= t;
                                    south.addRadiation(x, y, nz, t);
                                    RadiationTicker.markRadiationArrived(level, southPos);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < radiation.length; i++) {
            radiation[i] = Mth.clamp(next[i], 0, MAX_RADIATION);
        }
    }

    /** Сколько передать из a в b: положительное — в b, отрицательное — из b. */
    private static float transfer(float a, float b, float k) {
        float t = k * (a - b);
        if (t > a) t = a;
        if (-t > b) t = -b;
        return t;
    }
    /** Ослабление переноса материалом ячейки: 1 — прозрачно, ~4e-5 — глухой щит. */
    private float attenuation(int i, int x, int y, int z) {
        if (!opacityReady[i]) {
            opacity[i] = computeOpacity(x, y, z);
            opacityReady[i] = true;
        }
        return (float) Math.exp(-opacity[i] * SHIELD_STRENGTH);
    }

    /**
     * Непрозрачность ячейки — среднее по её блокам. Стены толщиной меньше ячейки
     * усредняются, но всё равно дают заметное ослабление.
     */
    private float computeOpacity(int cellX, int cellY, int cellZ) {
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        int maxY = minY + gridY * CELL_V;
        float sum = 0;

        for (int dx = 0; dx < CELL_H; dx++) {
            for (int dy = 0; dy < CELL_V; dy++) {
                int y = cellY + dy;
                if (y < minY || y >= maxY) {
                    sum += 1F;
                    continue;
                }
                for (int dz = 0; dz < CELL_H; dz++) {
                    mPos.set(cellX + dx, y, cellZ + dz);
                    sum += opacityOf(level.getBlockState(mPos));
                }
            }
        }
        return sum / (CELL_H * CELL_V * CELL_H);
    }

    /**
     * Непрозрачность блока. Взрывoстойкость — грубый, но дешёвый прокси плотности;
     * тяжёлые металлы HBM перекрывают её вручную, иначе свинец не отличался бы от камня.
     */
    private static float opacityOf(BlockState state) {
        if (state.isAir()) return 0F;
        if (state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS)) return 0.05F;
        if (state.is(BlockTags.IMPERMEABLE)) return 0.1F;

        String id = state.getBlock().getDescriptionId();
        if (id.contains("lead") || id.contains("tungsten") || id.contains("uranium")
                || id.contains("plutonium") || id.contains("bismuth") || id.contains("schrabidium")) {
            return 0.95F;
        }
        return Math.min(1F, state.getBlock().getExplosionResistance() / 20F);
    }

    /** Сбросить кеш непрозрачности ячейки, в которую попал изменённый блок. */
    @Override
    public void invalidateCell(int x, int y, int z) {
        int gy = gridYOf(y);
        if (gy < 0) return;
        opacityReady[index((x & 15) >> 2, gy, (z & 15) >> 2)] = false;
    }

    private void trySpawnRadFog(ChunkPos pos) {
        float maxRad = maxRadiation();
        if (maxRad <= RadiationConfig.FOG_RAD) return;
        if (level.random.nextInt(RadiationConfig.FOG_CH) != 0) return;
        if (!(level instanceof ServerLevel)) return;

        int x = pos.getMiddleBlockX() + level.random.nextInt(16) - 8;
        int z = pos.getMiddleBlockZ() + level.random.nextInt(16) - 8;
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + level.random.nextInt(5);

        CompoundTag data = new CompoundTag();
        data.putString("type", "radFog");
        data.putDouble("posX", x);
        data.putDouble("posY", y);
        data.putDouble("posZ", z);
        MainRegistry.proxy.effectNT(data);
    }

    // ======== NBT ========

    @Override public void markDirty() {
        dirty = true;
        // Mark BEFORE chunk save/unload eligibility is evaluated. Waiting
        // until ChunkEvent.Unload is not sufficient to guarantee persistence.
        markChunkUnsaved.run();
    }
    @Override public boolean isDirty() { return dirty; }
    @Override public void clearDirty() { dirty = false; }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ver", NBT_VERSION);
        tag.putInt("minY", minY);
        tag.putInt("gridY", gridY);
        tag.putLong("lastTick", lastTick);

        ByteBuffer buffer = ByteBuffer.allocate(radiation.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float value : radiation) {
            buffer.putFloat(value);
        }
        tag.putByteArray("rad", buffer.array());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.getInt("ver") != NBT_VERSION) return;
        if (tag.getInt("minY") != this.minY || tag.getInt("gridY") != this.gridY) return;

        byte[] bytes = tag.getByteArray("rad");
        if (bytes.length != radiation.length * 4) return;

        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < radiation.length; i++) {
            radiation[i] = Mth.clamp(buffer.getFloat(), 0, MAX_RADIATION);
        }
        lastTick = tag.getLong("lastTick");
    }
}
