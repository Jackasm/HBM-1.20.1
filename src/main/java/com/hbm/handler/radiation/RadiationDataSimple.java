package com.hbm.handler.radiation;

import com.hbm.config.RadiationConfig;
import com.hbm.main.MainRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;

public class RadiationDataSimple implements IRadiationData {
    private static final int GRID_SIZE = 4;               // 4x4x4 = 64 ячейки на чанк
    private static final float MAX_RADIATION = 100_000F;
    private static final int SPREAD_INTERVAL = 20;        // каждые 20 тиков (1 сек)
    private static final float SPREAD_FACTOR = 0.5F;      // 50% от ячейки уходит соседям
    private static final float NEIGHBOR_CHUNK_SPREAD = 0.2F; // 20% уходит в соседний чанк для граничных ячеек

    private static final float DECAY_PER_TICK = 0.995F;    // было 0.99
    private static final float DECAY_SUBTRACT = 0.01F;     // было 0.05

    private final float[][][] radiation = new float[GRID_SIZE][GRID_SIZE][GRID_SIZE];
    private boolean dirty;

    public RadiationDataSimple() {}

    @Override
    public float getRadiation(int gx, int gy, int gz) {
        if (gx < 0 || gx >= GRID_SIZE || gy < 0 || gy >= GRID_SIZE || gz < 0 || gz >= GRID_SIZE) return 0;
        return Mth.clamp(radiation[gx][gy][gz], 0, MAX_RADIATION);
    }

    @Override
    public void addRadiationAt(int gx, int gy, int gz, float amount) {
        if (gx < 0 || gx >= GRID_SIZE || gy < 0 || gy >= GRID_SIZE || gz < 0 || gz >= GRID_SIZE) return;
        radiation[gx][gy][gz] = Mth.clamp(radiation[gx][gy][gz] + amount, 0, MAX_RADIATION);
        markDirty();
    }

    @Override
    public void tick(Level level, ChunkPos pos) {
        // 1. Замедленный спад
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                for (int z = 0; z < GRID_SIZE; z++) {
                    float r = radiation[x][y][z];
                    r = Mth.clamp(r * DECAY_PER_TICK - DECAY_SUBTRACT, 0, MAX_RADIATION);
                    radiation[x][y][z] = r;
                }
            }
        }

        // 2. Распространение
        if (level.getGameTime() % SPREAD_INTERVAL == 0) {
            spreadInternally();
            spreadToNeighborChunks(level, pos);
        }

        trySpawnRadFog(level, pos);
        markDirty();
    }

    /** Распространение между ячейками внутри чанка (все 26 соседей) */
    private void spreadInternally() {
        float[][][] newRad = new float[GRID_SIZE][GRID_SIZE][GRID_SIZE];
        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                System.arraycopy(radiation[x][y], 0, newRad[x][y], 0, GRID_SIZE);

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                for (int z = 0; z < GRID_SIZE; z++) {
                    float val = radiation[x][y][z];
                    if (val <= 0.001f) continue;

                    float totalSpread = val * SPREAD_FACTOR;

                    // Собираем всех соседей в радиусе 1 (максимум 26)
                    List<int[]> neighbors = new ArrayList<>();
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (dx == 0 && dy == 0 && dz == 0) continue;
                                int nx = x + dx;
                                int ny = y + dy;
                                int nz = z + dz;
                                if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE && nz >= 0 && nz < GRID_SIZE) {
                                    neighbors.add(new int[]{nx, ny, nz, Math.abs(dx) + Math.abs(dy) + Math.abs(dz)});
                                }
                            }
                        }
                    }

                    if (neighbors.isEmpty()) continue;

                    // Вычисляем сумму весов для нормировки
                    float totalWeight = 0;
                    for (int[] n : neighbors) {
                        int dist = n[3]; // 1 = прямой, 2 = диагональ 2D, 3 = диагональ 3D
                        if (dist == 1) totalWeight += 1.0f;      // прямой сосед
                        else if (dist == 2) totalWeight += 0.5f; // диагональ по 2 осям
                        else totalWeight += 0.25f;               // диагональ по 3 осям
                    }

                    newRad[x][y][z] -= totalSpread;

                    // Распределяем пропорционально весам
                    for (int[] n : neighbors) {
                        int nx = n[0], ny = n[1], nz = n[2];
                        int dist = n[3];

                        float weight;
                        if (dist == 1) weight = 1.0f;
                        else if (dist == 2) weight = 0.5f;
                        else weight = 0.25f;

                        float spread = totalSpread * (weight / totalWeight);
                        newRad[nx][ny][nz] += spread;
                    }
                }
            }
        }

        // Применяем и ограничиваем
        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                for (int z = 0; z < GRID_SIZE; z++)
                    radiation[x][y][z] = Mth.clamp(newRad[x][y][z], 0, MAX_RADIATION);
    }

    /** Распространение на соседние чанки (8 направлений: прямые и диагонали) */
    private void spreadToNeighborChunks(Level level, ChunkPos pos) {
        final float straightFactor = NEIGHBOR_CHUNK_SPREAD;
        final float diagonalFactor = straightFactor * 0.25F;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;

                ChunkPos neighborPos = new ChunkPos(pos.x + dx, pos.z + dz);
                if (!level.hasChunk(neighborPos.x, neighborPos.z)) continue;

                IRadiationData neighbor = RadiationCapability.getChunkData(level, neighborPos);
                if (!(neighbor instanceof RadiationDataSimple nd)) continue;

                boolean isDiagonal = (dx != 0 && dz != 0);
                float factor = isDiagonal ? diagonalFactor : straightFactor;

                int srcGridXStart, srcGridXEnd, dstGridX;
                int srcGridZStart, srcGridZEnd, dstGridZ;

                if (dx == -1) { // запад
                    srcGridXStart = 0; srcGridXEnd = 0;
                    dstGridX = GRID_SIZE - 1;
                } else if (dx == 1) { // восток
                    srcGridXStart = GRID_SIZE - 1; srcGridXEnd = GRID_SIZE - 1;
                    dstGridX = 0;
                } else {
                    srcGridXStart = 0; srcGridXEnd = GRID_SIZE - 1;
                    dstGridX = 0;
                }

                if (dz == -1) { // север
                    srcGridZStart = 0; srcGridZEnd = 0;
                    dstGridZ = GRID_SIZE - 1;
                } else if (dz == 1) { // юг
                    srcGridZStart = GRID_SIZE - 1; srcGridZEnd = GRID_SIZE - 1;
                    dstGridZ = 0;
                } else {
                    srcGridZStart = 0; srcGridZEnd = GRID_SIZE - 1;
                    dstGridZ = 0;
                }

                for (int gx = srcGridXStart; gx <= srcGridXEnd; gx++) {
                    for (int gz = srcGridZStart; gz <= srcGridZEnd; gz++) {
                        for (int gy = 0; gy < GRID_SIZE; gy++) {
                            float val = radiation[gx][gy][gz];
                            if (val <= 0.001f) continue;

                            float spread = val * factor;
                            radiation[gx][gy][gz] -= spread;

                            int ndGx = (dx == -1) ? dstGridX : (dx == 1) ? dstGridX : gx;
                            int ndGz = (dz == -1) ? dstGridZ : (dz == 1) ? dstGridZ : gz;

                            nd.addRadiationAt(ndGx, gy, ndGz, spread);
                        }
                    }
                }
            }
        }
    }

    private void trySpawnRadFog(Level level, ChunkPos pos) {
        float maxRad = 0;
        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                for (int z = 0; z < GRID_SIZE; z++)
                    if (radiation[x][y][z] > maxRad) maxRad = radiation[x][y][z];

        if (maxRad <= RadiationConfig.fogRad.get()) return;
        if (level.random.nextInt(RadiationConfig.fogCh.get()) != 0) return;
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

    @Override public void markDirty() { dirty = true; }
    public boolean isDirty() { return dirty; }
    public void clearDirty() { dirty = false; }

    // ======== NBT ========
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                for (int z = 0; z < GRID_SIZE; z++)
                    tag.putFloat("r" + x + "_" + y + "_" + z, radiation[x][y][z]);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                for (int z = 0; z < GRID_SIZE; z++)
                    radiation[x][y][z] = tag.getFloat("r" + x + "_" + y + "_" + z);
    }
}