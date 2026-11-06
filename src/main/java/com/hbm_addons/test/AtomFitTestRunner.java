package com.hbm_addons.test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.List;

/**
 * Стенд «атомка в грунте»: ищет атомку в регионе, догенерирует её чанки и
 * считает зазор между нижним слоем постройки и грунтом под ним.
 *
 * <p>Нижний слой — {@code bbox.minY} коробки структуры (низ подушки). Для
 * каждой колонки, где в этом слое есть блок постройки, ищется первый непустой
 * блок под ним: зазор — число пустых блоков между. Владелец 03.11.2026:
 * подушка должна уходить в грунт, то есть зазор по колонкам должен быть 0.</p>
 */
public class AtomFitTestRunner extends TestRunner {

    /** Регион поиска: 24×24 чанка вокруг прежней точки. */
    private static final int CX0 = -182;
    private static final int CZ0 = -188;
    private static final int SIDE = 24;

    private int stage = 0;
    private int cursor = 0;
    private final List<StructureStart> atoms = new ArrayList<>();
    private final List<int[]> genQueue = new ArrayList<>();
    private int genCursor = 0;

    public AtomFitTestRunner(net.minecraft.server.level.ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            int total = SIDE * SIDE;
            int end = Math.min(total, cursor + 16);
            for (; cursor < end; cursor++) {
                level.getChunkSource().getChunk(
                        CX0 + cursor % SIDE, CZ0 + cursor / SIDE, ChunkStatus.STRUCTURE_STARTS, true);
            }
            if (cursor >= total) {
                collectAtoms();
                progress("scan_done atoms=" + atoms.size());
                stage = 1;
            }
            return false;
        }

        if (stage == 1) {
            if (genQueue.isEmpty()) {
                if (atoms.isEmpty()) {
                    check("atom_found", false);
                    finish(false);
                    return true;
                }
                buildGenQueue();
            }
            int end = Math.min(genQueue.size(), genCursor + 4);
            for (; genCursor < end; genCursor++) {
                int[] c = genQueue.get(genCursor);
                level.getChunk(c[0], c[1]);
            }
            if (genCursor >= genQueue.size()) {
                progress("generate_done");
                stage = 2;
            }
            return false;
        }

        progress("measure");
        return measure();
    }

    private void collectAtoms() {
        for (int cx = CX0; cx < CX0 + SIDE; cx++) {
            for (int cz = CZ0; cz < CZ0 + SIDE; cz++) {
                var chunk = level.getChunk(cx, cz);
                for (StructureStart start : chunk.getAllStarts().values()) {
                    ResourceLocation id = level.registryAccess()
                            .registryOrThrow(Registries.STRUCTURE)
                            .getKey(start.getStructure());
                    if (id != null && id.toString().equals("hbm:desert_atom")) atoms.add(start);
                }
            }
        }
    }

    private void buildGenQueue() {
        var bb = atoms.get(0).getBoundingBox();
        for (int cx = (bb.minX() >> 4) - 1; cx <= (bb.maxX() >> 4) + 1; cx++) {
            for (int cz = (bb.minZ() >> 4) - 1; cz <= (bb.maxZ() >> 4) + 1; cz++) {
                genQueue.add(new int[]{cx, cz});
            }
        }
    }

    private boolean measure() {
        int columns = 0;
        double gapSum = 0;
        int gapMax = 0;
        int floating = 0;

        var bb = atoms.get(0).getBoundingBox();
        int baseY = bb.minY();

        for (int x = bb.minX(); x <= bb.maxX(); x++) {
            for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
                if (level.getBlockState(new BlockPos(x, baseY, z)).isAir()) continue;

                int ground = Integer.MIN_VALUE;
                for (int y = baseY - 1; y >= level.getMinBuildHeight(); y--) {
                    if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                        ground = y;
                        break;
                    }
                }
                if (ground == Integer.MIN_VALUE) continue;

                int gap = baseY - ground - 1;
                columns++;
                gapSum += gap;
                gapMax = Math.max(gapMax, gap);
                if (gap > 0) floating++;
            }
        }

        double avg = columns == 0 ? -1 : gapSum / columns;
        info("atom_origin", bb.minX() + " " + bb.minY() + " " + bb.minZ());
        info("columns", columns);
        info("avg_gap", String.format("%.2f", avg));
        info("max_gap", gapMax);
        info("floating_columns", floating);

        check("atom_found", true);
        check("platform_not_floating", avg >= 0 && avg <= 0.5);
        check("no_big_gap", gapMax <= 2);
        check("cushion_under_pad", cushionMissing() == 0);
        check("pad_solid", padVoidColumns() == 0);
        check("wire_orientation", wireWrong() == 0);

        metric("columns", columns);
        metric("avg_gap", String.format("%.2f", avg));
        metric("max_gap", gapMax);
        metric("floating", floating);
        metric("cushion_missing", cushionMissing());
        metric("pad_void_columns", padVoidColumns());
        metric("wire_wrong", wireWrong());
        finish(avg >= 0 && avg <= 0.5 && gapMax <= 2 && cushionMissing() == 0 && wireWrong() == 0);
        return true;
    }

    /** Колонки площадки, где сразу под низом подушки воздух — «подушка» не досыпана. */
    private int cushionMissing() {
        var bb = atoms.get(0).getBoundingBox();
        int baseY = bb.minY();
        int missing = 0;
        for (int x = bb.minX(); x <= bb.maxX(); x++) {
            for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
                if (level.getBlockState(new BlockPos(x, baseY - 1, z)).isAir()) missing++;
            }
        }
        return missing;
    }

    /**
     * Колонки площадки, где между низом подушки и её верхом остался воздух —
     * та самая пустота под этажеркой с бочками.
     */
    private int padVoidColumns() {
        var bb = atoms.get(0).getBoundingBox();
        int baseY = bb.minY();
        int padTopY = baseY + 4;
        int voids = 0;
        for (int x = bb.minX(); x <= bb.maxX(); x++) {
            for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
                for (int y = baseY; y <= padTopY; y++) {
                    if (level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                        voids++;
                        break;
                    }
                }
            }
        }
        return voids;
    }

    /**
     * Проволока должна лежать вдоль своего ряда: есть сосед по X — панель на
     * север, сосед по Z — на запад. Проверяем по всей площадке, а не только по
     * кольцу: ряд вдоль бочек идёт внутри и раньше оставался повёрнутым на 90°.
     */
    private int wireWrong() {
        var bb = atoms.get(0).getBoundingBox();
        int wrong = 0;
        java.util.Map<String, Integer> dist = new java.util.TreeMap<>();

        for (int x = bb.minX(); x <= bb.maxX(); x++) {
            for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
                for (int y = bb.minY(); y <= bb.maxY(); y++) {
                    var state = level.getBlockState(new BlockPos(x, y, z));
                    if (state.getBlock() != com.hbm.blocks.ModBlocks.BARBED_WIRE.get()) continue;
                    if (!state.hasProperty(com.hbm.blocks.generic.BarbedWire.FACING)) continue;

                    boolean alongX = wireAt(x - 1, y, z) || wireAt(x + 1, y, z);
                    boolean alongZ = wireAt(x, y, z - 1) || wireAt(x, y, z + 1);
                    var expected = alongZ && !alongX
                            ? net.minecraft.core.Direction.WEST
                            : net.minecraft.core.Direction.NORTH;
                    if (!alongX && !alongZ) {
                        // Одиночный блок на стыке рядов — должен смотреть на запад.
                        boolean diagonal = wireAt(x - 1, y, z - 1) || wireAt(x - 1, y, z + 1)
                                || wireAt(x + 1, y, z - 1) || wireAt(x + 1, y, z + 1);
                        if (!diagonal) continue;
                        expected = net.minecraft.core.Direction.WEST;
                    }
                    var actual = state.getValue(com.hbm.blocks.generic.BarbedWire.FACING);
                    dist.merge((alongZ && !alongX ? "runZ/" : "runX/") + actual.getName(), 1, Integer::sum);
                    if (actual != expected) wrong++;
                }
            }
        }
        info("wire_distribution", dist);
        return wrong;
    }

    private boolean wireAt(int x, int y, int z) {
        return level.getBlockState(new BlockPos(x, y, z)).getBlock() == com.hbm.blocks.ModBlocks.BARBED_WIRE.get();
    }
}
