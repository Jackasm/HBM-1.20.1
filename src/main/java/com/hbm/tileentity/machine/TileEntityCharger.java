package com.hbm.tileentity.machine;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.blocks.machine.Charger;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileEntityCharger extends TileEntityLoadedBase implements IEnergyReceiver {

    private List<Player> players = List.of();
    private long charge = 0;
    private int lastOp = 0;
    private boolean particles = false;

    public int usingTicks;
    public int lastUsingTicks;
    public static final int delay = 20;

    public TileEntityCharger(BlockPos pos, BlockState state) {
        super(ModTileEntity.CHARGER.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        Direction facing = getBlockState().getValue(Charger.FACING);
        Direction dir = facing.getOpposite();

        // trySubscribe(...) — из IEnergyReceiver
        this.trySubscribe(level, worldPosition.relative(dir), dir);

        AABB box = new AABB(
                worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5,
                worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5
        ).inflate(0.5, 0.0, 0.5);

        players = level.getEntitiesOfClass(Player.class, box);
        charge = 0;

        EquipmentSlot[] slots = {
                EquipmentSlot.MAINHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS,
                EquipmentSlot.CHEST, EquipmentSlot.HEAD
        };

        for (Player player : players) {
            for (EquipmentSlot slot : slots) {
                ItemStack stack = player.getItemBySlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof IBatteryItem battery) {
                    charge += Math.min(battery.getMaxCharge(stack) - battery.getCharge(stack),
                            battery.getChargeRate());
                }
            }
        }

        particles = lastOp > 0;
        if (particles) {
            lastOp--;
            if (level.getGameTime() % 20 == 0) {
                level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5, SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS, 0.2F, 0.5F);
            }
        }

        networkPackNT(50);

        lastUsingTicks = usingTicks;

        if ((charge > 0 || particles) && usingTicks < delay) {
            usingTicks++;
            if (usingTicks == 2) {
                level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5, SoundEvents.PISTON_EXTEND,
                        SoundSource.BLOCKS, 0.5F, 0.5F);
            }
        }
        if ((charge <= 0 && !particles) && usingTicks > 0) {
            usingTicks--;
            if (usingTicks == 4) {
                level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5, SoundEvents.PISTON_CONTRACT,
                        SoundSource.BLOCKS, 0.5F, 0.5F);
            }
        }

        if (particles && level instanceof ServerLevel server) {
            double px = worldPosition.getX() + 0.5 + level.random.nextDouble() * 0.0625 + dir.getStepX() * 0.75;
            double py = worldPosition.getY() + 0.1;
            double pz = worldPosition.getZ() + 0.5 + level.random.nextDouble() * 0.0625 + dir.getStepZ() * 0.75;
            server.sendParticles(ParticleTypes.CRIT, px, py, pz, 1,
                    -dir.getStepX() + level.random.nextGaussian() * 0.1,
                    0,
                    -dir.getStepZ() + level.random.nextGaussian() * 0.1,
                    0.0);
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(this.charge);
        buf.writeBoolean(this.particles);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.charge = buf.readLong();
        this.particles = buf.readBoolean();
    }

    @Override public long getPower() { return 0; }
    @Override public long getMaxPower() { return charge; }
    @Override public void setPower(long power) { }

    @Override
    public long transferPower(long power) {
        if (this.usingTicks < delay || power == 0) return power;

        EquipmentSlot[] slots = {
                EquipmentSlot.MAINHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS,
                EquipmentSlot.CHEST, EquipmentSlot.HEAD
        };

        for (Player player : players) {
            for (EquipmentSlot slot : slots) {
                ItemStack stack = player.getItemBySlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof IBatteryItem battery) {
                    long toCharge = Math.min(battery.getMaxCharge(stack) - battery.getCharge(stack),
                            battery.getChargeRate());
                    toCharge = Math.min(toCharge, Math.max(power / 5, 1));
                    battery.chargeBattery(stack, toCharge);
                    power -= toCharge;
                    lastOp = 4;
                }
            }
        }
        return power;
    }
}