package com.hbm.sound;

public class ServerAudioWrapper implements AudioWrapper {
    // Простая заглушка для сервера
    @Override public void setKeepAlive(int keepAlive) { }
    @Override public void keepAlive() { }
    @Override public void updatePosition(float x, float y, float z) { }
    @Override public void updateVolume(float volume) { }
    @Override public void updateRange(float range) { }
    @Override public void updatePitch(float pitch) { }
    @Override public float getVolume() { return 0F; }
    @Override public float getRange() { return 0F; }
    @Override public float getPitch() { return 1F; }
    @Override public void setDoesRepeat(boolean repeats) { }
    @Override public void startSound() { }
    @Override public void stopSound() { }
    @Override public boolean isPlaying() { return false; }
}