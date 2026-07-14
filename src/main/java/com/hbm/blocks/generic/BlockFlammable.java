package com.hbm.blocks.generic;

import com.hbm.blocks.BlockBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;



public class BlockFlammable extends BlockBase {

    public int encouragement; // скорость распространения огня
    public int flammability;  // воспламеняемость

    public BlockFlammable(Properties properties, int encouragement, int flammability) {
        super(properties);
        this.encouragement = encouragement;
        this.flammability = flammability;
    }

    @Override
    public int getFlammability(@NotNull BlockState state, @NotNull BlockGetter world,
                               @NotNull BlockPos pos, @NotNull Direction face) {
        return flammability;
    }

    @Override
    public int getFireSpreadSpeed(@NotNull BlockState state, @NotNull BlockGetter world,
                                  @NotNull BlockPos pos, @NotNull Direction face) {
        return encouragement;
    }

    @Override
    public boolean isFlammable(@NotNull BlockState state, @NotNull BlockGetter world,
                               @NotNull BlockPos pos, @NotNull Direction face) {
        return flammability > 0;
    }

    public boolean shouldIgnite(@NotNull Level world, @NotNull BlockPos pos) {
        if(flammability == 0) return false;

        // Проверяем все направления на наличие огня
        for(Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            // Проверяем, является ли соседний блок огнем
            if(isFireBlock(neighborState)) {
                return true;
            }
        }

        return false;
    }

    // Вспомогательный метод для проверки огня
    private boolean isFireBlock(BlockState state) {
        Block block = state.getBlock();

        // Проверяем основные блоки огня
        if(block == net.minecraft.world.level.block.Blocks.FIRE) {
            return true;
        }

        // Проверяем душный огонь (Soul Fire)
        if(block == net.minecraft.world.level.block.Blocks.SOUL_FIRE) {
            return true;
        }

        // Дополнительно: проверяем теги для совместимости с модами
        // Например, если другие моды добавляют свои виды огня
        return false;
    }

    // Статический метод для создания свойств с воспламеняемостью
    public static Properties createFlammableProperties() {
        return BlockBehaviour.Properties.of()
                .strength(2.0F, 3.0F)
                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                .ignitedByLava(); // Блок может воспламениться от лавы

    }

    // Альтернативный конструктор для обратной совместимости
    @Deprecated
    public BlockFlammable(Properties properties) {
        this(properties, 5, 20); // Значения по умолчанию
    }
}