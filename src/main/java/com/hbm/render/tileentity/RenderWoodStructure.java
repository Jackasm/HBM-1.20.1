package com.hbm.render.tileentity;

import com.hbm.blocks.generic.BlockWoodStructure;
import com.hbm.blocks.generic.BlockWoodStructure.EnumWoodStructure;
import com.hbm.tileentity.deco.TileEntityWoodStructure;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderWoodStructure implements BlockEntityRenderer<TileEntityWoodStructure> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/block/wood_barrier.png");

    public RenderWoodStructure(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityWoodStructure tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        Level level = tile.getLevel();
        if (level == null) return;

        BlockPos pos = tile.getBlockPos();
        BlockState state = tile.getBlockState();
        if (!(state.getBlock() instanceof BlockWoodStructure structure)) return;

        EnumWoodStructure type = structure.getStructureType();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));

        switch (type) {
            case ROOF -> renderRoof(level, pos, state, poseStack, consumer, packedLight, packedOverlay);
            case SCAFFOLD -> renderScaffold(level, pos, state, poseStack, consumer, packedLight, packedOverlay);
            case CEILING -> renderCeiling(poseStack, consumer, packedLight, packedOverlay);
        }
    }

    // ---------- ROOF ----------
    private void renderRoof(Level level, BlockPos pos, BlockState state,
                            PoseStack poseStack, VertexConsumer consumer,
                            int light, int overlay) {

        boolean nx = isSame(level, pos.offset(-1, 0, 0), state);
        boolean px = isSame(level, pos.offset(1, 0, 0), state);

        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0F, 0F, 0F, 0.125F, 0.125F, 1F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0.875F, 0F, 0F, 1F, 0.125F, 1F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                nx ? 0F : 0.0625F, 0.125F, 0.0625F, px ? 1F : 0.9375F, 0.1875F, 0.4375F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                nx ? 0F : 0.0625F, 0.125F, 0.5625F, px ? 1F : 0.9375F, 0.1875F, 0.9375F);
    }

    // ---------- SCAFFOLD ----------
    private void renderScaffold(Level level, BlockPos pos, BlockState state,
                                PoseStack poseStack, VertexConsumer consumer,
                                int light, int overlay) {

        boolean py = isSame(level, pos.offset(0, 1, 0), state);
        boolean nx = isSame(level, pos.offset(-1, 0, 0), state);
        boolean nz = isSame(level, pos.offset(0, 0, -1), state);
        boolean px = isSame(level, pos.offset(1, 0, 0), state);
        boolean pz = isSame(level, pos.offset(0, 0, 1), state);

        // 4 стойки
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0.0625F, 0F, 0.0625F, 0.1875F, py ? 1F : 0.875F, 0.1875F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0.8125F, 0F, 0.0625F, 0.9375F, py ? 1F : 0.875F, 0.1875F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0.0625F, 0F, 0.8125F, 0.1875F, py ? 1F : 0.875F, 0.9375F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0.8125F, 0F, 0.8125F, 0.9375F, py ? 1F : 0.875F, 0.9375F);

        // 4 перекладины
        if (!nx) RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0F, 0.125F, 0F, 0.0625F, 0.375F, 1F);
        if (!px) RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0.9375F, 0.125F, 0F, 1F, 0.375F, 1F);
        if (!nz) RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0F, 0.5F, 0F, 1F, 0.75F, 0.0625F);
        if (!pz) RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0F, 0.5F, 0.9375F, 1F, 0.75F, 1F);

        // пол сверху
        if (!py) RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0F, 0.875F, 0F, 1F, 1F, 1F);
    }

    // ---------- CEILING ----------
    private void renderCeiling(PoseStack poseStack, VertexConsumer consumer, int light, int overlay) {
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0F, 0.875F, 0F, 0.125F, 0.9375F, 1F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0.875F, 0.875F, 0F, 1F, 0.9375F, 1F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0F, 0.9375F, 0.0625F, 1F, 1F, 0.4375F);
        RenderBarrier.drawBox(poseStack, consumer, light, overlay,
                0F, 0.9375F, 0.5625F, 1F, 1F, 0.9375F);
    }

    private boolean isSame(Level level, BlockPos pos, BlockState state) {
        BlockState other = level.getBlockState(pos);
        return other.getBlock() == state.getBlock()
                && other.getBlock() instanceof BlockWoodStructure;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityWoodStructure tile) {
        return false;
    }
}