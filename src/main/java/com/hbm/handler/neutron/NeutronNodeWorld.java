package com.hbm.handler.neutron;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NeutronNodeWorld {

    public static final HashMap<Level, StreamWorld> streamWorlds = new HashMap<>();

    public static NeutronNode getNode(Level level, BlockPos pos) {
        StreamWorld sw = streamWorlds.get(level);
        return sw != null ? sw.nodeCache.get(pos) : null;
    }

    public static void removeNode(Level level, BlockPos pos) {
        StreamWorld sw = streamWorlds.get(level);
        if (sw != null) sw.removeNode(pos);
    }

    public static StreamWorld getOrAddWorld(Level level) {
        return streamWorlds.computeIfAbsent(level, k -> new StreamWorld());
    }

    public static void removeAllWorlds() {
        streamWorlds.clear();
    }

    public static void removeEmptyWorlds() {
        streamWorlds.values().removeIf(sw -> sw.streams.isEmpty());
    }

    public static class StreamWorld {
        private final List<NeutronStream> streams = new ArrayList<>();
        private final HashMap<BlockPos, NeutronNode> nodeCache = new HashMap<>();

        public void runStreamInteractions(Level level) {
            for (NeutronStream stream : streams) {
                stream.runStreamInteraction(level, this);
            }
        }

        public void addStream(NeutronStream stream) {
            streams.add(stream);
        }

        public void removeAllStreams() {
            streams.clear();
        }

        public void cleanNodes() {
            // Пока для Pile нет очистки, можно расширить позже
        }

        public NeutronNode getNode(BlockPos pos) {
            return nodeCache.get(pos);
        }

        public void addNode(NeutronNode node) {
            nodeCache.put(node.pos, node);
        }

        public void removeNode(BlockPos pos) {
            nodeCache.remove(pos);
        }

        public void removeAllStreamsOfType(NeutronStream.NeutronType type) {
            streams.removeIf(s -> s.type == type);
        }
    }
}