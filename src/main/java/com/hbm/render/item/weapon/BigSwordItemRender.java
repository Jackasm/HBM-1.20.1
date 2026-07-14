package com.hbm.render.item.weapon;

import com.hbm.render.model.ModelBigSword;
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

public class BigSwordItemRender extends BlockEntityWithoutLevelRenderer {

    private final ModelBigSword bigSwordModel;
    private static final ResourceLocation TEXTURE = ResLocation(MODID, "textures/models/weapon/big_sword.png");

    public BigSwordItemRender() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.bigSwordModel = new ModelBigSword(Minecraft.getInstance().getEntityModels().bakeLayer(ModelBigSword.LAYER));
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
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                poseStack.translate(0.0F, 0.4F, -0.7F);
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.7F, 0.4F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-45.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-180.0F));
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

        bigSwordModel.renderToBuffer(poseStack, buffer.getBuffer(bigSwordModel.renderType(TEXTURE)), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}
