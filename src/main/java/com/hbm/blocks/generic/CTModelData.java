package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Данные модели для connected textures: для каждой из 6 граней — четыре индекса фрагментов
 * (0…19) плюс битовая маска граней, закрытых соседом.
 *
 * <p>Правило выбора фрагмента — из {@code CTContext} эталона 1.7.10: грань делится на четыре
 * квадранта, для каждого смотрятся сосед по ребру (горизонталь/вертикаль) и сосед по диагонали,
 * а итоговый тип даёт {@code cornerType(hor, corner, vert)}:
 * {@code c} — внутренний угол, {@code j} — Т-стык, {@code h}/{@code v} — прямой стык,
 * {@code f} — полный квадрант базовой текстуры.
 *
 * <p>Считается на клиенте по миру: свойств состояния у CT-блоков нет, поэтому генерация и
 * правка соседей не требуют пересчёта состояний.</p>
 */
public final class CTModelData {

    public static final ModelProperty<CTModelData> PROPERTY = new ModelProperty<>();

    /** 20 фрагментов: 0…3 — квадранты базовой текстуры, 4…19 — сетка 4×4 из атласа CT. */
    public static final int FRAGMENT_COUNT = 20;

    /**
     * Базис плоскости грани: {вверх, влево} в лексических координатах.
     * Порядок — порядок {@link Direction#ordinal()}: DOWN, UP, NORTH, SOUTH, WEST, EAST,
     * он же порядок ForgeDirection в эталоне.
     */
    private static final Direction[][] BASIS = {
            {Direction.SOUTH, Direction.WEST},  // DOWN
            {Direction.NORTH, Direction.WEST},  // UP
            {Direction.UP, Direction.EAST},     // NORTH
            {Direction.UP, Direction.WEST},     // SOUTH
            {Direction.UP, Direction.NORTH},    // WEST
            {Direction.UP, Direction.SOUTH}     // EAST
    };

    public static final CTModelData EMPTY = new CTModelData(fullyDisconnected(), 0);

    private final byte[] quadrants;
    private final int hiddenMask;

    private CTModelData(byte[] quadrants, int hiddenMask) {
        this.quadrants = quadrants;
        this.hiddenMask = hiddenMask;
    }

    /**
     * Индекс фрагмента для квадранта {@code quadrant} (0 — верхний левый, 1 — верхний правый,
     * 2 — нижний левый, 3 — нижний правый) грани {@code face}.
     */
    public int fragment(@NotNull Direction face, int quadrant) {
        return quadrants[face.ordinal() * 4 + quadrant] & 0xFF;
    }

    /** Грань закрыта соседом того же «семейства» — рисовать её не нужно. */
    public boolean isHidden(@NotNull Direction face) {
        return (hiddenMask & (1 << face.ordinal())) != 0;
    }

    @NotNull
    public static CTModelData compute(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CTBlock ct)) {
            return EMPTY;
        }

        byte[] quadrants = new byte[24];
        int hidden = 0;

        for (Direction face : Direction.values()) {
            int base = face.ordinal() * 4;

            if (connectsAt(level, pos, face, null, state, ct)) {
                hidden |= 1 << face.ordinal();
            }

            Direction up = BASIS[face.ordinal()][0];
            Direction left = BASIS[face.ordinal()][1];
            Direction down = up.getOpposite();
            Direction right = left.getOpposite();

            boolean[] cons = new boolean[8];
            cons[0] = connectsAt(level, pos, up, left, state, ct);
            cons[1] = connectsAt(level, pos, up, null, state, ct);
            cons[2] = connectsAt(level, pos, up, right, state, ct);
            cons[3] = connectsAt(level, pos, left, null, state, ct);
            cons[4] = connectsAt(level, pos, right, null, state, ct);
            cons[5] = connectsAt(level, pos, down, left, state, ct);
            cons[6] = connectsAt(level, pos, down, null, state, ct);
            cons[7] = connectsAt(level, pos, down, right, state, ct);

            quadrants[base] = (byte) (0 | cornerType(cons[3], cons[0], cons[1]));
            quadrants[base + 1] = (byte) (1 | cornerType(cons[4], cons[2], cons[1]));
            quadrants[base + 2] = (byte) (2 | cornerType(cons[3], cons[5], cons[6]));
            quadrants[base + 3] = (byte) (3 | cornerType(cons[4], cons[7], cons[6]));
        }

        return new CTModelData(quadrants, hidden);
    }

    /** Правило {@code CTContext.cornerType} эталона. */
    private static int cornerType(boolean hor, boolean corner, boolean vert) {
        if (vert && hor && corner) return 4;   // c — внутренний угол
        if (vert && hor) return 8;             // j — Т-стык
        if (vert) return 16;                   // v — вертикальный стык
        if (hor) return 12;                    // h — горизонтальный стык
        return 0;                              // f — полный квадрант
    }

    private static boolean connectsAt(BlockAndTintGetter level, BlockPos pos, Direction first,
                                      @Nullable Direction second, BlockState state, CTBlock ct) {
        BlockPos neighbour = second == null
                ? pos.relative(first)
                : pos.relative(first).relative(second);
        return ct.connectsTo(state, level.getBlockState(neighbour));
    }

    /** Данные «одиночного блока»: все грани разъединены, все квадранты полные. */
    @NotNull
    public static CTModelData single() {
        return EMPTY;
    }

    private static byte[] fullyDisconnected() {
        byte[] quadrants = new byte[24];
        for (int i = 0; i < quadrants.length; i++) {
            quadrants[i] = (byte) (i & 3);
        }
        return quadrants;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof CTModelData data)) return false;
        return hiddenMask == data.hiddenMask && Arrays.equals(quadrants, data.quadrants);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(quadrants) + hiddenMask;
    }

    @Override
    public String toString() {
        return "CTModelData{quadrants=" + Arrays.toString(quadrants) + ", hidden=" + hiddenMask + '}';
    }

    /** Значения по умолчанию для предмета: все квадранты — полные. */
    public static ModelData emptyModelData() {
        return ModelData.builder().with(PROPERTY, EMPTY).build();
    }
}
