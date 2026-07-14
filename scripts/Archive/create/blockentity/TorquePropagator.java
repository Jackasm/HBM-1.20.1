package com.hbm.create.blockentity;

import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.Map;

public class TorquePropagator {

    static Map<LevelAccessor, Map<Long, KineticNetwork>> networks = new HashMap<>();

    public void onLoadWorld(LevelAccessor world) {
        networks.put(world, new HashMap<>());
    }

    public void onUnloadWorld(LevelAccessor world) {
        networks.remove(world);
    }

    public KineticNetwork getOrCreateNetworkFor(KineticBlockEntity be) {
        Long id = be.network;
        KineticNetwork network;
        Map<Long, KineticNetwork> map = networks.computeIfAbsent(be.getLevel(), $ -> new HashMap<>());
        if (id == null)
            return null;

        if (!map.containsKey(id)) {
            network = new KineticNetwork();
            network.id = be.network;
            map.put(id, network);
        }
        network = map.get(id);
        return network;
    }

}
