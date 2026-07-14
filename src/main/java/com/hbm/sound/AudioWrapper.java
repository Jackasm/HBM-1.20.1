package com.hbm.sound;

public interface AudioWrapper {
    void setKeepAlive(int keepAlive);
    void keepAlive();
    void updatePosition(float x, float y, float z);
    void updateVolume(float volume);
    void updateRange(float range);
    void updatePitch(float pitch);
    float getVolume();
    float getRange();
    float getPitch();
    void setDoesRepeat(boolean repeats);
    void startSound();
    void stopSound();
    boolean isPlaying();
}