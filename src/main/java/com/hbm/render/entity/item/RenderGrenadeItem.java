package com.hbm.render.entity.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderGrenadeItem<T extends Entity> extends EntityRenderer<T> {

    private final ItemRenderer itemRenderer;
    private final ItemStack itemStack;
    private final float scale;

    public RenderGrenadeItem(EntityRendererProvider.Context context, ItemStack stack, float scale) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.itemStack = stack;
        this.scale = scale;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND,
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer,
                entity.level(), entity.getId());

        poseStack.popPose();
    }

    @Override
    public net.minecraft.resources.@NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return null;
    }
}