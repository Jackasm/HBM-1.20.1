package com.hbm.render.tileentity.door;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.DoorDecl;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderWaterDoor implements IRenderDoors {

    public static final RenderWaterDoor INSTANCE = new RenderWaterDoor();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation texture = DoorDecl.WATER_DOOR.getSkinFromIndex(door.getSkinIndex());
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        double maxRot = 120;
        double rot = 0;
        double bolt = 0;
        if (door.state == TileEntityDoorGeneric.STATE_OPEN) {
            rot = maxRot;
            bolt = 1D;
        }

        if (door.currentAnimation != null) {
            rot = IRenderDoors.getRelevantTransformation("DOOR", door.currentAnimation)[1] * maxRot;
            bolt = IRenderDoors.getRelevantTransformation("BOLT", door.currentAnimation)[2];
        }

        poseStack.pushPose();

        poseStack.translate(0.375, 0.0, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90F));

        HBMResourceManager.pheo_water_door.renderPart(poseStack, consumer, "Frame", packedLight, packedOverlay);

        poseStack.translate(-1.1875, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) -rot));
        poseStack.translate(1.1875, 0, 0);
        HBMResourceManager.pheo_water_door.renderPart(poseStack, consumer, "Door_Cube.003", packedLight, packedOverlay);

        // Bolts
        poseStack.pushPose();
        poseStack.translate(-0.4 * bolt, 0, 0);
        HBMResourceManager.pheo_water_door.renderPart(poseStack, consumer, "Bolts", packedLight, packedOverlay);
        poseStack.popPose();

        // Top
        poseStack.pushPose();
        poseStack.translate(0.40625F, 2.28125, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (bolt * 360)));
        poseStack.translate(-0.40625F, -2.28125, 0);
        HBMResourceManager.pheo_water_door.renderPart(poseStack, consumer, "Top", packedLight, packedOverlay);
        poseStack.popPose();

        // Bottom
        poseStack.pushPose();
        poseStack.translate(0.40625F, 0.71875, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (bolt * 360)));
        poseStack.translate(-0.40625F, -0.71875, 0);
        HBMResourceManager.pheo_water_door.renderPart(poseStack, consumer, "Bottom", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}