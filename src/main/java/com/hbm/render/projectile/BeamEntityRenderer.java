package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.hbm.render.util.BeamPronter.WHITE_TEXTURE;

@OnlyIn(Dist.CLIENT)
public class BeamEntityRenderer extends EntityRenderer<EntityBulletBeamBase> {

    public BeamEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityBulletBeamBase beam, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        if (beam.getConfig() == null) return;

        float beamLength = beam.getBeamLength();
        if (beamLength <= 0) return;

        if (beam.getConfig().renderRotations) {
            float yaw = Mth.lerp(partialTicks, beam.yRotO, beam.getYRot()) - 90.0F;
            float pitch = Mth.lerp(partialTicks, beam.xRotO, beam.getXRot()) + 180.0F;

            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            poseStack.mulPose(Axis.ZP.rotationDegrees(pitch)); // ZP! в оригинале ось Z
        }

        // Получаем оригинальную матрицу из рендерера
        Matrix4f originalMatrix = poseStack.last().pose();

        // Извлекаем только позицию (трансляцию) из матрицы
        float posX = originalMatrix.m30();
        float posY = originalMatrix.m31();
        float posZ = originalMatrix.m32();

        // Создаем новую матрицу ТОЛЬКО с позицией (без вращения)
        PoseStack positionOnlyPose = new PoseStack();
        positionOnlyPose.translate(posX, posY, posZ);

        // Передаем пуле матрицу только с позицией
        beam.setRenderPose(positionOnlyPose.last().pose());

        // Сохраняем bufferSource для использования в LegoClient
        beam.setBufferSource((MultiBufferSource.BufferSource) buffer);

        beam.getConfig().rendererBeam.accept(beam, partialTicks);

        if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
            bufferSource.endBatch(RenderType.entityTranslucentEmissive(WHITE_TEXTURE));
        }

        super.render(beam, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityBulletBeamBase entity) {
        return null;
    }
}