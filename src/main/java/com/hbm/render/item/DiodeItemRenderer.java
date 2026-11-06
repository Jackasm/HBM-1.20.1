package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
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

@OnlyIn(Dist.CLIENT)
public class DiodeItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation PAD_TEX =
            new ResourceLocation(RefStrings.MODID, "block/hadron_coil_alloy");
    private static final ResourceLocation CABLE_TEX =
            new ResourceLocation(RefStrings.MODID, "textures/block/red_cable.png");
    private static final ResourceLocation BLOCK_TEX =
            new ResourceLocation(RefStrings.MODID, "block/cable_diode");

    public DiodeItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        TextureAtlasSprite padSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(PAD_TEX);
        TextureAtlasSprite blockSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(BLOCK_TEX);

        poseStack.pushPose();
        applyTransform(context, poseStack);

        // Трансформация как в оригинале: rotate 90° вокруг Y, сдвиг, потом translate 0.5, 0.5, 0.5
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-0.5f, -0.625f, -0.5f);

        VertexConsumer solid = buffer.getBuffer(RenderType.cutout());

        // 1) Верхняя плита
        drawBox(poseStack, solid,
                -0.5f, 0.375f, -0.5f,
                0.5f, 0.5f, 0.5f,
                blockSprite, packedLight, packedOverlay);

        // 2) Pad
        drawBox(poseStack, solid,
                -0.375f, -0.375f, -0.375f,
                0.375f, 0.375f, 0.375f,
                padSprite, packedLight, packedOverlay);

        // 3) OBJ-части
        HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posX", CABLE_TEX, packedLight, packedOverlay);
        HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negX", CABLE_TEX, packedLight, packedOverlay);
        HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negY", CABLE_TEX, packedLight, packedOverlay);
        HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posZ", CABLE_TEX, packedLight, packedOverlay);
        HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negZ", CABLE_TEX, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void applyTransform(ItemDisplayContext context, PoseStack poseStack) {
        switch (context) {
            case GUI -> {
                poseStack.translate(0.5, 0.6, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(-135));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.35f, 0.35f, 0.35f);
            }
            case FIXED -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
            }
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.35f, 0.35f, 0.35f);
            }
            default -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
        }
    }

    private void drawBox(PoseStack poseStack, VertexConsumer c,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         TextureAtlasSprite sprite,
                         int light, int overlay) {
        Matrix4f m = poseStack.last().pose();
        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();

        // X-
        v(m, c, x1, y1, z1, u0, v1, -1, 0, 0, light, overlay);
        v(m, c, x1, y1, z2, u1, v1, -1, 0, 0, light, overlay);
        v(m, c, x1, y2, z2, u1, v0, -1, 0, 0, light, overlay);
        v(m, c, x1, y2, z1, u0, v0, -1, 0, 0, light, overlay);
        // X+
        v(m, c, x2, y1, z1, u0, v1, 1, 0, 0, light, overlay);
        v(m, c, x2, y2, z1, u0, v0, 1, 0, 0, light, overlay);
        v(m, c, x2, y2, z2, u1, v0, 1, 0, 0, light, overlay);
        v(m, c, x2, y1, z2, u1, v1, 1, 0, 0, light, overlay);
        // Y-
        v(m, c, x2, y1, z1, u0, v1, 0, -1, 0, light, overlay);
        v(m, c, x2, y1, z2, u1, v1, 0, -1, 0, light, overlay);
        v(m, c, x1, y1, z2, u1, v0, 0, -1, 0, light, overlay);
        v(m, c, x1, y1, z1, u0, v0, 0, -1, 0, light, overlay);
        // Y+
        v(m, c, x1, y2, z1, u0, v1, 0, 1, 0, light, overlay);
        v(m, c, x1, y2, z2, u1, v1, 0, 1, 0, light, overlay);
        v(m, c, x2, y2, z2, u1, v0, 0, 1, 0, light, overlay);
        v(m, c, x2, y2, z1, u0, v0, 0, 1, 0, light, overlay);
        // Z-
        v(m, c, x1, y1, z1, u0, v1, 0, 0, -1, light, overlay);
        v(m, c, x1, y2, z1, u0, v0, 0, 0, -1, light, overlay);
        v(m, c, x2, y2, z1, u1, v0, 0, 0, -1, light, overlay);
        v(m, c, x2, y1, z1, u1, v1, 0, 0, -1, light, overlay);
        // Z+
        v(m, c, x2, y1, z2, u0, v1, 0, 0, 1, light, overlay);
        v(m, c, x2, y2, z2, u0, v0, 0, 0, 1, light, overlay);
        v(m, c, x1, y2, z2, u1, v0, 0, 0, 1, light, overlay);
        v(m, c, x1, y1, z2, u1, v1, 0, 0, 1, light, overlay);
    }

    private void v(Matrix4f m, VertexConsumer c, float x, float y, float z,
                   float u, float v, float nx, float ny, float nz, int light, int overlay) {
        c.vertex(m, x, y, z).color(255, 255, 255, 255).uv(u, v)
                .overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
    }
}