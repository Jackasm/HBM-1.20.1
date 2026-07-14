package com.hbm.entity.logic;

import com.hbm.config.MobConfig;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.entity.mob.glyphid.EntityGlyphidNuclear;
import com.hbm.entity.mob.glyphid.EntityGlyphidScout;
import com.hbm.main.MainRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static com.hbm.entity.mob.glyphid.EntityGlyphid.*;

public class EntityWaypoint extends Entity {

    private static final EntityDataAccessor<Integer> WAYPOINT_TYPE = SynchedEntityData.defineId(EntityWaypoint.class, EntityDataSerializers.INT);

    public int maxAge = 2400;
    public int radius = 3;
    public boolean highPriority = false;
    protected EntityWaypoint additional;
    private boolean hasSpawned = false;

    public EntityWaypoint(EntityType<?> type, Level world) {
        super(type, world);
        this.blocksBuilding = true;
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(WAYPOINT_TYPE, 0);
    }

    public void setHighPriority() {
        highPriority = true;
    }

    public int getWaypointType() {
        return this.entityData.get(WAYPOINT_TYPE);
    }

    public void setWaypointType(int waypointType) {
        this.entityData.set(WAYPOINT_TYPE, waypointType);
    }

    public void setAdditionalWaypoint(EntityWaypoint waypoint) {
        additional = waypoint;
    }

    public int getColor() {
        return switch (getWaypointType()) {
            case TASK_RETREAT_FOR_REINFORCEMENTS -> 0x5FA6E8;
            case TASK_BUILD_HIVE, TASK_INITIATE_RETREAT -> 0x127766;
            default -> 0x566573;
        };
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount >= maxAge) {
            this.discard();
        }

        AABB bb = this.getBoundingBox().inflate(radius);

        if (!level().isClientSide) {

            if (tickCount % 40 == 0) {

                List<Entity> targets = level().getEntities(this, bb);

                for (Entity e : targets) {
                    if (e instanceof EntityGlyphid bug) {

                        if (additional != null && !hasSpawned) {
                            level().addFreshEntity(additional);
                            hasSpawned = true;
                        }

                        boolean exceptions = bug.getWaypoint() != this ||
                                e instanceof EntityGlyphidScout ||
                                e instanceof EntityGlyphidNuclear;

                        if (!exceptions) {
                            bug.setCurrentTask(getWaypointType(), additional);
                        }

                        if (getWaypointType() == TASK_BUILD_HIVE) {
                            if (e instanceof EntityGlyphidScout) {
                                this.discard();
                            }
                        } else {
                            this.discard();
                        }
                    }
                }
            }
        } else if (MobConfig.waypointDebug) {
            double x = bb.minX + (random.nextDouble() - 0.5) * (bb.maxX - bb.minX);
            double y = bb.minY + random.nextDouble() * (bb.maxY - bb.minY);
            double z = bb.minZ + (random.nextDouble() - 0.5) * (bb.maxZ - bb.minZ);

            CompoundTag data = new CompoundTag();
            data.putString("type", "tower");
            data.putFloat("lift", 0.5F);
            data.putFloat("base", 0.75F);
            data.putFloat("max", 2F);
            data.putInt("life", 50 + level().random.nextInt(10));
            data.putInt("color", getColor());
            data.putDouble("posX", x);
            data.putDouble("posY", y);
            data.putDouble("posZ", z);

            // Отправляем на клиент или вызываем напрямую на клиенте
            if (level().isClientSide) {
                MainRegistry.proxy.effectNT(data);
            } else {
                PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data, x, y, z), level(), BlockPos.containing(x, y, z), 50);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.setWaypointType(nbt.getInt("type"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("type", getWaypointType());
    }
}