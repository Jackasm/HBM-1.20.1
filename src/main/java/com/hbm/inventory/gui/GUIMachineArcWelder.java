package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineArcWelder;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.tileentity.machine.TileEntityMachineArcWelder;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
public class GUIMachineArcWelder extends AbstractContainerScreen<ContainerMachineArcWelder> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_arc_welder.png");
    private final TileEntityMachineArcWelder welder;

    public GUIMachineArcWelder(ContainerMachineArcWelder container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.welder = container.getWelder();
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Tank tooltip
        welder.tank.renderTankInfo(graphics, mouseX, mouseY, leftPos + 35, topPos + 63, 34, 16);

        // Power tooltip
        if (isHovering(152, 18, 16, 52, mouseX, mouseY)) {
            String text = String.format("%,d / %,d HE", welder.getPower(), welder.getMaxPower());
            graphics.renderTooltip(font, Component.literal(text), mouseX, mouseY);
        }

        // Upgrade info tooltip
        if (isHovering(78, 67, 8, 8, mouseX, mouseY)) {
            List<Component> info = getUpgradeInfo();
            if (!info.isEmpty()) {
                graphics.renderComponentTooltip(font, info, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(welder.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2 - 18, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Power gauge
        int power = (int) (welder.getPower() * 52 / Math.max(welder.getMaxPower(), 1));
        if (power > 0) {
            graphics.blit(TEXTURE, leftPos + 152, topPos + 70 - power, 176, 52 - power, 16, power);
        }

        // Progress bar
        int progress = welder.progress * 33 / Math.max(welder.processTime, 1);
        if (progress > 0) {
            graphics.blit(TEXTURE, leftPos + 72, topPos + 37, 192, 0, progress, 14);
        }

        // Power indicator (has enough power)
        if (welder.getPower() >= welder.consumption) {
            graphics.blit(TEXTURE, leftPos + 156, topPos + 4, 176, 52, 9, 12);
        }

        // Upgrade info panel button
        graphics.blit(TEXTURE, leftPos + 78, topPos + 67, 176, 64, 8, 8);

        // Tank
        welder.tank.renderTank(graphics, leftPos + 35, topPos + 79, 34, 16, 1);
    }

    private List<Component> getUpgradeInfo() {
        List<Component> list = new ArrayList<>();
        for (UpgradeType type : UpgradeType.values()) {
            int level = welder.upgradeManager.getLevel(type);
            if (level > 0) {
                list.add(Component.literal(type.name() + ": " + level));
            }
        }
        if (list.isEmpty()) {
            list.add(Component.literal("No upgrades installed").withStyle(ChatFormatting.GRAY));
        }
        return list;
    }

    protected boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w && mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}