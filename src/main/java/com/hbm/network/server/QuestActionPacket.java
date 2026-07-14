package com.hbm.network.server;

import com.hbm.network.PacketBase;
import com.hbm.quests.QuestManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class QuestActionPacket extends PacketBase implements PacketBase.DecodablePacket {

    public enum Action {
        ACCEPT,
        ABANDON,
        COMPLETE
    }

    private Action action;
    private String questId;

    public QuestActionPacket() {}

    public QuestActionPacket(Action action, String questId) {
        this.action = action;
        this.questId = questId;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        Action action = Action.values()[buf.readByte()];
        String questId = buf.readUtf();
        return new QuestActionPacket(action, questId);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(action.ordinal());
        buf.writeUtf(questId);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            context.get().enqueueWork(() -> {
                switch (action) {
                    case ACCEPT -> QuestManager.assignQuest(player, questId);
                    case ABANDON -> QuestManager.abandonQuest(player, questId);
                    case COMPLETE -> {
                        // Устанавливаем флаг для квеста выживания (если это он)
                        if ("hbm.quest.survival_guide".equals(questId)) {
                            player.getPersistentData().putBoolean("survival_guide_completed", true);
                        }
                        QuestManager.completeQuest(player, questId);
                    }
                }
            });
        }
        context.get().setPacketHandled(true);
    }
}