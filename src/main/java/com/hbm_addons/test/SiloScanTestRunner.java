package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.worldgen.component.SiloComponent;
import com.hbm.datagen.worldgen.structure.pieces.NTMFeaturesPiece;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/**
 * Поиск ракетных шахт по координатам (#152).
 *
 * <p>Шахта — под-фича {@code hbm:ntm_features} (1 из 10), поэтому `/locate`
 * её не выделяет. Стенд читает старты структур в регионе (только
 * {@code STRUCTURE_STARTS}, без генерации блоков) и печатает
 * {@code silo_at x,y,z} для каждого старта, в кусках которого есть
 * {@link SiloComponent}.</p>
 *
 * <p>Регион задаётся свойствами: {@code -Phbm.silo.cx=} и {@code -Phbm.silo.cz=}
 * (центр в чанках), {@code -Phbm.silo.chunks=} (сторона квадрата). По умолчанию
 * 325×325 чанков ≈ 5200 блоков — этого хватает на 3–6 шахт.</p>
 */
public class SiloScanTestRunner extends TestRunner {

    private static final int CHUNKS_PER_TICK = 64;

    private final int baseCx;
    private final int baseCz;
    private final int chunks;
    private final boolean probe;

    private int cursor = 0;
    private int silos = 0;
    private int features = 0;

    public SiloScanTestRunner(net.minecraft.server.level.ServerLevel level, String scenarioName) {
        super(level, scenarioName);
        this.baseCx = Integer.getInteger("hbm.silo.cx", 0);
        this.baseCz = Integer.getInteger("hbm.silo.cz", 0);
        this.chunks = Integer.getInteger("hbm.silo.chunks", 325);
        this.probe = Boolean.getBoolean("hbm.silo.probe");
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        int total = this.chunks * this.chunks;
        int end = Math.min(total, this.cursor + CHUNKS_PER_TICK);

        for (; this.cursor < end; this.cursor++) {
            int cx = this.baseCx + this.cursor % this.chunks;
            int cz = this.baseCz + this.cursor / this.chunks;

            ChunkAccess chunk = level.getChunkSource().getChunk(cx, cz, ChunkStatus.STRUCTURE_STARTS, true);
            if (chunk == null) continue;

            for (StructureStart start : chunk.getAllStarts().values()) {
                // ntm_features отличаем по куску: других стартов с
                // NTMFeaturesPiece в наборе нет.
                if (!hasFeaturePiece(start)) continue;

                this.features++;
                if (!isSilo(start)) continue;

                this.silos++;
                var bb = start.getBoundingBox();
                info("silo_at", bb.minX() + "," + bb.minY() + "," + bb.minZ()
                        + " (chunk " + cx + "," + cz + ", Y " + bb.minY() + ".." + bb.maxY() + ")");

                if (this.probe) probeSilo(bb);
            }
        }

        if (this.cursor >= total) {
            metric("silos", this.silos);
            metric("features", this.features);
            metric("chunks", total);
            info("silos_total", this.silos);
            info("features_total", this.features);
            finish(true);
            return true;
        }

        return false;
    }

    /**
     * Проверка площадки: дорос ли асфальт до поверхности и не засыпан ли он.
     * Генерирует чанки габарита целиком, ищет асфальт и считает колонки, где
     * над асфальтом лежит не воздух (то есть площадка закопана — #152).
     */
    private void probeSilo(net.minecraft.world.level.levelgen.structure.BoundingBox bb) {
        for (int cx = bb.minX() >> 4; cx <= bb.maxX() >> 4; cx++) {
            for (int cz = bb.minZ() >> 4; cz <= bb.maxZ() >> 4; cz++) {
                level.getChunk(cx, cz, ChunkStatus.FULL, true);
            }
        }

        int asphalt = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int covered = 0;

        for (int x = bb.minX(); x <= bb.maxX(); x++) {
            for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
                for (int y = bb.minY(); y <= bb.maxY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.getBlockState(pos).is(ModBlocks.ASPHALT.get())) continue;

                    asphalt++;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                    // Считаем только чужое: обвязка и люк — свои блоки hbm.
                    var above = level.getBlockState(pos.above());
                    if (!above.isAir() && !isModBlock(above)) covered++;
                }
            }
        }

        if (asphalt == 0) {
            info("silo_probe", bb.minX() + "," + bb.minZ() + " асфальта нет");
            return;
        }

        int surface = level.getHeight(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG,
                bb.minX(), bb.minZ());

        info("silo_probe", String.format("%d,%d асфальт Y %d..%d (%d блоков), засыпано %d, поверхность у угла %d",
                bb.minX(), bb.minZ(), minY, maxY, asphalt, covered, surface));
    }

    private boolean isModBlock(net.minecraft.world.level.block.state.BlockState state) {
        return state.getBlock().builtInRegistryHolder().key().location().getNamespace().equals("hbm");
    }

    private boolean hasFeaturePiece(StructureStart start) {        for (var piece : start.getPieces()) {
            if (piece instanceof NTMFeaturesPiece) return true;
        }
        return false;
    }

    private boolean isSilo(StructureStart start) {
        for (var piece : start.getPieces()) {
            if (piece instanceof NTMFeaturesPiece featuresPiece
                    && featuresPiece.component() instanceof SiloComponent) {
                return true;
            }
        }
        return false;
    }
}
