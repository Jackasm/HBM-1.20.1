package com.hbm.handler.pollution;

import com.hbm.config.MobConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.entity.mob.glyphid.EntityGlyphidDigger;
import com.hbm.entity.mob.glyphid.EntityGlyphidScout;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.*;

public class PollutionHandler {
    public static Vec3 targetCoords;

    private static final int DESTRUCTION_CHUNKS_PER_TICK = 2;
    private static final float DESTRUCTION_THRESHOLD = 15F;
    private static final int DESTRUCTION_COUNT = 5;

    private int destructionIndex = 0;

    /** Baserate of soot generation for a furnace-equivalent machine per second */
    public static final float SOOT_PER_SECOND = 1F / 25F;
    /** Baserate of heavy metal generation, balanced around the soot values of combustion engines */
    public static final float HEAVY_METAL_PER_SECOND = 1F / 50F;
    /** Baserate for poison when spilled */
    public static final float POISON_PER_SECOND = 1F / 50F;

    public static final UUID MAX_HEALTH_MODIFIER = UUID.fromString("25462f6c-2cb2-4ca8-9b47-3a011cc61207");
    public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("8f442d7c-d03f-49f6-a040-249ae742eed9");

    // ========== Утилиты ==========
    public static void incrementPollution(Level level, BlockPos pos, PollutionType type, float amount) {
        if (!RadiationConfig.enablePollution.get() || level.isClientSide) return;
        ChunkPos cp = new ChunkPos(pos);
        IPollutionData data = PollutionCapability.getChunkData(level, cp);
        if (data != null) {
            data.addPollution(type, amount * (float) MobConfig.pollutionMult);
        }
    }

    public static void decrementPollution(Level level, BlockPos pos, PollutionType type, float amount) {
        incrementPollution(level, pos, type, -amount);
    }

    public static void setPollution(Level level, BlockPos pos, PollutionType type, float amount) {
        if (!RadiationConfig.enablePollution.get() || level.isClientSide) return;
        ChunkPos cp = new ChunkPos(pos);
        IPollutionData data = PollutionCapability.getChunkData(level, cp);
        if (data != null) {
            data.setPollution(type, amount);
        }
    }

    public static float getPollution(Level level, BlockPos pos, PollutionType type) {
        if (!RadiationConfig.enablePollution.get() || level.isClientSide) return 0;
        ChunkPos cp = new ChunkPos(pos);
        IPollutionData data = PollutionCapability.getChunkData(level, cp);
        return data != null ? data.getPollution(type) : 0;
    }

    public static IPollutionData getPollutionData(Level level, BlockPos pos) {
        if (!RadiationConfig.enablePollution.get() || level.isClientSide) return null;
        ChunkPos cp = new ChunkPos(pos);
        return PollutionCapability.getChunkData(level, cp);
    }

    // ========== Тик сервера – ленивое обновление чанков вокруг игроков ==========
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != TickEvent.Phase.END) return;

        // Разрушение мира (распределённое)
        handleWorldDestruction(event.getServer().getAllLevels());

        // Обновление загрязнения в чанках, где есть игроки
        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (!RadiationConfig.enablePollution.get()) continue;

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
                IPollutionData data = PollutionCapability.getChunkData(level, cp);
                if (data != null) {
                    data.tick(level, cp);
                }
            }
        }
    }

    private void handleWorldDestruction(Iterable<ServerLevel> levels) {
        for (ServerLevel level : levels) {
            if (!RadiationConfig.enablePollution.get()) continue;

            // Собираем все загруженные чанки с высоким poison
            List<ChunkPos> highPoisonChunks = new ArrayList<>();

            for (ServerPlayer player : level.players()) {
                ChunkPos playerChunk = new ChunkPos(player.blockPosition());
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        ChunkPos cp = new ChunkPos(playerChunk.x + dx, playerChunk.z + dz);
                        if (!level.hasChunk(cp.x, cp.z)) continue;
                        if (!level.isPositionEntityTicking(cp.getWorldPosition())) continue;

                        IPollutionData data = PollutionCapability.getChunkData(level, cp);
                        if (data != null && data.getPollution(PollutionType.POISON) >= DESTRUCTION_THRESHOLD) {
                            highPoisonChunks.add(cp);
                        }
                    }
                }
            }

            if (highPoisonChunks.isEmpty()) continue;

            // Обрабатываем ограниченное количество чанков за тик
            int start = destructionIndex;
            int end = Math.min(highPoisonChunks.size(), start + DESTRUCTION_CHUNKS_PER_TICK);

            ChunkSource chunkSource = level.getChunkSource();
            RandomSource random = level.random;

            for (int i = start; i < end; i++) {
                ChunkPos chunkPos = highPoisonChunks.get(i);

                for (int j = 0; j < DESTRUCTION_COUNT; j++) {
                    int x = (chunkPos.x << 6) + random.nextInt(64);
                    int z = (chunkPos.z << 6) + random.nextInt(64);

                    if (chunkSource.hasChunk(x >> 4, z >> 4)) {
                        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - random.nextInt(3) + 1;
                        BlockPos targetPos = new BlockPos(x, y, z);
                        BlockState state = level.getBlockState(targetPos);
                        Block block = state.getBlock();

                        if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT) {
                            level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                        } else if (block == Blocks.TALL_GRASS || block == Blocks.GRASS || state.isAir() || state.canBeReplaced()) {
                            level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }

            // Обновляем индекс для следующего тика
            destructionIndex = (end >= highPoisonChunks.size()) ? 0 : end;
        }
    }

    // ========== Мобы и эффекты ==========
    @SubscribeEvent
    public void decorateMob(MobSpawnEvent.FinalizeSpawn event) {
        if (!RadiationConfig.enablePollution.get()) return;
        LevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor == null || levelAccessor.isClientSide()) return;
        if (!(levelAccessor instanceof Level level)) return;
        if (level.isClientSide) return;

        LivingEntity entity = event.getEntity();
        IPollutionData data = getPollutionData(level, BlockPos.containing(event.getX(), event.getY(), event.getZ()));
        if (data == null) return;

        if (entity instanceof Mob && !(entity instanceof EntityGlyphid)) {
            float soot = data.getPollution(PollutionType.SOOT);
            if (soot > RadiationConfig.buffMobThreshold.get()) {
                AttributeInstance maxHealthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealthAttr != null && maxHealthAttr.getModifier(MAX_HEALTH_MODIFIER) == null) {
                    maxHealthAttr.addTransientModifier(new AttributeModifier(MAX_HEALTH_MODIFIER, "Soot Anger Health Increase", 1.0D, AttributeModifier.Operation.ADDITION));
                }
                AttributeInstance attackAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attackAttr != null && attackAttr.getModifier(ATTACK_DAMAGE_MODIFIER) == null) {
                    attackAttr.addTransientModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Soot Anger Damage Increase", 1.5D, AttributeModifier.Operation.ADDITION));
                }
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    /// RAMPANT MODE STUFFS ///
    @SubscribeEvent
    public void rampantTargetSetter(PlayerSleepInBedEvent event) {
        if (MobConfig.rampantGlyphidGuidance && event.getEntity() != null) {
            targetCoords = new Vec3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
        }
    }

    @SubscribeEvent
    public void rampantScoutPopulator(LevelEvent.PotentialSpawns event) {
        if (!MobConfig.rampantNaturalScoutSpawn) return;
        LevelAccessor level = event.getLevel();
        if (level.isClientSide()) return;

        BlockPos pos = event.getPos();
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (serverLevel.dimension() != Level.OVERWORLD) return;
        if (!level.canSeeSky(pos)) return;

        if (level.getRandom().nextInt(MobConfig.rampantScoutSpawnChance) == 0) {
            float soot = getPollution(serverLevel, pos, PollutionType.SOOT);
            if (soot >= MobConfig.rampantScoutSpawnThresh) {
                EntityGlyphidScout scout = new EntityGlyphidScout(ModEntities.GLYPHID_SCOUT.get(), serverLevel);
                scout.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.getRandom().nextFloat() * 360.0F, 0.0F);
                if (scout.checkSpawnRules(serverLevel, MobSpawnType.NATURAL) && scout.checkSpawnObstruction(serverLevel)) {
                    EntityGlyphidDigger digger = new EntityGlyphidDigger(ModEntities.GLYPHID_DIGGER.get(), serverLevel);
                    digger.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.getRandom().nextFloat() * 360.0F, 0.0F);
                    serverLevel.addFreshEntity(scout);
                    serverLevel.addFreshEntity(digger);
                }
            }
        }
    }
}