package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineAutosaw;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AutosawRenderer implements BlockEntityRenderer<TileEntityMachineAutosaw> {

    public AutosawRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityMachineAutosaw tile, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        double turn = tile.prevRotationYaw + (tile.rotationYaw - tile.prevRotationYaw) * partialTicks;
        double angle = 80 - (tile.prevRotationPitch + (tile.rotationPitch - tile.prevRotationPitch) * partialTicks);
        float spin = tile.lastSpin + (tile.spin - tile.lastSpin) * partialTicks;
        double engine = tile.isOn ? Math.sin((tile.getLevel().getGameTime() * 2 + partialTicks) % (Math.PI * 2)) : 0;

        renderCommon(poseStack, buffer, turn, angle, spin, engine, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderCommon(PoseStack poseStack, MultiBufferSource buffer,
                              double turn, double angle, double spin, double engine,
                              int combinedLight, int combinedOverlay) {

        ResourceLocation texture = HBMResourceManager.autosaw_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // База
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "Base", combinedLight, combinedOverlay);

        // Основная часть (поворот)
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((float) -turn));
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "Main", combinedLight, combinedOverlay);

        // Двигатель (вибрация)
        poseStack.pushPose();
        poseStack.translate(0, engine * 0.01, 0);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "Engine", combinedLight, combinedOverlay);
        poseStack.popPose();

        // Верхняя рука
        poseStack.pushPose();
        poseStack.translate(0, 1.75, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) angle));
        poseStack.translate(0, -1.75, 0);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "ArmUpper", combinedLight, combinedOverlay);

        // Нижняя рука
        poseStack.translate(0, 1.75, -4);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) -angle * 2));
        poseStack.translate(0, -1.75, 4);
        poseStack.translate(-0.01, 0, 0);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "ArmLower", combinedLight, combinedOverlay);
        poseStack.translate(0.01, 0, 0);

        // Кончик руки
        poseStack.translate(0, 1.75, -8);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) angle));
        poseStack.translate(0, -1.75, 8);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "ArmTip", combinedLight, combinedOverlay);

        // Пила
        poseStack.translate(0, 1.75, -10);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) spin));
        poseStack.translate(0, -1.75, 10);
        HBMResourceManager.autosaw.renderPart(poseStack, vertexConsumer, "Sawblade", combinedLight, combinedOverlay);

        poseStack.popPose(); // ArmUpper
        poseStack.popPose(); // Main
    }
}