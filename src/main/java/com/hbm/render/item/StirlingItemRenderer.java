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

public class StirlingItemRenderer extends BlockEntityWithoutLevelRenderer {

    public StirlingItemRenderer() {
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
                poseStack.translate(0.5, 0.15, 0.5);
                poseStack.scale(0.22f, 0.22f, 0.22f);
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

        boolean isCreative = stack.getItem() == ModBlocks.MACHINE_STIRLING_CREATIVE.get().asItem();
        boolean isSteel = stack.getItem() == ModBlocks.MACHINE_STIRLING_STEEL.get().asItem();
        boolean hasCog = stack.getDamageValue() != 1;

        int type = isCreative ? 2 : (isSteel ? 1 : 0);

        ResourceLocation texture = switch (type) {
            case 0 -> HBMResourceManager.stirling_tex;
            case 2 -> HBMResourceManager.stirling_creative_tex;
            default -> HBMResourceManager.stirling_steel_tex;
        };

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));
        float rot = hasCog ? (System.currentTimeMillis() % 3600) * 0.1F : 0;

        // Для инвентаря нужен поворот
        if (context == ItemDisplayContext.GUI) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }

        HBMResourceManager.stirling.renderPart(poseStack, consumer, "Base", light, packedOverlay);

        if (hasCog) {
            poseStack.pushPose();
            poseStack.translate(0, 1.375, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rot));
            poseStack.translate(0, -1.375, 0);
            HBMResourceManager.stirling.renderPart(poseStack, consumer, "Cog", light, packedOverlay);
            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.translate(0, 1.375, 0.25);
        poseStack.mulPose(Axis.XP.rotationDegrees(rot * 2 + 3));
        poseStack.translate(0, -1.375, -0.25);
        HBMResourceManager.stirling.renderPart(poseStack, consumer, "CogSmall", light, packedOverlay);
        poseStack.popPose();

        poseStack.translate(Math.sin(rot * Math.PI / 90D) * 0.25 + 0.125, 0, 0);
        HBMResourceManager.stirling.renderPart(poseStack, consumer, "Piston", light, packedOverlay);

        poseStack.popPose();
    }
}