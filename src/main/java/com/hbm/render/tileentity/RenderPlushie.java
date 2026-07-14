package com.hbm.render.tileentity;

import com.hbm.blocks.generic.BlockPlushie.PlushieType;
import com.hbm.items.ModItems;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.util.HorsePronter;
import com.hbm.tileentity.block.TileEntityPlushie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class RenderPlushie implements BlockEntityRenderer<TileEntityPlushie> {

    public RenderPlushie(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(TileEntityPlushie tile, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        BlockState state = tile.getBlockState();
        float rotation = 22.5F * state.getValue(com.hbm.blocks.generic.BlockPlushie.FACING).get2DDataValue() + 90;
        poseStack.mulPose(new Quaternionf().rotationY(rotation * ((float) Math.PI / 180F)));

        // Squish animation
        if (tile.squishTimer > 0) {
            double squish = tile.squishTimer - partialTicks;
            double scale = 1 + (-(Math.sin(squish)) * squish) * 0.025;
            poseStack.scale(1, (float) scale, 1);
        }

        // Масштаб для разных типов
        switch (tile.type) {
            case NONE: break;
            case YOMI: poseStack.scale(0.5f, 0.5f, 0.5f); break;
            case NUMBERNINE: poseStack.scale(0.75f, 0.75f, 0.75f); break;
            case HUNDUN: poseStack.scale(1f, 1f, 1f); break;
        }

        renderPlushie(tile.type, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    public static void renderPlushie(PlushieType type, PoseStack poseStack,
                                     MultiBufferSource buffer, int packedLight, int packedOverlay) {

        switch (type) {
            case NONE: break;
            case YOMI:
                VertexConsumer yomiConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.yomi_tex));
                HBMResourceManager.yomi.renderAll(poseStack, yomiConsumer, packedLight, packedOverlay);
                break;

            case NUMBERNINE:
                poseStack.pushPose();
                poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(90)));
                poseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(-15)));
                poseStack.translate(0, -0.25, 0.75);
                float sc = 0.7f;
                poseStack.scale(sc, sc, sc);
                HorsePronter.reset();
                double r = 45;
                HorsePronter.pose(HorsePronter.id_body, 0, -r, 0);
                HorsePronter.pose(HorsePronter.id_tail, 0, 60, 90);
                HorsePronter.pose(HorsePronter.id_lbl, 0, -75 + r, 35);
                HorsePronter.pose(HorsePronter.id_rbl, 0, -75 + r, -35);
                HorsePronter.pose(HorsePronter.id_lfl, 0, r - 25, 5);
                HorsePronter.pose(HorsePronter.id_rfl, 0, r - 25, -5);
                HorsePronter.pose(HorsePronter.id_head, 0, r + 15, 0);
                HorsePronter.pront(poseStack, buffer, packedLight, packedOverlay);

                poseStack.pushPose();
                poseStack.translate(0, 1.1, -0.3);
                double s = 1.125D;
                poseStack.scale((float) (0.0625 * s), (float) (0.0625 * s), (float) (0.0625 * s));
                poseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(180)));

                VertexConsumer helmetConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.no9_tex));
                HBMResourceManager.armor_no9.renderPart(poseStack, helmetConsumer, "Helmet", packedLight, packedOverlay);

                VertexConsumer insigniaConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.no9_insignia_tex));
                HBMResourceManager.armor_no9.renderPart(poseStack, insigniaConsumer, "Insignia", packedLight, packedOverlay);
                poseStack.popPose();

                poseStack.pushPose();
                double scale2 = 0.25;
                poseStack.translate(-0.06, 1.13, 0);
                poseStack.scale((float) scale2, (float) scale2, (float) scale2);
                poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(75)));
                poseStack.mulPose(new Quaternionf().rotationZ((float) Math.toRadians(60)));

                Minecraft mc = Minecraft.getInstance();
                ItemStack cigStack = new ItemStack(ModItems.CIGARETTE.get());
                mc.getItemRenderer().renderStatic(
                        cigStack,
                        ItemDisplayContext.GUI,
                        packedLight,
                        packedOverlay,
                        poseStack,
                        buffer,
                        mc.level,
                        0
                );
                poseStack.popPose();

                poseStack.popPose();
                break;

            case HUNDUN:
                VertexConsumer hundunConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.hundun_tex));
                HBMResourceManager.hundun.renderPart(poseStack, hundunConsumer, "goober_posed", packedLight, packedOverlay);
                break;
        }
    }
}