package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
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

public class CargoContainerItemRenderer extends BlockEntityWithoutLevelRenderer {

    public CargoContainerItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();

        // Масштабирование и позиционирование для рендера в руке
        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.mulPose(Axis.XP.rotationDegrees(15));
            poseStack.mulPose(Axis.YP.rotationDegrees(35));
            poseStack.scale(0.16F, 0.16F, 0.16F);
            poseStack.translate(0, 1.5, 0.5);
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.scale(0.1F, 0.1F, 0.1F);
            poseStack.translate(0.5, 0.3, 0.5);
        } else if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.scale(0.1F, 0.1F, 0.1F);
            poseStack.translate(2.5, 4, 0.5);
        } else {
            poseStack.scale(0.1F, 0.1F, 0.1F);
            poseStack.translate(0.5, 0.2, 0.5);
        }

        // Рендерим модель контейнера
        HFRWavefrontObject model = HBMResourceManager.cargo_container;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(HBMResourceManager.cargo_container_tex));

        model.renderAll(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();
    }
}