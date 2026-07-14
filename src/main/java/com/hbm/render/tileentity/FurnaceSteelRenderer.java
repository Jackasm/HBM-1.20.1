package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityFurnaceSteel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class FurnaceSteelRenderer implements BlockEntityRenderer<TileEntityFurnaceSteel> {

    public FurnaceSteelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityFurnaceSteel tile, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Получаем направление из состояния блока
        BlockState state = tile.getBlockState();
        Direction facing = state.getValue(BlockDummyable.FACING).getOpposite();

        // Поворачиваем в зависимости от направления
        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.YN.rotationDegrees(0));
            case EAST -> poseStack.mulPose(Axis.YN.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(Axis.YN.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YN.rotationDegrees(270));
        }
        poseStack.mulPose(Axis.YN.rotationDegrees(-90));

        ResourceLocation texture = HBMResourceManager.furnace_steel_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // Рендерим основную модель
        HBMResourceManager.furnace_steel.renderAll(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        // Рендерим свечение, если печь работает
        if (tile.wasOn) {
            renderGlow(poseStack, buffer, tile, combinedLight);
        }

        poseStack.popPose();
    }

    private void renderGlow(PoseStack poseStack, MultiBufferSource buffer,
                            TileEntityFurnaceSteel tile, int combinedLight) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE);

        float time = (System.currentTimeMillis() % 10000) / 10000.0F;
        float col = (float) Math.sin(time * Math.PI * 2);

        VertexConsumer builder = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();

        float r = 0.875F + col * 0.125F;
        float g = 0.625F + col * 0.375F;
        float b = 0.0F;
        float a = 0.5F;

        int light = LightTexture.pack(15, 15); // максимальная яркость

        for (int i = 0; i < 4; i++) {
            float x = (float) (1 + i * 0.0625);

            // Грань с нормалью (1, 0, 0)
            builder.vertex(matrix, x, 1.0F, -1.0F).color(r, g, b, a).uv(0, 0).uv2(light).endVertex();
            builder.vertex(matrix, x, 1.0F, 1.0F).color(r, g, b, a).uv(0, 0).uv2(light).endVertex();
            builder.vertex(matrix, x, 0.5F, 1.0F).color(r, g, b, a).uv(0, 0).uv2(light).endVertex();
            builder.vertex(matrix, x, 0.5F, -1.0F).color(r, g, b, a).uv(0, 0).uv2(light).endVertex();
        }

        RenderSystem.disableBlend();
    }
}