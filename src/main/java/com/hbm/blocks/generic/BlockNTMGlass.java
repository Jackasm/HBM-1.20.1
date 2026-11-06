package com.hbm.blocks.generic;

import com.hbm.entity.CTBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Стекло порта. В 1.7.10 это семейство было CT-блоками ({@code BlockNTMGlassCT implements IBlockCT}):
 * соседние стёкла одного сорта образуют общую поверхность. Здесь то же поведение даёт
 * {@link CTBlock} — блок соединяется сам с собой, а модель берёт фрагменты из атласа 32×40
 * в {@code RenderType.translucent}.
 */
public class BlockNTMGlass extends GlassBlock implements EntityBlock, CTBlock {

    private final boolean dropSelf;
    private final int lightLevel;

    public BlockNTMGlass(Properties properties, boolean dropSelf, int lightLevel) {
        super(properties);
        this.dropSelf = dropSelf;
        this.lightLevel = lightLevel;
    }

    public BlockNTMGlass(Properties properties, boolean dropSelf) {
        this(properties, dropSelf, 0);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return lightLevel;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CTBlockEntity(pos, state);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                        @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        CTBlocks.notifyArea(level, pos);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (!state.is(newState.getBlock())) {
            CTBlocks.notifyArea(level, pos);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        CTBlocks.notifyArea(level, pos);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                     @NotNull RandomSource random) {
        CTBlocks.sendRefresh(level, pos);
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public boolean skipRendering(@NotNull BlockState state, BlockState adjacentState, @NotNull Direction side) {
        return adjacentState.getBlock() == this || super.skipRendering(state, adjacentState, side);
    }

    // Статические методы для создания свойств
    public static Properties createProperties(MapColor color, float hardness, float resistance, SoundType sound) {
        return Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .sound(sound)
                .noOcclusion()
                .isValidSpawn((state, level, pos, entity) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .pushReaction(PushReaction.NORMAL);
    }

    public static Properties createProperties(MapColor color, float hardness, SoundType sound) {
        return createProperties(color, hardness, 1.0F, sound);
    }

    public static Properties createProperties(MapColor color) {
        return createProperties(color, 0.3F, SoundType.GLASS);
    }

    public static Properties createProperties(MapColor color, float hardness) {
        return createProperties(color, hardness, SoundType.GLASS);
    }
}