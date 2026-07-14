package com.hbm.render.item;

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

public class AutosawItemRenderer extends BlockEntityWithoutLevelRenderer {

    public AutosawItemRenderer() {
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
                poseStack.translate(1, 0.9, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
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
                poseStack.translate(2.3, 1.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        // Для GROUND контекста используем максимальное освещение
        int light = (context == ItemDisplayContext.GROUND) ? 0xF000F0 : packedLight;

        poseStack.translate(0, -3.5, -3);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));

        double spin = (System.currentTimeMillis() % 3600) * 0.1D;

        renderItemCommon(poseStack, buffer, 0D, 80D, spin, 0D, light, packedOverlay);

        poseStack.popPose();
    }

    private void renderItemCommon(PoseStack poseStack, MultiBufferSource buffer,
                                  double turn, double angle, double spin, double engine,
                                  int light, int overlay) {

        ResourceLocation texture = HBMResourceManager.autosaw_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "Base", light, overlay);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) -turn));
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "Main", light, overlay);
        poseStack.pushPose();
        poseStack.translate(0, engine * 0.01, 0);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "Engine", light, overlay);
        poseStack.popPose();

        poseStack.translate(0, 1.75, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) angle));
        poseStack.translate(0, -1.75, 0);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "ArmUpper", light, overlay);

        poseStack.translate(0, 1.75, -4);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) -angle * 2));
        poseStack.translate(0, -1.75, 4);
        poseStack.translate(-0.01, 0, 0);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "ArmLower", light, overlay);
        poseStack.translate(0.01, 0, 0);

        poseStack.translate(0, 1.75, -8);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) angle));
        poseStack.translate(0, -1.75, 8);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "ArmTip", light, overlay);

        poseStack.translate(0, 1.75, -10);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) spin));
        poseStack.translate(0, -1.75, 10);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "Sawblade", light, overlay);
    }
}