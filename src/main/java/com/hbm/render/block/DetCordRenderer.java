package com.hbm.render.block;

import com.hbm.blocks.bomb.IDetConnectible;
import com.hbm.main.HBMResourceManager;

import com.hbm.tileentity.block.TileEntityDetCord;
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
public class DetCordRenderer implements BlockEntityRenderer<TileEntityDetCord> {

    public DetCordRenderer(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityDetCord tileEntity, float partialTicks, @NotNull PoseStack poseStack,
                                   @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Level level = tileEntity.getLevel();
        BlockPos pos = tileEntity.getBlockPos();


        ResourceLocation icon = HBMResourceManager.det_cord_tex;

        boolean pX = IDetConnectible.isConnectible(Objects.requireNonNull(level), pos.relative(Direction.EAST), Direction.WEST);
        boolean nX = IDetConnectible.isConnectible(level, pos.relative(Direction.WEST), Direction.EAST);
        boolean pY = IDetConnectible.isConnectible(level, pos.relative(Direction.UP), Direction.DOWN);
        boolean nY = IDetConnectible.isConnectible(level, pos.relative(Direction.DOWN), Direction.UP);
        boolean pZ = IDetConnectible.isConnectible(level, pos.relative(Direction.SOUTH), Direction.NORTH);
        boolean nZ = IDetConnectible.isConnectible(level, pos.relative(Direction.NORTH), Direction.SOUTH);

        int mask = (pX ? 32 : 0) + (nX ? 16 : 0) + (pY ? 8 : 0) + (nY ? 4 : 0) + (pZ ? 2 : 0) + (nZ ? 1 : 0);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        if (mask == 0b110000 || mask == 0b100000 || mask == 0b010000) {
            HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "CX", icon, packedLight, packedOverlay);
        } else if (mask == 0b001100 || mask == 0b001000 || mask == 0b000100) {
            HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "CY", icon, packedLight, packedOverlay);
        } else if (mask == 0b000011 || mask == 0b000010 || mask == 0b000001) {
            HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "CZ", icon, packedLight, packedOverlay);
        } else {
            HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "Core", icon, packedLight, packedOverlay);
            if (pX) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posX", icon, packedLight, packedOverlay);
            if (nX) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negX", icon, packedLight, packedOverlay);
            if (pY) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posY", icon, packedLight, packedOverlay);
            if (nY) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negY", icon, packedLight, packedOverlay);
            if (nZ) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posZ", icon, packedLight, packedOverlay);
            if (pZ) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negZ", icon, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

}