package com.hbm.network.client;

import com.hbm.network.PacketBase;
import com.hbm.quests.QuestInstance;
import com.hbm.quests.QuestManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class QuestSyncPacket extends PacketBase implements PacketBase.DecodablePacket {

    private Map<String, QuestInstance> activeQuests;
    private Set<String> completedQuests;

    public QuestSyncPacket() {}

    public QuestSyncPacket(Map<String, QuestInstance> activeQuests, Set<String> completedQuests) {
        this.activeQuests = activeQuests;
        this.completedQuests = completedQuests;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        Map<String, QuestInstance> active = new HashMap<>();
        int activeSize = buf.readInt();
        for (int i = 0; i < activeSize; i++) {
            QuestInstance inst = QuestInstance.load(Objects.requireNonNull(buf.readNbt()));
            active.put(inst.getQuestId(), inst);
        }

        Set<String> completed = new HashSet<>();
        int completedSize = buf.readInt();
        for (int i = 0; i < completedSize; i++) {
            completed.add(buf.readUtf());
        }

        return new QuestSyncPacket(active, completed);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(activeQuests.size());
        for (QuestInstance inst : activeQuests.values()) {
            buf.writeNbt(inst.save());
        }

        buf.writeInt(completedQuests.size());
        for (String qid : completedQuests) {
            buf.writeUtf(qid);
        }
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> QuestManager.syncQuestsForClient(activeQuests, completedQuests));
        context.get().setPacketHandled(true);
    }
}