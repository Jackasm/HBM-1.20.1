package com.hbm.render.block;

import com.hbm.blocks.generic.BlockPipe;
import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class PipeBlockRenderer implements BlockEntityRenderer<BlockEntity> {

    public PipeBlockRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull BlockEntity tile, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = tile.getBlockState();
        if (!(state.getBlock() instanceof BlockPipe pipe)) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        // Получаем ось из состояния блока
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

        float rotX = 0;
        float rotZ = 0;

        // Поворачиваем в зависимости от оси
        switch (axis) {
            case X:
                rotZ = 90F / 180F * (float) Math.PI;
                break;
            case Z:
                rotX = 90F / 180F * (float) Math.PI;
                break;
            case Y:
            default:
                // Y - стандартное положение
                break;
        }

        if (rotX != 0) {
            poseStack.mulPose(Axis.XP.rotation(rotX));
        }
        if (rotZ != 0) {
            poseStack.mulPose(Axis.ZP.rotation(rotZ));
        }

        String sideTexture = pipe.getSideTexture();
        String topTexture = pipe.getTopTexture();

        // Рендерим в зависимости от типа трубы
        switch (pipe.getPipeType()) {
            case NORMAL:
                HBMResourceManager.pipe.renderPart(poseStack, buffer, "Top",
                        ResLocation(RefStrings.MODID, "textures/block/" + topTexture + ".png"),
                        packedLight, packedOverlay);
                HBMResourceManager.pipe.renderPart(poseStack, buffer, "Side",
                        ResLocation(RefStrings.MODID, "textures/block/" + sideTexture + ".png"),
                        packedLight, packedOverlay);
                break;

            case RIM:
                HBMResourceManager.pipe_rim.renderPart(poseStack, buffer, "Top",
                        ResLocation(RefStrings.MODID, "textures/block/" + topTexture + ".png"),
                        packedLight, packedOverlay);
                HBMResourceManager.pipe_rim.renderPart(poseStack, buffer, "Side",
                        ResLocation(RefStrings.MODID, "textures/block/" + sideTexture + ".png"),
                        packedLight, packedOverlay);
                break;

            case QUAD:
                HBMResourceManager.pipe_quad.renderPart(poseStack, buffer, "Top",
                        ResLocation(RefStrings.MODID, "textures/block/" + topTexture + ".png"),
                        packedLight, packedOverlay);
                HBMResourceManager.pipe_quad.renderPart(poseStack, buffer, "Side",
                        ResLocation(RefStrings.MODID, "textures/block/" + sideTexture + ".png"),
                        packedLight, packedOverlay);
                break;

            case FRAMED:
                HBMResourceManager.pipe_rim.renderPart(poseStack, buffer, "Top",
                        ResLocation(RefStrings.MODID, "textures/block/" + topTexture + ".png"),
                        packedLight, packedOverlay);
                HBMResourceManager.pipe_rim.renderPart(poseStack, buffer, "Side",
                        ResLocation(RefStrings.MODID, "textures/block/" + sideTexture + ".png"),
                        packedLight, packedOverlay);
                HBMResourceManager.pipe_frame.renderPart(poseStack, buffer, "Frame",
                        ResLocation(RefStrings.MODID, "textures/block/pipe_frame.png"),
                        packedLight, packedOverlay);
                HBMResourceManager.pipe_frame.renderPart(poseStack, buffer, "Mesh",
                        ResLocation(RefStrings.MODID, "textures/block/pipe_mesh.png"),
                        packedLight, packedOverlay);
                break;
        }

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