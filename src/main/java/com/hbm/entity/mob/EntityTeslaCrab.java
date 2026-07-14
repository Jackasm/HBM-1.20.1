package com.hbm.entity.mob;

import com.hbm.items.ModItems;
import com.hbm.tileentity.machine.TileEntityTesla;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityTeslaCrab extends EntityCyberCrab {

    public List<double[]> targets = new ArrayList<>();

    public EntityTeslaCrab(EntityType<? extends EntityCyberCrab> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityCyberCrab.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            targets = TileEntityTesla.zap(this.level(), this.getX(), this.getY() + 1, this.getZ(), 3, this);
        }

        super.tick();
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        // Редкий дроп
        if (this.random.nextInt(100) == 0) {
            this.spawnAtLocation(new ItemStack(ModItems.COIL_COPPER.get()));
        }
    }

    @Override
    protected void dropEquipment() {
        // Можно добавить дополнительный дроп
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Загрузка данных при необходимости
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        // Сохранение данных при необходимости
    }
}