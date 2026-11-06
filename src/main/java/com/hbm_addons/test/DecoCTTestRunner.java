package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.CTModelData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Стенд правила соединения CT-блоков (#208): строит паттерны в мире и сверяет
 * {@link CTModelData} с таблицей, собранной вручную по правилу {@code cornerType}
 * из {@code CTContext} эталона 1.7.10.
 *
 * <p>Что проверяется:</p>
 * <ul>
 *   <li>одиночный блок — все грани полные (0,1,2,3), ничего не скрыто;</li>
 *   <li>линия из трёх — у середины закрыты EAST/WEST, верх — горизонтальный стык;</li>
 *   <li>квадрат 2×2 — внутренний угол {@code c} в квадранте, где есть диагональ;</li>
 *   <li>угол «Г» — на том же месте Т-стык {@code j}: диагонали нет, значит и угла нет;</li>
 *   <li>пара {@code deco_steel} ↔ {@code deco_rusty_steel} — соединяется взаимно;</li>
 *   <li>свойств состояния у блока нет вовсе.</li>
 * </ul>
 */
public class DecoCTTestRunner extends TestRunner {

    private static final int BASE_X = 1000;
    private static final int BASE_Y = 150;
    private static final int BASE_Z = 1000;

    private int stage = 0;

    public DecoCTTestRunner(ServerLevel level, String scenarioName) {
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
        for (int x = -2; x <= 20; x++) {
            for (int z = -2; z <= 3; z++) {
                for (int y = 0; y <= 3; y++) {
                    level.setBlock(pos(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private void placePatterns() {
        BlockState steel = ModBlocks.DECO_STEEL.get().defaultBlockState();
        BlockState rusty = ModBlocks.DECO_RUSTY_STEEL.get().defaultBlockState();

        // одиночный блок
        set(0, 0, steel);

        // линия из трёх по X
        set(2, 0, steel);
        set(3, 0, steel);
        set(4, 0, steel);

        // квадрат 2x2 — на всех четырёх блоках есть внутренний угол
        set(6, 0, steel);
        set(7, 0, steel);
        set(6, 1, steel);
        set(7, 1, steel);

        // угол «Г» — та же форма, но без диагонали
        set(9, 0, steel);
        set(10, 0, steel);
        set(9, 1, steel);

        // пара сталь | ржавая сталь
        set(12, 0, steel);
        set(13, 0, rusty);

        // вертикальное ребро
        set(15, 0, steel);
        level.setBlock(pos(15, 0, 0).above(), steel, 3);
    }

    private void verify() {
        // 1. Свойств состояния нет: один блок — одно состояние.
        BlockState steel = ModBlocks.DECO_STEEL.get().defaultBlockState();
        check("no_state_properties", steel.getProperties().isEmpty());
        info("state_properties", steel.getProperties().size());

        // 2. Одиночный блок: все шесть граней полные, скрытых нет.
        CTModelData single = model(0, 0);
        boolean singleOk = true;
        for (Direction face : Direction.values()) {
            singleOk &= expect(single, face, 0, 1, 2, 3);
            singleOk &= !single.isHidden(face);
        }
        check("single_all_faces_full", singleOk);

        // 3. Линия из трёх: у середины закрыты EAST и WEST, верх — горизонтальный стык.
        CTModelData middle = model(3, 0);
        check("line_middle_east_hidden", middle.isHidden(Direction.EAST));
        check("line_middle_west_hidden", middle.isHidden(Direction.WEST));
        check("line_middle_up_not_hidden", !middle.isHidden(Direction.UP));
        check("line_middle_up_horizontal", expect(middle, Direction.UP, 12, 13, 14, 15));
        check("line_middle_down_horizontal", expect(middle, Direction.DOWN, 12, 13, 14, 15));
        check("line_middle_north_horizontal", expect(middle, Direction.NORTH, 12, 13, 14, 15));

        // 4. Конец линии: закрыта только восточная грань, на верхней — половинка стыка.
        CTModelData end = model(2, 0);
        check("line_end_east_hidden", end.isHidden(Direction.EAST));
        check("line_end_west_open", !end.isHidden(Direction.WEST));
        check("line_end_up", expect(end, Direction.UP, 0, 13, 2, 15));

        // 5. Квадрат 2x2: внутренние углы в тех квадрантах, где есть диагональ.
        check("square_corner_nw_up", expect(model(6, 0), Direction.UP, 0, 13, 18, 7));
        check("square_corner_se_up", expect(model(7, 1), Direction.UP, 4, 17, 14, 3));
        check("square_corner_nw_hidden", model(6, 0).isHidden(Direction.EAST)
                && model(6, 0).isHidden(Direction.SOUTH));

        // 6. Угол «Г»: та же геометрия, но диагонали нет — Т-стык вместо угла.
        check("corner_l_junction", expect(model(9, 0), Direction.UP, 0, 13, 18, 11));

        // 7. Пара steel | rusty: соединение взаимное, обе грани скрыты.
        CTModelData steelPair = model(12, 0);
        CTModelData rustyPair = model(13, 0);
        check("pair_steel_hidden_east", steelPair.isHidden(Direction.EAST));
        check("pair_rusty_hidden_west", rustyPair.isHidden(Direction.WEST));
        check("pair_steel_up", expect(steelPair, Direction.UP, 0, 13, 2, 15));
        check("pair_rusty_up", expect(rustyPair, Direction.UP, 12, 1, 14, 3));

        // 8. Вертикальное ребро: скрыты UP у нижнего и DOWN у верхнего.
        check("vertical_lower_hidden_up", model(15, 0).isHidden(Direction.UP));
        check("vertical_upper_hidden_down",
                CTModelData.compute(level, pos(15, 0, 0).above()).isHidden(Direction.DOWN));

        metric("checks", checks.size());
        metric("passed", checks.stream().filter(c -> c.pass).count());
    }

    /** Сверяет четыре индекса фрагментов грани; при расхождении пишет фактическое значение. */
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
