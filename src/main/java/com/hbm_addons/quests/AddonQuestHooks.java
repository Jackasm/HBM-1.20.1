package com.hbm_addons.quests;

import com.hbm.api.quest.QuestHooks;
import com.hbm.network.PacketDispatcher;
import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.network.server.QuestActionPacket;
import net.minecraft.world.entity.player.Player;

/** Реализация шва {@link QuestHooks} на стороне аддона. */
public final class AddonQuestHooks implements QuestHooks {

    @Override
    public boolean isAvailable() {
        return AddonConfig.enableQuests;
    }

    @Override
    public boolean hasActiveQuest(Player player, String questId) {
        return AddonConfig.enableQuests && QuestManager.hasActiveQuest(player, questId);
    }

    @Override
    public boolean hasCompletedQuest(Player player, String questId) {
        return AddonConfig.enableQuests && QuestManager.hasCompletedQuest(player, questId);
    }

    @Override
    public void accept(Player player, String questId) {
        if (!AddonConfig.enableQuests || player == null || questId == null) return;
        if (!player.level().isClientSide()) return;

        PacketDispatcher.sendToServer(new QuestActionPacket(QuestActionPacket.Action.ACCEPT, questId));
        QuestManager.addLocalActiveQuest(questId);
    }

    @Override
    public void complete(Player player, String questId) {
        if (!AddonConfig.enableQuests || player == null || questId == null) return;
        if (!player.level().isClientSide()) return;

        PacketDispatcher.sendToServer(new QuestActionPacket(QuestActionPacket.Action.COMPLETE, questId));
    }
}
