package com.hbm.render.item;

import com.hbm.blocks.network.FluidDuctBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RenderFluidDuctBoxItem extends BlockEntityWithoutLevelRenderer {

    private static final float MIN = 0.125f;
    private static final float MAX = 0.875f;

    public RenderFluidDuctBoxItem() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        Block block = ((BlockItem) stack.getItem()).getBlock();
        if (!(block instanceof FluidDuctBox duct)) return;

        int material = duct.getMaterialFromBlock(block);
        int meta = material * 5;

        // Определяем размер "набухания"
        float lower = MIN;
        float upper = MAX;
        for (int i = 2; i < 13; i += 3) {
            if (meta > i) {
                lower += 0.0625f;
                upper -= 0.0625f;
            }
        }

        // Цвет (без жидкости, просто текстура)
        int color = 0xffffff;

        // Получаем текстуры
        ResourceLocation sideTex = duct.getStraightTexture(material);
        ResourceLocation endTex = duct.getEndTexture(material);

        VertexConsumer sideConsumer = buffer.getBuffer(RenderType.entityCutout(sideTex));
        VertexConsumer endConsumer = buffer.getBuffer(RenderType.entityCutout(endTex));

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        // Трансформации в зависимости от контекста
        if (context == ItemDisplayContext.GUI) {
            poseStack.scale(0.75f, 0.75f, 0.75f);
            poseStack.mulPose(Axis.XP.rotationDegrees(30));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
        } else if (context == ItemDisplayContext.GROUND) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else if (context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        } else if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.scale(0.6f, 0.6f, 0.6f);
        }

        // Рисуем центральный куб (всегда как узел)
        drawCube(poseStack, sideConsumer, endConsumer,
                lower, lower, lower, upper, upper, upper,
                packedLight, packedOverlay, color, true);

        poseStack.popPose();
    }

    private void drawCube(PoseStack poseStack, VertexConsumer sideConsumer, VertexConsumer endConsumer,
                          float x1, float y1, float z1, float x2, float y2, float z2,
                          int light, int overlay, int color, boolean useEndForZ) {
        // Преобразуем в координаты от -0.5 до 0.5
        float cx1 = x1 - 0.5f;
        float cy1 = y1 - 0.5f;
        float cz1 = z1 - 0.5f;
        float cx2 = x2 - 0.5f;
        float cy2 = y2 - 0.5f;
        float cz2 = z2 - 0.5f;

        Matrix4f matrix = poseStack.last().pose();

        // Грань X- (west)
        vertex(matrix, sideConsumer, cx1, cy1, cz1, 0, 1, -1, 0, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx1, cy2, cz1, 0, 0, -1, 0, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx1, cy2, cz2, 1, 0, -1, 0, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx1, cy1, cz2, 1, 1, -1, 0, 0, light, overlay, color);

        // Грань X+ (east)
        vertex(matrix, sideConsumer, cx2, cy1, cz1, 0, 1, 1, 0, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx2, cy1, cz2, 1, 1, 1, 0, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx2, cy2, cz2, 1, 0, 1, 0, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx2, cy2, cz1, 0, 0, 1, 0, 0, light, overlay, color);

        // Грань Y- (down)
        vertex(matrix, sideConsumer, cx1, cy1, cz1, 0, 1, 0, -1, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx2, cy1, cz1, 1, 1, 0, -1, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx2, cy1, cz2, 1, 0, 0, -1, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx1, cy1, cz2, 0, 0, 0, -1, 0, light, overlay, color);

        // Грань Y+ (up)
        vertex(matrix, sideConsumer, cx1, cy2, cz1, 0, 0, 0, 1, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx1, cy2, cz2, 1, 0, 0, 1, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx2, cy2, cz2, 1, 1, 0, 1, 0, light, overlay, color);
        vertex(matrix, sideConsumer, cx2, cy2, cz1, 0, 1, 0, 1, 0, light, overlay, color);

        // Грань Z- (north)
        VertexConsumer zConsumer = useEndForZ ? endConsumer : sideConsumer;
        vertex(matrix, zConsumer, cx1, cy1, cz1, 0, 1, 0, 0, -1, light, overlay, color);
        vertex(matrix, zConsumer, cx2, cy1, cz1, 1, 1, 0, 0, -1, light, overlay, color);
        vertex(matrix, zConsumer, cx2, cy2, cz1, 1, 0, 0, 0, -1, light, overlay, color);
        vertex(matrix, zConsumer, cx1, cy2, cz1, 0, 0, 0, 0, -1, light, overlay, color);

        // Грань Z+ (south)
        vertex(matrix, zConsumer, cx1, cy1, cz2, 0, 1, 0, 0, 1, light, overlay, color);
        vertex(matrix, zConsumer, cx1, cy2, cz2, 0, 0, 0, 0, 1, light, overlay, color);
        vertex(matrix, zConsumer, cx2, cy2, cz2, 1, 0, 0, 0, 1, light, overlay, color);
        vertex(matrix, zConsumer, cx2, cy1, cz2, 1, 1, 0, 0, 1, light, overlay, color);
    }

    private void vertex(Matrix4f matrix, VertexConsumer consumer, float x, float y, float z,
                        float u, float v, float nx, float ny, float nz,
                        int light, int overlay, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        consumer.vertex(matrix, x, y, z)
                .color(r, g, b, 255)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();
    }
}