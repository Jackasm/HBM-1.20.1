package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityRagingVortex;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RenderBlackHole extends EntityRenderer<Entity> {

    protected static final ResourceLocation HOLE_TEX = ResLocation.ResLocation(RefStrings.MODID, "textures/entity/blackhole.png");
    protected static final ResourceLocation SWIRL_TEX = ResLocation.ResLocation(RefStrings.MODID, "textures/entity/bhole.png");
    protected static final ResourceLocation DISC_TEX = ResLocation.ResLocation(RefStrings.MODID, "textures/entity/bholedisc.png");

    public RenderBlackHole(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull Entity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 0);

        RenderSystem.disableCull();

        float size = entity.getEntityData().get(EntityVortex.SIZE);
        poseStack.scale(size, size, size);

        VertexConsumer sphereConsumer = buffer.getBuffer(RenderType.entityCutout(HOLE_TEX));
        HBMResourceManager.sphere.renderAll(poseStack, sphereConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        if (entity instanceof EntityVortex) {
            renderSwirl(entity, partialTicks, poseStack);
        } else if (entity instanceof EntityRagingVortex) {
            renderSwirl(entity, partialTicks, poseStack);
            renderJets(entity, poseStack);
        } else {
            renderDisc(entity, partialTicks, poseStack);
            renderJets(entity, poseStack);
        }

        RenderSystem.enableCull();

        poseStack.popPose();
    }

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

    private void renderSwirl(Entity entity, float partialTicks, PoseStack poseStack) {
        poseStack.pushPose();

        int id = entity.getId();
        float age = entity.tickCount + partialTicks;
        float glow = (entity instanceof EntityRagingVortex) ? 0.25F : 0.75F;

        poseStack.mulPose(new Quaternionf().rotateX((id % 90 - 45) * Mth.DEG_TO_RAD));
        poseStack.mulPose(new Quaternionf().rotateY((id % 360) * Mth.DEG_TO_RAD));
        poseStack.mulPose(new Quaternionf().rotateY((age % 360) * -5 * Mth.DEG_TO_RAD));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, SWIRL_TEX);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        Matrix4f matrix = poseStack.last().pose();
        double s = 3;
        int count = 16;

        for (int j = 0; j < 2; j++) {
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            Vec3 vec = new Vec3(1, 0, 0);
            float alpha = (j == 0) ? 1.0F : glow;

            for (int i = 0; i < count; i++) {

                float[] colorFull = getColorFull(entity);

                builder.vertex(matrix, (float) (vec.x * 0.9), 0, (float) (vec.z * 0.9))
                        .color(0, 0, 0, 1)
                        .uv(0.5F + (float) vec.x * 0.25F / (float)s * 0.9F, 0.5F + (float) vec.z * 0.25F / (float)s * 0.9F)
                        .endVertex();

                builder.vertex(matrix, (float) (vec.x * s), 0, (float) (vec.z * s))
                        .color(colorFull[0], colorFull[1], colorFull[2], alpha)
                        .uv(0.5F + (float) vec.x * 0.25F, 0.5F + (float) vec.z * 0.25F)
                        .endVertex();

                vec = vec.yRot((float) (Math.PI * 2 / count));

                builder.vertex(matrix, (float) (vec.x * s), 0, (float) (vec.z * s))
                        .color(colorFull[0], colorFull[1], colorFull[2], alpha)
                        .uv(0.5F + (float) vec.x * 0.25F, 0.5F + (float) vec.z * 0.25F)
                        .endVertex();

                builder.vertex(matrix, (float) (vec.x * 0.9), 0, (float) (vec.z * 0.9))
                        .color(0, 0, 0, 1)
                        .uv(0.5F + (float) vec.x * 0.25F / (float)s * 0.9F, 0.5F + (float) vec.z * 0.25F / (float)s * 0.9F)
                        .endVertex();
            }
            BufferUploader.drawWithShader(builder.end());

            if (j == 0) {
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            }
        }

        RenderSystem.defaultBlendFunc();
        for (int j = 0; j < 2; j++) {
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            Vec3 vec = new Vec3(1, 0, 0);
            float alpha = (j == 0) ? 1.0F : glow;

            for (int i = 0; i < count; i++) {
                float[] colorFull = getColorFull(entity);
                float[] colorNone = getColorNone(entity);

                builder.vertex(matrix, (float) (vec.x * s), 0, (float) (vec.z * s))
                        .color(colorFull[0], colorFull[1], colorFull[2], alpha)
                        .uv(0.5F + (float) vec.x * 0.25F, 0.5F + (float) vec.z * 0.25F)
                        .endVertex();

                builder.vertex(matrix, (float) (vec.x * s * 2), 0, (float) (vec.z * s * 2))
                        .color(colorNone[0], colorNone[1], colorNone[2], 0)
                        .uv(0.5F + (float) vec.x * 0.5F, 0.5F + (float) vec.z * 0.5F)
                        .endVertex();

                vec = vec.yRot((float) (Math.PI * 2 / count));

                builder.vertex(matrix, (float) (vec.x * s * 2), 0, (float) (vec.z * s * 2))
                        .color(colorNone[0], colorNone[1], colorNone[2], 0)
                        .uv(0.5F + (float) vec.x * 0.5F, 0.5F + (float) vec.z * 0.5F)
                        .endVertex();

                builder.vertex(matrix, (float) (vec.x * s), 0, (float) (vec.z * s))
                        .color(colorFull[0], colorFull[1], colorFull[2], alpha)
                        .uv(0.5F + (float) vec.x * 0.25F, 0.5F + (float) vec.z * 0.25F)
                        .endVertex();
            }
            BufferUploader.drawWithShader(builder.end());

            if (j == 0) {
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            }
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        poseStack.popPose();
    }


    public void renderJets(Entity entity, PoseStack poseStack) {
        poseStack.pushPose();

        int id = entity.getId();

        poseStack.mulPose(new Quaternionf().rotateX((id % 90 - 45) * Mth.DEG_TO_RAD));
        poseStack.mulPose(new Quaternionf().rotateY((id % 360) * Mth.DEG_TO_RAD));

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        for (int j = -1; j <= 1; j += 2) {
            builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            builder.vertex(matrix, 0, 0, 0)
                    .color(1, 1, 1, 0.35F)
                    .endVertex();

            for (int i = 0; i <= 12; i++) {
                float angle = (float) (Math.PI / 6 * -j * i);
                float x = 0.5F * Mth.cos(angle);
                float z = 0.5F * Mth.sin(angle);
                builder.vertex(matrix, x, 10 * j, z)
                        .color(1, 1, 1, 0)
                        .endVertex();
            }

            BufferUploader.drawWithShader(builder.end());
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        poseStack.popPose();
    }

    protected int steps() {
        return 15;
    }

    public float[] getColorFromIteration(int iteration) {
        if (iteration < 5) {
            float g = 0.125F + iteration * (1F / 10F);
            return new float[]{1.0F, g, 0.0F};
        } else if (iteration == 5) {
            return new float[]{1.0F, 1.0F, 0.0F};
        } else {
            int i = iteration - 6;
            float r = 1.0F - i * (1F / 9F);
            float g = 1.0F - i * (1F / 9F);
            float b = i * (1F / 5F);
            return new float[]{r, g, b};
        }
    }

    private float[] getColorFull(Entity entity) {
        if (entity instanceof EntityVortex) {
            return new float[]{0x38 / 255F, 0x98 / 255F, 0xB3 / 255F};
        } else if (entity instanceof EntityRagingVortex) {
            return new float[]{0xE8 / 255F, 0x39 / 255F, 0x0D / 255F};
        } else {
            return new float[]{0xFF / 255F, 0xB9 / 255F, 0x00 / 255F};
        }
    }

    private float[] getColorNone(Entity entity) {
        return getColorFull(entity);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        return ResLocation.ResLocation("minecraft", "textures/block/dirt.png");
    }

    @Override
    public boolean shouldRender(@NotNull Entity entity, @NotNull Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }
}