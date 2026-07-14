package com.hbm.handler.neutron;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;

public abstract class NeutronStream {

    public enum NeutronType {
        DUMMY, RBMK, PILE
    }

    public final NeutronNode origin;
    public double fluxQuantity;
    public double fluxRatio;
    public NeutronType type;
    public final Vec3 vector;
    private final BlockPos posInstance;
    private int i;

    public NeutronStream(NeutronNode origin, Vec3 vector) {
        this.origin = origin;
        this.vector = vector;
        this.posInstance = origin.pos.immutable();
        this.type = NeutronType.DUMMY;
    }

    public NeutronStream(NeutronNode origin, Vec3 vector, double flux, double ratio, NeutronType type) {
        this(origin, vector);
        this.fluxQuantity = flux;
        this.fluxRatio = ratio;
        this.type = type;
        NeutronNodeWorld.getOrAddWorld(origin.tile.getLevel()).addStream(this);
    }

    public Iterator<BlockPos> getBlocks(int range) {
        i = 1;
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return i <= range;
            }

            @Override
            public BlockPos next() {
                int x = (int) Math.floor(0.5 + vector.x * i);
                int z = (int) Math.floor(0.5 + vector.z * i);
                i++;
                return posInstance.offset(x, 0, z);
            }
        };
    }

    public abstract void runStreamInteraction(Level level, NeutronNodeWorld.StreamWorld streamWorld);
}