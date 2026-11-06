package com.hbm.render.block;

import com.hbm.blocks.machine.MachineCapacitor;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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

        switch (facing) {
            case DOWN -> flip = (float) Math.PI;
            case UP -> flip = 0F;
            case SOUTH -> rotation = 90F / 180F * (float) Math.PI;
            case NORTH -> rotation = 270F / 180F * (float) Math.PI;
            case EAST -> rotation = 180F / 180F * (float) Math.PI;
            case WEST -> rotation = 0F;
        }

        if (rotation != 0F) {
            poseStack.mulPose(Axis.YP.rotation(rotation));
        }
        if (flip != 0F) {
            poseStack.mulPose(Axis.XP.rotation(flip));
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