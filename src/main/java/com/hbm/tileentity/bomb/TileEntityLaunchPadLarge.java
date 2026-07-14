package com.hbm.tileentity.bomb;

import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.items.weapon.ItemMissile;
import com.hbm.items.weapon.ItemMissile.MissileFormFactor;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.sound.ModSounds;
import com.hbm.sound.SoundHelper;
import com.hbm.util.Library.PosDir;

import com.hbm.sound.AudioWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.hbm.blocks.bomb.LaunchPadLarge.FACING;

public class TileEntityLaunchPadLarge extends TileEntityLaunchPadBase {

    public int formFactor = -1;
    public boolean erected = false;
    public boolean readyToLoad = false;
    public boolean scheduleErect = false;
    public float lift = 1F;
    public float erector = 90F;
    public float prevLift = 1F;
    public float prevErector = 90F;
    public float syncLift;
    public float syncErector;
    private int sync;
    public int delay = 20;

    private AudioWrapper audioLift;
    private AudioWrapper audioErector;

    protected boolean liftMoving = false;
    protected boolean erectorMoving = false;

    public TileEntityLaunchPadLarge(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean isReadyForLaunch() {
        return this.erected && this.readyToLoad;
    }

    @Override
    public double getLaunchOffset() {
        return 2D;
    }

    @Override
    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {

            this.prevLift = this.lift;
            this.prevErector = this.erector;

            float erectorSpeed = 1.5F;
            float liftSpeed = 0.025F;

            if (this.isMissileValid()) {
                if (inventory.getStackInSlot(0).getItem() instanceof ItemMissile missile) {
                    this.formFactor = missile.formFactor.ordinal();

                    if (missile.formFactor == MissileFormFactor.ATLAS || missile.formFactor == MissileFormFactor.HUGE) {
                        erectorSpeed /= 2F;
                        liftSpeed /= 2F;
                    }
                }

                if (this.erector == 90F && this.lift == 1F) {
                    this.readyToLoad = true;
                }
            } else {
                readyToLoad = false;
                erected = false;
                delay = 20;
            }

            if (this.power >= 75_000) {
                if (delay > 0) {
                    delay--;

                    if (delay < 10 && scheduleErect) {
                        this.erected = true;
                        this.scheduleErect = false;
                    }

                    if (inventory.getStackInSlot(0).isEmpty() || !readyToLoad) {
                        if (erector < 90F) {
                            erector = Math.min(erector + erectorSpeed, 90F);
                            if (erector == 90F) delay = 20;
                        } else if (lift < 1F) {
                            lift = Math.min(lift + liftSpeed, 1F);
                            if (lift == 1F) {
                                readyToLoad = true;
                                delay = 20;
                            }
                        }
                    }
                } else {
                    if (!erected && readyToLoad) {
                        this.state = STATE_LOADING;

                        if (erector != 0F) {
                            erector = Math.max(erector - erectorSpeed, 0F);
                            if (erector == 0F) delay = 20;
                        } else if (lift > 0) {
                            lift = Math.max(lift - liftSpeed, 0F);
                            if (lift == 0F) {
                                scheduleErect = true;
                                delay = 20;
                            }
                        }
                    } else {
                        if (erector < 90F) {
                            erector = Math.min(erector + erectorSpeed, 90F);
                            if (erector == 90F) delay = 20;
                        } else if (lift < 1F) {
                            lift = Math.min(lift + liftSpeed, 1F);
                            if (lift == 1F) {
                                readyToLoad = true;
                                delay = 20;
                            }
                        }
                    }
                }
            }

            if (!this.hasFuel() || !this.isMissileValid()) this.state = STATE_MISSING;
            if (this.erected && this.canLaunch()) this.state = STATE_READY;

            boolean prevLiftMoving = this.liftMoving;
            boolean prevErectorMoving = this.erectorMoving;
            this.liftMoving = false;
            this.erectorMoving = false;
            if (this.prevLift != this.lift) this.liftMoving = true;
            if (this.prevErector != this.erector) this.erectorMoving = true;

            if (prevLiftMoving && !this.liftMoving) {
                level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        ModSounds.WGH_STOP.get(), net.minecraft.sounds.SoundSource.BLOCKS, 2F, 1F);
            }
            if (prevErectorMoving && !this.erectorMoving) {
                level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        ModSounds.GARAGE_STOP.get(), net.minecraft.sounds.SoundSource.BLOCKS, 2F, 1F);
            }

        } else {
            this.prevLift = this.lift;
            this.prevErector = this.erector;

            if (this.sync > 0) {
                this.lift = this.lift + ((this.syncLift - this.lift) / (float) this.sync);
                this.erector = this.erector + ((this.syncErector - this.erector) / (float) this.sync);
                --this.sync;
            } else {
                this.lift = this.syncLift;
                this.erector = this.syncErector;
            }

            if (this.liftMoving) {
                if (this.audioLift == null || !this.audioLift.isPlaying()) {
                    this.audioLift = SoundHelper.createLoopedSound(ModSounds.WGH_START.get(),
                            worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                            0.75F, 25F, 1.0F, 5);
                    if (this.audioLift != null) this.audioLift.startSound();
                }
                if (this.audioLift != null) this.audioLift.keepAlive();
            } else {
                if (this.audioLift != null) {
                    this.audioLift.stopSound();
                    this.audioLift = null;
                }
            }

            if (this.erectorMoving) {
                if (this.audioErector == null || !this.audioErector.isPlaying()) {
                    this.audioErector = SoundHelper.createLoopedSound(ModSounds.GARAGE_MOVE.get(),
                            worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                            1.5F, 25F, 1.0F, 5);
                    if (this.audioErector != null) this.audioErector.startSound();
                }
                if (this.audioErector != null) this.audioErector.keepAlive();
            } else {
                if (this.audioErector != null) {
                    this.audioErector.stopSound();
                    this.audioErector = null;
                }
            }

            if (this.erected && (this.formFactor == MissileFormFactor.HUGE.ordinal() || this.formFactor == MissileFormFactor.ATLAS.ordinal()) && this.tanks[1].getFill() > 0) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "tower");
                data.putFloat("lift", 0F);
                data.putFloat("base", 0.5F);
                data.putFloat("max", 2F);
                data.putInt("life", 70 + level.random.nextInt(30));
                data.putDouble("posX", worldPosition.getX() + 0.5 + level.random.nextGaussian() * 0.5);
                data.putDouble("posZ", worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 0.5);
                data.putDouble("posY", worldPosition.getY() + 2);
                data.putBoolean("noWind", true);
                data.putFloat("alphaMod", 2F);
                data.putFloat("strafe", 0.05F);
                for (int i = 0; i < 3; i++) {
                    PacketDispatcher.sendToAllAround(
                            new AuxParticlePacketNT(data, worldPosition.getX() + 0.5, worldPosition.getY() + 2, worldPosition.getZ() + 0.5),
                            level,
                            worldPosition,
                            64
                    );
                }
            }

            AABB box = new AABB(
                    worldPosition.getX() - 0.5, worldPosition.getY(), worldPosition.getZ() - 0.5,
                    worldPosition.getX() + 1.5, worldPosition.getY() + 10, worldPosition.getZ() + 1.5
            );

            List<EntityMissileBaseNT> entities = level.getEntitiesOfClass(EntityMissileBaseNT.class, box);

            if (!entities.isEmpty()) {
                for (int i = 0; i < 15; i++) {
                    Direction dir = this.getBlockState().getValue(FACING);
                    if (level.random.nextBoolean()) dir = dir.getOpposite();
                    float moX = (float) (level.random.nextGaussian() * 0.15F + 0.75) * dir.getStepX();
                    float moZ = (float) (level.random.nextGaussian() * 0.15F + 0.75) * dir.getStepZ();

                    CompoundTag data = new CompoundTag();
                    data.putDouble("posX", worldPosition.getX() + 0.5);
                    data.putDouble("posY", worldPosition.getY() + 0.25);
                    data.putDouble("posZ", worldPosition.getZ() + 0.5);
                    data.putString("type", "launchSmoke");
                    data.putDouble("moX", moX);
                    data.putDouble("moY", 0);
                    data.putDouble("moZ", moZ);
                    PacketDispatcher.sendToAllAround(
                            new AuxParticlePacketNT(data, worldPosition.getX() + 0.5, worldPosition.getY() + 0.25, worldPosition.getZ() + 0.5),
                            level,
                            worldPosition,
                            64
                    );
                }
            }
        }

        super.tick();
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);

        buf.writeBoolean(this.liftMoving);
        buf.writeBoolean(this.erectorMoving);
        buf.writeBoolean(this.erected);
        buf.writeBoolean(this.readyToLoad);
        buf.writeByte((byte) this.formFactor);
        buf.writeFloat(this.lift);
        buf.writeFloat(this.erector);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);

        this.liftMoving = buf.readBoolean();
        this.erectorMoving = buf.readBoolean();
        this.erected = buf.readBoolean();
        this.readyToLoad = buf.readBoolean();
        this.formFactor = buf.readByte();
        this.syncLift = buf.readFloat();
        this.syncErector = buf.readFloat();

        if (this.lift != this.syncLift || this.erector != this.syncErector) {
            this.sync = 3;
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        this.erected = nbt.getBoolean("erected");
        this.readyToLoad = nbt.getBoolean("readyToLoad");
        this.lift = nbt.getFloat("lift");
        this.erector = nbt.getFloat("erector");
        this.formFactor = nbt.getInt("formFactor");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putBoolean("erected", erected);
        nbt.putBoolean("readyToLoad", readyToLoad);
        nbt.putFloat("lift", lift);
        nbt.putFloat("erector", erector);
        nbt.putInt("formFactor", formFactor);
    }

    @Override
    public void finalizeLaunch(Entity missile) {
        super.finalizeLaunch(missile);
        this.erected = false;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public PosDir[] getConPos() {
        return new PosDir[]{
                new PosDir(new BlockPos(worldPosition.getX() + 5, worldPosition.getY(), worldPosition.getZ() - 2), Direction.EAST),
                new PosDir(new BlockPos(worldPosition.getX() + 5, worldPosition.getY(), worldPosition.getZ() + 2), Direction.EAST),
                new PosDir(new BlockPos(worldPosition.getX() - 5, worldPosition.getY(), worldPosition.getZ() - 2), Direction.WEST),
                new PosDir(new BlockPos(worldPosition.getX() - 5, worldPosition.getY(), worldPosition.getZ() + 2), Direction.WEST),
                new PosDir(new BlockPos(worldPosition.getX() - 2, worldPosition.getY(), worldPosition.getZ() + 5), Direction.SOUTH),
                new PosDir(new BlockPos(worldPosition.getX() + 2, worldPosition.getY(), worldPosition.getZ() + 5), Direction.SOUTH),
                new PosDir(new BlockPos(worldPosition.getX() - 2, worldPosition.getY(), worldPosition.getZ() - 5), Direction.NORTH),
                new PosDir(new BlockPos(worldPosition.getX() + 2, worldPosition.getY(), worldPosition.getZ() - 5), Direction.NORTH)
        };
    }

    private AABB renderBoundingBox = null;

    @Override
    public AABB getRenderBoundingBox() {
        if (renderBoundingBox == null) {
            renderBoundingBox = new AABB(
                    worldPosition.getX() - 10,
                    worldPosition.getY(),
                    worldPosition.getZ() - 10,
                    worldPosition.getX() + 11,
                    worldPosition.getY() + 15,
                    worldPosition.getZ() + 11
            );
        }
        return renderBoundingBox;
    }

}