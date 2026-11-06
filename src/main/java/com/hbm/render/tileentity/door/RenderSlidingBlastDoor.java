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
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderSlidingBlastDoor implements IRenderDoors {

    public static final RenderSlidingBlastDoor INSTANCE = new RenderSlidingBlastDoor();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation texture = DoorDecl.SLIDE_DOOR.getSkinFromIndex(door.getSkinIndex());
        // В 1.7.10 было GL11.glDisable(GL11.GL_CULL_FACE) → здесь noCull
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        double maxOpen = 2.125;
        double open = 0;
        double lock = 0;
        if (door.state == TileEntityDoorGeneric.STATE_OPEN) {
            open = maxOpen;
            lock = 90;
        }

        if (door.currentAnimation != null) {
            open = IRenderDoors.getRelevantTransformation("DOOR", door.currentAnimation)[1] * maxOpen;
            lock = IRenderDoors.getRelevantTransformation("LOCK", door.currentAnimation)[0] * 90;
        }

        poseStack.pushPose();

        HBMResourceManager.pheo_blast_door.renderPart(poseStack, consumer, "Frame", packedLight, packedOverlay);

        // Клип-плоскости в 1.20.1 нет — пропускаем.

        double clamped = Mth.clamp(open, 0, maxOpen);

        // LeftDoor + RightLock
        poseStack.pushPose();
        poseStack.translate(0, 0, clamped);
        HBMResourceManager.pheo_blast_door.renderPart(poseStack, consumer, "LeftDoor", packedLight, packedOverlay);

        poseStack.pushPose();
        poseStack.translate(0, 1.8125, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 + lock)));
        poseStack.translate(0, -1.8125, 0);
        HBMResourceManager.pheo_blast_door.renderPart(poseStack, consumer, "RightLock", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();

        // RightDoor + LeftLock
        poseStack.pushPose();
        poseStack.translate(0, 0, -clamped);
        HBMResourceManager.pheo_blast_door.renderPart(poseStack, consumer, "RightDoor", packedLight, packedOverlay);

        poseStack.pushPose();
        poseStack.translate(0, 1.8125, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float)(90 + lock)));
        poseStack.translate(0, -1.8125, 0);
        HBMResourceManager.pheo_blast_door.renderPart(poseStack, consumer, "LeftLock", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();

        poseStack.popPose();
    }
}