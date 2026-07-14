package com.hbm.saveddata.satellites;

import com.hbm.advancements.ModCriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SatelliteRelay extends Satellite {

    public SatelliteRelay() {
        this.satIface = Interfaces.NONE;
    }

    @Override
    public void onOrbit(Level level, double x, double y, double z) {
        for (Player player : level.players()) {
            if (player instanceof ServerPlayer sp) {
                ModCriteriaTriggers.FOEQ.trigger(sp);
            }
        }
    }
}