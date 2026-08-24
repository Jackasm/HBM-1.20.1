package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineAmmoPress;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderAmmoPress implements BlockEntityRenderer<TileEntityMachineAmmoPress> {

    public RenderAmmoPress(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntityMachineAmmoPress tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(getRotation(tile)));

        float press = tile.prevPress + (tile.press - tile.prevPress) * partialTicks;
        float lift = tile.prevLift + (tile.lift - tile.prevLift) * partialTicks;

        // Рендер рамки
        HBMResourceManager.ammo_press.renderPart(poseStack, buffer,
                "Frame", HBMResourceManager.ammo_press_tex, combinedLight, combinedOverlay);

        // Рендер пресса (двигается вниз)
        poseStack.pushPose();
        poseStack.translate(0, -press * 0.25F, 0);
        HBMResourceManager.ammo_press.renderPart(poseStack, buffer,
                "Press", HBMResourceManager.ammo_press_tex, combinedLight, combinedOverlay);
        poseStack.popPose();

        // Рендер гильз (двигаются вверх)
        poseStack.pushPose();
        poseStack.translate(0, lift * 0.5F - 0.5F, 0);
        HBMResourceManager.ammo_press.renderPart(poseStack, buffer,
                "Shells", HBMResourceManager.ammo_press_tex, combinedLight, combinedOverlay);

        // Пули показываются только при отжиме
        if (tile.animState == TileEntityMachineAmmoPress.AnimationState.RETRACTING ||
                tile.animState == TileEntityMachineAmmoPress.AnimationState.LOWERING) {
            HBMResourceManager.ammo_press.renderPart(poseStack, buffer,
                    "Bullets", HBMResourceManager.ammo_press_tex,  combinedLight, combinedOverlay);
        }
        poseStack.popPose();

        poseStack.popPose();
    }

    private float getRotation(TileEntityMachineAmmoPress tile) {
        BlockState state = tile.getBlockState();
        if (state.hasProperty(BlockDummyable.FACING)) {
            Direction facing = state.getValue(BlockDummyable.FACING);
            return switch (facing) {
                case SOUTH -> 90;
                case WEST -> 180;
                case NORTH -> 270;
                case EAST -> 0;
                default -> 0;
            };
        }
        return 0;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineAmmoPress tile) {
        return true;
    }
}