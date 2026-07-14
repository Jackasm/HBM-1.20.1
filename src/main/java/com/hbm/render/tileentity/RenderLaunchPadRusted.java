package com.hbm.render.tileentity;

import com.hbm.blocks.bomb.LaunchPadRusted;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.item.ItemRenderMissileGeneric;
import com.hbm.tileentity.bomb.TileEntityLaunchPadRusted;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class RenderLaunchPadRusted implements BlockEntityRenderer<TileEntityLaunchPadRusted> {

    public RenderLaunchPadRusted(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityLaunchPadRusted launchpad, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Поворот в зависимости от FACING
        Direction facing = launchpad.getBlockState().getValue(LaunchPadRusted.FACING);
        switch (facing) {
            case EAST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270));
            default -> {}
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.launch_pad_silo_tex));
        HBMResourceManager.launch_pad_silo.renderAll(poseStack, consumer, packedLight, packedOverlay);

        // Ракета
        if (launchpad.missileLoaded) {
            poseStack.translate(0, 1, 0);
            var renderer = ItemRenderMissileGeneric.renderers.get(
                    new ComparableStack(ModItems.MISSILE_DOOMSDAY_RUSTED.get()).makeSingular()
            );
            if (renderer != null) {
                // Создаём временный контекст для рендера
                var context = new ItemRenderMissileGeneric.RenderContext(
                        poseStack,
                        buffer,
                        packedLight,
                        packedOverlay
                );
                renderer.accept(context);
            }
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityLaunchPadRusted launchpad) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}