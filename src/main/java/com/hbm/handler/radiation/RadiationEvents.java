package com.hbm.handler.radiation;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.RadiationConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.*;

public class RadiationEvents {

    // «Мёртвая земля». Эталон (1.7.10) каждый тик брал 5 случайных радиоактивных
    // чанков и в каждом перебирал 10 итераций по 16x16 позиций — до 12 800 проверок
    // за тик на чанк, вдали от игроков. Счётчик там выглядит багом перебора, а не
    // задумкой, поэтому копируем не число, а бюджет: предсказуемая цена и эффект,
    // который видно. Текущий бюджет: 5 чанков x 64 позиции раз в 5 тиков = 64 проверки
    // за тик — в ~200 раз дешевле эталона и в ~37 раз плотнее прежнего порта.
    // Закреплено в ADR-0004 как осознанно более мягкий режим.
    private static final float DESTRUCTION_THRESHOLD = 10F;
    private static final int DESTRUCTION_COUNT = 64;
    private static final int DESTRUCTION_CHUNKS_TO_SELECT = 5;
    private static final int DESTRUCTION_INTERVAL_TICKS = 5;

    private int tickCounter = 0;

    // ========== API ==========

    public static float getRadiation(Level level, BlockPos pos) {
        return getRadiation(level, pos.getX(), pos.getY(), pos.getZ());
    }

    public static float getRadiation(Level level, int x, int y, int z) {
        if (!RadiationConfig.ENABLE_CHUNK_RADS || level.isClientSide) return 0;
        IRadiationData data = RadiationCapability.getChunkData(level, new ChunkPos(x >> 4, z >> 4));
        return data == null ? 0 : data.getRadiation(x, y, z);
    }

    public static void setRadiation(Level level, BlockPos pos, float rad) {
        setRadiation(level, pos.getX(), pos.getY(), pos.getZ(), rad);
    }

    public static void setRadiation(Level level, int x, int y, int z, float rad) {
        if (!RadiationConfig.ENABLE_CHUNK_RADS || level.isClientSide) return;
        IRadiationData data = RadiationCapability.getChunkData(level, new ChunkPos(x >> 4, z >> 4));
        if (data != null) {
            data.addRadiation(x, y, z, rad - data.getRadiation(x, y, z));
        }
    }

    public static void incrementRadiation(Level level, BlockPos pos, float rad) {
        incrementRadiation(level, pos.getX(), pos.getY(), pos.getZ(), rad);
    }

    public static void incrementRadiation(Level level, int x, int y, int z, float rad) {
        if (!RadiationConfig.ENABLE_CHUNK_RADS || level.isClientSide) return;
        ChunkPos pos = new ChunkPos(x >> 4, z >> 4);
        IRadiationData data = RadiationCapability.getChunkData(level, pos);
        if (data != null) {
            data.addRadiation(x, y, z, rad);
            RadiationTicker.markRadiationArrived(level, pos);
        }
    }

    public static void decrementRadiation(Level level, BlockPos pos, float rad) {
        incrementRadiation(level, pos, -rad);
    }

    public static void decrementRadiation(Level level, int x, int y, int z, float rad) {
        incrementRadiation(level, x, y, z, -rad);
    }

    // ========== Тик сервера ==========

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != TickEvent.Phase.END) return;
        if (!RadiationConfig.ENABLE_CHUNK_RADS) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            RadiationTicker.tick(level);
        }

        tickCounter++;
        if (tickCounter >= DESTRUCTION_INTERVAL_TICKS) {
            tickCounter = 0;
            if (RadiationConfig.WORLD_RAD_EFFECTS) {
                handleWorldDestruction(event.getServer().getAllLevels());
            }
        }
    }

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide) {
            RadiationTicker.clear(level);
        }
    }

    // ========== Изменение блоков ==========

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof Level level) {
            invalidate(level, event.getPos());
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof Level level) {
            invalidate(level, event.getPos());
        }
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        invalidateAll(event.getLevel(), event.getAffectedBlocks());
    }

    /** Блок изменился — сбрасываем кеш непрозрачности ячейки. */
    public static void invalidate(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        IRadiationData data = RadiationCapability.getChunkData(
                level, new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
        if (data != null) {
            data.invalidateCell(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    /**
     * То же для пачки блоков (взрыв): данные чанка берутся один раз на чанк,
     * а не на каждый блок — иначе ядерный взрыв даёт тысячи одинаковых запросов.
     */
    public static void invalidateAll(Level level, Iterable<BlockPos> positions) {
        if (level.isClientSide) return;

        Map<Long, IRadiationData> byChunk = new HashMap<>();
        for (BlockPos pos : positions) {
            int cx = pos.getX() >> 4;
            int cz = pos.getZ() >> 4;
            long key = ChunkPos.asLong(cx, cz);

            IRadiationData data = byChunk.get(key);
            if (data == null && !byChunk.containsKey(key)) {
                data = RadiationCapability.getChunkData(level, new ChunkPos(cx, cz));
                byChunk.put(key, data);
            }
            if (data != null) {
                data.invalidateCell(pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    private void handleWorldDestruction(Iterable<ServerLevel> levels) {
        for (ServerLevel level : levels) {
            // Эталон брал случайные чанки из карты радиоактивных чанков по всему миру
            // (ChunkRadiationHandlerSimple.handleWorldDestruction: 5 случайных записей из
            // list.radiation), а не соседей игрока. Порт сканировал чанки вокруг игрока —
            // и мёртвая земля появлялась ровно там, куда он прилетал, а вдали трава
            // оставалась живой до первого визита. Берём то же множество, что тикает
            // симуляция радиации, и выбираем из него случайные чанки.
            List<ChunkPos> hotChunks = RadiationTicker.hotChunks(level);
            if (hotChunks.isEmpty()) continue;

            Collections.shuffle(hotChunks, new Random(level.random.nextLong()));
            int chunksToProcess = Math.min(hotChunks.size(), DESTRUCTION_CHUNKS_TO_SELECT);

            for (int c = 0; c < chunksToProcess; c++) {
                ChunkPos chunkPos = hotChunks.get(c);
                if (!level.hasChunk(chunkPos.x, chunkPos.z)) continue;

                IRadiationData chunkData = RadiationCapability.getChunkData(level, chunkPos);
                if (chunkData == null || chunkData.maxRadiation() < DESTRUCTION_THRESHOLD) continue;

                for (int i = 0; i < DESTRUCTION_COUNT; i++) {
                    int x = chunkPos.getMiddleBlockX() - 8 + level.random.nextInt(16);
                    int z = chunkPos.getMiddleBlockZ() - 8 + level.random.nextInt(16);
                    // Раньше здесь стоял шанс 1/3 — он резал бюджет втрое молча.
                    // Бюджет теперь задаётся числом попыток, а не вероятностью.
                    int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - level.random.nextInt(2);
                    BlockPos pos = new BlockPos(x, y, z);

                    // Фон в самой точке, а не максимум по чанку. В 1.7.10 радиация была
                    // одной цифрой на чанк, поэтому руда глубоко под землёй «фонила» всей
                    // поверхности. Здесь поле трёхмерное (PRISM), и порог имеет смысл
                    // проверять там, где трава: иначе вымирал любой чанк с рудой.
                    if (getRadiation(level, pos) < DESTRUCTION_THRESHOLD) continue;

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
