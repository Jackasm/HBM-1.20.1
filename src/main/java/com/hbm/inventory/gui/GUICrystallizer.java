package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerCrystallizer;
import com.hbm.tileentity.machine.TileEntityMachineCrystallizer;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUICrystallizer extends AbstractContainerScreen<ContainerCrystallizer> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_crystallizer.png");
    private final TileEntityMachineCrystallizer crystallizer;

    public GUICrystallizer(ContainerCrystallizer container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.crystallizer = container.getCrystallizer();
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Power tooltip
        if (isHovering(152, 18, 16, 52, mouseX, mouseY)) {
            String text = String.format("%,d / %,d HE", crystallizer.getPower(), crystallizer.getMaxPower());
            graphics.renderTooltip(font, Component.literal(text), mouseX, mouseY);
        }

        // Tank tooltip
        crystallizer.tank.renderTankInfo(graphics, mouseX, mouseY, leftPos + 35, topPos + 18, 16, 52);

        // Upgrade tooltip
        if (isHovering(117, 22, 8, 8, mouseX, mouseY)) {
            String[] upgradeText = new String[4];
            upgradeText[0] = Component.translatable("desc.gui.upgrade").getString();
            upgradeText[1] = Component.translatable("desc.gui.upgrade.speed").getString();
            upgradeText[2] = Component.translatable("desc.gui.upgrade.effectiveness").getString();
            upgradeText[3] = Component.translatable("desc.gui.upgrade.overdrive").getString();
            graphics.renderComponentTooltip(font,
                    List.of(
                            Component.literal(upgradeText[0]),
                            Component.literal(upgradeText[1]),
                            Component.literal(upgradeText[2]),
                            Component.literal(upgradeText[3])
                    ),
                    mouseX,
                    mouseY
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(crystallizer.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Power bar
        int power = (int) (crystallizer.getPowerScaled(52));
        if (power > 0) {
            graphics.blit(TEXTURE, leftPos + 152, topPos + 70 - power, 176, 64 - power, 16, power);
        }

        // Progress
        int progress = crystallizer.getProgressScaled(28);
        if (progress > 0) {
            graphics.blit(TEXTURE, leftPos + 80, topPos + 47, 176, 0, progress, 12);
        }

        // Upgrade info panel
        graphics.blit(TEXTURE, leftPos + 117, topPos + 22, 176, 52, 8, 8);

        // Tank
        crystallizer.tank.renderTank(graphics, leftPos + 35, topPos + 70, 16, 52, 1);
    }

    protected boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w && mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}