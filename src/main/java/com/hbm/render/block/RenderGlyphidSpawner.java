package com.hbm.render.block;

import com.hbm.tileentity.block.TileEntityGlyphidSpawner;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderGlyphidSpawner implements BlockEntityRenderer<TileEntityGlyphidSpawner> {

    private static final ResourceLocation[] TEX = {
            ResLocation(RefStrings.MODID, "textures/block/glyphid_eggs.png"),
            ResLocation(RefStrings.MODID, "textures/block/glyphid_eggs_infested.png"),
            ResLocation(RefStrings.MODID, "textures/block/glyphid_eggs_rad.png")
    };

    public RenderGlyphidSpawner(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityGlyphidSpawner te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ResourceLocation tex = TEX[Math.floorMod(te.type, TEX.length)];
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(tex));

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        drawCube(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void drawCube(PoseStack poseStack, VertexConsumer c, int light, int overlay) {
        Matrix4f m = poseStack.last().pose();
        float a = -0.5f, b = 0.5f;
        // X-
        v(m, c, a, a, a, 0, 1, -1, 0, 0, light, overlay);
        v(m, c, a, a, b, 1, 1, -1, 0, 0, light, overlay);
        v(m, c, a, b, b, 1, 0, -1, 0, 0, light, overlay);
        v(m, c, a, b, a, 0, 0, -1, 0, 0, light, overlay);
        // X+
        v(m, c, b, a, a, 0, 1, 1, 0, 0, light, overlay);
        v(m, c, b, b, a, 0, 0, 1, 0, 0, light, overlay);
        v(m, c, b, b, b, 1, 0, 1, 0, 0, light, overlay);
        v(m, c, b, a, b, 1, 1, 1, 0, 0, light, overlay);
        // Y-
        v(m, c, b, a, a, 0, 1, 0, -1, 0, light, overlay);
        v(m, c, b, a, b, 1, 1, 0, -1, 0, light, overlay);
        v(m, c, a, a, b, 1, 0, 0, -1, 0, light, overlay);
        v(m, c, a, a, a, 0, 0, 0, -1, 0, light, overlay);
        // Y+
        v(m, c, a, b, a, 0, 1, 0, 1, 0, light, overlay);
        v(m, c, a, b, b, 1, 1, 0, 1, 0, light, overlay);
        v(m, c, b, b, b, 1, 0, 0, 1, 0, light, overlay);
        v(m, c, b, b, a, 0, 0, 0, 1, 0, light, overlay);
        // Z-
        v(m, c, a, a, a, 0, 1, 0, 0, -1, light, overlay);
        v(m, c, a, b, a, 0, 0, 0, 0, -1, light, overlay);
        v(m, c, b, b, a, 1, 0, 0, 0, -1, light, overlay);
        v(m, c, b, a, a, 1, 1, 0, 0, -1, light, overlay);
        // Z+
        v(m, c, b, a, b, 0, 1, 0, 0, 1, light, overlay);
        v(m, c, b, b, b, 0, 0, 0, 0, 1, light, overlay);
        v(m, c, a, b, b, 1, 0, 0, 0, 1, light, overlay);
        v(m, c, a, a, b, 1, 1, 0, 0, 1, light, overlay);
    }

    private void v(Matrix4f m, VertexConsumer c, float x, float y, float z,
                   float u, float v, float nx, float ny, float nz, int light, int overlay) {
        c.vertex(m, x, y, z).color(255, 255, 255, 255).uv(u, v)
                .overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityGlyphidSpawner te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}