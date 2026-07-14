package com.hbm.world.teleporter;

import com.hbm.network.PacketHandler;
import com.hbm.network.client.TeleportLoadingPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeleportationService {

    // Ограничиваем диапазон поиска разумными пределами
    private static final int MAX_SEARCH_RADIUS = 50000; // 50,000 блоков
    private static final int SEARCH_GRID_SIZE = 1000;   // шаг поиска 1000 блоков
    private static final int MAX_ATTEMPTS = 200;        // максимальное количество попыток

    public static void teleportArea(Level level, BlockPos center, ServerPlayer player, int radius, boolean captureEntities, ResourceKey<Biome> targetBiome) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // Показываем экран загрузки
        PacketHandler.sendToPlayer(new TeleportLoadingPacket(true), player);

        // Запускаем асинхронную операцию
        CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Сканирование области
                AreaSnapshot snapshot = new AreaSnapshot();
                snapshot.capture(serverLevel, center, radius, captureEntities);

                // 2. Поиск целевого места
                BlockPos targetCenter = findTarget(serverLevel, targetBiome, radius, serverLevel.getRandom());
                if (targetCenter == null) {
                    throw new RuntimeException("No suitable location found");
                }

                // 3. Создание воронки на старом месте
                createCrater(serverLevel, center, radius);

                return new TeleportResult(snapshot, targetCenter);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).thenAccept(result -> serverLevel.getServer().execute(() -> {
            if (result != null) {
                // 4. Очистка области назначения
                result.snapshot.clearArea(serverLevel, result.targetCenter, radius);

                // 5. Вставка области
                result.snapshot.paste(serverLevel, result.targetCenter);

                // 6. Телепортация игрока
                player.teleportTo(serverLevel, result.targetCenter.getX() + 0.5, result.targetCenter.getY() + 0.5, result.targetCenter.getZ() + 0.5, player.getYRot(), player.getXRot());
            } else {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable("hbm.teleport.failed"), true);
            }
            // Скрываем экран загрузки
            PacketHandler.sendToPlayer(new TeleportLoadingPacket(false), player);
        }));
    }

    private static BlockPos findTarget(ServerLevel level, ResourceKey<Biome> targetBiome, int radius, RandomSource random) {
        if (targetBiome == null) {
            targetBiome = getRandom(random);
        }

        // Сначала пытаемся найти место в пределах разумного диапазона
        BlockPos spawnPos = level.getSharedSpawnPos();
        int spawnX = spawnPos.getX();
        int spawnZ = spawnPos.getZ();

        // Используем поиск по сетке в пределах MAX_SEARCH_RADIUS от спавна
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            // Генерируем координаты в пределах разумного диапазона от спавна
            int offsetX = random.nextInt(MAX_SEARCH_RADIUS * 2) - MAX_SEARCH_RADIUS;
            int offsetZ = random.nextInt(MAX_SEARCH_RADIUS * 2) - MAX_SEARCH_RADIUS;
            int x = spawnX + offsetX;
            int z = spawnZ + offsetZ;

            // Округляем до ближайшей координаты, кратной SEARCH_GRID_SIZE
            x = Math.round(x / (float) SEARCH_GRID_SIZE) * SEARCH_GRID_SIZE;
            z = Math.round(z / (float) SEARCH_GRID_SIZE) * SEARCH_GRID_SIZE;

            // Добавляем случайное смещение для разнообразия
            x += random.nextInt(SEARCH_GRID_SIZE) - SEARCH_GRID_SIZE / 2;
            z += random.nextInt(SEARCH_GRID_SIZE) - SEARCH_GRID_SIZE / 2;

            try {
                // Получаем высоту через ChunkGenerator
                int y = level.getChunkSource().getGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE, level, level.getChunkSource().randomState());

                // Проверяем, что высота в разумных пределах
                if (y < 60 || y > 100) continue;

                BlockPos candidate = new BlockPos(x, y, z);

                // Проверяем, что чанк загружен и биом корректен
                if (!level.hasChunk(candidate.getX() >> 4, candidate.getZ() >> 4)) {
                    continue;
                }

                Biome biome = level.getBiome(candidate).value();
                ResourceKey<Biome> biomeKey = level.registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getResourceKey(biome)
                        .orElse(null);

                if (!targetBiome.equals(biomeKey)) continue;

                return candidate;
            } catch (Exception e) {
                // Игнорируем ошибки при попытке получить высоту
                continue;
            }
        }

        // Если не нашли, пытаемся найти место рядом со спавном
        return findNearSpawn(level, targetBiome, radius, random, spawnX, spawnZ);
    }

    private static BlockPos findNearSpawn(ServerLevel level, ResourceKey<Biome> targetBiome, int radius, RandomSource random, int spawnX, int spawnZ) {
        for (int attempt = 0; attempt < 100; attempt++) {
            int x = spawnX + random.nextInt(2000) - 1000;
            int z = spawnZ + random.nextInt(2000) - 1000;

            try {
                int y = level.getChunkSource().getGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE, level, level.getChunkSource().randomState());
                if (y < 60 || y > 100) continue;

                BlockPos candidate = new BlockPos(x, y, z);

                if (!level.hasChunk(candidate.getX() >> 4, candidate.getZ() >> 4)) continue;

                Biome biome = level.getBiome(candidate).value();
                ResourceKey<Biome> biomeKey = level.registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getResourceKey(biome)
                        .orElse(null);

                if (!targetBiome.equals(biomeKey)) continue;

                return candidate;
            } catch (Exception e) {
                continue;
            }
        }

        // Если всё равно не нашли, возвращаем спавн
        return new BlockPos(spawnX, 64, spawnZ);
    }

    public static ResourceKey<Biome> getRandom(RandomSource random) {
        // Список биомов для телепортации
        List<ResourceKey<Biome>> biomes = new ArrayList<>();
        biomes.add(Biomes.PLAINS);
        biomes.add(Biomes.DESERT);
        biomes.add(Biomes.FOREST);
        biomes.add(Biomes.TAIGA);
        biomes.add(Biomes.SWAMP);
        biomes.add(Biomes.JUNGLE);
        biomes.add(Biomes.SAVANNA);
        biomes.add(Biomes.BADLANDS);
        biomes.add(Biomes.WINDSWEPT_HILLS);
        biomes.add(Biomes.BIRCH_FOREST);
        biomes.add(Biomes.DARK_FOREST);
        biomes.add(Biomes.OLD_GROWTH_BIRCH_FOREST);
        biomes.add(Biomes.OLD_GROWTH_PINE_TAIGA);
        biomes.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA);
        biomes.add(Biomes.SNOWY_PLAINS);
        biomes.add(Biomes.SNOWY_TAIGA);
        biomes.add(Biomes.MUSHROOM_FIELDS);
        biomes.add(Biomes.CHERRY_GROVE);
        biomes.add(Biomes.MEADOW);
        biomes.add(Biomes.BEACH);
        biomes.add(Biomes.OCEAN);
        biomes.add(Biomes.RIVER);

        return biomes.get(random.nextInt(biomes.size()));
    }

    private static void createCrater(Level level, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double dist = Math.sqrt(x*x + z*z);
                if (dist > radius) continue;
                int depth = (int)((radius - dist) * 0.5);
                for (int dy = 0; dy <= depth; dy++) {
                    BlockPos pos = center.offset(x, -dy, z);
                    if (level.isInWorldBounds(pos)) {
                        level.removeBlock(pos, false);
                    }
                }
            }
        }
    }

    public static class AreaSnapshot {
        private final List<BlockSnapshot> blocks = new ArrayList<>();
        private final List<EntitySnapshot> entities = new ArrayList<>();
        private BlockPos center;

        public void capture(Level level, BlockPos center, int radius, boolean captureEntities) {
            this.center = center;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x*x + y*y + z*z > radius*radius) continue;
                        BlockPos pos = center.offset(x, y, z);
                        BlockState state = level.getBlockState(pos);
                        if (!state.isAir()) {
                            BlockSnapshot bs = new BlockSnapshot(pos, state);
                            if (state.hasBlockEntity()) {
                                BlockEntity be = level.getBlockEntity(pos);
                                if (be != null) {
                                    CompoundTag nbt = be.saveWithoutMetadata();
                                    bs.setBlockEntityNbt(nbt);
                                }
                            }
                            blocks.add(bs);
                        }
                    }
                }
            }
            if (captureEntities) {
                AABB aabb = new AABB(center).inflate(radius);
                List<Entity> list = level.getEntitiesOfClass(Entity.class, aabb);
                for (Entity e : list) {
                    if (e instanceof net.minecraft.world.entity.player.Player) continue;
                    EntitySnapshot es = new EntitySnapshot(e, center);
                    entities.add(es);
                }
            }
        }

        public void paste(Level level, BlockPos targetCenter) {
            for (BlockSnapshot bs : blocks) {
                BlockPos newPos = targetCenter.offset(bs.pos.getX() - center.getX(),
                        bs.pos.getY() - center.getY(),
                        bs.pos.getZ() - center.getZ());
                level.setBlock(newPos, bs.state, 3);
                if (bs.blockEntityNbt != null) {
                    BlockEntity be = level.getBlockEntity(newPos);
                    if (be != null) {
                        CompoundTag nbt = bs.blockEntityNbt.copy();
                        nbt.putInt("x", newPos.getX());
                        nbt.putInt("y", newPos.getY());
                        nbt.putInt("z", newPos.getZ());
                        be.load(nbt);
                    }
                }
            }
            for (EntitySnapshot es : entities) {
                es.spawn(level, targetCenter);
            }
        }

        public void clearArea(Level level, BlockPos center, int radius) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x*x + y*y + z*z > radius*radius) continue;
                        BlockPos pos = center.offset(x, y, z);
                        level.removeBlock(pos, false);
                    }
                }
            }
        }
    }

    private static class BlockSnapshot {
        private final BlockPos pos;
        private final BlockState state;
        private CompoundTag blockEntityNbt;

        BlockSnapshot(BlockPos pos, BlockState state) {
            this.pos = pos;
            this.state = state;
        }

        void setBlockEntityNbt(CompoundTag nbt) { this.blockEntityNbt = nbt; }
    }

    private static class EntitySnapshot {
        private final CompoundTag data;
        private final EntityType<?> type;
        private final double offsetX, offsetY, offsetZ;

        EntitySnapshot(Entity entity, BlockPos center) {
            this.data = entity.saveWithoutId(new CompoundTag());
            this.type = entity.getType();
            this.offsetX = entity.getX() - center.getX();
            this.offsetY = entity.getY() - center.getY();
            this.offsetZ = entity.getZ() - center.getZ();
        }

        void spawn(Level level, BlockPos center) {
            Entity entity = type.create(level);
            if (entity != null) {
                double x = center.getX() + offsetX;
                double y = center.getY() + offsetY;
                double z = center.getZ() + offsetZ;
                entity.setPos(x, y, z);
                entity.load(data);
                level.addFreshEntity(entity);
            }
        }
    }

    public static class TeleportResult {
        final AreaSnapshot snapshot;
        final BlockPos targetCenter;

        TeleportResult(AreaSnapshot snapshot, BlockPos targetCenter) {
            this.snapshot = snapshot;
            this.targetCenter = targetCenter;
        }
    }
}