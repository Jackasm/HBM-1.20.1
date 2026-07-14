package com.hbm.uninos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Generic node class for UNINOS networks
 * @param <N> Network type
 */
public class GenNode<N extends NodeNet> {

    private final BlockPos[] positions;
    private ConnectionPoint[] connections;
    public N net;
    public boolean expired = false;
    private boolean recentlyChanged = true;
    private final INetworkProvider<N> networkProvider;

    /**
     * Record to store a position with a direction
     */
    public record ConnectionPoint(BlockPos pos, Direction dir) {
        public ConnectionPoint offset(Direction offsetDir) {
            return new ConnectionPoint(pos.relative(offsetDir), dir);
        }

        public ConnectionPoint opposite() {
            return new ConnectionPoint(pos, dir.getOpposite());
        }
    }

    public GenNode(INetworkProvider<N> provider, BlockPos... positions) {
        this.networkProvider = provider;
        this.positions = positions.clone();
        this.connections = new ConnectionPoint[0];
    }

    /**
     * Returns all node positions (core + extra)
     */
    public BlockPos[] getPositions() {
        return positions.clone();
    }

    /**
     * Sets the connection points for this node
     */
    public GenNode<N> setConnections(ConnectionPoint... connections) {
        this.connections = connections.clone();
        return this;
    }

    /**
     * Sets connections using separate arrays
     */
    public GenNode<N> setConnections(BlockPos[] positions, Direction[] directions) {
        if (positions.length != directions.length) {
            throw new IllegalArgumentException("Positions and directions arrays must have the same length");
        }
        this.connections = new ConnectionPoint[positions.length];
        for (int i = 0; i < positions.length; i++) {
            this.connections[i] = new ConnectionPoint(positions[i], directions[i]);
        }
        return this;
    }

    /**
     * Adds a single connection point
     */
    public GenNode<N> addConnection(ConnectionPoint connection) {
        ConnectionPoint[] newCons = new ConnectionPoint[this.connections.length + 1];
        System.arraycopy(this.connections, 0, newCons, 0, this.connections.length);
        newCons[newCons.length - 1] = connection;
        this.connections = newCons;
        return this;
    }

    /**
     * Adds a connection with separate position and direction
     */
    public GenNode<N> addConnection(BlockPos pos, Direction dir) {
        return addConnection(new ConnectionPoint(pos, dir));
    }

    /**
     * Returns all connection points
     */
    public ConnectionPoint[] getConnections() {
        return connections.clone();
    }

    /**
     * Returns the network provider type
     */
    public INetworkProvider<N> getNetworkProvider() {
        return networkProvider;
    }

    /**
     * Checks if this node has a valid network
     */
    public boolean hasValidNet() {
        return net != null && net.isValid();
    }

    /**
     * Gets the current network
     */
    public N getNet() {
        return net;
    }

    /**
     * Sets the network for this node
     */
    public void setNet(N net) {
        this.net = net;
        this.recentlyChanged = true;
    }

    /**
     * Returns whether this node has recently changed (new network, etc.)
     */
    public boolean hasRecentlyChanged() {
        return recentlyChanged;
    }

    /**
     * Resets the recently changed flag
     */
    public void resetChanged() {
        this.recentlyChanged = false;
    }

    /**
     * Returns whether this node is expired (should be removed)
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * Marks this node as expired
     */
    public void expire() {
        this.expired = true;
    }

    /**
     * Checks if a position is part of this node
     */
    public boolean containsPos(BlockPos pos) {
        for (BlockPos p : positions) {
            if (p.equals(pos)) return true;
        }
        return false;
    }

    /**
     * Finds a connection point at a specific position
     */
    public ConnectionPoint getConnectionAt(BlockPos pos) {
        for (ConnectionPoint conn : connections) {
            if (conn.pos().equals(pos)) return conn;
        }
        return null;
    }
}