package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderVendingMachineItem extends BlockEntityWithoutLevelRenderer {

    public RenderVendingMachineItem() {
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
                poseStack.translate(0.5, 0.15, 0.5);
                poseStack.scale(0.35f, 0.35f, 0.35f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.25f, 0.25f, 0.25f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIXED -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND,
                 THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case HEAD -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
            }
            default -> {
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.scale(0.35f, 0.35f, 0.35f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
        }

        String part = (stack.getItem() == com.hbm.items.ModItems.VENDING_MACHINE_SODA.get())
                ? "Soda" : "Obamna";

        HBMResourceManager.vending_machine.renderPart(poseStack, buffer, part,
                HBMResourceManager.vending_machine_tex, combinedLight, combinedOverlay);

        poseStack.popPose();
    }
}