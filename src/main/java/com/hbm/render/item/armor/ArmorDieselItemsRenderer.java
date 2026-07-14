package com.hbm.render.item.armor;

import com.hbm.items.ModArmorItems;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ArmorDieselItemsRenderer extends BlockEntityWithoutLevelRenderer {

    public ModelRendererObj head;
    public ModelRendererObj body;
    public ModelRendererObj leftArm;
    public ModelRendererObj rightArm;
    public ModelRendererObj leftLeg;
    public ModelRendererObj rightLeg;
    public ModelRendererObj leftFoot;
    public ModelRendererObj rightFoot;

    public ArmorDieselItemsRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.head = new ModelRendererObj(HBMResourceManager.armor_dieselsuit, "Head");
        this.body = new ModelRendererObj(HBMResourceManager.armor_dieselsuit, "Body");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_dieselsuit, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_dieselsuit, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_dieselsuit, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_dieselsuit, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_dieselsuit, "LeftBoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_dieselsuit, "RightBoot");
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        float scale = 0.0625F;

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        Item item = stack.getItem();

        if (item == ModArmorItems.DIESELSUIT_HELMET.get()) {
            poseStack.translate(-0.45D, -0.2D, 0.0D);
            poseStack.scale(0.8F, 0.8F, 0.8F);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/dieselsuit_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.DIESELSUIT_CHESTPLATE.get()) {
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.translate(-0.6D, -1D, 0.0D);

            VertexConsumer consumerChest = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/dieselsuit_chest.png")));
            this.body.render(poseStack, consumerChest, packedLight, packedOverlay, scale);

            VertexConsumer consumerArm = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/dieselsuit_arm.png")));
            this.leftArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.DIESELSUIT_LEGGINGS.get()) {
            poseStack.translate(-0.5D, -1.75D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/dieselsuit_leg.png")));
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.DIESELSUIT_BOOTS.get()) {
            poseStack.translate(-0.5D, -1.9D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/dieselsuit_leg.png")));
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}