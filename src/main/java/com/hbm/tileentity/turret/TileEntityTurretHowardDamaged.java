package com.hbm.tileentity.turret;

import com.hbm.config.WeaponConfig;

import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.EntityDamageUtil;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class TileEntityTurretHowardDamaged extends TileEntityTurretHoward {

    public TileEntityTurretHowardDamaged(BlockPos pos, BlockState state) {
        super(ModTileEntity.TURRET_HOWARD_DAMAGED.get(), pos, state);
    }

    @Override
    public boolean hasPower() {
        return true;
    }

    @Override
    public boolean isOn() {
        return true;
    }

    @Override
    public double getTurretYawSpeed() {
        return 3D;
    }

    @Override
    public double getTurretPitchSpeed() {
        return 2D;
    }

    @Override
    public double getDecetorRange() {
        return 16D;
    }

    @Override
    public double getDecetorGrace() {
        return 5D;
    }

    @Override
    public void updateFiringTick() {
        timer++;

        if (tPos != null) {
            if (timer % 4 == 0) {
                Objects.requireNonNull(level).playSound(null, worldPosition, ModSounds.TURRET_HOWARD_FIRE.get(), net.minecraft.sounds.SoundSource.BLOCKS, 4.0F, 0.7F + level.random.nextFloat() * 0.3F);

                cachedCasingConfig = BulletConfigRegistry.CASINGDGK;
                spawnCasing();

                if (level.random.nextInt(100) + 1 <= WeaponConfig.CIWS_HITRATE * 0.5) {
                    EntityDamageUtil.attackEntityFromIgnoreIFrame(target, ModDamageSource.causeShrapnelDamage(level),
                            2F + level.random.nextInt(2));
                }

                Vec3 pos = getTurretPos();
                Vec3 vec = new Vec3(getBarrelLength(), 0, 0);
                vec = vec.xRot((float) -rotationPitch);
                vec = vec.yRot((float) -(rotationYaw + Math.PI * 0.5));

                Vec3 hOff = new Vec3(0, 0.25, 0);
                hOff = hOff.xRot((float) -rotationPitch);
                hOff = hOff.yRot((float) -(rotationYaw + Math.PI * 0.5));

                CompoundTag data = new CompoundTag();
                data.putString("type", "vanillaExt");
                data.putString("mode", "largeexplode");
                data.putFloat("size", 1.5F);
                data.putByte("count", (byte) 1);
                PacketDispatcher.sendAuxParticleNT(data, pos.x + vec.x + hOff.x, pos.y + vec.y + hOff.y, pos.z + vec.z + hOff.z, level, worldPosition);
            }
        }
    }
}