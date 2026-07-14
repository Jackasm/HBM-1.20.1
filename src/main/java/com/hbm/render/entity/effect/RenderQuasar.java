package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityVortex;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderQuasar extends RenderBlackHole {

    protected static final ResourceLocation QUASAR = ResLocation(RefStrings.MODID, "textures/entity/bhole_d.png");
    private static final ResourceLocation HOLE = ResLocation(RefStrings.MODID, "textures/entity/bhole.png");

    public RenderQuasar(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        float size = entity.getEntityData().get(EntityVortex.SIZE);
        poseStack.scale(size, size, size);

        // Рендерим черную дыру
        HFRWavefrontObject blastModel = HBMResourceManager.sphere;
        VertexConsumer holeConsumer = buffer.getBuffer(RenderType.entityCutout(QUASAR));
        blastModel.renderAll(poseStack, holeConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        // Рендерим диск и джеты
        renderDisc(entity, partialTicks, poseStack);
        renderJets(entity, poseStack);

        poseStack.popPose();
    }


    @Override
    protected int steps() {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return super.getTextureLocation(entity);
    }

    // Переопределяем методы renderDisc и renderJets чтобы использовать QUASAR текстуру
    @Override
    public void renderDisc(Entity entity, float partialTicks, PoseStack poseStack) {
        poseStack.pushPose();

        int id = entity.getId();
        float age = entity.tickCount + partialTicks;

        poseStack.mulPose(new Quaternionf().rotateX((id % 90 - 45) * Mth.DEG_TO_RAD));
        poseStack.mulPose(new Quaternionf().rotateY((id % 360) * Mth.DEG_TO_RAD));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, DISC_TEX);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        Matrix4f matrix;
        int steps = steps();
        int count = 16;
        float glow = 0.75F;

        for (int k = 0; k < steps; k++) {
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateY((age % 360) * -Mth.DEG_TO_RAD * (float) Math.pow(k + 1, 1.25)));

            double s = 3 - k * 0.175D;
            matrix = poseStack.last().pose();

            for (int j = 0; j < 2; j++) {
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
                Vec3 vec = new Vec3(1, 0, 0);
                for (int i = 0; i < count; i++) {
                    float alpha = (j == 0) ? 1.0F : glow;

                    float[] color1 = getColorFromIteration(k);
                    float[] color2 = getColorFromIteration(k);

                    builder.vertex(matrix, (float) (vec.x * s), 0, (float) (vec.z * s))
                            .color(color1[0], color1[1], color1[2], alpha)
                            .uv(0.5F + (float) vec.x * 0.25F, 0.5F + (float) vec.z * 0.25F)
                            .endVertex();

                    builder.vertex(matrix, (float) (vec.x * s * 2), 0, (float) (vec.z * s * 2))
                            .color(color2[0], color2[1], color2[2], 0)
                            .uv(0.5F + (float) vec.x * 0.5F, 0.5F + (float) vec.z * 0.5F)
                            .endVertex();

                    vec = vec.yRot((float) (Math.PI * 2 / count));

                    builder.vertex(matrix, (float) (vec.x * s * 2), 0, (float) (vec.z * s * 2))
                            .color(color2[0], color2[1], color2[2], 0)
                            .uv(0.5F + (float) vec.x * 0.5F, 0.5F + (float) vec.z * 0.5F)
                            .endVertex();

                    builder.vertex(matrix, (float) (vec.x * s), 0, (float) (vec.z * s))
                            .color(color1[0], color1[1], color1[2], alpha)
                            .uv(0.5F + (float) vec.x * 0.25F, 0.5F + (float) vec.z * 0.25F)
                            .endVertex();
                }
                BufferUploader.drawWithShader(builder.end());

                if (j == 0) {
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                }
            }
            poseStack.popPose();
            RenderSystem.defaultBlendFunc();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

}