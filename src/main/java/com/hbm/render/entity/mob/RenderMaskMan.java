package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityMaskMan;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.model.ModelMaskMan;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderMaskMan extends LivingEntityRenderer<EntityMaskMan, ModelMaskMan> {

    private static final ResourceLocation TEXTURE = HBMResourceManager.maskman_tex;

    public RenderMaskMan(EntityRendererProvider.Context context) {
        super(context, new ModelMaskMan(), 1.0F);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull EntityMaskMan entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        // Получаем правильный overlay для эффекта урона
        int packedOverlay = getOverlayCoords(entity, 0.0F);

        this.model.render(
                entity,
                entity.walkAnimation.position(),
                entity.walkAnimation.speed(),
                entity.tickCount + partialTicks,
                entity.getYHeadRot(),
                entity.getXRot(),
                1.0F,
                poseStack,
                buffer,
                packedLight,
                packedOverlay  // Передаем правильный overlay
        );

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityMaskMan entity) {
        return TEXTURE;
    }
}