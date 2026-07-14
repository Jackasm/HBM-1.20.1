package com.hbm.render.block;

import com.hbm.blocks.network.FluidDuctStandard;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.util.Library;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RenderFluidDuct implements BlockEntityRenderer<BlockEntity> {


    public RenderFluidDuct(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlockEntity tileEntity, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        Level world = tileEntity.getLevel();
        BlockPos pos = tileEntity.getBlockPos();
        BlockState state = Objects.requireNonNull(world).getBlockState(pos);
        Block block = state.getBlock();

        if (!(block instanceof FluidDuctStandard))
            return;

        // Получаем иконки из текстур
        int meta = state.getValue(FluidDuctStandard.PIPE_TYPE);

        ResourceLocation icon = FluidDuctStandard.TEXTURES.get(meta);
        ResourceLocation overlay = FluidDuctStandard.OVERLAYS.get(meta);

        // Получаем тип жидкости из TileEntity
        FluidTypeHBM type = Fluids.NONE.get();
        int color = 0xff00ff;

        if (tileEntity instanceof TileEntityPipeBaseNT pipe) {
            type = pipe.getFluidType();
            color = type.getColor();
        }

        // Проверяем соединения
        boolean pX = Library.canConnectFluid(world, pos.relative(Direction.EAST), Direction.EAST, type);
        boolean nX = Library.canConnectFluid(world, pos.relative(Direction.WEST), Direction.WEST, type);
        boolean pY = Library.canConnectFluid(world, pos.relative(Direction.UP), Direction.UP, type);
        boolean nY = Library.canConnectFluid(world, pos.relative(Direction.DOWN), Direction.DOWN, type);
        boolean nZ = Library.canConnectFluid(world, pos.relative(Direction.SOUTH), Direction.SOUTH, type);
        boolean pZ = Library.canConnectFluid(world, pos.relative(Direction.NORTH), Direction.NORTH, type);

        int mask = (pX ? 32 : 0) | (nX ? 16 : 0) | (pY ? 8 : 0) | (nY ? 4 : 0) | (pZ ? 2 : 0) | (nZ ? 1 : 0);

        

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        if (mask == 0) {
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pX", combinedLight, combinedOverlay);
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nX", combinedLight, combinedOverlay);
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pY", combinedLight, combinedOverlay);
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nY", combinedLight, combinedOverlay);
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pZ", combinedLight, combinedOverlay);
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nZ", combinedLight, combinedOverlay);
        } else if (mask == 0b100000 || mask == 0b010000) {
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pX", combinedLight, combinedOverlay);
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nX", combinedLight, combinedOverlay);
        } else if (mask == 0b001000 || mask == 0b000100) {
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pY", combinedLight, combinedOverlay);
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nY", combinedLight, combinedOverlay);
        } else if (mask == 0b000010 || mask == 0b000001) {
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pZ", combinedLight, combinedOverlay);
            renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nZ", combinedLight, combinedOverlay);
        } else {

            if (pX) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pX", combinedLight, combinedOverlay);
            if (nX) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nX", combinedLight, combinedOverlay);
            if (pY) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pY", combinedLight, combinedOverlay);
            if (nY) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nY", combinedLight, combinedOverlay);
            if (pZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pZ", combinedLight, combinedOverlay);
            if (nZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nZ", combinedLight, combinedOverlay);

            if (!pX && !pY && !pZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "ppp", combinedLight, combinedOverlay);
            if (!pX && !pY && !nZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "ppn", combinedLight, combinedOverlay);
            if (!nX && !pY && !pZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "npp", combinedLight, combinedOverlay);
            if (!nX && !pY && !nZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "npn", combinedLight, combinedOverlay);
            if (!pX && !nY && !pZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pnp", combinedLight, combinedOverlay);
            if (!pX && !nY && !nZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "pnn", combinedLight, combinedOverlay);
            if (!nX && !nY && !pZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nnp", combinedLight, combinedOverlay);
            if (!nX && !nY && !nZ) renderDuct(HBMResourceManager.fluid_duct, icon, overlay, color, buffer, poseStack, "nnn", combinedLight, combinedOverlay);
        }

        poseStack.popPose();
    }

    private void renderDuct(HFRWavefrontObject model, ResourceLocation icon, ResourceLocation overlay,
                            int color, MultiBufferSource buffer, PoseStack poseStack,
                            String part, int light, int overlayVal) {

        poseStack.pushPose();

        int fullBright = 0xF000F0;

        model.renderPart(poseStack, buffer, part, icon, fullBright, overlayVal);

        // Рендер оверлея с цветом жидкости

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 0.4f;

        model.renderPartColored(poseStack, buffer, part, overlay, light, overlayVal, r, g, b, a);

        poseStack.popPose();
    }
}