package com.hbm.render.loader;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Face {
    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public TextureCoordinate[] textureCoordinates;
    public Vector3f faceNormal;
    private final boolean smoothing;

    private transient Vector4f[] cachedPositions;
    private transient Vector3f[] cachedNormals;
    private transient float[][] cachedUVs;
    private transient PoseStack.Pose lastPose;

    public Face(boolean smoothing) {
        this.smoothing = smoothing;
    }

    // Вычисление нормали грани
    public Vector3f calculateFaceNormal() {
        if (vertices == null || vertices.length < 3) {
            return new Vector3f(0, 0, 0);
        }

        Vector3f v1 = new Vector3f(
                vertices[1].x - vertices[0].x,
                vertices[1].y - vertices[0].y,
                vertices[1].z - vertices[0].z
        );

        Vector3f v2 = new Vector3f(
                vertices[2].x - vertices[0].x,
                vertices[2].y - vertices[0].y,
                vertices[2].z - vertices[0].z
        );

        Vector3f normal = v1.cross(v2);
        normal.normalize();
        return normal;
    }

    public void render(VertexConsumer builder, PoseStack.Pose pose,
                       float textureOffset, int packedLight, int packedOverlay) {

        // Проверяем, нужно ли пересчитать кеш
        if (cachedPositions == null || lastPose != pose) {
            calculateCache(pose);
            lastPose = pose;
        }

        if (textureOffset > 0 && textureCoordinates != null && textureCoordinates.length > 0) {
            renderWithTextureOffsetCached(builder, textureOffset, packedLight, packedOverlay);
        } else {
            renderWithoutTextureOffsetCached(builder, packedLight, packedOverlay);
        }
    }

    private void calculateCache(PoseStack.Pose pose) {
        if (faceNormal == null) {
            faceNormal = calculateFaceNormal();
        }

        int vertexCount = vertices.length;
        cachedPositions = new Vector4f[vertexCount];
        cachedNormals = new Vector3f[vertexCount];
        cachedUVs = new float[vertexCount][2];

        for (int i = 0; i < vertexCount; i++) {
            // Преобразуем позицию
            Vector4f pos = new Vector4f(vertices[i].x, vertices[i].y, vertices[i].z, 1.0f);
            pos.mul(pose.pose());
            cachedPositions[i] = pos;

            // Преобразуем нормаль
            Vector3f normal;
            if (smoothing && vertexNormals != null && i < vertexNormals.length) {
                normal = vertexNormals[i].toVector();
            } else {
                normal = faceNormal;
            }
            Vector3f transformedNormal = new Vector3f(normal);
            transformedNormal.mul(pose.normal());
            transformedNormal.normalize();
            cachedNormals[i] = transformedNormal;

            // UV координаты
            if (textureCoordinates != null && i < textureCoordinates.length) {
                cachedUVs[i][0] = textureCoordinates[i].u;
                cachedUVs[i][1] = textureCoordinates[i].v;
            }
        }
    }

    private void renderWithTextureOffsetCached(VertexConsumer builder, float textureOffset,
                                               int packedLight, int packedOverlay) {
        // Вычисляем средние координаты текстуры
        float averageU = 0;
        float averageV = 0;
        for (float[] uv : cachedUVs) {
            averageU += uv[0];
            averageV += uv[1];
        }
        averageU /= cachedUVs.length;
        averageV /= cachedUVs.length;

        for (int i = 0; i < vertices.length; i++) {
            float offsetU = cachedUVs[i][0] > averageU ? -textureOffset : textureOffset;
            float offsetV = cachedUVs[i][1] > averageV ? -textureOffset : textureOffset;

            renderVertexCached(builder, i, offsetU, offsetV, packedLight, packedOverlay);

            if (vertices.length == 3 && i == 2) {
                float firstOffsetU = cachedUVs[0][0] > averageU ? -textureOffset : textureOffset;
                float firstOffsetV = cachedUVs[0][1] > averageV ? -textureOffset : textureOffset;

                renderVertexCached(builder, 0, firstOffsetU, firstOffsetV, packedLight, packedOverlay);
            }
        }
    }

    private void renderWithoutTextureOffsetCached(VertexConsumer builder,
                                                  int packedLight, int packedOverlay) {
        for (int i = 0; i < vertices.length; i++) {
            renderVertexCached(builder, i, 0, 0, packedLight, packedOverlay);

            if (vertices.length == 3 && i == 2) {
                renderVertexCached(builder, 0, 0, 0, packedLight, packedOverlay);
            }
        }
    }

    private void renderColoredCached(VertexConsumer builder,
                                                  int packedLight, int packedOverlay, float r, float g, float b, float a) {
        for (int i = 0; i < vertices.length; i++) {
            renderColoredVertexCached(builder, i, packedLight, packedOverlay, r, g, b, a);

            if (vertices.length == 3 && i == 2) {
                renderColoredVertexCached(builder, 0, packedLight, packedOverlay, r, g, b, a);
            }
        }
    }

    private void renderVertexCached(VertexConsumer builder, int index,
                                    float offsetU, float offsetV,
                                    int packedLight, int packedOverlay) {
        Vector4f pos = cachedPositions[index];
        Vector3f normal = cachedNormals[index];
        float[] uv = cachedUVs[index];

        builder.vertex(pos.x(), pos.y(), pos.z())
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(uv[0] + offsetU, uv[1] + offsetV)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();
    }

    private void renderColoredVertexCached(VertexConsumer builder, int index,
                                    int packedLight, int packedOverlay, float r, float g, float b, float a) {
        Vector4f pos = cachedPositions[index];
        Vector3f normal = cachedNormals[index];
        float[] uv = cachedUVs[index];

        builder.vertex(pos.x(), pos.y(), pos.z())
                .color(r, g, b, a)
                .uv(uv[0], uv[1])
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();
    }

    public void renderColored(VertexConsumer builder, PoseStack.Pose pose,
                              int packedLight, int packedOverlay,
                              float r, float g, float b, float a) {

        // Используем существующий кэш
        if (cachedPositions == null || lastPose != pose) {
            calculateCache(pose);
            lastPose = pose;
        }

        renderColoredCached(builder, packedLight, packedOverlay, r, g, b, a);

    }
}