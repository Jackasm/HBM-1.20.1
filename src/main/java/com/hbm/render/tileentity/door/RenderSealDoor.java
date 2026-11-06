package com.hbm.render.tileentity.door;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.DoorDecl;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.hbm.util.Library;
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
public class RenderSealDoor implements IRenderDoors {

    public static final RenderSealDoor INSTANCE = new RenderSealDoor();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation texture = DoorDecl.SLIDING_SEAL_DOOR.getSkinFromIndex(door.getSkinIndex());
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        double maxRaise = 1;
        double raise = 0;
        if (door.state == TileEntityDoorGeneric.STATE_OPEN) raise = maxRaise;

        if (door.currentAnimation != null) {
            raise = IRenderDoors.getRelevantTransformation("DOOR", door.currentAnimation)[1] * maxRaise;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0);

        HBMResourceManager.pheo_seal_door.renderPart(poseStack, consumer, "Frame", packedLight, packedOverlay);

        // Клип-плоскости в 1.20.1 нет — пропускаем.

        poseStack.pushPose();
        poseStack.translate(0, 0, Library.smoothstep(Mth.clamp(raise, 0, maxRaise), 0, 1) * 0.9);
        HBMResourceManager.pheo_seal_door.renderPart(poseStack, consumer, "Door", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}