package com.hbm_addons.test;

import com.hbm.datagen.worldgen.structure.cleanup.TemplateLayout;
import com.hbm.datagen.worldgen.structure.pieces.NBTStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Скан габарита NBT-структуры в мире (#190): что лежит внутри, чего нет в шаблоне.
 *
 * <p>Нужен, чтобы дефект «объём не вычищается» проверять числом, а не раскопками.
 * Находит старты структур в чанке точки, догенерирует чанки габарита и сравнивает
 * каждый блок с палитрой шаблона. Чужое считается отдельно выше подошвы — там,
 * где игрок и видит мусор.</p>
 *
 * <p>Точка задаётся свойствами {@code -Phbm.probe.x} и {@code -Phbm.probe.z}
 * (блоки), по умолчанию маяк {@code 13283, -7186}.</p>
 */
public class StructureProbeTestRunner extends TestRunner {

    private final int blockX;
    private final int blockZ;
    private int stage = 0;

    public StructureProbeTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
        this.blockX = Integer.getInteger("hbm.probe.x", 13283);
        this.blockZ = Integer.getInteger("hbm.probe.z", -7186);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (stage++ > 0) {
            finish(true);
            return true;
        }
        probe();
        return false;
    }

    private void probe() {
        ChunkPos chunkPos = new ChunkPos(blockX >> 4, blockZ >> 4);
        info("probe_point", blockX + "," + blockZ + " chunk=" + chunkPos.x + "," + chunkPos.z);

        boolean any = false;
        for (int cx = chunkPos.x - 4; cx <= chunkPos.x + 4 && !any; cx++) {
            for (int cz = chunkPos.z - 4; cz <= chunkPos.z + 4 && !any; cz++) {
                LevelChunk chunk = level.getChunk(cx, cz);
                for (StructureStart start : chunk.getAllStarts().values()) {
                    if (!start.isValid()) continue;
                    BoundingBox startBox = start.getBoundingBox();
                    if (blockX < startBox.minX() || blockX > startBox.maxX()) continue;
                    if (blockZ < startBox.minZ() || blockZ > startBox.maxZ()) continue;

                    for (StructurePiece piece : start.getPieces()) {
                        if (!(piece instanceof NBTStructurePiece nbtPiece)) continue;
                        any = true;
                        scan(nbtPiece);
                    }
                }
            }
        }
        if (!any) {
            progress("no_nbt_piece_at_point");
            metric("nbt_pieces", 0);
        }
    }

    private void scan(NBTStructurePiece piece) {
        BoundingBox box = piece.getBoundingBox();
        String name = piece.getClass().getSimpleName() + "@" + box.minX() + "," + box.minY() + "," + box.minZ();

        for (int cx = box.minX() >> 4; cx <= box.maxX() >> 4; cx++) {
            for (int cz = box.minZ() >> 4; cz <= box.maxZ() >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }

        TemplateLayout layout = piece.layout(level);
        Map<BlockPos, BlockState> expected = new HashMap<>(Math.max(16, layout.blocks().size()));
        for (Map.Entry<BlockPos, BlockState> entry : layout.blocks().entrySet()) {
            BlockPos world = piece.localToWorld(entry.getKey());
            if (world == null) continue;
            expected.put(world, piece.transformState(entry.getValue()));
        }

        Map<String, Integer> foreign = new TreeMap<>();
        Map<String, Integer> foreignAbove = new TreeMap<>();
        Map<String, Integer> foreignCovered = new TreeMap<>();
        Map<Long, Integer> topByColumn = new HashMap<>();
        for (BlockPos pos : expected.keySet()) {
            topByColumn.merge(net.minecraft.world.level.ChunkPos.asLong(pos.getX(), pos.getZ()),
                    pos.getY(), Math::max);
        }
        int expectedInside = 0;
        int foreignTotal = 0;
        int foreignAboveTotal = 0;
        int foreignCoveredTotal = 0;

        for (int px = box.minX(); px <= box.maxX(); px++) {
            for (int pz = box.minZ(); pz <= box.maxZ(); pz++) {
                int base = piece.cleanupBaseY(px, pz);
                Integer top = topByColumn.get(net.minecraft.world.level.ChunkPos.asLong(px, pz));
                for (int py = box.minY(); py <= box.maxY(); py++) {
                    BlockPos pos = new BlockPos(px, py, pz);
                    if (expected.containsKey(pos)) {
                        expectedInside++;
                        continue;
                    }
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue;
                    String id = state.getBlock().builtInRegistryHolder().key().location().toString();
                    foreign.merge(id, 1, Integer::sum);
                    foreignTotal++;
                    if (base >= 0 && py > base) {
                        foreignAbove.merge(id, 1, Integer::sum);
                        foreignAboveTotal++;
                    }
                    // Накрыто шаблоном: колонка с блоками шаблона, клетка ниже её верха.
                    if (base >= 0 && top != null && py > base && py < top) {
                        foreignCovered.merge(id, 1, Integer::sum);
                        foreignCoveredTotal++;
                    }
                }
            }
        }

        info("piece", name);
        metric("expected_blocks", expectedInside);
        metric("foreign_total", foreignTotal);
        metric("foreign_above_base", foreignAboveTotal);
        metric("foreign_covered", foreignCoveredTotal);
        metric("foreign_kinds", foreign.size());

        foreignCovered.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(15)
                .forEach(e -> info("foreign_covered", e.getKey() + "=" + e.getValue()));

        foreignAbove.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> info("foreign_above", e.getKey() + "=" + e.getValue()));
        foreign.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> info("foreign_all", e.getKey() + "=" + e.getValue()));

        info("point_column", column(box));
    }

    /** Столб блоков в точке владельца — чтобы видеть, что именно он раскопал. */
    private String column(BoundingBox box) {
        int y = box.minY();
        StringBuilder sb = new StringBuilder();
        for (int py = box.minY(); py <= box.maxY(); py++) {
            BlockState state = level.getBlockState(new BlockPos(blockX, py, blockZ));
            if (state.isAir()) continue;
            sb.append(py).append(':')
                    .append(state.getBlock().builtInRegistryHolder().key().location().getPath())
                    .append(' ');
        }
        return sb.toString();
    }
}
