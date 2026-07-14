package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerFurnaceSteel;
import com.hbm.tileentity.machine.TileEntityFurnaceSteel;
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
import java.util.Locale;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIFurnaceSteel extends AbstractContainerScreen<ContainerFurnaceSteel> {

    private static final ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_furnace_steel.png");
    private final TileEntityFurnaceSteel furnace;

    public GUIFurnaceSteel(ContainerFurnaceSteel container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.furnace = container.getFurnace();

        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        for (int i = 0; i < 3; i++) {
            // Progress tooltip
            drawCustomInfoStat(graphics, mouseX, mouseY, this.leftPos + 53, this.topPos + 17 + 18 * i, 70, 7,
                    new String[]{String.format(Locale.US, "%,d", furnace.progress[i]) +
                            " / " + String.format(Locale.US, "%,d", TileEntityFurnaceSteel.processTime) + "TU"});

            // Bonus tooltip
            drawCustomInfoStat(graphics, mouseX, mouseY, this.leftPos + 53, this.topPos + 26 + 18 * i, 70, 7,
                    new String[]{"Bonus: " + furnace.bonus[i] + "%"});
        }

        // Heat tooltip
        drawCustomInfoStat(graphics, mouseX, mouseY, this.leftPos + 151, this.topPos + 18, 9, 50,
                new String[]{String.format(Locale.US, "%,d", furnace.heat) +
                        " / " + String.format(Locale.US, "%,d", TileEntityFurnaceSteel.maxHeat) + "TU"});
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

        // Heat meter
        int heat = furnace.heat * 48 / TileEntityFurnaceSteel.maxHeat;
        graphics.blit(texture, this.leftPos + 152, this.topPos + 67 - heat, 176, 76 - heat, 7, heat);

        for (int i = 0; i < 3; i++) {
            // Progress bar
            int progress = furnace.progress[i] * 69 / TileEntityFurnaceSteel.processTime;
            graphics.blit(texture, this.leftPos + 54, this.topPos + 18 + 18 * i, 176, 18, progress, 5);

            // Bonus bar
            int bonus = furnace.bonus[i] * 69 / 100;
            graphics.blit(texture, this.leftPos + 54, this.topPos + 27 + 18 * i, 176, 23, bonus, 5);

            // Active indicator
            if (furnace.wasOn) {
                graphics.blit(texture, this.leftPos + 16, this.topPos + 16 + 18 * i, 176, 0, 18, 18);
            }
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