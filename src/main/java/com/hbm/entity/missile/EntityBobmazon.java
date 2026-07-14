package com.hbm.entity.missile;

import com.hbm.entity.ModEntities;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.network.PacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EntityBobmazon extends Entity {

    public ItemStack payload;

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(EntityBobmazon.class, EntityDataSerializers.INT);

    public EntityBobmazon(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.setSize(1.0F, 3.0F);
    }

    public EntityBobmazon(Level level) {
        this(ModEntities.BOBMAZON.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(STATE, 0);
    }

    @Override
    public void tick() {
        this.setDeltaMovement(0, -0.5, 0);

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        for (int i = 0; i < 4; i++) {
            BlockPos pos = BlockPos.containing(this.getX() - 0.5, this.getY() + 1, this.getZ() - 0.5);
            BlockState blockState = level().getBlockState(pos);
            FluidState fluidState = level().getFluidState(pos);

            if (!blockState.isAir() && !fluidState.isEmpty() && !level().isClientSide && this.getEntityData().get(STATE) != 1) {
                ExplosionLarge.spawnParticles(level(), this.getX(), this.getY() + 1, this.getZ(), 50);

                level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                        net.minecraft.sounds.SoundSource.PLAYERS, 10.0F, 0.5F + this.random.nextFloat() * 0.1F);

                if (payload != null && !payload.isEmpty()) {
                    ItemEntity pack = new ItemEntity(level(), this.getX(), this.getY() + 2, this.getZ(), payload);
                    pack.setDeltaMovement(0, 0, 0);
                    level().addFreshEntity(pack);
                }

                this.discard();
                break;
            }

            this.setPos(this.getX() + this.getDeltaMovement().x,
                    this.getY() + this.getDeltaMovement().y,
                    this.getZ() + this.getDeltaMovement().z);
        }

        if (level().isClientSide) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "exhaust");
            data.putString("mode", "meteor");
            data.putInt("count", 1);
            data.putDouble("width", 0);
            data.putDouble("posX", this.getX());
            data.putDouble("posY", this.getY() + 1);
            data.putDouble("posZ", this.getZ());

            PacketDispatcher.sendAuxParticleNT(data, this.getX(), this.getY() + 1, this.getZ(), this);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        if (nbt.contains("payload")) {
            this.payload = ItemStack.of(nbt.getCompound("payload"));
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        if (payload != null) {
            nbt.put("payload", payload.save(new CompoundTag()));
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 500000;
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }
}