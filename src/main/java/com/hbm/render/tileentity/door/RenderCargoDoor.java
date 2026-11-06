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
public class RenderCargoDoor implements IRenderDoors {

    public static final RenderCargoDoor INSTANCE = new RenderCargoDoor();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation texture = DoorDecl.CARGO_DOOR.getSkinFromIndex(door.getSkinIndex());
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        double botMove = 0;
        double topMove = 0;

        if (door.state == TileEntityDoorGeneric.STATE_OPEN) {
            botMove = 2.0;
            topMove = 1.0;
        }

        if (door.currentAnimation != null) {
            double botProgress = Mth.clamp(IRenderDoors.getRelevantTransformation("BOT", door.currentAnimation)[1], 0, 1);
            double topProgress = Mth.clamp(IRenderDoors.getRelevantTransformation("TOP", door.currentAnimation)[1], 0, 1);
            botMove = botProgress * 2.0;
            topMove = topProgress * 1.0;
        }

        poseStack.pushPose();

        HBMResourceManager.pheo_cargo_door.renderPart(poseStack, consumer, "Frame", packedLight, packedOverlay);

        poseStack.pushPose();
        poseStack.translate(0, topMove, 0);
        HBMResourceManager.pheo_cargo_door.renderPart(poseStack, consumer, "DoorTop", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, botMove, 0);
        HBMResourceManager.pheo_cargo_door.renderPart(poseStack, consumer, "DoorBot", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}