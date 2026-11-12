package com.hbm_addons.test;

import com.hbm.handler.radiation.IRadiationData;
import com.hbm.handler.radiation.RadiationCapability;
import com.hbm.handler.radiation.RadiationDataPrism;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Сохранение и загрузка поля через NBT.
 *
 * <p>Две вещи, которые раньше не проверялись: сериализация возвращает ту же сетку
 * (значения, {@code minY}, {@code gridY}, {@code lastTick}), и на загрузке
 * применяется ленивый спад за пропущенный интервал — поле не «замерзает» на
 * выгруженном чанке.</p>
 */
public class RadiationNbtTestRunner extends TestRunner {

    private static final int CHUNK_X = 41;
    private static final int CHUNK_Z = 41;
    private static final int GAP_TICKS = 1200;

    private final List<ChunkPos> forcedChunks = new ArrayList<>();

    public RadiationNbtTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (tickCount != 1) return false;

        forcedChunks.add(TestChunks.force(level, CHUNK_X, CHUNK_Z));
        ChunkPos pos = new ChunkPos(CHUNK_X, CHUNK_Z);
        IRadiationData source = RadiationCapability.getChunkData(level, pos);
        if (source == null) {
            check("chunk_loaded", false);
            finish(false);
            return true;
        }
        source.clear();

        int x = CHUNK_X * 16 + 8;
        int y = level.getMinBuildHeight() + 8 * RadiationDataPrism.CELL_V + 8;
        int z = CHUNK_Z * 16 + 8;
        source.addRadiation(x, y, z, 1234.5F);
        source.addRadiation(x + 4, y, z, 77.25F);

        CompoundTag tag = source.serializeNBT();

        // Отдельная структура того же чанка: загрузка «с нуля».
        IRadiationData target = new RadiationDataPrism(level, pos,
                level.getMinBuildHeight(), level.getHeight());
        target.deserializeNBT(tag);

        boolean sameCells = true;
        for (int gx = 0; gx < RadiationDataPrism.CELL_H; gx++) {
            for (int gz = 0; gz < RadiationDataPrism.CELL_H; gz++) {
                for (int gy = 0; gy < gridY(); gy++) {
                    if (source.getRawCell(gx, gy, gz) != target.getRawCell(gx, gy, gz)) {
                        sameCells = false;
                    }
                }
            }
        }

        check("cells_roundtrip", sameCells);
        check("minY_roundtrip", tag.getInt("minY") == level.getMinBuildHeight());
        check("gridY_roundtrip", tag.getInt("gridY") == gridY());
        check("lastTick_present", tag.contains("lastTick"));

        // Ленивый спад: сдвигаем lastTick в прошлое и грузим заново.
        CompoundTag rewound = tag.copy();
        long now = level.getGameTime();
        long lastTick = Math.max(0L, now - GAP_TICKS);
        rewound.putLong("lastTick", lastTick);

        IRadiationData reloaded = new RadiationDataPrism(level, pos,
                level.getMinBuildHeight(), level.getHeight());
        reloaded.deserializeNBT(rewound);
        float beforeTick = reloaded.getRawCell(2, 8, 2);
        reloaded.tick(level, pos);
        float afterTick = reloaded.getRawCell(2, 8, 2);

        long actualGap = Math.max(0L, now - lastTick);
        float factor = (float) Math.pow(0.998F, actualGap);
        float subtract = (float) (0.005F * (factor - 1.0D) / (0.998F - 1.0D));
        float expectedAfter = Math.max(0F, beforeTick * factor - subtract);
        float ratio = beforeTick > 0 ? afterTick / beforeTick : 1F;
        metric("gap_ticks", actualGap);
        metric("value_before_tick", String.format("%.3f", beforeTick));
        metric("value_after_tick", String.format("%.3f", afterTick));
        metric("expected_after_tick", String.format("%.3f", expectedAfter));
        metric("decay_ratio", String.format("%.4f", ratio));
        check("lazy_decay_applied", actualGap > 0 && afterTick < beforeTick);
        check("lazy_decay_matches_formula",
                Math.abs(afterTick - expectedAfter) <= Math.max(1.0E-4F, beforeTick * 1.0E-4F));
        check("lazy_decay_not_zero", afterTick > 0F);

        TestChunks.release(level, forcedChunks);
        finish(checks.stream().allMatch(c -> c.pass));
        return true;
    }

    private int gridY() {
        return Math.max(1, level.getHeight() / RadiationDataPrism.CELL_V);
    }
}
