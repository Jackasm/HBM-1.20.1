package com.hbm.render.entity.item;

import com.hbm.entity.item.EntityTNTPrimedBase;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RenderTNTPrimedBase extends EntityRenderer<EntityTNTPrimedBase> {

    private final BlockRenderDispatcher blockRenderer;

    public RenderTNTPrimedBase(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(@NotNull EntityTNTPrimedBase entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);

        float flashIntensity;

        // Эффект увеличения перед взрывом
        if ((float) entity.fuse - partialTicks + 1.0F < 10.0F) {
            flashIntensity = 1.0F - ((float) entity.fuse - partialTicks + 1.0F) / 10.0F;
            if (flashIntensity < 0.0F) flashIntensity = 0.0F;
            if (flashIntensity > 1.0F) flashIntensity = 1.0F;
            flashIntensity *= flashIntensity;
            flashIntensity *= flashIntensity;
            float scale = 1.0F + flashIntensity * 0.3F;
            poseStack.scale(scale, scale, scale);
        }

        Block block = entity.getBlock();
        BlockState blockState = block.defaultBlockState();

        // Рендерим блок
        this.blockRenderer.renderSingleBlock(blockState, poseStack, buffer, packedLight, 0);

        // Эффект мерцания (мигание)
        flashIntensity = (1.0F - ((float) entity.fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;

        if (entity.fuse / 5 % 2 == 0) {
            poseStack.pushPose();
            poseStack.scale(1.01F, 1.01F, 1.01F);

            // Включаем смешивание цветов для эффекта свечения
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, flashIntensity);

            // Рендерим блок с прозрачностью
            this.blockRenderer.renderSingleBlock(blockState, poseStack, buffer, packedLight, 0);

            // Восстанавливаем цвет
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            poseStack.popPose();
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityTNTPrimedBase entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}