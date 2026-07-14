package com.hbm.uninos;

import com.hbm.api.tile.ILoadedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract network class for all UNINOS networks (energy, fluid, etc.)
 * @param <R> Receiver type
 * @param <P> Provider type
 * @param <L> Node (link) type
 */
public abstract class NodeNet<R, P, L extends GenNode> {

    /** Global random for distribution */
    public static final Random RANDOM = new Random();

    private boolean valid = true;
    private final Set<L> links = ConcurrentHashMap.newKeySet();

    private final Map<R, Long> receivers = new ConcurrentHashMap<>();
    private final Map<P, Long> providers = new ConcurrentHashMap<>();

    public NodeNet() {
        UniNodespace.registerNet(this);
    }

    // ------------------------------------------------------------------------
    // Receiver handling
    // ------------------------------------------------------------------------

    public boolean isReceiver(R receiver) {
        return receivers.containsKey(receiver);
    }

    public void addReceiver(R receiver) {
        receivers.put(receiver, System.currentTimeMillis());
    }

    public void removeReceiver(R receiver) {
        receivers.remove(receiver);
    }

    public Map<R, Long> getReceivers() {
        return Collections.unmodifiableMap(receivers);
    }

    // ------------------------------------------------------------------------
    // Provider handling
    // ------------------------------------------------------------------------

    public boolean isProvider(P provider) {
        return providers.containsKey(provider);
    }

    public void addProvider(P provider) {
        providers.put(provider, System.currentTimeMillis());
    }

    public void removeProvider(P provider) {
        providers.remove(provider);
    }

    public Map<P, Long> getProviders() {
        return Collections.unmodifiableMap(providers);
    }

    // ------------------------------------------------------------------------
    // Link handling
    // ------------------------------------------------------------------------

    public Set<L> getLinks() {
        return Collections.unmodifiableSet(links);
    }

    /**
     * Adds a link to this network, removing it from its current network first
     */
    public NodeNet<R, P, L> joinLink(L link) {
        if (link.getNet() != null && link.getNet() != this) {
            link.getNet().leaveLink(link);
        }
        return forceJoinLink(link);
    }

    /**
     * Adds a link without removing from previous network (internal use)
     */
    public NodeNet<R, P, L> forceJoinLink(L link) {
        links.add(link);
        link.setNet(this);
        return this;
    }

    /**
     * Removes a link from this network
     */
    public void leaveLink(L link) {
        if (links.remove(link)) {
            link.setNet(null);
        }
    }

    /**
     * Merges another network into this one
     */
    public void merge(NodeNet<R, P, L> other) {
        if (other == this) return;

        // Transfer all links
        List<L> otherLinks = new ArrayList<>(other.links);
        for (L link : otherLinks) {
            forceJoinLink(link);
        }
        other.links.clear();

        // Transfer all receivers and providers
        other.receivers.forEach((receiver, timestamp) -> this.addReceiver(receiver));
        other.providers.forEach((provider, timestamp) -> this.addProvider(provider));

        // Clean up the other network
        other.invalidate();
    }

    // ------------------------------------------------------------------------
    // Network lifecycle
    // ------------------------------------------------------------------------

    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        this.valid = false;
        UniNodespace.unregisterNet(this);
    }

    /**
     * Clean up dead links and outdated entries
     */
    protected void cleanup() {
        links.removeIf(NodeNet::isInvalid);
        receivers.keySet().removeIf(NodeNet::isInvalid);
        providers.keySet().removeIf(NodeNet::isInvalid);
    }

    /**
     * Called every tick to update network state
     */
    public abstract void tick();

    /**
     * Destroys this network completely
     */
    public void destroy() {
        invalidate();

        // Disconnect all links
        List<L> linksCopy = new ArrayList<>(links);
        for (L link : linksCopy) {
            if (link.getNet() == this) {
                link.setNet(null);
            }
        }
        links.clear();

        // Clear all connections
        receivers.clear();
        providers.clear();
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * Checks if a network participant is still valid
     */
    public static boolean isInvalid(Object obj) {
        if (obj == null) return true;
        if (obj instanceof ILoadedTile loaded && !loaded.isLoaded()) return true;
        if (obj instanceof BlockEntity be && be.isRemoved()) return true;
        return false;
    }

    /**
     * Checks if a block position is still loaded
     */
    public static boolean isLoaded(BlockPos pos, BlockEntity be) {
        return be != null && !be.isRemoved() && be.getLevel() != null && be.getLevel().isLoaded(pos);
    }
}