package com.hbm.handler.radiation;

import com.hbm.blocks.IRadiationSource;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

/**
 * Первый тик для источников, поставленных генерацией (#198).
 *
 * <p>Блоки-источники планируют свой тик в {@code onPlace}, но генерация мира
 * его не вызывает: {@code WorldGenRegion.setBlock} side-эффекты не запускает.
 * Поэтому при загрузке чанка ищем источники по палитре секций
 * ({@code maybeHas} — дешёвая проверка без обхода 4096 блоков) и планируем им
 * первый тик. Дальше блок тикает и перепланирует себя сам, как в эталоне.</p>
 *
 * <p>Проверка идёт по палитре, поэтому чанк без источников стоит несколько
 * сравнений на секцию. Секция с источником обходится целиком — это редкий случай.</p>
 */
@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RadiationSourceTicker {

    /** Тот же период, что у бочки и блоков отходов в эталоне. */
    private static final int FIRST_TICK_DELAY = 20;

    private static final Predicate<BlockState> SOURCE =
            state -> state.getBlock() instanceof IRadiationSource source && source.emitsRadiation();

    private RadiationSourceTicker() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.isClientSide) return;

        ChunkAccess chunk = event.getChunk();
        LevelChunkSection[] sections = chunk.getSections();
        int minSection = level.getMinSection();
        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();

        for (int index = 0; index < sections.length; index++) {
            LevelChunkSection section = sections[index];
            if (section == null || section.hasOnlyAir()) continue;
            if (!section.maybeHas(SOURCE)) continue;

            int baseY = (minSection + index) << 4;
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (!SOURCE.test(state)) continue;
                        level.scheduleTick(new BlockPos(baseX + x, baseY + y, baseZ + z),
                                state.getBlock(), FIRST_TICK_DELAY);
                    }
                }
            }
        }
    }
}
