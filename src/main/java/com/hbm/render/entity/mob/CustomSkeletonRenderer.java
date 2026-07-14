package com.hbm.render.entity.mob;

import com.hbm.entity.mob.CustomSkeleton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class CustomSkeletonRenderer extends HumanoidMobRenderer<CustomSkeleton, SkeletonModel<CustomSkeleton>> {

    private static final ResourceLocation TEXTURE = ResLocation("textures/entity/skeleton.png");

    public CustomSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public void render(CustomSkeleton entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        // Принудительно устанавливаем повороты рук после рендеринга (но это уже поздно)
        // Лучше использовать событие или модифицировать модель.
    }

    @Override
    protected void setupRotations(@NotNull CustomSkeleton entity, @NotNull PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);
        // Анимация руки при стрельбе
        float attackProgress = entity.attackTicks / 10.0F; // от 0 до 1
        if (attackProgress > 0) {
            float armPitch = attackProgress * 90.0F; // поднимаем руку до 90 градусов
            this.model.rightArm.xRot = -Mth.DEG_TO_RAD * armPitch;
            // Можно добавить небольшой подъём левой руки
            this.model.leftArm.xRot = -Mth.DEG_TO_RAD * armPitch * 0.3F;
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CustomSkeleton entity) {
        return TEXTURE;
    }
}