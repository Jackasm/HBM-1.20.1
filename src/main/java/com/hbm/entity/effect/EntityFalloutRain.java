package com.hbm.entity.effect;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.BombConfig;
import com.hbm.config.FalloutConfigJSON;
import com.hbm.config.FalloutConfigJSON.FalloutEntry;
import com.hbm.config.WorldConfig;
import com.hbm.datagen.worldgen.ModBiomes;
import com.hbm.entity.ModEntities;
import com.hbm.entity.item.EntityFallingBlockNT;
import com.hbm.entity.logic.EntityExplosionChunkloading;

import com.hbm.world.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityFalloutRain extends EntityExplosionChunkloading {

    private static final EntityDataAccessor<Integer> DATA_SCALE = SynchedEntityData.defineId(EntityFalloutRain.class, EntityDataSerializers.INT);

    private boolean firstTick = true;
    private int tickDelay = BombConfig.F_DELAY;

    private final List<Long> chunksToProcess = new ArrayList<>();
    private final List<Long> outerChunksToProcess = new ArrayList<>();

    public EntityFalloutRain(EntityType<?> type, Level level) {
        super(type, level);
        this.setBoundingBox(new AABB(this.getX(), this.getY(), this.getZ(), this.getX(), this.getY(), this.getZ()).inflate(4, 20, 4));
        this.setInvulnerable(true);
    }

    public EntityFalloutRain(EntityType<?> type, Level level, int maxAge) {
        super(type, level);
        this.setBoundingBox(new AABB(this.getX(), this.getY(), this.getZ(), this.getX(), this.getY(), this.getZ()).inflate(4, 20, 4));
        this.setInvulnerable(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SCALE, 0);
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            long start = System.currentTimeMillis();

            if (firstTick) {
                if (chunksToProcess.isEmpty() && outerChunksToProcess.isEmpty()) {
                    gatherChunks();
                }
                firstTick = false;
            }

            if (tickDelay == 0) {
                tickDelay = BombConfig.F_DELAY;

                while (System.currentTimeMillis() < start + BombConfig.MK5) {
                    if (!chunksToProcess.isEmpty()) {
                        long chunkPos = chunksToProcess.remove(chunksToProcess.size() - 1);
                        int chunkPosX = (int) (chunkPos & Integer.MAX_VALUE);
                        int chunkPosZ = (int) ((chunkPos >> 32) & Integer.MAX_VALUE);

                        for (int x = chunkPosX << 4; x < (chunkPosX << 4) + 16; x++) {
                            for (int z = chunkPosZ << 4; z < (chunkPosZ << 4) + 16; z++) {
                                double percent = Math.hypot(x - getX(), z - getZ()) * 100 / getScale();
                                stomp(x, z, percent);

                                Biome biome = getBiomeChange(percent, getScale(),
                                        level().getBiome(new BlockPos(x, 0, z)).value());

                                if (biome != null) {
                                    Registry<Biome> registry = level().registryAccess().registryOrThrow(Registries.BIOME);
                                    Optional<ResourceKey<Biome>> key = registry.getResourceKey(biome);

                                    int finalX = x;
                                    int finalZ = z;
                                    key.flatMap(registry::getHolder).ifPresent(biomeHolder -> WorldUtil.setBiomeAndSync(level(), finalX, finalZ, biomeHolder));
                                }
                            }
                        }

                    } else if (!outerChunksToProcess.isEmpty()) {
                        long chunkPos = outerChunksToProcess.remove(outerChunksToProcess.size() - 1);
                        int chunkPosX = (int) (chunkPos & Integer.MAX_VALUE);
                        int chunkPosZ = (int) ((chunkPos >> 32) & Integer.MAX_VALUE);

                        for (int x = chunkPosX << 4; x < (chunkPosX << 4) + 16; x++) {
                            for (int z = chunkPosZ << 4; z < (chunkPosZ << 4) + 16; z++) {
                                double percent = Math.hypot(x - getX(), z - getZ()) * 100 / getScale();
                                stomp(x, z, percent);

                                Biome biome = getBiomeChange(percent, getScale(),
                                        level().getBiome(new BlockPos(x, 0, z)).value());

                                if (biome != null) {
                                    Registry<Biome> registry = level().registryAccess().registryOrThrow(Registries.BIOME);
                                    Optional<ResourceKey<Biome>> key = registry.getResourceKey(biome);

                                    int finalX = x;
                                    int finalZ = z;
                                    key.flatMap(registry::getHolder).ifPresent(biomeHolder -> WorldUtil.setBiomeAndSync(level(), finalX, finalZ, biomeHolder));
                                }
                            }
                        }

                    } else {
                        this.clearChunkLoader();
                        this.discard();
                        break;
                    }
                }
            }

            tickDelay--;
        }
    }

    public Biome getBiomeChange(double dist, int scale, Biome original) {
        if (!WorldConfig.ENABLE_CRATER_BIOMES) return null;

        ResourceKey<Biome> originalKey = level().registryAccess()
                .registryOrThrow(Registries.BIOME)
                .getResourceKey(original)
                .orElse(null);

        var biomeRegistry = level().registryAccess().registryOrThrow(Registries.BIOME);

        if (scale >= 150 && dist < 15) {
            return biomeRegistry.get(ModBiomes.CRATER_INNER.location());
        }
        if (scale >= 100 && dist < 55 && originalKey != ModBiomes.CRATER_INNER) {
            return biomeRegistry.get(ModBiomes.CRATER.location());
        }
        if (scale >= 25 && originalKey != ModBiomes.CRATER_INNER && originalKey != ModBiomes.CRATER) {
            return biomeRegistry.get(ModBiomes.CRATER_OUTER.location());
        }
        return null;
    }

    private void gatherChunks() {
        Set<Long> chunks = new LinkedHashSet<>();
        Set<Long> outerChunks = new LinkedHashSet<>();
        int outerRange = getScale();
        int adjustedMaxAngle = 20 * outerRange / 32;

        for (int angle = 0; angle <= adjustedMaxAngle; angle++) {
            Vec3 vector = new Vec3(outerRange, 0, 0);
            vector = vector.yRot((float) (angle * Math.PI / 180.0 / (adjustedMaxAngle / 360.0)));
            outerChunks.add(ChunkPos.asLong((int) (getX() + vector.x) >> 4, (int) (getZ() + vector.z) >> 4));
        }

        for (int distance = 0; distance <= outerRange; distance += 8) {
            for (int angle = 0; angle <= adjustedMaxAngle; angle++) {
                Vec3 vector = new Vec3(distance, 0, 0);
                vector = vector.yRot((float) (angle * Math.PI / 180.0 / (adjustedMaxAngle / 360.0)));
                long chunkCoord = ChunkPos.asLong((int) (getX() + vector.x) >> 4, (int) (getZ() + vector.z) >> 4);
                if (!outerChunks.contains(chunkCoord)) {
                    chunks.add(chunkCoord);
                }
            }
        }

        chunksToProcess.addAll(chunks);
        outerChunksToProcess.addAll(outerChunks);
        Collections.reverse(chunksToProcess);
        Collections.reverse(outerChunksToProcess);
    }

    private void stomp(int x, int z, double dist) {
        int depth = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 255, z);

        for (int y = 255; y >= 0; y--) {
            pos.setY(y);
            if (depth >= 3) return;

            BlockState state = level().getBlockState(pos);
            Block block = state.getBlock();

            if (state.isAir() || block == ModBlocks.FALLOUT.get()) continue;

            if (block == ModBlocks.VOLCANO_CORE.get()) {
                level().setBlock(pos, ModBlocks.VOLCANO_RAD_CORE.get().defaultBlockState(), 3);
                continue;
            }

            BlockState aboveState = level().getBlockState(pos.above());

            if (depth == 0 && block != ModBlocks.FALLOUT.get() && (aboveState.isAir() || aboveState.canBeReplaced())) {
                double d = dist / 100;
                double chance = 0.1 - Math.pow((d - 0.7) * 1.0, 2);

                if (chance >= random.nextDouble() && ModBlocks.FALLOUT.get().defaultBlockState().canSurvive(level(), pos.above())) {
                    level().setBlock(pos.above(), ModBlocks.FALLOUT.get().defaultBlockState(), 3);
                }
            }

            if (dist < 65 && state.isFlammable(level(), pos, Direction.UP)) {
                if (random.nextInt(5) == 0 && level().getBlockState(pos.above()).isAir()) {
                    level().setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), 3);
                }
            }

            boolean eval = false;

            for (FalloutEntry entry : FalloutConfigJSON.entries) {
                if (entry.eval(level(), x, y, z, state, dist)) {
                    if (entry.isSolid()) {
                        depth++;
                    }
                    eval = true;
                    break;
                }
            }

            float hardness = state.getDestroySpeed(level(), pos);
            BlockState stoneBrickState = Blocks.STONE_BRICKS.defaultBlockState();

            if (y > 0 && dist < 65 && hardness <= stoneBrickState.getDestroySpeed(level(), pos) && hardness >= 0) {
                BlockState belowState = level().getBlockState(pos.below());
                if (belowState.isAir()) {
                    for (int i = 0; i <= depth; i++) {
                        BlockPos targetPos = pos.offset(0, i, 0);
                        BlockState targetState = level().getBlockState(targetPos);
                        float targetHardness = targetState.getDestroySpeed(level(), targetPos);

                        if (targetHardness <= stoneBrickState.getDestroySpeed(level(), targetPos) && targetHardness >= 0) {
                            EntityFallingBlockNT falling = new EntityFallingBlockNT(
                                    ModEntities.FALLING_BLOCK_NT.get(),
                                    level(),
                                    targetPos.getX() + 0.5D,
                                    targetPos.getY() + 0.5D,
                                    targetPos.getZ() + 0.5D,
                                    targetState
                            );
                            falling.canDrop = false;
                            level().addFreshEntity(falling);
                        }
                    }
                }
            }

            if (!eval && state.canOcclude()) {
                depth++;
            }
        }
    }

    public void setScale(int scale) {
        this.entityData.set(DATA_SCALE, scale);
    }

    public int getScale() {
        int scale = this.entityData.get(DATA_SCALE);
        return scale == 0 ? 1 : scale;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setScale(tag.getInt("scale"));
        chunksToProcess.addAll(readChunksFromArray(tag.getLongArray("chunks")));
        outerChunksToProcess.addAll(readChunksFromArray(tag.getLongArray("outerChunks")));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("scale", getScale());
        tag.putLongArray("chunks", chunksToProcess);
        tag.putLongArray("outerChunks", outerChunksToProcess);
    }

    private Collection<Long> readChunksFromArray(long[] data) {
        List<Long> list = new ArrayList<>();
        Collections.addAll(list, Arrays.stream(data).boxed().toArray(Long[]::new));
        return list;
    }
}