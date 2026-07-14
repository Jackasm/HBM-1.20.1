package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineRotaryFurnace;
import com.hbm.util.BobMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderRotaryFurnace implements BlockEntityRenderer<TileEntityMachineRotaryFurnace> {

    private static final ResourceLocation TEXTURE = ResLocation("hbm", "textures/block/machines/rotary_furnace.png");

    public RenderRotaryFurnace(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TileEntityMachineRotaryFurnace entity, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int light, int overlay) {
        stack.pushPose();

        stack.translate(0.5, 0, 0.5);

        Direction facing = entity.getBlockState().getValue(BlockDummyable.FACING);

        switch (facing) {
            case EAST -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
            case SOUTH -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
            case WEST -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(0));
            case NORTH -> stack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270));
        }


        // Furnace body
        HBMResourceManager.rotary_furnace.renderPart(stack, buffer, "Furnace", TEXTURE, light, overlay);

        // Piston animation
        stack.pushPose();
        float anim = entity.lastAnim + (entity.anim - entity.lastAnim) * partialTicks;
        double pistonY = BobMathUtil.sps((anim * 0.75) * 0.125) * 0.5 - 0.5;
        stack.translate(0, pistonY, 0);
        HBMResourceManager.rotary_furnace.renderPart(stack, buffer, "Piston", TEXTURE, light, overlay);
        stack.popPose();

        stack.popPose();
    }
}