package com.hbm.render.tileentity.door;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderVaultDoor implements IRenderDoors {

    public static final RenderVaultDoor INSTANCE = new RenderVaultDoor();

    @Override
    public void render(@NotNull TileEntityDoorGeneric door, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation doorTex = HBMResourceManager.pheo_vault_door_3;
        ResourceLocation labelTex = HBMResourceManager.pheo_label_101;

        labelTex = switch (door.getSkinIndex()) {
            case 1 -> HBMResourceManager.pheo_label_87;
            case 2 -> HBMResourceManager.pheo_label_106;
            case 3 -> {
                doorTex = HBMResourceManager.pheo_vault_door_4;
                yield HBMResourceManager.pheo_label_81;
            }
            case 4 -> {
                doorTex = HBMResourceManager.pheo_vault_door_4;
                yield HBMResourceManager.pheo_label_111;
            }
            case 5 -> {
                doorTex = HBMResourceManager.pheo_vault_door_s;
                yield HBMResourceManager.pheo_label_2;
            }
            case 6 -> {
                doorTex = HBMResourceManager.pheo_vault_door_s;
                yield HBMResourceManager.pheo_label_99;
            }
            default -> labelTex;
        };

        double pull = 0;
        double slide = 0;
        if (door.state == TileEntityDoorGeneric.STATE_OPEN) {
            pull = 1;
            slide = 1;
        }
        if (door.currentAnimation != null) {
            pull = IRenderDoors.getRelevantTransformation("PULL", door.currentAnimation)[2];
            slide = IRenderDoors.getRelevantTransformation("SLIDE", door.currentAnimation)[0];
        }
        double diameter = 4.25D;
        double circumference = diameter * Math.PI;
        slide *= 5D;
        double roll = 360D * slide / circumference;

        poseStack.pushPose();

        // Frame — doorTex
        HBMResourceManager.pheo_vault_door.renderPart(poseStack, buffer, "Frame",
                doorTex, packedLight, packedOverlay);

        poseStack.translate(-pull, 0, 0);
        poseStack.translate(0, 0, slide);
        poseStack.translate(0, 2.5, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) roll));
        poseStack.translate(0, -2.5, 0);

        // Door — doorTex
        HBMResourceManager.pheo_vault_door.renderPart(poseStack, buffer, "Door",
                doorTex, packedLight, packedOverlay);

        // Label — labelTex
        HBMResourceManager.pheo_vault_door.renderPart(poseStack, buffer, "Label",
                labelTex, packedLight, packedOverlay);

        poseStack.popPose();
    }
}