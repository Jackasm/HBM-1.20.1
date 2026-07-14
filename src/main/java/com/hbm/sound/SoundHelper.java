package com.hbm.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hbm.util.ResLocation.ResLocation;

public class SoundHelper {

    // === СТАТИЧЕСКИЕ МЕТОДЫ ДЛЯ ЗВУКОВ ===

    /**
     * Проигрывает звук в мире (серверная сторона)
     */
    public static void playSound(Level world, double x, double y, double z,
                                 SoundEvent sound, SoundSource category,
                                 float volume, float pitch) {
        if (!world.isClientSide && sound != null) {
            world.playSound(null, x, y, z, sound, category, volume, pitch);
        }
    }

    /**
     * Проигрывает звук в позиции сущности
     */
    public static void playSound(LivingEntity entity, SoundEvent sound,
                                 float volume, float pitch) {
        if (!entity.level().isClientSide && sound != null) {
            entity.level().playSound(null,
                    entity.getX(),
                    entity.getY() + entity.getEyeHeight(),
                    entity.getZ(),
                    sound,
                    entity.getSoundSource(),
                    volume,
                    pitch);
        }
    }

    /**
     * Проигрывает звук по имени (использует ModSounds)
     */
    public static void playSound(LivingEntity entity, String soundName,
                                 float volume, float pitch) {
        if (!entity.level().isClientSide) {
            SoundEvent sound = ModSounds.getCachedSound(soundName);
            if (sound != null) {
                playSound(entity, sound, volume, pitch);
            } else {
                System.err.println("[HBM] Sound not found: " + soundName);
            }
        }
    }

    /**
     * Проигрывает звук в блоке
     */
    public static void playSound(Level world, BlockPos pos,
                                 SoundEvent sound, SoundSource category,
                                 float volume, float pitch) {
        playSound(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                sound, category, volume, pitch);
    }


    // === МЕТОДЫ ДЛЯ ЗАЦИКЛЕННЫХ ЗВУКОВ ===

    /**
     * Создает зацикленный звук (только клиент)
     */
    @OnlyIn(Dist.CLIENT)
    public static AudioWrapper createLoopedSound(SoundEvent soundName,
                                                 float x, float y, float z,
                                                 float volume, float range,
                                                 float pitch, int keepAlive) {
        ClientAudioWrapper wrapper = new ClientAudioWrapper(soundName, x, y, z, volume, range, pitch);
        wrapper.setKeepAlive(keepAlive);
        return wrapper;
    }

    /**
     * Создает зацикленный звук привязанный к сущности
     */
    @OnlyIn(Dist.CLIENT)
    public static AudioWrapper createEntityLoopedSound(Entity entity,
                                                             SoundEvent sound,
                                                             float volume, float range,
                                                             float pitch, int keepAlive) {
        return createLoopedSound(sound,
                (float) entity.getX(),
                (float) entity.getY() + entity.getEyeHeight(),
                (float) entity.getZ(),
                volume, range, pitch, keepAlive);
    }

    public static void playSoundClient(double x, double y, double z, SoundEvent sound, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(sound, pitch)
        );
    }
}