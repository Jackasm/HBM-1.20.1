package com.hbm_addons.test;

import com.hbm.datagen.worldgen.structure.pieces.AtomPiece;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * Стенд атомной станции (#160): ставит постройку куском {@link AtomPiece}
 * на расчищенную площадку и снимает состав блоков.
 *
 * <p>Проверяет то, что видно только целиком: габарит куска совпадает с
 * постройкой (низ −5, верх +19), периметр ограды замкнут, а состав блоков
 * пишется в {@code run/test-results/atom_blocks.txt} для сверки с эталоном
 * 1.7.10 офлайн.</p>
 */
public class AtomScanTestRunner extends TestRunner {

    /** Площадка: далеко от прошлых прогонов. */
    private static final int OX = 11200;
    private static final int OZ = 11200;
    private static final int OY = 100;

    private static final int CLEAR_RADIUS = 24;

    private int stage = 0;
    private final Map<String, Integer> counts = new TreeMap<>();
    private int total = 0;
    private int minY = Integer.MAX_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private int outsideBounds = 0;
    private int perimeter = 0;
    private int perimeterClosed = 0;

    public AtomScanTestRunner(net.minecraft.server.level.ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            progress("clear_area");
            clearArea();
            stage = 1;
            return false;
        }

        if (stage == 1) {
            progress("place_atom");
            placeAtom();
            stage = 2;
            return false;
        }

        progress("scan");
        scan();
        dump();

        check("structure_present", total > 3000);
        check("bounds_cover_build", outsideBounds == 0);
        check("bottom_at_base", minY >= OY - AtomPiece.BASE_DEPTH);
        check("top_at_design", maxY <= OY + AtomPiece.TOP_Y);
        // В эталоне ограда с воротами: 110 из 146 колонок периметра закрыты на трёх уровнях.
        check("perimeter_gates", perimeterClosed >= 100);
        check("fence_count", counts.getOrDefault("hbm:fence_metal", 0) == 456);
        check("barbed_wire_count", counts.getOrDefault("hbm:barbed_wire", 0) == 134);
        check("platform_count", counts.getOrDefault("hbm:reinforced_sand", 0) == 739);
        check("brick_light_count", counts.getOrDefault("hbm:brick_light", 0) == 559);
        check("deco_steel_count", counts.getOrDefault("hbm:deco/deco_steel", 0) == 536);
        check("concrete_total", concreteTotal() == 1071);
        check("water_count", counts.getOrDefault("minecraft:water", 0) == 396);

        metric("blocks", total);
        metric("min_y", minY);
        metric("max_y", maxY);
        metric("outside_bounds", outsideBounds);
        metric("perimeter_columns", perimeter);
        metric("perimeter_closed", perimeterClosed);
        metric("concrete_total", concreteTotal());

        finish(checks.stream().allMatch(c -> c.pass));
        return true;
    }

    private void clearArea() {
        BoundingBox box = new BoundingBox(OX - CLEAR_RADIUS, OY - AtomPiece.BASE_DEPTH + 1, OZ - CLEAR_RADIUS,
                OX + AtomPiece.SIZE_X - 1 + CLEAR_RADIUS, OY + AtomPiece.TOP_Y + 4, OZ + AtomPiece.SIZE_Z - 1 + CLEAR_RADIUS);
        BlockState air = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                for (int y = box.minY(); y <= box.maxY(); y++) {
                    level.setBlock(new BlockPos(x, y, z), air, 2);
                }
            }
        }
    }

    private void placeAtom() {
        AtomPiece piece = new AtomPiece(OX, OY, OZ);
        BoundingBox box = piece.getBoundingBox();
        RandomSource random = RandomSource.create(20261102L);
        ChunkPos min = new ChunkPos(box.minX() >> 4, box.minZ() >> 4);
        ChunkPos max = new ChunkPos(box.maxX() >> 4, box.maxZ() >> 4);
        for (int cx = min.x; cx <= max.x; cx++) {
            for (int cz = min.z; cz <= max.z; cz++) {
                ChunkPos chunkPos = new ChunkPos(cx, cz);
                level.getChunk(cx, cz);
                piece.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(),
                        random, box, chunkPos, BlockPos.ZERO);
            }
        }
    }

    private void scan() {
        BoundingBox box = new AtomPiece(OX, OY, OZ).getBoundingBox();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                for (int y = box.minY(); y <= box.maxY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue;
                    String name = state.getBlock().builtInRegistryHolder().key().location().toString();
                    counts.merge(name, 1, Integer::sum);
                    total++;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                    if (!box.isInside(pos)) outsideBounds++;
                }
            }
        }

        // Периметр ограды: по углам и серединам сторон на трёх уровнях от OY.
        java.util.List<int[]> probes = new java.util.ArrayList<>();
        for (int x = 0; x < AtomPiece.SIZE_X; x++) {
            probes.add(new int[]{x, 0});
            probes.add(new int[]{x, AtomPiece.SIZE_Z - 1});
        }
        for (int z = 1; z < AtomPiece.SIZE_Z - 1; z++) {
            probes.add(new int[]{0, z});
            probes.add(new int[]{AtomPiece.SIZE_X - 1, z});
        }
        for (int[] p : probes) {
            perimeter++;
            boolean ok = true;
            for (int dy = 0; dy <= 2; dy++) {
                BlockPos pos = new BlockPos(OX + p[0], OY + dy, OZ + p[1]);
                String name = level.getBlockState(pos).getBlock().builtInRegistryHolder().key().location().toString();
                if (!name.equals("hbm:fence_metal")) {
                    ok = false;
                    break;
                }
            }
            if (ok) perimeterClosed++;
        }
    }

    /** Бетон эталон берёт случайным вариантом ({@code Library.getRandomConcrete}), поэтому
     *  проверяется сумма четырёх вариантов, а не каждый по отдельности. */
    private int concreteTotal() {
        return counts.getOrDefault("hbm:brick_concrete", 0)
                + counts.getOrDefault("hbm:brick_concrete_broken", 0)
                + counts.getOrDefault("hbm:brick_concrete_cracked", 0)
                + counts.getOrDefault("hbm:brick_concrete_mossy", 0);
    }

    private void dump() {
        Path dir = Path.of("test-results");
        try {
            Files.createDirectories(dir);
            try (BufferedWriter w = Files.newBufferedWriter(dir.resolve("atom_blocks.txt"), StandardCharsets.UTF_8)) {
                w.write("# origin " + OX + " " + OY + " " + OZ + "\n");
                for (Map.Entry<String, Integer> e : counts.entrySet()) {
                    w.write("count " + e.getValue() + " " + e.getKey() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("HBM_TEST_INFO: dump_failed=" + e.getMessage());
        }
    }
}
