package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IExplosionSFX;
import com.hbm.network.PacketDispatcher;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class ExplosionEffectAmat implements IExplosionSFX {

    @Override
    public void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
        if(world.isClientSide()) return;

        if(size < 15) {
            world.playSound(null, x, y, z,
                    SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                    4.0F, (1.4F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
        } else {
            // Используйте кастомный звук, если он есть
            world.playSound(null, x, y, z,
                    SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 15.0F, 1.0F);
        }

        // Отправка пакета для клиентских эффектов
        CompoundTag data = new CompoundTag();
        data.putString("type", "amat");
        data.putFloat("scale", size);

        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world,
                net.minecraft.core.BlockPos.containing(x, y, z));
    }
}