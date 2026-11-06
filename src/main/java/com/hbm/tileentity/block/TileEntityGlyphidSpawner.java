package com.hbm.tileentity.block;

import com.hbm.config.MobConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.glyphid.*;
import com.hbm.handler.pollution.PollutionHandler;

import com.hbm.handler.pollution.PollutionType;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class TileEntityGlyphidSpawner extends BlockEntity {

    private boolean initialSpawn = true;
    public int type = 0; // 0 = STANDARD, 1 = INFESTED, 2 = RAD

    public TileEntityGlyphidSpawner(BlockPos pos, BlockState state) {
        super(ModTileEntity.GLYPHID_SPAWNER.get(), pos, state);
    }

    public void tick() {
        Level level = this.level;
        if (level == null || level.isClientSide) return;
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;

        long time = level.getGameTime();
        if (!initialSpawn && time % MobConfig.swarmCooldown != 0) return;

        // Глобальная проверка на максимум
        if (level instanceof ServerLevel serverLevel) {
            int count = 0;
            for (Entity e : serverLevel.getAllEntities()) {
                if (e instanceof EntityGlyphid) {
                    count++;
                    if (count >= MobConfig.spawnMax) return;
                }
            }
        }

        AABB box = new AABB(
                worldPosition.getX() - 5, worldPosition.getY() + 1, worldPosition.getZ() - 5,
                worldPosition.getX() + 6, worldPosition.getY() + 7, worldPosition.getZ() + 6
        );
        List<EntityGlyphid> list = level.getEntitiesOfClass(EntityGlyphid.class, box);
        float soot = PollutionHandler.getPollution(level, worldPosition, PollutionType.SOOT);

        int subtype = this.type;

        if (list.size() <= 3 || subtype == EntityGlyphid.TYPE_RADIOACTIVE) {
            boolean wasInitial = initialSpawn;
            if (initialSpawn) {
                initialSpawn = false;
                setChanged();
            }

            for (EntityGlyphid glyphid : createSwarm(soot, subtype, level)) {
                trySpawnEntity(glyphid, level);
            }

            if (!wasInitial
                    && level.random.nextInt(MobConfig.scoutSwarmSpawnChance + 1) == 0
                    && soot >= MobConfig.scoutThreshold
                    && subtype != EntityGlyphid.TYPE_RADIOACTIVE) {
                EntityGlyphidScout scout = ModEntities.GLYPHID_SCOUT.get().create(level);
                if (scout != null) {
                    if (subtype == 1) scout.setSubtype(EntityGlyphid.TYPE_INFECTED);
                    trySpawnEntity(scout, level);
                }
            }
        }
    }

    private void trySpawnEntity(EntityGlyphid glyphid, Level level) {
        double offsetX = glyphid.getRandom().nextGaussian() * 3;
        double offsetZ = glyphid.getRandom().nextGaussian() * 3;

        for (int i = 0; i < 7; i++) {
            glyphid.moveTo(
                    worldPosition.getX() + 0.5 + offsetX,
                    worldPosition.getY() - 2 + i,
                    worldPosition.getZ() + 0.5 + offsetZ,
                    level.random.nextFloat() * 360.0F,
                    0.0F
            );
            if (glyphid.canSpawnHere(level)) {
                level.addFreshEntity(glyphid);
                return;
            }
        }
    }

    private ArrayList<EntityGlyphid> createSwarm(float soot, int meta, Level level) {
        Random rand = new Random();
        ArrayList<EntityGlyphid> spawns = new ArrayList<>();
        int swarmAmount = (int) Math.min(MobConfig.baseSwarmSize * Math.max(MobConfig.swarmScalingMult * (soot / MobConfig.sootStep), 1), 10);
        int cap = 100;

        while (spawns.size() <= swarmAmount && cap >= 0) {
            for (var entry : SPAWN_MAP) {
                int[] chance = entry.getValue();
                int adjusted = (int) (chance[0] + (chance[1] - chance[1] / Math.max((soot + 1) / 3, 1)));
                if (soot >= chance[2] && rand.nextInt(100) <= adjusted) {
                    EntityGlyphid entity = entry.getKey().get().create(level);
                    if (entity == null) continue;
                    if (meta == 1) entity.setSubtype(EntityGlyphid.TYPE_INFECTED);
                    if (meta == 2) entity.setSubtype(EntityGlyphid.TYPE_RADIOACTIVE);
                    spawns.add(entity);
                }
            }
            cap--;
        }
        return spawns;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("initialSpawn", initialSpawn);
        tag.putInt("type", type);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.initialSpawn = tag.getBoolean("initialSpawn");
        this.type = tag.getInt("type");
    }

    private static <T extends EntityGlyphid> Map.Entry<Supplier<EntityType<? extends EntityGlyphid>>, int[]> entry(
            RegistryObject<EntityType<T>> ro, int[] chance) {
        return Map.entry(ro::get, chance);
    }

    // === spawn map ===

    private static final List<Map.Entry<Supplier<EntityType<? extends EntityGlyphid>>, int[]>> SPAWN_MAP = new ArrayList<>();

    static {
        SPAWN_MAP.add(entry(ModEntities.GLYPHID,            MobConfig.glyphidChance));
        SPAWN_MAP.add(entry(ModEntities.GLYPHID_BOMBARDIER, MobConfig.bombardierChance));
        SPAWN_MAP.add(entry(ModEntities.GLYPHID_BRAWLER,    MobConfig.brawlerChance));
        SPAWN_MAP.add(entry(ModEntities.GLYPHID_DIGGER,     MobConfig.diggerChance));
        SPAWN_MAP.add(entry(ModEntities.GLYPHID_BLASTER,    MobConfig.blasterChance));
        SPAWN_MAP.add(entry(ModEntities.GLYPHID_BEHEMOTH,   MobConfig.behemothChance));
        SPAWN_MAP.add(entry(ModEntities.GLYPHID_BRENDA,     MobConfig.brendaChance));
        SPAWN_MAP.add(entry(ModEntities.GLYPHID_NUCLEAR,    MobConfig.johnsonChance));
    }
}