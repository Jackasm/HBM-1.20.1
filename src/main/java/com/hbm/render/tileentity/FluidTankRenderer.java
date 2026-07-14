package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineFluidTank;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Corrosive;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.util.DiamondPronter;
import com.hbm.tileentity.storage.TileEntityMachineFluidTank;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class FluidTankRenderer implements BlockEntityRenderer<TileEntityMachineFluidTank> {

    public FluidTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull TileEntityMachineFluidTank tile, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();

        Direction facing = tile.getBlockState().getValue(MachineFluidTank.FACING);
        switch (facing) {
            case NORTH -> {
                poseStack.mulPose(new Quaternionf().rotationY(0));
                poseStack.translate(0.5, 0, 0.5);
            }
            case SOUTH -> {
                poseStack.mulPose(new Quaternionf().rotationY((float) Math.PI));
                poseStack.translate(0.5 - 1, 0, 0.5 - 1);
            }
            case WEST -> {
                poseStack.mulPose(new Quaternionf().rotationY((float) (Math.PI / 2)));
                poseStack.translate(0.5 - 1, 0, 0.5);
            }
            case EAST -> {
                poseStack.mulPose(new Quaternionf().rotationY((float) (-Math.PI / 2)));
                poseStack.translate(0.5, 0, 0.5 - 1);
            }
            default -> {}
        }

        FluidTypeHBM type = tile.tank.getTankType();

        ResourceLocation tankTexture = HBMResourceManager.fluid_tank_tex;

        if (!tile.hasExploded) {
            HBMResourceManager.fluid_tank.renderPart(poseStack, buffer,"Frame", tankTexture,   combinedLight, combinedOverlay);
            tankTexture = getTextureFromType(type, tile.tank);
            HBMResourceManager.fluid_tank.renderPart(poseStack, buffer,"Tank", tankTexture,   combinedLight, combinedOverlay);
        } else {
            HBMResourceManager.fluid_tank_exploded.renderPart(poseStack, buffer,"Frame", tankTexture,   combinedLight, combinedOverlay);
            tankTexture = HBMResourceManager.fluid_tank_inner_tex;
            HBMResourceManager.fluid_tank_exploded.renderPart(poseStack, buffer,"TankInner", tankTexture,   combinedLight, combinedOverlay);
            tankTexture = getTextureFromType(type, tile.tank);
            HBMResourceManager.fluid_tank_exploded.renderPart(poseStack, buffer,"Tank", tankTexture,   combinedLight, combinedOverlay);
        }

        if (type != null && type != Fluids.NONE.get()) {
            // Передняя сторона
            poseStack.pushPose();
            poseStack.translate(-0.25, 0.5, -1.501);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(1.0F, 0.375F, 0.375F);
            DiamondPronter.renderNFPA(poseStack, buffer,
                    type.getPoison(), type.getFlammability(), type.getReactivity(), type.getSymbol(),
                    combinedLight, combinedOverlay);
            poseStack.popPose();

            // Задняя сторона
            poseStack.pushPose();
            poseStack.translate(0.25, 0.5, 1.501);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            poseStack.scale(1.0F, 0.375F, 0.375F);
            DiamondPronter.renderNFPA(poseStack, buffer,
                    type.getPoison(), type.getFlammability(), type.getReactivity(), type.getSymbol(),
                    combinedLight, combinedOverlay);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private ResourceLocation getTextureFromType(FluidTypeHBM type, FluidTankHBM tank) {
        if (type.renderWithTint) {
            int color = type.getTint();
            float r = ((color >> 16) & 0xFF) / 255F;
            float g = ((color >> 8) & 0xFF) / 255F;
            float b = (color & 0xFF) / 255F;
            return ResLocation(RefStrings.MODID, "textures/block/storage/tank/tank_none.png");
        }

        String name = type.getName().toLowerCase();

        if (type.isAntimatter() || (type.isCorrosive() && type.getTrait(FT_Corrosive.class).isHighlyCorrosive())) {
            name = "DANGER";
        }

        return ResLocation("hbm", "textures/block/storage/tank/tank_" + name + ".png");
    }
}