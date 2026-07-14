package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineDiesel;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineDiesel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DieselGenRenderer implements BlockEntityRenderer<TileEntityMachineDiesel> {

    public DieselGenRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityMachineDiesel tile, float partialTicks, PoseStack stack, @NotNull MultiBufferSource buffer, int light, int overlay) {
        stack.pushPose();
        stack.translate(0.5D, 0, 0.5D);

        BlockState state = tile.getBlockState();
        Direction facing = state.getValue(MachineDiesel.FACING).getOpposite();

        switch (facing) {
            case EAST -> stack.mulPose(Axis.YP.rotationDegrees(270));
            case SOUTH -> stack.mulPose(Axis.YP.rotationDegrees(0));
            case WEST -> stack.mulPose(Axis.YP.rotationDegrees(90));
            case NORTH -> stack.mulPose(Axis.YP.rotationDegrees(180));
            default -> {}
        }

        ResourceLocation genTexture = HBMResourceManager.diesel_gen_tex;
        // Render Generator part
        HBMResourceManager.dieselgen.renderPart(stack, buffer, "Generator", genTexture, light, overlay);

        if (tile.hasAcceptableFuel() && tile.tank.getFill() > 0) {
            double swingSide = Math.sin(System.currentTimeMillis() / 50D) * 0.005;
            double swingFront = Math.sin(System.currentTimeMillis() / 25D) * 0.005;
            stack.translate(swingFront, 0, swingSide);
        }

        // Render Engine part
        HBMResourceManager.dieselgen.renderPart(stack, buffer, "Engine", genTexture, light, overlay);

        stack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineDiesel tile) {
        return false;
    }
}