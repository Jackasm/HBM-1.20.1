package com.hbm.render.item;

import com.hbm.render.loader.HFRWavefrontObject;
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

public class DemonLampItemRenderer extends BlockEntityWithoutLevelRenderer {

    private HFRWavefrontObject DEMON_LAMP = null;
    private ResourceLocation TEX = null;

    public DemonLampItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    private HFRWavefrontObject getModel() {
        if (DEMON_LAMP == null) {
            DEMON_LAMP = new HFRWavefrontObject(
                    ResLocation(RefStrings.MODID, "models/block/machines/lamp_demon.obj")
            );
        }
        return DEMON_LAMP;
    }

    private ResourceLocation getTexture() {
        if (TEX == null) {
            TEX = ResLocation(RefStrings.MODID, "textures/block/machines/lamp_demon.png");
        }
        return TEX;
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        HFRWavefrontObject model = getModel();
        if (model == null) {
            return;
        }

        poseStack.pushPose();

        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.XP.rotationDegrees(25));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.2, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0.2, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(getTexture()));
        model.renderAll(poseStack, consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}