package com.hbm.saveddata.satellites;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.entity.projectile.EntityTom;
import com.hbm.saveddata.SatelliteSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;

import java.util.Objects;

public class SatelliteHorizons extends Satellite {

    private boolean used = false;

    public SatelliteHorizons() {
        this.satIface = Interfaces.SAT_COORD;
    }

    @Override
    public void onOrbit(Level level, double x, double y, double z) {
        for (Player player : level.players()) {
            if (player instanceof ServerPlayer sp) {
                ModCriteriaTriggers.HORIZONS_START.trigger(sp);
            }
        }
    }

    @Override
    public void writeToNBT(net.minecraft.nbt.CompoundTag nbt) {
        nbt.putBoolean("used", used);
    }

    @Override
    public void readFromNBT(net.minecraft.nbt.CompoundTag nbt) {
        used = nbt.getBoolean("used");
    }

    @Override
    public void onCoordAction(Level level, Player player, int x, int y, int z) {
        if (used) return;

        used = true;
        Objects.requireNonNull(SatelliteSavedData.getData(level)).markDirty();

        EntityTom tom = new EntityTom(level);
        tom.setPos(x + 0.5, 600, z + 0.5);

        if (level instanceof ServerLevel serverLevel) {
            ChunkSource provider = serverLevel.getChunkSource();
            provider.getChunk(x >> 4, z >> 4, true);
        }

        level.addFreshEntity(tom);

        for (Player p : level.players()) {
            if (p instanceof ServerPlayer sp) {
                ModCriteriaTriggers.HORIZONS_END.trigger(sp);
            }
        }

        if (!level.isClientSide) {
            if (level.getServer() != null) {
                level.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal("Horizons has been activated.").withStyle(ChatFormatting.RED),
                        false
                );
            }
        }
    }
}