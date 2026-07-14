package com.hbm.tileentity.machine;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class TileEntityDemonLamp extends TileEntityMachineBase {

    public TileEntityDemonLamp(BlockPos pos, BlockState state) {
        super(ModTileEntity.LAMP_DEMON.get(), pos, state);
    }

    public void tick() {
        if (!Objects.requireNonNull(level).isClientSide) {
            radiate(level, worldPosition);
        }
    }

    private void radiate(Level level, BlockPos pos) {
        float rads = 100000F;
        double range = 25D;

        AABB aabb = new AABB(
                pos.getX() + 0.5 - range,
                pos.getY() + 0.5 - range,
                pos.getZ() + 0.5 - range,
                pos.getX() + 0.5 + range,
                pos.getY() + 0.5 + range,
                pos.getZ() + 0.5 + range
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);

        for (LivingEntity e : entities) {
            Vec3 start = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Vec3 end = new Vec3(e.getX(), e.getY() + e.getEyeHeight(), e.getZ());
            Vec3 vec = end.subtract(start);
            double len = vec.length();
            vec = vec.normalize();

            float res = 0;

            // Проходим по лучу, суммируя сопротивление блоков
            for (int i = 1; i < len; i++) {
                int ix = (int) Math.floor(pos.getX() + 0.5 + vec.x * i);
                int iy = (int) Math.floor(pos.getY() + 0.5 + vec.y * i);
                int iz = (int) Math.floor(pos.getZ() + 0.5 + vec.z * i);
                BlockPos checkPos = new BlockPos(ix, iy, iz);
                BlockState state = level.getBlockState(checkPos);
                // В 1.20.1 нет прямого метода getExplosionResistance у блока, используем значение по умолчанию
                float resistance = state.getBlock().getExplosionResistance();
                res += resistance;
            }

            if (res < 1) res = 1;

            float eRads = rads;
            eRads /= res;
            eRads /= (float) (len * len);

            ContaminationUtil.contaminate(e, HazardType.RADIATION, ContaminationType.CREATIVE, eRads);

            if (len < 2) {
                e.hurt(level.damageSources().inFire(), 100);
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

}