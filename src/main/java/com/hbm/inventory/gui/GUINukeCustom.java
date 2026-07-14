package com.hbm.inventory.gui;

import com.hbm.blocks.bomb.NukeCustom;
import com.hbm.inventory.container.ContainerNukeCustom;
import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

public class GUINukeCustom extends AbstractContainerScreen<ContainerNukeCustom> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/weapon/nuke_custom.png");
    private final TileEntityNukeCustom nuke;

    public GUINukeCustom(ContainerNukeCustom container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.nuke = container.getNukeBoy();

        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // TNT
        drawInfoStat(graphics, mouseX, mouseY, this.leftPos + 16, this.topPos + 89, 18, 18,
                Component.translatable("gui.nuke.tnt.level", nuke.tnt, Math.min(nuke.tnt, NukeCustom.maxTnt)).withStyle(ChatFormatting.YELLOW),
                Component.translatable("gui.nuke.tnt.cap", NukeCustom.maxTnt),
                Component.translatable("gui.nuke.tnt.n2"),
                Component.translatable("gui.nuke.tnt.quote").withStyle(ChatFormatting.ITALIC));

        // Nuclear
        float nukeAdj = Math.min(nuke.nuke + nuke.tnt / 2, NukeCustom.maxNuke);
        drawInfoStat(graphics, mouseX, mouseY, this.leftPos + 34, this.topPos + 89, 18, 18,
                Component.translatable("gui.nuke.nuke.level", nuke.nuke, nukeAdj).withStyle(ChatFormatting.YELLOW),
                Component.translatable("gui.nuke.nuke.requirement"),
                Component.translatable("gui.nuke.nuke.cap", NukeCustom.maxNuke),
                Component.translatable("gui.nuke.nuke.fallout"),
                Component.translatable("gui.nuke.nuke.quote").withStyle(ChatFormatting.ITALIC));

        // Thermonuclear
        float hydroAdj = Math.min(nuke.hydro + nuke.nuke / 2 + nuke.tnt / 4, NukeCustom.maxHydro);
        drawInfoStat(graphics, mouseX, mouseY, this.leftPos + 52, this.topPos + 89, 18, 18,
                Component.translatable("gui.nuke.hydro.level", nuke.hydro, hydroAdj).withStyle(ChatFormatting.YELLOW),
                Component.translatable("gui.nuke.hydro.requirement"),
                Component.translatable("gui.nuke.hydro.cap", NukeCustom.maxHydro),
                Component.translatable("gui.nuke.hydro.fallout_reduction"),
                Component.translatable("gui.nuke.hydro.quote1").withStyle(ChatFormatting.ITALIC),
                Component.translatable("gui.nuke.hydro.quote2").withStyle(ChatFormatting.ITALIC));

        // Antimatter
        float amatAdj = Math.min(nuke.amat + nuke.hydro / 2 + nuke.nuke / 4 + nuke.tnt / 8, NukeCustom.maxAmat);
        drawInfoStat(graphics, mouseX, mouseY, this.leftPos + 70, this.topPos + 89, 18, 18,
                Component.translatable("gui.nuke.amat.level", nuke.amat, amatAdj).withStyle(ChatFormatting.YELLOW),
                Component.translatable("gui.nuke.amat.requirement"),
                Component.translatable("gui.nuke.amat.cap", NukeCustom.maxAmat),
                Component.translatable("gui.nuke.amat.quote").withStyle(ChatFormatting.ITALIC));

        // Salted
        drawInfoStat(graphics, mouseX, mouseY, this.leftPos + 88, this.topPos + 89, 18, 18,
                Component.translatable("gui.nuke.dirty.level", nuke.dirty, Math.min(nuke.dirty, 100)).withStyle(ChatFormatting.YELLOW),
                Component.translatable("gui.nuke.dirty.effect1"),
                Component.translatable("gui.nuke.dirty.effect2"),
                Component.translatable("gui.nuke.dirty.cap"),
                Component.translatable("gui.nuke.dirty.quote").withStyle(ChatFormatting.ITALIC));

        // Schrabidium
        float schrabAdj = Math.min(nuke.schrab + nuke.amat / 2 + nuke.hydro / 4 + nuke.nuke / 8 + nuke.tnt / 16, NukeCustom.maxSchrab);
        drawInfoStat(graphics, mouseX, mouseY, this.leftPos + 106, this.topPos + 89, 18, 18,
                Component.translatable("gui.nuke.schrab.level", nuke.schrab, schrabAdj).withStyle(ChatFormatting.YELLOW),
                Component.translatable("gui.nuke.schrab.requirement"),
                Component.translatable("gui.nuke.schrab.cap", NukeCustom.maxSchrab),
                Component.translatable("gui.nuke.schrab.quote1").withStyle(ChatFormatting.ITALIC),
                Component.translatable("gui.nuke.schrab.quote2").withStyle(ChatFormatting.ITALIC));

        // Ice cream
        drawInfoStat(graphics, mouseX, mouseY, this.leftPos + 142, this.topPos + 89, 18, 18,
                Component.translatable("gui.nuke.ice_cream.level").withStyle(ChatFormatting.YELLOW),
                Component.translatable("gui.nuke.ice_cream.quote").withStyle(ChatFormatting.ITALIC));
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, I18n.get("container.inventory"), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Активные индикаторы
        if (nuke.euph > 0) {
            graphics.blit(TEXTURE, this.leftPos + 142, this.topPos + 89, 176, 108, 18, 18);
        } else if (nuke.schrab > 0) {
            graphics.blit(TEXTURE, this.leftPos + 106, this.topPos + 89, 176, 90, 18, 18);
        } else if (nuke.amat > 0) {
            graphics.blit(TEXTURE, this.leftPos + 70, this.topPos + 89, 176, 54, 18, 18);
        } else if (nuke.hydro > 0) {
            graphics.blit(TEXTURE, this.leftPos + 52, this.topPos + 89, 176, 36, 18, 18);
        } else if (nuke.nuke > 0) {
            graphics.blit(TEXTURE, this.leftPos + 34, this.topPos + 89, 176, 18, 18, 18);
        } else if (nuke.tnt > 0) {
            graphics.blit(TEXTURE, this.leftPos + 16, this.topPos + 89, 176, 0, 18, 18);
        }

        // Salted индикатор (только если нет антиматерии и шрабидия)
        if (nuke.dirty > 0 && nuke.nuke > 0 && nuke.amat == 0 && nuke.schrab == 0 && nuke.euph == 0) {
            graphics.blit(TEXTURE, this.leftPos + 88, this.topPos + 89, 176, 72, 18, 18);
        }
    }

    private void drawInfoStat(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, Component... lines) {
        if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
            List<Component> components = new ArrayList<>();
            for (Component line : lines) {
                components.add(line);
            }
            graphics.renderTooltip(this.font, components, java.util.Optional.empty(), mouseX, mouseY);
        }
    }
}