package com.hbm.render.block;

import com.hbm.main.HBMResourceManager;

import com.hbm.tileentity.network.TileEntityCableBaseNT;
import com.hbm.util.Library;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RedCableRenderer implements BlockEntityRenderer<TileEntityCableBaseNT> {

    public RedCableRenderer(BlockEntityRendererProvider.Context ignoredContext) {
    }

    public void render(TileEntityCableBaseNT tileEntity, float partialTicks, @NotNull PoseStack poseStack,
                              @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Level level = tileEntity.getLevel();
        BlockPos pos = tileEntity.getBlockPos();

        ResourceLocation texture = HBMResourceManager.red_cable_tex;

        boolean pX = Library.canConnect(Objects.requireNonNull(level), pos.relative(Direction.EAST), Direction.EAST);
        boolean nX = Library.canConnect(level, pos.relative(Direction.WEST), Direction.WEST);
        boolean pY = Library.canConnect(level, pos.relative(Direction.UP), Direction.UP);
        boolean nY = Library.canConnect(level, pos.relative(Direction.DOWN), Direction.DOWN);
        boolean nZ = Library.canConnect(level, pos.relative(Direction.SOUTH), Direction.SOUTH);
        boolean pZ = Library.canConnect(level, pos.relative(Direction.NORTH), Direction.NORTH);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        if (pX && nX && !pY && !nY && !pZ && !nZ) {
            HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "CX", texture, packedLight, packedOverlay);
        } else if (!pX && !nX && pY && nY && !pZ && !nZ) {
            HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "CY", texture, packedLight, packedOverlay);
        } else if (!pX && !nX && !pY && !nY && pZ && nZ) {
            HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "CZ", texture, packedLight, packedOverlay);
        } else {
            HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "Core", texture, packedLight, packedOverlay);
            if (pX) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posX", texture, packedLight, packedOverlay);
            if (nX) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negX", texture, packedLight, packedOverlay);
            if (pY) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posY", texture, packedLight, packedOverlay);
            if (nY) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negY", texture, packedLight, packedOverlay);
            if (pZ) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posZ", texture, packedLight, packedOverlay);
            if (nZ) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negZ", texture, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}