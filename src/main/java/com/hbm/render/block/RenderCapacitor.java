package com.hbm.render.block;

import com.hbm.blocks.machine.MachineCapacitor;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RenderCapacitor implements BlockEntityRenderer<BlockEntity> {

    public RenderCapacitor(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull BlockEntity tile, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = tile.getBlockState();
        if (!(state.getBlock() instanceof MachineCapacitor capacitor)) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        Direction facing = state.getValue(MachineCapacitor.FACING);

        float rotation = 0F;
        float flip = 0F;

        // Обработка направления
        if (facing == Direction.DOWN) {
            flip = (float) Math.PI;
        } else if (facing == Direction.SOUTH) {
            rotation = 90F / 180F * (float) Math.PI;
        } else if (facing == Direction.NORTH) {
            rotation = 270F / 180F * (float) Math.PI;
        } else if (facing == Direction.EAST) {
            rotation = 180F / 180F * (float) Math.PI;
        } else if (facing == Direction.UP || facing == Direction.WEST) {
            flip = (float) Math.PI * 0.5F;
        }

        if (rotation != 0F) {
            poseStack.mulPose(Axis.YP.rotation(rotation));
        }
        if (flip != 0F) {
            if (flip == (float)Math.PI) {
                poseStack.mulPose(Axis.XP.rotation((float)Math.PI));
            } else if (flip == (float)Math.PI * 0.5F) {
                poseStack.mulPose(Axis.XP.rotation((float)Math.PI * 0.5F));
            }
        }

        HFRWavefrontObject model = HBMResourceManager.capacitor;
        ResourceLocation[] textures = capacitor.getTextures();

        // Top
        model.renderPart(poseStack, buffer, "Top", textures[0], packedLight, packedOverlay);
        // Side
        model.renderPart(poseStack, buffer, "Side", textures[1], packedLight, packedOverlay);
        // Bottom
        model.renderPart(poseStack, buffer, "Bottom", textures[2], packedLight, packedOverlay);
        // InnerTop
        model.renderPart(poseStack, buffer, "InnerTop", textures[3], packedLight, packedOverlay);
        // InnerSide
        model.renderPart(poseStack, buffer, "InnerSide", textures[4], packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockEntity blockEntity) {
        return true;
    }
}