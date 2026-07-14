package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LandmineRenderer implements BlockEntityRenderer<BlockEntity> {

    public LandmineRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlockEntity tileEntity, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = tileEntity.getBlockState();
        Block block = state.getBlock();
        Level level = tileEntity.getLevel();
        BlockPos pos = tileEntity.getBlockPos();

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        if (block == ModBlocks.MINE_AP.get()) {
            poseStack.scale(0.375F, 0.375F, 0.375F);
            poseStack.translate(0, -0.0625 * 3.5, 0);

            ResourceLocation texture = HBMResourceManager.mine_ap_grass_tex;
            if (level != null) {
                int height = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
                if (height > pos.getY() + 2) {
                    texture = HBMResourceManager.mine_ap_stone_tex;
                } else {
                    Biome biome = level.getBiome(pos).value();
                    if (biome.coldEnoughToSnow(pos)) {
                        texture = HBMResourceManager.mine_ap_snow_tex;
                    } else if (biome.getBaseTemperature() >= 1.5F && biome.getModifiedClimateSettings().downfall() <= 0.1F) {
                        texture = HBMResourceManager.mine_ap_desert_tex;
                    }
                }
            }

            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));
            HBMResourceManager.mine_ap.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        if (block == ModBlocks.MINE_SHRAP.get()) {
            poseStack.scale(0.375F, 0.375F, 0.375F);
            poseStack.translate(0, -0.0625 * 3.5, 0);
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.mine_shrap_tex));
            HBMResourceManager.mine_ap.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        if (block == ModBlocks.MINE_HE.get()) {
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.mine_he_tex));
            HBMResourceManager.mine_he.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        if (block == ModBlocks.MINE_FAT.get()) {
            poseStack.scale(0.25F, 0.25F, 0.25F);
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(HBMResourceManager.mine_fat_tex));
            HBMResourceManager.mine_fat.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        if (block == ModBlocks.MINE_NAVAL.get()) {
            poseStack.translate(0, 0.5, 0);
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.mine_naval_tex));
            HBMResourceManager.mine_naval.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}