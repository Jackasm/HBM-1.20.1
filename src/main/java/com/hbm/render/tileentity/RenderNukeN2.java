package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.tileentity.bomb.TileEntityNukeN2;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class RenderNukeN2 implements BlockEntityRenderer<TileEntityNukeN2> {

    private static final HFRWavefrontObject MODEL = HBMResourceManager.n2;
    private static final ResourceLocation TEXTURE = HBMResourceManager.n2_tex;

    public RenderNukeN2(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull TileEntityNukeN2 tileEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5D, 0, 0.5D);

        // Получаем направление из BlockState
        BlockState state = tileEntity.getBlockState();
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

        // Поворачиваем модель в зависимости от направления
        float rotation = switch (facing) {
            case SOUTH -> 90F;
            case WEST -> 180F;
            case NORTH -> 270F;
            default -> 0F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        MODEL.renderAll(poseStack, consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    // Метод для рендеринга в инвентаре
    public static void renderInventory(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                                       int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0, -5, 0);
        poseStack.scale(2.25F, 2.25F, 2.25F);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        MODEL.renderAll(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    // Метод для рендеринга в мире (предмет)
    public static void renderWorld(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                                   int packedLight, int packedOverlay, ItemDisplayContext transformType) {
        poseStack.pushPose();

        if (transformType == ItemDisplayContext.GROUND) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } else if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.scale(0.75F, 0.75F, 0.75F);
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.scale(1.5F, 1.5F, 1.5F);
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        MODEL.renderAll(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityNukeN2 tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}