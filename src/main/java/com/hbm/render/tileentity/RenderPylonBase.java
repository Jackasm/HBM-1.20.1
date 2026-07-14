package com.hbm.render.tileentity;

import com.hbm.config.ClientConfig;
import com.hbm.tileentity.network.TileEntityPylonBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

public abstract class RenderPylonBase<T extends TileEntityPylonBase> implements BlockEntityRenderer<T> {

    private static final ResourceLocation WIRE_TEX =
            ResLocation("hbm", "textures/block/network/wire.png");
    private static final ResourceLocation WIRE_GREYSCALE_TEX =
            ResLocation("hbm", "textures/block/network/wire_greyscale.png");

    public RenderPylonBase(BlockEntityRendererProvider.Context context) {}

    public void renderLinesGeneric(T entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
        ResourceLocation tex = entity.color == 0 ? WIRE_TEX : WIRE_GREYSCALE_TEX;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entitySolid(tex));

        Level level = entity.getLevel();
        if (level == null) return;

        entity.getConnected().forEach(wire -> {
            BlockPos wirePos = new BlockPos(wire[0], wire[1], wire[2]);
            BlockEntity tile = level.getBlockEntity(wirePos);

            if (tile instanceof TileEntityPylonBase pylon) {
                Vec3[] m1 = entity.getMountPos();
                Vec3[] m2 = pylon.getMountPos();

                int lineCount = Math.min(m1.length, m2.length);

                for (int line = 0; line < lineCount; line++) {
                    Vec3 first = m1[line % m1.length];
                    int secondIndex = line % m2.length;

                    Vec3 second = m2[secondIndex];

                    double sX = second.x + pylon.getBlockPos().getX() - entity.getBlockPos().getX();
                    double sY = second.y + pylon.getBlockPos().getY() - entity.getBlockPos().getY();
                    double sZ = second.z + pylon.getBlockPos().getZ() - entity.getBlockPos().getZ();

                    renderHalfLine(entity, stack, consumer, light,
                            first.x, first.y, first.z,
                            first.x + (sX - first.x) * 0.5,
                            first.y + (sY - first.y) * 0.5,
                            first.z + (sZ - first.z) * 0.5);
                }
            }
        });
    }

    private void renderHalfLine(T entity, PoseStack stack, VertexConsumer consumer, int light,
                                double x0, double y0, double z0,
                                double x1, double y1, double z1) {
        stack.pushPose();

        int color = entity.color == 0 ? 0xffffff : entity.color;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        if (!ClientConfig.RENDER_CABLE_HANG) {
            drawLineSegmentBillboard(consumer, stack, r, g, b, light,
                    x0, y0, z0, x1, y1, z1);
        } else {
            float count = 10;
            double deltaX = x1 - x0;
            double deltaY = y1 - y0;
            double deltaZ = z1 - z0;
            double hang = Math.min(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / 15D, 2.5D);

            for (float j = 0; j < count; j++) {
                float k = j + 1;
                double sagJ = Math.sin(j / count * Math.PI * 0.5) * hang;
                double sagK = Math.sin(k / count * Math.PI * 0.5) * hang;

                drawLineSegmentBillboard(consumer, stack, r, g, b, light,
                        x0 + (deltaX * j / count),
                        y0 + (deltaY * j / count) - sagJ,
                        z0 + (deltaZ * j / count),
                        x0 + (deltaX * k / count),
                        y0 + (deltaY * k / count) - sagK,
                        z0 + (deltaZ * k / count));
            }
        }

        stack.popPose();
    }

    // Новый метод для строгого билборда
    private void drawLineSegmentBillboard(VertexConsumer consumer, PoseStack stack,
                                          float r, float g, float b, int light,
                                          double x, double y, double z,
                                          double a, double bx, double c) {
        double girth = 0.03125D;
        double length = Math.sqrt((a - x) * (a - x) + (bx - y) * (bx - y) + (c - z) * (c - z));
        float u1 = 0;
        float u2 = (float) length * 8;

        Matrix4f matrix = stack.last().pose();
        Matrix3f normalMatrix = stack.last().normal();

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        double cx = (x + a) / 2.0;
        double cy = (y + bx) / 2.0;
        double cz = (z + c) / 2.0;

        double dx = a - x;
        double dy = bx - y;
        double dz = c - z;
        double wireLen = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (wireLen > 0) {
            dx /= wireLen;
            dy /= wireLen;
            dz /= wireLen;
        }

        double toCameraX = cameraPos.x - cx;
        double toCameraY = cameraPos.y - cy;
        double toCameraZ = cameraPos.z - cz;

        double rightX = dy * toCameraZ - dz * toCameraY;
        double rightY = dz * toCameraX - dx * toCameraZ;
        double rightZ = dx * toCameraY - dy * toCameraX;
        double rightLen = Math.sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ);
        if (rightLen > 0) {
            rightX /= rightLen;
            rightY /= rightLen;
            rightZ /= rightLen;
        } else {
            rightX = -dz;
            rightY = 0;
            rightZ = dx;
            rightLen = Math.sqrt(rightX * rightX + rightZ * rightZ);
            if (rightLen > 0) {
                rightX /= rightLen;
                rightZ /= rightLen;
            }
        }

        double upX = dy * rightZ - dz * rightY;
        double upY = dz * rightX - dx * rightZ;
        double upZ = dx * rightY - dy * rightX;

        double halfLen = wireLen / 2.0;

        float v1x = (float)(cx - rightX * girth - dx * halfLen);
        float v1y = (float)(cy - rightY * girth - dy * halfLen);
        float v1z = (float)(cz - rightZ * girth - dz * halfLen);

        float v2x = (float)(cx + rightX * girth - dx * halfLen);
        float v2y = (float)(cy + rightY * girth - dy * halfLen);
        float v2z = (float)(cz + rightZ * girth - dz * halfLen);

        float v3x = (float)(cx + rightX * girth + dx * halfLen);
        float v3y = (float)(cy + rightY * girth + dy * halfLen);
        float v3z = (float)(cz + rightZ * girth + dz * halfLen);

        float v4x = (float)(cx - rightX * girth + dx * halfLen);
        float v4y = (float)(cy - rightY * girth + dy * halfLen);
        float v4z = (float)(cz - rightZ * girth + dz * halfLen);

        // Нормаль билборда (к камере)
        float nx = (float) upX;
        float ny = (float) upY;
        float nz = (float) upZ;

        // Передняя грань
        consumer.vertex(matrix, v1x, v1y, v1z)
                .color(r, g, b, 1.0f).uv(u1, 0).overlayCoords(0).uv2(0xF000F0)
                .normal(normalMatrix, nx, ny, nz).endVertex();
        consumer.vertex(matrix, v2x, v2y, v2z)
                .color(r, g, b, 1.0f).uv(u1, 1).overlayCoords(0).uv2(0xF000F0)
                .normal(normalMatrix, nx, ny, nz).endVertex();
        consumer.vertex(matrix, v3x, v3y, v3z)
                .color(r, g, b, 1.0f).uv(u2, 1).overlayCoords(0).uv2(0xF000F0)
                .normal(normalMatrix, nx, ny, nz).endVertex();
        consumer.vertex(matrix, v4x, v4y, v4z)
                .color(r, g, b, 1.0f).uv(u2, 0).overlayCoords(0).uv2(0xF000F0)
                .normal(normalMatrix, nx, ny, nz).endVertex();

// Задняя грань
        consumer.vertex(matrix, v4x, v4y, v4z)
                .color(r, g, b, 1.0f).uv(u2, 0).overlayCoords(0).uv2(0xF000F0)
                .normal(normalMatrix, -nx, -ny, -nz).endVertex();
        consumer.vertex(matrix, v3x, v3y, v3z)
                .color(r, g, b, 1.0f).uv(u2, 1).overlayCoords(0).uv2(0xF000F0)
                .normal(normalMatrix, -nx, -ny, -nz).endVertex();
        consumer.vertex(matrix, v2x, v2y, v2z)
                .color(r, g, b, 1.0f).uv(u1, 1).overlayCoords(0).uv2(0xF000F0)
                .normal(normalMatrix, -nx, -ny, -nz).endVertex();
        consumer.vertex(matrix, v1x, v1y, v1z)
                .color(r, g, b, 1.0f).uv(u1, 0).overlayCoords(0).uv2(0xF000F0)
                .normal(normalMatrix, -nx, -ny, -nz).endVertex();
    }
}