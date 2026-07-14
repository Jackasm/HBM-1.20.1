package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class MachineCrystallizerItemRenderer extends BlockEntityWithoutLevelRenderer {

    public MachineCrystallizerItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.1, 0.5);
                poseStack.scale(0.15f, 0.13f, 0.15f);
                poseStack.mulPose(new Quaternionf().rotationXYZ(
                        (float) Math.toRadians(15),
                        (float) Math.toRadians(45),
                        0
                ));
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
                poseStack.mulPose(new Quaternionf().rotationXYZ(
                        0,
                        (float) Math.toRadians(45),
                        0
                ));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.15f, 0.15f, 0.15f);
                poseStack.mulPose(new Quaternionf().rotationXYZ(
                        0,
                        (float) Math.toRadians(45),
                        0
                ));
                break;
            default:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        // Рендерим модель кристаллизатора
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.crystallizer_tex));

        // Body
        HBMResourceManager.crystallizer.renderPart(poseStack, consumer, "Body", packedLight, packedOverlay);

        // Spinner (вращающаяся часть)
        HBMResourceManager.crystallizer.renderPart(poseStack, consumer, "Spinner", packedLight, packedOverlay);

        poseStack.popPose();
    }
}