package com.hbm.handler;

import com.hbm.blocks.ModBlocks;
import com.hbm.saveddata.TomSaveData;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ImpactWorldHandler {

    public static void impactEffects(Level level) {

        if (!(level instanceof ServerLevel serverLevel)) return;

        if (level.dimension() != Level.OVERWORLD) return;

        TomSaveData data = TomSaveData.forWorld(level);

        if (Objects.requireNonNull(data).dust <= 0 && data.fire <= 0) return;

        List<LevelChunk> chunks = new ArrayList<>();

        try {
            // Получаем доступ к полю visibleChunkMap
            Field visibleChunksField = serverLevel.getChunkSource().chunkMap.getClass().getDeclaredField("visibleChunkMap");
            visibleChunksField.setAccessible(true);
            Object visibleChunks = visibleChunksField.get(serverLevel.getChunkSource().chunkMap);

            if (visibleChunks instanceof Long2ObjectLinkedOpenHashMap<?> map) {
                for (Object value : map.values()) {
                    if (value instanceof ChunkHolder holder) {
                        // Пробуем получить чанк из футуры
                        Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> either = holder.getFullChunkFuture().getNow(null);
                        if (either != null) {
                            Optional<LevelChunk> opt = either.left();
                            opt.ifPresent(chunks::add);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Fallback: используем простой подход с центром мира
            ChunkPos center = new ChunkPos(serverLevel.getSharedSpawnPos());
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    ChunkPos pos = new ChunkPos(center.x + dx, center.z + dz);
                    if (serverLevel.hasChunk(pos.x, pos.z)) {
                        chunks.add(serverLevel.getChunk(pos.x, pos.z));
                    }
                }
            }
        }

        int listSize = chunks.size();

        if (listSize > 0) {
            for (int i = 0; i < 3; i++) {

                LevelChunk chunk = chunks.get(serverLevel.random.nextInt(listSize));

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {

                        if (level.random.nextBoolean()) continue;

                        int X = chunk.getPos().getMinBlockX() + x;
                        int Z = chunk.getPos().getMinBlockZ() + z;
                        int Y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, X, Z) - level.random.nextInt(Math.max(1, level.getHeight(Heightmap.Types.MOTION_BLOCKING, X, Z)));

                        if (data.dust > 0) {
                            die(level, X, Y, Z);
                        }
                        if (data.fire > 0) {
                            burn(level, X, Y, Z);
                        }
                    }
                }
            }
        }
    }

    /// Plants die without sufficient light.
    public static void die(Level level, int x, int y, int z) {

        TomSaveData data = TomSaveData.forWorld(level);
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos above = pos.above();

        int light = Math.max(level.getBrightness(LightLayer.BLOCK, above),
                (int) (level.getBrightness(LightLayer.SKY, above) * (1 - Objects.requireNonNull(data).dust)));

        if (light < 4) {
            Block b = level.getBlockState(pos).getBlock();
            if (b == Blocks.GRASS_BLOCK) {
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            } else if (b instanceof BushBlock) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            } else if (b instanceof LeavesBlock) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            } else if (b instanceof VineBlock) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    /// Burn the world.
    public static void burn(Level level, int x, int y, int z) {

        BlockPos pos = new BlockPos(x, y, z);
        BlockPos above = pos.above();
        Block b = level.getBlockState(pos).getBlock();

        if (b.defaultBlockState().ignitedByLava() && level.getBlockState(above).isAir() && level.getBrightness(LightLayer.SKY, above) >= 7) {
            if (b instanceof LeavesBlock || b instanceof BushBlock) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
            level.setBlock(above, Blocks.FIRE.defaultBlockState(), 3);

        } else if ((b == Blocks.GRASS_BLOCK || b == Blocks.MYCELIUM || b == ModBlocks.WASTE_EARTH.get() ||
                b == ModBlocks.FROZEN_GRASS.get() || b == ModBlocks.WASTE_MYCELIUM.get()) &&
                !level.canSeeSky(pos) && level.getBrightness(LightLayer.SKY, above) >= 7) {
            level.setBlock(pos, ModBlocks.BURNING_EARTH.get().defaultBlockState(), 3);

        } else if (b == ModBlocks.FROZEN_DIRT.get() && level.getBrightness(LightLayer.SKY, above) >= 7) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        }
    }

    public static Level lastSyncLevel = null;
    public static float fire = 0F;
    public static float dust = 0F;
    public static boolean impact = false;

    @OnlyIn(Dist.CLIENT)
    public static float getFireForClient(Level level) {
        if (level != lastSyncLevel) return 0F;
        return fire;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getDustForClient(Level level) {
        if (level != lastSyncLevel) return 0F;
        return dust;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean getImpactForClient(Level level) {
        if (level != lastSyncLevel) return false;
        return impact;
    }
}