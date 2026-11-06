package com.hbm.blocks.generic;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Блок с connected textures: правило, с каким соседом он образует общую поверхность.
 *
 * <p>Аналог {@code IBlockCT#canConnect} из 1.7.10. По умолчанию соединяется только
 * с таким же блоком; семейства с особыми правилами (например, пара
 * {@code deco_steel} ↔ {@code deco_rusty_steel}) переопределяют {@link #connectsTo}.</p>
 */
public interface CTBlock {

    default boolean connectsTo(BlockState self, BlockState other) {
        return self.getBlock() == other.getBlock();
    }

    /** Блок участвует в CT: нужен для проверок в данных модели и в хелперах обновления. */
    static boolean isCT(BlockState state) {
        return state.getBlock() instanceof CTBlock;
    }
}
