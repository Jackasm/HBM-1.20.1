package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.radiation.RadiationEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Locale;

/**
 * Фон вокруг атомки (#198): сколько бочонков в постройке и какое поле они дают.
 *
 * <p>Находит структуру {@code hbm:desert_atom} в районе точки, генерирует её чанки,
 * считает жёлтые бочонки и через {@link #WAIT_TICKS} тиков читает
 * {@link RadiationEvents#getRadiation}: у бочки и на расстояниях 8/16/32/64.
 * Если источники, поставленные генерацией, не тикают (#198), все числа нулевые.</p>
 *
 * <p>Точка задаётся свойствами {@code -Phbm.atom.x} и {@code -Phbm.atom.z}
 * (блоки), по умолчанию атомка стенда {@code 2816,-2048}.</p>
 */
public class AtomRadiationTestRunner extends TestRunner {

    /** Даём источникам время протикать: бочка даёт +5 раз в 20 тиков. */
    private static final int WAIT_TICKS = 120;

    private final int pointX;
    private final int pointZ;

    private int stage = 0;
    private int waited = 0;
    private BoundingBox box;
    private BlockPos anchor;
    private int barrels;
    private int waste;

    public AtomRadiationTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
        this.pointX = Integer.getInteger("hbm.atom.x", 2816);
        this.pointZ = Integer.getInteger("hbm.atom.z", -2048);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (stage == 0) {
            find();
            if (box == null) {
                return true;
            }
            stage = 1;
            return false;
        }
        if (stage == 1) {
            if (++waited < WAIT_TICKS) return false;
            measure();
            finish(true);
            return true;
        }
        return true;
    }

    private void find() {
        ChunkPos center = new ChunkPos(pointX >> 4, pointZ >> 4);
        StructureStart found = null;
        StructureStart nearest = null;
        long nearestDistance = Long.MAX_VALUE;

        for (int cx = center.x - 12; cx <= center.x + 12; cx++) {
            for (int cz = center.z - 12; cz <= center.z + 12; cz++) {
                var chunk = level.getChunk(cx, cz);
                for (StructureStart start : chunk.getAllStarts().values()) {
                    if (!start.isValid()) continue;
                    if (!isAtom(start.getStructure())) continue;
                    BoundingBox sb = start.getBoundingBox();
                    if (pointX >= sb.minX() && pointX <= sb.maxX()
                            && pointZ >= sb.minZ() && pointZ <= sb.maxZ()) {
                        found = start;
                        break;
                    }
                    long dx = (sb.minX() + sb.maxX()) / 2 - pointX;
                    long dz = (sb.minZ() + sb.maxZ()) / 2 - pointZ;
                    long distance = dx * dx + dz * dz;
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = start;
                    }
                }
                if (found != null) break;
            }
            if (found != null) break;
        }

        if (found == null) found = nearest;

        if (found == null) {
            info("atom", "not_found");
            metric("atom_found", 0);
            finish(false);
            return;
        }

        box = found.getBoundingBox();
        for (int cx = box.minX() >> 4; cx <= box.maxX() >> 4; cx++) {
            for (int cz = box.minZ() >> 4; cz <= box.maxZ() >> 4; cz++) {
                level.getChunk(cx, cz);
                level.setChunkForced(cx, cz, true);
            }
        }

        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                for (int y = box.minY(); y <= box.maxY(); y++) {
                    BlockState state = level.getBlockState(new BlockPos(x, y, z));
                    if (state.is(ModBlocks.YELLOW_BARREL.get())) {
                        barrels++;
                        if (anchor == null) anchor = new BlockPos(x, y, z);
                    } else if (state.is(ModBlocks.BLOCK_WASTE.get())) {
                        waste++;
                    }
                }
            }
        }

        info("atom", box.minX() + "," + box.minY() + "," + box.minZ() + ".."
                + box.maxX() + "," + box.maxY() + "," + box.maxZ());
        metric("atom_found", 1);
        metric("barrels", barrels);
        metric("waste", waste);
        if (anchor == null) {
            info("barrel", "none");
            anchor = new BlockPos(box.minX(), box.minY(), box.minZ());
        }
    }

    private void measure() {
        double atBarrel = rad(anchor);
        metric("rad_barrel", round(atBarrel));
        for (int distance : new int[]{8, 16, 32, 64}) {
            metric("rad_" + distance, round(rad(anchor.offset(distance, 0, 0))));
        }
        metric("rad_y_top", round(rad(anchor.above(20))));
        metric("wait_ticks", waited);
        info("anchor", anchor.getX() + "," + anchor.getY() + "," + anchor.getZ());
        progress("rad_barrel=" + round(atBarrel));
    }

    private double rad(BlockPos pos) {
        return RadiationEvents.getRadiation(level, pos);
    }

    private boolean isAtom(Structure structure) {
        return "hbm:desert_atom".equals(idOf(structure));
    }

    private String idOf(Structure structure) {
        var key = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getResourceKey(structure).orElse(null);
        return key == null ? "unknown" : key.location().toString();
    }

    private static double round(double value) {
        return Double.parseDouble(String.format(Locale.ROOT, "%.4f", value));
    }
}
