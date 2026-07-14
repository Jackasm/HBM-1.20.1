package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RadarItemRenderer extends BlockEntityWithoutLevelRenderer {

    public RadarItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext transformType,
                             PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.2F, 0.5F);

        // Масштабирование для разных контекстов
        if (transformType == ItemDisplayContext.GUI) {
            poseStack.scale(0.3F, 0.3F, 0.3F);
            poseStack.mulPose(Axis.XP.rotationDegrees(20));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
        } else if (transformType == ItemDisplayContext.GROUND) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.scale(0.6F, 0.6F, 0.6F);
        } else {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        // Base
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.radar_base_tex));
        HBMResourceManager.radar.renderPart(poseStack, consumer, "Base", packedLight, OverlayTexture.NO_OVERLAY);

        // Dish (повёрнута для красоты в инвентаре)
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.radar_dish_tex));
        HBMResourceManager.radar.renderPart(poseStack, consumer, "Dish", packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}