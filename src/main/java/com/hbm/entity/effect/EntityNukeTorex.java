package com.hbm.entity.effect;

import com.hbm.entity.ModEntities;
import com.hbm.main.MainRegistry;
import com.hbm.sound.ModSounds;
import com.hbm.sound.SoundHelper;
import com.hbm.util.BobMathUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class EntityNukeTorex extends Entity implements IEntityAdditionalSpawnData {

    public double coreHeight = 3;
    public double convectionHeight = 3;
    public double torusWidth = 3;
    public double rollerSize = 1;
    public double heat = 1;
    public double lastSpawnY = -1;
    public ArrayList<Cloudlet> cloudlets = new ArrayList<>();

    public boolean didPlaySound = false;
    public boolean didShake = false;

    // Server game time at creation, shared with every client that starts tracking this entity.
    // A new client must not interpret its local Entity.tickCount as the explosion's age.
    private static final EntityDataAccessor<Long> START_GAME_TIME =
            SynchedEntityData.defineId(EntityNukeTorex.class, EntityDataSerializers.LONG);
    // MaxAge is at most 4500 ticks for ordinary statFac scales (0.5..5).
    // Scan birth times, NOT all historical particles or movement steps.
    private static final int RECONNECT_HISTORY_SCAN_LIMIT = 5000;
    private static final int RECONNECT_CLOUD_LIMIT = 2800;
    private boolean clientPhaseInitialized;
    // Age sampled on the server when THIS tracking/spawn packet was constructed.
    // Unlike world-age-on-arrival, this distinguishes a late tracking client from
    // a fresh observer whose packet was delayed by the network.
    private int trackingSpawnAge = -1;
    private int recoveredCloudletCount;

    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(EntityNukeTorex.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityNukeTorex.class, EntityDataSerializers.INT);

    public EntityNukeTorex(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SCALE, 1.0F);
        this.entityData.define(TYPE, 0);
        this.entityData.define(START_GAME_TIME, -1L);
    }

    @Override
    public void tick() {
        if (level().isClientSide) {
            long start = getStartGameTime();
            // Wait for the entity-data snapshot before displaying a false first frame.
            if (start < 0L || trackingSpawnAge < 0) return;
            if (!clientPhaseInitialized) {
                clientPhaseInitialized = true;
                long elapsed = Math.max(0L, level().getGameTime() - start);
                int visualAge = (int) Math.min(elapsed, (long) getMaxAge());
                if (isLateTrackingClient() && visualAge > 1) {
                    restoreClientPhase(visualAge - 1);
                } else if (visualAge > 1) {
                    // A freshly spawned explosion can arrive >10 ticks late due
                    // to network jitter, but it must not run the heavy replay.
                    alignClientGeometry(visualAge - 1);
                }
                if (isLateTrackingClient()) {
                    // Even an 11-tick-old reconnect must not replay the initial
                    // sound or camera shock. Use packet-time age, not age-1.
                    didPlaySound = true;
                    didShake = true;
                }
            }
        } else if (getStartGameTime() < 0L) {
            // Covers entities created directly by tests, not only statFac().
            entityData.set(START_GAME_TIME, level().getGameTime() - tickCount);
        }

        // Minecraft 1.20.1 increments Entity.tickCount in Entity.baseTick().
        super.tick();

        // 1.7.10 TorEx keeps simulation geometry separate from the synced entity scale.
        double s = 1.5D;
        double cs = 1.5;
        int maxAge = this.getMaxAge();

        if (level().isClientSide) {

            if (tickCount == 1) this.setScale((float) s);

            if (lastSpawnY == -1) {
                lastSpawnY = getY() - 3;
            }

            if (tickCount < 100 && this.level() instanceof ClientLevel clientLevel) {
                clientLevel.setSkyFlashTime(10);
            }

            int spawnTarget = Math.max(level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                    BlockPos.containing(getX(), 0, getZ())).getY() - 3, 1);
            double moveSpeed = 0.5D;

            if (Math.abs(spawnTarget - lastSpawnY) < moveSpeed) {
                lastSpawnY = spawnTarget;
            } else {
                lastSpawnY += moveSpeed * Math.signum(spawnTarget - lastSpawnY);
            }

            // spawn mush clouds
            double range = (torusWidth - rollerSize) * 0.25;
            double simSpeed = getSimulationSpeed();
            int toSpawn = (int) Math.ceil(10 * simSpeed * simSpeed);
            int lifetime = Math.min((tickCount * tickCount) + 200, maxAge - tickCount + 200);

            for (int i = 0; i < toSpawn; i++) {
                double x = getX() + random.nextGaussian() * range;
                double z = getZ() + random.nextGaussian() * range;
                Cloudlet cloud = new Cloudlet(x, lastSpawnY, z, (float) (random.nextDouble() * 2D * Math.PI), 0, lifetime);
                cloud.setScale(1F + this.tickCount * 0.005F * (float) cs, 5F * (float) cs);
                cloudlets.add(cloud);
            }

            // spawn shock clouds
            if (tickCount < 150) {

                int cloudCount = tickCount * 5;
                int shockLife = Math.max(300 - tickCount * 20, 50);

                for (int i = 0; i < cloudCount; i++) {
                    Vec3 vec = new Vec3((tickCount * 1.5 + random.nextDouble()) * 1.5, 0, 0);
                    float rot = (float) (Math.PI * 2 * random.nextDouble());
                    vec = vec.yRot(rot);
                    BlockPos pos = BlockPos.containing(vec.x + getX(), 0, vec.z + getZ());
                    int height = level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, pos).getY();
                    this.cloudlets.add(new Cloudlet(vec.x + getX(), height, vec.z + getZ(), rot, 0, shockLife, TorexType.SHOCK)
                            .setScale(7F, 2F)
                            .setMotion(tickCount > 15 ? 0.75 : 0));
                }

                if (!didPlaySound) {
                    if (MainRegistry.proxy.me() != null && MainRegistry.proxy.me().distanceTo(this) < (tickCount * 1.5 + 1) * 1.5) {
                        SoundHelper.playSoundClient(getX(), getY(), getZ(), ModSounds.NUCLEAR_EXPLOSION.get(), 10000F, 1F);
                        didPlaySound = true;
                    }
                }
            }

            // spawn ring clouds
            if (tickCount < 130 * s) {
                lifetime *= s;
                for (int i = 0; i < 2; i++) {
                    Cloudlet cloud = new Cloudlet(getX(), getY() + coreHeight, getZ(), (float) (random.nextDouble() * 2D * Math.PI), 0, lifetime, TorexType.RING);
                    cloud.setScale(1F + this.tickCount * 0.0025F * (float) (cs * cs), 3F * (float) (cs * cs));
                    cloudlets.add(cloud);
                }
            }

            // spawn condensation clouds
            if (tickCount > 130 * s && tickCount < 600 * s) {

                for (int i = 0; i < 20; i++) {
                    for (int j = 0; j < 4; j++) {
                        float angle = (float) (Math.PI * 2 * random.nextDouble());
                        Vec3 vec = new Vec3(torusWidth + rollerSize * (5 + random.nextDouble()), 0, 0);
                        vec = TorexVisualMath.rotateConvectionPlane(vec, (float) (Math.PI / 45 * j));
                        vec = vec.yRot(angle);
                        Cloudlet cloud = new Cloudlet(getX() + vec.x, getY() + coreHeight - 5 + j * s, getZ() + vec.z, angle, 0,
                                (int) ((20 + tickCount / 10) * (1 + random.nextDouble() * 0.1)), TorexType.CONDENSATION);
                        cloud.setScale(0.125F * (float) (cs), 3F * (float) (cs));
                        cloudlets.add(cloud);
                    }
                }
            }
            if (tickCount > 200 * s && tickCount < 600 * s) {

                for (int i = 0; i < 20; i++) {
                    for (int j = 0; j < 4; j++) {
                        float angle = (float) (Math.PI * 2 * random.nextDouble());
                        Vec3 vec = new Vec3(torusWidth + rollerSize * (3 + random.nextDouble() * 0.5), 0, 0);
                        vec = TorexVisualMath.rotateConvectionPlane(vec, (float) (Math.PI / 45 * j));
                        vec = vec.yRot(angle);
                        Cloudlet cloud = new Cloudlet(getX() + vec.x, getY() + coreHeight + 25 + j * cs, getZ() + vec.z, angle, 0,
                                (int) ((20 + tickCount / 10) * (1 + random.nextDouble() * 0.1)), TorexType.CONDENSATION);
                        cloud.setScale(0.125F * (float) (cs), 3F * (float) (cs));
                        cloudlets.add(cloud);
                    }
                }
            }

            cloudlets.removeIf(cloud -> cloud.isDead);
            for (Cloudlet cloud : cloudlets) {
                cloud.update();
            }
            coreHeight += 0.15 / s;
            torusWidth += 0.05 / s;
            rollerSize = torusWidth * 0.35;
            convectionHeight = coreHeight + rollerSize;

            int maxHeat = (int) (50 * cs);
            heat = maxHeat - Math.pow((maxHeat * this.tickCount) / maxAge, 1);
        }

        if (!level().isClientSide && this.tickCount > maxAge) {
            this.discard();
        }
    }


    /** Shared start time makes late-joining clients use the server's current phase. */
    public long getStartGameTime() {
        return this.entityData.get(START_GAME_TIME);
    }

    /** Snapshot age is sampled per tracking packet, not inferred from network latency. */
    public int getTrackingSpawnAge() {
        return trackingSpawnAge;
    }

    public boolean isLateTrackingClient() {
        return level().isClientSide && trackingSpawnAge > 1;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(Math.max(0, tickCount));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        trackingSpawnAge = Math.max(0, buffer.readVarInt());
    }

    /** Observable for client-side regression tests; normal spawns report zero. */
    public int getRecoveredCloudletCount() {
        return recoveredCloudletCount;
    }

    /**
     * Align an entity's visual geometry with its world age even when the spawn packet
     * arrives several ticks late. Does not generate historical cloudlets.
     */
    private void alignClientGeometry(int age) {
        tickCount = age;
        final double s = 1.5D;
        final double cs = 1.5D;
        coreHeight = 3D + age * (0.15D / s);
        torusWidth = 3D + age * (0.05D / s);
        rollerSize = torusWidth * 0.35D;
        convectionHeight = coreHeight + rollerSize;

        int spawnTarget = Math.max(level().getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                BlockPos.containing(getX(), 0, getZ())).getY() - 3, 1);
        double spawnOrigin = getY() - 3D;
        double delta = spawnTarget - spawnOrigin;
        lastSpawnY = spawnOrigin + Math.signum(delta) * Math.min(Math.abs(delta), age * 0.5D);

        int maxAge = getMaxAge();
        int maxHeat = (int) (50 * cs);
        heat = maxHeat - (double) maxHeat * age / maxAge;
    }

    /**
     * Reconstruct a bounded visual snapshot for late-tracking clients.
     * Iterate historical birth TICKS, but instantiate only still-living sampled
     * particles. Never replay all historical clouds or their full movement paths.
     */
    private void restoreClientPhase(int age) {
        alignClientGeometry(age);
        final double s = 1.5D;
        final double cs = 1.5D;
        final int maxAge = getMaxAge();
        // This method is used only for a late tracking snapshot, not a normal
        // initial spawn. Suppression must cover the 10/11 tick boundary too.
        didPlaySound = true;
        didShake = true;

        Random reconstruction = new Random(getUUID().getMostSignificantBits()
                ^ getUUID().getLeastSignificantBits() ^ 0x544f5245584cL);
        int groundY = Math.max(level().getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                BlockPos.containing(getX(), 0, getZ())).getY(), 4);

        // Reserve room for old, long-lived RING cloudlets. Without this pass
        // the smoke quota can saturate before reaching their early birth ticks.
        int oldest = Math.max(1, age - RECONNECT_HISTORY_SCAN_LIMIT);
        int latestRingBirth = Math.min(age, (int) Math.ceil(130 * s) - 1);
        for (int birth = latestRingBirth; birth >= oldest && recoveredCloudletCount < RECONNECT_CLOUD_LIMIT; birth--) {
            int elapsed = age - birth;
            int originalLife = Math.min(birth * birth + 200, maxAge - birth + 200);
            int ringLife = (int) (originalLife * s);
            if (elapsed >= ringLife) continue;
            double birthCore = 3D + birth * (0.15D / s);
            for (int i = 0; i < 2 && recoveredCloudletCount < RECONNECT_CLOUD_LIMIT; i++) {
                Cloudlet cloud = new Cloudlet(getX(), getY() + birthCore,
                        getZ(), (float) (reconstruction.nextDouble() * Math.PI * 2D),
                        0, ringLife, TorexType.RING);
                cloud.setScale(1F + birth * 0.0025F * (float) (cs * cs),
                        3F * (float) (cs * cs));
                addRecoveredCloud(cloud, elapsed);
            }
        }

        for (int birth = age; birth >= oldest && recoveredCloudletCount < RECONNECT_CLOUD_LIMIT; birth--) {
            int elapsed = age - birth;
            double speed = simulationSpeedAt(birth, maxAge);
            double birthCore = 3D + birth * (0.15D / s);
            double birthWidth = 3D + birth * (0.05D / s);
            int originalLife = Math.min(birth * birth + 200, maxAge - birth + 200);

            if (elapsed < originalLife) {
                int smokeCount = (int) Math.ceil(10D * speed * speed);
                double range = birthWidth * (1D - 0.35D) * 0.25D;
                for (int i = 0; i < smokeCount && recoveredCloudletCount < RECONNECT_CLOUD_LIMIT; i++) {
                    double x = getX() + reconstruction.nextGaussian() * range;
                    double z = getZ() + reconstruction.nextGaussian() * range;
                    double y = lastSpawnY + Math.min(coreHeight * 0.8D, elapsed * 0.3D);
                    Cloudlet cloud = new Cloudlet(x, y, z,
                            (float) (reconstruction.nextDouble() * Math.PI * 2D), 0, originalLife);
                    cloud.setScale(1F + birth * 0.005F * (float) cs, 5F * (float) cs);
                    addRecoveredCloud(cloud, elapsed);
                }
            }

            if (birth < 150) {
                int shockLife = Math.max(300 - birth * 20, 50);
                if (elapsed < shockLife) {
                    // The real effect may spawn hundreds per tick; sample the current ring.
                    int count = Math.min(16, birth * 5);
                    for (int i = 0; i < count && recoveredCloudletCount < RECONNECT_CLOUD_LIMIT; i++) {
                        float angle = (float) (reconstruction.nextDouble() * Math.PI * 2D);
                        double distance = birth * 2.25D + reconstruction.nextDouble() * 1.5D;
                        Cloudlet cloud = new Cloudlet(
                                getX() + Math.cos(angle) * distance, groundY,
                                getZ() + Math.sin(angle) * distance, angle, 0, shockLife, TorexType.SHOCK);
                        cloud.setScale(7F, 2F).setMotion(birth > 15 ? 0.75D : 0D);
                        addRecoveredCloud(cloud, elapsed);
                    }
                }
            }

            if (birth > 130 * s && birth < 600 * s) {
                // Cap the dense condensation phase rather than replaying 160 births/tick.
                int count = birth > 200 * s ? 16 : 8;
                for (int i = 0; i < count && recoveredCloudletCount < RECONNECT_CLOUD_LIMIT; i++) {
                    int life = (int) ((20 + birth / 10) * (1D + reconstruction.nextDouble() * 0.1D));
                    if (elapsed >= life) continue;
                    float angle = (float) (reconstruction.nextDouble() * Math.PI * 2D);
                    double radius = birthWidth + birthWidth * 0.35D * (3D + reconstruction.nextDouble() * 3D);
                    double height = getY() + birthCore + (i % 4) * cs;
                    Cloudlet cloud = new Cloudlet(getX() + Math.cos(angle) * radius, height,
                            getZ() + Math.sin(angle) * radius, angle, 0, life, TorexType.CONDENSATION);
                    cloud.setScale(0.125F * (float) cs, 3F * (float) cs);
                    addRecoveredCloud(cloud, elapsed);
                }
            }
        }
    }

    private void addRecoveredCloud(Cloudlet cloud, int elapsed) {
        // A few movement steps give interpolation and colours a valid starting state.
        // Preserve its chronological age without simulating every missed frame.
        if (getSimulationSpeed() <= 0D) {
            // Historical clouds can outlive particle motion. Do not run a
            // synthetic physics step with zero speed: NaN motion * 0 is NaN.
            cloud.age = elapsed;
            cloud.updateColor();
        } else {
            int steps = Math.min(elapsed, 6);
            cloud.age = Math.max(0, elapsed - steps);
            for (int i = 0; i < steps; i++) cloud.update();
        }
        cloud.prevPosX = cloud.posX;
        cloud.prevPosY = cloud.posY;
        cloud.prevPosZ = cloud.posZ;
        cloud.prevColor = cloud.color;
        cloudlets.add(cloud);
        recoveredCloudletCount++;
    }

    private static double simulationSpeedAt(int age, int lifetime) {
        int slow = lifetime / 4;
        int stop = lifetime / 2;
        if (age > stop) return 0D;
        if (age > slow) return 1D - (double) (age - slow) / (stop - slow);
        return 1D;
    }

    public EntityNukeTorex setScale(float scale) {
        if (!level().isClientSide) getEntityData().set(SCALE, scale);
        this.coreHeight = this.coreHeight / 1.5D * scale;
        this.convectionHeight = this.convectionHeight / 1.5D * scale;
        this.torusWidth = this.torusWidth / 1.5D * scale;
        this.rollerSize = this.rollerSize / 1.5D * scale;
        return this;
    }

    public EntityNukeTorex setType(int type) {
        this.entityData.set(TYPE, type);
        return this;
    }

    public double getSimulationSpeed() {
        int lifetime = getMaxAge();
        int simSlow = lifetime / 4;
        int simStop = lifetime / 2;
        int life = this.tickCount;

        if (life > simStop) {
            return 0D;
        }

        if (life > simSlow) {
            return 1D - ((double) (life - simSlow) / (double) (simStop - simSlow));
        }

        return 1.0D;
    }

    public float getScale() {
        return this.entityData.get(SCALE);
    }

    public double getSaturation() {
        double d = (double) this.tickCount / (double) this.getMaxAge();
        return 1D - (d * d * d * d);
    }

    public double getGreying() {
        int lifetime = getMaxAge();
        int greying = lifetime * 3 / 4;

        if (tickCount > greying) {
            return 1 + ((double) (tickCount - greying) / (double) (lifetime - greying));
        }

        return 1D;
    }

    public float getAlpha() {
        int lifetime = getMaxAge();
        int fadeOut = lifetime * 3 / 4;
        int life = this.tickCount;

        if (life > fadeOut) {
            float fac = (float) (life - fadeOut) / (float) (lifetime - fadeOut);
            return 1F - fac;
        }

        return 1.0F;
    }

    public int getMaxAge() {
        double s = this.getScale();
        return (int) (45 * 20 * s);
    }

    public class Cloudlet {

        public double posX;
        public double posY;
        public double posZ;
        public double prevPosX;
        public double prevPosY;
        public double prevPosZ;
        public double motionX;
        public double motionY;
        public double motionZ;
        public int age;
        public int cloudletLife;
        public float angle;
        public boolean isDead = false;
        float rangeMod = 1.0F;
        public float colorMod = 1.0F;
        public Vec3 color;
        public Vec3 prevColor;
        public TorexType type;

        public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge) {
            this(posX, posY, posZ, angle, age, maxAge, TorexType.STANDARD);
        }

        public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge, TorexType type) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.age = age;
            this.cloudletLife = maxAge;
            this.angle = angle;
            this.rangeMod = 0.3F + random.nextFloat() * 0.7F;
            this.colorMod = 0.8F + random.nextFloat() * 0.2F;
            this.type = type;

            this.updateColor();
        }

        private void update() {

            age++;

            if (age > cloudletLife) {
                this.isDead = true;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            Vec3 simPos = new Vec3(EntityNukeTorex.this.getX() - this.posX, 0, EntityNukeTorex.this.getZ() - this.posZ);
            double simPosX = EntityNukeTorex.this.getX() + simPos.length();
            double simPosZ = EntityNukeTorex.this.getZ() + 0D;

            if (this.type == TorexType.STANDARD) {
                Vec3 convection = getConvectionMotion(simPosX, simPosZ);
                Vec3 lift = getLiftMotion(simPosX, simPosZ);

                double factor = Mth.clamp((this.posY - EntityNukeTorex.this.getY()) / EntityNukeTorex.this.coreHeight, 0, 1);
                this.motionX = convection.x * factor + lift.x * (1D - factor);
                this.motionY = convection.y * factor + lift.y * (1D - factor);
                this.motionZ = convection.z * factor + lift.z * (1D - factor);
            } else if (this.type == TorexType.SHOCK) {

                double factor = Mth.clamp((this.posY - EntityNukeTorex.this.getY()) / EntityNukeTorex.this.coreHeight, 0, 1);
                Vec3 motion = new Vec3(1, 0, 0);
                motion = motion.yRot(this.angle);
                this.motionX = motion.x * factor;
                this.motionY = motion.y * factor;
                this.motionZ = motion.z * factor;
            } else if (this.type == TorexType.RING) {
                Vec3 motion = getRingMotion(simPosX, simPosZ);
                this.motionX = motion.x;
                this.motionY = motion.y;
                this.motionZ = motion.z;
            } else if (this.type == TorexType.CONDENSATION) {
                Vec3 motion = getCondensationMotion();
                this.motionX = motion.x;
                this.motionY = motion.y;
                this.motionZ = motion.z;
            }

            double mult = this.motionMult * getSimulationSpeed();

            this.posX += this.motionX * mult;
            this.posY += this.motionY * mult;
            this.posZ += this.motionZ * mult;

            this.updateColor();
        }

        private Vec3 getCondensationMotion() {
            Vec3 delta = new Vec3(posX - EntityNukeTorex.this.getX(), 0, posZ - EntityNukeTorex.this.getZ());
            double speed = 0.00002 * EntityNukeTorex.this.tickCount;
            return delta.scale(speed);
        }

        private Vec3 getRingMotion(double simPosX, double simPosZ) {

            if (simPosX > EntityNukeTorex.this.getX() + torusWidth * 2)
                return Vec3.ZERO;

            Vec3 torusPos = new Vec3(
                    (EntityNukeTorex.this.getX() + torusWidth),
                    (EntityNukeTorex.this.getY() + coreHeight * 0.5),
                    EntityNukeTorex.this.getZ());

            Vec3 delta = new Vec3(torusPos.x - simPosX, torusPos.y - this.posY, torusPos.z - simPosZ);

            double roller = EntityNukeTorex.this.rollerSize * this.rangeMod * 0.25;
            double dist = delta.length() / roller - 1D;

            double func = 1D - Math.pow(Math.E, -dist);
            float angle = (float) (func * Math.PI * 0.5D);

            Vec3 rot = new Vec3(-delta.x / dist, -delta.y / dist, -delta.z / dist);
            rot = TorexVisualMath.rotateConvectionPlane(rot, angle);

            Vec3 motion = new Vec3(
                    torusPos.x + rot.x - simPosX,
                    torusPos.y + rot.y - this.posY,
                    torusPos.z + rot.z - simPosZ);

            double speed = 0.001D;
            motion = motion.scale(speed).normalize();
            motion = motion.yRot(this.angle);

            return motion;
        }

        private Vec3 getConvectionMotion(double simPosX, double simPosZ) {

            Vec3 torusPos = new Vec3(
                    (EntityNukeTorex.this.getX() + torusWidth),
                    (EntityNukeTorex.this.getY() + coreHeight),
                    EntityNukeTorex.this.getZ());

            Vec3 delta = new Vec3(torusPos.x - simPosX, torusPos.y - this.posY, torusPos.z - simPosZ);

            double roller = EntityNukeTorex.this.rollerSize * this.rangeMod;
            double dist = delta.length() / roller - 1D;

            double func = 1D - Math.pow(Math.E, -dist);
            float angle = (float) (func * Math.PI * 0.5D);

            Vec3 rot = new Vec3(-delta.x / dist, -delta.y / dist, -delta.z / dist);
            rot = TorexVisualMath.rotateConvectionPlane(rot, angle);

            Vec3 motion = new Vec3(
                    torusPos.x + rot.x - simPosX,
                    torusPos.y + rot.y - this.posY,
                    torusPos.z + rot.z - simPosZ);

            motion = motion.normalize();
            motion = motion.yRot(this.angle);

            return motion;
        }

        private Vec3 getLiftMotion(double simPosX, double simPosZ) {
            double scale = Mth.clamp(1D - (simPosX - (EntityNukeTorex.this.getX() + torusWidth)), 0, 1);

            Vec3 motion = new Vec3(
                    EntityNukeTorex.this.getX() - this.posX,
                    (EntityNukeTorex.this.getY() + convectionHeight) - this.posY,
                    EntityNukeTorex.this.getZ() - this.posZ);

            motion = motion.normalize().scale(scale);

            return motion;
        }

        private void updateColor() {
            this.prevColor = this.color;

            double exX = EntityNukeTorex.this.getX();
            double exY = EntityNukeTorex.this.getY() + EntityNukeTorex.this.coreHeight;
            double exZ = EntityNukeTorex.this.getZ();

            double distX = exX - posX;
            double distY = exY - posY;
            double distZ = exZ - posZ;

            double distSq = distX * distX + distY * distY + distZ * distZ;
            distSq /= EntityNukeTorex.this.heat;
            double dist = Math.sqrt(distSq);

            dist = Math.max(dist, 1);
            double col = 2D / dist;

            int type = EntityNukeTorex.this.entityData.get(TYPE);

            if (type == 1) {
                this.color = new Vec3(
                        Math.max(col * 1, 0.25),
                        Math.max(col * 2, 0.25),
                        Math.max(col * 0.5, 0.25)
                );
            } else if (type == 2) {
                Color color = Color.getHSBColor(this.angle / 2F / (float) Math.PI, 1F, 1F);
                if (this.type == TorexType.RING) {
                    this.color = new Vec3(
                            Math.max(col * 1, 0.25),
                            Math.max(col * 1, 0.25),
                            Math.max(col * 1, 0.25)
                    );
                } else {
                    this.color = new Vec3(color.getRed() / 255D, color.getGreen() / 255D, color.getBlue() / 255D);
                }
            } else {
                // Original 1.7.10 type-0 RGB, including SHOCK and RING.
                // Colour changes with heat and distance, not a fixed-time ramp.
                this.color = TorexVisualMath.standardSmokeColor(col);
            }
        }

        public Vec3 getInterpPos(float interp) {
            float scale = EntityNukeTorex.this.getScale();
            Vec3 base = new Vec3(
                    prevPosX + (posX - prevPosX) * interp,
                    prevPosY + (posY - prevPosY) * interp,
                    prevPosZ + (posZ - prevPosZ) * interp);

            if (this.type != TorexType.SHOCK) {
                base = new Vec3(
                        (base.x - EntityNukeTorex.this.getX()) * scale + EntityNukeTorex.this.getX(),
                        (base.y - EntityNukeTorex.this.getY()) * scale + EntityNukeTorex.this.getY(),
                        (base.z - EntityNukeTorex.this.getZ()) * scale + EntityNukeTorex.this.getZ()
                );
            }

            return base;
        }

        public Vec3 getInterpColor(float interp) {
            if (this.type == TorexType.CONDENSATION) {
                return new Vec3(1F, 1F, 1F);
            }

            // In the 1.7.10 simulation greying brightens RGB, not alpha.
            return TorexVisualMath.interpolateColor(
                    this.prevColor, this.color, interp,
                    EntityNukeTorex.this.getGreying(), this.type == TorexType.RING);
        }

        public float getAlpha() {
            float alpha = (1F - ((float) age / (float) cloudletLife)) * EntityNukeTorex.this.getAlpha();
            if (this.type == TorexType.CONDENSATION) alpha *= 0.25F;
            return Math.max(0F, Math.min(1F, alpha));
        }

        private float startingScale = 1;
        private float growingScale = 5F;

        public float getScale() {
            float base = startingScale + ((float) age / (float) cloudletLife) * growingScale;
            if (this.type != TorexType.SHOCK) base *= EntityNukeTorex.this.getScale();
            return base;
        }

        public Cloudlet setScale(float start, float grow) {
            this.startingScale = start;
            this.growingScale = grow;
            return this;
        }

        private double motionMult = 1F;

        public Cloudlet setMotion(double mult) {
            this.motionMult = mult;
            return this;
        }
    }

    public enum TorexType {
        STANDARD,
        SHOCK,
        RING,
        CONDENSATION
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    public static void statFacStandard(Level level, double x, double y, double z, float scale) {
        statFac(level, x, y, z, scale, 0);
    }

    public static void statFacBale(Level level, double x, double y, double z, float scale) {
        statFac(level, x, y, z, scale, 1);
    }

    public static void statFac(Level level, double x, double y, double z, float scale, int type) {
        EntityNukeTorex torex = new EntityNukeTorex(ModEntities.NUKE_TOREX.get(), level);
        torex.setScale(Mth.clamp((float) BobMathUtil.squirt(scale * 0.01) * 1.5F, 0.5F, 5F));
        torex.setType(type);
        torex.setPos(x, y, z);
        if (!level.isClientSide) {
            torex.entityData.set(START_GAME_TIME, level.getGameTime());
        }
        level.addFreshEntity(torex);
    }
}