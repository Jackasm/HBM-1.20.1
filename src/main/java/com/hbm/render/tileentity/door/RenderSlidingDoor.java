package com.hbm.render.tileentity.door;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.DoorDecl;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderSlidingDoor implements IRenderDoors {

    public static final RenderSlidingDoor INSTANCE = new RenderSlidingDoor();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation texture = DoorDecl.QE_SLIDING.getSkinFromIndex(door.getSkinIndex());
        // В 1.7.10 было GL11.glDisable(GL11.GL_CULL_FACE) → здесь noCull
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        double maxOpen = 0.95;
        double open = 0;
        if (door.state == TileEntityDoorGeneric.STATE_OPEN) open = maxOpen;

        if (door.currentAnimation != null) {
            open = IRenderDoors.getRelevantTransformation("DOOR", door.currentAnimation)[1] * maxOpen;
        }

        poseStack.pushPose();
        poseStack.translate(0.53125, 0.001, 0.5);

        HBMResourceManager.pheo_sliding_door.renderPart(poseStack, consumer, "Frame", packedLight, packedOverlay);

        double clamped = Mth.clamp(open, 0, maxOpen);

        poseStack.pushPose();
        poseStack.translate(0, 0, clamped);
        HBMResourceManager.pheo_sliding_door.renderPart(poseStack, consumer, "Left", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 0, -clamped);
        HBMResourceManager.pheo_sliding_door.renderPart(poseStack, consumer, "Right", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}