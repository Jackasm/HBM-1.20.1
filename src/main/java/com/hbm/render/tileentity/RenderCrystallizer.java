package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineCrystallizer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static com.hbm.blocks.BlockDummyable.FACING;

@OnlyIn(Dist.CLIENT)
public class RenderCrystallizer implements BlockEntityRenderer<TileEntityMachineCrystallizer> {

    public RenderCrystallizer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityMachineCrystallizer te, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0, 0.5D);

        BlockState state = te.getBlockState();
        Direction dir = state.getValue(FACING);
        float rot = switch (dir) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) Math.toRadians(rot), 0));

        // Body
        HBMResourceManager.crystallizer.renderPart(poseStack, buffer, "Body", HBMResourceManager.crystallizer_tex, packedLight, packedOverlay);

        // Spinner
        poseStack.pushPose();
        float spinnerRot = te.prevAngle + (te.angle - te.prevAngle) * partialTicks;
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) Math.toRadians(spinnerRot), 0));
        HBMResourceManager.crystallizer.renderPart(poseStack, buffer, "Spinner", HBMResourceManager.crystallizer_tex, packedLight, packedOverlay);
        poseStack.popPose();

        // Fluid (blend)
        if (te.prevAngle != te.angle && te.tank != null && te.tank.getFill() > 0) {
            ResourceLocation fluidTexture = te.tank.getTankType().getTexture();
            VertexConsumer fluidConsumer = buffer.getBuffer(RenderType.entityTranslucent(fluidTexture));
            HBMResourceManager.crystallizer.renderPart(poseStack, fluidConsumer, "Fluid", packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineCrystallizer te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

}