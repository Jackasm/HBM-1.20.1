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
public class RenderFireDoor implements IRenderDoors {

    public static final RenderFireDoor INSTANCE = new RenderFireDoor();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation texture = DoorDecl.FIRE_DOOR.getSkinFromIndex(door.getSkinIndex());
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        double maxRaise = 2.75;
        double raise = 0;
        if (door.state == TileEntityDoorGeneric.STATE_OPEN) raise = maxRaise;

        if (door.currentAnimation != null) {
            raise = IRenderDoors.getRelevantTransformation("DOOR", door.currentAnimation)[1] * maxRaise;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90F));
        poseStack.translate(-0.5, 0, 0);

        HBMResourceManager.pheo_fire_door.renderPart(poseStack, consumer, "Frame", packedLight, packedOverlay);

        poseStack.pushPose();
        poseStack.translate(0, Mth.clamp(raise, 0, maxRaise), 0);
        HBMResourceManager.pheo_fire_door.renderPart(poseStack, consumer, "Door", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}