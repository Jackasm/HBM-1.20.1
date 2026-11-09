package com.hbm.datagen.worldgen.structure.pieces;

import com.hbm.util.RefStrings;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Догружает отложенные лут-блоки, когда чанк уже промотирован.
 *
 * <p>Во время генерации {@code getBlockEntity} для только что поставленного
 * блока возвращает {@code null}; TE появится позже. Очередь ведёт
 * {@link StructurePostProcess}.</p>
 */
@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StructurePostProcessEvents {

    private StructurePostProcessEvents() {
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            StructurePostProcess.drain();
        }
    }
}
