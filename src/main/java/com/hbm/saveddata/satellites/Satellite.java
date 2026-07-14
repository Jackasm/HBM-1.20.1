package com.hbm.saveddata.satellites;

import com.hbm.items.ModItems;
import com.hbm.saveddata.SatelliteSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Satellite {

    public static final List<Class<? extends Satellite>> satellites = new ArrayList<>();
    public static final HashMap<Item, Class<? extends Satellite>> itemToClass = new HashMap<>();

    public enum InterfaceActions {
        HAS_MAP,        //lets the interface display loaded chunks
        CAN_CLICK,      //enables onClick events
        SHOW_COORDS,    //enables coordinates as a mouse tooltip
        HAS_RADAR,      //lets the interface display loaded entities
        HAS_ORES        //like HAS_MAP but only shows ores
    }

    public enum CoordActions {
        HAS_Y           //enables the Y-coord field which is disabled by default
    }

    public enum Interfaces {
        NONE,           //does not interact with any sat interface (i.e. asteroid miners)
        SAT_PANEL,      //allows to interact with the sat interface panel (for graphical applications)
        SAT_COORD       //allows to interact with the sat coord remote (for teleportation or other coord related actions)
    }

    public List<InterfaceActions> ifaceAcs = new ArrayList<>();
    public List<CoordActions> coordAcs = new ArrayList<>();
    public Interfaces satIface = Interfaces.NONE;

    public static void register() {
        registerSatellite(SatelliteMapper.class, ModItems.SAT_MAPPER.get());
        registerSatellite(SatelliteScanner.class, ModItems.SAT_SCANNER.get());
        registerSatellite(SatelliteRadar.class, ModItems.SAT_RADAR.get());
        registerSatellite(SatelliteLaser.class, ModItems.SAT_LASER.get());
        registerSatellite(SatelliteResonator.class, ModItems.SAT_RESONATOR.get());
        registerSatellite(SatelliteRelay.class, ModItems.SAT_FOEQ.get());
        registerSatellite(SatelliteMiner.class, ModItems.SAT_MINER.get());
        registerSatellite(SatelliteLunarMiner.class, ModItems.SAT_LUNAR_MINER.get());
        registerSatellite(SatelliteHorizons.class, ModItems.SAT_GERALD.get());
    }

    public static void registerSatellite(Class<? extends Satellite> sat, Item item) {
        if (!itemToClass.containsKey(item) && !itemToClass.containsValue(sat)) {
            satellites.add(sat);
            itemToClass.put(item, sat);
        }
    }

    public static void orbit(Level level, int id, int freq, double x, double y, double z) {
        if (level.isClientSide) {
            return;
        }

        Satellite sat = create(id);

        if (sat != null) {
            SatelliteSavedData data = SatelliteSavedData.getData(level);
            data.sats.put(freq, sat);
            sat.onOrbit(level, x, y, z);
            data.markDirty();
        }
    }

    public static Satellite create(int id) {
        Satellite sat = null;

        try {
            Class<? extends Satellite> c = satellites.get(id);
            sat = c.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sat;
    }

    public static int getIDFromItem(Item item) {
        Class<? extends Satellite> sat = itemToClass.get(item);
        return satellites.indexOf(sat);
    }

    public int getID() {
        return satellites.indexOf(this.getClass());
    }

    public void writeToNBT(CompoundTag nbt) { }

    public void readFromNBT(CompoundTag nbt) { }

    public void onOrbit(Level level, double x, double y, double z) { }

    public void onClick(Level level, int x, int z) { }

    public void onCoordAction(Level level, Player player, int x, int y, int z) { }
}