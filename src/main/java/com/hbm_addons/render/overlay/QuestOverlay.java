package com.hbm_addons.render.overlay;

import com.hbm_addons.config.AddonConfig;
import com.hbm.render.overlay.*;
import com.hbm_addons.quests.HBMQuests;
import com.hbm_addons.quests.QuestDefinition;
import com.hbm_addons.quests.QuestInstance;
import com.hbm_addons.quests.QuestManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestOverlay implements IOverlayProvider {

    private static boolean temporaryVisible = false;
    private static long temporaryStartTime = 0;
    private static int temporaryDuration = 0;

    public static void showTemporary(int durationMs) {
        temporaryVisible = true;
        temporaryStartTime = System.currentTimeMillis();
        temporaryDuration = durationMs;
    }

    public static boolean isTemporaryVisible() {
        if (!temporaryVisible) return false;
        long elapsed = System.currentTimeMillis() - temporaryStartTime;
        if (elapsed >= temporaryDuration) {
            temporaryVisible = false;
            return false;
        }
        return true;
    }

    public static long getTemporaryStartTime() {
        return temporaryStartTime;
    }

    public static int getTemporaryDuration() {
        return temporaryDuration;
    }

    /** Альфа плашки: 255 — полная, линейное затухание за последнюю секунду. */
    private static int currentAlpha() {
        if (!temporaryVisible) return 255;
        if (temporaryDuration <= 0) return 255;
        long remaining = temporaryDuration - (System.currentTimeMillis() - temporaryStartTime);
        if (remaining <= 0) return 0;
        if (remaining > 1000) return 255;
        return (int) (255 * remaining / 1000);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        if (!AddonConfig.enableQuests || !AddonConfig.enableQuestOverlay) return false;
        Player player = context.mc().player;
        if (player == null) return false;

        return isTemporaryVisible();
    }

    @Override
    public List<OverlaySection> getSections(OverlayContext context) {
        Player player = context.mc().player;
        if (player == null) return List.of();

        List<QuestInstance> active = QuestManager.getActiveQuests(player);

        List<OverlaySection> sections = new ArrayList<>();
        OverlaySection questSection = new OverlaySection(OverlaySection.Type.QUEST);
        questSection.setAlpha(currentAlpha());

        // Если временный режим активен
        if (isTemporaryVisible()) {
            // Заголовок
            questSection.addLine(Component.literal("Active Quests").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));

            // Если нет активных квестов - показываем сообщение
            if (active.isEmpty()) {
                questSection.addLine(Component.translatable("hbm.quest.no_active_quests")
                        .withStyle(ChatFormatting.YELLOW));
            } else {
                // Показываем список активных квестов
                for (QuestInstance inst : active) {
                    QuestDefinition def = HBMQuests.getQuest(inst.getQuestId());
                    if (def == null) continue;

                    questSection.addLine(Component.translatable(def.getTitleKey()).withStyle(ChatFormatting.YELLOW));

                    if (!def.getRequiredItems().isEmpty()) {
                        StringBuilder req = new StringBuilder("Required: ");
                        for (int i = 0; i < def.getRequiredItems().size(); i++) {
                            req.append(def.getRequiredItems().get(i).getCount())
                                    .append("x ").append(def.getRequiredItems().get(i).getHoverName().getString());
                            if (i < def.getRequiredItems().size() - 1) req.append(", ");
                        }
                        questSection.addLine(Component.literal(req.toString()).withStyle(ChatFormatting.GRAY));
                    } else {
                        questSection.addLine(Component.translatable(def.getDescriptionKey()).withStyle(ChatFormatting.GRAY));
                    }

                    if (def.getTimeLimit() > 0) {
                        long rem = inst.getTimeRemaining(def.getTimeLimit());
                        String timeStr = String.format("Time: %02d:%02d", rem / 60, rem % 60);
                        ChatFormatting color = rem < 60 ? ChatFormatting.RED : ChatFormatting.GRAY;
                        questSection.addLine(Component.literal(timeStr).withStyle(color));
                    }

                    questSection.addLine(Component.literal(""));
                }
            }

            sections.add(questSection);
        }

        return sections;
    }
}