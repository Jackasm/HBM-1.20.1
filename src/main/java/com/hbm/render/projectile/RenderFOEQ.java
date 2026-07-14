package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityBurningFOEQ;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RenderFOEQ extends EntityRenderer<EntityBurningFOEQ> {


    private final Random random = new Random();

    public RenderFOEQ(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityBurningFOEQ entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, -75, 0);

        // Поворот по рысканью
        float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        // Поворот по тангажу
        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        float pitch = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));


        // Рендерим модель спутника
        HBMResourceManager.sat_foeq_burning.renderAll(poseStack, buffer,
                HBMResourceManager.sat_foeq_burning_tex, packedLight, 0);

        // Рендерим огонь
        long time = System.currentTimeMillis() / 50;
        random.setSeed(time);

        poseStack.scale(1.15F, 0.75F, 1.15F);
        poseStack.translate(0, -0.5, 0.3);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(HBMResourceManager.sat_foeq_burning_tex));

        // Создаём несколько слоёв огня
        for (int i = 0; i < 10; i++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextInt(360)));

            // Слой 1
            HBMResourceManager.sat_foeq_fire.renderPart(poseStack, consumer, "Fire", 0xF000F0, 0);

            poseStack.translate(0, 2, 0);

            // Слой 2
            HBMResourceManager.sat_foeq_fire.renderPart(poseStack, consumer, "Fire", 0xF000F0, 0);

            poseStack.translate(0, 2, 0);

            // Слой 3
            HBMResourceManager.sat_foeq_fire.renderPart(poseStack, consumer, "Fire", 0xF000F0, 0);

            poseStack.translate(0, 2, 0);

            // Слой 4
            HBMResourceManager.sat_foeq_fire.renderPart(poseStack, consumer, "Fire", 0xF000F0, 0);

            poseStack.popPose();
            poseStack.translate(0, -3.8, 0);
            poseStack.scale(0.95F, 1.2F, 0.95F);
        }

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityBurningFOEQ entity) {
        return HBMResourceManager.sat_foeq_tex;
    }
}