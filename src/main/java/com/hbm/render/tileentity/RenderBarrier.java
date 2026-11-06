package com.hbm.render.tileentity;

import com.hbm.blocks.generic.BlockBarrier;
import com.hbm.tileentity.deco.TileEntityBarrier;
import com.hbm.util.RefStrings;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderBarrier implements BlockEntityRenderer<TileEntityBarrier> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/block/wood_barrier.png");

    public RenderBarrier(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityBarrier tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        Level level = tile.getLevel();
        if (level == null) return;

        BlockPos pos = tile.getBlockPos();
        BlockState state = tile.getBlockState();
        Direction facing = state.getValue(BlockBarrier.FACING);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));

        // Позиция уже локализована, так что все боксы — от 0..1 в локальных координатах

        // Смотрим соседей
        BlockPos pNegX = pos.offset(-1, 0, 0);
        BlockPos pPosX = pos.offset(1, 0, 0);
        BlockPos pNegZ = pos.offset(0, 0, -1);
        BlockPos pPosZ = pos.offset(0, 0, 1);
        BlockPos pPosY = pos.offset(0, 1, 0);

        boolean negX = isSolid(level, pNegX) || facing == Direction.WEST;
        boolean negZ = isSolid(level, pNegZ) || facing == Direction.NORTH;
        boolean posX = isSolid(level, pPosX) || facing == Direction.EAST;
        boolean posZ = isSolid(level, pPosZ) || facing == Direction.SOUTH;
        boolean posY = isSolid(level, pPosY);

        // negX — планка у -X: 0..0.125 по X, 0.4375..0.5625 по Z
        if (negX) {
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0F, 0F, 0.4375F, 0.125F, 1F, 0.5625F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0F, 0.0625F, negZ ? 0.125F : 0F, 0.0625F, 0.4375F, posZ ? 0.875F : 1F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0F, 0.5625F, negZ ? 0.125F : 0F, 0.0625F, 0.9375F, posZ ? 0.875F : 1F);
        }
        // negZ — планка у -Z
        if (negZ) {
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0.4375F, 0F, 0F, 0.5625F, 1F, 0.125F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, negX ? 0.125F : 0F, 0.0625F, 0F, posX ? 0.875F : 1F, 0.4375F, 0.0625F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, negX ? 0.125F : 0F, 0.5625F, 0F, posX ? 0.875F : 1F, 0.9375F, 0.0625F);
        }
        // posX — планка у +X
        if (posX) {
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0.875F, 0F, 0.4375F, 1F, 1F, 0.5625F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0.9375F, 0.0625F, negZ ? 0.125F : 0F, 1F, 0.4375F, posZ ? 0.875F : 1F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0.9375F, 0.5625F, negZ ? 0.125F : 0F, 1F, 0.9375F, posZ ? 0.875F : 1F);
        }
        // posZ — планка у +Z
        if (posZ) {
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0.4375F, 0F, 0.875F, 0.5625F, 1F, 1F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, negX ? 0.125F : 0F, 0.0625F, 0.9375F, posX ? 0.875F : 1F, 0.4375F, 1F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, negX ? 0.125F : 0F, 0.5625F, 0.9375F, posX ? 0.875F : 1F, 0.9375F, 1F);
        }
        // posY — верхняя крышка
        if (posY) {
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0F, 0.875F, 0F, 0.125F, 0.9375F, 1F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0.875F, 0.875F, 0F, 1F, 0.9375F, 1F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0F, 0.9375F, 0.0625F, 1F, 1F, 0.4375F);
            drawBox(poseStack, consumer, packedLight, packedOverlay, 0F, 0.9375F, 0.5625F, 1F, 1F, 0.9375F);
        }
    }

    /** Проверка «solid» — как в 1.7.10 `isOpaqueCube() || isNormalCube()`. */
    private boolean isSolid(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isFaceSturdy(level, pos, Direction.UP) || state.canOcclude();
    }

    /**
     * Рисует кубоид с растяжкой всей текстуры (UV 0..1) на каждую грань — как 1.7.10
     * `renderStandardBlock`.
     */
    public static void drawBox(PoseStack poseStack, VertexConsumer consumer,
                               int light, int overlay,
                               float x1, float y1, float z1, float x2, float y2, float z2) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f m = pose.pose();
        Matrix3f n = pose.normal();

        // -Y (низ) — нормаль (0, -1, 0)
        quad(consumer, m, n, light, overlay,
                x1, y1, z1, 0, 1,
                x2, y1, z1, 1, 1,
                x2, y1, z2, 1, 0,
                x1, y1, z2, 0, 0,
                0, -1, 0);

        // +Y (верх) — нормаль (0, 1, 0)
        quad(consumer, m, n, light, overlay,
                x1, y2, z1, 0, 0,
                x1, y2, z2, 0, 1,
                x2, y2, z2, 1, 1,
                x2, y2, z1, 1, 0,
                0, 1, 0);
        // -Z (север) — нормаль (0, 0, -1)
        quad(consumer, m, n, light, overlay,
                x2, y1, z1, 1, 1,
                x1, y1, z1, 0, 1,
                x1, y2, z1, 0, 0,
                x2, y2, z1, 1, 0,
                0, 0, -1);
        // +Z (юг) — нормаль (0, 0, 1)
        quad(consumer, m, n, light, overlay,
                x1, y1, z2, 0, 1,
                x2, y1, z2, 1, 1,
                x2, y2, z2, 1, 0,
                x1, y2, z2, 0, 0,
                0, 0, 1);
        // -X (запад) — нормаль (-1, 0, 0)
        quad(consumer, m, n, light, overlay,
                x1, y1, z1, 0, 1,
                x1, y1, z2, 1, 1,
                x1, y2, z2, 1, 0,
                x1, y2, z1, 0, 0,
                -1, 0, 0);
        // +X (восток) — нормаль (1, 0, 0)
        quad(consumer, m, n, light, overlay,
                x2, y1, z2, 0, 1,
                x2, y1, z1, 1, 1,
                x2, y2, z1, 1, 0,
                x2, y2, z2, 0, 0,
                1, 0, 0);
    }

    private static void quad(VertexConsumer consumer, Matrix4f m, Matrix3f n,
                             int light, int overlay,
                             float x1, float y1, float z1, float u1, float v1,
                             float x2, float y2, float z2, float u2, float v2,
                             float x3, float y3, float z3, float u3, float v3,
                             float x4, float y4, float z4, float u4, float v4,
                             float nx, float ny, float nz) {
        consumer.vertex(m, x1, y1, z1).color(1F, 1F, 1F, 1F).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
        consumer.vertex(m, x2, y2, z2).color(1F, 1F, 1F, 1F).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
        consumer.vertex(m, x3, y3, z3).color(1F, 1F, 1F, 1F).uv(u3, v3).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
        consumer.vertex(m, x4, y4, z4).color(1F, 1F, 1F, 1F).uv(u4, v4).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityBarrier tile) {
        return false;
    }
}