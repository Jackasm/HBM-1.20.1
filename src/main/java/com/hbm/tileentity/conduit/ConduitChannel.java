package com.hbm.tileentity.conduit;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumSet;

public abstract class ConduitChannel {
    public boolean isLoaded = true;

    protected ConduitEntry entry;
    protected EnumSet<Direction> connections = EnumSet.noneOf(Direction.class);

    public ConduitChannel(ConduitEntry entry) {
        this.entry = entry;
    }

    public ConduitEntry getEntry() {
        return entry;
    }

    public EnumSet<Direction> getConnections() {
        return connections;
    }

    public void setConnections(EnumSet<Direction> connections) {
        this.connections = connections;
    }

    public abstract void tick(ConduitTileEntity host);

}