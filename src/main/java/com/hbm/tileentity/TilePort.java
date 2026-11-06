package com.hbm.tileentity;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.uninos.GenNode;

import com.hbm.uninos.INetworkProvider;
import com.hbm.uninos.NodeNet;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.Library.PosDir;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TilePort — UNINOS-порты для машин.
 */
public class TilePort {

    protected GenNode node;
    protected NodeNet prevNet;
    protected INetworkProvider type;
    public BlockPos[] positions;
    public PosDir[] connections;

    public boolean needsRebuild = false;
    protected boolean isEnabled = true;
    protected int timeSinceNetworkChange = 0;
    protected Object owner;

    public TilePort setupOwner(Object owner) {
        this.owner = owner;
        return this;
    }

    public TilePort setupType(INetworkProvider type) {
        if (this.type != type) {
            this.type = type;
            this.needsRebuild = true;
        }
        return this;
    }

    public TilePort setupPort(PortDef def) {
        return this.setupPositions(def.portPositions).setupConnections(def.portConnections);
    }

    public TilePort setupPositions(BlockPos... pos) {
        this.positions = pos;
        this.needsRebuild = true;
        return this;
    }

    public TilePort setupConnections(PosDir... pos) {
        this.connections = pos;
        this.needsRebuild = true;
        return this;
    }

    public void update(Level level) {
        if (this.positions == null || this.connections == null || this.owner == null) return;

        NodeNet current = this.node != null ? this.node.net : null;
        if (current != this.prevNet) {
            this.timeSinceNetworkChange = 0;
            if (this.prevNet != null) this.prevNet.removeReceiver(owner);
            if (this.prevNet != null) this.prevNet.removeProvider(owner);
        } else {
            this.timeSinceNetworkChange++;
        }

        this.prevNet = this.node != null ? this.node.net : null;

        if (this.needsRebuild) {
            disableIfPresent(level);
            this.needsRebuild = false;
        }

        if (isEnabled()) {
            enableIfMissing(level);
        } else {
            disableIfPresent(level);
        }
    }

    public void enableIfMissing(Level level) {
        if (this.node == null || this.node.expired) {
            BlockPos pos = positions[0];
            this.node = UniNodespace.getNode(level, pos.getX(), pos.getY(), pos.getZ(), type);
        }
        if (this.node == null || this.node.expired) {
            this.createNode(level);
        }
    }

    public void disableIfPresent(Level level) {
        if (this.node != null) {
            UniNodespace.destroyNode(level, node);
            this.node = null;
        }
    }

    protected void createNode(Level level) {
        this.node = new GenNode(this.type, positions);
        GenNode.ConnectionPoint[] cps = new GenNode.ConnectionPoint[connections.length];
        for (int i = 0; i < connections.length; i++) {
            cps[i] = new GenNode.ConnectionPoint(connections[i].pos(), connections[i].dir());
        }
        this.node.setConnections(cps);
        UniNodespace.createNode(level, this.node);
    }

    public void checkSubscribe(Level level) {
        if (this.node == null) return;

        if (timeSinceNetworkChange < 2) {
            if (this.node.net != null) this.node.net.addReceiver(owner);
        }

    }

    public void checkProvide(Level level) {
        if (this.node == null) return;

        if (timeSinceNetworkChange < 2) {
            if (this.node.net != null) this.node.net.addProvider(owner);
        }

    }

    public void forceRebuild() { this.needsRebuild = true; }

    public void enable() {
        if (!isEnabled) {
            isEnabled = true;
            this.forceRebuild();
        }
    }

    public void disable() {
        if (isEnabled) {
            isEnabled = false;
            this.forceRebuild();
        }
    }

    protected boolean isEnabled() {
        if (!isEnabled) return false;
        if (type == null) return false;
        if (type == Fluids.NONE.get().getNetworkProvider()) return false;
        return true;
    }

    public static TilePort[] oneToMany(Object owner, int portCount, PortDef def) {
        TilePort[] ports = new TilePort[portCount];
        for (int i = 0; i < portCount; i++) {
            ports[i] = new TilePort().setupOwner(owner)
                    .setupPositions(def.portPositions)
                    .setupConnections(def.portConnections);
        }
        return ports;
    }

    public static TilePort[] manyToMany(Object owner, PortDef... defs) {
        TilePort[] ports = new TilePort[defs.length];
        for (int i = 0; i < defs.length; i++) {
            ports[i] = new TilePort().setupOwner(owner)
                    .setupPositions(defs[i].portPositions)
                    .setupConnections(defs[i].portConnections);
        }
        return ports;
    }

    public NodeNet getNetwork() {
        if (this.node == null || this.node.expired || this.node.net == null || !this.node.net.isValid()) return null;
        return this.node.net;
    }

    public static class PortDef {
        public BlockPos[] portPositions;
        public PosDir[] portConnections;

        public PortDef atPos(BlockPos... pos) {
            this.portPositions = pos;
            return this;
        }

        public PortDef withCon(PosDir... pos) {
            this.portConnections = pos;
            return this;
        }

        public static PortDef make(int x, int y, int z, Direction... dirs) {
            PosDir[] cons = new PosDir[dirs.length];
            PortDef def = new PortDef().atPos(new BlockPos(x, y, z));
            for (int i = 0; i < cons.length; i++) {
                cons[i] = new PosDir(new BlockPos(x + dirs[i].getStepX(),
                        y + dirs[i].getStepY(),
                        z + dirs[i].getStepZ()), dirs[i]);
            }
            def.withCon(cons);
            return def;
        }

        public static PortDef combine(PortDef... defs) {
            PortDef single = new PortDef();
            List<BlockPos> pos = new ArrayList<>();
            List<PosDir> con = new ArrayList<>();
            for (PortDef def : defs) {
                pos.addAll(Arrays.asList(def.portPositions));
                con.addAll(Arrays.asList(def.portConnections));
            }
            single.atPos(pos.toArray(new BlockPos[0])).withCon(con.toArray(new PosDir[0]));
            return single;
        }
    }
}