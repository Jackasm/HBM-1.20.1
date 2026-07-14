package com.hbm.tileentity.machine;

import com.hbm.network.PacketDispatcher;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileEntityZirnoxDestroyed extends BlockEntity {

    public boolean onFire = true;

    public TileEntityZirnoxDestroyed(BlockPos pos, BlockState state) {
        super(ModTileEntity.ZIRNOX_DESTROYED.get(), pos, state);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        onFire = nbt.getBoolean("fire");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("onFire", onFire);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            radiate(level, worldPosition);

            if (level.random.nextInt(5000) == 0) {
                onFire = false;
            }

            if (onFire && level.getGameTime() % 50 == 0) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "rbmkflame");
                data.putInt("maxAge", 90);
                PacketDispatcher.sendAuxParticleNT(data,
                        worldPosition.getX() + 0.25 + level.random.nextDouble() * 0.5,
                        worldPosition.getY() + 1.75,
                        worldPosition.getZ() + 0.25 + level.random.nextDouble() * 0.5,
                        level,
                        worldPosition
                );

                level.playSound(null, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                        SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                        1.0F + level.random.nextFloat(),
                        level.random.nextFloat() * 0.7F + 0.3F
                );
            }
        }
    }

    private void radiate(Level level, BlockPos pos) {
        float rads = onFire ? 500000F : 75000F;
        double range = 100D;

        AABB aabb = new AABB(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5
        ).inflate(range);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);

        for (LivingEntity e : entities) {
            Vec3 vec = new Vec3(
                    e.getX() - (pos.getX() + 0.5),
                    (e.getY() + e.getEyeHeight()) - (pos.getY() + 0.5),
                    e.getZ() - (pos.getZ() + 0.5)
            );
            double len = vec.length();
            vec = vec.normalize();

            float res = 0;

            for (int i = 1; i < len; i++) {
                int ix = (int) Math.floor(pos.getX() + 0.5 + vec.x * i);
                int iy = (int) Math.floor(pos.getY() + 0.5 + vec.y * i);
                int iz = (int) Math.floor(pos.getZ() + 0.5 + vec.z * i);

                res += level.getBlockState(new BlockPos(ix, iy, iz)).getBlock().getExplosionResistance();
            }

            if (res < 1) res = 1;

            float eRads = rads;
            eRads /= res;
            eRads /= (float) (len * len);

            ContaminationUtil.contaminate(e, HazardType.RADIATION, ContaminationType.CREATIVE, eRads);

            if (onFire && len < 5) {
                e.hurt(level.damageSources().onFire(), 2);
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 3,
                worldPosition.getY(),
                worldPosition.getZ() - 3,
                worldPosition.getX() + 4,
                worldPosition.getY() + 3,
                worldPosition.getZ() + 4
        );
    }

    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}