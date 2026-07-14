package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class EntityChopperMine extends Entity {

    public int timer = 0;
    public Entity shooter;

    public EntityChopperMine(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.setSize(12, 12);
        this.setInvulnerable(false);
    }

    public EntityChopperMine(Level level, double x, double y, double z, double moX, double moY, double moZ, Entity shooter) {
        this(ModEntities.CHOPPER_MINE.get(), level);
        this.setPos(x, y, z);
        this.setDeltaMovement(moX, moY, moZ);
        this.shooter = shooter;
        this.setSize(12, 12);
        this.setInvulnerable(true);
    }

    @Override
    protected void defineSynchedData() {
        // Нет данных для синхронизации
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 start = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 end = new Vec3(this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z);

        // Raytrace для поиска препятствий
        BlockHitResult blockHit = this.level().clip(
                new net.minecraft.world.level.ClipContext(start, end,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE,
                        this)
        );

        if (blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            end = new Vec3(blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z);
        }

        // Поиск сущностей на пути
        Entity hitEntity = null;
        double closestDist = 0.0D;

        AABB box = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D);
        List<Entity> entities = this.level().getEntities(this, box);

        for (Entity entity : entities) {
            if (entity.isPickable() && entity != this.shooter) {
                float f1 = 0.3F;
                AABB entityBox = entity.getBoundingBox().inflate(f1);
                Optional<Vec3> entityHit = entityBox.clip(start, end);

                if (entityHit.isPresent()) {
                    double dist = start.distanceToSqr(entityHit.get());
                    if (dist < closestDist || closestDist == 0.0D) {
                        hitEntity = entity;
                        closestDist = dist;
                    }
                }
            }
        }

        if (hitEntity instanceof Player) {
            this.level().explode(this.shooter, this.getX(), this.getY(), this.getZ(), 5.0F, Level.ExplosionInteraction.MOB);
            this.discard();
            return;
        }

        // Звук (каждый тик, но в оригинале тоже)
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.NULL_MINE.get(), net.minecraft.sounds.SoundSource.HOSTILE, 10.0F, 1.0F);

        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        BlockState blockState = this.level().getBlockState(pos);
        FluidState fluidState = this.level().getFluidState(pos);

        if (this.timer >= 100 || (!blockState.isAir() && !fluidState.isEmpty())) {
            this.level().explode(this.shooter, this.getX(), this.getY(), this.getZ(), 5.0F, Level.ExplosionInteraction.MOB);
            this.discard();
            return;
        }

        // Гравитация
        if (this.getDeltaMovement().y > -0.85) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05, 0));
        }

        // Трение
        this.setDeltaMovement(this.getDeltaMovement().x * 0.9,
                this.getDeltaMovement().y,
                this.getDeltaMovement().z * 0.9);

        // Движение
        this.setPos(this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z);

        this.timer++;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        // Нет данных
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        // Нет данных
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }
}