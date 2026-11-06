package com.hbm_addons.network;

import com.hbm.network.PacketHandler;
import com.hbm_addons.network.client.QuestSyncPacket;
import com.hbm_addons.network.server.QuestActionPacket;
import com.hbm_addons.quests.QuestInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

import java.util.Map;
import java.util.Set;

/** Сетевые пакеты аддона: регистрация и отправка. */
public final class AddonNetwork {

    private AddonNetwork() {
    }

    /**
     * Вызывается после PacketHandler.register(), чтобы id шли следом за
     * пакетами мода и совпадали на клиенте и сервере.
     */
    public static void registerPackets() {
        PacketHandler.registerPacket(QuestSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        PacketHandler.registerPacket(QuestActionPacket.class, NetworkDirection.PLAY_TO_SERVER);
    }

    public static void sendQuestSync(ServerPlayer player, Map<String, QuestInstance> activeQuests,
                                     Set<String> completedQuests) {
        PacketHandler.sendToPlayer(new QuestSyncPacket(activeQuests, completedQuests), player);
    }
}
