package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IExplosionSFX;
import com.hbm.network.PacketDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExplosionEffectTiny implements IExplosionSFX {

    @Override
    public void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
        if(world.isClientSide()) return;

        // Играем звук взрыва
        world.playSound(null, x, y, z,
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                15.0F, 1.0F);

        // Создаем данные для частиц
        CompoundTag data = new CompoundTag();
        data.putString("type", "explosion_tiny");
        data.putString("mode", "largeexplode");
        data.putFloat("size", 1.5F);
        data.putByte("count", (byte)1);

        // Отправляем пакет частиц
        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world,
                net.minecraft.core.BlockPos.containing(x, y, z));

    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnExplosionTinyEffect(CompoundTag nbt, double x, double y, double z) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return;

        String mode = nbt.getString("mode");

        if("largeexplode".equals(mode)) {
            float size = nbt.getFloat("size");
            byte count = nbt.getByte("count");

            Particle mainExplosion = mc.particleEngine.createParticle(
                    ParticleTypes.EXPLOSION,
                    x, y, z,
                    0, 0, 0
            );

            if (mainExplosion != null) {
                mainExplosion.scale(size/5);
            }

            for(int i = 0; i < count; i++) {

                Particle secondary = mc.particleEngine.createParticle(
                        ParticleTypes.CLOUD,
                        x, y, z,
                        0, 0, 0
                );
                if(secondary != null) {

                    secondary.scale((i + 1) * 0.8f);
                }
            }

        }
    }
}