package com.hbm.render.item;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class SatItemRenderer extends BlockEntityWithoutLevelRenderer {

    public SatItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        Block block = Block.byItem(stack.getItem());
        if (block == null) return;

        poseStack.pushPose();


        // Масштабирование для GUI
        if (context == ItemDisplayContext.GUI) {
            if (block == ModBlocks.SAT_FOEQ.get()) {
                poseStack.translate(0.1, 0.7, 0.5);
                poseStack.scale(0.07F, 0.07F, 0.07F);
                poseStack.mulPose(Axis.XP.rotationDegrees(25));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            } else if (block == ModBlocks.SAT_DOCK.get()) {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.2F, 0.2F, 0.2F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-75));
                poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            } else {
                poseStack.translate(0.5, 0.35, 0.5);
                poseStack.scale(0.04F, 0.04F, 0.04F);
                poseStack.mulPose(Axis.XP.rotationDegrees(25));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
        } else if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            if (block == ModBlocks.SAT_FOEQ.get()) {
                poseStack.translate(0.2, 0.5, 1.3);
                poseStack.scale(0.07F, 0.07F, 0.07F);
                poseStack.mulPose(Axis.XP.rotationDegrees(45));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            } else if (block == ModBlocks.SAT_DOCK.get()) {
                poseStack.translate(0.5, 0.7, 0.5);
                poseStack.scale(0.2F, 0.2F, 0.2F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-75));
                poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            } else {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.04F, 0.04F, 0.04F);
                poseStack.mulPose(Axis.XP.rotationDegrees(25));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
        } else if (context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            if (block == ModBlocks.SAT_FOEQ.get()) {
                poseStack.translate(0.5, 0.2, 0.7);
                poseStack.scale(0.07F, 0.07F, 0.07F);
                poseStack.mulPose(Axis.XP.rotationDegrees(45));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            } else if (block == ModBlocks.SAT_DOCK.get()) {
                poseStack.translate(0.5, 0.7, 0.5);
                poseStack.scale(0.2F, 0.2F, 0.2F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-75));
                poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            } else {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.04F, 0.04F, 0.04F);
                poseStack.mulPose(Axis.XP.rotationDegrees(25));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
        } else {
            poseStack.scale(0.8F, 0.8F, 0.8F);
        }

        // SAT_MAPPER
        if (block == ModBlocks.SAT_MAPPER.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, HBMResourceManager.sat_mapper_tex, HBMResourceManager.sat_mapper);
        }

        // SAT_RADAR
        if (block == ModBlocks.SAT_RADAR.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, HBMResourceManager.sat_radar_tex, HBMResourceManager.sat_radar);
        }

        // SAT_SCANNER
        if (block == ModBlocks.SAT_SCANNER.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, HBMResourceManager.sat_scanner_tex, HBMResourceManager.sat_scanner);
        }

        // SAT_LASER
        if (block == ModBlocks.SAT_LASER.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, HBMResourceManager.sat_laser_tex, HBMResourceManager.sat_laser);
        }

        // SAT_RESONATOR
        if (block == ModBlocks.SAT_RESONATOR.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, HBMResourceManager.sat_resonator_tex, HBMResourceManager.sat_resonator);
        }

        // SAT_FOEQ
        if (block == ModBlocks.SAT_FOEQ.get()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            renderSatelliteSimple(poseStack, buffer, packedLight, packedOverlay, HBMResourceManager.sat_foeq_tex, HBMResourceManager.sat_foeq);
        }

        // SAT_DOCK
        if (block == ModBlocks.SAT_DOCK.get()) {
            renderSatelliteDock(poseStack, buffer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private void renderSatellite(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
                                 ResourceLocation tex, HFRWavefrontObject model) {
        // Базовая часть (основание)
        VertexConsumer baseConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.sat_base_tex));
        HBMResourceManager.sat_base.renderAll(poseStack, baseConsumer, packedLight, packedOverlay);

        // Основная часть спутника
        VertexConsumer satConsumer = buffer.getBuffer(RenderType.entityCutout(tex));
        model.renderAll(poseStack, satConsumer, packedLight, packedOverlay);
    }

    private void renderSatelliteSimple(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
                                       ResourceLocation tex, HFRWavefrontObject model) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(tex));
        model.renderAll(poseStack, consumer, packedLight, packedOverlay);
    }

    private void renderSatelliteDock(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.translate(0, -0.5, 0);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.sat_dock_tex));
        HBMResourceManager.sat_dock.renderAll(poseStack, consumer, packedLight, packedOverlay);
    }
}