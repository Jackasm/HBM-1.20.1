package com.hbm.render.tileentity;

import com.hbm.blocks.network.ConnectorRedWire;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.network.TileEntityConnector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderConnector extends RenderPylonBase<TileEntityConnector> {

    private static final ResourceLocation CONNECTOR_TEX =
            ResLocation("hbm", "textures/block/network/red_connector.png");

    public RenderConnector(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TileEntityConnector entity, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int light, int overlay) {
        stack.pushPose();

        stack.translate(0.5, 0.5, 0.5);

        Direction facing = entity.getBlockState().getValue(ConnectorRedWire.FACING);

        switch (facing) {
            case DOWN:
                stack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180));
                break;
            case UP:
                break;
            case NORTH:
                stack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
                stack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
                break;
            case SOUTH:
                stack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
                break;
            case WEST:
                stack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
                stack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90));
                break;
            case EAST:
                stack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
                stack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(270));
                break;
        }

        stack.translate(0, -0.5, 0);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entitySolid(CONNECTOR_TEX));
        HBMResourceManager.connector.renderAll(stack, vertexConsumer, light, overlay);

        stack.popPose();

        // Рендер линий соединения
        renderLinesGeneric(entity, partialTicks, stack, buffer, light, overlay);
    }
}