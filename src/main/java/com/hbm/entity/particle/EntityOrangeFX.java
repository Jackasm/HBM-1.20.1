package com.hbm.entity.particle;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityOrangeFX extends EntityModFX {

    public EntityOrangeFX(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EntityOrangeFX(Level level, double x, double y, double z, double mx, double my, double mz) {
        this(ModEntities.ORANGE_FX.get(), level, x, y, z, mx, my, mz, 1.0F);
    }

    public EntityOrangeFX(EntityType<?> type, Level level, double x, double y, double z, double mx, double my, double mz, float scale) {
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

    // Конструктор для спавна через Forge Network
    public EntityOrangeFX(net.minecraftforge.network.PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType<? extends EntityOrangeFX>) BuiltInRegistries.ENTITY_TYPE.byId(spawnEntity.getTypeId()), level);
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
            ExplosionChaos.poison(this.level(), pos.getX(), pos.getY(), pos.getZ(), 2);
        }

        this.particleAge++;

        if (this.particleAge >= maxAge) {
            this.discard();
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x * 0.86, motion.y * 0.86, motion.z * 0.86);
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1, 0));

        double subdivisions = 4;

        for (int i = 0; i < subdivisions; i++) {
            this.setPos(
                    this.getX() + motion.x / subdivisions,
                    this.getY() + motion.y / subdivisions,
                    this.getZ() + motion.z / subdivisions
            );

            BlockPos blockPos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            BlockState state = this.level().getBlockState(blockPos);

            if (!state.isAir()) {
                this.discard();

                for (int a = -1; a < 2; a++) {
                    for (int b = -1; b < 2; b++) {
                        for (int c = -1; c < 2; c++) {
                            BlockPos targetPos = blockPos.offset(a, b, c);
                            BlockState targetState = this.level().getBlockState(targetPos);

                            if (targetState.is(Blocks.GRASS)) {
                                this.level().setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                            } else {
                                ExplosionNukeGeneric.solinium(this.level(), targetPos);
                            }
                        }
                    }
                }
                return;
            }
        }
    }

    @Override
    public boolean save(CompoundTag tag) {
        return false;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.discard();
    }
}