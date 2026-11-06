package com.hbm.render.tileentity;

import com.hbm.blocks.generic.BlockSandbags;
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
public class RenderSandbags implements BlockEntityRenderer<BlockSandbags.TileEntitySandbags> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/block/sandbags.png");

    public RenderSandbags(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull BlockSandbags.TileEntitySandbags tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        Level level = tile.getLevel();
        if (level == null) return;

        BlockPos pos = tile.getBlockPos();
        BlockState state = tile.getBlockState();

        boolean nx = isConnectable(level, pos.offset(-1, 0, 0), state);
        boolean nz = isConnectable(level, pos.offset(0, 0, -1), state);
        boolean px = isConnectable(level, pos.offset(1, 0, 0), state);
        boolean pz = isConnectable(level, pos.offset(0, 0, 1), state);

        float minX = nx ? 0F : 0.25F;
        float minZ = nz ? 0F : 0.25F;
        float maxX = px ? 1F : 0.75F;
        float maxZ = pz ? 1F : 0.75F;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));

        RenderBarrier.drawBox(poseStack, consumer, packedLight, packedOverlay,
                minX, 0F, minZ, maxX, 1F, maxZ);
    }

    private boolean isConnectable(Level level, BlockPos pos, BlockState self) {
        BlockState other = level.getBlockState(pos);
        return other.getBlock() == self.getBlock() || other.isCollisionShapeFullBlock(level, pos);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockSandbags.TileEntitySandbags tile) {
        return false;
    }
}