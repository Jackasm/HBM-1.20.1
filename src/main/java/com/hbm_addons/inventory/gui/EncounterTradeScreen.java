package com.hbm_addons.inventory.gui;

import com.hbm.util.RefStrings;
import com.hbm_addons.inventory.container.EncounterTradeMenu;
import com.hbm_addons.inventory.container.EquipmentSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class EncounterTradeScreen extends AbstractContainerScreen<EncounterTradeMenu> {

    /* ============================================================
     *  ТУТ ВСЁ ПОДГОНЯЕШЬ ПОД СВОЙ GUI
     * ============================================================ */

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/mob/stranger.png");

    // --- ФИГУРКИ ---
    private static final int MOB_PORTRAIT_X = 65;
    private static final int MOB_PORTRAIT_Y = 39;
    private static final int MOB_PORTRAIT_W = 40;
    private static final int MOB_PORTRAIT_H = 50;

    private static final int PLAYER_PORTRAIT_X = 130;
    private static final int PLAYER_PORTRAIT_Y = 39;
    private static final int PLAYER_PORTRAIT_W = 40;
    private static final int PLAYER_PORTRAIT_H = 50;

    private static final float PORTRAIT_SCALE = 26.0F;

    // --- КНОПКИ ---
    private static final int ACCEPT_BUTTON_X = 116;
    private static final int ACCEPT_BUTTON_Y = 62;
    private static final int ACCEPT_BUTTON_W = 20;
    private static final int ACCEPT_BUTTON_H = 20;

    private static final int CANCEL_BUTTON_X = 116;
    private static final int CANCEL_BUTTON_Y = 86;
    private static final int CANCEL_BUTTON_W = 20;
    private static final int CANCEL_BUTTON_H = 20;

    /* ============================================================ */

    public EncounterTradeScreen(EncounterTradeMenu menu, net.minecraft.world.entity.player.Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 176;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(Button.builder(Component.literal("✓"), b -> this.acceptTrade())
                .bounds(leftPos + ACCEPT_BUTTON_X, topPos + ACCEPT_BUTTON_Y,
                        ACCEPT_BUTTON_W, ACCEPT_BUTTON_H)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("✕"), b -> this.onClose())
                .bounds(leftPos + CANCEL_BUTTON_X, topPos + CANCEL_BUTTON_Y,
                        CANCEL_BUTTON_W, CANCEL_BUTTON_H)
                .build());
    }

    private void acceptTrade() {
        // TODO: реализовать обмен.
        // 1. Проверить панели обмена (mobTradePanel / playerTradePanel).
        // 2. Если обе не пусты — перенести предметы.
        // 3. Проверить цены в CAP_NUKA (заглушка пока).
        this.onClose();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 200);

        // Фигурки
        if (menu.getMob() != null) {
            renderEntity(graphics, menu.getMob(),
                    leftPos + MOB_PORTRAIT_X, topPos + MOB_PORTRAIT_Y,
                    MOB_PORTRAIT_W, MOB_PORTRAIT_H, PORTRAIT_SCALE);
        }
        renderEntity(graphics, this.minecraft != null ? this.minecraft.player : null,
                leftPos + PLAYER_PORTRAIT_X, topPos + PLAYER_PORTRAIT_Y,
                PLAYER_PORTRAIT_W, PLAYER_PORTRAIT_H, PORTRAIT_SCALE);
    }

    /** Рендер сущности в прямоугольнике (x, y, w, h) со скейлом scale. */
    private void renderEntity(GuiGraphics graphics, LivingEntity entity,
                              int x, int y, int w, int h, float scale) {
        if (entity == null) return;

        PoseStack pose = graphics.pose();
        pose.pushPose();

        float centerX = x + w / 2.0F;
        float centerY = y + h / 2.0F;

        // Запоминаем текущие повороты
        float oldYaw = entity.getYRot();
        float oldYawHead = entity.yHeadRot;
        float oldYawBody = entity.yBodyRot;
        float oldPitch = entity.getXRot();

        // Фиксированный поворот — лицом к камере (как в ванильном инвентаре)
        entity.setYRot(0.0F);      // тело — к камере (у ванилы вроде 30°)
        entity.setXRot(0.0F);      // без наклона
        entity.yHeadRot = 0.0F;    // голова — туда же
        entity.yBodyRot = 0.0F;    // корпус — туда же
        entity.yHeadRotO = 0.0F;
        entity.yBodyRotO = 0.0F;

        pose.translate(centerX, centerY, 50.0);
        pose.scale(scale, scale, -scale);
        pose.mulPose(new Quaternionf().rotateZ((float) Math.PI));
        pose.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180)));

        EntityRenderDispatcher dispatcher = Objects.requireNonNull(this.minecraft).getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        dispatcher.render(
                entity,
                0.0, 0.0, 0.0,
                0.0F,        // partialTick — фиксируем
                0.0F,        // ещё partialTick (см. сигнатуру)
                pose,
                graphics.bufferSource(),
                0x00F000F0
        );
        dispatcher.setRenderShadow(true);

        // Восстанавливаем повороты
        entity.setYRot(oldYaw);
        entity.setXRot(oldPitch);
        entity.yHeadRot = oldYawHead;
        entity.yBodyRot = oldYawBody;
        entity.yHeadRotO = oldYawHead;
        entity.yBodyRotO = oldYawBody;

        pose.popPose();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        // Имена — не рисуем, чтобы не накладывалось на кастомный фон.
    }
}