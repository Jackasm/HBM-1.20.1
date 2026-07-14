package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineCrucible;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityCrucible;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderCrucible implements BlockEntityRenderer<TileEntityCrucible> {

    private static final ResourceLocation TEXTURE = ResLocation("hbm", "textures/block/machines/machine_crucible.png");
    private static final ResourceLocation LAVA = ResLocation("hbm", "textures/block/network/lava.png");

    public RenderCrucible(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(TileEntityCrucible entity, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int light, int overlay) {
        stack.pushPose();

        stack.translate(0.5, 0, 0.5);

        Direction facing = entity.getBlockState().getValue(MachineCrucible.FACING);

        switch (facing) {
            case NORTH -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
            case SOUTH -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270));
            case EAST -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(0));
            case WEST -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
        }

        // Crucible body
        HBMResourceManager.machine_crucible.renderAll(stack, buffer, TEXTURE, light, overlay);

        // Molten material surface
        if (!entity.recipeStack.isEmpty() || !entity.wasteStack.isEmpty()) {
            int totalCap = TileEntityCrucible.recipeZCapacity + TileEntityCrucible.wasteZCapacity;
            int totalMass = 0;

            for (MaterialStack s : entity.recipeStack) totalMass += s.amount;
            for (MaterialStack s : entity.wasteStack) totalMass += s.amount;

            double level = ((double) totalMass / (double) totalCap) * 0.875;

            stack.pushPose();
            VertexConsumer lavaConsumer = buffer.getBuffer(RenderType.entitySolid(LAVA));

            Matrix4f matrix = stack.last().pose();
            Matrix3f normal = stack.last().normal();

            float y = (float) (0.5 + level);
            int fullBright = 0xF000F0;
            lavaConsumer.vertex(matrix, -1, y, -1).color(1, 1, 1, 1.0f).uv(0, 0).overlayCoords(overlay).uv2(fullBright).normal(normal, 0, 1, 0).endVertex();
            lavaConsumer.vertex(matrix, -1, y,  1).color(1, 1, 1, 1.0f).uv(0, 1).overlayCoords(overlay).uv2(fullBright).normal(normal, 0, 1, 0).endVertex();
            lavaConsumer.vertex(matrix,  1, y,  1).color(1, 1, 1, 1.0f).uv(1, 1).overlayCoords(overlay).uv2(fullBright).normal(normal, 0, 1, 0).endVertex();
            lavaConsumer.vertex(matrix,  1, y, -1).color(1, 1, 1, 1.0f).uv(1, 0).overlayCoords(overlay).uv2(fullBright).normal(normal, 0, 1, 0).endVertex();

            stack.popPose();
        }

        stack.popPose();
    }
}