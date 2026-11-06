package com.hbm_addons.test;

import com.hbm.datagen.worldgen.structure.cleanup.CleanablePiece;
import com.hbm.datagen.worldgen.structure.cleanup.TemplateLayout;
import com.hbm.datagen.worldgen.structure.pieces.ConformingNBTStructurePiece;
import com.hbm.datagen.worldgen.structure.pieces.NBTStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Стенд генерации структур: считает, что и где поставилось, проверяет опору
 * подошвы руин и что декор не растёт внутри NBT-структур (#102).
 *
 * <p>Полная генерация — вокруг каждой найденной NBT-структуры (плюс чанк запаса,
 * чтобы деревья соседей успели перекинуть листву), остальное сканируется до стадии
 * {@code STRUCTURE_STARTS}, иначе прогон по сотням чанков занимает минуты.</p>
 */
public class StructuresScanTestRunner extends TestRunner {

    /** Регион сканирования: 48×48 чанков = 16 клеток по 12×12. */
    private static final int CHUNKS = 48;
    private static final int BASE_CX = 33000;
    private static final int BASE_CZ = 33000;
    private static final int CHUNKS_PER_TICK = 8;

    private int cursor = 0;
    private int stage = 0;

    private int chunksScanned = 0;
    private final Map<String, Integer> startsByType = new TreeMap<>();
    private final Map<Long, Integer> structuresPerCell = new TreeMap<>();
    private final List<StructureStart> nbtStarts = new ArrayList<>();

    private final List<ChunkPos> fullGenChunks = new ArrayList<>();
    private int fullGenCursor = 0;
    private final Set<Long> queuedChunks = new LinkedHashSet<>();
    private final FarChunkCapture farChunk = new FarChunkCapture();

    private int nothingStarts = 0;
    private int mainStarts = 0;
    private int conformRuins = 0;
    private int flatRuins = 0;
    private int floatingColumns = 0;
    private int missingColumns = 0;
    private int maxSlope = 0;
    /** Колонки плоских структур и из них висящие в воздухе (#159). */
    private int flatColumns = 0;
    private int flatFloating = 0;
    private final Map<String, Integer> floatingFlatByStructure = new TreeMap<>();

    private int foreignLogs = 0;
    private int foreignLeaves = 0;
    private final Map<String, Integer> foreignByStructure = new TreeMap<>();

    public StructuresScanTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
        farChunk.attach();
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            int total = CHUNKS * CHUNKS;
            int end = Math.min(total, cursor + CHUNKS_PER_TICK);
            for (; cursor < end; cursor++) {
                int cx = BASE_CX + cursor % CHUNKS;
                int cz = BASE_CZ + cursor / CHUNKS;
                ChunkAccess chunk = level.getChunkSource()
                        .getChunk(cx, cz, ChunkStatus.STRUCTURE_STARTS, true);
                chunksScanned++;
                for (StructureStart start : chunk.getAllStarts().values()) {
                    String name = structureName(start.getStructure());
                    startsByType.merge(name, 1, Integer::sum);
                    if (hasNbtPiece(start)) nbtStarts.add(start);
                    if (name.startsWith("hbm:nothing")) {
                        nothingStarts++;
                    } else if (isMainStructure(name)) {
                        mainStarts++;
                        long cell = (long) (cx / 12) * 100000L + (cz / 12);
                        structuresPerCell.merge(cell, 1, Integer::sum);
                    }
                }
            }
            if (cursor >= total) {
                progress("scan_done");
                buildFullGenList();
                info("nbt_starts", nbtStarts.size());
                info("full_gen_chunks", fullGenChunks.size());
                stage = 1;
                cursor = 0;
            }
            return false;
        }

        if (stage == 1) {
            // Полная генерация чанков вокруг структур: блоки и декор проверяются
            // только в готовом чанке. По одному чанку за тик — иначе один тик
            // тянет несколько секунд и сервер ругается на watchdog.
            if (fullGenCursor >= fullGenChunks.size()) {
                stage = 2;
                return false;
            }
            ChunkPos pos = fullGenChunks.get(fullGenCursor++);
            level.getChunk(pos.x, pos.z);
            if (fullGenCursor % 32 == 0) {
                progress("generated " + fullGenCursor + "/" + fullGenChunks.size());
            }
            return false;
        }

        progress("checking");
        for (StructureStart start : nbtStarts) {
            checkStructure(start);
        }

        int cells = structuresPerCell.size();
        int cellsOverOne = (int) structuresPerCell.values().stream().filter(v -> v > 1).count();

        info("chunks_scanned", chunksScanned);
        info("structures", startsByType);
        info("ruins", nbtStarts.size());
        info("ruins_conforming", conformRuins);
        info("ruins_flat", flatRuins);
        info("floating_columns", floatingColumns);
        info("missing_columns", missingColumns);
        info("max_ruin_slope", maxSlope);
        info("flat_columns", flatColumns);
        info("flat_floating_columns", flatFloating);
        info("flat_floating_by_structure", floatingFlatByStructure);
        info("main_starts", mainStarts);
        info("nothing_starts", nothingStarts);
        info("cells_with_structures", cells);
        info("cells_over_one", cellsOverOne);
        info("foreign_logs", foreignLogs);
        info("foreign_leaves", foreignLeaves);
        info("foreign_by_structure", foreignByStructure);

        boolean ok = true;
        ok &= expect("scan_ran", chunksScanned == CHUNKS * CHUNKS);
        ok &= expect("structures_found", startsByType.values().stream().mapToInt(Integer::intValue).sum() > 0);
        ok &= expect("ruins_found", !nbtStarts.isEmpty());
        ok &= expect("ruins_conforming", conformRuins > 0);
        ok &= expect("no_floating_columns", floatingColumns == 0);
        ok &= expect("no_missing_columns", missingColumns == 0);
        ok &= expect("one_structure_per_cell", cellsOverOne == 0);
        ok &= expect("no_foreign_logs", foreignLogs == 0);
        ok &= expect("no_foreign_leaves", foreignLeaves == 0);
        ok &= expect("empty_outcomes_registered", emptyOutcomesRegistered());
        ok &= expect("grid_12_4", gridIs12x4());

        metric("chunks", chunksScanned);
        metric("starts", startsByType.values().stream().mapToInt(Integer::intValue).sum());
        metric("nbt_structures", nbtStarts.size());
        metric("conform", conformRuins);
        metric("floating", floatingColumns);
        metric("flat_columns", flatColumns);
        metric("flat_floating", flatFloating);
        metric("max_slope", maxSlope);
        metric("main", mainStarts);
        metric("empty", nothingStarts);
        metric("cells", cells);
        metric("over_one", cellsOverOne);
        metric("foreign_logs", foreignLogs);
        metric("foreign_leaves", foreignLeaves);

        // #156: страховка от возврата записи в чужие чанки. Ловим предупреждение
        // WorldGenRegion целиком, а не по источникам: любая строка — красный стенд.
        metric("far_chunk_lines", farChunk.count());
        ok &= expect("no_far_chunk_writes", farChunk.count() == 0);
        farChunk.detach();

        finish(ok);
        return true;
    }

    /** Чанки под полную генерацию: габарит каждой структуры плюс чанк запаса. */
    private void buildFullGenList() {
        for (StructureStart start : nbtStarts) {
            var bb = start.getBoundingBox();
            for (int cx = (bb.minX() >> 4) - 1; cx <= (bb.maxX() >> 4) + 1; cx++) {
                for (int cz = (bb.minZ() >> 4) - 1; cz <= (bb.maxZ() >> 4) + 1; cz++) {
                    if (queuedChunks.add(ChunkPos.asLong(cx, cz))) {
                        fullGenChunks.add(new ChunkPos(cx, cz));
                    }
                }
            }
        }
    }

    private void checkStructure(StructureStart start) {
        var bb = start.getBoundingBox();
        boolean conforming = start.getPieces().stream()
                .anyMatch(p -> p instanceof ConformingNBTStructurePiece);
        if (conforming) {
            conformRuins++;
            checkRuin(start);
        } else if (start.getPieces().stream().anyMatch(p -> p instanceof NBTStructurePiece)) {
            flatRuins++;
            for (StructurePiece piece : start.getPieces()) {
                if (piece instanceof NBTStructurePiece flat) {
                    checkFlat(flat, start);
                }
            }
        }
        checkForeignDecor(start);
        info("structure_at", structureName(start.getStructure()) + " " + bb.minX() + "," + bb.minY() + "," + bb.minZ());
    }

    /**
     * Подошва плоской (не конформной) NBT-структуры: у каждой колонки, где вообще
     * есть блоки, смотрим нижний блок и проверяем, что под ним не воздух.
     *
     * <p>Метрика отвечает на жалобу «висит в воздухе» (#159): в колонке под краем
     * подошвы блок есть, а опоры под ним нет. Вода опорой считается — иначе
     * океанские структуры давали бы ложные срабатывания.</p>
     */
    private void checkFlat(NBTStructurePiece piece, StructureStart start) {
        var bb = piece.getBoundingBox();
        for (int x = bb.minX(); x <= bb.maxX(); x++) {
            for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
                int lowest = Integer.MIN_VALUE;
                for (int y = bb.minY(); y <= bb.maxY(); y++) {
                    if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                        lowest = y;
                        break;
                    }
                }
                if (lowest == Integer.MIN_VALUE) continue;
                flatColumns++;
                if (level.getBlockState(new BlockPos(x, lowest - 1, z)).isAir()) {
                    flatFloating++;
                    floatingFlatByStructure.merge(structureName(start.getStructure()), 1, Integer::sum);
                }
            }
        }
    }

    /** Залётный декор внутри габарита: брёвна и листва вне палитры шаблона. */
    private void checkForeignDecor(StructureStart start) {
        int logs = 0;
        int leaves = 0;
        for (StructurePiece piece : start.getPieces()) {
            if (!(piece instanceof CleanablePiece cleanable)) continue;
            var box = cleanable.cleanupBox();
            TemplateLayout layout = cleanable.layout(level);
            Set<BlockPos> palette = new HashSet<>();
            for (BlockPos local : layout.blocks().keySet()) {
                BlockPos world = cleanable.localToWorld(local);
                if (world != null && box.isInside(world)) {
                    palette.add(world);
                }
            }
            for (int x = box.minX(); x <= box.maxX(); x++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    int base = cleanable.cleanupBaseY(x, z);
                    if (base < 0) continue;
                    for (int y = base + 1; y <= box.maxY(); y++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (palette.contains(pos)) continue;
                        BlockState state = level.getBlockState(pos);
                        if (state.is(BlockTags.LOGS)) {
                            logs++;
                        } else if (state.is(BlockTags.LEAVES)) {
                            leaves++;
                        }
                    }
                }
            }
        }
        if (logs + leaves > 0) {
            foreignByStructure.merge(structureName(start.getStructure()), logs + leaves, Integer::sum);
        }
        foreignLogs += logs;
        foreignLeaves += leaves;
    }

    private void checkRuin(StructureStart start) {
        var bb = start.getBoundingBox();

        int minH = Integer.MAX_VALUE;
        int maxH = Integer.MIN_VALUE;
        for (StructurePiece piece : start.getPieces()) {
            if (!(piece instanceof ConformingNBTStructurePiece ruin)) continue;

            var pbb = piece.getBoundingBox();
            for (int x = pbb.minX(); x <= pbb.maxX(); x++) {
                for (int z = pbb.minZ(); z <= pbb.maxZ(); z++) {
                    int baseY = ruin.baseYAt(x, z);
                    if (baseY < 0) continue;

                    int top = ruin.heightAt(x, z);
                    minH = Math.min(minH, top);
                    maxH = Math.max(maxH, top);

                    int lowest = Integer.MIN_VALUE;
                    for (int y = baseY; y <= pbb.maxY(); y++) {
                        if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                            lowest = y;
                            break;
                        }
                    }
                    if (lowest == Integer.MIN_VALUE) {
                        missingColumns++;
                    } else if (!level.getBlockState(new BlockPos(x, lowest - 1, z)).isSolid()) {
                        floatingColumns++;
                    }
                }
            }
        }
        if (minH != Integer.MAX_VALUE) {
            maxSlope = Math.max(maxSlope, maxH - minH);
        }
    }

    private boolean hasNbtPiece(StructureStart start) {
        return start.getPieces().stream()
                .anyMatch(p -> p instanceof NBTStructurePiece || p instanceof ConformingNBTStructurePiece);
    }

    private boolean isMainStructure(String name) {
        return name.startsWith("hbm:")
                && !name.startsWith("hbm:ore_cave")
                && !name.equals("hbm:schist_stratum")
                && !name.equals("hbm:oil_bubble")
                && !name.equals("hbm:sand_bubble")
                && !name.equals("hbm:desert_atom")
                && !name.equals("hbm:spaceship");
    }

    private boolean emptyOutcomesRegistered() {
        var sets = level.registryAccess().registryOrThrow(Registries.STRUCTURE_SET);
        var set = sets.get(new ResourceLocation("hbm", "overworld_set"));
        if (set == null) return false;
        return set.structures().stream().anyMatch(entry -> entry.structure().unwrapKey()
                .map(key -> key.location().getPath().startsWith("nothing"))
                .orElse(false));
    }

    private boolean gridIs12x4() {
        var sets = level.registryAccess().registryOrThrow(Registries.STRUCTURE_SET);
        var set = sets.get(new ResourceLocation("hbm", "overworld_set"));
        if (set == null || !(set.placement() instanceof RandomSpreadStructurePlacement placement)) return false;
        return placement.spacing() == 12 && placement.separation() == 4;
    }

    private String structureName(Structure structure) {
        ResourceKey<Structure> key = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getResourceKey(structure)
                .orElse(null);
        if (key == null) return "unknown";
        ResourceLocation id = key.location();
        return id.getNamespace() + ":" + id.getPath().replace("ntmruins_", "ntmruins");
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }

    /**
     * Ловит предупреждение Forge «Detected setBlock in a far chunk» целиком (#156).
     *
     * <p>Дефект в том, что структура пишет блоки в чанк вне региона генерации:
     * такой блок может не сохраниться. Поведением это не поймать — только логом,
     * поэтому страж живёт в стенде, а не в метриках.</p>
     */
    private static final class FarChunkCapture extends AbstractAppender {

        private final java.util.List<String> lines = new java.util.ArrayList<>();
        private org.apache.logging.log4j.core.Logger root;

        FarChunkCapture() {
            super("hbm-far-chunk-capture", null, null, true, null);
        }

        void attach() {
            start();
            root = (org.apache.logging.log4j.core.Logger) org.apache.logging.log4j.LogManager.getRootLogger();
            root.addAppender(this);
        }

        void detach() {
            if (root != null) {
                root.removeAppender(this);
                root = null;
            }
            stop();
        }

        @Override
        public void append(org.apache.logging.log4j.core.LogEvent event) {
            String message = event.getMessage() == null ? "" : event.getMessage().getFormattedMessage();
            if (message.contains("far chunk")) {
                lines.add(message);
            }
        }

        int count() {
            return lines.size();
        }
    }
}
