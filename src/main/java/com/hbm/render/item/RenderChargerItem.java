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
public class RenderChargerItem extends BlockEntityWithoutLevelRenderer {

    public RenderChargerItem() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.75, 0.15, 0.5);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.mulPose(Axis.XP.rotationDegrees(15));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));

        // Нейтральное положение: extend=0, swivel=0
        HBMResourceManager.charger.renderPart(poseStack, buffer,"Base",
                HBMResourceManager.charger_tex, combinedLight, combinedOverlay);

        HBMResourceManager.charger.renderPart(poseStack, buffer,"Left",
                HBMResourceManager.charger_tex, combinedLight, combinedOverlay);

        HBMResourceManager.charger.renderPart(poseStack, buffer,"Right",
                HBMResourceManager.charger_tex, combinedLight, combinedOverlay);

        HBMResourceManager.charger.renderPart(poseStack, buffer,"Slide",
                HBMResourceManager.charger_tex, combinedLight, combinedOverlay);

        // Свечение можно не рисовать в инвентаре — или нарисовать с FULL_BRIGHT,
        // если хочешь, чтобы «лампа» всегда светилась.
        HBMResourceManager.charger.renderPart(poseStack, buffer,"Light",
                HBMResourceManager.charger_tex,
                net.minecraft.client.renderer.LightTexture.FULL_BRIGHT, combinedOverlay);

        poseStack.popPose();
    }
}