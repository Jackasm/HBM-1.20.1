package com.hbm.render.util;

import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaternionf;

import java.util.Arrays;

import static com.hbm.util.ResLocation.ResLocation;

public class HorsePronter {

    public static final ResourceLocation tex_demohorse = ResLocation(
            RefStrings.MODID, "textures/block/numbernine.png"
    );

    private static final Vec3[] pose = new Vec3[8];
    private static final Vec3[] offsets = new Vec3[8];

    static {
        Arrays.fill(pose, Vec3.ZERO);
        offsets[0] = new Vec3(0, 1.125, 0.375);      // head
        offsets[1] = new Vec3(0.125, 0.75, 0.3125);   // left front leg
        offsets[2] = new Vec3(-0.125, 0.75, 0.3125);  // right front leg
        offsets[3] = new Vec3(0.125, 0.75, -0.25);    // left back leg
        offsets[4] = new Vec3(-0.125, 0.75, -0.25);   // right back leg
        offsets[5] = new Vec3(0, 1.125, -0.4375);     // tail
        offsets[6] = Vec3.ZERO;                       // body
        offsets[7] = Vec3.ZERO;                       // body offset
    }

    public static final int id_head = 0;
    public static final int id_lfl = 1;
    public static final int id_rfl = 2;
    public static final int id_lbl = 3;
    public static final int id_rbl = 4;
    public static final int id_tail = 5;
    public static final int id_body = 6;
    public static final int id_position = 7;

    private static boolean wings = false;
    private static boolean horn = false;
    private static boolean maleSnoot = false;

    public static void reset() {
        wings = false;
        horn = false;
        maleSnoot = false;
        Arrays.fill(pose, Vec3.ZERO);
    }

    public static void enableHorn() {
        horn = true;
    }

    public static void enableWings() {
        wings = true;
    }

    public static void setMaleSnoot() {
        maleSnoot = true;
    }

    public static void setAlicorn() {
        enableHorn();
        enableWings();
    }

    public static void poseStandardSit() {
        double r = 60;
        pose(id_body, 0, -r, 0);
        pose(id_tail, 0, 45, 90);
        pose(id_lbl, 0, -90 + r, 35);
        pose(id_rbl, 0, -90 + r, -35);
        pose(id_lfl, 0, r - 10, 5);
        pose(id_rfl, 0, r - 10, -5);
        pose(id_head, 0, r, 0);
    }

    public static void pose(int id, double yaw, double pitch, double roll) {
        pose[id] = new Vec3(yaw, pitch, roll);
    }

    public static void pront(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(tex_demohorse));

        poseStack.pushPose();
        // Включаем отсечение граней
        // В 1.20.1 это делается через RenderSystem

        doTransforms(poseStack, id_body);

        HBMResourceManager.horse.renderPart(poseStack, consumer, "Body", packedLight, packedOverlay);

        if (horn) {
            renderWithTransform(poseStack, consumer, id_head, packedLight, packedOverlay, "Head", "Mane", maleSnoot ? "NoseMale" : "NoseFemale", "HornPointy");
        } else {
            renderWithTransform(poseStack, consumer, id_head, packedLight, packedOverlay, "Head", "Mane", maleSnoot ? "NoseMale" : "NoseFemale");
        }

        renderWithTransform(poseStack, consumer, id_lfl, packedLight, packedOverlay, "LeftFrontLeg");
        renderWithTransform(poseStack, consumer, id_rfl, packedLight, packedOverlay, "RightFrontLeg");
        renderWithTransform(poseStack, consumer, id_lbl, packedLight, packedOverlay, "LeftBackLeg");
        renderWithTransform(poseStack, consumer, id_rbl, packedLight, packedOverlay, "RightBackLeg");
        renderWithTransform(poseStack, consumer, id_tail, packedLight, packedOverlay, "Tail");

        if (wings) {
            HBMResourceManager.horse.renderPart(poseStack, consumer, "LeftWing", packedLight, packedOverlay);
            HBMResourceManager.horse.renderPart(poseStack, consumer, "RightWing", packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private static void doTransforms(PoseStack poseStack, int id) {
        Vec3 rotation = pose[id];
        Vec3 offset = offsets[id];

        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(rotation.x)));
        poseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(rotation.y)));
        poseStack.mulPose(new Quaternionf().rotationZ((float) Math.toRadians(rotation.z)));
        poseStack.translate(-offset.x, -offset.y, -offset.z);
    }

    private static void renderWithTransform(PoseStack poseStack, VertexConsumer consumer, int id,
                                            int packedLight, int packedOverlay, String... parts) {
        poseStack.pushPose();
        doTransforms(poseStack, id);
        for (String part : parts) {
            HBMResourceManager.horse.renderPart(poseStack, consumer, part, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }

    public static void renderHorseWithTexture(PoseStack poseStack, MultiBufferSource buffer,
                                              ResourceLocation texture, int packedLight, int packedOverlay) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));
        pront(poseStack, buffer, packedLight, packedOverlay);
    }
}