package com.hbm_addons.handler;

import com.hbm_addons.entity.ai.EncounterGoal;
import com.hbm_addons.entity.mob.EntityEncounterVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EncounterGroup {

    public final List<EntityEncounterVillager> members = new ArrayList<>();

    @Nullable
    public final Player primaryPlayer;

    public final EncounterGoal goal;
    public final boolean hostile;
    public final Vec3 spawnPos;
    public final Vec3 targetPoint;
    public long lastSeenTick;
    public int teleportCount = 0;

    /** Для тестов: не деспавнить по таймауту. */
    public boolean testMode = false;

    /** Для тестов: если не null — используется вместо level.players() в getEffectiveTarget. */
    @Nullable
    public List<Player> testExtraPlayers = null;

    /** Сколько тиков живёт группа. */
    public int age = 0;

    /** TRADER: сколько тиков стоит в ожидании. */
    public long traderWaitingTicks = 0;

    /** SCOUT: фазы 0=идёт, 1=стоит, 2=отходит. */
    public int scoutPhase = 0;
    public int scoutWaitTicks = 0;

    /** RAIDER: последняя известная позиция игрока, если LOS потерян. */
    @Nullable
    public Vec3 lastKnownPlayerPos = null;

    /* ============================================================
     *  STUCK DETECTION
     * ============================================================ */
    @Nullable
    public Vec3 lastStuckCheckPos = null;
    public long lastStuckCheckTick = 0;
    public int stuckChecks = 0;

    public EncounterGroup(Level level, @Nullable Player primaryPlayer, EncounterGoal goal,
                          boolean hostile, Vec3 spawnPos, Vec3 targetPoint) {
        this.primaryPlayer = primaryPlayer;
        this.goal = goal;
        this.hostile = hostile;
        this.spawnPos = spawnPos;
        this.targetPoint = targetPoint;
        this.lastSeenTick = level.getGameTime();
        this.lastStuckCheckTick = level.getGameTime();
    }

    public boolean isEmpty() {
        members.removeIf(e -> !e.isAlive());
        return members.isEmpty();
    }

    /** Центр группы — среднее по живым мобам. */
    @Nullable
    public Vec3 getCenter() {
        double x = 0, y = 0, z = 0;
        int n = 0;
        for (EntityEncounterVillager v : members) {
            if (!v.isAlive()) continue;
            x += v.getX(); y += v.getY(); z += v.getZ();
            n++;
        }
        if (n == 0) return null;
        return new Vec3(x / n, y / n, z / n);
    }

    /**
     * Эффективная цель группы:
     * - primary, если жив и не в креативе/спектейторе
     * - иначе ближайший валидный игрок в радиусе 64
     * - если testExtraPlayers != null, используется вместо level.players()
     * - иначе null
     */
    @Nullable
    public Player getEffectiveTarget(Level level) {
        if (primaryPlayer != null && primaryPlayer.isAlive()
                && !primaryPlayer.isCreative()
                && !primaryPlayer.isSpectator()) {
            return primaryPlayer;
        }

        Iterable<? extends Player> pool = (testExtraPlayers != null)
                ? testExtraPlayers
                : level.players();

        Player nearest = null;
        double bestDist = 64.0 * 64.0;
        for (Player p : pool) {
            if (!p.isAlive() || p.isCreative() || p.isSpectator()) continue;
            double d = p.distanceToSqr(spawnPos);
            if (d < bestDist) {
                bestDist = d;
                nearest = p;
            }
        }
        return nearest;
    }
}
