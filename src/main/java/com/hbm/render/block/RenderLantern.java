package com.hbm.render.block;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.deco.TileEntityLantern;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderLantern implements BlockEntityRenderer<TileEntityLantern> {

    public RenderLantern(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityLantern te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        ResourceLocation tex = HBMResourceManager.lantern_tex;
        HBMResourceManager.lantern.renderPart(poseStack, buffer, "Lantern", tex, packedLight, packedOverlay);

        float mult = (float) (Math.sin(System.currentTimeMillis() / 200D) / 2 + 0.5) * 0.1F + 0.9F;
        HBMResourceManager.lantern.renderPartColored(
                poseStack, buffer, "Light",
                tex, packedLight, packedOverlay,
                1F * mult, 1F * mult, 0.7F * mult, 1F
        );

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityLantern te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}