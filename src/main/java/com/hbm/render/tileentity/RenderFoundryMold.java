package com.hbm.render.tileentity;

import com.hbm.inventory.material.NTMMaterial;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.network.TileEntityFoundryMold;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderFoundryMold implements BlockEntityRenderer<TileEntityFoundryMold> {

    private static final ResourceLocation TEXTURE = ResLocation("hbm", "textures/block/network/foundry_mold.png");
    private static final ResourceLocation LAVA = ResLocation("hbm", "textures/block/network/lava.png");

    public RenderFoundryMold(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(TileEntityFoundryMold entity, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int light, int overlay) {
        stack.pushPose();
        stack.translate(0.5, 0, 0.5);

        // Модель формы
        HBMResourceManager.foundry_mold.renderAll(stack, buffer, TEXTURE, light, overlay);

        // Установленный молд
        ItemStack moldStack = entity.inventory.getStackInSlot(0);
        if (!moldStack.isEmpty()) {
            stack.pushPose();
            stack.translate(0, entity.moldHeight() + 0.05, 0);
            stack.scale(0.7f, 0.7f, 0.7f);
            stack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    moldStack, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
            stack.popPose();
        }

        // Готовый предмет на выходе
        ItemStack outputStack = entity.inventory.getStackInSlot(1);
        if (!outputStack.isEmpty()) {
            stack.pushPose();
            double scale = 0.875 - 0.125; // внутренний размер Mold
            stack.translate(0, entity.moldHeight() + 0.15, 0);
            stack.scale((float) scale, (float) scale, (float) scale);
            stack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    outputStack, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
            stack.popPose();
        }

        // Расплавленный металл
        if (entity.shouldRender()) {
            stack.pushPose();
            NTMMaterial mat = entity.getMat();
            float y = (float) entity.getFillLevel();
            int color = mat.moltenColor;
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;

            VertexConsumer lava = buffer.getBuffer(RenderType.entitySolid(LAVA));
            Matrix4f matrix = stack.last().pose();
            Matrix3f normal = stack.last().normal();
            int fullBright = 0xF000F0;

            float minX = (float) entity.minX() - 0.5f;
            float maxX = (float) entity.maxX() - 0.5f;
            float minZ = (float) entity.minZ() - 0.5f;
            float maxZ = (float) entity.maxZ() - 0.5f;

            lava.vertex(matrix, minX, y, minZ).color(r, g, b, 1).uv(0, 0).overlayCoords(overlay).uv2(fullBright).normal(normal, 0, 1, 0).endVertex();
            lava.vertex(matrix, minX, y, maxZ).color(r, g, b, 1).uv(0, 1).overlayCoords(overlay).uv2(fullBright).normal(normal, 0, 1, 0).endVertex();
            lava.vertex(matrix, maxX, y, maxZ).color(r, g, b, 1).uv(1, 1).overlayCoords(overlay).uv2(fullBright).normal(normal, 0, 1, 0).endVertex();
            lava.vertex(matrix, maxX, y, minZ).color(r, g, b, 1).uv(1, 0).overlayCoords(overlay).uv2(fullBright).normal(normal, 0, 1, 0).endVertex();
            stack.popPose();
        }

        stack.popPose();
    }
}