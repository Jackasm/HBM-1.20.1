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
public class RenderAirlockDoor implements IRenderDoors {

    public static final RenderAirlockDoor INSTANCE = new RenderAirlockDoor();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation texture = DoorDecl.ROUND_AIRLOCK_DOOR.getSkinFromIndex(door.getSkinIndex());
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        double maxOpen = 1.5;
        double open = 0;
        if (door.state == TileEntityDoorGeneric.STATE_OPEN) open = maxOpen;

        if (door.currentAnimation != null) {
            double[] transform = IRenderDoors.getRelevantTransformation("DOOR", door.currentAnimation);
            open = transform[1] * maxOpen;
        }

        // В 1.7.10 было GL11.glDisable(GL11.GL_CULL_FACE) — в 1.20.1 это настраивается на уровне RenderType
        // (entityCutout без culling уже даёт нужный результат).

        poseStack.pushPose();
        poseStack.translate(0, 0, 0.5);

        // Frame
        HBMResourceManager.pheo_airlock_door.renderPart(poseStack, consumer, "Frame", packedLight, packedOverlay);

        // В 1.7.10 тут были clip-planes (GL_CLIP_PLANE0/1), в 1.20.1 их нет — пропускаем.
        // Без них Left/Right могут чуть заходить за Frame при полном открытии.

        double clamped = Mth.clamp(open, 0, maxOpen);

        // Left
        poseStack.pushPose();
        poseStack.translate(0, 0, clamped);
        HBMResourceManager.pheo_airlock_door.renderPart(poseStack, consumer, "Left", packedLight, packedOverlay);
        poseStack.popPose();

        // Right
        poseStack.pushPose();
        poseStack.translate(0, 0, -clamped);
        HBMResourceManager.pheo_airlock_door.renderPart(poseStack, consumer, "Right", packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}