package com.hbm.render.item;

import com.hbm.items.ModToolItems;
import com.hbm.render.util.RenderMiscEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class MeteorSwordRenderer extends BlockEntityWithoutLevelRenderer {

    public MeteorSwordRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);

        poseStack.pushPose();

        // --- Трансформации как в оригинале ---
        switch (context) {
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.translate(0.5, 0.5, 0);
                poseStack.translate(-0.5, -0.5, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.translate(0.5, 0.5, 0);
                poseStack.scale(2.72F, 2.72F, 1.36F);
                poseStack.translate(-0.5, -0.5, 0.25);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.translate(0.2, 0.55, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(45));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-45));
                poseStack.scale(1.7F, 1.7F, 0.85F);
                break;
            case GUI:
                // Для инвентаря трансформации не нужны
                break;
            default:
                break;
        }

        // --- Рендерим сам меч (основная текстура) ---
        itemRenderer.render(stack, context, false, poseStack, buffer, packedLight, packedOverlay, model);

        // --- Рендерим глит (свечение) с кастомным цветом в зависимости от предмета ---
        float[] colors = getColorsForItem(stack.getItem());
        if (colors != null) {
            renderGlint(poseStack, buffer, packedLight, mc, colors[0], colors[1], colors[2]);
        }

        poseStack.popPose();
    }

    /**
     * Возвращает цвета для конкретного предмета
     * @param item предмет
     * @return массив {r, g, b} или null если предмет не найден
     */
    private float[] getColorsForItem(Item item) {
        // Обычный метеоритный меч — белый
        if (item == ModToolItems.METEORITE_SWORD.get()) {
            return new float[]{1.0F, 1.0F, 1.0F};
        }
        // Seared — оранжевый
        if (item == ModToolItems.METEORITE_SWORD_SEARED.get()) {
            return new float[]{1.0F, 0.5F, 0.0F};
        }
        // Reforged — голубой
        if (item == ModToolItems.METEORITE_SWORD_REFORGED.get()) {
            return new float[]{0.5F, 1.0F, 1.0F};
        }
        // Hardened — серый
        if (item == ModToolItems.METEORITE_SWORD_HARDENED.get()) {
            return new float[]{0.25F, 0.25F, 0.25F};
        }
        // Alloyed — синий
        if (item == ModToolItems.METEORITE_SWORD_ALLOYED.get()) {
            return new float[]{0.0F, 0.5F, 1.0F};
        }
        // Machined — жёлтый
        if (item == ModToolItems.METEORITE_SWORD_MACHINED.get()) {
            return new float[]{1.0F, 1.0F, 0.0F};
        }
        // Treated — зелёный
        if (item == ModToolItems.METEORITE_SWORD_TREATED.get()) {
            return new float[]{0.5F, 1.0F, 0.5F};
        }
        // Etched — светло-жёлтый
        if (item == ModToolItems.METEORITE_SWORD_ETCHED.get()) {
            return new float[]{1.0F, 1.0F, 0.5F};
        }
        // Bred — тёмно-жёлтый
        if (item == ModToolItems.METEORITE_SWORD_BRED.get()) {
            return new float[]{0.5F, 0.5F, 0.0F};
        }
        // Irradiated — ярко-зелёный
        if (item == ModToolItems.METEORITE_SWORD_IRRADIATED.get()) {
            return new float[]{0.75F, 1.0F, 0.0F};
        }
        // Fused — розовый
        if (item == ModToolItems.METEORITE_SWORD_FUSED.get()) {
            return new float[]{1.0F, 0.0F, 0.5F};
        }
        // Baleful — зелёный
        if (item == ModToolItems.METEORITE_SWORD_BALEFUL.get()) {
            return new float[]{0.0F, 1.0F, 0.0F};
        }
        return null;
    }

    /**
     * Рендерит глит (свечение) с кастомным цветом
     */
    private void renderGlint(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                             Minecraft mc, float r, float g, float b) {
        float partialTicks = mc.getFrameTime();
        float offset = mc.player != null ? mc.player.tickCount + partialTicks : 0;

        // Сохраняем состояние OpenGL
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL11.GL_EQUAL);
        RenderSystem.depthMask(false);

        float glintColor = 0.76F;
        float speed = 20.0F;
        float scale = 1F / 3F;

        // Два прохода глита (как в оригинале)
        for (int k = 0; k < 2; ++k) {
            poseStack.pushPose();

            float movement = offset * (0.001F + (float) k * 0.003F) * speed;

            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.ZP.rotationDegrees(30.0F - (float) k * 60.0F));
            poseStack.translate(0.0F, movement, 0.0F);

            // Получаем VertexConsumer для глита с кастомной текстурой
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(RenderMiscEffects.GLINT));

            // Устанавливаем кастомный цвет
            RenderSystem.setShaderColor(
                    r * glintColor,
                    g * glintColor,
                    b * glintColor,
                    0.5F // прозрачность
            );

            // Здесь должен быть рендер модели с глитом
            // В 1.20.1 это сложно сделать без кастомного RenderType
            // Поэтому пока оставляем как заглушку

            poseStack.popPose();
        }

        // Восстанавливаем состояние
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}