package com.hbm.render.block;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.storage.TileEntitySoyuzCapsule;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderSoyuzCapsule implements BlockEntityRenderer<TileEntitySoyuzCapsule> {

    public RenderSoyuzCapsule(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntitySoyuzCapsule te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);

        poseStack.mulPose(Axis.YP.rotationDegrees(-25));
        poseStack.mulPose(Axis.ZP.rotationDegrees(15));
        poseStack.translate(0.0F, -0.25F, 0.0F);

        ResourceLocation capsuleTex = HBMResourceManager.soyuz_lander_tex;

        HBMResourceManager.soyuz_lander.renderPart(poseStack, buffer, "Capsule", capsuleTex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntitySoyuzCapsule te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}