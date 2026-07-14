package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SolarMirrorItemRenderer extends BlockEntityWithoutLevelRenderer {

    public SolarMirrorItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        // Трансформации для разных контекстов
        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case GROUND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
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
                poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        int light = (context == ItemDisplayContext.GROUND) ? 0xF000F0 : packedLight;

        // Рендерим модель
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.solar_mirror_tex));
        HBMResourceManager.solar_mirror.renderPart(poseStack, consumer, "Base", light, packedOverlay);
        HBMResourceManager.solar_mirror.renderPart(poseStack, consumer, "Mirror", light, packedOverlay);

        poseStack.popPose();
    }
}