package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityCoin;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderCoin extends EntityRenderer<EntityCoin> {

    private final HFRWavefrontObject coinModel;
    private final ResourceLocation coinTexture;

    public RenderCoin(EntityRendererProvider.Context context) {
        super(context);
        this.coinModel = HBMResourceManager.chip;
        this.coinTexture = HBMResourceManager.chip_gold_tex;
    }

    @Override
    public void render(@NotNull EntityCoin entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        if (!entity.isAlive()) return;

        poseStack.pushPose();

        // Позиция
        poseStack.translate(0, 0, 0);

        // Вращение по рысканью (yaw)
        float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks - 90.0F;
        poseStack.mulPose(Axis.YN.rotationDegrees(yaw));

        // Вращение вокруг оси Z (как в оригинале)
        float rotationZ = (entity.tickCount + partialTicks) * 45;
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));

        // Масштаб
        float scale = 0.125F;
        poseStack.scale(scale, scale, scale);

        // Рендер модели
        if (coinModel != null && coinTexture != null) {
            // Настройки рендеринга
            RenderType renderType = RenderType.entityCutout(coinTexture);
            var consumer = buffer.getBuffer(renderType);

            // Рендерим всю модель
            coinModel.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityCoin entity) {
        return coinTexture;
    }
}