package com.hbm.render.loader;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.ArrayList;
import java.util.List;

public class GroupObject {
    public final String name;
    public final List<Face> faces = new ArrayList<>();
    public int glDrawingMode = -1; // -1 означает "еще не определен"

    public GroupObject(String name) {
        this.name = name;
    }

    public GroupObject(String name, int glDrawingMode) {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    public void render(VertexConsumer builder, PoseStack.Pose pose,
                       int packedLight, int packedOverlay) {
        for (Face face : faces) {
            face.render(builder, pose, 0.0005f, packedLight, packedOverlay);
        }
    }

    public void renderColored(VertexConsumer builder, PoseStack.Pose pose,
                              int packedLight, int packedOverlay,
                              float r, float g, float b, float a) {
        for (Face face : faces) {
            face.renderColored(builder, pose, packedLight, packedOverlay, r, g, b, a);
        }
    }
}