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

public class SteamEngineItemRenderer extends BlockEntityWithoutLevelRenderer {

    public SteamEngineItemRenderer() {
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
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.13f, 0.13f, 0.13f);
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
                poseStack.scale(0.24f, 0.24f, 0.24f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.24f, 0.24f, 0.24f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        int light = (context == ItemDisplayContext.GROUND) ? 0xF000F0 : packedLight;

        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        ResourceLocation texture = HBMResourceManager.steam_engine_tex;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        float rot = (System.currentTimeMillis() % 3600) * 0.1F;

        HBMResourceManager.steam_engine.renderPart(poseStack, consumer, "Base", light, packedOverlay);

        poseStack.pushPose();
        poseStack.translate(2, 1.375, 0);
        poseStack.mulPose(Axis.ZN.rotationDegrees(rot));
        poseStack.translate(-2, -1.375, 0);
        HBMResourceManager.steam_engine.renderPart(poseStack, consumer, "Flywheel", light, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 1.375, -0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(rot * 2));
        poseStack.translate(0, -1.375, 0.5);
        HBMResourceManager.steam_engine.renderPart(poseStack, consumer, "Shaft", light, packedOverlay);
        poseStack.popPose();

        double sin = Math.sin(rot * Math.PI / 180D) * 0.25D - 0.25D;
        double cos = Math.cos(rot * Math.PI / 180D) * 0.25D;
        double ang = Math.acos(cos / 1.875D);

        poseStack.pushPose();
        poseStack.translate(sin, cos, 0);
        poseStack.translate(2.25, 1.375, 0);
        poseStack.mulPose(Axis.ZN.rotationDegrees((float) (ang * 180D / Math.PI - 90D)));
        poseStack.translate(-2.25, -1.375, 0);
        HBMResourceManager.steam_engine.renderPart(poseStack, consumer, "Transmission", light, packedOverlay);
        poseStack.popPose();

        double cath = Math.sqrt(3.515625D - (cos * cos) / 2);
        poseStack.translate(1.875 - cath + sin, 0, 0);
        HBMResourceManager.steam_engine.renderPart(poseStack, consumer, "Piston", light, packedOverlay);

        poseStack.popPose();
    }
}