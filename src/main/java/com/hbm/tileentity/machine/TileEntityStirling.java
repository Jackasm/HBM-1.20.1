package com.hbm.tileentity.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.energy.IEnergyProvider;
import com.hbm.api.tile.IHeatSource;
import com.hbm.blocks.machine.MachineStirling;
import com.hbm.entity.projectile.EntityCog;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class TileEntityStirling extends TileEntityLoadedBase implements IBufPacketReceiver, IEnergyProvider, IConfigurableMachine {

    public long powerBuffer;
    public int heat;
    private int warnCooldown = 0;
    private int overspeed = 0;
    public boolean hasCog = true;

    public float spin;
    public float lastSpin;

    private final MachineStirling.StirlingType type;

    public static double diffusion = 0.1D;
    public static double efficiency = 0.5D;
    public static int maxHeatNormal = 300;
    public static int maxHeatSteel = 1500;
    public static int overspeedLimit = 300;

    public TileEntityStirling(BlockPos pos, BlockState state, MachineStirling.StirlingType type) {
        super(ModTileEntity.MACHINE_STIRLING.get(), pos, state);
        this.type = type;
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (hasCog) {
                powerBuffer = 0;
                tryPullHeat();

                powerBuffer = (long) (heat * (isCreative() ? 1 : efficiency));

                if (warnCooldown > 0) warnCooldown--;

                if (heat > maxHeat() && !isCreative()) {
                    overspeed++;

                    if (overspeed > 60 && warnCooldown == 0) {
                        warnCooldown = 100;
                        level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                                ModSounds.WARN_OVERSPEED.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
                    }

                    if (overspeed > overspeedLimit) {
                        hasCog = false;
                        level.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                                5F, Level.ExplosionInteraction.TNT);

                        Direction dir = getBlockState().getValue(MachineStirling.FACING);
                        int dummyState = getBlockState().getValue(MachineStirling.DUMMY_STATE);
                        EntityCog cog = new EntityCog(level, worldPosition.getX() + 0.5 + dir.getStepX(),
                                worldPosition.getY() + 1, worldPosition.getZ() + 0.5 + dir.getStepZ());
                        cog.setOrientation(dummyState);
                        cog.setMeta(getGearMeta());

                        Direction rot = dir.getClockWise();
                        cog.setDeltaMovement(rot.getStepX(), 1 + (heat - maxHeat()) * 0.0001D, rot.getStepZ());
                        level.addFreshEntity(cog);

                        setChanged();
                    }
                } else {
                    overspeed = 0;
                }
            } else {
                overspeed = 0;
                warnCooldown = 0;
            }

            networkPackNT(150);

            if (hasCog) {
                for (PosDir pos : getConPos()) {
                    tryProvide(level, pos.pos(), pos.dir());
                }
            } else {
                if (powerBuffer > 0) powerBuffer--;
            }

            heat = 0;
        } else {
            float momentum = powerBuffer * 50F / ((float) maxHeat());
            if (isCreative()) momentum = Math.min(momentum, 45F);

            lastSpin = spin;
            spin += momentum;
            if (spin >= 360F) {
                spin -= 360F;
                lastSpin -= 360F;
            }
        }
    }

    public int getGearMeta() {
        return switch (type) {
            case NORMAL -> 0;
            case CREATIVE -> 2;
            case STEEL -> 1;
        };
    }

    public int maxHeat() {
        return type == MachineStirling.StirlingType.NORMAL ? maxHeatNormal : maxHeatSteel;
    }

    public boolean isCreative() {
        return type == MachineStirling.StirlingType.CREATIVE;
    }

    protected PosDir[] getConPos() {
        return new PosDir[]{
                new PosDir(worldPosition.offset(2, 0, 0), Direction.EAST),
                new PosDir(worldPosition.offset(-2, 0, 0), Direction.WEST),
                new PosDir(worldPosition.offset(0, 0, 2), Direction.SOUTH),
                new PosDir(worldPosition.offset(0, 0, -2), Direction.NORTH)
        };
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeLong(powerBuffer);
        buf.writeInt(heat);
        buf.writeBoolean(hasCog);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        powerBuffer = buf.readLong();
        heat = buf.readInt();
        hasCog = buf.readBoolean();
    }

    protected void tryPullHeat() {
        BlockEntity con = Objects.requireNonNull(level).getBlockEntity(worldPosition.below());

        if (con instanceof IHeatSource source) {
            int heatSrc = (int) (source.getHeatStored() * diffusion);
            if (heatSrc > 0) {
                source.useUpHeat(heatSrc);
                heat += heatSrc;
                return;
            }
        }

        heat = Math.max(heat - Math.max(heat / 1000, 1), 0);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        powerBuffer = nbt.getLong("powerBuffer");
        hasCog = nbt.getBoolean("hasCog");
        overspeed = nbt.getInt("overspeed");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("powerBuffer", powerBuffer);
        nbt.putBoolean("hasCog", hasCog);
        nbt.putInt("overspeed", overspeed);
    }

    // ==================== IEnergyProvider ====================
    @Override
    public long getPower() {
        return powerBuffer;
    }

    @Override
    public void setPower(long power) {
        powerBuffer = power;
    }

    @Override
    public long getMaxPower() {
        return powerBuffer;
    }

    @Override
    public long getProviderSpeed() {
        return powerBuffer;
    }

    @Override
    public void usePower(long power) {
        powerBuffer -= power;
    }

    // ==================== IConfigurableMachine ====================
    @Override
    public String getConfigName() {
        return "stirling";
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        diffusion = IConfigurableMachine.grab(obj, "D:diffusion", diffusion);
        efficiency = IConfigurableMachine.grab(obj, "D:efficiency", efficiency);
        maxHeatNormal = IConfigurableMachine.grab(obj, "I:maxHeatNormal", maxHeatNormal);
        maxHeatSteel = IConfigurableMachine.grab(obj, "I:maxHeatSteel", maxHeatSteel);
        overspeedLimit = IConfigurableMachine.grab(obj, "I:overspeedLimit", overspeedLimit);
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("D:diffusion").value(diffusion);
        writer.name("D:efficiency").value(efficiency);
        writer.name("I:maxHeatNormal").value(maxHeatNormal);
        writer.name("I:maxHeatSteel").value(maxHeatSteel);
        writer.name("I:overspeedLimit").value(overspeedLimit);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 1,
                worldPosition.getY(),
                worldPosition.getZ() - 1,
                worldPosition.getX() + 2,
                worldPosition.getY() + 2,
                worldPosition.getZ() + 2
        );
    }
}