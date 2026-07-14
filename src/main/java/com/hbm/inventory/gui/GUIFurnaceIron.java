package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerFurnaceIron;
import com.hbm.tileentity.machine.TileEntityFurnaceIron;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIFurnaceIron extends AbstractContainerScreen<ContainerFurnaceIron> {

    private static final ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_furnace_iron.png");
    private final TileEntityFurnaceIron furnace;

    public GUIFurnaceIron(ContainerFurnaceIron container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.furnace = container.getFurnace();

        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        this.renderTooltip(graphics, mouseX, mouseY);

        // Progress tooltip
        drawCustomInfoStat(graphics, mouseX, mouseY, this.leftPos + 52, this.topPos + 35, 71, 7,
                new String[]{(furnace.progress * 100 / Math.max(furnace.processingTime, 1)) + "%"});

        // Burn time tooltip
        drawCustomInfoStat(graphics, mouseX, mouseY, this.leftPos + 52, this.topPos + 44, 71, 7,
                new String[]{(furnace.burnTime / 20) + "s"});
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(furnace.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(this.font, name, this.imageWidth / 2 - this.font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(this.font, I18n.get("container.inventory"), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Progress bar
        int progress = furnace.progress * 70 / Math.max(furnace.processingTime, 1);
        graphics.blit(texture, this.leftPos + 53, this.topPos + 36, 176, 18, progress, 5);

        // Burn bar
        int burn = furnace.burnTime * 70 / Math.max(furnace.maxBurnTime, 1);
        graphics.blit(texture, this.leftPos + 53, this.topPos + 45, 176, 23, burn, 5);

        // Active indicator
        if (furnace.canSmelt()) {
            graphics.blit(texture, this.leftPos + 70, this.topPos + 16, 176, 0, 18, 18);
        }
    }

    private void drawCustomInfoStat(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, String[] text) {
        if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
            List<Component> components = new ArrayList<>();
            for (String s : text) {
                components.add(Component.literal(s));
            }
            graphics.renderTooltip(this.font, components, java.util.Optional.empty(), mouseX, mouseY);
        }
    }
}