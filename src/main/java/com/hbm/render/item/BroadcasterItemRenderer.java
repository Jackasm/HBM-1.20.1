package com.hbm.render.item;

import com.hbm.render.model.ModelBroadcaster;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class BroadcasterItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/block/model_broadcaster.png");
    private final ModelBroadcaster model;

    public BroadcasterItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.model = new ModelBroadcaster();
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext transformType,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);

        // Масштабирование для разных контекстов
        if (transformType == ItemDisplayContext.GUI) {
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.mulPose(Axis.XP.rotationDegrees(20));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
        } else if (transformType == ItemDisplayContext.GROUND) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.scale(0.6F, 0.6F, 0.6F);
        } else {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}