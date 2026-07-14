package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityCog;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderCog extends EntityRenderer<EntityCog> {

    public RenderCog(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityCog cog, float entityYaw, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        int orientation = cog.getOrientation();
        switch (orientation % 6) {
            case 3 -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            case 5 -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case 2 -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case 4 -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
        }

        poseStack.translate(0, 0, -1);

        if (orientation < 6) {
            float rot = (System.currentTimeMillis() % (360 * 3)) / 3.0F;
            poseStack.mulPose(Axis.ZN.rotationDegrees(rot));
        }

        poseStack.translate(0, -1.375, 0);

        int meta = cog.getMeta();
        ResourceLocation texture = meta == 0 ? HBMResourceManager.stirling_tex : HBMResourceManager.stirling_steel_tex;

        HBMResourceManager.stirling.renderPart(poseStack, buffer, "Cog", texture, packedLight, 0);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(EntityCog cog) {
        int meta = cog.getMeta();
        return meta == 0 ? HBMResourceManager.stirling_tex : HBMResourceManager.stirling_steel_tex;
    }
}