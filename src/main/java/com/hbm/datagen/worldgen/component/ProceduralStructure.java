package com.hbm.datagen.worldgen.component;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Порт ProceduralStructureStart из 1.7.10.
 * Строит цепочку компонентов (комнат) через getNextComponentXxx.
 */
public class ProceduralStructure {

    public final List<NTMComponent> components = new ArrayList<>();
    public final List<NTMComponent> queuedComponents = new ArrayList<>();
    protected List<Weight> componentWeightList;

    protected int sizeLimit = 50;
    protected int distanceLimit = 64;

    public int chunkX;
    public int chunkZ;

    public record GenContext(
            net.minecraft.world.level.chunk.ChunkGenerator chunkGen,
            net.minecraft.world.level.LevelHeightAccessor height,
            net.minecraft.world.level.levelgen.RandomState randomState
    ) { }

    private static final ThreadLocal<GenContext> CONTEXT = new ThreadLocal<>();

    public static void setContext(GenContext ctx) { CONTEXT.set(ctx); }
    public static void clearContext() { CONTEXT.remove(); }

    public ProceduralStructure() { }

    public ProceduralStructure(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public ProceduralStructure buildStart(RandomSource rand, NTMComponent starter, Weight... weights) {
        prepareWeights(weights);
        components.add(starter);
        queuedComponents.add(starter);

        while (!queuedComponents.isEmpty()) {
            int i = rand.nextInt(queuedComponents.size());
            NTMComponent component = queuedComponents.remove(i);
            if (component instanceof ProceduralComponent pc) {
                pc.buildComponent(this, rand);
            }
        }
        return this;
    }

    public void prepareWeights(Weight... weights) {
        componentWeightList = new ArrayList<>(weights.length);
        for (Weight w : weights) {
            w.instancesSpawned = 0;
            componentWeightList.add(w);
        }
    }

    protected int getTotalWeight() {
        boolean any = false;
        int total = 0;
        for (Weight w : componentWeightList) {
            if (w.instanceLimit < 0 || w.instancesSpawned < w.instanceLimit) any = true;
            total += w.weight;
        }
        return any ? total : -1;
    }

    protected NTMComponent getWeightedComponent(RandomSource rand, int minX, int minY, int minZ, int coordMode, int componentType) {
        int totalWeight = getTotalWeight();
        if (totalWeight < 0) return null;

        for (int i = 0; i < 5; i++) {
            int value = rand.nextInt(totalWeight);
            Iterator<Weight> it = componentWeightList.iterator();
            while (it.hasNext()) {
                Weight weight = it.next();
                value -= weight.weight;

                if (value < 0) {
                    NTMComponent component = weight.factory.findValidPlacement(
                            components, rand, minX, minY, minZ, coordMode, componentType);
                    if (component != null) {
                        weight.instancesSpawned++;
                        if (!weight.canSpawnMoreStructures()) {
                            it.remove();
                        }
                        return component;
                    }
                }
            }
        }
        return null;
    }

    public NTMComponent getNextValidComponent(RandomSource rand, int minX, int minY, int minZ, int coordMode, int componentType) {
        if (components.size() > sizeLimit) return null;
        int cx = chunkX * 16 + 8;
        int cz = chunkZ * 16 + 8;
        if (Math.abs(minX - cx) <= distanceLimit && Math.abs(minZ - cz) <= distanceLimit) {
            NTMComponent component = getWeightedComponent(rand, minX, minY, minZ, coordMode, componentType);
            if (component != null) {
                components.add(component);
                queuedComponents.add(component);
            }
            return component;
        }
        return null;
    }

    /** 1-в-1 с оригиналом, только BoundingBox вместо StructureBoundingBox. */
    public static BoundingBox getComponentToAddBoundingBox(int posX, int posY, int posZ,
                                                           int offsetX, int offsetY, int offsetZ,
                                                           int maxX, int maxY, int maxZ, int coordMode) {
        return switch (coordMode) {
            case 1 -> BoundingBox.fromCorners(
                    new BlockPos(posX - maxZ + 1 - offsetZ, posY + offsetY, posZ + offsetX),
                    new BlockPos(posX - offsetZ,             posY + maxY - 1 + offsetY, posZ + maxX - 1 + offsetX));
            case 2 -> BoundingBox.fromCorners(
                    new BlockPos(posX - maxX + 1 - offsetX, posY + offsetY, posZ - maxZ + 1 - offsetZ),
                    new BlockPos(posX - offsetX,             posY + maxY - 1 + offsetY, posZ + offsetZ));
            case 3 -> BoundingBox.fromCorners(
                    new BlockPos(posX + offsetZ,             posY + offsetY, posZ - maxX + 1 - offsetX),
                    new BlockPos(posX + maxZ - 1 + offsetZ,  posY + maxY - 1 + offsetY, posZ - offsetX));
            default -> BoundingBox.fromCorners(
                    new BlockPos(posX + offsetX,             posY + offsetY, posZ + offsetZ),
                    new BlockPos(posX + maxX - 1 + offsetX,  posY + maxY - 1 + offsetY, posZ + maxZ - 1 + offsetZ));
        };
    }

    /** Проверка: пересекается ли box с чем-нибудь из components. */
    public static boolean findIntersecting(List<NTMComponent> components, BoundingBox box) {
        for (NTMComponent c : components) {
            if (c.getBoundingBox().intersects(box)) return true;
        }
        return false;
    }

    public interface ProceduralComponent {
        default void buildComponent(ProceduralStructure start, RandomSource rand) { }

        default NTMComponent getNextComponentNormal(ProceduralStructure start, NTMComponent caller, int coordMode, RandomSource rand, int offset, int offsetY) {
            BoundingBox box = caller.getBoundingBox();
            return switch (coordMode) {
                case 1 -> start.getNextValidComponent(rand, box.minX() - 1, box.minY() + offsetY, box.minZ() + offset, 1, caller.getComponentType());
                case 2 -> start.getNextValidComponent(rand, box.maxX() - offset, box.minY() + offsetY, box.minZ() - 1, 2, caller.getComponentType());
                case 3 -> start.getNextValidComponent(rand, box.maxX() + 1, box.minY() + offsetY, box.maxZ() - offset, 3, caller.getComponentType());
                default -> start.getNextValidComponent(rand, box.minX() + offset, box.minY() + offsetY, box.maxZ() + 1, 0, caller.getComponentType());
            };
        }

        default NTMComponent getNextComponentAntiNormal(ProceduralStructure start, NTMComponent caller, int coordMode, RandomSource rand, int offset, int offsetY) {
            BoundingBox box = caller.getBoundingBox();
            return switch (coordMode) {
                case 1 -> start.getNextValidComponent(rand, box.maxX() + 1, box.minY() + offsetY, box.maxZ() - offset, 3, caller.getComponentType());
                case 2 -> start.getNextValidComponent(rand, box.minX() + offset, box.minY() + offsetY, box.maxZ() + 1, 0, caller.getComponentType());
                case 3 -> start.getNextValidComponent(rand, box.minX() - 1, box.minY() + offsetY, box.minZ() + offset, 1, caller.getComponentType());
                default -> start.getNextValidComponent(rand, box.maxX() - offset, box.minY() + offsetY, box.minZ() - 1, 2, caller.getComponentType());
            };
        }

        default NTMComponent getNextComponentWest(ProceduralStructure start, NTMComponent caller, int coordMode, RandomSource rand, int offset, int offsetY) {
            BoundingBox box = caller.getBoundingBox();
            return switch (coordMode) {
                case 1 -> start.getNextValidComponent(rand, box.maxX() - offset, box.minY() + offsetY, box.minZ() - 1, 2, caller.getComponentType());
                case 2 -> start.getNextValidComponent(rand, box.maxX() + 1, box.minY() + offsetY, box.maxZ() - offset, 3, caller.getComponentType());
                case 3 -> start.getNextValidComponent(rand, box.minX() + offset, box.minY() + offsetY, box.maxZ() + 1, 0, caller.getComponentType());
                default -> start.getNextValidComponent(rand, box.minX() - 1, box.minY() + offsetY, box.minZ() + offset, 1, caller.getComponentType());
            };
        }

        default NTMComponent getNextComponentEast(ProceduralStructure start, NTMComponent caller, int coordMode, RandomSource rand, int offset, int offsetY) {
            BoundingBox box = caller.getBoundingBox();
            return switch (coordMode) {
                case 1 -> start.getNextValidComponent(rand, box.minX() + offset, box.minY() + offsetY, box.maxZ() + 1, 0, caller.getComponentType());
                case 2 -> start.getNextValidComponent(rand, box.minX() - 1, box.minY() + offsetY, box.minZ() + offset, 1, caller.getComponentType());
                case 3 -> start.getNextValidComponent(rand, box.maxX() - offset, box.minY() + offsetY, box.minZ() - 1, 2, caller.getComponentType());
                default -> start.getNextValidComponent(rand, box.maxX() + 1, box.minY() + offsetY, box.maxZ() - offset, 3, caller.getComponentType());
            };
        }
    }

    @FunctionalInterface
    public interface ComponentFactory {
        NTMComponent findValidPlacement(List<NTMComponent> components, RandomSource rand,
                                        int minX, int minY, int minZ, int coordMode, int componentType);
    }

    public static class Weight {
        public final ComponentFactory factory;
        public final int weight;
        public int instancesSpawned;
        public int instanceLimit;

        public Weight(int weight, int limit, ComponentFactory factory) {
            this.weight = weight;
            this.instanceLimit = limit;
            this.factory = factory;
        }

        public boolean canSpawnMoreStructures() {
            return this.instanceLimit < 0 || this.instancesSpawned < this.instanceLimit;
        }
    }
}