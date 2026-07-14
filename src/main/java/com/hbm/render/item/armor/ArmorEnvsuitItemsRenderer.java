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
public class ArmorEnvsuitItemsRenderer extends BlockEntityWithoutLevelRenderer {

    public ModelRendererObj head;
    public ModelRendererObj lamps;
    public ModelRendererObj body;
    public ModelRendererObj leftArm;
    public ModelRendererObj rightArm;
    public ModelRendererObj leftLeg;
    public ModelRendererObj rightLeg;
    public ModelRendererObj leftFoot;
    public ModelRendererObj rightFoot;

    public ArmorEnvsuitItemsRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.head = new ModelRendererObj(HBMResourceManager.armor_envsuit, "Helmet");
        this.lamps = new ModelRendererObj(HBMResourceManager.armor_envsuit, "Lamps");
        this.body = new ModelRendererObj(HBMResourceManager.armor_envsuit, "Chest");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_envsuit, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_envsuit, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_envsuit, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_envsuit, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_envsuit, "LeftFoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_envsuit, "RightFoot");
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        float scale = 0.0625F;

        // Поворачиваем модель
        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        Item item = stack.getItem();

        // Копируем повороты головы для ламп
        this.lamps.rotationPointX = this.head.rotationPointX;
        this.lamps.rotationPointY = this.head.rotationPointY;
        this.lamps.rotationPointZ = this.head.rotationPointZ;
        this.lamps.rotateAngleX = this.head.rotateAngleX;
        this.lamps.rotateAngleY = this.head.rotateAngleY;
        this.lamps.rotateAngleZ = this.head.rotateAngleZ;

        if (item == ModArmorItems.ENVSUIT_HELMET.get()) {
            poseStack.translate(0.5D, -0.2D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.lamps.render(poseStack, consumer, 0xF000F0, packedOverlay, scale);

        } else if (item == ModArmorItems.ENVSUIT_CHESTPLATE.get()) {
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.translate(0.625D, -0.95D, 0.0D);

            VertexConsumer consumerChest = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_chest.png")));
            this.body.render(poseStack, consumerChest, packedLight, packedOverlay, scale);

            VertexConsumer consumerArm = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_arm.png")));
            this.leftArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.ENVSUIT_LEGGINGS.get()) {
            poseStack.translate(0.5D, -1.55D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_leg.png")));
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.ENVSUIT_BOOTS.get()) {
            poseStack.translate(0.5D, -1.8D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/envsuit_leg.png")));
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}