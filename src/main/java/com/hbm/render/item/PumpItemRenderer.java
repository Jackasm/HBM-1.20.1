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

public class PumpItemRenderer  extends BlockEntityWithoutLevelRenderer {
    public PumpItemRenderer() {
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
                poseStack.scale(0.18f, 0.18f, 0.18f);
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

        boolean isSteam = stack.getItem() == ModBlocks.PUMP_STEAM.get().asItem();

        ResourceLocation texture = isSteam ? HBMResourceManager.pump_steam_tex : HBMResourceManager.pump_electric_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // Рендерим все части (без анимации в инвентаре)
        float rot = (System.currentTimeMillis() % 3600) * 0.1F;

        HBMResourceManager.pump.renderPart(poseStack, vertexConsumer, "Base", light, packedOverlay);

        // Rotor с анимацией
        poseStack.pushPose();
        poseStack.translate(0, 2.25, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rot - 90));
        poseStack.translate(0, -2.25, 0);
        HBMResourceManager.pump.renderPart(poseStack, vertexConsumer, "Rotor", light, packedOverlay);
        poseStack.popPose();

        double sin = Math.sin(rot * Math.PI / 180D) * 0.5D - 0.5D;
        double cos = Math.cos(rot * Math.PI / 180D) * 0.5D;
        double ang = Math.acos(cos / 2D);
        double cath = Math.sqrt(1 + (cos * cos) / 2);

        // Arms
        poseStack.pushPose();
        poseStack.translate(0, 1 - cath + sin, 0);
        poseStack.translate(0, 4.75, 0);
        poseStack.mulPose(Axis.ZN.rotationDegrees((float) (ang * 180D / Math.PI - 90D)));
        poseStack.translate(0, -4.75, 0);
        HBMResourceManager.pump.renderPart(poseStack, vertexConsumer, "Arms", light, packedOverlay);
        poseStack.popPose();

        // Piston
        poseStack.pushPose();
        poseStack.translate(0, 1 - cath + sin, 0);
        HBMResourceManager.pump.renderPart(poseStack, vertexConsumer, "Piston", light, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}
