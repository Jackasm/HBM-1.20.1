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
public class ArmorTrenchmasterItemsRenderer extends BlockEntityWithoutLevelRenderer {

    public ModelRendererObj head;
    public ModelRendererObj light;
    public ModelRendererObj body;
    public ModelRendererObj leftArm;
    public ModelRendererObj rightArm;
    public ModelRendererObj leftLeg;
    public ModelRendererObj rightLeg;
    public ModelRendererObj leftFoot;
    public ModelRendererObj rightFoot;

    public ArmorTrenchmasterItemsRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.head = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "Helmet");
        this.light = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "Light");
        this.body = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "Chest");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "LeftBoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_trenchmaster, "RightBoot");
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        float scale = 0.0625F;

        // Поворачиваем модель
        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        Item item = stack.getItem();

        // Копируем повороты головы для света
        this.light.rotationPointX = this.head.rotationPointX;
        this.light.rotationPointY = this.head.rotationPointY;
        this.light.rotationPointZ = this.head.rotationPointZ;
        this.light.rotateAngleX = this.head.rotateAngleX;
        this.light.rotateAngleY = this.head.rotateAngleY;
        this.light.rotateAngleZ = this.head.rotateAngleZ;

        if (item == ModArmorItems.TRENCHMASTER_HELMET.get()) {
            poseStack.translate(0.5D, -0.25D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.light.render(poseStack, consumer, 0xF000F0, packedOverlay, scale);

        } else if (item == ModArmorItems.TRENCHMASTER_CHESTPLATE.get()) {
            poseStack.scale(0.6F, 0.55F, 0.6F);
            poseStack.translate(0.8D, -1.45D, 0.0D);

            VertexConsumer consumerChest = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_chest.png")));
            this.body.render(poseStack, consumerChest, packedLight, packedOverlay, scale);


            VertexConsumer consumerArm = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_arm.png")));
            this.leftArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.TRENCHMASTER_LEGGINGS.get()) {
            poseStack.translate(0.5D, -1.5D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_leg.png")));
            poseStack.translate(-0.01, 0, 0);
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            poseStack.translate(0.02, 0, 0);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.TRENCHMASTER_BOOTS.get()) {
            poseStack.translate(0.5D, -1.8D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/trenchmaster_leg.png")));
            poseStack.translate(-0.01, 0, 0);
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            poseStack.translate(0.02, 0, 0);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}