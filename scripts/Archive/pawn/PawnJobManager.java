package com.hbm.pawn;

import com.hbm.entity.mob.PawnEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PawnJobManager extends SavedData {
    private static final String ID = "hbm_pawn_jobs";

    private final Map<String, JobEntry> activeJobs = new ConcurrentHashMap<>();
    private final Map<String, IPawnServicable> servicables = new ConcurrentHashMap<>();

    public record JobEntry(String serviceId, UUID pawnId, long expireTime) {}

    public static PawnJobManager get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(
                    PawnJobManager::new,
                    PawnJobManager::new,
                    ID
            );
        }
        return new PawnJobManager();
    }

    public PawnJobManager() {}

    public PawnJobManager(CompoundTag tag) {
        ListTag list = tag.getList("jobs", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            String serviceId = entry.getString("serviceId");
            UUID pawnId = entry.getUUID("pawnId");
            long expireTime = entry.getLong("expireTime");
            activeJobs.put(serviceId, new JobEntry(serviceId, pawnId, expireTime));
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag list = new ListTag();
        for (JobEntry job : activeJobs.values()) {
            CompoundTag entry = new CompoundTag();
            entry.putString("serviceId", job.serviceId());
            entry.putUUID("pawnId", job.pawnId());
            entry.putLong("expireTime", job.expireTime());
            list.add(entry);
        }
        tag.put("jobs", list);
        return tag;
    }

    public void registerServicable(IPawnServicable servicable) {
        servicables.put(servicable.getServiceId(), servicable);
        setDirty();
    }

    public void unregisterServicable(String serviceId) {
        servicables.remove(serviceId);
        activeJobs.remove(serviceId);
        setDirty();
    }

    /**
     * Возвращает список доступных заданий, отсортированный по приоритету и расстоянию.
     */
    public List<IPawnServicable> getAvailableJobs(PawnEntity pawn) {
        List<IPawnServicable> available = new ArrayList<>();
        long now = System.currentTimeMillis();
        BlockPos pawnPos = pawn.blockPosition();
        UUID ownerUUID = pawn.getOwnerUUID(); // получаем владельца пешки

        for (IPawnServicable serv : servicables.values()) {
            String id = serv.getServiceId();
            JobEntry job = activeJobs.get(id);
            if (job != null && job.expireTime() > now) continue;

            // Проверка владельца: блок должен быть публичным или принадлежать владельцу пешки
            UUID blockOwner = serv.getOwnerUUID();
            if (blockOwner != null && !blockOwner.equals(ownerUUID)) continue;

            if (serv.needsService()) {
                available.add(serv);
            }
        }

        available.sort(Comparator
                .comparingInt(IPawnServicable::getServicePriority)
                .thenComparingDouble(serv -> serv.getPosition().distSqr(pawnPos)));
        return available;
    }

    public boolean tryClaimJob(IPawnServicable servicable, UUID pawnId, int durationTicks) {
        String id = servicable.getServiceId();
        long expireTime = System.currentTimeMillis() + durationTicks * 50L;

        synchronized (activeJobs) {
            JobEntry existing = activeJobs.get(id);
            if (existing != null && existing.expireTime() > System.currentTimeMillis()) {
                return false;
            }
            activeJobs.put(id, new JobEntry(id, pawnId, expireTime));
            setDirty();
            return true;
        }
    }

    public void releaseJob(IPawnServicable servicable) {
        activeJobs.remove(servicable.getServiceId());
        setDirty();
    }

    public void cleanExpiredJobs() {
        long now = System.currentTimeMillis();
        activeJobs.values().removeIf(job -> job.expireTime() <= now);
        setDirty();
    }
}