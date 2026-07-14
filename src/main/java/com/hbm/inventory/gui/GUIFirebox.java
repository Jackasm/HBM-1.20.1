package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerFirebox;
import com.hbm.tileentity.machine.TileEntityFireboxBase;
import com.hbm.tileentity.machine.TileEntityHeaterOven;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIFirebox extends AbstractContainerScreen<ContainerFirebox> {

    private TileEntityFireboxBase firebox;
    private ResourceLocation texture;

    public GUIFirebox(ContainerFirebox container, Inventory inv, Component title) {
        super(container, inv, title);
        this.firebox = container.getFirebox();
        if (container.getFirebox() instanceof TileEntityHeaterOven oven) {
            this.texture = oven.getGuiTexture();
        } else {
            this.texture = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_firebox.png");
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Отображение тултипов для слотов
        if (Minecraft.getInstance().player.containerMenu.getCarried().isEmpty()) {
            for (int i = 0; i < 2; ++i) {
                Slot slot = this.getMenu().slots.get(i);

                if (this.isMouseOverSlot(slot, mouseX, mouseY) && !slot.hasItem()) {
                    List<Component> bonuses = this.firebox.getModule().getDesc();
                    if (!bonuses.isEmpty()) {
                        graphics.renderComponentTooltip(this.font, bonuses, mouseX, mouseY);
                    }
                }
            }
        }

        // Информация о тепле
        this.drawCustomInfoStat(graphics, mouseX, mouseY, this.leftPos + 80, this.topPos + 27, 71, 7, mouseX, mouseY,
                new String[]{String.format(Locale.US, "%,d", firebox.heatEnergy) + " / " + String.format(Locale.US, "%,d", firebox.getMaxHeat()) + "TU"});

        // Информация о горении
        this.drawCustomInfoStat(graphics, mouseX, mouseY, this.leftPos + 80, this.topPos + 36, 71, 7, mouseX, mouseY,
                new String[]{firebox.burnHeat + "TU/t", (firebox.burnTime / 20) + "s"});
    }

    protected boolean isMouseOverSlot(Slot slot, double mouseX, double mouseY) {
        return this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
        String name = Component.translatable(firebox.getBlockState().getBlock().getDescriptionId()).getString();

        int color = firebox instanceof TileEntityHeaterOven ? 0xffffff : 0x404040;

        graphics.drawString(this.font, name, this.imageWidth / 2 - this.font.width(name) / 2, 6, color, false);
        graphics.drawString(this.font, I18n.get("container.inventory"), 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Прогресс тепла
        int heatProgress = firebox.heatEnergy * 69 / Math.max(firebox.getMaxHeat(), 1);
        graphics.blit(texture, this.leftPos + 81, this.topPos + 28, 176, 0, heatProgress, 5);

        // Прогресс горения
        int burnProgress = firebox.burnTime * 70 / Math.max(firebox.maxBurnTime, 1);
        graphics.blit(texture, this.leftPos + 81, this.topPos + 37, 176, 5, burnProgress, 5);

        // Индикатор работы
        if (firebox.wasOn) {
            graphics.blit(texture, this.leftPos + 25, this.topPos + 26, 176, 10, 18, 18);
        }
    }

    public void drawCustomInfoStat(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, int tPosX, int tPosY, String[] text) {
        if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
            List<Component> components = new java.util.ArrayList<>();
            for (String s : text) {
                components.add(Component.literal(s));
            }
            graphics.renderTooltip(this.font, components, java.util.Optional.empty(), tPosX, tPosY);
        }
    }
}