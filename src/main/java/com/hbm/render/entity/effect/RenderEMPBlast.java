package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityEMPBlast;
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
public class RenderEMPBlast extends EntityRenderer<EntityEMPBlast> {

    public RenderEMPBlast(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityEMPBlast entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(0));

        // Масштабирование кольца
        float scale = entity.scale;
        poseStack.scale(scale, 1.0F, scale);

        // Рендер модели кольца
        HBMResourceManager.ring.renderAll(poseStack, buffer,
                HBMResourceManager.emp_blast_tex, packedLight, 0);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityEMPBlast entity) {
        return HBMResourceManager.emp_blast_tex;
    }
}