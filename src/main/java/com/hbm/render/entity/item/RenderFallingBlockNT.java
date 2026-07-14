package com.hbm.render.entity.item;

import com.hbm.blocks.BlockFallingNT;
import com.hbm.entity.item.EntityFallingBlockNT;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class RenderFallingBlockNT extends EntityRenderer<EntityFallingBlockNT> {

    public RenderFallingBlockNT(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(@NotNull EntityFallingBlockNT entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        Level world = entity.level();
        Block block = entity.getBlockForRender();
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());

        poseStack.pushPose();

        try {
            if (block != null && block != world.getBlockState(pos).getBlock()) {
                // Позиционирование
                poseStack.translate(0, 0, 0);

                BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                BlockState state = block.defaultBlockState();

                // Проверяем, нужно ли кастомное отображение
                if (block instanceof BlockFallingNT falling && falling.shouldOverrideRenderer()) {
                    // Кастомный рендер через метод блока
                    VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
                    falling.overrideRenderer(entity, poseStack, vertexConsumer, packedLight);
                } else {
                    // Стандартный рендер падающего блока
                    RenderType renderType = ItemBlockRenderTypes.getRenderType(state, false);
                    VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

                    BakedModel model = dispatcher.getBlockModel(state);
                    int color = Minecraft.getInstance().getBlockColors().getColor(state, world, pos, 0);
                    float r = (float) (color >> 16 & 255) / 255.0F;
                    float g = (float) (color >> 8 & 255) / 255.0F;
                    float b = (float) (color & 255) / 255.0F;

                    // Рендерим блок
                    for (RenderType rt : model.getRenderTypes(state, RandomSource.create(42), ModelData.EMPTY)) {
                        VertexConsumer customVertexConsumer = buffer.getBuffer(rt);
                        dispatcher.getModelRenderer().renderModel(
                                poseStack.last(),
                                customVertexConsumer,
                                state,
                                model,
                                r, g, b,
                                packedLight,
                                OverlayTexture.NO_OVERLAY,
                                ModelData.EMPTY,
                                rt
                        );
                    }
                }
            }
        } catch (Exception ignored) {
            // Ловим исключения, как в оригинале
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityFallingBlockNT entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}