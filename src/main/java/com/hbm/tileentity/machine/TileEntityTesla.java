package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.entity.mob.*;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ModDamageSource;
import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileEntityTesla extends TileEntityLoadedBase implements IEnergyReceiver {

    public static final long MAX_POWER = 100000;
    public static final int RANGE = 10;
    public static final double OFFSET = 1.75;

    private long power;
    private List<double[]> targets = new ArrayList<>();

    public TileEntityTesla(BlockPos pos, BlockState state) {
        super(ModTileEntity.TESLA.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        updateConnections();

        this.targets.clear();

        if (level.getBlockState(worldPosition.below()).getBlock() == ModBlocks.METEOR_BATTERY.get()) {
            power = MAX_POWER;
        }

        if (power >= 5000) {
            power -= 5000;

            double dx = worldPosition.getX() + 0.5;
            double dy = worldPosition.getY() + OFFSET;
            double dz = worldPosition.getZ() + 0.5;

            this.targets = zap(level, dx, dy, dz, RANGE, null);
        }

        this.networkPackNT(100);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        int size = buf.readShort();
        targets.clear();
        for (int i = 0; i < size; i++) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            targets.add(new double[]{x, y, z});
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeShort(targets.size());
        for (double[] d : targets) {
            buf.writeDouble(d[0]);
            buf.writeDouble(d[1]);
            buf.writeDouble(d[2]);
        }
    }

    private void updateConnections() {
        for (Direction dir : Direction.values()) {
            trySubscribe(level, worldPosition.relative(dir), dir);
        }
    }

    public static List<double[]> zap(Level level, double x, double y, double z, double radius, Entity source) {
        List<double[]> ret = new ArrayList<>();

        AABB box = new AABB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box);

        for (LivingEntity e : targets) {
            if (e instanceof Ocelot || e == source) continue;

            Vec3 vec = new Vec3(e.getX() - x, e.getY() + e.getBbHeight() / 2 - y, e.getZ() - z);

            if (vec.length() > radius) continue;

            if (Library.isObstructed(level, x, y, z, e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ())) continue;

            if (e instanceof EntityTaintCrab) {
                ret.add(new double[]{e.getX(), e.getY() + 1.25, e.getZ()});
                e.heal(15F);
                continue;
            }

            if (e instanceof EntityTeslaCrab) {
                ret.add(new double[]{e.getX(), e.getY() + 1, e.getZ()});
                e.heal(10F);
                continue;
            }

            if (e instanceof EntityCyberCrab) {
                ret.add(new double[]{e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ()});
                continue;
            }

            if (e instanceof Creeper creeper) {
                creeper.setSwellDir(1);
                ret.add(new double[]{e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ()});
                continue;
            }

            if (!(e instanceof Player player && ArmorUtil.checkForFaraday(player))) {
                float damage = Mth.clamp(e.getMaxHealth() * 0.5F, 3, 20) / Math.max(targets.size(), 1);
                if (e.hurt(ModDamageSource.causeElectricity(level, null, source), damage)) {
                    level.playSound(null, e.getX(), e.getY(), e.getZ(),
                            SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 1.0F, 1.0F);
                }
            }

            double offset = 0;
            if (source != null && e instanceof Player && level.isClientSide) {
                offset = e.getBbHeight();
            }

            ret.add(new double[]{e.getX(), e.getY() + e.getBbHeight() / 2 - offset, e.getZ()});
        }

        return ret;
    }

    public long getPower() { return power; }
    public List<double[]> getTargets() { return targets; }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.power = nbt.getLong("power");
    }

    @Override
    public void setPower(long power) {
        this.power = Math.min(power, MAX_POWER);
    }

    @Override
    public long getMaxPower() {
        return MAX_POWER;
    }

    @Override
    public long getReceiverSpeed() {
        return MAX_POWER / 100;
    }

    @Override
    public ConnectionPriority getPriority() {
        return ConnectionPriority.LOW;
    }

    @Override
    public boolean canConnect(Direction dir) {
        return true;
    }
}