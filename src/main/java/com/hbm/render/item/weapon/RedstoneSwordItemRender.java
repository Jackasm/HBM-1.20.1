package com.hbm.render.item.weapon;

import com.hbm.render.model.ModelSword;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class RedstoneSwordItemRender extends BlockEntityWithoutLevelRenderer {

    private final ModelSword swordModel;
    private static final ResourceLocation TEXTURE = ResLocation(MODID, "textures/models/weapon/sword_redstone.png");

    public RedstoneSwordItemRender() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.swordModel = new ModelSword(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSword.LAYER));
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        switch (context) {
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.mulPose(Axis.ZP.rotationDegrees(-135.0F));
                poseStack.translate(-0.8F, 0.4F, -0.1F);
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.7F, 0.4F, 0.5F);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-135.0F));
                break;
            case GROUND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                break;
            case GUI:
                poseStack.translate(0.2, 0.15, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));
                break;
            default:
                break;
        }

        swordModel.renderToBuffer(poseStack, buffer.getBuffer(swordModel.renderType(TEXTURE)), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}
