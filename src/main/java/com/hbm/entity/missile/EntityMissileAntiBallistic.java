package com.hbm.entity.missile;

import com.hbm.api.entity.IRadarDetectableNT;
import com.hbm.entity.ModEntities;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.network.PacketDispatcher;
import com.hbm.tileentity.machine.TileEntityMachineRadarNT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.RefStrings.MODID;

public class EntityMissileAntiBallistic extends ThrowableProjectile implements IChunkLoader, IRadarDetectableNT {

    public Entity tracking;
    public double velocity;
    protected int activationTimer;
    public static double baseSpeed = 1.5D;

    private String modId = "";
    private boolean chunksForced = false;
    private final List<ChunkPos> forcedChunks = new ArrayList<>();

    public EntityMissileAntiBallistic(EntityType<? extends EntityMissileAntiBallistic> entityType, Level level) {
        super(entityType, level);
        this.setSize(1.5F, 1.5F);
        this.setDeltaMovement(0, baseSpeed, 0);
    }

    public EntityMissileAntiBallistic(Level level) {
        this(ModEntities.MISSILE_ANTI_BALLISTIC.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        // Добавьте свои синхронизируемые данные, если нужно
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (velocity < 6) velocity += 0.1;

            if (activationTimer < 40) {
                activationTimer++;
                setDeltaMovement(getDeltaMovement().x, baseSpeed, getDeltaMovement().z);
            } else {
                Entity prevTracking = this.tracking;

                if (this.tracking == null || !this.tracking.isAlive()) {
                    this.targetMissile();
                }

                if (prevTracking == null && this.tracking != null) {
                    ExplosionLarge.spawnShock(level(), getX(), getY(), getZ(), 24, 3F);
                }
                if (this.tracking != null && this.tracking.isAlive()) {
                    this.aimAtTarget();
                } else {
                    if (this.tickCount > 600) this.discard();
                }
            }

            if (level() instanceof ServerLevel serverLevel) {
                ChunkPos chunkPos = new ChunkPos(BlockPos.containing(getX(), getY(), getZ()));
                loadNeighboringChunks(serverLevel, chunkPos, MODID);
            }

            if (getY() > 2000 && (this.tracking == null || !this.tracking.isAlive())) {
                this.discard();
            }

        } else {
            Vec3 vec = new Vec3(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z).normalize();
            CompoundTag data = new CompoundTag();
            data.putString("type", "ABMContrail");
            data.putDouble("posX", getX() - vec.x);
            data.putDouble("posY", getY() - vec.y);
            data.putDouble("posZ", getZ() - vec.z);

            PacketDispatcher.sendAuxParticleNT(data, getX() - vec.x, getY() - vec.y, getZ() - vec.z, null);
        }

        float f2 = (float) Math.sqrt(getDeltaMovement().x * getDeltaMovement().x + getDeltaMovement().z * getDeltaMovement().z);
        this.setYRot((float) (Math.atan2(getDeltaMovement().x, getDeltaMovement().z) * 180.0D / Math.PI));
        this.setXRot((float) (Math.atan2(getDeltaMovement().y, f2) * 180.0D / Math.PI) - 90);
    }

    protected void targetMissile() {
        Entity closest = null;
        double dist = 1000;

        for (Entity e : TileEntityMachineRadarNT.matchingEntities) {
            if (e.level().dimension() != this.level().dimension()) continue;
            if (!(e instanceof EntityMissileBaseNT)) continue;
            if (e instanceof EntityMissileStealth) continue;

            Vec3 vec = new Vec3(e.getX() - getX(), e.getY() - getY(), e.getZ() - getZ());

            if (vec.length() < dist) {
                closest = e;
                dist = vec.length();
            }
        }

        this.tracking = closest;
    }

    protected void aimAtTarget() {
        Vec3 delta = new Vec3(tracking.getX() - getX(), tracking.getY() - getY(), tracking.getZ() - getZ());
        double intercept = delta.length() / (baseSpeed * this.velocity);
        Vec3 predicted = new Vec3(
                tracking.getX() + (tracking.getX() - tracking.xOld) * intercept,
                tracking.getY() + (tracking.getY() - tracking.yOld) * intercept,
                tracking.getZ() + (tracking.getZ() - tracking.zOld) * intercept
        );
        Vec3 motion = new Vec3(predicted.x - getX(), predicted.y - getY(), predicted.z - getZ()).normalize();

        if (delta.length() < 10 && activationTimer >= 40) {
            this.discard();
            ExplosionLarge.explode(level(), getX(), getY(), getZ(), 15F, true, false, false);
            return;
        }

        this.setDeltaMovement(motion.x * baseSpeed, motion.y * baseSpeed, motion.z * baseSpeed);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (activationTimer >= 40) {
            this.discard();
            ExplosionLarge.explode(level(), getX(), getY(), getZ(), 20F, true, false, false);
        }
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.velocity = nbt.getDouble("veloc");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putDouble("veloc", this.velocity);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void init(ServerLevel level, String modId) {
        this.modId = modId;
        if (!level.isClientSide && !chunksForced) {
            ChunkPos chunkPos = new ChunkPos(BlockPos.containing(getX(), getY(), getZ()));
            forceChunk(level, chunkPos, true, false);
            chunksForced = true;
        }
    }

    protected void forceChunk(ServerLevel level, ChunkPos chunkPos, boolean add, boolean ticking) {
        if (add) {
            forcedChunks.add(chunkPos);
            ForgeChunkManager.forceChunk(level, modId, this, chunkPos.x, chunkPos.z, true, ticking);
        } else {
            forcedChunks.remove(chunkPos);
            ForgeChunkManager.forceChunk(level, modId, this, chunkPos.x, chunkPos.z, false, ticking);
        }
    }

    @Override
    public void loadNeighboringChunks(ServerLevel level, ChunkPos centerChunk, String modId) {
        if (!level.isClientSide) {
            for (ChunkPos chunk : forcedChunks) {
                ForgeChunkManager.forceChunk(level, modId, this, chunk.x, chunk.z, false, false);
            }
            forcedChunks.clear();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    ChunkPos chunk = new ChunkPos(centerChunk.x + i, centerChunk.z + j);
                    forcedChunks.add(chunk);
                    ForgeChunkManager.forceChunk(level, modId, this, chunk.x, chunk.z, true, false);
                }
            }
        }
    }

    @Override
    public void clearChunkLoader() {
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            for (ChunkPos chunk : forcedChunks) {
                ForgeChunkManager.forceChunk(serverLevel, modId, this, chunk.x, chunk.z, false, false);
            }
            forcedChunks.clear();
            chunksForced = false;
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.clearChunkLoader();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public String getUnlocalizedName() {
        return "radar.target.abm";
    }

    @Override
    public int getBlipLevel() {
        return IRadarDetectableNT.TIER_AB;
    }

    @Override
    public boolean canBeSeenBy(Object radar) {
        return true;
    }

    @Override
    public boolean paramsApplicable(RadarScanParams params) {
        return params.scanMissiles;
    }

    @Override
    public boolean suppliesRedstone(RadarScanParams params) {
        return false;
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }
}