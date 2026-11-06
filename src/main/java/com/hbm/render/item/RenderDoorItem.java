package com.hbm.render.item;

import com.hbm.blocks.generic.BlockDoorGeneric;
import com.hbm.render.tileentity.door.IRenderDoors;
import com.hbm.tileentity.DoorDecl;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderDoorItem extends BlockEntityWithoutLevelRenderer {

    public RenderDoorItem() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int combinedLight, int combinedOverlay) {

        Block block = Block.byItem(stack.getItem());
        if (!(block instanceof BlockDoorGeneric doorBlock)) return;

        DoorDecl decl = doorBlock.type;
        if (decl == null) return;

        IRenderDoors renderer = decl.getSEDNARenderer();
        if (renderer == null) return;

        TileEntityDoorGeneric dummy = new TileEntityDoorGeneric(BlockPos.ZERO, block.defaultBlockState());
        dummy.state = TileEntityDoorGeneric.STATE_CLOSED;
        dummy.currentAnimation = null;
        dummy.setSkinIndex(0);

        poseStack.pushPose();
        applyDoorTransform(decl, context, poseStack);
        renderer.render(dummy, poseStack, buffer, combinedLight, combinedOverlay);
        poseStack.popPose();
    }

    private void applyDoorTransform(DoorDecl decl, ItemDisplayContext context, PoseStack poseStack) {
        // Базовая трансформация по контексту
        float guiScale     = 0.2f;
        float groundScale  = 0.25f;
        float handScale    = 0.3f;
        float fixedScale   = 0.3f;
        float headScale    = 0.3f;
        float defaultScale = 0.4f;

        float xOffset = 0f;
        float yOffset = 0f;
        float zOffset = 0f;

        // Индивидуальные параметры по двери
        if (decl == DoorDecl.SLIDE_DOOR) {
            guiScale = 0.17f; groundScale = 0.28f; handScale = 0.32f;
            yOffset = 0.15f;
            xOffset = 0f;
        } else if (decl == DoorDecl.FIRE_DOOR) {
            guiScale = 0.25f; groundScale = 0.32f; handScale = 0.38f;
            yOffset = 0.15f;
            xOffset = -0.1f;
        } else if (decl == DoorDecl.VAULT_DOOR) {
            guiScale = 0.13f; groundScale = 0.18f; handScale = 0.22f;
            yOffset = 0.2f;
            xOffset = -0.1f;
        } else if (decl == DoorDecl.SLIDING_SEAL_DOOR) {
            guiScale = 0.35f; groundScale = 0.4f; handScale = 0.5f;
            yOffset = 0.1f;
            xOffset = -0.1f;
        } else if (decl == DoorDecl.SECURE_ACCESS_DOOR) {
            guiScale = 0.15f; groundScale = 0.22f; handScale = 0.25f;
            yOffset = 0.1f;
            xOffset = 0f;
        } else if (decl == DoorDecl.ROUND_AIRLOCK_DOOR) {
            guiScale = 0.2f; groundScale = 0.26f; handScale = 0.3f;
            yOffset = 0.1f;
            xOffset = -0.075f;
        } else if (decl == DoorDecl.QE_SLIDING) {
            guiScale = 0.3f; groundScale = 0.35f; handScale = 0.4f;
            yOffset = 0.15f;
            xOffset = -0.2f;
        } else if (decl == DoorDecl.QE_CONTAINMENT) {
            guiScale = 0.25f; groundScale = 0.3f; handScale = 0.35f;
            yOffset = 0.1f;
            xOffset = -0.05f;
        } else if (decl == DoorDecl.WATER_DOOR) {
            guiScale = 0.25f; groundScale = 0.3f; handScale = 0.35f;
            yOffset = 0.1f;
            xOffset = -0.05f;
        } else if (decl == DoorDecl.SILO_HATCH) {
            guiScale = 0.12f; groundScale = 0.4f; handScale = 0.45f;
            yOffset = 0.2f;
            xOffset = 0f;
        } else if (decl == DoorDecl.SILO_HATCH_LARGE) {
            guiScale = 0.11f; groundScale = 0.32f; handScale = 0.38f;
            yOffset = 0.2f;
            xOffset = 0f;
        } else if (decl == DoorDecl.LARGE_VEHICLE_DOOR) {
            guiScale = 0.13f; groundScale = 0.18f; handScale = 0.2f;
            yOffset = 0.1f;
            xOffset = 0f;
        } else if (decl == DoorDecl.CARGO_DOOR) {
            guiScale = 0.25f; groundScale = 0.3f; handScale = 0.35f;
            yOffset = 0.1f;
            xOffset = 0f;
        }

        switch (context) {
            case GUI -> {
                poseStack.translate(0.5 + xOffset, yOffset, 0.5 + zOffset);
                poseStack.scale(guiScale, guiScale, guiScale);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case GROUND -> {
                poseStack.translate(0.5 + xOffset, yOffset, 0.5 + zOffset);
                poseStack.scale(groundScale, groundScale, groundScale);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIXED -> {
                poseStack.translate(0.5 + xOffset, yOffset, 0.5 + zOffset);
                poseStack.scale(fixedScale, fixedScale, fixedScale);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND,
                 THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5 + xOffset, yOffset, 0.5 + zOffset);
                poseStack.scale(handScale, handScale, handScale);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case HEAD -> {
                poseStack.translate(0.5 + xOffset, yOffset, 0.5 + zOffset);
                poseStack.scale(headScale, headScale, headScale);
            }
            default -> {
                poseStack.translate(0.5 + xOffset, yOffset, 0.5 + zOffset);
                poseStack.scale(defaultScale, defaultScale, defaultScale);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
        }
    }
}