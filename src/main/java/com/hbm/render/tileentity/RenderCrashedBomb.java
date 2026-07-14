package com.hbm.render.tileentity;

import com.hbm.blocks.bomb.BlockCrashedBomb;
import com.hbm.blocks.bomb.BlockCrashedBomb.EnumDudType;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.bomb.TileEntityCrashedBomb;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RenderCrashedBomb implements BlockEntityRenderer<TileEntityCrashedBomb> {

    private static final Random rand = new Random();

    public RenderCrashedBomb(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull TileEntityCrashedBomb tileEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5D, 0, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(0, 0, -0.5);

        // Используем позицию блока для seed рандома
        rand.setSeed(tileEntity.getBlockPos().hashCode());
        double yaw = rand.nextDouble() * 360;
        double pitch = rand.nextDouble() * 45 + 45;
        double roll = rand.nextDouble() * 360;
        double offset = rand.nextDouble() * 2 - 1;

        poseStack.mulPose(Axis.YP.rotationDegrees((float) yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) roll));
        poseStack.translate(0, 0, -offset);

        EnumDudType type = tileEntity.getBlockState().getValue(BlockCrashedBomb.TYPE);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTexture(type)));

        if (type == EnumDudType.BALEFIRE) {
            HBMResourceManager.dud_balefire.renderAll(poseStack, consumer, packedLight, packedOverlay);
        } else if (type == EnumDudType.CONVENTIONAL) {
            HBMResourceManager.dud_conventional.renderAll(poseStack, consumer, packedLight, packedOverlay);
        } else if (type == EnumDudType.NUKE) {
            poseStack.translate(0, 0, 1.25);
            HBMResourceManager.dud_nuke.renderAll(poseStack, consumer, packedLight, packedOverlay);
        } else if (type == EnumDudType.SALTED) {
            poseStack.translate(0, 0, 0.5);
            HBMResourceManager.dud_salted.renderAll(poseStack, consumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private static ResourceLocation getTexture(EnumDudType type) {
        return switch (type) {
            case BALEFIRE -> HBMResourceManager.dud_balefire_tex;
            case CONVENTIONAL -> HBMResourceManager.dud_conventional_tex;
            case NUKE -> HBMResourceManager.dud_nuke_tex;
            case SALTED -> HBMResourceManager.dud_salted_tex;
        };
    }


    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityCrashedBomb tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}