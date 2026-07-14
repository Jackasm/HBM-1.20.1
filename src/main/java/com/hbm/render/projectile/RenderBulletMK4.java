package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderBulletMK4 extends EntityRenderer<EntityBulletBaseMK4> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/models/thegadget3.png");

    public RenderBulletMK4(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull EntityBulletBaseMK4 bullet, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        if(bullet.isRemoved()) return;
        if (bullet.getConfig().renderer == null) return;

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
        bullet.setRenderPose(positionOnlyPose.last().pose());

        bullet.setBufferSource((MultiBufferSource.BufferSource) buffer);

        bullet.setPackedLight(packedLight);

        bullet.getConfig().renderer.accept(bullet, partialTicks);

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityBulletBaseMK4 entity) {
        return TEXTURE;
    }
}