package com.hbm.render.tileentity;

import com.hbm.blocks.network.FoundryOutlet;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.network.TileEntityFoundryOutlet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderFoundryOutlet implements BlockEntityRenderer<TileEntityFoundryOutlet> {

    private static final ResourceLocation TEXTURE_OUTLET = ResLocation("hbm", "textures/block/network/foundry_outlet.png");
    private static final ResourceLocation TEXTURE_SLAGTAP = ResLocation("hbm", "textures/block/network/foundry_slagtap.png");

    public RenderFoundryOutlet(BlockEntityRendererProvider.Context ignoredContext) {}

    private ResourceLocation getTexture(TileEntityFoundryOutlet entity) {
        if (entity.getBlockState().getBlock() instanceof com.hbm.blocks.network.FoundrySlagtap) {
            return TEXTURE_SLAGTAP;
        }
        return TEXTURE_OUTLET;
    }

    @Override
    public void render(TileEntityFoundryOutlet entity, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int light, int overlay) {
        Direction facing = entity.getBlockState().getValue(FoundryOutlet.FACING);
        boolean hasFilter = entity.filter != null;
        boolean isClosed = entity.isClosed();

        stack.pushPose();
        stack.translate(0.5, 0, 0.5);

        switch (facing) {
            case EAST -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
            case WEST -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270));
            case SOUTH -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(0));
            case NORTH -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
        }

        ResourceLocation tex = getTexture(entity);

        HBMResourceManager.foundry_outlet.renderPart(stack, buffer, "Bottom", tex, light, overlay);
        HBMResourceManager.foundry_outlet.renderPart(stack, buffer, "Wall_XPos_Front", tex, light, overlay);
        HBMResourceManager.foundry_outlet.renderPart(stack, buffer, "Wall_XNeg_Inner", tex, light, overlay);

        if (hasFilter) {
            HBMResourceManager.foundry_outlet.renderPart(stack, buffer, "Filter", tex, light, overlay);
        }
        if (isClosed) {
            HBMResourceManager.foundry_outlet.renderPart(stack, buffer, "Lock", tex, light, overlay);
        }

        stack.popPose();
    }
}