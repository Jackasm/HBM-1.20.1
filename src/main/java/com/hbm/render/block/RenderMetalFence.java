package com.hbm.render.block;

import com.hbm.blocks.generic.BlockMetalFence;
import com.hbm.render.model.ModelMetalFence;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class RenderMetalFence implements BlockEntityRenderer<BlockEntity> {

    private final ModelMetalFence model;

    public RenderMetalFence(BlockEntityRendererProvider.Context ignoredContext) {
        this.model = new ModelMetalFence();
    }

    @Override
    public void render(@NotNull BlockEntity tile, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = tile.getBlockState();
        if (!(state.getBlock() instanceof BlockMetalFence fence)) return;

        // === ПЕРЕСЧЁТ СОЕДИНЕНИЙ ПО СОСЕДЯМ В МИРЕ ===
        // Не доверяем state.getValue(NORTH/etc), потому что при генерации структур
        // neighborChanged не вызывается, и state остаётся дефолтным.
        var level = tile.getLevel();
        var pos = tile.getBlockPos();

        boolean north = false, south = false, east = false, west = false;
        if (level != null) {
            north = connectsTo(level.getBlockState(pos.north()));
            south = connectsTo(level.getBlockState(pos.south()));
            east  = connectsTo(level.getBlockState(pos.east()));
            west  = connectsTo(level.getBlockState(pos.west()));
        }

        boolean isPost = state.getValue(BlockMetalFence.TYPE) == BlockMetalFence.FenceType.POST;

        boolean showPost = isPost;
        if (!isPost) {
            int count = (north ? 1 : 0) + (south ? 1 : 0) + (east ? 1 : 0) + (west ? 1 : 0);
            boolean straight = (north && south && !east && !west) || (!north && !south && east && west);
            if (count == 0) {
                showPost = true;
            } else if (count == 2 && straight) {
                showPost = false;
            } else {
                showPost = true;
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        var vertexConsumer = buffer.getBuffer(RenderType.entityCutout(
                ResLocation(MODID, "textures/block/fence_metal_model.png")));
        this.model.render(poseStack, vertexConsumer, packedLight, packedOverlay,
                north, south, east, west, showPost);

        poseStack.popPose();
    }

    private static boolean connectsTo(BlockState state) {
        return state.getBlock() instanceof BlockMetalFence;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockEntity blockEntity) {
        return true;
    }
}