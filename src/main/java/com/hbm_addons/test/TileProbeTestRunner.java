package com.hbm_addons.test;

import com.hbm.handler.MixinStats;
import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.TreeMap;

/**
 * Принудительно грузит плитку чанков (как {@code /forceload add}) и снимает
 * палитру вокруг точки — проверка «а не зависит ли генерация от порядка».
 *
 * <p>Стенд гоняет мётлу через {@code forceload}; сценарий {@code block_probe}
 * грузит чанки обычным {@code getChunk}. Если содержимое расходится, дело в
 * порядке/типе загрузки, а не в данных мира.</p>
 */
public class TileProbeTestRunner extends TestRunner {

    private static final int TILE_CX = intProperty("hbm.tile.cx", 0);
    private static final int TILE_CZ = intProperty("hbm.tile.cz", 0);
    private static final int TILE_SIZE = intProperty("hbm.tile.size", 8);
    private static final int WAIT_TICKS = intProperty("hbm.tile.wait", 200);
    private static final int RADIUS = intProperty("hbm.probe.r", 4);
    private static final int PX = intProperty("hbm.probe.x", 0);
    private static final int PY = intProperty("hbm.probe.y", 64);
    private static final int PZ = intProperty("hbm.probe.z", 0);

    private int stage = 0;
    private int waited = 0;
    private int warnings = 0;
    private CountingAppender appender;
    private Logger chunkLogger;

    public TileProbeTestRunner(net.minecraft.server.level.ServerLevel level, String scenario) {
        super(level, scenario);
        appender = new CountingAppender();
        appender.start();
        chunkLogger = (Logger) LogManager.getLogger("net.minecraft.world.level.chunk.LevelChunk");
        chunkLogger.addAppender(appender);
        MixinStats.reset();
    }

    /** Считает те же строки, что и сборщик стенда: осиротевшие теги DUMMY. */
    private final class CountingAppender extends AbstractAppender {
        CountingAppender() {
            super("hbm-tile-probe", null, null, false, Property.EMPTY_ARRAY);
        }

        @Override
        public void append(LogEvent event) {
            if (event.getMessage().getFormattedMessage().startsWith("Tried to load a DUMMY")) {
                warnings++;
            }
        }
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (stage == 0) {
            for (int dx = 0; dx < TILE_SIZE; dx++) {
                for (int dz = 0; dz < TILE_SIZE; dz++) {
                    level.setChunkForced(TILE_CX + dx, TILE_CZ + dz, true);
                }
            }
            progress("forced_" + (TILE_SIZE * TILE_SIZE));
            stage = 1;
            return false;
        }
        if (stage == 1) {
            waited++;
            if (waited < WAIT_TICKS) return false;
            progress("waited_" + waited);
            stage = 2;
            return false;
        }
        if (stage == 2) {
            report();
            for (int dx = 0; dx < TILE_SIZE; dx++) {
                for (int dz = 0; dz < TILE_SIZE; dz++) {
                    level.setChunkForced(TILE_CX + dx, TILE_CZ + dz, false);
                }
            }
            return true;
        }
        return true;
    }

    private void report() {
        BlockPos center = new BlockPos(PX, PY, PZ);
        Map<String, Integer> palette = new TreeMap<>();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    BlockState state = level.getBlockState(center.offset(dx, dy, dz));
                    String id = state.isAir() ? "air" : BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
                    palette.merge(id, 1, Integer::sum);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        palette.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(25)
                .forEach(e -> sb.append(e.getKey()).append('=').append(e.getValue()).append(' '));
        info("tile", TILE_CX + "," + TILE_CZ + " size " + TILE_SIZE);
        info("center", PX + "," + PY + "," + PZ);
        info("seed", level.getSeed());
        info("palette", sb.toString().trim());
        info("dummy_warnings", warnings);
        info("dropped_tags", MixinStats.droppedDummyTags());
        if (chunkLogger != null && appender != null) { chunkLogger.removeAppender(appender); appender.stop(); }
        finish(true);
    }

    private static int intProperty(String key, int fallback) {
        String raw = System.getProperty(key);
        if (raw == null || raw.isEmpty()) return fallback;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
