package com.hbm.entity.item;

import com.hbm.blocks.ModBlocks;

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
        return true; // РџРѕР»РЅР°СЏ РЅРµСѓСЏР·РІРёРјРѕСЃС‚СЊ
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false; // РќРёРєР°РєРѕР№ СѓСЂРѕРЅ РЅРµ РїСЂРѕС…РѕРґРёС‚
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            int age = this.entityData.get(AGE);
            this.entityData.set(AGE, age + 1);

            // Р Р°РґРёР°С†РёРё РѕС‚ Р»РµР¶Р°С‰РµРіРѕ РјСѓСЃРѕСЂР° РЅРµС‚: РІ 1.7.10 EntityItemWaste С‚РѕР»СЊРєРѕ
            // Р±РµСЃСЃРјРµСЂС‚РµРЅ, Р° РїРѕР»Рµ РґР°С‘С‚ РїСЂРµРґРјРµС‚ РІ РёРЅРІРµРЅС‚Р°СЂРµ Рё Р±Р»РѕРє РїРѕСЃР»Рµ РѕСЃС‚С‹РІР°РЅРёСЏ.
            // РџРѕСЂС‚ РґРѕР±Р°РІР»СЏР» +0.5 РІ С‚РёРє СЃРІРµСЂС… РѕСЂРёРіРёРЅР°Р»Р° (#61).

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