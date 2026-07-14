package com.hbm.saveddata.satellites;

import com.hbm.itempool.ItemPoolsSatellite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

import java.util.HashMap;

public class SatelliteMiner extends Satellite {

    private static final HashMap<Class<? extends SatelliteMiner>, String> CARGO = new HashMap<>();

    public long lastOp;

    public SatelliteMiner() {
        this.satIface = Interfaces.NONE;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putLong("lastOp", lastOp);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        lastOp = nbt.getLong("lastOp");
    }

    public static void registerCargo(Class<? extends SatelliteMiner> minerSatelliteClass, String cargo) {
        CARGO.put(minerSatelliteClass, cargo);
    }

    public String getCargo() {
        return CARGO.get(getClass());
    }

    public static String getCargoForItem(Item satelliteItem) {
        Class<? extends Satellite> satelliteClass = itemToClass.getOrDefault(satelliteItem, null);
        return satelliteClass != null ? CARGO.getOrDefault(satelliteClass, null) : null;
    }

    static {
        registerCargo(SatelliteMiner.class, ItemPoolsSatellite.POOL_SAT_MINER);
    }
}