package com.hbm.api.energy;

import com.hbm.api.tile.ILoadedTile;

import net.minecraft.core.BlockPos;

import net.minecraft.world.phys.Vec3;

public interface IEnergyHandler extends IEnergyConnector, ILoadedTile {

    long getPower();
    void setPower(long power);
    long getMaxPower();

    boolean PARTICLE_DEBUG = false;

    default Vec3 getDebugParticlePos(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
    }

}