package com.hbm.entity.particle;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.ModEntities;
import com.hbm.explosion.ExplosionChaos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityPinkCloudFX extends EntityModFX {

    public EntityPinkCloudFX(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EntityPinkCloudFX(EntityType<?> type, Level level, double x, double y, double z, double mx, double my, double mz) {
        this(type, level, x, y, z, mx, my, mz, 1.0F);
    }

    public EntityPinkCloudFX(Level level, double x, double y, double z, double mx, double my, double mz) {
        this(ModEntities.PINK_CLOUD_FX.get(), level, x, y, z, mx, my, mz);
    }

    public EntityPinkCloudFX(EntityType<?> type, Level level, double x, double y, double z, double mx, double my, double mz, float scale) {
        super(type, level, x, y, z, 0.0, 0.0, 0.0);

        this.setDeltaMovement(
                this.getDeltaMovement().x * 0.1 + mx,
                this.getDeltaMovement().y * 0.1 + my,
                this.getDeltaMovement().z * 0.1 + mz
        );

        this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.3);
        this.particleScale *= 0.75F * scale;
        this.noPhysics = false;
    }

    @Override
    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        if (maxAge < 900) {
            maxAge = random.nextInt(301) + 900;
        }

        if (!this.level().isClientSide && random.nextInt(50) == 0) {
            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            ExplosionChaos.pinkCloud(this.level(), pos.getX(), pos.getY(), pos.getZ(), 2);
        }

        this.particleAge++;

        if (this.particleAge >= maxAge) {
            this.discard();
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x * 0.76, motion.y * 0.76, motion.z * 0.76);

        if (this.onGround()) {
            motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x * 0.7, motion.y, motion.z * 0.7);
        }

        if (this.level().isRaining() && this.level().canSeeSky(BlockPos.containing(this.getX(), this.getY(), this.getZ()))) {
            motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, motion.y - 0.01, motion.z);
        }

        double subdivisions = 4;

        for (int i = 0; i < subdivisions; i++) {
            this.setPos(
                    this.getX() + motion.x / subdivisions,
                    this.getY() + motion.y / subdivisions,
                    this.getZ() + motion.z / subdivisions
            );

            BlockPos blockPos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            BlockState state = this.level().getBlockState(blockPos);

            if (state.is(ModBlocks.RADIOREC.get())) {
                this.discard();
                int meta = 0; // метаданные больше не используются
                this.level().setBlock(blockPos, ModBlocks.BROADCASTER_PC.get().defaultBlockState(), 2);
                return;
            }

            if (state.isSolidRender(this.level(), blockPos)) {
                if (random.nextInt(5) != 0) {
                    this.discard();
                    return;
                }

                this.setPos(
                        this.getX() - motion.x / subdivisions,
                        this.getY() - motion.y / subdivisions,
                        this.getZ() - motion.z / subdivisions
                );

                this.setDeltaMovement(0, 0, 0);
                break;
            }
        }
    }
}