package com.hbm.render.block;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.Face;
import com.hbm.render.loader.GroupObject;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.loader.TextureCoordinate;
import com.hbm.render.loader.Vertex;
import com.hbm.tileentity.machine.TileEntitySolarMirror;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class RenderMirror implements BlockEntityRenderer<TileEntitySolarMirror> {

    public RenderMirror(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntitySolarMirror mirror, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Level level = mirror.getLevel();
        if (level == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(HBMResourceManager.solar_mirror_tex));

        HBMResourceManager.solar_mirror.renderPart(poseStack, consumer, "Base", packedLight, packedOverlay);

        int dx = mirror.tX - mirror.getBlockPos().getX();
        int dy = mirror.tY - mirror.getBlockPos().getY();
        int dz = mirror.tZ - mirror.getBlockPos().getZ();

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        boolean withinReach = dist <= 100;
        boolean withinAngle = (dx * dx + dz * dz) >= (dy * dy);

        if (withinReach && withinAngle) {
            HFRWavefrontObject model = HBMResourceManager.solar_mirror;
            GroupObject mirrorGroup = model.groupMap.get("Mirror");
            if (mirrorGroup != null) {
                renderMirrorWithRotation(poseStack, consumer, mirrorGroup, dx, dy, dz, packedLight, packedOverlay);
            }
        } else {
            HBMResourceManager.solar_mirror.renderPart(poseStack, consumer, "Mirror", packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    /**
     * Рендерит группу "Mirror" с поворотом вершин, как в оригинальном printMirror.
     */
    private void renderMirrorWithRotation(PoseStack poseStack, VertexConsumer consumer,
                                          GroupObject mirrorGroup, int dx, int dy, int dz,
                                          int packedLight, int packedOverlay) {

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.01) return;

        double pitch = -Math.asin((dy + 0.5) / dist) + Math.PI / 2D;
        double yaw = -Math.atan2(dz, dx) - Math.PI / 2D;

        double normX = dx / dist;
        double normY = dy / dist;
        double normZ = dz / dist;
        float SHIFT = 0.02f;

        float textureOffset = 0.0005f;

        for (Face face : mirrorGroup.faces) {
            Vector3f normal = face.faceNormal;
            if (normal == null) {
                normal = face.calculateFaceNormal();
                face.faceNormal = normal;
            }

            Vertex[] vertices = face.vertices;
            TextureCoordinate[] texCoords = face.textureCoordinates;

            int vertexCount = vertices.length;

            float avgU = 0, avgV = 0;
            for (int i = 0; i < vertexCount; i++) {
                if (texCoords != null && i < texCoords.length) {
                    avgU += texCoords[i].u;
                    avgV += texCoords[i].v;
                }
            }
            avgU /= vertexCount;
            avgV /= vertexCount;

            for (int i = 0; i < vertexCount; i++) {
                Vertex vertex = vertices[i];

                Vec3 vec = new Vec3(vertex.x, vertex.y - 1, vertex.z);
                vec = vec.xRot((float) pitch);
                vec = vec.yRot((float) yaw);

                float x = (float) (vec.x + normX * SHIFT);
                float y = (float) (vec.y + 1 + normY * SHIFT);
                float z = (float) (vec.z + normZ * SHIFT);

                float u = 0, v = 0;
                if (texCoords != null && i < texCoords.length) {
                    u = texCoords[i].u;
                    v = texCoords[i].v;
                }

                float offsetU = u > avgU ? -textureOffset : textureOffset;
                float offsetV = v > avgV ? -textureOffset : textureOffset;

                consumer.vertex(poseStack.last().pose(), x, y, z)
                        .color(1.0F, 1.0F, 1.0F, 1.0F)
                        .uv(u + offsetU, v + offsetV)
                        .overlayCoords(packedOverlay)
                        .uv2(packedLight)
                        .normal(normal.x(), normal.y(), normal.z())
                        .endVertex();

                if (vertexCount == 3 && i == 2) {
                    Vertex firstVertex = vertices[0];
                    Vec3 firstVec = new Vec3(firstVertex.x, firstVertex.y - 1, firstVertex.z);
                    firstVec = firstVec.xRot((float) pitch);
                    firstVec = firstVec.yRot((float) yaw);

                    float x2 = (float) (firstVec.x + normX * SHIFT);
                    float y2 = (float) (firstVec.y + 1 + normY * SHIFT);
                    float z2 = (float) (firstVec.z + normZ * SHIFT);

                    float u2 = 0, v2 = 0;
                    if (texCoords != null && texCoords.length > 0) {
                        u2 = texCoords[0].u;
                        v2 = texCoords[0].v;
                    }

                    float offsetU2 = u2 > avgU ? -textureOffset : textureOffset;
                    float offsetV2 = v2 > avgV ? -textureOffset : textureOffset;

                    consumer.vertex(poseStack.last().pose(), x2, y2, z2)
                            .color(1.0F, 1.0F, 1.0F, 1.0F)
                            .uv(u2 + offsetU2, v2 + offsetV2)
                            .overlayCoords(packedOverlay)
                            .uv2(packedLight)
                            .normal(normal.x(), normal.y(), normal.z())
                            .endVertex();
                }
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntitySolarMirror mirror) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}