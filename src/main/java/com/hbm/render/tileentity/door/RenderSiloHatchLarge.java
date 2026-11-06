package com.hbm.render.tileentity.door;

import com.hbm.main.HBMResourceManager;
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
public class RenderSiloHatchLarge implements IRenderDoors {

    public static final RenderSiloHatchLarge INSTANCE = new RenderSiloHatchLarge();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation texture = HBMResourceManager.silo_hatch_large_tex;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        long ms = System.currentTimeMillis() - door.animStartTime;
        float openTicks = Mth.clamp(
                (door.state == TileEntityDoorGeneric.STATE_CLOSING || door.state == TileEntityDoorGeneric.STATE_CLOSED)
                        ? door.getDoorType().timeToOpen() * 50 - ms
                        : ms,
                0, door.getDoorType().timeToOpen() * 50) * 0.02F;

        HBMResourceManager.silo_hatch_large.renderPart(poseStack, consumer, "Frame", packedLight, packedOverlay);

        float[] trans = new float[3];
        float[] orig = new float[3];
        float[] rot = new float[3];
        door.getDoorType().getTranslation("Hatch", openTicks, false, trans);
        door.getDoorType().getOrigin("Hatch", orig);
        door.getDoorType().getRotation("Hatch", openTicks, rot);

        poseStack.pushPose();
        poseStack.translate(orig[0], orig[1], orig[2]);
        if (rot[0] != 0) poseStack.mulPose(Axis.XP.rotationDegrees(rot[0]));
        if (rot[1] != 0) poseStack.mulPose(Axis.YP.rotationDegrees(rot[1]));
        if (rot[2] != 0) poseStack.mulPose(Axis.ZP.rotationDegrees(rot[2]));
        poseStack.translate(-orig[0] + trans[0], -orig[1] + trans[1], -orig[2] + trans[2]);

        HBMResourceManager.silo_hatch_large.renderPart(poseStack, consumer, "Hatch", packedLight, packedOverlay);

        poseStack.popPose();
    }
}