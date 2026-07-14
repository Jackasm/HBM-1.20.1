package com.hbm.quests;

import net.minecraft.nbt.CompoundTag;

public class QuestInstance {
    private final String questId;
    private final long startTime;
    private boolean completed;
    private long lastCompletionTime;

    public QuestInstance(String questId) {
        this.questId = questId;
        this.startTime = System.currentTimeMillis();
        this.completed = false;
        this.lastCompletionTime = 0;
    }

    public QuestInstance(String questId, long startTime, boolean completed, long lastCompletionTime) {
        this.questId = questId;
        this.startTime = startTime;
        this.completed = completed;
        this.lastCompletionTime = lastCompletionTime;
    }

    public String getQuestId() { return questId; }
    public long getStartTime() { return startTime; }
    public boolean isCompleted() { return completed; }
    public long getLastCompletionTime() { return lastCompletionTime; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setLastCompletionTime(long time) { this.lastCompletionTime = time; }

    public boolean isExpired(int timeLimit) {
        if (timeLimit <= 0) return false;
        return (System.currentTimeMillis() - startTime) / 1000 >= timeLimit;
    }

    public boolean isOnCooldown(int cooldown) {
        if (cooldown <= 0) return false;
        return (System.currentTimeMillis() - lastCompletionTime) / 1000 < cooldown;
    }

    public long getTimeRemaining(int timeLimit) {
        if (timeLimit <= 0) return -1;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(0, timeLimit - elapsed);
    }

    public long getCooldownRemaining(int cooldown) {
        if (cooldown <= 0) return 0;
        long elapsed = (System.currentTimeMillis() - lastCompletionTime) / 1000;
        return Math.max(0, cooldown - elapsed);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("questId", questId);
        tag.putLong("startTime", startTime);
        tag.putBoolean("completed", completed);
        tag.putLong("lastCompletionTime", lastCompletionTime);
        return tag;
    }

    public static QuestInstance load(CompoundTag tag) {
        return new QuestInstance(
                tag.getString("questId"),
                tag.getLong("startTime"),
                tag.getBoolean("completed"),
                tag.getLong("lastCompletionTime")
        );
    }
}