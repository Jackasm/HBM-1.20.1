package com.hbm.render.tileentity;

import com.hbm.blocks.generic.BlockSnowglobe;
import com.hbm.blocks.generic.BlockSnowglobe.SnowglobeType;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.block.TileEntitySnowglobe;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class RenderSnowglobe implements BlockEntityRenderer<TileEntitySnowglobe> {

    public RenderSnowglobe(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(TileEntitySnowglobe tile, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        BlockState state = tile.getBlockState();
        Direction rotation = state.getValue(BlockSnowglobe.FACING);

        switch (rotation) {
            case NORTH -> poseStack.mulPose(Axis.YN.rotationDegrees(270));
            case EAST -> poseStack.mulPose(Axis.YN.rotationDegrees(0));
            case SOUTH -> poseStack.mulPose(Axis.YN.rotationDegrees(90));
            case WEST -> poseStack.mulPose(Axis.YN.rotationDegrees(180));
        }

        renderSnowglobe(tile.type, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    public static void renderSnowglobe(SnowglobeType type, PoseStack poseStack,
                                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        double scale = 0.0625D;
        poseStack.pushPose();
        poseStack.scale((float) scale, (float) scale, (float) scale);

        // Socket
        VertexConsumer socketConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.snowglobe_socket_tex));
        HBMResourceManager.snowglobe.renderPart(poseStack, socketConsumer, "Socket", packedLight, packedOverlay);

        // Glass
        VertexConsumer glassConsumer = buffer.getBuffer(RenderType.entityTranslucent(HBMResourceManager.snowglobe_glass_tex));
        HBMResourceManager.snowglobe.renderPart(poseStack, glassConsumer, "Glass", packedLight, packedOverlay);

        // Features
        VertexConsumer featuresConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.snowglobe_features_tex));
        switch (type) {
            case NONE: break;
            case RIVETCITY: HBMResourceManager.snowglobe.renderPart(poseStack, featuresConsumer, "RivetCity", packedLight, packedOverlay); break;
            case TENPENNYTOWER: HBMResourceManager.snowglobe.renderPart(poseStack, featuresConsumer, "TenpennyTower", packedLight, packedOverlay); break;
            case LUCKY38: HBMResourceManager.snowglobe.renderPart(poseStack, featuresConsumer, "Lucky38", packedLight, packedOverlay); break;
            case SIERRAMADRE: HBMResourceManager.snowglobe.renderPart(poseStack, featuresConsumer, "SierraMadre", packedLight, packedOverlay); break;
            case PRYDWEN: HBMResourceManager.snowglobe.renderPart(poseStack, featuresConsumer, "Prydwen", packedLight, packedOverlay); break;
            default: break;
        }

        poseStack.popPose();

        // Текст на подставке
        if (type != SnowglobeType.NONE) {
            poseStack.pushPose();
            float f3 = 0.005F;

            poseStack.translate(0.251, 0.05, 0.25);
            poseStack.scale(f3, -f3, f3);

            Font font = Minecraft.getInstance().font;
            String text = type.label;
            float textWidth = font.width(text);
            float maxWidth = 50F;
            float offset = (maxWidth - textWidth/ 2f) ;
            poseStack.translate(0, 0, -offset);

            poseStack.mulPose(new Quaternionf().rotationY(90 * ((float) Math.PI / 180F)));

            font.drawInBatch(
                    text,
                    0, 0,
                    0xFFFFFF,
                    false,
                    poseStack.last().pose(),
                    buffer,
                    Font.DisplayMode.NORMAL,
                    0,
                    packedLight
            );

            poseStack.popPose();
        }
    }
}