package com.hbm.render.item;

import com.hbm.items.block.ItemBlockSellafield;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class SellafieldItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEX = ResLocation(RefStrings.MODID, "textures/block/sellafield_0_0.png");

    public SellafieldItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int light, int overlay) {

        poseStack.pushPose();
        switch (context) {
            case GUI -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
            case FIXED -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
            }
            default -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
            }
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        drawCube(poseStack, consumer, getTexture(stack), light, overlay);
        poseStack.popPose();
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

    private ResourceLocation getTexture(ItemStack stack) {
        int level = ItemBlockSellafield.getLevel(stack);
        return ResLocation(RefStrings.MODID,
                "block/sellafield_" + level);
    }
}