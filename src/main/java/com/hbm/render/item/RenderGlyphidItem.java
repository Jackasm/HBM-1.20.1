package com.hbm.render.item;

import com.hbm.blocks.generic.BlockGlyphid.GlyphidType;
import com.hbm.items.block.ItemGlyphid;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderGlyphidItem extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEX_STANDARD = ResLocation(RefStrings.MODID, "textures/block/glyphid_base.png");
    private static final ResourceLocation TEX_INFESTED = ResLocation(RefStrings.MODID, "textures/block/glyphid_base_infested.png");
    private static final ResourceLocation TEX_RAD      = ResLocation(RefStrings.MODID, "textures/block/glyphid_base_rad.png");

    public RenderGlyphidItem() {
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
                poseStack.mulPose(Axis.XP.rotationDegrees(35));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIXED -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND,
                 THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            default -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(getTexture(stack)));
        drawCube(poseStack, consumer, light, overlay);
        poseStack.popPose();
    }

    private ResourceLocation getTexture(ItemStack stack) {
        GlyphidType type = ItemGlyphid.getType(stack);
        return switch (type) {
            case INFESTED -> TEX_INFESTED;
            case RAD      -> TEX_RAD;
            default       -> TEX_STANDARD;
        };
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
}