package com.hbm.render.block;

import com.hbm.blocks.generic.BlockSellafield;
import com.hbm.blocks.generic.BlockSellafield.TileEntitySellafield;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderSellafield implements BlockEntityRenderer<TileEntitySellafield> {

    public RenderSellafield(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntitySellafield te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = te.getBlockState();
        int level = state.getValue(BlockSellafield.LEVEL);
        int variant = getVariant(te.getBlockPos());

        ResourceLocation tex = ResLocation(RefStrings.MODID,
                "block/sellafield_" + level);

        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        drawCube(poseStack, consumer, tex, packedLight, packedOverlay);
        poseStack.popPose();
    }

    /** Тот же хэш, что в оригинале 1.7.10. */
    private static int getVariant(BlockPos pos) {
        long l = (long) (pos.getX() * 3129871) ^ (long) pos.getY() * 116129781L ^ (long) pos.getZ();
        l = l * l * 42317861L + l * 11L;
        return (int) Math.abs((l >> 16) & 3L);
    }

    private void drawCube(PoseStack poseStack, VertexConsumer c, ResourceLocation tex,
                          int light, int overlay) {

        // ПРАВИЛЬНО: берём UV из спрайта атласа
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(tex);
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        Matrix4f m = poseStack.last().pose();
        float a = -0.5f, b = 0.5f;

        // X- (west)
        v(m, c, a, a, a, u0, v1, -1, 0, 0, light, overlay);
        v(m, c, a, a, b, u1, v1, -1, 0, 0, light, overlay);
        v(m, c, a, b, b, u1, v0, -1, 0, 0, light, overlay);
        v(m, c, a, b, a, u0, v0, -1, 0, 0, light, overlay);
        // X+ (east)
        v(m, c, b, a, a, u0, v1, 1, 0, 0, light, overlay);
        v(m, c, b, b, a, u0, v0, 1, 0, 0, light, overlay);
        v(m, c, b, b, b, u1, v0, 1, 0, 0, light, overlay);
        v(m, c, b, a, b, u1, v1, 1, 0, 0, light, overlay);
        // Y- (down)
        v(m, c, b, a, a, u0, v1, 0, -1, 0, light, overlay);
        v(m, c, b, a, b, u1, v1, 0, -1, 0, light, overlay);
        v(m, c, a, a, b, u1, v0, 0, -1, 0, light, overlay);
        v(m, c, a, a, a, u0, v0, 0, -1, 0, light, overlay);
        // Y+ (up)
        v(m, c, a, b, a, u0, v1, 0, 1, 0, light, overlay);
        v(m, c, a, b, b, u1, v1, 0, 1, 0, light, overlay);
        v(m, c, b, b, b, u1, v0, 0, 1, 0, light, overlay);
        v(m, c, b, b, a, u0, v0, 0, 1, 0, light, overlay);
        // Z- (north)
        v(m, c, a, a, a, u0, v1, 0, 0, -1, light, overlay);
        v(m, c, a, b, a, u0, v0, 0, 0, -1, light, overlay);
        v(m, c, b, b, a, u1, v0, 0, 0, -1, light, overlay);
        v(m, c, b, a, a, u1, v1, 0, 0, -1, light, overlay);
        // Z+ (south)
        v(m, c, b, a, b, u0, v1, 0, 0, 1, light, overlay);
        v(m, c, b, b, b, u0, v0, 0, 0, 1, light, overlay);
        v(m, c, a, b, b, u1, v0, 0, 0, 1, light, overlay);
        v(m, c, a, a, b, u1, v1, 0, 0, 1, light, overlay);
    }

    private void v(Matrix4f m, VertexConsumer c, float x, float y, float z,
                   float u, float v, float nx, float ny, float nz, int light, int overlay) {
        c.vertex(m, x, y, z).color(255, 255, 255, 255).uv(u, v)
                .overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}