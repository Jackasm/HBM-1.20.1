package com.hbm.render.item;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SawmillItemRenderer extends BlockEntityWithoutLevelRenderer {

    public SawmillItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        // Настройка трансформаций для разных контекстов
        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.2f, 0.2f, 0.2f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case GROUND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.15f, 0.15f, 0.15f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.15f, 0.15f, 0.15f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        // Для GROUND контекста используем максимальное освещение
        int light = (context == ItemDisplayContext.GROUND) ? 0xF000F0 : packedLight;

        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        boolean hasBlade = stack.getDamageValue() != 1;
        float rot = hasBlade ? (System.currentTimeMillis() % 3600) * 0.1F : 0;

        renderItemCommon(poseStack, buffer, rot, hasBlade, light, packedOverlay);

        poseStack.popPose();
    }

    private void renderItemCommon(PoseStack poseStack, MultiBufferSource buffer, float rot, boolean hasBlade,
                                  int light, int overlay) {

        ResourceLocation texture = HBMResourceManager.sawmill_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // Рендерим основную часть
        HBMResourceManager.sawmill.renderPart(poseStack, vertexConsumer, "Main", light, overlay);

        if (hasBlade) {
            poseStack.pushPose();
            poseStack.translate(0, 1.375, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rot * 2));
            poseStack.translate(0, -1.375, 0);
            HBMResourceManager.sawmill.renderPart(poseStack, vertexConsumer, "Blade", light, overlay);
            poseStack.popPose();
        }

        // Левая шестерня
        poseStack.pushPose();
        poseStack.translate(0.5625, 1.375, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
        poseStack.translate(-0.5625, -1.375, 0);
        HBMResourceManager.sawmill.renderPart(poseStack, vertexConsumer, "GearLeft", light, overlay);
        poseStack.popPose();

        // Правая шестерня
        poseStack.pushPose();
        poseStack.translate(-0.5625, 1.375, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-rot));
        poseStack.translate(0.5625, -1.375, 0);
        HBMResourceManager.sawmill.renderPart(poseStack, vertexConsumer, "GearRight", light, overlay);
        poseStack.popPose();
    }
}