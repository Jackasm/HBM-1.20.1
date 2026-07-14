package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineShredder;
import com.hbm.tileentity.machine.TileEntityMachineShredder;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIMachineShredder extends AbstractContainerScreen<ContainerMachineShredder> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_shredder.png");
    private final TileEntityMachineShredder shredder;

    public GUIMachineShredder(ContainerMachineShredder container, Inventory inv, Component title) {
        super(container, inv, title);
        this.shredder = container.getShredder();
        this.imageWidth = 176;
        this.imageHeight = 233;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        // Энергия тултип
        if (mouseX >= leftPos + 8 && mouseX <= leftPos + 24 && mouseY >= topPos + 106 - 88 && mouseY <= topPos + 106) {
            graphics.renderTooltip(font, Component.literal(shredder.power + " / " + TileEntityMachineShredder.MAX_POWER + " HE"), mouseX, mouseY);
        }

        // Предупреждение о лезвиях
        boolean warn = (shredder.getGearLeft() == 0 || shredder.getGearLeft() == 3) ||
                (shredder.getGearRight() == 0 || shredder.getGearRight() == 3);
        if (warn && mouseX >= leftPos - 16 && mouseX <= leftPos && mouseY >= topPos + 36 && mouseY <= topPos + 52) {
            graphics.renderTooltip(font, Component.literal("Error: Shredder blades are broken or missing!"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Энергия
        if (shredder.power > 0) {
            int i = (int) shredder.getPowerScaled(88);
            graphics.blit(TEXTURE, leftPos + 8, topPos + 106 - i, 176, 160 - i, 16, i);
        }

        // Прогресс
        int progress = shredder.getProgressScaled(34);
        graphics.blit(TEXTURE, leftPos + 63, topPos + 89, 176, 54, progress + 1, 18);

        // Левое лезвие
        int leftGear = shredder.getGearLeft();
        if (leftGear != 0) {
            int texY = switch (leftGear) {
                case 1 -> 0;
                case 2 -> 18;
                case 3 -> 36;
                default -> 0;
            };
            graphics.blit(TEXTURE, leftPos + 43, topPos + 71, 176, texY, 18, 18);
        }

        // Правое лезвие
        int rightGear = shredder.getGearRight();
        if (rightGear != 0) {
            int texY = switch (rightGear) {
                case 1 -> 0;
                case 2 -> 18;
                case 3 -> 36;
                default -> 0;
            };
            graphics.blit(TEXTURE, leftPos + 79, topPos + 71, 194, texY, 18, 18);
        }

        // Предупреждение
        boolean warn = (leftGear == 0 || leftGear == 3) || (rightGear == 0 || rightGear == 3);
        if (warn) {
            graphics.blit(TEXTURE, leftPos - 16, topPos + 36, 176, 118, 16, 16);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title.getString(), imageWidth / 2 - font.width(title.getString()) / 2, 6, 4210752, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4210752, false);
    }
}