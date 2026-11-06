package com.hbm_addons.handler;

import com.hbm_addons.config.AddonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class EncounterReputation {

    public static final String KEY_KILLS = "encounterKills";
    public static final String KEY_LAST_KILL = "encounterLastKill";

    private EncounterReputation() {}

    public static double getHostilityChance(Player player) {
        CompoundTag data = player.getPersistentData();
        double base = AddonConfig.encounterFriendlyBaseChance;
        int kills = data.getInt(KEY_KILLS);
        long last = data.getLong(KEY_LAST_KILL);
        long now = player.level().getGameTime();

        if (last > 0 && now - last > AddonConfig.encounterHostileDecayTicks) {
            long steps = (now - last) / AddonConfig.encounterHostileDecayTicks;
            kills = Math.max(0, kills - (int) steps);
            data.putInt(KEY_KILLS, kills);
            data.putLong(KEY_LAST_KILL, now);
        }

        double hostile = (1.0D - base) + kills * AddonConfig.encounterHostilePerKill;
        return Math.min(AddonConfig.encounterHostileMax, Math.max(0.0D, hostile));
    }

    public static void onFriendlyKilled(Player player) {
        CompoundTag data = player.getPersistentData();
        int kills = data.getInt(KEY_KILLS) + 1;
        data.putInt(KEY_KILLS, kills);
        data.putLong(KEY_LAST_KILL, player.level().getGameTime());
    }
}
