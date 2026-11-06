package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.CTModelData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Стенд семейств connected textures (#211): platemetal и катушки адронного коллайдера.
 *
 * <p>Правило соединения в эталоне одинаковое для всех семейств — {@code canConnect} по умолчанию
 * сравнивает блок с собой. Значит:</p>
 * <ul>
 *   <li>два одинаковых блока рядом соединяются: грань скрыта, на стыке — фрагмент стыка;</li>
 *   <li>два разных цвета platemetal рядом <b>не</b> соединяются: обе грани полные;</li>
 *   <li>две катушки одного сорта — соединяются, разные сорта — нет.</li>
 * </ul>
 */
public class CTFamiliesTestRunner extends TestRunner {

    private static final int BASE_X = 1020;
    private static final int BASE_Y = 150;
    private static final int BASE_Z = 1020;

    private int stage = 0;

    public CTFamiliesTestRunner(ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            progress("place");
            clearArea();
            placePatterns();
            stage = 1;
            return false;
        }

        progress("verify");
        verify();
        finish(checks.stream().allMatch(c -> c.pass));
        return true;
    }

    private void clearArea() {
        for (int x = -2; x <= 21; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y <= 1; y++) {
                    level.setBlock(pos(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private void placePatterns() {
        BlockState black = ModBlocks.PLATEMETAL_BLACK.get().defaultBlockState();
        BlockState white = ModBlocks.PLATEMETAL_WHITE.get().defaultBlockState();
        BlockState coilAlloy = ModBlocks.HADRON_COIL_ALLOY.get().defaultBlockState();
        BlockState coilGold = ModBlocks.HADRON_COIL_GOLD.get().defaultBlockState();
        BlockState glassBoron = ModBlocks.GLASS_BORON.get().defaultBlockState();
        BlockState glassLead = ModBlocks.GLASS_LEAD.get().defaultBlockState();
        BlockState wire = ModBlocks.RED_WIRE_COATED.get().defaultBlockState();

        // пара одного цвета
        set(0, 0, black);
        set(1, 0, black);

        // два разных цвета рядом — соединяться не должны
        set(3, 0, black);
        set(4, 0, white);

        // катушки: один сорт и разные сорта
        set(6, 0, coilAlloy);
        set(7, 0, coilAlloy);
        set(9, 0, coilAlloy);
        set(10, 0, coilGold);

        // стекло: пара одного сорта и два разных сорта рядом
        set(12, 0, glassBoron);
        set(13, 0, glassBoron);
        set(15, 0, glassBoron);
        set(16, 0, glassLead);

        // провод в оболочке: пара
        set(18, 0, wire);
        set(19, 0, wire);
    }

    private void verify() {
        // 1. Один цвет рядом с собой: грань скрыта, на верхней грани стык.
        CTModelData pair = model(0, 0);
        check("plate_same_hidden_east", pair.isHidden(Direction.EAST));
        check("plate_same_up", expect(pair, Direction.UP, 0, 13, 2, 15));
        check("plate_same_mirror_hidden_west", model(1, 0).isHidden(Direction.WEST));
        check("plate_same_mirror_up", expect(model(1, 0), Direction.UP, 12, 1, 14, 3));

        // 2. Разные цвета: не соединяются, грани полные, ничего не скрыто.
        CTModelData blackNextWhite = model(3, 0);
        CTModelData whiteNextBlack = model(4, 0);
        check("plate_other_color_not_hidden", !blackNextWhite.isHidden(Direction.EAST));
        check("plate_other_color_full_face", expect(blackNextWhite, Direction.UP, 0, 1, 2, 3));
        check("plate_other_color_mirror_not_hidden", !whiteNextBlack.isHidden(Direction.WEST));
        check("plate_other_color_mirror_full_face", expect(whiteNextBlack, Direction.UP, 0, 1, 2, 3));

        // 3. Катушки одного сорта соединяются, разных — нет.
        check("coil_same_hidden_east", model(6, 0).isHidden(Direction.EAST));
        check("coil_same_up", expect(model(6, 0), Direction.UP, 0, 13, 2, 15));
        check("coil_other_type_not_hidden", !model(9, 0).isHidden(Direction.EAST));
        check("coil_other_type_full_face", expect(model(9, 0), Direction.UP, 0, 1, 2, 3));

        // 4. Стекло: пара одного сорта соединяется, разные сорта — нет.
        check("glass_same_hidden_east", model(12, 0).isHidden(Direction.EAST));
        check("glass_same_up", expect(model(12, 0), Direction.UP, 0, 13, 2, 15));
        check("glass_same_mirror_hidden_west", model(13, 0).isHidden(Direction.WEST));
        check("glass_other_type_not_hidden", !model(15, 0).isHidden(Direction.EAST));
        check("glass_other_type_full_face", expect(model(15, 0), Direction.UP, 0, 1, 2, 3));

        // 5. Провод в оболочке: соединяется сам с собой, данные модели отдаёт сетевой TE.
        check("wire_same_hidden_east", model(18, 0).isHidden(Direction.EAST));
        check("wire_same_up", expect(model(18, 0), Direction.UP, 0, 13, 2, 15));
        BlockEntity cable = level.getBlockEntity(pos(18, 0, 0));
        boolean cableModelData = cable != null
                && cable.getModelData().get(CTModelData.PROPERTY) != null;
        check("wire_te_returns_model_data", cableModelData);
        info("wire_te", cable == null ? "нет BE" : cable.getType().toString());

        metric("checks", checks.size());
        metric("passed", checks.stream().filter(c -> c.pass).count());
    }

    private boolean expect(CTModelData data, Direction face, int q0, int q1, int q2, int q3) {
        int a0 = data.fragment(face, 0);
        int a1 = data.fragment(face, 1);
        int a2 = data.fragment(face, 2);
        int a3 = data.fragment(face, 3);
        boolean ok = a0 == q0 && a1 == q1 && a2 == q2 && a3 == q3;
        if (!ok) {
            info("mismatch_" + face.getName(), "[" + a0 + "," + a1 + "," + a2 + "," + a3
                    + "] ожидалось [" + q0 + "," + q1 + "," + q2 + "," + q3 + "]");
        }
        return ok;
    }

    private CTModelData model(int x, int z) {
        return CTModelData.compute(level, pos(x, 0, z));
    }

    private static BlockPos pos(int x, int y, int z) {
        return new BlockPos(BASE_X + x, BASE_Y + y, BASE_Z + z);
    }

    private void set(int x, int z, BlockState state) {
        level.setBlock(pos(x, 0, z), state, 3);
    }
}
