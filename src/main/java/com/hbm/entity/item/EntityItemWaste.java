package com.hbm.entity.item;

import com.hbm.blocks.ModBlocks;

import com.hbm.handler.radiation.RadiationEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EntityItemWaste extends ItemEntity {

    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(EntityItemWaste.class, EntityDataSerializers.INT);

    public EntityItemWaste(EntityType<? extends EntityItemWaste> type, Level level) {
        super(type, level);
    }

    public EntityItemWaste(Level level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
        this.setPickUpDelay(10);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
    }

    @Override
    public boolean isInvulnerable() {
        return true; // Полная неуязвимость
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false; // Никакой урон не проходит
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            int age = this.entityData.get(AGE);
            this.entityData.set(AGE, age + 1);

            // Радиация
            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            RadiationEvents.incrementRadiation(this.level(), pos, 0.5F);

            if (age > 10 * 60 * 20) {
                ItemStack stack = new ItemStack(ModBlocks.WASTE_TRINITITE.get().asItem());
                this.setItem(stack);
                this.entityData.set(AGE, 0);
            }
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}