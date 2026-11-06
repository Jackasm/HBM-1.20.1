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

    public Face(boolean smoothing) {
        this.smoothing = smoothing;
    }

    public Vector3f calculateFaceNormal() {
        if (vertices == null || vertices.length < 3 ||
                vertices[0] == null || vertices[1] == null || vertices[2] == null) {
            return new Vector3f(0.0f, 1.0f, 0.0f);
        }

        Vector3f edge1 = new Vector3f(
                vertices[1].x - vertices[0].x,
                vertices[1].y - vertices[0].y,
                vertices[1].z - vertices[0].z
        );
        Vector3f edge2 = new Vector3f(
                vertices[2].x - vertices[0].x,
                vertices[2].y - vertices[0].y,
                vertices[2].z - vertices[0].z
        );

        Vector3f normal = edge1.cross(edge2);
        if (normal.lengthSquared() < 1.0e-12f) {
            return new Vector3f(0.0f, 1.0f, 0.0f);
        }
        return normal.normalize();
    }

    public void render(VertexConsumer builder, PoseStack.Pose pose,
                       float textureOffset, int packedLight, int packedOverlay) {
        renderInternal(builder, pose, textureOffset, packedLight, packedOverlay,
                1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderColored(VertexConsumer builder, PoseStack.Pose pose,
                              int packedLight, int packedOverlay,
                              float r, float g, float b, float a) {
        renderInternal(builder, pose, 0.0f, packedLight, packedOverlay, r, g, b, a);
    }

    private void renderInternal(VertexConsumer builder, PoseStack.Pose pose,
                                float textureOffset, int packedLight, int packedOverlay,
                                float r, float g, float b, float a) {
        if (vertices == null || vertices.length < 3) {
            return;
        }

        if (faceNormal == null) {
            faceNormal = calculateFaceNormal();
        }

        // Recompute only the UV center; it doesn't depend on the pose matrix.
        // This retains the original small UV inset to reduce texture bleeding.
        float averageU = 0.0f;
        float averageV = 0.0f;
        if (textureOffset > 0.0f && textureCoordinates != null) {
            for (int i = 0; i < vertices.length; i++) {
                TextureCoordinate uv = textureAt(i);
                if (uv != null) {
                    averageU += uv.u;
                    averageV += uv.v;
                }
            }
            averageU /= vertices.length;
            averageV /= vertices.length;
        }

        int count = vertices.length;
        if (count == 4) {
            for (int i = 0; i < 4; i++) {
                emitVertex(builder, pose, i, averageU, averageV,
                        textureOffset, packedLight, packedOverlay, r, g, b, a);
            }
        } else {
            // Render triangles using the QUADS format expected by entityCutout.
            // The repeated index 0 creates a degenerate second triangle.
            // Fan triangulation is correct for convex N-gons; for concave
            // polygons, triangulate the OBJ mesh in the asset pipeline.
            for (int i = 1; i < count - 1; i++) {
                emitVertex(builder, pose, 0, averageU, averageV,
                        textureOffset, packedLight, packedOverlay, r, g, b, a);
                emitVertex(builder, pose, i, averageU, averageV,
                        textureOffset, packedLight, packedOverlay, r, g, b, a);
                emitVertex(builder, pose, i + 1, averageU, averageV,
                        textureOffset, packedLight, packedOverlay, r, g, b, a);
                emitVertex(builder, pose, 0, averageU, averageV,
                        textureOffset, packedLight, packedOverlay, r, g, b, a);
            }
        }
    }

    private TextureCoordinate textureAt(int index) {
        if (textureCoordinates == null || index >= textureCoordinates.length) {
            return null;
        }
        return textureCoordinates[index];
    }

    private void emitVertex(VertexConsumer builder, PoseStack.Pose pose,
                            int index, float averageU, float averageV,
                            float textureOffset, int packedLight, int packedOverlay,
                            float r, float g, float b, float a) {
        Vertex vertex = vertices[index];
        if (vertex == null) {
            throw new IllegalStateException("OBJ face contains a null vertex at index " + index);
        }

        TextureCoordinate tc = textureAt(index);
        float u = tc != null ? tc.u : 0.0f;
        float v = tc != null ? tc.v : 0.0f;
        if (textureOffset > 0.0f && textureCoordinates != null) {
            u += u > averageU ? -textureOffset : textureOffset;
            v += v > averageV ? -textureOffset : textureOffset;
        }

        Vertex smoothNormal = null;
        if (smoothing && vertexNormals != null && index < vertexNormals.length) {
            smoothNormal = vertexNormals[index];
        }
        float nx = smoothNormal != null ? smoothNormal.x : faceNormal.x();
        float ny = smoothNormal != null ? smoothNormal.y : faceNormal.y();
        float nz = smoothNormal != null ? smoothNormal.z : faceNormal.z();

        // Transform against the CURRENT pose, without persisting any pose-
        // dependent data in the shared Face instance.
        builder.vertex(pose.pose(), vertex.x, vertex.y, vertex.z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }
}