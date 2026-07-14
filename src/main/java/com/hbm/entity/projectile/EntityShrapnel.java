package com.hbm.entity.projectile;

import com.hbm.blocks.ModBlocks;

import com.hbm.entity.ModEntities;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityShrapnel extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Byte> TRAIL_TYPE = SynchedEntityData.defineId(EntityShrapnel.class, EntityDataSerializers.BYTE);
    public static final byte TYPE_NORMAL = 0;
    public static final byte TYPE_TRAIL = 1;
    public static final byte TYPE_VOLCANO = 2;
    public static final byte TYPE_WATZ = 3;
    public static final byte TYPE_RAD_VOLCANO = 4;

    public EntityShrapnel(EntityType<? extends EntityShrapnel> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.blocksBuilding = true;
    }

    public EntityShrapnel(Level level, LivingEntity shooter) {
        super(ModEntities.SHRAPNEL.get(), shooter, level);
        this.setNoGravity(true);
        this.blocksBuilding = true;
    }

    public EntityShrapnel(Level level, double x, double y, double z) {
        super(ModEntities.SHRAPNEL.get(), level);
        this.setPos(x, y, z);
        this.setNoGravity(true);
        this.blocksBuilding = true;
    }

    public EntityShrapnel(Level level)
    {
        super(ModEntities.SHRAPNEL.get(), level);
    }

    public byte getShrapnelType() {
        return this.entityData.get(TRAIL_TYPE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRAIL_TYPE, (byte) 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide && this.getTrailType() == 1) {
            level().addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                    getX(), getY(), getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) result;
            if (entityHit.getEntity() != null) {
                int damage = 15;
                entityHit.getEntity().hurt(ModDamageSource.createDamageSource(ModDamageSource.SHRAPNEL, this, getOwner(), level()), damage);
            }
        }

        if (this.tickCount > 5) {
            BlockHitResult blockHit = null;
            if (result.getType() == HitResult.Type.BLOCK) {
                blockHit = (BlockHitResult) result;
            }

            int type = this.getTrailType();

            if (type == 2 || type == 4) { // Volcano или Rad Volcano
                if (!level().isClientSide) {
                    // Проверяем направление движения вниз
                    if (this.getDeltaMovement().y < -0.2D && blockHit != null) {
                        BlockPos pos = blockHit.getBlockPos();
                        BlockPos abovePos = pos.above();

                        if (level().getBlockState(abovePos).canBeReplaced()) {
                            level().setBlock(abovePos,
                                    type == 2 ? ModBlocks.VOLCANIC_LAVA_BLOCK.get().defaultBlockState()
                                            : ModBlocks.RAD_LAVA_BLOCK.get().defaultBlockState(), 3);
                        }

                        // Заполняем область угарным газом
                        for (int x = -1; x <= 1; x++) {
                            for (int y = 0; y <= 2; y++) {
                                for (int z = -1; z <= 1; z++) {
                                    BlockPos checkPos = pos.offset(x, y, z);
                                    if (level().getBlockState(checkPos).isAir()) {
                                        level().setBlock(checkPos, ModBlocks.GAS_MONOXIDE.get().defaultBlockState(), 3);
                                    }
                                }
                            }
                        }
                    }

                    if (this.getDeltaMovement().y > 0 && blockHit != null) {
                        BlockPos pos = blockHit.getBlockPos();
                        ExplosionVNT explosion = new ExplosionVNT(level(),
                                pos.getX() + 0.5,   // x
                                pos.getY() + 0.5,   // y
                                pos.getZ() + 0.5,   // z
                                7.0f,               // size (float, не int)
                                null                // exploder (Entity)
                        );

                        // Собираем атрибуты
                        List<ExplosionVNT.ExAttrib> attribs = new ArrayList<>();
                        attribs.add(type == 2 ? ExplosionVNT.ExAttrib.LAVA_V : ExplosionVNT.ExAttrib.LAVA_R);
                        attribs.add(ExplosionVNT.ExAttrib.NODROP);
                        attribs.add(ExplosionVNT.ExAttrib.NOSOUND);
                        attribs.add(ExplosionVNT.ExAttrib.ALLMOD);
                        attribs.add(ExplosionVNT.ExAttrib.NOHURT);

                        explosion.addAllAttrib(attribs);
                        explosion.makeStandard(); // Инициализируем стандартными процессорами
                        explosion.explode();
                    }
                }
            } else if (type == 3) { // Watz
                if (!level().isClientSide && blockHit != null) {
                    BlockPos pos = blockHit.getBlockPos();
                    BlockPos abovePos = pos.above();
                    if (level().getBlockState(abovePos).canBeReplaced()) {
                        level().setBlock(abovePos, ModBlocks.MUD_BLOCK.get().defaultBlockState(), 3);
                    }
                }
            } else {
                // Обычный осколок - эффект лавы
                if (level().isClientSide) {
                    for (int i = 0; i < 5; i++) {
                        level().addParticle(net.minecraft.core.particles.ParticleTypes.LAVA,
                                getX(), getY(), getZ(), 0.0, 0.0, 0.0);
                    }
                }
            }

            // Звук
            if (!level().isClientSide) {
                level().playSound(null, blockHit != null ? blockHit.getBlockPos() : this.blockPosition(),
                        SoundEvents.LAVA_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            this.discard();
        }
    }

    private int getTrailType() {
        return this.entityData.get(TRAIL_TYPE);
    }

    private void setTrailType(int type) {
        this.entityData.set(TRAIL_TYPE, (byte) type);
    }

    public void setTrail(boolean b) {
        setTrailType(b ? 1 : 0);
    }

    public void setVolcano(boolean b) {
        setTrailType(b ? 2 : 0);
    }

    public void setWatz(boolean b) {
        setTrailType(b ? 3 : 0);
    }

    public void setRadVolcano(boolean b) {
        setTrailType(b ? 4 : 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setTrailType(tag.getByte("trailType"));
        this.discard(); // Оригинальное поведение
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("trailType", (byte) getTrailType());
    }

    @Override
    protected net.minecraft.world.item.@NotNull Item getDefaultItem() {
        return null; // Не используется, так как это не предметный снаряд
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }
}