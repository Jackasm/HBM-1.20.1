package com.hbm.pawn;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MineRegistry extends SavedData {
    private static final String ID = "hbm_mine_registry";
    private final Map<BlockPos, MineSite> mines = new HashMap<>();

    public static MineRegistry get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(
                    MineRegistry::new,
                    MineRegistry::new,
                    ID
            );
        }
        return new MineRegistry();
    }

    public MineRegistry() {}

    public MineRegistry(CompoundTag tag) {
        ListTag list = tag.getList("mines", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            MineSite site = new MineSite();
            site.deserializeNBT(entry);
            mines.put(site.getPosition(), site);
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag list = new ListTag();
        for (MineSite site : mines.values()) {
            list.add(site.serializeNBT());
        }
        tag.put("mines", list);
        return tag;
    }

    public void addMine(MineSite site) {
        mines.put(site.getPosition(), site);
        setDirty();
        if (site.getServiceLevel() instanceof ServerLevel level) {
            PawnJobManager.get(level).registerServicable(site);
        }
    }

    public void removeMine(BlockPos pos) {
        mines.remove(pos);
        setDirty();
    }

    public MineSite getMine(BlockPos pos) {
        return mines.get(pos);
    }

    public Collection<MineSite> getAllMines() {
        return mines.values();
    }

}