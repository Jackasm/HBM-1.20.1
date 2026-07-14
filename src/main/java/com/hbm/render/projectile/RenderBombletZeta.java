package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityBombletZeta;
import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderBombletZeta extends EntityRenderer<EntityBombletZeta> {

    public RenderBombletZeta(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityBombletZeta entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        // Поворот по рысканью
        float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        // Поворот по тангажу
        float pitch = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        // Масштабирование
        poseStack.scale(0.5F, 0.5F, 0.5F);

        // Выбор текстуры
        ResourceLocation texture = getTextureLocation(entity);

        // Рендерим модель
        HBMResourceManager.bomblet.renderAll(poseStack, buffer, texture, packedLight, 0);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityBombletZeta entity) {
        if (entity.type == 4) {
            return ResLocation(RefStrings.MODID, "textures/models/projectiles/bomblet_theta.png");
        }
        return ResLocation(RefStrings.MODID, "textures/models/projectiles/bomblet_zeta.png");
    }
}