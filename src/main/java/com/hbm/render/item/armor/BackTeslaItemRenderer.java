package com.hbm.render.item.armor;

import com.hbm.render.model.ModelBackTesla;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BackTeslaItemRenderer extends BlockEntityWithoutLevelRenderer {

    private ModelBackTesla model;

    public BackTeslaItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        if (context == ItemDisplayContext.GUI) {
            poseStack.translate(0.2, 0.2, 0.5);
            poseStack.scale(0.06F, 0.06F, 0.06F);
            poseStack.mulPose(Axis.XP.rotationDegrees(15));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
        } else if (context == ItemDisplayContext.GROUND) {
            poseStack.translate(0.5, 0, 0.5);
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } else if (context == ItemDisplayContext.FIXED) {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } else {
            poseStack.translate(0.5, 0.1, 0.7);
            poseStack.scale(0.06F, 0.06F, 0.06F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }

        if (model == null) {
            model = new ModelBackTesla(Minecraft.getInstance().getEntityModels().bakeLayer(ModelBackTesla.LAYER_LOCATION));
        }

        var consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(ModelBackTesla.TEXTURE));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}