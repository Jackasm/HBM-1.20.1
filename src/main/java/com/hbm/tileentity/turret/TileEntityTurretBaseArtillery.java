package com.hbm.tileentity.turret;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.IRadarCommandReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class TileEntityTurretBaseArtillery extends TileEntityTurretBaseNT implements IRadarCommandReceiver {

    protected List<Vec3> targetQueue = new ArrayList<>();

    public TileEntityTurretBaseArtillery(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean sendCommandPosition(int x, int y, int z) {
        this.enqueueTarget(x + 0.5, y, z + 0.5);
        return true;
    }

    @Override
    public boolean sendCommandEntity(Entity target) {
        this.enqueueTarget(target.getX(), target.getY(), target.getZ());
        return true;
    }

    public void enqueueTarget(double x, double y, double z) {
        if (level == null) return;

        Vec3 pos = this.getTurretPos();
        Vec3 delta = new Vec3(x - pos.x, y - pos.y, z - pos.z);
        if (delta.length() <= this.getDetectorRange()) {
            this.targetQueue.add(new Vec3(x, y, z));
        }
    }

    public abstract boolean doLOSCheck();

    @Override
    public boolean entityInLOS(Entity e) {
        if (level == null) return false;

        if (doLOSCheck()) {
            return super.entityInLOS(e);
        } else {
            Vec3 pos = this.getTurretPos();
            Vec3 ent = this.getEntityPos(e);
            Vec3 delta = new Vec3(ent.x - pos.x, ent.y - pos.y, ent.z - pos.z);
            double length = delta.length();

            if (length < this.getDetectorGrace() || length > this.getDetectorRange() * 1.1) {
                return false;
            }

            int height = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                    (int) Math.floor(e.getX()), (int) Math.floor(e.getZ()));
            return height < (e.getY() + e.getBbHeight());
        }
    }

    @Override
    protected void updateConnections() {
        if (level == null) return;

        Direction dir = this.getBlockState().getValue(BlockDummyable.FACING).getOpposite();
        Direction rot = dir.getClockWise();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                BlockPos pos1 = worldPosition.offset(
                        dir.getStepX() * (-1 + j) + rot.getStepX() * -3,
                        i,
                        dir.getStepZ() * (-1 + j) + rot.getStepZ() * -3
                );
                this.trySubscribe(level, pos1, Direction.SOUTH);

                BlockPos pos2 = worldPosition.offset(
                        dir.getStepX() * (-1 + j) + rot.getStepX() * 2,
                        i,
                        dir.getStepZ() * (-1 + j) + rot.getStepZ() * 2
                );
                this.trySubscribe(level, pos2, Direction.NORTH);

                BlockPos pos3 = worldPosition.offset(
                        dir.getStepX() * -2 + rot.getStepX() * (1 - j),
                        i,
                        dir.getStepZ() * -2 + rot.getStepZ() * (1 - j)
                );
                this.trySubscribe(level, pos3, Direction.EAST);

                BlockPos pos4 = worldPosition.offset(
                        dir.getStepX() * 3 + rot.getStepX() * (1 - j),
                        i,
                        dir.getStepZ() * 3 + rot.getStepZ() * (1 - j)
                );
                this.trySubscribe(level, pos4, Direction.WEST);
            }
        }
    }

    // OpenComputers методы удалены

    public Vec3 getTurretPos() {
        return new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
    }

    public Vec3 getEntityPos(Entity entity) {
        return new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ());
    }

    public abstract double getDetectorRange();
    public abstract double getDetectorGrace();
}