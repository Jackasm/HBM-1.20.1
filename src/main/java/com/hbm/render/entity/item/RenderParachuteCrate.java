package com.hbm.render.entity.item;

import com.hbm.entity.item.EntityParachuteCrate;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderParachuteCrate extends EntityRenderer<EntityParachuteCrate> {

    public RenderParachuteCrate(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityParachuteCrate entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // Позиция
        poseStack.translate(0, 0, 0);

        // Время для анимации покачивания
        double time = entity.level().getGameTime() + partialTicks;
        double sine = Math.sin(time * 0.05) * 5;
        double sin3 = Math.sin(time * 0.05 + Math.PI * 0.5) * 5;

        int height = 7;

        // Анимация покачивания
        poseStack.translate(0.0F, height, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) sine));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) sin3));
        poseStack.translate(0.0F, -height, 0.0F);

        // Рендерим ящик
        var crateBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.supply_crate_tex));
        HBMResourceManager.supply_crate.renderAll(poseStack, crateBuilder, packedLight, OverlayTexture.NO_OVERLAY);

        // Смещение для парашюта
        poseStack.translate(0, -1, 0);

        // Рендерим парашют
        var chuteBuilder = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.soyuz_chute_tex));
        HBMResourceManager.soyuz_lander.renderPart(poseStack, chuteBuilder, "Chute", packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityParachuteCrate entity) {
        return HBMResourceManager.soyuz_lander_tex;
    }
}