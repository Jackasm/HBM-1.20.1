package com.hbm.handler.radiation;

import com.hbm.config.RadiationConfig;
import com.hbm.main.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;

public class RadiationDataPrism implements IRadiationData {
    private static final int GRID_SIZE = 4;
    private static final float MAX_RADIATION = 100_000F;
    private static final int SPREAD_INTERVAL = 10;

    // Коэффициенты распространения
    private static final float SPREAD_FACTOR = 0.5F;
    private static final float NEIGHBOR_CHUNK_SPREAD = 0.35F;
    private static final float DECAY_PER_TICK = 0.998F;
    private static final float DECAY_SUBTRACT = 0.005F;

    // Параметры сопротивления
    private static final float MAX_RESISTANCE = 1000F;
    private static final float RESISTANCE_SCALE = 5000F;

    private final float[][][] radiation = new float[GRID_SIZE][GRID_SIZE][GRID_SIZE];

    // Массивы сопротивлений по каждой оси
    private final float[][][] resistanceX = new float[GRID_SIZE][GRID_SIZE][GRID_SIZE];
    private final float[][][] resistanceY = new float[GRID_SIZE][GRID_SIZE][GRID_SIZE];
    private final float[][][] resistanceZ = new float[GRID_SIZE][GRID_SIZE][GRID_SIZE];

    private boolean dirty;
    private boolean resistancesCalculated;

    public RadiationDataPrism() {}

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
        // 1. Спад
        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                for (int z = 0; z < GRID_SIZE; z++)
                    radiation[x][y][z] = Mth.clamp(radiation[x][y][z] * DECAY_PER_TICK - DECAY_SUBTRACT, 0, MAX_RADIATION);

        // 2. Распространение раз в секунду
        if (level.getGameTime() % SPREAD_INTERVAL == 0) {
            spreadInternally();
            spreadToNeighborChunks(level, pos);
        }

        // 3. Эффект тумана
        trySpawnRadFog(level, pos);
        markDirty();
    }

    /** Рассчитать сопротивления для всех ячеек на основе блоков в чанке */
    public void calculateResistances(LevelChunk chunk) {
        if (resistancesCalculated) return;

        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        int chunkX = chunk.getPos().x << 4;
        int chunkZ = chunk.getPos().z << 4;

        for (int gx = 0; gx < GRID_SIZE; gx++) {
            for (int gy = 0; gy < GRID_SIZE; gy++) {
                for (int gz = 0; gz < GRID_SIZE; gz++) {
                    float sumResistX = 0, sumResistY = 0, sumResistZ = 0;
                    int blockCount = 0;

                    int startX = gx * 4;
                    int startY = gy * 16; // 4 ячейки по высоте, каждая 16 блоков
                    int startZ = gz * 4;

                    for (int dx = 0; dx < 4; dx++) {
                        for (int dy = 0; dy < 16; dy++) {
                            for (int dz = 0; dz < 4; dz++) {
                                int worldX = chunkX + startX + dx;
                                int worldY = startY + dy;
                                int worldZ = chunkZ + startZ + dz;

                                if (worldY < chunk.getMinBuildHeight() || worldY >= chunk.getMaxBuildHeight()) continue;

                                mPos.set(worldX, worldY, worldZ);
                                BlockState state = chunk.getBlockState(mPos);
                                if (state.isAir()) continue;

                                Block block = state.getBlock();
                                float resist = Math.min(block.getExplosionResistance(), MAX_RESISTANCE);

                                sumResistX += resist;
                                sumResistY += resist;
                                sumResistZ += resist;
                                blockCount++;
                            }
                        }
                    }

                    if (blockCount > 0) {
                        resistanceX[gx][gy][gz] = sumResistX / blockCount;
                        resistanceY[gx][gy][gz] = sumResistY / blockCount;
                        resistanceZ[gx][gy][gz] = sumResistZ / blockCount;
                    }
                }
            }
        }

        resistancesCalculated = true;
    }

    /** Внутреннее распространение с учётом сопротивления (все 26 соседей) */
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

                    // Собираем всех соседей
                    List<int[]> neighbors = new ArrayList<>();
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (dx == 0 && dy == 0 && dz == 0) continue;
                                int nx = x + dx, ny = y + dy, nz = z + dz;
                                if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE && nz >= 0 && nz < GRID_SIZE) {
                                    neighbors.add(new int[]{nx, ny, nz, Math.abs(dx) + Math.abs(dy) + Math.abs(dz)});
                                }
                            }
                        }
                    }

                    if (neighbors.isEmpty()) continue;

                    // Вычисляем веса с учётом сопротивления
                    float totalWeight = 0;
                    float[] weights = new float[neighbors.size()];

                    for (int i = 0; i < neighbors.size(); i++) {
                        int[] n = neighbors.get(i);
                        int nx = n[0], ny = n[1], nz = n[2];
                        int dist = n[3];

                        // Базовый вес по расстоянию
                        float baseWeight;
                        if (dist == 1) baseWeight = 1.0f;
                        else if (dist == 2) baseWeight = 0.5f;
                        else baseWeight = 0.25f;

                        // Сопротивление на пути
                        float resist = 0;
                        if (nx != x) resist += (resistanceX[x][y][z] + resistanceX[nx][ny][nz]) / 2f;
                        if (ny != y) resist += (resistanceY[x][y][z] + resistanceY[nx][ny][nz]) / 2f;
                        if (nz != z) resist += (resistanceZ[x][y][z] + resistanceZ[nx][ny][nz]) / 2f;

                        float attenuation = (float) Math.exp(-resist / RESISTANCE_SCALE);
                        weights[i] = baseWeight * attenuation;
                        totalWeight += weights[i];
                    }

                    if (totalWeight <= 0) continue;

                    newRad[x][y][z] -= totalSpread;

                    // Распределяем
                    for (int i = 0; i < neighbors.size(); i++) {
                        int[] n = neighbors.get(i);
                        float spread = totalSpread * (weights[i] / totalWeight);
                        newRad[n[0]][n[1]][n[2]] += spread;
                    }
                }
            }
        }

        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                for (int z = 0; z < GRID_SIZE; z++)
                    radiation[x][y][z] = Mth.clamp(newRad[x][y][z], 0, MAX_RADIATION);
    }

    /** Межчанковое распространение (8 направлений) */
    private void spreadToNeighborChunks(Level level, ChunkPos pos) {
        final float straightFactor = NEIGHBOR_CHUNK_SPREAD;        // 0.2
        final float diagonalFactor = straightFactor * 0.5F;        // 0.1 (было 0.25)
        final float cornerFactor = straightFactor * 0.25F;         // 0.05

        // Перебираем все граничные ячейки
        for (int gx = 0; gx < GRID_SIZE; gx++) {
            for (int gz = 0; gz < GRID_SIZE; gz++) {
                // Проверяем, является ли ячейка граничной
                boolean onMinX = (gx == 0);
                boolean onMaxX = (gx == GRID_SIZE - 1);
                boolean onMinZ = (gz == 0);
                boolean onMaxZ = (gz == GRID_SIZE - 1);

                if (!onMinX && !onMaxX && !onMinZ && !onMaxZ) continue; // внутренняя ячейка

                for (int gy = 0; gy < GRID_SIZE; gy++) {
                    float val = radiation[gx][gy][gz];
                    if (val <= 0.001f) continue;

                    // Для каждого соседнего чанка, с которым граничит эта ячейка
                    int[] dxVals = onMinX ? new int[]{-1} : onMaxX ? new int[]{1} : new int[]{};
                    int[] dzVals = onMinZ ? new int[]{-1} : onMaxZ ? new int[]{1} : new int[]{};

                    // Прямые соседи (по одной оси)
                    for (int dx : dxVals) {
                        spreadToNeighbor(level, pos, dx, 0, gx, gy, gz, straightFactor);
                    }
                    for (int dz : dzVals) {
                        spreadToNeighbor(level, pos, 0, dz, gx, gy, gz, straightFactor);
                    }

                    // Диагональные соседи (по двум осям)
                    for (int dx : dxVals) {
                        for (int dz : dzVals) {
                            spreadToNeighbor(level, pos, dx, dz, gx, gy, gz, diagonalFactor);
                        }
                    }
                }
            }
        }
    }

    private void spreadToNeighbor(Level level, ChunkPos pos, int dx, int dz,
                                  int gx, int gy, int gz, float factor) {
        ChunkPos neighborPos = new ChunkPos(pos.x + dx, pos.z + dz);
        if (!level.hasChunk(neighborPos.x, neighborPos.z)) return;

        IRadiationData neighbor = RadiationCapability.getChunkData(level, neighborPos);
        if (!(neighbor instanceof RadiationDataSimple nd)) return;

        float val = radiation[gx][gy][gz];
        float spread = val * factor;
        radiation[gx][gy][gz] -= spread;

        int ndGx = (dx == -1) ? GRID_SIZE - 1 : (dx == 1) ? 0 : gx;
        int ndGz = (dz == -1) ? GRID_SIZE - 1 : (dz == 1) ? 0 : gz;

        nd.addRadiationAt(ndGx, gy, ndGz, spread);
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
        tag.putBoolean("resistCalc", resistancesCalculated);
        // Сохраняем сопротивления
        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                for (int z = 0; z < GRID_SIZE; z++) {
                    tag.putFloat("rx" + x + "_" + y + "_" + z, resistanceX[x][y][z]);
                    tag.putFloat("ry" + x + "_" + y + "_" + z, resistanceY[x][y][z]);
                    tag.putFloat("rz" + x + "_" + y + "_" + z, resistanceZ[x][y][z]);
                }
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        for (int x = 0; x < GRID_SIZE; x++)
            for (int y = 0; y < GRID_SIZE; y++)
                for (int z = 0; z < GRID_SIZE; z++)
                    radiation[x][y][z] = tag.getFloat("r" + x + "_" + y + "_" + z);
        resistancesCalculated = tag.getBoolean("resistCalc");
        if (resistancesCalculated) {
            for (int x = 0; x < GRID_SIZE; x++)
                for (int y = 0; y < GRID_SIZE; y++)
                    for (int z = 0; z < GRID_SIZE; z++) {
                        resistanceX[x][y][z] = tag.getFloat("rx" + x + "_" + y + "_" + z);
                        resistanceY[x][y][z] = tag.getFloat("ry" + x + "_" + y + "_" + z);
                        resistanceZ[x][y][z] = tag.getFloat("rz" + x + "_" + y + "_" + z);
                    }
        }
    }
}