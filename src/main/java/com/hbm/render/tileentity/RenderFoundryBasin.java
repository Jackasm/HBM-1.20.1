package com.hbm.render.tileentity;

import com.hbm.inventory.material.NTMMaterial;
import com.hbm.items.machine.ItemMold;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.network.TileEntityFoundryBasin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderFoundryBasin implements BlockEntityRenderer<TileEntityFoundryBasin> {

    private static final ResourceLocation TEXTURE = ResLocation("hbm", "textures/block/network/foundry_basin.png");
    private static final ResourceLocation LAVA = ResLocation("hbm", "textures/block/network/lava_gray.png");

    public RenderFoundryBasin(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(TileEntityFoundryBasin entity, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int light, int overlay) {
        stack.pushPose();
        stack.translate(0.5, 0, 0.5);

        HBMResourceManager.foundry_basin.renderAll(stack, buffer, TEXTURE, light, overlay);

        // Установленный молд
        ItemStack moldStack = entity.inventory.getStackInSlot(0);
        if (!moldStack.isEmpty()) {
            renderMold(stack, buffer, moldStack, light, overlay);
        }

        // Готовый блок/предмет на выходе
        ItemStack outputStack = entity.inventory.getStackInSlot(1);
        if (!outputStack.isEmpty()) {
            ItemMold.MoldType moldType = entity.getInstalledMold();
            if (moldType != null) {
                renderOutput(stack, buffer, outputStack, moldType, light, overlay);
            }
        }

        // Расплав
        if (entity.shouldRender()) {
            renderMoltenMetal(stack, buffer, entity, overlay);
        }

        stack.popPose();
    }

    private void renderMold(PoseStack stack, MultiBufferSource buffer, ItemStack moldStack, int light, int overlay) {
        stack.pushPose();

        stack.translate(0, 0.9, 0);
        stack.scale(0.8f, 0.8f, 0.8f);

        stack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
        Minecraft.getInstance().getItemRenderer().renderStatic(
                moldStack, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
        stack.popPose();
    }

    private void renderOutput(PoseStack stack, MultiBufferSource buffer, ItemStack outputStack,
                              ItemMold.MoldType moldType, int light, int overlay) {

        boolean isBlock = outputStack.getItem() instanceof BlockItem;

        // Для блоков всегда один предмет
        if (isBlock) {
            stack.pushPose();
            stack.translate(0, 0.625, 0);
            stack.scale(1.1f, 1.1f, 1.1f);
            stack.mulPose(Axis.XP.rotationDegrees(90));
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    outputStack, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
            stack.popPose();
            return;
        }

        // Для предметов: если количество > 1, показываем 3 штуки
        if (moldType.amount > 1) {
            renderMultipleItems(stack, buffer, outputStack, light, overlay);
        } else {
            // Одиночный предмет
            stack.pushPose();
            stack.translate(0, 1.0, 0);
            stack.scale(0.7f, 0.7f, 0.7f);
            stack.mulPose(Axis.XP.rotationDegrees(90));
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    outputStack, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
            stack.popPose();
        }
    }

    private void renderMoltenMetal(PoseStack stack, MultiBufferSource buffer, TileEntityFoundryBasin entity, int overlay) {
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

    private void renderMultipleItems(PoseStack stack, MultiBufferSource buffer, ItemStack itemStack, int light, int overlay) {
        int method =1;           // 1 = россыпь, 2 = стопка, 3 = веер
        float baseScale = 0.3f;   // размер каждого предмета
        float posY = 1.0f;        // высота


        for (int i = 0; i < 3; i++) {
            stack.pushPose();
            stack.translate(0, posY, 0);
            applyTransform(stack, i, method);
            stack.scale(baseScale, baseScale, baseScale);
            stack.mulPose(Axis.XP.rotationDegrees(90));
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
            stack.popPose();
        }
    }

    private void applyTransform(PoseStack stack, int index, int method) {
        switch (method) {
            case 1: // Россыпь: хаотично разбросаны
                double xOffset = (index - 1) * 0.2;   // -0.2, 0, 0.2
                double zOffset = (index == 1) ? 0 : (index == 0 ? -0.15 : 0.15);
                stack.translate(xOffset, 0, zOffset);
                stack.mulPose(Axis.YP.rotationDegrees(index * 60));
                break;
            case 2: // Стопка: лежат аккуратной стопкой
                float yShift = index * 0.01f;
                float xShift = (index == 1) ? 0 : (index == 0 ? -0.1f : 0.1f);
                stack.translate(xShift, yShift, 0);
                stack.mulPose(Axis.YP.rotationDegrees(index * 30));
                break;
            case 3: // Веер: разложены веером
                stack.translate(0, -0.4, 0);
                float angle = (index - 1) * 35f; // -35, 0, 35
                stack.mulPose(Axis.YP.rotationDegrees(angle));
                stack.translate(0, 0.4, 0);
                double xFan = (index - 1) * 0.2;
                stack.translate(xFan, 0, 0);
                break;
            default:
                break;
        }
    }
}