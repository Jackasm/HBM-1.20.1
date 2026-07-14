package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityShrapnel;
import com.hbm.render.model.ModelShrapnel;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderShrapnel extends EntityRenderer<EntityShrapnel> {

    private final ModelShrapnel model;

    public RenderShrapnel(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelShrapnel();
    }

    @Override
    public void render(EntityShrapnel entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // Позиционирование
        poseStack.translate(0, 0, 0);

        // Масштаб
        poseStack.scale(1.0F, 1.0F, 1.0F);

        // Поворот на 180 градусов вокруг X (как в оригинале)
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.PI));

        // Вращение на основе времени
        float rotation = (entity.tickCount + partialTicks) * 10;
        poseStack.mulPose(new Quaternionf().rotateAxis(
                (float) Math.toRadians(rotation),
                new Vector3f(1, 1, 1).normalize()
        ));

        byte shrapnelType = entity.getShrapnelType();
        if (shrapnelType == EntityShrapnel.TYPE_VOLCANO ||
                shrapnelType == EntityShrapnel.TYPE_RAD_VOLCANO) {
            poseStack.scale(3.0F, 3.0F, 3.0F);
        }

        ResourceLocation texture = this.getTextureLocation(entity);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityShrapnel entity) {
        return ResLocation(RefStrings.MODID, "textures/entity/shrapnel.png");
    }
}