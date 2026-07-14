package com.hbm.inventory.gui;

import com.hbm.handler.HbmKeybinds;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.QuestActionPacket;
import com.hbm.quests.HBMQuests;
import com.hbm.quests.QuestDefinition;
import com.hbm.quests.QuestInstance;
import com.hbm.quests.QuestManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestBookScreen extends Screen {
    private static final int BOOK_WIDTH = 320;
    private static final int BOOK_HEIGHT = 240;
    private static final int TOP_OFFSET = 35;      // отступ сверху до первого элемента
    private static final int BOTTOM_OFFSET = 10;    // отступ снизу
    private static final int AVAILABLE_HEIGHT = BOOK_HEIGHT - TOP_OFFSET - BOTTOM_OFFSET; // ≈195

    private final Player player;
    private List<QuestDefinition> availableQuests;
    private List<QuestInstance> activeQuests;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int visibleEntries = 0; // вычисляется динамически

    private Button refreshButton;
    private final Map<String, Long> questCooldowns = new HashMap<>();

    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 1000;

    private static final int SCROLL_BAR_WIDTH = 6;
    private static final int SCROLL_BAR_COLOR = 0xFFAAAAAA;
    private static final int SCROLL_BAR_BG_COLOR = 0x44AAAAAA;

    private List<Object> allEntries = new ArrayList<>();

    private static class QuestEntry {
        enum Type { ACTIVE, AVAILABLE }
        final Type type;
        final QuestDefinition def;
        final QuestInstance instance;
        final String questId;
        final long cooldown;

        QuestEntry(Type type, QuestDefinition def, QuestInstance instance, String questId, long cooldown) {
            this.type = type;
            this.def = def;
            this.instance = instance;
            this.questId = questId;
            this.cooldown = cooldown;
        }
    }

    public QuestBookScreen(Player player) {
        super(Component.translatable("hbm.gui.quest_book"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        refreshQuests();
        rebuildWidgets();
    }

    private void refreshQuests() {
        availableQuests = QuestManager.getAvailableQuests(player);
        activeQuests = QuestManager.getActiveQuests(player);
        questCooldowns.clear();

        for (QuestDefinition quest : availableQuests) {
            if (!quest.isOneTimeOnly()) {
                Map<String, QuestInstance> playerQuests = QuestManager.getPlayerQuests(player);
                QuestInstance existing = playerQuests != null ? playerQuests.get(quest.getId()) : null;
                if (existing != null && existing.isCompleted()) {
                    long cooldownRemaining = existing.getCooldownRemaining(quest.getCooldown());
                    if (cooldownRemaining > 0) {
                        questCooldowns.put(quest.getId(), cooldownRemaining);
                    }
                }
            }
        }

        allEntries.clear();
        allEntries.add("§lActive Quests:");
        if (activeQuests.isEmpty()) {
            allEntries.add("  No active quests");
        } else {
            for (QuestInstance inst : activeQuests) {
                QuestDefinition def = HBMQuests.getQuest(inst.getQuestId());
                if (def != null) {
                    allEntries.add(new QuestEntry(QuestEntry.Type.ACTIVE, def, inst, inst.getQuestId(), 0));
                }
            }
        }

        allEntries.add("§lAvailable Quests:");
        if (availableQuests.isEmpty()) {
            allEntries.add("  No available quests");
        } else {
            for (QuestDefinition quest : availableQuests) {
                long cooldown = questCooldowns.getOrDefault(quest.getId(), 0L);
                allEntries.add(new QuestEntry(QuestEntry.Type.AVAILABLE, quest, null, quest.getId(), cooldown));
            }
        }

        updateVisibleEntries();
    }

    private void updateVisibleEntries() {
        int totalHeight = 0;
        int count = 0;
        for (Object entry : allEntries) {
            int h = getEntryHeight(entry);
            if (totalHeight + h > AVAILABLE_HEIGHT) break;
            totalHeight += h;
            count++;
        }
        visibleEntries = Math.max(1, count);
        maxScrollOffset = Math.max(0, allEntries.size() - visibleEntries);
        if (scrollOffset > maxScrollOffset) scrollOffset = maxScrollOffset;
    }

    private int getEntryHeight(Object entry) {
        if (entry instanceof String) {
            return 15;
        } else if (entry instanceof QuestEntry qe) {
            int height = 12; // название
            if (qe.type == QuestEntry.Type.ACTIVE) {
                if (qe.def.getTimeLimit() > 0) height += 10;
                height += 5; // отступ после активного
            } else {
                if (!qe.def.getRequiredItems().isEmpty()) height += 10;
                height += 8; // отступ после доступного
            }
            return height;
        }
        return 0;
    }

    protected void rebuildWidgets() {
        clearWidgets();

        int x = (width - BOOK_WIDTH) / 2;
        int y = (height - BOOK_HEIGHT) / 2;

        refreshButton = Button.builder(Component.literal("Refresh"), btn -> {
            refreshQuests();
            rebuildWidgets();
        }).pos(x + BOOK_WIDTH - 60, y + 10).size(50, 20).build();
        addRenderableWidget(refreshButton);

        int start = scrollOffset;
        int end = Math.min(start + visibleEntries, allEntries.size());
        int currentY = y + TOP_OFFSET;

        for (int i = start; i < end; i++) {
            Object entry = allEntries.get(i);
            if (entry instanceof QuestEntry qe) {
                if (qe.type == QuestEntry.Type.ACTIVE) {
                    Button abandonBtn = Button.builder(Component.literal("Abandon"),
                                    btn -> {
                                        if (player.level().isClientSide()) {
                                            QuestManager.removeLocalActiveQuest(qe.questId);
                                            PacketDispatcher.sendToServer(new QuestActionPacket(QuestActionPacket.Action.ABANDON, qe.questId));
                                        }
                                        refreshQuests();
                                        rebuildWidgets();
                                    })
                            .pos(x + BOOK_WIDTH - 130, currentY - 4)
                            .size(60, 15)
                            .build();
                    addRenderableWidget(abandonBtn);
                    currentY += 12;
                    if (qe.def.getTimeLimit() > 0) currentY += 10;
                    currentY += 5;
                } else {
                    if (qe.cooldown <= 0) {
                        Button acceptBtn = Button.builder(Component.literal("Accept"),
                                        btn -> {
                                            if (player.level().isClientSide()) {
                                                QuestManager.addLocalActiveQuest(qe.questId);
                                                PacketDispatcher.sendToServer(new QuestActionPacket(QuestActionPacket.Action.ACCEPT, qe.questId));
                                            }
                                            refreshQuests();
                                            rebuildWidgets();
                                        })
                                .pos(x + BOOK_WIDTH - 130, currentY - 4)
                                .size(60, 15)
                                .build();
                        addRenderableWidget(acceptBtn);
                    }
                    currentY += 12;
                    if (!qe.def.getRequiredItems().isEmpty()) currentY += 10;
                    currentY += 8;
                }
            } else {
                // заголовки — кнопок нет
                currentY += 15;
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        long now = System.currentTimeMillis();
        if (now - lastUpdateTime >= UPDATE_INTERVAL_MS) {
            lastUpdateTime = now;
            updateCooldowns();
            refreshQuests();
            rebuildWidgets();
        }

        int x = (width - BOOK_WIDTH) / 2;
        int y = (height - BOOK_HEIGHT) / 2;

        guiGraphics.fill(x, y, x + BOOK_WIDTH, y + BOOK_HEIGHT, 0xFF8B5A2B);
        guiGraphics.fill(x + 2, y + 2, x + BOOK_WIDTH - 2, y + BOOK_HEIGHT - 2, 0xFFF5DEB3);

        guiGraphics.drawString(font, Component.translatable("hbm.gui.quest_book").getString(),
                x + BOOK_WIDTH / 2 - 40, y + 10, 0x4A2B1A, false);

        int start = scrollOffset;
        int end = Math.min(start + visibleEntries, allEntries.size());
        int currentY = y + TOP_OFFSET;

        for (int i = start; i < end; i++) {
            Object entry = allEntries.get(i);
            if (entry instanceof String text) {
                int color = text.startsWith("§l") ? 0x4A2B1A : 0x888888;
                guiGraphics.drawString(font, text, x + 10, currentY, color, false);
                currentY += 15;
            } else if (entry instanceof QuestEntry qe) {
                if (qe.type == QuestEntry.Type.ACTIVE) {
                    guiGraphics.drawString(font, "- " + Component.translatable(qe.def.getTitleKey()).getString(),
                            x + 15, currentY, 0x2C5E2E, false);
                    currentY += 12;
                    if (qe.def.getTimeLimit() > 0 && qe.instance != null) {
                        long rem = qe.instance.getTimeRemaining(qe.def.getTimeLimit());
                        String timeStr = String.format("  Time: %02d:%02d", rem / 60, rem % 60);
                        int color = rem < 60 ? 0xFF5555 : 0x888888;
                        guiGraphics.drawString(font, timeStr, x + 20, currentY, color, false);
                        currentY += 10;
                    }
                    currentY += 5;
                } else {
                    String type = qe.def.isOneTimeOnly() ? "[One-time]" : "[Repeatable]";
                    int color = qe.def.isOneTimeOnly() ? 0x44AA44 : 0xAA8844;
                    guiGraphics.drawString(font, type + " " + Component.translatable(qe.def.getTitleKey()).getString(),
                            x + 15, currentY, color, false);
                    currentY += 12;
                    if (!qe.def.getRequiredItems().isEmpty()) {
                        String req = qe.def.getRequiredItems().get(0).getCount() + "x " +
                                qe.def.getRequiredItems().get(0).getHoverName().getString();
                        guiGraphics.drawString(font, req, x + 20, currentY, 0x888888, false);
                        currentY += 10;
                    }
                    if (qe.cooldown > 0) {
                        String cooldownText = String.format("  Cooldown: %02d:%02d", qe.cooldown / 60, qe.cooldown % 60);
                        guiGraphics.drawString(font, cooldownText, x + BOOK_WIDTH - 80, currentY - 10, 0xFF5555, false);
                    }
                    currentY += 8;
                }
            }
        }

        if (allEntries.size() > visibleEntries) {
            int scrollBarX = x + BOOK_WIDTH - SCROLL_BAR_WIDTH - 5;
            int scrollBarY = y + TOP_OFFSET;
            int scrollBarHeight = AVAILABLE_HEIGHT;

            guiGraphics.fill(scrollBarX, scrollBarY,
                    scrollBarX + SCROLL_BAR_WIDTH, scrollBarY + scrollBarHeight,
                    SCROLL_BAR_BG_COLOR);

            float visibleRatio = (float) visibleEntries / allEntries.size();
            int thumbHeight = Math.max(15, (int) (scrollBarHeight * visibleRatio));

            float scrollRatio = maxScrollOffset > 0 ? (float) scrollOffset / maxScrollOffset : 0;
            int thumbY = scrollBarY + (int) (scrollRatio * (scrollBarHeight - thumbHeight));

            guiGraphics.fill(scrollBarX, thumbY,
                    scrollBarX + SCROLL_BAR_WIDTH, thumbY + thumbHeight,
                    SCROLL_BAR_COLOR);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (allEntries.size() > visibleEntries) {
            int newOffset = scrollOffset - (int) delta;
            if (newOffset >= 0 && newOffset <= maxScrollOffset) {
                scrollOffset = newOffset;
                rebuildWidgets();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void updateCooldowns() {
        questCooldowns.clear();

        // Определяем, на клиенте мы или на сервере
        boolean isClient = player.level().isClientSide();

        for (QuestDefinition quest : availableQuests) {
            if (!quest.isOneTimeOnly()) {
                Map<String, QuestInstance> playerQuests;
                if (isClient) {
                    playerQuests = QuestManager.getClientPlayerQuests();
                } else {
                    playerQuests = QuestManager.getPlayerQuests(player);
                }

                QuestInstance existing = playerQuests != null ? playerQuests.get(quest.getId()) : null;
                if (existing != null && existing.isCompleted()) {
                    long cooldownRemaining = existing.getCooldownRemaining(quest.getCooldown());
                    if (cooldownRemaining > 0) {
                        questCooldowns.put(quest.getId(), cooldownRemaining);
                    }
                }
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == 69) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        HbmKeybinds.ClientForgeEvents.resetTabState();
    }
}