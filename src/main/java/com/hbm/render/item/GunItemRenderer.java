package com.hbm.render.item;

import com.hbm.render.item.weapon.sedna.BaseGunRenderer;
import com.hbm.render.item.weapon.sedna.RegistryGunRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GunItemRenderer extends BlockEntityWithoutLevelRenderer {


    public GunItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {



        BaseGunRenderer renderer = RegistryGunRender.getRenderer(stack.getItem());

        if (renderer != null) {
            switch (context) {
                case FIRST_PERSON_LEFT_HAND:
                case FIRST_PERSON_RIGHT_HAND:
                    float partialTick = Minecraft.getInstance().getFrameTime();
                    renderer.renderFirstPerson(stack, context, poseStack, buffer,
                            packedLight, packedOverlay, partialTick);
                    break;

                case THIRD_PERSON_LEFT_HAND:
                case THIRD_PERSON_RIGHT_HAND:
                    renderer.renderThirdPerson(stack, context, poseStack, buffer,
                            packedLight, packedOverlay);
                    break;

                case GUI:
                    renderer.renderGUI(stack, poseStack, buffer, packedLight, packedOverlay);
                    break;

                case GROUND:
                case FIXED:
                default:
                    renderer.renderGround(stack, poseStack, buffer, packedLight, packedOverlay);
                    break;
            }
        }
    }
}