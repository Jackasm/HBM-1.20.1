package com.hbm.inventory.gui;

import com.hbm.items.tool.ItemGuideBook;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.QuestActionPacket;
import com.hbm.quests.QuestManager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class GUIScreenGuide extends Screen {

    private static final ResourceLocation TEXTURE = ResLocation(MODID, "textures/gui/book/book.png");
    private static final ResourceLocation TEXTURE_COVER = ResLocation(MODID, "textures/gui/book/book_cover.png");

    private final ItemGuideBook.BookType type;
    private int page;
    private final int maxPage;

    // Состояния для квестов
    private boolean autoQuestAccepted = false;
    private boolean lastPageQuestCompleted = false;
    private boolean showingAcceptButton = false;
    private int buttonX, buttonY, buttonW = 50, buttonH = 15;
    private String pendingQuestId = null;

    // Для тултипов
    private ItemStack hoverStack = ItemStack.EMPTY;
    private int hoverSlotX = -1, hoverSlotY = -1;

    private enum LangScale {
        RUSSIAN(new String[]{"ru", "uk", "be"}, 0.7F),
        ENGLISH(new String[]{"en"}, 1.0F),
        DEFAULT(new String[]{}, 1.0F);

        private final String[] langs;
        private final float scale;

        LangScale(String[] langs, float scale) {
            this.langs = langs;
            this.scale = scale;
        }

        public static float getScale(String lang) {
            for (LangScale ls : values()) {
                for (String l : ls.langs) {
                    if (lang.contains(l)) return ls.scale;
                }
            }
            return DEFAULT.scale;
        }
    }

    public GUIScreenGuide(ItemGuideBook.BookType type) {
        super(Component.translatable(type.title));
        this.type = type;
        this.page = -1;
        this.maxPage = (int) Math.ceil(type.pages.size() / 2D) - 1;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        renderBg(guiGraphics, mouseX, mouseY);
        renderForeground(guiGraphics, mouseX, mouseY);

        if (!hoverStack.isEmpty() && hoverSlotX != -1 && hoverSlotY != -1) {
            if (mouseX >= hoverSlotX && mouseX <= hoverSlotX + 16 && mouseY >= hoverSlotY && mouseY <= hoverSlotY + 16) {
                guiGraphics.renderTooltip(font, hoverStack, mouseX, mouseY);
            } else {
                hoverStack = ItemStack.EMPTY;
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void renderBg(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int guiLeft = (this.width - 272) / 2;
        int guiTop = (this.height - 182) / 2;

        if (page < 0) {
            guiGraphics.blit(TEXTURE_COVER, guiLeft, guiTop, 0, 0, 272, 182, 512, 512);
            return;
        }

        guiGraphics.blit(TEXTURE, guiLeft, guiTop, 0, 0, 272, 182, 512, 512);

        boolean overLeft = mouseX >= guiLeft + 24 && mouseX < guiLeft + 42 && mouseY >= guiTop + 160 && mouseY < guiTop + 170;
        boolean overRight = mouseX >= guiLeft + 230 && mouseX < guiLeft + 248 && mouseY >= guiTop + 160 && mouseY < guiTop + 170;

        if (this.page > 0) {
            if (!overLeft)
                guiGraphics.blit(TEXTURE, guiLeft + 24, guiTop + 160, 3, 207, 18, 10, 512, 512);
            else
                guiGraphics.blit(TEXTURE, guiLeft + 24, guiTop + 160, 26, 207, 18, 10, 512, 512);
        }

        if (this.page < this.maxPage) {
            if (!overRight)
                guiGraphics.blit(TEXTURE, guiLeft + 230, guiTop + 160, 3, 194, 18, 10, 512, 512);
            else
                guiGraphics.blit(TEXTURE, guiLeft + 230, guiTop + 160, 26, 194, 18, 10, 512, 512);
        }
    }

    private void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int guiLeft = (this.width - 272) / 2;
        int guiTop = (this.height - 182) / 2;
        PoseStack poseStack = guiGraphics.pose();
        Player player = Minecraft.getInstance().player;

        if (this.page < 0) {
            float scale = this.type.titleScale;
            Component titleComp = Component.translatable(this.type.title);
            String[] coverLines = titleComp.getString().split("\n");

            poseStack.pushPose();
            poseStack.scale(scale, scale, 1F);
            for (int i = 0; i < coverLines.length; i++) {
                String cover = coverLines[i];
                int stringWidth = font.width(cover);
                float x = (guiLeft + (272F / 2) - (stringWidth / 2F * scale)) / scale;
                float y = (guiTop + 50 + i * 10 * scale) / scale;
                guiGraphics.drawString(font, cover, (int) x, (int) y, 0xfece00, false);
            }
            poseStack.popPose();
            return;
        }

        int sideOffset = 130;
        showingAcceptButton = false;

        for (int i = 0; i < 2; i++) {
            int defacto = this.page * 2 + i;
            if (defacto >= this.type.pages.size()) continue;

            ItemGuideBook.GuidePage page = this.type.pages.get(defacto);

            // Автоматическое принятие квеста
            if (page.autoAccept && page.questId != null && !autoQuestAccepted && player != null) {
                if (!QuestManager.hasActiveQuest(player, page.questId) && !QuestManager.hasCompletedQuest(player, page.questId)) {
                    if (player.level().isClientSide()) {
                        PacketDispatcher.sendToServer(new QuestActionPacket(QuestActionPacket.Action.ACCEPT, page.questId));
                        QuestManager.addLocalActiveQuest(page.questId);
                    }
                }
                autoQuestAccepted = true;
            }

            // Отрисовка заголовка
            int currentY = guiTop + 15;

            if (page.title != null) {
                String titleLoc = Component.translatable(page.title).getString();
                int maxWidth = 100;
                List<String> titleLines = new ArrayList<>();
                String[] words = titleLoc.split(" ");
                StringBuilder line = new StringBuilder();

                for (String word : words) {
                    String testLine = line.isEmpty() ? word : line + " " + word;
                    if (font.width(testLine) <= maxWidth) {
                        if (line.isEmpty()) {
                            line = new StringBuilder(word);
                        } else {
                            line.append(" ").append(word);
                        }
                    } else {
                        if (!line.isEmpty()) {
                            titleLines.add(line.toString());
                            line = new StringBuilder(word);
                        } else {
                            titleLines.add(word);
                        }
                    }
                }
                if (!line.isEmpty()) {
                    titleLines.add(line.toString());
                }

                int startX = guiLeft + 20 + i * sideOffset;
                int y = currentY;

                for (String lineStr : titleLines) {
                    int stringWidth = font.width(lineStr);
                    int x = startX + (100 - stringWidth) / 2;
                    guiGraphics.drawString(font, lineStr, x, y, page.titleColor, false);
                    y += font.lineHeight + 2;
                }

                currentY += titleLines.size() * (font.lineHeight + 2) + 5;
            }

            // Отрисовка текстов
            for (ItemGuideBook.GuideText textBox : page.texts) {
                String text = Component.translatable(textBox.text).getString();
                int maxWidth = textBox.width;
                float scale = LangScale.getScale(Minecraft.getInstance().getLanguageManager().getSelected());

                List<String> lines = new ArrayList<>();
                String[] words = text.split(" ");
                StringBuilder line = new StringBuilder();

                for (String word : words) {
                    String testLine = line.isEmpty() ? word : line + " " + word;
                    int testWidth = (int)(font.width(testLine) * scale);
                    if (testWidth <= maxWidth) {
                        if (line.isEmpty()) {
                            line = new StringBuilder(word);
                        } else {
                            line.append(" ").append(word);
                        }
                    } else {
                        if (!line.isEmpty()) {
                            lines.add(line.toString());
                            line = new StringBuilder(word);
                        } else {
                            lines.add(word);
                        }
                    }
                }
                if (!line.isEmpty()) {
                    lines.add(line.toString());
                }

                poseStack.pushPose();
                poseStack.scale(scale, scale, 1F);

                int y = (int)(currentY / scale);
                for (String lineStr : lines) {
                    int x = (int)((guiLeft + 20 + i * sideOffset + textBox.xOffset) / scale);
                    guiGraphics.drawString(font, lineStr, x, y, 4210752, false);
                    y += (int)((font.lineHeight + 2) / scale);
                }

                poseStack.popPose();
                currentY = (int)(y * scale) + 5;
            }

            // Отрисовка изображений
            for (ItemGuideBook.GuideImage image : page.images) {
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                RenderSystem.setShaderTexture(0, image.image());
                int ix = (image.x() == -1) ? (100 / 2 - image.sizeX() / 2) : image.x();
                int x = guiLeft + 20 + ix + sideOffset * i;
                int y = guiTop + image.y();
                guiGraphics.blit(image.image(), x, y, 0, 0, image.sizeX(), image.sizeY(), image.sizeX(), image.sizeY());
            }

            // Отрисовка рецепта крафта
            if (page.craftingGrid != null && !page.craftingGrid.isEmpty()) {
                int startX = guiLeft + 20 + i * sideOffset + page.craftingX;
                int startY = guiTop + 44;
                int cell = page.cellSize;

                guiGraphics.fill(startX - 2, startY - 2, startX + 3 * cell + 2, startY + 3 * cell + 2, 0xFF888888);
                guiGraphics.fill(startX - 1, startY - 1, startX + 3 * cell + 1, startY + 3 * cell + 1, 0xFF444444);

                for (int slot = 0; slot < 9; slot++) {
                    ItemStack stack = page.craftingGrid.get(slot);
                    if (!stack.isEmpty()) {
                        int col = slot % 3;
                        int row = slot / 3;
                        int x = startX + col * cell;
                        int y = startY + row * cell;
                        guiGraphics.renderItem(stack, x, y);
                        guiGraphics.renderItemDecorations(font, stack, x, y);
                    }
                }

                int arrowX = startX + 3 * cell + 7;
                int arrowY = startY + cell + 4;
                guiGraphics.drawString(font, "→", arrowX, arrowY, 0x000000, false);

                if (!page.craftingResult.isEmpty()) {
                    int resultX = startX + 3 * cell + 20;
                    int resultY = startY + cell - 2;
                    guiGraphics.fill(resultX - 2, resultY - 2, resultX + cell + 2, resultY + cell + 2, 0xFF888888);
                    guiGraphics.fill(resultX - 1, resultY - 1, resultX + cell + 1, resultY + cell + 1, 0xFF444444);
                    guiGraphics.renderItem(page.craftingResult, resultX + 1, resultY + 2);
                    guiGraphics.renderItemDecorations(font, page.craftingResult, resultX, resultY);
                }

                checkCraftingHover(mouseX, mouseY, startX, startY, cell, page);
            }

            // Отрисовка рецепта наковальни
            if (!page.anvilRequirements.isEmpty() && !page.anvilResult.isEmpty()) {
                int startX = guiLeft + i * sideOffset + page.anvilX;
                int startY = guiTop + 20;
                int lineHeight = 12;
                int itemSize = 16;

                guiGraphics.drawString(font, "Required:", startX, startY, 0x888888, false);
                startY += lineHeight;

                float textScale = LangScale.getScale(Minecraft.getInstance().getLanguageManager().getSelected());

                for (ItemStack req : page.anvilRequirements) {
                    String name = req.getHoverName().getString();
                    int maxNameWidth = 70;

                    List<String> nameLines = new ArrayList<>();
                    String[] words = name.split(" ");
                    StringBuilder line = new StringBuilder();

                    for (String word : words) {
                        String testLine = line.isEmpty() ? word : line + " " + word;
                        int testWidth = (int)(font.width(testLine) * textScale);
                        if (testWidth <= maxNameWidth) {
                            if (line.isEmpty()) {
                                line = new StringBuilder(word);
                            } else {
                                line.append(" ").append(word);
                            }
                        } else {
                            if (!line.isEmpty()) {
                                nameLines.add(line.toString());
                                line = new StringBuilder(word);
                            } else {
                                nameLines.add(word);
                            }
                        }
                    }
                    if (!line.isEmpty()) {
                        nameLines.add(line.toString());
                    }

                    guiGraphics.renderItem(req, startX, startY);

                    String countStr = "x" + req.getCount();
                    guiGraphics.drawString(font, countStr, startX + itemSize + 2, startY + 4, 0x000000, false);

                    int nameStartX = startX + itemSize + 2 + font.width(countStr) + 4;
                    int iconHeight = 16;
                    int nameStartY = startY + 4 + (iconHeight - (nameLines.size() * (font.lineHeight + 2))) / 2;

                    poseStack.pushPose();
                    poseStack.scale(textScale, textScale, 1F);
                    for (int l = 0; l < nameLines.size(); l++) {
                        int nameX = (int)(nameStartX / textScale);
                        int nameY = (int)((nameStartY + l * (font.lineHeight + 2)) / textScale);
                        guiGraphics.drawString(font, nameLines.get(l), nameX, nameY, 0xAAAAAA, false);
                    }
                    poseStack.popPose();

                    int textHeight = nameLines.size() * (font.lineHeight + 2);
                    startY += Math.max(iconHeight, textHeight) + 2;
                }

                startY += lineHeight;

                guiGraphics.drawString(font, "Result:", startX, startY, 0x888888, false);
                startY += lineHeight;

                guiGraphics.renderItem(page.anvilResult, startX, startY);

                String resultName = page.anvilResult.getHoverName().getString();
                poseStack.pushPose();
                poseStack.scale(textScale, textScale, 1F);
                int resultX = (int)((startX + itemSize + 2) / textScale);
                int resultY = (int)((startY + 4) / textScale);
                guiGraphics.drawString(font, resultName, resultX, resultY, 0x000000, false);
                poseStack.popPose();
            }

            // Кнопка "Принять"
            if (page.showAcceptButton && page.questId != null && player != null) {
                boolean alreadyAccepted = QuestManager.hasActiveQuest(player, page.questId);
                boolean alreadyCompleted = QuestManager.hasCompletedQuest(player, page.questId);

                int btnW = 50;
                int btnH = 15;
                int btnX = guiLeft + 170;
                int btnY = guiTop + 135;

                if (!alreadyAccepted && !alreadyCompleted) {
                    int padding = 1;
                    int innerBtnX = btnX + padding;
                    int innerBtnY = btnY + padding;
                    int innerBtnW = btnW - padding * 2;
                    int innerBtnH = btnH - padding * 2;

                    boolean hovering = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

                    guiGraphics.fill(innerBtnX, innerBtnY, innerBtnX + innerBtnW, innerBtnY + innerBtnH, 0xFF444444);
                    guiGraphics.renderOutline(innerBtnX, innerBtnY, innerBtnW, innerBtnH, 0xFF888888);

                    int textColor = hovering ? 0xFFFFFF : ChatFormatting.GOLD.getColor();
                    int textX = btnX + (btnW - font.width("Accept")) / 2;
                    int textY = btnY + (btnH - font.lineHeight) / 2 + 1;
                    guiGraphics.drawString(font, "Accept", textX, textY, textColor, false);

                    showingAcceptButton = true;
                    buttonX = btnX;
                    buttonY = btnY;
                    buttonW = btnW;
                    buttonH = btnH;
                    pendingQuestId = page.questId;
                } else if (alreadyAccepted) {
                    String text = Component.translatable("book.quest.accepted").getString();
                    int textX = btnX + (btnW - font.width(text)) / 2;
                    int textY = btnY + (btnH - font.lineHeight) / 2 + 1;
                    guiGraphics.drawString(font, text, textX, textY, 0x44AA44, false);
                } else {
                    String text = Component.translatable("book.quest.completed").getString();
                    int textX = btnX + (btnW - font.width(text)) / 2;
                    int textY = btnY + (btnH - font.lineHeight) / 2 + 1;
                    guiGraphics.drawString(font, text, textX, textY, 0xAAAAAA, false);
                }
            }
        }
    }

    private void checkCraftingHover(int mouseX, int mouseY, int startX, int startY, int cell, ItemGuideBook.GuidePage page) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = page.craftingGrid.get(slot);
            if (!stack.isEmpty()) {
                int col = slot % 3;
                int row = slot / 3;
                int x = startX + col * cell;
                int y = startY + row * cell;

                if (mouseX >= x && mouseX <= x + cell && mouseY >= y && mouseY <= y + cell) {
                    hoverStack = stack;
                    hoverSlotX = x;
                    hoverSlotY = y;
                    return;
                }
            }
        }

        if (!page.craftingResult.isEmpty()) {
            int resultX = startX + 3 * cell + 20;
            int resultY = startY + cell - 2;
            int resultW = cell + 4;
            int resultH = cell + 4;

            if (mouseX >= resultX && mouseX <= resultX + resultW && mouseY >= resultY && mouseY <= resultY + resultH) {
                hoverStack = page.craftingResult;
                hoverSlotX = resultX;
                hoverSlotY = resultY;
                return;
            }
        }

        hoverStack = ItemStack.EMPTY;
        hoverSlotX = -1;
        hoverSlotY = -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiLeft = (this.width - 272) / 2;
        int guiTop = (this.height - 182) / 2;
        Player player = Minecraft.getInstance().player;

        if (showingAcceptButton && pendingQuestId != null && player != null) {
            if (mouseX >= buttonX && mouseX <= buttonX + buttonW && mouseY >= buttonY && mouseY <= buttonY + buttonH) {
                if (player.level().isClientSide()) {
                    PacketDispatcher.sendToServer(new QuestActionPacket(QuestActionPacket.Action.ACCEPT, pendingQuestId));
                    QuestManager.addLocalActiveQuest(pendingQuestId);
                }
                showingAcceptButton = false;
                pendingQuestId = null;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        if (page < 0) {
            page = 0;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        boolean overLeft = mouseX >= guiLeft + 24 && mouseX < guiLeft + 42 && mouseY >= guiTop + 160 && mouseY < guiTop + 170;
        boolean overRight = mouseX >= guiLeft + 230 && mouseX < guiLeft + 248 && mouseY >= guiTop + 160 && mouseY < guiTop + 170;

        if (overLeft && page > 0) {
            page--;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        if (overRight && page < maxPage) {
            page++;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            if (!lastPageQuestCompleted && player != null && page == maxPage) {
                for (ItemGuideBook.GuidePage p : type.pages) {
                    if (p.completeOnLastPage && p.questId != null) {
                        if (QuestManager.hasActiveQuest(player, p.questId)) {
                            player.getPersistentData().putBoolean("survival_guide_completed", true);
                            if (player.level().isClientSide()) {
                                PacketDispatcher.sendToServer(new QuestActionPacket(QuestActionPacket.Action.COMPLETE, p.questId));
                            }
                            lastPageQuestCompleted = true;
                        }
                        break;
                    }
                }
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
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
    public boolean isPauseScreen() {
        return false;
    }
}