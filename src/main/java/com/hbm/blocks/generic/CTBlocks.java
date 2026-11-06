package com.hbm.blocks.generic;

import com.hbm.network.PacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Обновление картинки CT-блоков. Соединение зависит от диагоналей, а блок-апдейт
 * о них не сообщает, поэтому после постановки или слома сервер шлёт один пакет на
 * блок и его прямых соседей, а клиент пересобирает область 3×3×3.
 */
public final class CTBlocks {

    private CTBlocks() {
    }

    /** Ставит тик на сам блок и шесть соседей-«собратьев» (только сервер и загруженные чанки). */
    public static void notifyArea(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel)) return;
        scheduleIfCT(level, pos);
        for (Direction direction : Direction.values()) {
            scheduleIfCT(level, pos.relative(direction));
        }
    }

    private static void scheduleIfCT(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) return;
        BlockState state = level.getBlockState(pos);
        if (CTBlock.isCT(state)) {
            level.scheduleTick(pos, state.getBlock(), 1);
        }
    }

    /** Тик блока: один пакет, дальше клиент сам пересобирает область. */
    public static void sendRefresh(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) return;
        PacketDispatcher.sendModelDataUpdate(level, pos);
    }
}
