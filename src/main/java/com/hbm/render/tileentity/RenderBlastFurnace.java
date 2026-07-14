package com.hbm.render.tileentity;

import com.hbm.blocks.machine.BlastFurnaceBlock;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.tileentity.machine.TileEntityBlastFurnace;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RenderBlastFurnace implements BlockEntityRenderer<TileEntityBlastFurnace> {

    public RenderBlastFurnace(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(@NotNull TileEntityBlastFurnace furnace, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = furnace.getBlockState();
        if (!(state.getBlock() instanceof BlastFurnaceBlock)) return;

        boolean isLit = state.getValue(BlastFurnaceBlock.LIT);
        Direction facing = state.getValue(BlastFurnaceBlock.FACING);
        float rotation = switch (facing) {
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> 270f;
            default -> 0f;
        };
        //packedLight = 0xF000F0;

        // Выбираем текстуру в зависимости от состояния
        ResourceLocation furnaceTex = isLit ?
                HBMResourceManager.machine_blast_furnace_lit_tex : // текстура работающей печки
                HBMResourceManager.machine_blast_furnace_tex; // текстура выключенной печки

        // Рисуем печку
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));

        HFRWavefrontObject furnaceModel = HBMResourceManager.blast_furnace;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(furnaceTex));
        furnaceModel.renderAll(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();

        // Если есть расширение, рисуем его сверху
        if (furnace.hasExtension) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1, 0.5);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));

            ResourceLocation extension = isLit ?
                    HBMResourceManager.difurnace_top_on_alt :
                    HBMResourceManager.difurnace_top_off_alt;

            HBMResourceManager.difurnace_extension.renderPart(poseStack, buffer, "Top",
                    extension, packedLight, packedOverlay);
            // Bottom
            HBMResourceManager.difurnace_extension.renderPart(poseStack, buffer, "Bottom",
                    HBMResourceManager.brick_fire, packedLight, packedOverlay);
            // Side
            HBMResourceManager.difurnace_extension.renderPart(poseStack, buffer, "Side",
                    HBMResourceManager.difurnace_extension_tex, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityBlastFurnace blockEntity) {
        return true;
    }
}
