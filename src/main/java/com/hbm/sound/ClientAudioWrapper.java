package com.hbm.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientAudioWrapper implements AudioWrapper{

    private LoopedSound soundInstance;
    private int keepAlive = 0;
    private boolean doesRepeat = true;

    public ClientAudioWrapper() {
        // Пустой конструктор для серверной части
    }

    public ClientAudioWrapper(SoundEvent sound, float x, float y, float z, float volume, float range, float pitch) {


        if (sound != null) {
            this.soundInstance = new LoopedSound(sound, SoundSource.PLAYERS, volume, pitch, x, y, z, true, range);
        }
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
        if (soundInstance != null) {
            soundInstance.setKeepAlive(keepAlive);
        }
    }

    public void keepAlive() {
        if (soundInstance != null) {
            soundInstance.resetKeepAlive();
        }
    }

    public void updatePosition(float x, float y, float z) {
        if (soundInstance != null) {
            soundInstance.updatePosition(x, y, z);
        }
    }

    public void updateVolume(float volume) {
        if (soundInstance != null) {
            soundInstance.updateVolume(volume);
        }
    }

    public void updateRange(float range) {
        if (soundInstance != null) {
            soundInstance.updateRange(range);
        }
    }

    public void updatePitch(float pitch) {
        if (soundInstance != null) {
            soundInstance.updatePitch(pitch);
        }
    }

    public float getVolume() {
        return soundInstance != null ? soundInstance.getVolume() : 0F;
    }

    public float getRange() {
        return 16F; // или другая логика вычисления
    }

    public float getPitch() {
        return soundInstance != null ? soundInstance.getPitch() : 1F;
    }

    public void setDoesRepeat(boolean repeats) {
        this.doesRepeat = repeats;
        if (soundInstance != null) {
            soundInstance.setLooping(repeats);
        }
    }

    public void startSound() {
        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().play(soundInstance);
        }
    }

    public void stopSound() {
        if (soundInstance != null) {
            soundInstance.stopSound();
        }
    }

    public boolean isPlaying() {
        return soundInstance != null && !soundInstance.isStopped();
    }

    @OnlyIn(Dist.CLIENT)
    private static class LoopedSound extends AbstractTickableSoundInstance {
        private int keepAliveTimer = 10;
        private int initialKeepAlive = 10;
        private boolean isStoppedFlag = false;
        private float maxRange;

        // Текущие значения для плавной интерполяции
        private float targetX, targetY, targetZ;
        private float targetVolume, targetPitch;

        public LoopedSound(SoundEvent sound, SoundSource category, float volume, float pitch, double x, double y, double z, boolean looping, float range) {
            super(sound, category, SoundInstance.createUnseededRandom());
            this.volume = volume;
            this.pitch = pitch;
            this.x = x;
            this.y = y;
            this.z = z;
            this.looping = looping; // Устанавливаем protected поле looping - это допустимо внутри класса
            this.delay = 0;
            this.attenuation = Attenuation.LINEAR;
            this.maxRange = range;

            // Инициализация целевых значений
            this.targetX = (float) x;
            this.targetY = (float) y;
            this.targetZ = (float) z;
            this.targetVolume = volume;
            this.targetPitch = pitch;
        }

        @Override
        public void tick() {
            if (this.isStoppedFlag) {
                return;
            }

            // Плавная интерполяция позиции
            if (Math.abs(this.x - targetX) > 0.01) {
                this.x = this.x + (targetX - (float) this.x) * 0.2;
            }
            if (Math.abs(this.y - targetY) > 0.01) {
                this.y = this.y + (targetY - (float) this.y) * 0.2;
            }
            if (Math.abs(this.z - targetZ) > 0.01) {
                this.z = this.z + (targetZ - (float) this.z) * 0.2;
            }

            // Плавная интерполяция громкости
            if (Math.abs(this.volume - targetVolume) > 0.001) {
                this.volume = this.volume + (targetVolume - this.volume) * 0.2f;
            }

            // Плавная интерполяция тона
            if (Math.abs(this.pitch - targetPitch) > 0.001) {
                this.pitch = this.pitch + (targetPitch - this.pitch) * 0.2f;
            }

            // Проверка keepAlive
            if (keepAliveTimer > 0) {
                keepAliveTimer--;
            } else if (keepAliveTimer == 0) {
                this.stopSound();
            }
        }

        public void setKeepAlive(int keepAlive) {
            this.initialKeepAlive = keepAlive;
            this.keepAliveTimer = keepAlive;
        }

        public void resetKeepAlive() {
            this.keepAliveTimer = this.initialKeepAlive;
        }

        public void updatePosition(float x, float y, float z) {
            this.targetX = x;
            this.targetY = y;
            this.targetZ = z;
        }

        public void updateVolume(float volume) {
            this.targetVolume = volume;
        }

        public void updateRange(float range) {
            // Диапазон можно настроить через attenuation
            this.attenuation = Attenuation.LINEAR;
        }

        public void updatePitch(float pitch) {
            this.targetPitch = pitch;
        }

        public void setLooping(boolean looping) {
            this.looping = looping; // Устанавливаем protected поле looping - это допустимо внутри класса
        }

        public float getVolume() {
            return this.volume;
        }

        public float getPitch() {
            return this.pitch;
        }

        public void stopSound() {
            if (!this.isStoppedFlag) {
                this.isStoppedFlag = true;
                super.stop();
            }
        }

        public boolean isStopped() {
            return this.isStoppedFlag || super.isStopped();
        }
    }
}