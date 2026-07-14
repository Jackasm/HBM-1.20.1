package com.hbm.render.tileentity;

import com.hbm.items.ModItems;

import com.hbm.render.model.ModelGun;
import com.hbm.render.model.ModelStatue;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderDecoBlockAlt implements BlockEntityRenderer<BlockEntity> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/models/model_statue.png");
    private static final ResourceLocation GUN_TEXTURE = ResLocation(RefStrings.MODID, "textures/models/model_gun.png");

    private final ModelStatue model;
    private final ModelGun gun;

    public RenderDecoBlockAlt(BlockEntityRendererProvider.Context context) {
        this.model = new ModelStatue();
        this.gun = new ModelGun();
    }

    @Override
    public void render(@NotNull BlockEntity tileEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = tileEntity.getBlockState();
        int metadata = state.getBlock().getDescriptionId().hashCode(); // Упрощенно, лучше использовать свойство блока

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        // Повороты на основе метаданных (нужно адаптировать под ваши свойства блока)
        int rotation = switch (metadata) {
            case 4 -> 90;
            case 2 -> 180;
            case 5 -> 270;
            default -> 0;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // Рендерим статую
        VertexConsumer statueConsumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        model.renderToBuffer(poseStack, statueConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        float g = 0.0625F;
        float q = g * 2 + 0.0625F / 3;

        poseStack.translate(0.0F, -2 * g, q);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        // Рендерим часы (watch) для определённых типов
        //if (tileEntity instanceof TileEntityDecoBlockAltW || tileEntity instanceof TileEntityDecoBlockAltF) {
        renderWatch(poseStack, buffer, packedLight, packedOverlay, g);
        //}

        poseStack.translate(0.0F, 2 * g, -q);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(-g * 20, g * 4, g * 11);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-20));

        // Рендерим пистолет для определённых типов
        //if (tileEntity instanceof TileEntityDecoBlockAltG || tileEntity instanceof TileEntityDecoBlockAltF) {
            VertexConsumer gunConsumer = buffer.getBuffer(RenderType.entityCutout(GUN_TEXTURE));
            gun.renderToBuffer(poseStack, gunConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
       //}

        poseStack.popPose();
    }

    private void renderWatch(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, float g) {
        ItemStack watchStack = new ItemStack(ModItems.WATCH.get());

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        poseStack.pushPose();

        // Применяем трансформации как в оригинале
        poseStack.translate(0.0F, -2 * g, 0.0F);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        // Рендерим предмет
        itemRenderer.renderStatic(
                watchStack,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                buffer,
                mc.level,
                0
        );

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockEntity tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}