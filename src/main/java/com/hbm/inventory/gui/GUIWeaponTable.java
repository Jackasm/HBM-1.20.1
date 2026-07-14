package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerWeaponTable;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.render.item.weapon.sedna.BaseGunRenderer;
import com.hbm.render.item.weapon.sedna.RegistryGunRender;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIWeaponTable extends AbstractContainerScreen<ContainerWeaponTable> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_weapon_modifier.png");

    private double yaw = 20;
    private double pitch = -10;

    public GUIWeaponTable(ContainerWeaponTable container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 240;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Обработка вращения камеры
        if (isHovering(8, 18, 160, 79, mouseX, mouseY)) {
            if (GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS) {
                double distX = (leftPos + 8 + 80) - mouseX;
                double distY = (topPos + 18 + 39.5) - mouseY;
                yaw = distX / 80D * -180D;
                pitch = distY / 39.5D * 90D;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Кнопка переключения конфигурации
        if (isHovering(26, 111, 7, 10, mouseX, mouseY)) {
            menu.nextIndex();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable("container.weaponsTable").getString();
        graphics.drawString(font, name, (imageWidth) / 2 - font.width(name) / 2, 6, 0xFFFFFF, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Индикатор текущей конфигурации
        int configs = menu.getConfigCount();
        if (configs > 0) {
            graphics.blit(TEXTURE, leftPos + 35, topPos + 112, 176 + 6 * menu.index, 0, 6, 8);
        }

        // Рендер оружия
        ItemStack gun = menu.getGun();
        if (!gun.isEmpty() && gun.getItem() instanceof GunItem) {
            renderWeapon(graphics, gun);
        }
    }

    private void renderWeapon(GuiGraphics graphics, ItemStack gun) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(leftPos + 88, topPos + 57, 100);

        // Рендер через кастомный рендерер оружия
        BaseGunRenderer renderGun = RegistryGunRender.getRenderer(gun.getItem());

        RenderSystem.enableBlend();
        RenderSystem.enableCull();

        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180));
        poseStack.popPose();

        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees((float) yaw));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees((float) pitch));

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderGun.renderModTable(gun, poseStack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY);
        buffer.endBatch();


        poseStack.popPose();
    }

    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + width && mouseY >= topPos + y && mouseY < topPos + y + height;
    }
}