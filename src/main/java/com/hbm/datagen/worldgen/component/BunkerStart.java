package com.hbm.datagen.worldgen.component;

import net.minecraft.util.RandomSource;

public class BunkerStart extends ProceduralStructure {

    public BunkerStart() { }

    public BunkerStart(int chunkX, int chunkZ) {
        super(chunkX, chunkZ);
    }

    public static BunkerStart create(RandomSource rand, int chunkX, int chunkZ) {
        BunkerStart start = new BunkerStart(chunkX, chunkZ);
        start.sizeLimit = 7 + rand.nextInt(6);
        start.distanceLimit = 40;

        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;

        Weight[] weights = new Weight[] {
                new Weight(6, 3, Corridor::findValidPlacement),
                new Weight(5, 4, BedroomL::findValidPlacement),
                new Weight(10, 3, FunJunction::findValidPlacement),
                new Weight(5, 2, BathroomL::findValidPlacement),
                new Weight(7, 2, Laboratory::findValidPlacement),
                new Weight(5, 1, PowerRoom::findValidPlacement),
        };

        StartingHub starter = new StartingHub(rand, x, z);
        start.buildStart(rand, starter, weights);

        return start;
    }
}