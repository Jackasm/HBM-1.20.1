package com.hbm.render.projectile;

import java.util.Random;

import com.hbm.entity.projectile.EntityBullet;
import com.hbm.render.model.ModelBullet;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderRocket extends EntityRenderer<EntityBullet> {

    private final ModelBullet model;

    public RenderRocket(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelBullet();
    }

    @Override
    public void render(@NotNull EntityBullet entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight) {

        poseStack.pushPose();

        // Позиционирование
        poseStack.translate(0, 0, 0);

        // Вращение по рысканию (Yaw)
        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        poseStack.mulPose(new Quaternionf().rotateY(-yaw * Mth.DEG_TO_RAD));

        // Вращение по тангажу (Pitch)
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.mulPose(new Quaternionf().rotateX((pitch + 90) * Mth.DEG_TO_RAD));

        // Масштаб
        poseStack.scale(1.5F, 1.5F, 1.5F);

        // Случайное вращение вокруг оси X (как в оригинале)
        Random rand = new Random(entity.getId());
        poseStack.mulPose(new Quaternionf().rotateX(rand.nextInt(360) * Mth.DEG_TO_RAD));

        // Выбор текстуры
        ResourceLocation texture;
        if (entity.isChopper()) {
            texture = ResLocation(RefStrings.MODID, "textures/entity/emplacer.png");
        } else if (entity.isCritical()) {
            texture = ResLocation(RefStrings.MODID, "textures/entity/tau.png");
        } else {
            texture = ResLocation(RefStrings.MODID, "textures/entity/bullet.png");
        }

        // Получаем VertexConsumer для рендера
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(texture));

        // Рендерим модель
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityBullet entity) {
        if (entity.isChopper()) {
            return ResLocation(RefStrings.MODID, "textures/entity/emplacer.png");
        } else if (entity.isCritical()) {
            return ResLocation(RefStrings.MODID, "textures/entity/tau.png");
        } else {
            return ResLocation(RefStrings.MODID, "textures/entity/bullet.png");
        }
    }
}