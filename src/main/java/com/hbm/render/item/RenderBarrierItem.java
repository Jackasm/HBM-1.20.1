package com.hbm.render.item;

import com.hbm.render.tileentity.RenderBarrier;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderBarrierItem extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/block/wood_barrier.png");

    public RenderBarrierItem() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int combinedLight, int combinedOverlay) {

        poseStack.pushPose();

        switch (context) {
            case GUI -> {
                poseStack.translate(-0.1, 0.1, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIXED -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND,
                 THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0, 0.0, 0.6);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case HEAD -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
            }
            default -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.9f, 0.9f, 0.9f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));

        // Как в 1.7.10 renderInventoryBlock: центральная планка + 2 перекладины
        RenderBarrier.drawBox(poseStack, consumer, combinedLight, combinedOverlay,
                0.4375F, 0F, 0.4375F, 0.5625F, 1F, 0.5625F);   // центральная планка
        RenderBarrier.drawBox(poseStack, consumer, combinedLight, combinedOverlay,
                0.5F, 0.0625F, 0F, 0.5625F, 0.4725F, 1F);      // нижняя перекладина
        RenderBarrier.drawBox(poseStack, consumer, combinedLight, combinedOverlay,
                0.5F, 0.5625F, 0F, 0.5625F, 0.9375F, 1F);      // верхняя перекладина

        poseStack.popPose();
    }
}