package com.hbm.render.tileentity;

import com.hbm.blocks.network.FoundryChannel;
import com.hbm.blocks.network.FoundryOutlet;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.network.TileEntityFoundryChannel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderFoundryChannel implements BlockEntityRenderer<TileEntityFoundryChannel> {

    private static final ResourceLocation TEXTURE = ResLocation("hbm", "textures/block/network/foundry_channel.png");
    private static final ResourceLocation LAVA = ResLocation("hbm", "textures/block/network/lava.png");

    public RenderFoundryChannel(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(TileEntityFoundryChannel entity, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int light, int overlay) {
        Level level = entity.getLevel();
        if (level == null) return;

        BlockPos pos = entity.getBlockPos();

        // Проверяем соединения
        boolean posX = canConnectTo(level, pos, Direction.EAST);
        boolean negX = canConnectTo(level, pos, Direction.WEST);
        boolean posZ = canConnectTo(level, pos, Direction.SOUTH);
        boolean negZ = canConnectTo(level, pos, Direction.NORTH);

        stack.pushPose();
        stack.translate(0.5, 0, 0.5);

        // Центральная плита
        HBMResourceManager.foundry_channel.renderPart(stack, buffer, "CenterBase", TEXTURE, light, overlay);

        // Рендер ответвлений
        renderArm(stack, buffer, "posX", posX, light, overlay);
        renderArm(stack, buffer, "negX", negX, light, overlay);
        renderArm(stack, buffer, "posZ", posZ, light, overlay);
        renderArm(stack, buffer, "negZ", negZ, light, overlay);

        // Рендер расплава
        if (entity.amount > 0 && entity.type != null) {
            stack.pushPose();

            NTMMaterial mat = entity.type;
            int color = mat.moltenColor;
            Color col = new Color(color).brighter();

            float r = col.getRed() / 255.0f;
            float g = col.getGreen() / 255.0f;
            float b = col.getBlue() / 255.0f;

            double level_fill = entity.amount * 0.25D / entity.getCapacity();
            float y = (float) (0.125 + level_fill);

            VertexConsumer lavaConsumer = buffer.getBuffer(RenderType.entitySolid(LAVA));
            Matrix4f matrix = stack.last().pose();
            Matrix3f normal = stack.last().normal();
            int fullBright = 0xF000F0;

            // Центр
            stack.translate(-0.5, 0, -0.5);
            drawLavaFace(lavaConsumer, matrix, normal, 0.375, 0.625, 0.375, 0.625, y, r, g, b, overlay, fullBright);

            // Ответвления
            if (posX) drawLavaFace(lavaConsumer, matrix, normal, 0.625, 1.0, 0.3125, 0.6875, y, r, g, b, overlay, fullBright);
            if (negX) drawLavaFace(lavaConsumer, matrix, normal, 0.0, 0.375, 0.3125, 0.6875, y, r, g, b, overlay, fullBright);
            if (posZ) drawLavaFace(lavaConsumer, matrix, normal, 0.3125, 0.6875, 0.625, 1.0, y, r, g, b, overlay, fullBright);
            if (negZ) drawLavaFace(lavaConsumer, matrix, normal, 0.3125, 0.6875, 0.0, 0.375, y, r, g, b, overlay, fullBright);

            stack.popPose();
        }

        stack.popPose();
    }

    private void renderArm(PoseStack stack, MultiBufferSource buffer, String dirName, boolean isOpen, int light, int overlay) {
        if (isOpen) {
            HBMResourceManager.foundry_channel.renderPart(stack, buffer, "Arm_" + dirName + "_open_base", TEXTURE, light, overlay);
            HBMResourceManager.foundry_channel.renderPart(stack, buffer, "Arm_" + dirName + "_open_wall1", TEXTURE, light, overlay);
            HBMResourceManager.foundry_channel.renderPart(stack, buffer, "Arm_" + dirName + "_open_wall2", TEXTURE, light, overlay);
        } else {
            // Закрытая заглушка у центра — сдвигаем на 2 пикселя (0.125) обратно к центру
            stack.pushPose();
            switch (dirName) {
                case "posX" -> stack.translate(-0.3125, 0, 0);  // -0.375 + 0.0625 = -0.3125
                case "negX" -> stack.translate(0.3125, 0, 0);   // 0.375 - 0.0625 = 0.3125
                case "posZ" -> stack.translate(0, 0, -0.3125);
                case "negZ" -> stack.translate(0, 0, 0.3125);
            }
            HBMResourceManager.foundry_channel.renderPart(stack, buffer, "Arm_" + dirName + "_closed", TEXTURE, light, overlay);
            stack.popPose();
        }
    }

    private void drawLavaFace(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                              double minX, double maxX, double minZ, double maxZ, float y,
                              float r, float g, float b, int overlay, int light) {
        consumer.vertex(matrix, (float) minX, y, (float) minZ).color(r, g, b, 1.0f).uv(0, 0).overlayCoords(overlay).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(matrix, (float) minX, y, (float) maxZ).color(r, g, b, 1.0f).uv(0, 1).overlayCoords(overlay).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(matrix, (float) maxX, y, (float) maxZ).color(r, g, b, 1.0f).uv(1, 1).overlayCoords(overlay).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(matrix, (float) maxX, y, (float) minZ).color(r, g, b, 1.0f).uv(1, 0).overlayCoords(overlay).uv2(light).normal(normal, 0, 1, 0).endVertex();
    }

    private boolean canConnectTo(Level level, BlockPos pos, Direction dir) {
        BlockPos neighborPos = pos.relative(dir);
        Block block = level.getBlockState(neighborPos).getBlock();
        return block instanceof FoundryChannel || block instanceof FoundryOutlet;
    }
}