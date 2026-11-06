package com.hbm.render.block;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.deco.TileEntityLanternBehemoth;
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
public class RenderLanternBehemoth implements BlockEntityRenderer<TileEntityLanternBehemoth> {

    public RenderLanternBehemoth(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityLanternBehemoth te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        if (te.isBroken) {
            poseStack.mulPose(Axis.XP.rotationDegrees(5));
            poseStack.mulPose(Axis.ZP.rotationDegrees(10));
        }

        ResourceLocation tex = HBMResourceManager.lantern_rusty_tex;
        HBMResourceManager.lantern.renderPart(poseStack, buffer, "Lantern", tex, packedLight, packedOverlay);

        float r, g;
        if (te.isBroken) {
            float mult = (float) (Math.sin(System.currentTimeMillis() / 200D) / 2 + 0.5);
            r = mult; g = 0F;
        } else {
            float mult = (float) (Math.sin(System.currentTimeMillis() / 200D) / 2 + 0.5) * 0.5F + 0.5F;
            r = 0F; g = mult;
        }

        HBMResourceManager.lantern.renderPartColored(
                poseStack, buffer, "Light",
                tex, packedLight, packedOverlay,
                r, g, 0F, 1F
        );

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityLanternBehemoth te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}