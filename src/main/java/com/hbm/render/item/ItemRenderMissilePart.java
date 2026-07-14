package com.hbm.render.item;

import com.hbm.items.weapon.ItemCustomMissilePart;
import com.hbm.render.util.MissilePart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemRenderMissilePart extends BlockEntityWithoutLevelRenderer {

    public ItemRenderMissilePart() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext transformType,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        if (!(stack.getItem() instanceof ItemCustomMissilePart itemPart)) return;

        MissilePart part = MissilePart.getPart(stack.getItem());
        if (part == null || part.model == null) return;

        poseStack.pushPose();

        switch (transformType) {
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
                poseStack.translate(0.5, 0, 0);
                // fall through
            case GROUND:
            case FIXED:
                double scale = 0.4;
                poseStack.scale((float) scale, (float) scale, (float) scale);

                VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(part.texture));
                part.model.renderAll(poseStack, consumer, packedLight, packedOverlay);
                break;

            case GUI:
                double height = part.guiheight;
                if (height == 0D) height = 4D;

                double size = 10;
                double scaleGui = size / height;



                poseStack.translate(0.7, 1, 0);

                scale = 0.06;
                poseStack.scale((float) scale, (float) scale, (float) scale);

                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.mulPose(Axis.ZP.rotationDegrees(135));
                poseStack.mulPose(Axis.XP.rotationDegrees(145));

                if (part.type == ItemCustomMissilePart.PartType.WARHEAD) {
                    poseStack.translate(0, height / 8 * scaleGui, 0);
                }

                if (part.type == ItemCustomMissilePart.PartType.FUSELAGE) {
                    poseStack.translate(0, height / 4 * scaleGui, 0);
                }

                poseStack.translate(3.5, 14, 0);
                poseStack.scale((float) -scaleGui, (float) -scaleGui, (float) -scaleGui);

                float rotation = (System.currentTimeMillis() / 25) % 360;
                poseStack.mulPose(Axis.YN.rotationDegrees(rotation));

                VertexConsumer guiConsumer = buffer.getBuffer(RenderType.entityCutout(part.texture));
                part.model.renderAll(poseStack, guiConsumer, packedLight, packedOverlay);
                break;

            default:
                break;
        }

        poseStack.popPose();
    }
}