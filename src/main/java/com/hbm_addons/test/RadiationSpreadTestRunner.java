package com.hbm_addons.test;

import com.hbm.handler.radiation.IRadiationData;
import com.hbm.handler.radiation.RadiationCapability;
import com.hbm.handler.radiation.RadiationEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Проверка радиационного поля: интерполяция, высота, симметрия распространения
 * по четырём направлениям и ослабление материалом.
 *
 * <p>Тикает не сам, а через штатный планировщик: источник ставится через
 * {@link RadiationEvents#incrementRadiation}, дальше работает серверный тик.
 * Иначе тест не поймал бы баги в очереди активных чанков (именно так уехало
 * распространение на запад и север).
 */
public class RadiationSpreadTestRunner extends TestRunner {

    // Центры ячеек: X/Z ≡ 2 (mod 4), Y ≡ 8 (mod 16) от minY = -64.
    private static final int SRC_X = 10;
    private static final int SRC_Y = 88;
    private static final int SRC_Z = 10;
    private static final int TOP_Y = 312;
    private static final int BOTTOM_Y = -56;

    private static final int BOX_Y = 200;
    private static final int PROBE_X = 22;

    private static final float SOURCE_AMOUNT = 20_000F;
    private static final float HIGH_AMOUNT = 5_000F;
    private static final int PHASE_TICKS = 200;

    private IRadiationData originData;
    private IRadiationData eastData;
    private float openValue;
    private float stoneValue;
    private int placed;

    public RadiationSpreadTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (tickCount == 0) {
            originData = RadiationCapability.getChunkData(level, new ChunkPos(0, 0));
            eastData = RadiationCapability.getChunkData(level, new ChunkPos(1, 0));
            if (originData == null || eastData == null) {
                check("chunks_loaded", false);
                finish(false);
                return true;
            }

            originData.clear();
            eastData.clear();
            fillBox(Blocks.AIR.defaultBlockState());
            clearArea();

            RadiationEvents.incrementRadiation(level, SRC_X, SRC_Y, SRC_Z, SOURCE_AMOUNT);
            RadiationEvents.incrementRadiation(level, SRC_X, BOX_Y, SRC_Z, HIGH_AMOUNT);
            RadiationEvents.incrementRadiation(level, SRC_X, TOP_Y, SRC_Z, 777F);
            RadiationEvents.incrementRadiation(level, SRC_X, BOTTOM_Y, SRC_Z, 888F);

            boolean centerExact = originData.getRadiation(SRC_X, SRC_Y, SRC_Z) == SOURCE_AMOUNT;
            boolean midHalf = Math.abs(originData.getRadiation(12, SRC_Y, SRC_Z) - SOURCE_AMOUNT / 2F) < 1F;
            boolean nextEmpty = originData.getRadiation(14, SRC_Y, SRC_Z) == 0F;
            boolean topOk = originData.getRadiation(SRC_X, TOP_Y, SRC_Z) == 777F;
            boolean bottomOk = originData.getRadiation(SRC_X, BOTTOM_Y, SRC_Z) == 888F;

            check("interp_center_exact", centerExact);
            check("interp_mid_half", midHalf);
            check("interp_next_empty", nextEmpty);
            check("index_top", topOk);
            check("index_bottom", bottomOk);

            long start = System.nanoTime();
            for (int i = 0; i < 100_000; i++) {
                originData.getRadiation(SRC_X, SRC_Y, SRC_Z);
            }
            long perQuery = (System.nanoTime() - start) / 100_000;
            metric("query_ns", perQuery);
            check("query_under_1us", perQuery < 1000);

            if (!(centerExact && midHalf && nextEmpty && topOk && bottomOk)) {
                finish(false);
                return true;
            }

            tickCount++;
            return false;
        }

        if (tickCount < PHASE_TICKS) {
            tickCount++;
            return false;
        }

        if (tickCount == PHASE_TICKS) {
            // Симметрия: источник в центре ячейки (8..11), пробы — по центру
            // соседних ячеек в двух шагах, в своих чанках.
            float east = value(1, 0, 22, SRC_Y, SRC_Z);
            float west = value(-1, 0, -2, SRC_Y, SRC_Z);
            float south = value(0, 1, SRC_X, SRC_Y, 22);
            float north = value(0, -1, SRC_X, SRC_Y, -2);

            IRadiationData westChunk = RadiationCapability.getChunkData(level, new ChunkPos(-1, 0));
            IRadiationData northChunk = RadiationCapability.getChunkData(level, new ChunkPos(0, -1));
            metric("x_profile", profile(originData, 9, 2, true));
            metric("z_profile", profile(originData, 9, 2, false));
            metric("src_w_edge", round(originData.getRawCell(0, 9, 2)));
            metric("src_e_edge", round(originData.getRawCell(3, 9, 2)));
            metric("src_n_edge", round(originData.getRawCell(2, 9, 0)));
            metric("src_s_edge", round(originData.getRawCell(2, 9, 3)));
            metric("w_chunk_edge", westChunk == null ? "n/a" : round(westChunk.getRawCell(3, 9, 2)));
            metric("n_chunk_edge", northChunk == null ? "n/a" : round(northChunk.getRawCell(2, 9, 3)));
            metric("east", round(east));
            metric("west", round(west));
            metric("south", round(south));
            metric("north", round(north));

            check("spread_east", east > 1F);
            check("spread_south", south > 1F);
            check("spread_west", west > 1F);
            check("spread_north", north > 1F);

            float min = Math.min(Math.min(east, west), Math.min(south, north));
            float max = Math.max(Math.max(east, west), Math.max(south, north));
            boolean symmetric = min > 0 && max / min < 4F;
            metric("symmetry_ratio", round(min > 0 ? max / min : 0));
            check("spread_symmetric", symmetric);

            openValue = originData.getRadiation(PROBE_X, BOX_Y, SRC_Z);
            metric("open_probe", round(openValue));

            startContainedPhase(Blocks.STONE.defaultBlockState());
            tickCount++;
            return false;
        }

        if (tickCount < PHASE_TICKS * 2) {
            tickCount++;
            return false;
        }

        if (tickCount == PHASE_TICKS * 2) {
            stoneValue = originData.getRadiation(PROBE_X, BOX_Y, SRC_Z);
            metric("stone_probe", round(stoneValue));

            startContainedPhase(Blocks.OBSIDIAN.defaultBlockState());
            tickCount++;
            return false;
        }

        if (tickCount < PHASE_TICKS * 3) {
            tickCount++;
            return false;
        }

        float obsidianValue = originData.getRadiation(PROBE_X, BOX_Y, SRC_Z);

        float stoneAttenuation = openValue > 0 ? stoneValue / openValue : 1F;
        float obsidianAttenuation = stoneValue > 0 ? obsidianValue / stoneValue : 1F;
        metric("obsidian_probe", round(obsidianValue));
        metric("stone_attenuation", round(stoneAttenuation));
        metric("obsidian_vs_stone", round(obsidianAttenuation));
        metric("box_blocks", placed);

        check("stone_shields", openValue > 0 && stoneValue > 0 && stoneAttenuation < 0.8F);
        check("material_matters", stoneValue > 0 && obsidianValue < stoneValue * 0.5F);

        boolean pass = checks.stream().allMatch(c -> c.pass);
        finish(pass);
        return true;
    }

    private String profile(IRadiationData data, int gy, int gz, boolean alongX) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i > 0) sb.append('/');
            sb.append(round(alongX ? data.getRawCell(i, gy, gz) : data.getRawCell(gz, gy, i)));
        }
        return sb.toString();
    }

    /** Воздух вокруг источника: тест не должен зависеть от рельефа под землёй. */
    private void clearArea() {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -8; x < 22; x++) {
            for (int y = 80; y < 96; y++) {
                for (int z = -8; z < 22; z++) {
                    pos.set(x, y, z);
                    if (level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)) {
                        RadiationEvents.invalidate(level, pos);
                    }
                }
            }
        }
    }

    private float value(int chunkX, int chunkZ, int x, int y, int z) {
        IRadiationData data = RadiationCapability.getChunkData(level, new ChunkPos(chunkX, chunkZ));
        return data == null ? 0F : data.getRadiation(x, y, z);
    }

    /** Перезапуск фазы: чистим поле и ставим источник заново, через события. */
    private void startContainedPhase(BlockState shell) {
        originData.clear();
        eastData.clear();
        fillBox(shell);
        RadiationEvents.incrementRadiation(level, SRC_X, BOX_Y, SRC_Z, HIGH_AMOUNT);
    }

    /**
     * Оболочка вокруг ячейки источника: 3×3×3 ячейки, внутри полость.
     * Одиночная стена не годится — поле трёхмерное и обходит её сверху и снизу.
     */
    private void fillBox(BlockState state) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 4; x < 16; x++) {
            for (int y = 176; y < 224; y++) {
                for (int z = 4; z < 16; z++) {
                    boolean inner = x >= 8 && x < 12 && y >= 192 && y < 208 && z >= 8 && z < 12;
                    if (inner) continue;
                    pos.set(x, y, z);
                    if (level.setBlock(pos, state, 2)) {
                        placed++;
                        RadiationEvents.invalidate(level, pos);
                    }
                }
            }
        }
    }

    private static String round(float value) {
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }
}
