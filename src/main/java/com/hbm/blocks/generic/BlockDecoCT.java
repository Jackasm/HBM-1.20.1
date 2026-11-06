package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Декоративный металлический блок с connected textures.
 *
 * <p>Поведение повторяет 1.7.10 ({@code BlockDecoCT implements IBlockCT}): соединение считается
 * по соседям в мире, а не хранится в состоянии блока. Соединяются либо одинаковые блоки, либо
 * пара {@code deco_steel} ↔ {@code deco_rusty_steel} — как в {@code canConnect} эталона.</p>
 */
public class BlockDecoCT extends BlockCTBase {

    public BlockDecoCT() {
        super(Properties.of()
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops());
    }

    /** Особое правило эталона: сталь и ржавая сталь образуют одну поверхность. */
    @Override
    public boolean connectsTo(BlockState self, BlockState other) {
        Block selfBlock = self.getBlock();
        Block otherBlock = other.getBlock();
        if (selfBlock == otherBlock) {
            return true;
        }
        Block steel = ModBlocks.DECO_STEEL.get();
        Block rusty = ModBlocks.DECO_RUSTY_STEEL.get();
        return (selfBlock == steel && otherBlock == rusty) || (selfBlock == rusty && otherBlock == steel);
    }
}
