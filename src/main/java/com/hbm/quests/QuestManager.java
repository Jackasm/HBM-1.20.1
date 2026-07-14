package com.hbm.quests;

import com.hbm.config.GeneralConfig;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemGuideBook;
import com.hbm.network.PacketDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class QuestManager {

    private static final String FIRST_JOIN_TAG = "firstJoin";
    private static final String WORLD_DATA_KEY = "hbm_quests";

    private static final Map<String, QuestInstance> CLIENT_ACTIVE_QUESTS = new ConcurrentHashMap<>();
    private static final Set<String> CLIENT_COMPLETED_QUESTS = ConcurrentHashMap.newKeySet();

    public static void init() {
        HBMQuests.registerQuests();
    }

    // Получение мировых данных
    private static QuestWorldData getWorldData(Level level) {
        if (level.isClientSide) {
            return null;
        }
        return Objects.requireNonNull(level.getServer()).overworld().getDataStorage()
                .computeIfAbsent(QuestWorldData::load, QuestWorldData::new, WORLD_DATA_KEY);
    }

    // Клиентские методы
    public static void addLocalActiveQuest(String questId) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level().isClientSide()) {
            CLIENT_ACTIVE_QUESTS.put(questId, new QuestInstance(questId));
        }
    }

    public static void removeLocalActiveQuest(String questId) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level().isClientSide()) {
            CLIENT_ACTIVE_QUESTS.remove(questId);
        }
    }

    public static void completeLocalQuest(String questId) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level().isClientSide()) {
            QuestDefinition def = HBMQuests.getQuest(questId);
            if (def != null && def.isOneTimeOnly()) {
                CLIENT_COMPLETED_QUESTS.add(questId);
            }
            CLIENT_ACTIVE_QUESTS.remove(questId);
        }
    }

    public static void syncQuestsForClient(Map<String, QuestInstance> activeQuests, Set<String> completedQuests) {
        CLIENT_ACTIVE_QUESTS.clear();
        CLIENT_ACTIVE_QUESTS.putAll(activeQuests);
        CLIENT_COMPLETED_QUESTS.clear();
        CLIENT_COMPLETED_QUESTS.addAll(completedQuests);
    }

    public static Map<String, QuestInstance> getClientPlayerQuests() {
        return new HashMap<>(CLIENT_ACTIVE_QUESTS);
    }

    public static List<QuestDefinition> getAvailableQuestsClient() {
        List<QuestDefinition> available = new ArrayList<>();
        for (QuestDefinition quest : HBMQuests.getAllQuests()) {
            if (quest.isOneTimeOnly()) {
                if (!CLIENT_COMPLETED_QUESTS.contains(quest.getId()) && !CLIENT_ACTIVE_QUESTS.containsKey(quest.getId())) {
                    available.add(quest);
                }
            } else {
                QuestInstance inst = CLIENT_ACTIVE_QUESTS.get(quest.getId());
                if (inst == null || inst.isCompleted()) {
                    available.add(quest);
                }
            }
        }
        return available;
    }

    public static List<QuestInstance> getActiveQuestsClient() {
        List<QuestInstance> active = new ArrayList<>();
        for (QuestInstance inst : CLIENT_ACTIVE_QUESTS.values()) {
            if (!inst.isCompleted()) {
                QuestDefinition def = HBMQuests.getQuest(inst.getQuestId());
                if (def != null && !inst.isExpired(def.getTimeLimit())) active.add(inst);
            }
        }
        return active;
    }

    public static void assignQuest(Player player, String questId) {
        if (!GeneralConfig.enableQuestsValue) return;
        if (player.level().isClientSide()) return;

        QuestDefinition def = HBMQuests.getQuest(questId);
        if (def == null) return;

        QuestWorldData worldData = getWorldData(player.level());
        if (worldData == null) return;

        UUID pid = player.getUUID();

        if (def.isOneTimeOnly()) {
            Set<String> completed = worldData.getCompletedOneTimeQuests(pid);
            if (completed.contains(questId)) return;
        }

        Map<String, QuestInstance> playerQuests = worldData.getPlayerQuests(pid);
        long activeCount = playerQuests.values().stream().filter(i -> !i.isCompleted()).count();
        if (activeCount >= GeneralConfig.maxActiveQuestsValue) return;

        QuestInstance existing = playerQuests.get(questId);
        if (!def.isOneTimeOnly() && existing != null && !existing.isCompleted() && existing.isOnCooldown(def.getCooldown())) {
            return;
        }

        playerQuests.put(questId, new QuestInstance(questId));
        worldData.setDirty();
        syncToClient(player);

        Component message = Component.literal("§6[Quest] §r")
                .append(Component.translatable(def.getTitleKey()).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" §7- quest accepted"));
        player.sendSystemMessage(message);

        player.playSound(SoundEvents.NOTE_BLOCK_BELL.get(), 1.0F, 1.0F);
    }

    public static void completeQuest(Player player, String questId) {
        if (!GeneralConfig.enableQuestsValue) return;
        if (player.level().isClientSide()) return;

        QuestWorldData worldData = getWorldData(player.level());
        if (worldData == null) return;

        UUID pid = player.getUUID();
        Map<String, QuestInstance> playerQuests = worldData.getPlayerQuests(pid);
        if (playerQuests == null) return;

        QuestInstance instance = playerQuests.get(questId);
        if (instance == null || instance.isCompleted()) return;

        QuestDefinition def = HBMQuests.getQuest(questId);
        if (def == null) return;

        if (instance.isExpired(def.getTimeLimit())) {
            playerQuests.remove(questId);
            worldData.setDirty();
            syncToClient(player);
            return;
        }

        if (def.checkCompletion(player, instance)) {
            def.giveRewards(player);
            instance.setCompleted(true);

            if (def.isOneTimeOnly()) {
                worldData.getCompletedOneTimeQuests(pid).add(questId);
                playerQuests.remove(questId);
            } else {
                instance.setLastCompletionTime(System.currentTimeMillis());
            }
            worldData.setDirty();
            syncToClient(player);
        }
    }

    public static void abandonQuest(Player player, String questId) {
        if (!GeneralConfig.enableQuestsValue) return;
        if (player.level().isClientSide()) return;

        QuestWorldData worldData = getWorldData(player.level());
        if (worldData == null) return;

        UUID pid = player.getUUID();
        Map<String, QuestInstance> playerQuests = worldData.getPlayerQuests(pid);
        if (playerQuests == null) return;

        QuestInstance instance = playerQuests.get(questId);
        if (instance == null) return;

        QuestDefinition def = HBMQuests.getQuest(questId);
        if (def == null) return;

        playerQuests.remove(questId);
        worldData.setDirty();
        syncToClient(player);
    }

    public static List<QuestDefinition> getAvailableQuests(Player player) {
        if (player.level().isClientSide()) {
            return getAvailableQuestsClient();
        }

        UUID pid = player.getUUID();
        QuestWorldData worldData = getWorldData(player.level());
        if (worldData == null) return new ArrayList<>();

        Set<String> completedOneTime = worldData.getCompletedOneTimeQuests(pid);
        Map<String, QuestInstance> activeQuests = worldData.getPlayerQuests(pid);

        List<QuestDefinition> available = new ArrayList<>();
        for (QuestDefinition quest : HBMQuests.getAllQuests()) {
            if (quest.isOneTimeOnly()) {
                if (!completedOneTime.contains(quest.getId()) && !activeQuests.containsKey(quest.getId())) {
                    available.add(quest);
                }
            } else {
                QuestInstance inst = activeQuests.get(quest.getId());
                if (inst == null || inst.isCompleted()) {
                    available.add(quest);
                }
            }
        }
        return available;
    }

    public static List<QuestInstance> getActiveQuests(Player player) {
        if (player.level().isClientSide()) {
            return getActiveQuestsClient();
        }

        UUID pid = player.getUUID();
        QuestWorldData worldData = getWorldData(player.level());
        if (worldData == null) return new ArrayList<>();

        Map<String, QuestInstance> playerQuests = worldData.getPlayerQuests(pid);
        List<QuestInstance> active = new ArrayList<>();
        for (QuestInstance inst : playerQuests.values()) {
            if (!inst.isCompleted()) {
                QuestDefinition def = HBMQuests.getQuest(inst.getQuestId());
                if (def != null && !inst.isExpired(def.getTimeLimit())) active.add(inst);
            }
        }
        return active;
    }

    public static boolean hasActiveQuest(Player player, String questId) {
        if (player.level().isClientSide()) {
            return CLIENT_ACTIVE_QUESTS.containsKey(questId);
        }

        QuestWorldData worldData = getWorldData(player.level());
        if (worldData == null) return false;

        Map<String, QuestInstance> playerQuests = worldData.getPlayerQuests(player.getUUID());
        if (playerQuests == null) return false;
        QuestInstance inst = playerQuests.get(questId);
        return inst != null && !inst.isCompleted();
    }

    public static boolean hasCompletedQuest(Player player, String questId) {
        if (player.level().isClientSide()) {
            return CLIENT_COMPLETED_QUESTS.contains(questId);
        }

        QuestWorldData worldData = getWorldData(player.level());
        if (worldData == null) return false;

        UUID pid = player.getUUID();
        QuestDefinition def = HBMQuests.getQuest(questId);
        if (def == null) return false;

        if (def.isOneTimeOnly()) {
            return worldData.getCompletedOneTimeQuests(pid).contains(questId);
        } else {
            Map<String, QuestInstance> playerQuests = worldData.getPlayerQuests(pid);
            QuestInstance inst = playerQuests != null ? playerQuests.get(questId) : null;
            return inst != null && inst.isCompleted();
        }
    }

    private static void syncToClient(Player player) {
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            QuestWorldData worldData = getWorldData(player.level());
            if (worldData != null) {
                Map<String, QuestInstance> active = worldData.getPlayerQuests(player.getUUID());
                Set<String> completed = worldData.getCompletedOneTimeQuests(player.getUUID());
                PacketDispatcher.sendQuestSync(serverPlayer, active, completed);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            QuestWorldData worldData = getWorldData(player.level());
            if (worldData != null) {
                Map<String, QuestInstance> active = worldData.getPlayerQuests(player.getUUID());
                Set<String> completed = worldData.getCompletedOneTimeQuests(player.getUUID());
                PacketDispatcher.sendQuestSync(serverPlayer, active, completed);
            }

            HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(serverPlayer);
            if (props != null) {
                // Синхронизируем разблокированные крафтеры при входе
                PacketDispatcher.sendCrafterSync(serverPlayer, props.getUnlockedCrafters());
            }
        }

        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(FIRST_JOIN_TAG)) {
            persistentData.putBoolean(FIRST_JOIN_TAG, true);
            giveStarterItems(player);
        }
    }

    private static void giveStarterItems(Player player) {
        ItemStack guideBook = new ItemStack(ModItems.BOOK_GUIDE.get());
        guideBook.setDamageValue(ItemGuideBook.BookType.TEST.ordinal());
        if (!player.getInventory().add(guideBook)) {
            player.drop(guideBook, false);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!GeneralConfig.enableQuestsValue) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;

        for (QuestInstance inst : getActiveQuests(player)) {
            QuestDefinition def = HBMQuests.getQuest(inst.getQuestId());
            if (def != null && !inst.isCompleted()) {
                if (inst.isExpired(def.getTimeLimit())) {
                    QuestWorldData worldData = getWorldData(player.level());
                    if (worldData != null) {
                        worldData.getPlayerQuests(player.getUUID()).remove(inst.getQuestId());
                        worldData.setDirty();
                    }
                } else {
                    completeQuest(player, inst.getQuestId());
                }
            }
        }
    }

    public static Map<String, QuestInstance> getPlayerQuests(Player player) {
        if (player.level().isClientSide()) {
            return CLIENT_ACTIVE_QUESTS;
        }

        QuestWorldData worldData = getWorldData(player.level());
        if (worldData == null) return new HashMap<>();

        return worldData.getPlayerQuests(player.getUUID());
    }
}