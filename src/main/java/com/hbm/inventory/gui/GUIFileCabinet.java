package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerFileCabinet;
import com.hbm.tileentity.storage.TileEntityFileCabinet;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIFileCabinet extends AbstractContainerScreen<ContainerFileCabinet> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/storage/gui_file_cabinet.png");

    private final TileEntityFileCabinet cabinet;

    public GUIFileCabinet(ContainerFileCabinet container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.cabinet = container.getCabinet();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Основной фон
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Рисуем сетку для слотов (если текстура не включает её)
        // Можно расскомментировать если нужны видимые границы слотов
        /*
        for (int i = 0; i < 8; i++) {
            int x = leftPos + 44 + (i % 4) * 18;
            int y = topPos + 18 + (i / 4) * 18;
            guiGraphics.blit(TEXTURE, x, y, 176, 0, 18, 18);
        }
        */
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Заголовок контейнера
        guiGraphics.drawString(this.font, this.title,
                this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);

        // Заголовок инвентаря игрока
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
                8, this.imageHeight - 90, 0x404040, false);

    }



    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Показываем тултипы для слотов с предметами
        renderSlotTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderSlotTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Проверяем каждый слот шкафа
        for (int i = 0; i < 8; i++) {
            int x = leftPos + 44 + (i % 4) * 18;
            int y = topPos + 18 + (i / 4) * 18;

            if (isHovering(44 + (i % 4) * 18, 18 + (i / 4) * 18, 18, 18, mouseX, mouseY)) {
                ItemStack stack = cabinet.getItem(i);
                if (!stack.isEmpty()) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(stack.getHoverName());

                    // Добавляем информацию о количестве
                    tooltip.add(Component.literal("§7Count: " + stack.getCount()));

                    // Можно добавить другую информацию
                    if (stack.hasTag()) {
                        tooltip.add(Component.literal("§8Has NBT Data"));
                    }

                    guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                }
                break;
            }
        }
    }

    // Вспомогательный метод для проверки наведения
    public boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + width &&
                mouseY >= topPos + y && mouseY < topPos + y + height;
    }
}