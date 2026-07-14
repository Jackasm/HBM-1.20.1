package com.hbm.entity.missile;


import com.hbm.api.entity.IRadarDetectableNT;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockMutatorFire;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.explosion.vanillant.standard.EntityProcessorCross;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.items.weapon.ItemMissile;

import com.hbm.main.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.RefStrings.MODID;

public abstract class EntityMissileBaseNT extends ThrowableProjectile implements IChunkLoader, IRadarDetectableNT {

    public int startX;
    public int startZ;
    public int targetX;
    public int targetZ;
    public double velocity;
    public double decelY;
    public double accelXZ;
    public boolean isCluster = false;
    public int health = 50;

    public static final EntityDataAccessor<Byte> ROTATION = SynchedEntityData.defineId(EntityMissileBaseNT.class, EntityDataSerializers.BYTE);

    private String modId = "";
    private boolean chunksForced = false;
    private final List<ChunkPos> forcedChunks = new ArrayList<>();

    public EntityMissileBaseNT(EntityType<? extends EntityMissileBaseNT> entityType, Level level) {
        super(entityType, level);
        startX = (int) this.getX();
        startZ = (int) this.getZ();
        targetX = (int) this.getX();
        targetZ = (int) this.getZ();
    }

    public EntityMissileBaseNT(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileBaseNT> entityType) {
        this(entityType, level);
        this.setPos(x, y, z);
        startX = (int) x;
        startZ = (int) z;
        this.targetX = targetX;
        this.targetZ = targetZ;
        this.setDeltaMovement(0, 2, 0);

        Vec3 vector = new Vec3(targetX - startX, 0, targetZ - startZ);
        accelXZ = decelY = 1 / vector.length();
        decelY *= 2;
        velocity = 0;

        this.setYRot((float) (Math.atan2(targetX - this.getX(), targetZ - this.getZ()) * 180.0D / Math.PI));
        this.setSize(1.5F, 1.5F);
    }

    public abstract ItemStack getMissileItemForInfo();

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
        return !params.smartMode || !(this.getDeltaMovement().y >= 0);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HEALTH, (byte) 5);
        this.entityData.define(ROTATION, (byte) 0);
    }

    private static final EntityDataAccessor<Byte> HEALTH =
            SynchedEntityData.defineId(EntityMissileBaseNT.class,
                    EntityDataSerializers.BYTE);

    @Override
    public void tick() {
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();

        if (velocity < 4) velocity += MathHelper.clamp(this.tickCount / 60D * 0.05D, 0, 0.05);

        if (!level().isClientSide) {
            // --- СЕРВЕРНАЯ ЛОГИКА ---
            if (hasPropulsion()) {
                Vec3 delta = this.getDeltaMovement();
                double newY = delta.y - decelY * velocity;

                Vec3 vector = new Vec3(targetX - this.getX(), 0, targetZ - this.getZ());
                vector = vector.normalize();
                vector = vector.scale(accelXZ);

                delta = delta.add(vector.x * velocity, 0, vector.z * velocity);
                this.setDeltaMovement(delta.x, newY, delta.z);
            } else {
                Vec3 delta = this.getDeltaMovement();
                this.setDeltaMovement(delta.x * 0.99, delta.y, delta.z * 0.99);
                if (delta.y > -1.5) {
                    this.setDeltaMovement(delta.x, delta.y - 0.05, delta.z);
                }
            }

            if (this.getDeltaMovement().y < -velocity && this.isCluster) {
                cluster();
                this.discard();
                return;
            }

            this.setYRot((float) (Math.atan2(targetX - this.getX(), targetZ - this.getZ()) * 180.0D / Math.PI));
            float f2 = (float) Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z);
            this.setXRot((float) (Math.atan2(this.getDeltaMovement().y, f2) * 180.0D / Math.PI) - 90);

            // === ПРОВЕРКА СТОЛКНОВЕНИЯ С БЛОКАМИ (рейкаст) ===
            Vec3 start = new Vec3(this.getX(), this.getY(), this.getZ());
            Vec3 end = new Vec3(
                    this.getX() + this.getDeltaMovement().x,
                    this.getY() + this.getDeltaMovement().y,
                    this.getZ() + this.getDeltaMovement().z
            );

            net.minecraft.world.level.ClipContext context = new net.minecraft.world.level.ClipContext(
                    start, end,
                    net.minecraft.world.level.ClipContext.Block.COLLIDER,
                    net.minecraft.world.level.ClipContext.Fluid.NONE,
                    this
            );

            BlockHitResult hitResult = this.level().clip(context);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // Ракета врезалась в блок!
                this.onMissileImpact(hitResult);
                this.discard();
                return;
            }

            // ==== ОБНОВЛЯЕМ ПОЗИЦИЮ ====
            this.setPos(
                    this.getX() + this.getDeltaMovement().x,
                    this.getY() + this.getDeltaMovement().y,
                    this.getZ() + this.getDeltaMovement().z
            );

            if (level() instanceof ServerLevel serverLevel) {
                ChunkPos currentChunk = new ChunkPos(BlockPos.containing(this.getX(), this.getY(), this.getZ()));
                loadNeighboringChunks(serverLevel, currentChunk, MODID);
            }
        } else {
            // --- КЛИЕНТСКАЯ ЛОГИКА ---
            super.tick(); // интерполяция
            this.spawnContrail();
        }
    }

    public boolean hasPropulsion() {
        return true;
    }

    protected void spawnContrail() {
        this.spawnContrailWithOffset(0, 0, 0);
    }

    public void setRotation(byte rot) {
        this.entityData.set(ROTATION, rot);
    }

    protected void spawnContrailWithOffset(double offsetX, double offsetY, double offsetZ) {
        Vec3 posVec = new Vec3(this.xOld - this.getX(), this.yOld - this.getY(), this.zOld - this.getZ());
        double len = posVec.length();
        posVec = posVec.normalize();
        Vec3 thrust = new Vec3(0, 1, 0);
        // Поворот сложен, в 1.20.1 используем другой подход
        thrust = thrust.xRot(this.getXRot() * (float) Math.PI / 180F);
        thrust = thrust.yRot((this.getYRot() + 90) * (float) Math.PI / 180F);

        for (int i = 0; i < Math.max(Math.min(len, 10), 1); i++) {
            double j = i - len;
            CompoundTag data = new CompoundTag();
            data.putDouble("posX", this.getX() - posVec.x * j + offsetX);
            data.putDouble("posY", this.getY() - posVec.y * j + offsetY);
            data.putDouble("posZ", this.getZ() - posVec.z * j + offsetZ);
            data.putString("type", "missileContrail");
            data.putFloat("scale", this.getContrailScale());
            data.putDouble("moX", -thrust.x);
            data.putDouble("moY", -thrust.y);
            data.putDouble("moZ", -thrust.z);
            data.putInt("maxAge", 60 + random.nextInt(20));
            MainRegistry.proxy.effectNT(data);
        }
    }

    protected float getContrailScale() {
        return 1F;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setDeltaMovement(nbt.getDouble("moX"), nbt.getDouble("moY"), nbt.getDouble("moZ"));
        this.setPos(nbt.getDouble("poX"), nbt.getDouble("poY"), nbt.getDouble("poZ"));
        decelY = nbt.getDouble("decel");
        accelXZ = nbt.getDouble("accel");
        targetX = nbt.getInt("tX");
        targetZ = nbt.getInt("tZ");
        startX = nbt.getInt("sX");
        startZ = nbt.getInt("sZ");
        velocity = nbt.getDouble("veloc");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putDouble("moX", this.getDeltaMovement().x);
        nbt.putDouble("moY", this.getDeltaMovement().y);
        nbt.putDouble("moZ", this.getDeltaMovement().z);
        nbt.putDouble("poX", this.getX());
        nbt.putDouble("poY", this.getY());
        nbt.putDouble("poZ", this.getZ());
        nbt.putDouble("decel", decelY);
        nbt.putDouble("accel", accelXZ);
        nbt.putInt("tX", targetX);
        nbt.putInt("tZ", targetZ);
        nbt.putInt("sX", startX);
        nbt.putInt("sZ", startZ);
        nbt.putDouble("veloc", velocity);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (this.health > 0 && !this.level().isClientSide) {
                health -= (int) amount;
                if (this.health <= 0) {
                    this.killMissile();
                }
            }
            return true;
        }
    }

    protected void killMissile() {
        if (!this.isRemoved()) {
            this.discard();
            ExplosionLarge.explode(level(), this.getX(), this.getY(), this.getZ(), 5, true, false, true);
            ExplosionLarge.spawnShrapnelShower(level(), this.getX(), this.getY(), this.getZ(),
                    this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z, 15, 0.075);
            ExplosionLarge.spawnMissileDebris(level(), this.getX(), this.getY(), this.getZ(),
                    this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z, 0.25, getDebris(), getDebrisRareDrop());
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.BLOCK) {
            this.onMissileImpact(result);
            this.discard();
        }
    }

    public abstract void onMissileImpact(HitResult mop);
    public abstract List<ItemStack> getDebris();
    public abstract ItemStack getDebrisRareDrop();
    public void cluster() { }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void init(ServerLevel level, String modId) {
        this.modId = modId;
        if (!level.isClientSide && !chunksForced) {
            ChunkPos chunkPos = level.getChunkAt(BlockPos.containing(this.getX(), this.getY(), this.getZ())).getPos();
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

    List<ChunkPos> loadedChunks = new ArrayList<>();

    @Override
    public void loadNeighboringChunks(ServerLevel level, ChunkPos centerChunk, String modId) {
        if (!level.isClientSide) {
            for (ChunkPos chunk : forcedChunks) {
                ForgeChunkManager.forceChunk(level, modId, this, chunk.x, chunk.z, false, false);
            }
            forcedChunks.clear();

            forcedChunks.add(centerChunk);
            ForgeChunkManager.forceChunk(level, modId, this, centerChunk.x, centerChunk.z, true, false);
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.clearChunkLoader();
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

    public void explodeStandard(float strength, int resolution, boolean fire) {
        ExplosionVNT xnt = new ExplosionVNT(level(), this.getX(), this.getY(), this.getZ(), strength, null);
        xnt.setBlockAllocator(new BlockAllocatorStandard(resolution));
        xnt.setBlockProcessor(new BlockProcessorStandard().setNoDrop().withBlockEffect(fire ? new BlockMutatorFire(Blocks.FIRE) : null));
        xnt.setEntityProcessor(new EntityProcessorCross(7.5D).withRangeMod(2));
        xnt.setPlayerProcessor(new PlayerProcessorStandard());
        xnt.explode();
    }

    @Override
    public String getUnlocalizedName() {
        ItemStack item = this.getMissileItemForInfo();
        if (item != null && item.getItem() instanceof ItemMissile missile) {
            return switch (missile.tier) {
                case TIER0 -> "radar.target.tier0";
                case TIER1 -> "radar.target.tier1";
                case TIER2 -> "radar.target.tier2";
                case TIER3 -> "radar.target.tier3";
                case TIER4 -> "radar.target.tier4";
            };
        }
        return "Unknown";
    }

    @Override
    public int getBlipLevel() {
        ItemStack item = this.getMissileItemForInfo();
        if (item != null && item.getItem() instanceof ItemMissile missile) {
            return switch (missile.tier) {
                case TIER0 -> IRadarDetectableNT.TIER0;
                case TIER1 -> IRadarDetectableNT.TIER1;
                case TIER2 -> IRadarDetectableNT.TIER2;
                case TIER3 -> IRadarDetectableNT.TIER3;
                case TIER4 -> IRadarDetectableNT.TIER4;
            };
        }
        return IRadarDetectableNT.SPECIAL;
    }

    public void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }

    private static class MathHelper {
        public static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }
    }
}