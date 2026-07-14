package com.hbm.quests;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QuestWorldData extends SavedData {
    private final Map<UUID, Map<String, QuestInstance>> playerQuests = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> completedOneTimeQuests = new ConcurrentHashMap<>();

    public QuestWorldData() {}

    public static QuestWorldData load(CompoundTag nbt) {
        QuestWorldData data = new QuestWorldData();
        CompoundTag playersTag = nbt.getCompound("players");

        for (String uuidStr : playersTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            CompoundTag playerData = playersTag.getCompound(uuidStr);

            // Загрузка активных квестов
            Map<String, QuestInstance> quests = new ConcurrentHashMap<>();
            ListTag activeList = playerData.getList("activeQuests", 10);
            for (int i = 0; i < activeList.size(); i++) {
                QuestInstance inst = QuestInstance.load(activeList.getCompound(i));
                quests.put(inst.getQuestId(), inst);
            }
            data.playerQuests.put(uuid, quests);

            // Загрузка выполненных квестов
            Set<String> completed = ConcurrentHashMap.newKeySet();
            ListTag completedList = playerData.getList("completedOneTime", 10);
            for (int i = 0; i < completedList.size(); i++) {
                completed.add(completedList.getCompound(i).getString("questId"));
            }
            data.completedOneTimeQuests.put(uuid, completed);
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        CompoundTag playersTag = new CompoundTag();

        for (Map.Entry<UUID, Map<String, QuestInstance>> entry : playerQuests.entrySet()) {
            CompoundTag playerData = new CompoundTag();

            // Сохраняем активные квесты
            ListTag activeList = new ListTag();
            for (QuestInstance inst : entry.getValue().values()) {
                if (!inst.isCompleted()) {
                    activeList.add(inst.save());
                }
            }
            playerData.put("activeQuests", activeList);

            // Сохраняем выполненные квесты
            ListTag completedList = new ListTag();
            Set<String> completed = completedOneTimeQuests.get(entry.getKey());
            if (completed != null) {
                for (String qid : completed) {
                    CompoundTag t = new CompoundTag();
                    t.putString("questId", qid);
                    completedList.add(t);
                }
            }
            playerData.put("completedOneTime", completedList);

            playersTag.put(entry.getKey().toString(), playerData);
        }

        nbt.put("players", playersTag);
        return nbt;
    }

    public Map<String, QuestInstance> getPlayerQuests(UUID uuid) {
        return playerQuests.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
    }

    public Set<String> getCompletedOneTimeQuests(UUID uuid) {
        return completedOneTimeQuests.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet());
    }
}