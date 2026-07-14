package com.hbm.uninos;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unified Nodespace, a Nodespace for all applications.
 * "Nodespace" is an invisible "dimension" where nodes exist, a node is basically the "soul" of a tile entity with networking capabilities.
 * Instead of tile entities having to find each other which is costly and assumes the tiles are loaded, tiles simply create nodes at their
 * respective position in nodespace, the nodespace itself handles stuff like connections which can also happen in unloaded chunks.
 * A node is so to say the "soul" of a tile entity which can act independent of its "body".
 */
public class UniNodespace {

    public static final Map<Level, UniNodeWorld> WORLDS = new ConcurrentHashMap<>();
    public static final Set<NodeNet> ACTIVE_NODE_NETS = ConcurrentHashMap.newKeySet();

    /**
     * Gets a node at a specific position
     */
    public static GenNode<?> getNode(Level world, BlockPos pos, INetworkProvider<?> type) {
        UniNodeWorld nodeWorld = WORLDS.get(world);
        if (nodeWorld != null) {
            return nodeWorld.nodes.get(new NodeKey(pos, type));
        }
        return null;
    }

    /**
     * Gets a node at specific coordinates
     */
    public static GenNode<?> getNode(Level world, int x, int y, int z, INetworkProvider<?> type) {
        return getNode(world, new BlockPos(x, y, z), type);
    }

    /**
     * Creates a new node in the nodespace
     */
    public static void createNode(Level world, GenNode<?> node) {
        UniNodeWorld nodeWorld = WORLDS.computeIfAbsent(world, k -> new UniNodeWorld());
        nodeWorld.pushNode(node);
    }

    /**
     * Destroys a node at a specific position
     */
    public static void destroyNode(Level world, BlockPos pos, INetworkProvider<?> type) {
        GenNode<?> node = getNode(world, pos, type);
        if (node != null) {
            UniNodeWorld nodeWorld = WORLDS.get(world);
            if (nodeWorld != null) {
                nodeWorld.popNode(node);
            }
        }
    }

    /**
     * Destroys a node at specific coordinates
     */
    public static void destroyNode(Level world, int x, int y, int z, INetworkProvider<?> type) {
        destroyNode(world, new BlockPos(x, y, z), type);
    }

    /**
     * Registers a network in the active set
     */
    public static void registerNet(NodeNet net) {
        ACTIVE_NODE_NETS.add(net);
    }

    /**
     * Unregisters a network from the active set
     */
    public static void unregisterNet(NodeNet net) {
        ACTIVE_NODE_NETS.remove(net);
    }

    /**
     * Updates the entire nodespace - should be called every tick
     */
    public static void tick() {
        // First pass: check node connections
        for (Map.Entry<Level, UniNodeWorld> worldEntry : WORLDS.entrySet()) {
            Level world = worldEntry.getKey();
            UniNodeWorld nodeWorld = worldEntry.getValue();

            if (world == null || nodeWorld == null) continue;

            // Create a copy to avoid concurrent modification
            List<Map.Entry<NodeKey, GenNode<?>>> entries = new ArrayList<>(nodeWorld.nodes.entrySet());

            for (Map.Entry<NodeKey, GenNode<?>> entry : entries) {
                GenNode<?> node = entry.getValue();
                NodeKey key = entry.getKey();

                if (node.isExpired()) {
                    nodeWorld.popNode(node);
                    continue;
                }

                if (!node.hasValidNet() || node.hasRecentlyChanged()) {
                    checkNodeConnection(world, node, key.provider);
                    node.resetChanged();
                }
            }
        }

        // Second pass: update networks
        updateNetworks();
    }

    /**
     * Updates all active networks
     */
    private static void updateNetworks() {
        // Reset trackers
        for (NodeNet net : ACTIVE_NODE_NETS) {
            if (net.isValid()) {
                // net.resetTrackers(); // Removed as it's not in the interface
            }
        }

        // Update networks
        List<NodeNet> netsCopy = new ArrayList<>(ACTIVE_NODE_NETS);
        for (NodeNet net : netsCopy) {
            if (net.isValid()) {
                net.tick();
            } else {
                ACTIVE_NODE_NETS.remove(net);
            }
        }
    }

    /**
     * Checks connections for a node and attempts to join networks
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void checkNodeConnection(Level world, GenNode<?> node, INetworkProvider<?> provider) {
        for (GenNode.ConnectionPoint conn : node.getConnections()) {
            GenNode<?> conNode = getNode(world, conn.pos(), provider);

            if (conNode != null) {
                // If both nodes are in the same valid net, skip
                if (conNode.hasValidNet() && conNode.getNet() == node.getNet()) {
                    continue;
                }

                if (checkConnection(conNode, conn, false)) {
                    connectToNode(node, conNode);
                }
            }
        }

        // If node still has no net, have the provider create one
        if (node.getNet() == null || !node.getNet().isValid()) {
            NodeNet net = provider.provideNetwork();
            if (net != null) {
                net.joinLink(node);
            }
        }
    }

    /**
     * Checks if a node can be connected to from a given connection point
     */
    public static boolean checkConnection(GenNode<?> targetNode, GenNode.ConnectionPoint sourcePoint, boolean skipSideCheck) {
        for (GenNode.ConnectionPoint revCon : targetNode.getConnections()) {
            // Calculate the expected position that should connect to us
            BlockPos expectedPos = revCon.pos().relative(revCon.dir().getOpposite());

            if (expectedPos.equals(sourcePoint.pos()) &&
                    (revCon.dir().getOpposite() == sourcePoint.dir() || skipSideCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Connects two nodes, merging or creating networks as needed
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void connectToNode(GenNode<?> origin, GenNode<?> connection) {
        NodeNet originNet = origin.getNet();
        NodeNet connectionNet = connection.getNet();

        if (originNet != null && originNet.isValid() && connectionNet != null && connectionNet.isValid()) {
            // Both have valid nets, merge them (choose the larger one)
            if (originNet.getLinks().size() > connectionNet.getLinks().size()) {
                originNet.merge(connectionNet);
            } else {
                connectionNet.merge(originNet);
            }
        } else if (connectionNet != null && connectionNet.isValid()) {
            // Only connection has a net
            connectionNet.joinLink(origin);
        } else if (originNet != null && originNet.isValid()) {
            // Only origin has a net
            originNet.joinLink(connection);
        }
        // If neither has a net, do nothing - will be handled by provider later
    }

    /**
     * Key class for node storage
     */
    public record NodeKey(BlockPos pos, INetworkProvider<?> provider) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NodeKey key)) return false;
            return pos.equals(key.pos) && provider.equals(key.provider);
        }

        @Override
        public int hashCode() {
            return 31 * pos.hashCode() + provider.hashCode();
        }
    }

    /**
     * World-specific node storage
     */
    public static class UniNodeWorld {
        private final Map<NodeKey, GenNode<?>> nodes = new LinkedHashMap<>();

        /**
         * Adds a node at all its positions to the nodespace
         */
        public void pushNode(GenNode<?> node) {
            for (BlockPos pos : node.getPositions()) {
                nodes.put(new NodeKey(pos, node.getNetworkProvider()), node);
            }
        }

        /**
         * Removes the specified node from all positions from nodespace
         */
        public void popNode(GenNode<?> node) {
            if (node.getNet() != null) {
                node.getNet().destroy();
            }

            for (BlockPos pos : node.getPositions()) {
                nodes.remove(new NodeKey(pos, node.getNetworkProvider()));
            }

            node.expire();
        }

        /**
         * Gets all nodes in this world
         */
        public Map<NodeKey, GenNode<?>> getNodes() {
            return Collections.unmodifiableMap(nodes);
        }
    }
}