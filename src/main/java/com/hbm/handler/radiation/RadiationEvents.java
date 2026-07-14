package com.hbm.handler.radiation;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.RadiationConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.*;

public class RadiationEvents {
    private static final int GRID_SIZE = 4;
    private static final int CHUNK_SIZE = 16;
    private static final int GRID_CELL_SIZE = CHUNK_SIZE / GRID_SIZE;

    private static final float DESTRUCTION_THRESHOLD = 10F;
    private static final int DESTRUCTION_COUNT = 10;
    private static final int DESTRUCTION_CHUNKS_TO_SELECT = 5;

    private int tickCounter = 0;

    // ========== Утилиты ==========
    private static int[] getGridIndices(BlockPos pos) {
        int xInChunk = pos.getX() & 15;
        int zInChunk = pos.getZ() & 15;
        int gx = xInChunk / GRID_CELL_SIZE;
        int gz = zInChunk / GRID_CELL_SIZE;
        int gy = Mth.clamp(pos.getY() / 16, 0, 15) / (16 / GRID_SIZE);
        return new int[]{gx, gy, gz};
    }

    public static float getRadiation(Level level, BlockPos pos) {
        if (!RadiationConfig.enableChunkRads.get() || level.isClientSide) return 0;
        ChunkPos cp = new ChunkPos(pos);
        IRadiationData data = RadiationCapability.getChunkData(level, cp);
        if (data != null) {
            int[] idx = getGridIndices(pos);
            return data.getRadiation(idx[0], idx[1], idx[2]);
        }
        return 0;
    }

    public static void setRadiation(Level level, BlockPos pos, float rad) {
        if (!RadiationConfig.enableChunkRads.get() || level.isClientSide) return;
        ChunkPos cp = new ChunkPos(pos);
        IRadiationData data = RadiationCapability.getChunkData(level, cp);
        if (data != null) {
            int[] idx = getGridIndices(pos);
            float current = data.getRadiation(idx[0], idx[1], idx[2]);
            data.addRadiationAt(idx[0], idx[1], idx[2], rad - current);
        }
    }

    public static void incrementRadiation(Level level, BlockPos pos, float rad) {
        if (!RadiationConfig.enableChunkRads.get() || level.isClientSide) return;
        ChunkPos cp = new ChunkPos(pos);
        IRadiationData data = RadiationCapability.getChunkData(level, cp);
        if (data != null) {
            int[] idx = getGridIndices(pos);
            data.addRadiationAt(idx[0], idx[1], idx[2], rad);
        }
    }

    public static void decrementRadiation(Level level, BlockPos pos, float rad) {
        incrementRadiation(level, pos, -rad);
    }

    // Перегрузки с int координатами
    public static float getRadiation(Level level, int x, int y, int z) {
        return getRadiation(level, new BlockPos(x, y, z));
    }
    public static void setRadiation(Level level, int x, int y, int z, float rad) {
        setRadiation(level, new BlockPos(x, y, z), rad);
    }
    public static void incrementRadiation(Level level, int x, int y, int z, float rad) {
        incrementRadiation(level, new BlockPos(x, y, z), rad);
    }
    public static void decrementRadiation(Level level, int x, int y, int z, float rad) {
        decrementRadiation(level, new BlockPos(x, y, z), rad);
    }

    // ========== Тик сервера ==========
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != TickEvent.Phase.END) return;
        if (!RadiationConfig.enableChunkRads.get()) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            Set<ChunkPos> activeChunks = new HashSet<>();
            for (ServerPlayer player : level.players()) {
                ChunkPos playerChunk = new ChunkPos(player.blockPosition());
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        ChunkPos cp = new ChunkPos(playerChunk.x + dx, playerChunk.z + dz);
                        if (level.hasChunk(cp.x, cp.z) && level.isPositionEntityTicking(cp.getWorldPosition())) {
                            activeChunks.add(cp);
                        }
                    }
                }
            }

            for (ChunkPos cp : activeChunks) {
                IRadiationData data = RadiationCapability.getChunkData(level, cp);
                if (data != null) {
                    data.tick(level, cp);
                }
            }
        }

        tickCounter++;
        if (tickCounter >= 10) {
            tickCounter = 0;
            if (RadiationConfig.worldRadEffects.get()) {
                handleWorldDestruction(event.getServer().getAllLevels());
            }
        }
    }

    private void handleWorldDestruction(Iterable<ServerLevel> levels) {
        for (ServerLevel level : levels) {
            List<ChunkPos> highRadChunks = new ArrayList<>();
            for (ServerPlayer player : level.players()) {
                ChunkPos playerChunk = new ChunkPos(player.blockPosition());
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        ChunkPos cp = new ChunkPos(playerChunk.x + dx, playerChunk.z + dz);
                        if (!level.hasChunk(cp.x, cp.z)) continue;
                        if (!level.isPositionEntityTicking(cp.getWorldPosition())) continue;

                        IRadiationData data = RadiationCapability.getChunkData(level, cp);
                        if (data != null) {
                            boolean high = false;
                            outer: for (int x = 0; x < 4; x++)
                                for (int y = 0; y < 4; y++)
                                    for (int z = 0; z < 4; z++)
                                        if (data.getRadiation(x, y, z) >= DESTRUCTION_THRESHOLD) {
                                            high = true;
                                            break outer;
                                        }
                            if (high) highRadChunks.add(cp);
                        }
                    }
                }
            }

            if (highRadChunks.isEmpty()) continue;

            Collections.shuffle(highRadChunks);
            int chunksToProcess = Math.min(highRadChunks.size(), DESTRUCTION_CHUNKS_TO_SELECT);

            for (int c = 0; c < chunksToProcess; c++) {
                ChunkPos chunkPos = highRadChunks.get(c);
                for (int i = 0; i < DESTRUCTION_COUNT; i++) {
                    int x = chunkPos.getMiddleBlockX() - 8 + level.random.nextInt(16);
                    int z = chunkPos.getMiddleBlockZ() - 8 + level.random.nextInt(16);
                    if (level.random.nextInt(3) != 0) continue;
                    int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - level.random.nextInt(2);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.GRASS_BLOCK)) {
                        level.setBlock(pos, ModBlocks.WASTE_EARTH.get().defaultBlockState(), 3);
                    } else if (state.is(Blocks.GRASS)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (state.is(BlockTags.LEAVES) && !state.is(ModBlocks.WASTE_LEAVES.get())) {
                        if (level.random.nextInt(7) <= 5) {
                            level.setBlock(pos, ModBlocks.WASTE_LEAVES.get().defaultBlockState(), 3);
                        } else {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
}