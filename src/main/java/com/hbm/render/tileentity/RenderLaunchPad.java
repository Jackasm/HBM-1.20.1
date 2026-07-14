package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.item.ItemRenderMissileGeneric;
import com.hbm.tileentity.bomb.TileEntityLaunchPad;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class RenderLaunchPad implements BlockEntityRenderer<TileEntityLaunchPad> {

    public RenderLaunchPad(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(TileEntityLaunchPad tile, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Поворот в зависимости от направления блока
        BlockState state = tile.getBlockState();
        Direction facing = state.getValue(BlockDummyable.FACING);

        float rotation = switch (facing) {
            case NORTH -> 0F;
            case EAST -> 90F;
            case SOUTH -> 180F;
            case WEST -> 270F;
            default -> 0F;
        };
        poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(rotation)));

        VertexConsumer padConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.launch_pad_tex));
        HBMResourceManager.launch_pad.renderAll(poseStack, padConsumer, packedLight, packedOverlay);

        if (tile.toRender != null && !tile.toRender.isEmpty()) {
            renderMissile(tile.toRender, poseStack, buffer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private void renderMissile(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                               int packedLight, int packedOverlay) {

        ComparableStack key = new ComparableStack(stack).makeSingular();
        Consumer<ItemRenderMissileGeneric.RenderContext> renderer =
                ItemRenderMissileGeneric.renderers.get(key);

        if (renderer == null) return;

        poseStack.pushPose();

        poseStack.translate(0, 1.0, 0);

        // Создаем контекст для рендера
        ItemRenderMissileGeneric.RenderContext context =
                new ItemRenderMissileGeneric.RenderContext(poseStack, buffer, packedLight, packedOverlay);

        renderer.accept(context);

        poseStack.popPose();
    }
}