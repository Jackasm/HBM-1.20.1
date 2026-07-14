package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityMeteor;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderMeteor extends EntityRenderer<EntityMeteor> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/blocks/block_meteor_molten.png");

    public RenderMeteor(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(EntityMeteor entity, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 0);
        poseStack.scale(1.0F, 1.0F, 1.0F);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(180), 0, 0));
        poseStack.mulPose(new Quaternionf().rotationXYZ(
                (entity.tickCount % 360) * 10,
                (entity.tickCount % 360) * 10,
                (entity.tickCount % 360) * 10
        ));

        poseStack.scale(5.0F, 5.0F, 5.0F);
        renderBlock(poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    private void renderBlock(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        Tesselator tessellator = Tesselator.getInstance();
        var builder = tessellator.getBuilder();

        // Используем entityCutout для текстуры с прозрачностью
        var consumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutout(TEXTURE));

        float size = 0.5F;
        float u0 = 0, u1 = 1, v0 = 0, v1 = 1;

        // Front
        addQuad(consumer, poseStack, -size, -size, -size, size, -size, -size, size, size, -size, -size, size, -size, u0, u1, v0, v1, packedLight);
        // Back
        addQuad(consumer, poseStack, -size, -size, size, -size, -size, -size, -size, size, -size, -size, size, size, u0, u1, v0, v1, packedLight);
        // Top
        addQuad(consumer, poseStack, -size, -size, size, size, -size, size, size, -size, -size, -size, -size, -size, u0, u1, v0, v1, packedLight);
        // Bottom
        addQuad(consumer, poseStack, -size, size, size, size, size, size, size, size, -size, -size, size, -size, u0, u1, v0, v1, packedLight);
        // Left
        addQuad(consumer, poseStack, -size, -size, size, -size, -size, -size, -size, size, -size, -size, size, size, u0, u1, v0, v1, packedLight);
        // Right
        addQuad(consumer, poseStack, size, -size, -size, size, -size, size, size, size, size, size, size, -size, u0, u1, v0, v1, packedLight);

        poseStack.popPose();
    }

    private void addQuad(VertexConsumer consumer, PoseStack poseStack,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         float x3, float y3, float z3,
                         float x4, float y4, float z4,
                         float u0, float u1, float v0, float v1, int packedLight) {

        var matrix = poseStack.last().pose();

        consumer.vertex(matrix, x1, y1, z1).color(1, 1, 1, 1).uv(u0, v0).uv2(packedLight).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(1, 1, 1, 1).uv(u1, v0).uv2(packedLight).endVertex();
        consumer.vertex(matrix, x3, y3, z3).color(1, 1, 1, 1).uv(u1, v1).uv2(packedLight).endVertex();
        consumer.vertex(matrix, x4, y4, z4).color(1, 1, 1, 1).uv(u0, v1).uv2(packedLight).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityMeteor entity) {
        return TEXTURE;
    }
}