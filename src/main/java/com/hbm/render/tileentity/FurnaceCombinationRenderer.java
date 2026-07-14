package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityFurnaceCombination;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
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
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class FurnaceCombinationRenderer implements BlockEntityRenderer<TileEntityFurnaceCombination> {

    public static final ResourceLocation FIRE_TEXTURE = ResLocation("hbm", "textures/particles/rbmk_fire.png");

    public FurnaceCombinationRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityFurnaceCombination tile, float partialTicks, PoseStack poseStack,
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

        ResourceLocation texture = HBMResourceManager.furnace_combination_tex;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // Рендерим основную модель
        HBMResourceManager.furnace_combination.renderAll(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        // Рендерим огонь, если печь работает
        if (tile.wasOn) {
            renderFire(poseStack, buffer, tile, combinedLight);
        }

        poseStack.popPose();
    }

    private void renderFire(PoseStack poseStack, MultiBufferSource buffer,
                            TileEntityFurnaceCombination tile, int combinedLight) {

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float rotationY = camera.getYRot();

        int texIndex = (int) ((tile.getLevel().getGameTime() / 2) % 14);
        float f0 = 1F / 14F;

        float uMin = texIndex * f0;
        float uMax = uMin + f0;
        float vMin = 0;
        float vMax = 1;

        poseStack.pushPose();
        poseStack.translate(0, 1.75, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotationY));

        // Используем прямой рендер через Tessellator, а не MultiBufferSource
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, FIRE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        Matrix4f matrix = poseStack.last().pose();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        double scaleH = 1;
        double scaleV = 3;

        // Вершины прямоугольника
        builder.vertex(matrix, (float) -scaleH, 0, 0).uv(uMax, vMax).endVertex();
        builder.vertex(matrix, (float) -scaleH, (float) scaleV, 0).uv(uMax, vMin).endVertex();
        builder.vertex(matrix, (float) scaleH, (float) scaleV, 0).uv(uMin, vMin).endVertex();
        builder.vertex(matrix, (float) scaleH, 0, 0).uv(uMin, vMax).endVertex();

        tesselator.end();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }
}