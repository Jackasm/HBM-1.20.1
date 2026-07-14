package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.projectile.EntityChemical.ChemicalStyle;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderChemical extends EntityRenderer<EntityChemical> {

    private static final ResourceLocation GAS = ResLocation(RefStrings.MODID, "textures/particle/particle_base.png");

    public RenderChemical(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityChemical entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        ChemicalStyle style = entity.getStyle();

        if (style == ChemicalStyle.AMAT || style == ChemicalStyle.LIGHTNING) {
            renderAmatBeam(entity, partialTicks, poseStack, buffer);
        }

        if (style == ChemicalStyle.GAS) {
            renderGasCloud(entity, partialTicks, poseStack, buffer);
        }

        if (style == ChemicalStyle.GASFLAME) {
            renderGasFire(entity, partialTicks, poseStack, buffer);
        }
    }

    private void renderGasFire(EntityChemical entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        float exp = (entity.tickCount + partialTicks) / (float) entity.getMaxAge();
        double size = 0.0 + exp * 2;
        Color color = Color.getHSBColor(Math.max((60 - exp * 100) / 360F, 0.0F), 1 - exp * 0.25F, 1 - exp * 0.5F);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        poseStack.pushPose();
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                (float) Math.toRadians(180.0F - this.entityRenderDispatcher.camera.getYRot()),
                (float) Math.toRadians(this.entityRenderDispatcher.camera.getXRot())
        ));

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder builder = tess.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        float alpha = (float) Math.max(255 * (1 - exp), 0) / 255F;
        float r = color.getRed() / 255F;
        float g = color.getGreen() / 255F;
        float b = color.getBlue() / 255F;

        builder.vertex(matrix, (float) -size, (float) -size, 0).color(r, g, b, alpha).uv(1, 1).endVertex();
        builder.vertex(matrix, (float) size, (float) -size, 0).color(r, g, b, alpha).uv(0, 1).endVertex();
        builder.vertex(matrix, (float) size, (float) size, 0).color(r, g, b, alpha).uv(0, 0).endVertex();
        builder.vertex(matrix, (float) -size, (float) size, 0).color(r, g, b, alpha).uv(1, 0).endVertex();

        BufferUploader.drawWithShader(builder.end());

        poseStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void renderGasCloud(EntityChemical entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        double exp = (entity.tickCount + partialTicks) / (double) entity.getMaxAge();
        double size = 0.0 + exp * 10;
        int colorInt = entity.getFluidType().getColor();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        poseStack.pushPose();
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                (float) Math.toRadians(180.0F - this.entityRenderDispatcher.camera.getYRot()),
                (float) Math.toRadians(this.entityRenderDispatcher.camera.getXRot())
        ));

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder builder = tess.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        Color color = new Color(colorInt);
        float r = color.getRed() / 255F;
        float g = color.getGreen() / 255F;
        float b = color.getBlue() / 255F;
        float alpha = (float) Math.max(127 * (1 - exp), 0) / 255F;

        Random rand = new Random(entity.getId());
        int i = rand.nextInt(2);
        int j = rand.nextInt(2);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        builder.vertex(matrix, (float) -size, (float) -size, 0).color(r, g, b, alpha).uv(1 - i, 1 - j).endVertex();
        builder.vertex(matrix, (float) size, (float) -size, 0).color(r, g, b, alpha).uv(i, 1 - j).endVertex();
        builder.vertex(matrix, (float) size, (float) size, 0).color(r, g, b, alpha).uv(i, j).endVertex();
        builder.vertex(matrix, (float) -size, (float) size, 0).color(r, g, b, alpha).uv(1 - i, j).endVertex();
        BufferUploader.drawWithShader(builder.end());

        poseStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void renderAmatBeam(EntityChemical entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks;
        float pitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;

        poseStack.pushPose();
        poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ(
                0,
                (float) Math.toRadians(yaw),
                (float) Math.toRadians(-pitch - 90)
        ));

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder builder = tess.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        Vec3 motion = entity.getDeltaMovement();
        double length = motion.length() * (entity.tickCount + partialTicks) * 0.75;
        double size = 0.0625;
        float alpha = 0.2F;

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Side 1
        builder.vertex(matrix, (float) -size, 0, (float) -size).color(1, 1, 1, alpha).endVertex();
        builder.vertex(matrix, (float) size, 0, (float) -size).color(1, 1, 1, alpha).endVertex();
        builder.vertex(matrix, (float) size, (float) length, (float) -size).color(1, 1, 1, 0).endVertex();
        builder.vertex(matrix, (float) -size, (float) length, (float) -size).color(1, 1, 1, 0).endVertex();

        // Side 2
        builder.vertex(matrix, (float) -size, 0, (float) size).color(1, 1, 1, alpha).endVertex();
        builder.vertex(matrix, (float) size, 0, (float) size).color(1, 1, 1, alpha).endVertex();
        builder.vertex(matrix, (float) size, (float) length, (float) size).color(1, 1, 1, 0).endVertex();
        builder.vertex(matrix, (float) -size, (float) length, (float) size).color(1, 1, 1, 0).endVertex();

        // Side 3
        builder.vertex(matrix, (float) -size, 0, (float) -size).color(1, 1, 1, alpha).endVertex();
        builder.vertex(matrix, (float) -size, 0, (float) size).color(1, 1, 1, alpha).endVertex();
        builder.vertex(matrix, (float) -size, (float) length, (float) size).color(1, 1, 1, 0).endVertex();
        builder.vertex(matrix, (float) -size, (float) length, (float) -size).color(1, 1, 1, 0).endVertex();

        // Side 4
        builder.vertex(matrix, (float) size, 0, (float) -size).color(1, 1, 1, alpha).endVertex();
        builder.vertex(matrix, (float) size, 0, (float) size).color(1, 1, 1, alpha).endVertex();
        builder.vertex(matrix, (float) size, (float) length, (float) size).color(1, 1, 1, 0).endVertex();
        builder.vertex(matrix, (float) size, (float) length, (float) -size).color(1, 1, 1, 0).endVertex();

        BufferUploader.drawWithShader(builder.end());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityChemical entity) {
        return GAS;
    }
}