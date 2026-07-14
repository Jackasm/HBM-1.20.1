package com.hbm.render.item;

import com.hbm.items.block.ItemFoundryOutlet;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderFoundryOutletItem extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEXTURE_OUTLET = ResLocation("hbm", "textures/block/network/foundry_outlet.png");
    private static final ResourceLocation TEXTURE_SLAGTAP = ResLocation("hbm", "textures/block/network/foundry_slagtap.png");

    public RenderFoundryOutletItem() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private ResourceLocation getTexture(ItemStack stack) {
        if (stack.getItem() instanceof ItemFoundryOutlet item) {
            if (item.getBlock() instanceof com.hbm.blocks.network.FoundrySlagtap) {
                return TEXTURE_SLAGTAP;
            }
        }
        return TEXTURE_OUTLET;
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        poseStack.pushPose();

        switch (context) {
            case GUI -> {
                poseStack.translate(0.65, 0.2, 0);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(15));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
            case FIXED, THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND,
                 FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
            default -> {
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.75f, 0.75f, 0.75f);
            }
        }

        ResourceLocation tex = getTexture(stack);
        HBMResourceManager.foundry_outlet.renderAll(poseStack, buffer, tex, packedLight, packedOverlay);

        poseStack.popPose();
    }
}