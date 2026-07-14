package com.hbm.handler.neutron;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class NeutronHandler {

    private static int ticks = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        int cacheTime = 20;
        boolean clear = ticks >= cacheTime;
        if (clear) ticks = 0;
        ticks++;

        NeutronNodeWorld.removeEmptyWorlds();

        for (Map.Entry<Level, NeutronNodeWorld.StreamWorld> entry : NeutronNodeWorld.streamWorlds.entrySet()) {
            Level level = entry.getKey();
            NeutronNodeWorld.StreamWorld sw = entry.getValue();
            sw.runStreamInteractions(level);
            sw.removeAllStreams();
            if (clear) sw.cleanNodes();
        }
    }
}