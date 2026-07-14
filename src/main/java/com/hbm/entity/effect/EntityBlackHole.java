package com.hbm.entity.effect;

import com.hbm.entity.ModEntities;
import com.hbm.entity.projectile.EntityRubble;
import com.hbm.items.ModItems;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class EntityBlackHole extends Entity {

    public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(EntityBlackHole.class, EntityDataSerializers.FLOAT);

    private final Random rand = new Random();

    public EntityBlackHole(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setInvulnerable(true);
    }

    public EntityBlackHole(EntityType<?> type, Level level, float size) {
        this(type, level);
        this.entityData.set(SIZE, size);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SIZE, 0.5F);
    }

    @Override
    public void tick() {
        super.tick();

        float size = this.entityData.get(SIZE);

        if (!this.level().isClientSide && this.level() instanceof ServerLevel) {
            for (int k = 0; k < size * 2; k++) {
                double phi = rand.nextDouble() * (Math.PI * 2);
                double costheta = rand.nextDouble() * 2 - 1;
                double theta = Math.acos(costheta);
                double x = Math.sin(theta) * Math.cos(phi);
                double y = Math.sin(theta) * Math.sin(phi);
                double z = Math.cos(theta);

                Vec3 vec = new Vec3(x, y, z);
                int length = (int) Math.ceil(size * 15);

                for (int i = 0; i < length; i++) {
                    int x0 = (int) (this.getX() + (vec.x * i));
                    int y0 = (int) (this.getY() + (vec.y * i));
                    int z0 = (int) (this.getZ() + (vec.z * i));

                    BlockPos pos = new BlockPos(x0, y0, z0);
                    BlockState state = this.level().getBlockState(pos);

                    if (!state.getFluidState().isEmpty()) {
                        this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }

                    if (!state.isAir()) {
                        EntityRubble rubble = new EntityRubble(ModEntities.RUBBLE.get(), this.level());
                        rubble.setPos(x0 + 0.5, y0, z0 + 0.5);
                        rubble.setMetaBasedOnBlock(state.getBlock(), 0); // meta больше нет, используем 0

                        this.level().addFreshEntity(rubble);
                        this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        break;
                    }
                }
            }
        }

        double range = size * 15;

        AABB box = new AABB(
                this.getX() - range, this.getY() - range, this.getZ() - range,
                this.getX() + range, this.getY() + range, this.getZ() + range
        );

        List<Entity> entities = this.level().getEntities(this, box);

        for (Entity e : entities) {
            if (e instanceof Player player && player.getAbilities().instabuild) {
                continue;
            }

            if (e instanceof FallingBlockEntity falling && !this.level().isClientSide && e.tickCount > 1) {
                double x = e.getX();
                double y = e.getY();
                double z = e.getZ();

                BlockState fallingState = falling.getBlockState();
                Block block = fallingState.getBlock();

                e.discard();

                EntityRubble rubble = new EntityRubble(ModEntities.RUBBLE.get(), this.level());
                rubble.setMetaBasedOnBlock(block, 0);
                rubble.setPos(x, y, z);
                rubble.setDeltaMovement(falling.getDeltaMovement());
                this.level().addFreshEntity(rubble);
            }

            Vec3 vec = new Vec3(
                    this.getX() - e.getX(),
                    this.getY() - e.getY(),
                    this.getZ() - e.getZ()
            );

            double dist = vec.length();

            if (dist > range) continue;

            vec = vec.normalize();

            if (!(e instanceof ItemEntity)) {
                vec = vec.yRot((float) Math.toRadians(15));
            }

            double speed = 0.1D;
            e.setDeltaMovement(e.getDeltaMovement().add(vec.x * speed, vec.y * speed * 2, vec.z * speed));

            if (e instanceof EntityBlackHole) continue;

            if (dist < size * 1.5) {
                DamageSource blackholeSource = ModDamageSource.createDamageSource(
                        ModDamageSource.BLACKHOLE, this, this, this.level());
                e.hurt(blackholeSource, 1000);

                if (!(e instanceof LivingEntity)) {
                    e.discard();
                }

                if (!this.level().isClientSide && e instanceof ItemEntity item) {
                    ItemStack stack = item.getItem();

                    if (stack.getItem() == ModItems.PELLET_ANTIMATTER.get() || stack.getItem() == ModItems.FLAME_PONY.get()) {
                        this.discard();
                        this.level().explode(null, this.getX(), this.getY(), this.getZ(), 5.0F, Level.ExplosionInteraction.MOB);
                        return;
                    }
                }
            }
        }

        this.setPos(this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z);

        this.setDeltaMovement(this.getDeltaMovement().scale(0.99));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(SIZE, tag.getFloat("size"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("size", this.entityData.get(SIZE));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 25000;
    }
}