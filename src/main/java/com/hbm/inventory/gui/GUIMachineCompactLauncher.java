package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerCompactLauncher;
import com.hbm.items.weapon.ItemCustomMissile;

import com.hbm.render.util.MissileMultipart;
import com.hbm.render.util.MissilePronter;
import com.hbm.tileentity.bomb.TileEntityCompactLauncher;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIMachineCompactLauncher extends AbstractContainerScreen<ContainerCompactLauncher> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/weapon/gui_launch_table_small.png");
    private final TileEntityCompactLauncher launcher;

    public GUIMachineCompactLauncher(ContainerCompactLauncher container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.launcher = container.getLauncher();
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Tooltips для баков
        if (isHovering(116, 36, 16, 34, mouseX, mouseY)) {
            launcher.tanks[0].renderTankInfo(graphics, mouseX, mouseY, leftPos + 116, topPos + 36, 16, 34);
        }
        if (isHovering(134, 36, 16, 34, mouseX, mouseY)) {
            launcher.tanks[1].renderTankInfo(graphics, mouseX, mouseY, leftPos + 134, topPos + 36, 16, 34);
        }
        if (isHovering(152, 36, 16, 52, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.literal("Solid Fuel: " + launcher.solidFuel + "l"), mouseX, mouseY);
        }
        if (isHovering(134, 113, 34, 6, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.literal("Power: " + launcher.getPower() + " / " + launcher.getMaxPower()), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = launcher.getDisplayName().getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Power bar
        int power = (int) launcher.getPowerScaled(34);
        graphics.blit(TEXTURE, leftPos + 134, topPos + 113, 176, 96, power, 6);

        // Solid fuel bar
        int solid = launcher.getSolidScaled(52);
        graphics.blit(TEXTURE, leftPos + 152, topPos + 88 - solid, 176, 96 - solid, 16, solid);

        // Индикаторы готовности (аналогично оригиналу)
        if (launcher.isMissileValid()) {
            graphics.blit(TEXTURE, leftPos + 25, topPos + 35, 176, 26, 18, 18);
        }
        if (launcher.hasDesignator()) {
            graphics.blit(TEXTURE, leftPos + 25, topPos + 71, 176, 26, 18, 18);
        }

        if (launcher.liquidState() == 1) graphics.blit(TEXTURE, leftPos + 121, topPos + 23, 176, 0, 6, 8);
        if (launcher.liquidState() == 0) graphics.blit(TEXTURE, leftPos + 121, topPos + 23, 182, 0, 6, 8);
        if (launcher.oxidizerState() == 1) graphics.blit(TEXTURE, leftPos + 139, topPos + 23, 176, 0, 6, 8);
        if (launcher.oxidizerState() == 0) graphics.blit(TEXTURE, leftPos + 139, topPos + 23, 182, 0, 6, 8);
        if (launcher.solidState() == 1) graphics.blit(TEXTURE, leftPos + 157, topPos + 23, 176, 0, 6, 8);
        if (launcher.solidState() == 0) graphics.blit(TEXTURE, leftPos + 157, topPos + 23, 182, 0, 6, 8);

        // Отображение ракеты (как в оригинале)
        if (launcher.isMissileValid()) {
            renderMissile(graphics, launcher.getInventory().getStackInSlot(0), leftPos + 88, topPos + 115);
        }

        // Информационные иконки
        graphics.blit(TEXTURE, leftPos - 16, topPos + 36, 176, 122, 16, 16);
        graphics.blit(TEXTURE, leftPos - 16, topPos + 52, 176, 122, 16, 16);

        // Баки
        launcher.tanks[0].renderTank(graphics, leftPos + 116, topPos + 70, 16, 34, 0);
        launcher.tanks[1].renderTank(graphics, leftPos + 134, topPos + 70, 16, 34, 0);
    }

    private void renderMissile(GuiGraphics graphics, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) return;

        // Получаем структуру ракеты
        MissileMultipart missile = MissileMultipart.loadFromStruct(ItemCustomMissile.getStruct(stack));

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 100);

        double size = 5 * 18;
        double scale = size / Math.max(missile.getHeight(), 6);
        graphics.pose().scale((float) scale, (float) scale, (float) scale);

        graphics.pose().mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
        graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
        graphics.pose().scale(-1, -1, -1);

        // Рендерим модель (используем MissilePronter)
        MissilePronter.prontMissile(missile, graphics.bufferSource(), graphics.pose(), 0xF000F0);

        graphics.pose().popPose();
    }

    protected boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w &&
                mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}