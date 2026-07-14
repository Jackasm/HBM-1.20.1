package com.hbm.render.item.weapon;

import com.hbm.items.ModToolItems;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WeaponSpecialRenderer extends BlockEntityWithoutLevelRenderer {

    public WeaponSpecialRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        Item item = stack.getItem();

        if (item == ModToolItems.SHIMMER_SLEDGE.get()) {
            renderShimmerSledge(poseStack, buffer, context, packedLight, packedOverlay);
        } else if (item == ModToolItems.SHIMMER_AXE.get()) {
            renderShimmerAxe(poseStack, buffer, context, packedLight, packedOverlay);
        } else if (item == ModToolItems.STOPSIGN.get() ||
                item == ModToolItems.SOPSIGN.get() ||
                item == ModToolItems.CHERNOBYLSIGN.get()) {
            renderSign(poseStack, buffer, context, packedLight, packedOverlay, item);
        } else if (item == ModToolItems.WOOD_GAVEL.get() ||
                item == ModToolItems.LEAD_GAVEL.get() ||
                item == ModToolItems.DIAMOND_GAVEL.get()) {
            renderGavel(poseStack, buffer, context, packedLight, packedOverlay, item);
        }
    }

    private void renderShimmerSledge(PoseStack poseStack, MultiBufferSource buffer,
                                     ItemDisplayContext context, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        setupShimmerTransform(poseStack, context);
        ResourceLocation texture = HBMResourceManager.shimmer_sledge_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        HBMResourceManager.shimmer_sledge.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderShimmerAxe(PoseStack poseStack, MultiBufferSource buffer,
                                  ItemDisplayContext context, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        setupShimmerTransform(poseStack, context);

        HBMResourceManager.shimmer_axe.renderAll(poseStack, buffer,
                HBMResourceManager.shimmer_axe_tex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderSign(PoseStack poseStack, MultiBufferSource buffer,
                            ItemDisplayContext context, int packedLight, int packedOverlay, Item item) {
        poseStack.pushPose();

        setupSignTransform(poseStack, context, false);

        ResourceLocation texture = getSignTexture(item);
        HBMResourceManager.stopsign.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderGavel(PoseStack poseStack, MultiBufferSource buffer,
                             ItemDisplayContext context, int packedLight, int packedOverlay, Item item) {
        poseStack.pushPose();

        setupGavelTransform(poseStack, context, item);

        ResourceLocation texture = getGavelTexture(item);
        HBMResourceManager.gavel.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void setupShimmerTransform(PoseStack poseStack, ItemDisplayContext context) {
        switch (context) {
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.mulPose(Axis.ZP.rotationDegrees(-135.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                poseStack.scale(1.5F, 1.5F, 1.5F);
                poseStack.translate(0.45F, -0.3F, 0.0F);
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5F, -0.3F, 0.3F);
                poseStack.scale(-1.7F, 1.7F, -1.7F);
                poseStack.mulPose(Axis.YN.rotationDegrees(90.0F));
                break;
            case GROUND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                break;
            case GUI:
                poseStack.translate(0, 0, 0.5);
                poseStack.scale(-1.3f, 1.3f, -1.3f);
                poseStack.mulPose(Axis.ZN.rotationDegrees(-50.0F));
                break;
            default:
                break;
        }
    }

    private void setupSignTransform(PoseStack poseStack, ItemDisplayContext context, boolean isShimmer) {
        switch (context) {
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                poseStack.scale(0.35F, 0.35F, 0.35F);
                poseStack.translate(2.0F, -2.0F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.translate(-1.0F, -2F, 0.0F);
                poseStack.scale(0.6f, 0.6f, -0.6f);
                break;
            case GROUND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                break;
            case GUI:
                poseStack.translate(0, 0, 0.5);
                poseStack.scale(0.2f, 0.2f, 0.2f);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.XN.rotationDegrees(-45.0F));
                break;
            default:
                break;
        }
    }

    private void setupGavelTransform(PoseStack poseStack, ItemDisplayContext context, Item item) {
        switch (context) {
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                poseStack.translate(1.375F, 0.0F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                if (item == ModToolItems.DIAMOND_GAVEL.get()) {
                    poseStack.scale(2.0F, 2.0F, 2.0F);
                    poseStack.translate(0.0F, 0.25F, 0.0F);
                }
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(1.0F, 0.5F, 0.0F);
                if (item == ModToolItems.DIAMOND_GAVEL.get()) {
                    poseStack.scale(2.0F, 2.0F, 2.0F);
                }
                break;
            case GROUND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case GUI:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.625f, 0.625f, 0.625f);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.XN.rotationDegrees(45.0F));
                break;
            default:
                break;
        }
    }

    private ResourceLocation getSignTexture(Item item) {
        if (item == ModToolItems.STOPSIGN.get()) return HBMResourceManager.stopsign_tex;
        if (item == ModToolItems.SOPSIGN.get()) return HBMResourceManager.sopsign_tex;
        if (item == ModToolItems.CHERNOBYLSIGN.get()) return HBMResourceManager.chernobylsign_tex;
        return HBMResourceManager.stopsign_tex;
    }

    private ResourceLocation getGavelTexture(Item item) {
        if (item == ModToolItems.WOOD_GAVEL.get()) return HBMResourceManager.gavel_wood_tex;
        if (item == ModToolItems.LEAD_GAVEL.get()) return HBMResourceManager.gavel_lead_tex;
        if (item == ModToolItems.DIAMOND_GAVEL.get()) return HBMResourceManager.gavel_diamond_tex;
        return HBMResourceManager.gavel_wood_tex;
    }
}