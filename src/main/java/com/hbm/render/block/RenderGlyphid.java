package com.hbm.render.block;

import com.hbm.blocks.generic.BlockGlyphid;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderGlyphid implements BlockEntityRenderer<BlockGlyphid.TileEntityGlyphid> {

    private static final ResourceLocation[] STANDARD = {
            ResLocation(RefStrings.MODID, "textures/block/glyphid_base.png"),
            ResLocation(RefStrings.MODID, "textures/block/glyphid_base_alt.png")
    };
    private static final ResourceLocation[] INFESTED = {
            ResLocation(RefStrings.MODID, "textures/block/glyphid_base_infested.png"),
            ResLocation(RefStrings.MODID, "textures/block/glyphid_base_infested_alt.png")
    };
    private static final ResourceLocation[] RAD = {
            ResLocation(RefStrings.MODID, "textures/block/glyphid_base_rad.png"),
            ResLocation(RefStrings.MODID, "textures/block/glyphid_base_rad_alt.png")
    };

    public RenderGlyphid(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull BlockGlyphid.TileEntityGlyphid te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation[] variants = switch (te.type) {
            case INFESTED -> INFESTED;
            case RAD      -> RAD;
            case STANDARD -> STANDARD;
        };

        BlockPos pos = te.getBlockPos();
        long l = (long) (pos.getX() * 3129871) ^ (long) pos.getY() * 116129781L ^ (long) pos.getZ();
        l = l * l * 42317861L + l * 11L;
        int i = (int) (l >> 16 & 3L);
        ResourceLocation tex = variants[Math.abs(i) % variants.length];

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
    public boolean shouldRenderOffScreen(@NotNull BlockGlyphid.TileEntityGlyphid te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}