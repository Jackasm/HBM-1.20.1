package com.hbm.saveddata;

import com.hbm.saveddata.satellites.Satellite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map.Entry;

public class SatelliteSavedData extends SavedData {

    public final HashMap<Integer, Satellite> sats = new HashMap<>();

    public SatelliteSavedData() {
        super();
    }

    public SatelliteSavedData(CompoundTag nbt) {
        load(nbt);
    }

    public boolean isFreqTaken(int freq) {
        return getSatFromFreq(freq) != null;
    }

    public Satellite getSatFromFreq(int freq) {
        return sats.get(freq);
    }

    public void load(CompoundTag nbt) {
        ListTag list = nbt.getList("satellites", 10);
        sats.clear();

        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            int id = tag.getInt("id");
            int freq = tag.getInt("freq");
            CompoundTag data = tag.getCompound("data");

            Satellite sat = Satellite.create(id);
            if (sat != null) {
                sat.readFromNBT(data);
                sats.put(freq, sat);
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag list = new ListTag();

        for (Entry<Integer, Satellite> entry : sats.entrySet()) {
            CompoundTag tag = new CompoundTag();
            Satellite sat = entry.getValue();
            int freq = entry.getKey();

            tag.putInt("id", sat.getID());
            tag.putInt("freq", freq);

            CompoundTag data = new CompoundTag();
            sat.writeToNBT(data);
            tag.put("data", data);

            list.add(tag);
        }

        nbt.put("satellites", list);
        return nbt;
    }

    public static SatelliteSavedData getData(Level level) {
        if (level.isClientSide) {
            // На клиенте нет сохранения данных
            return null;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        DimensionDataStorage storage = serverLevel.getDataStorage();

        return storage.computeIfAbsent(
                SatelliteSavedData::new,
                SatelliteSavedData::new,
                "satellites"
        );
    }

    public void markDirty() {
        this.setDirty();
    }
}