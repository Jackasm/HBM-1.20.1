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

public class SkeletonHolderItemRenderer extends BlockEntityWithoutLevelRenderer {

    public SkeletonHolderItemRenderer() {
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
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(20));
            poseStack.mulPose(Axis.YP.rotationDegrees(-135));
        } else if (transformType == ItemDisplayContext.GROUND) {
            poseStack.scale(0.8F, 0.8F, 0.8F);
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        } else {
            poseStack.scale(0.8F, 0.8F, 0.8F);
        }

        // Рендерим модель
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.skeleton_holder_tex));
        HBMResourceManager.skeleton_holder.renderPart(poseStack, consumer, "Holder1", packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}