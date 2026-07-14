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
public class ArmorRPAItemsRenderer extends BlockEntityWithoutLevelRenderer {

    public ModelRendererObj head;
    public ModelRendererObj body;
    public ModelRendererObj fan;
    public ModelRendererObj glow;
    public ModelRendererObj leftArm;
    public ModelRendererObj rightArm;
    public ModelRendererObj leftLeg;
    public ModelRendererObj rightLeg;
    public ModelRendererObj leftFoot;
    public ModelRendererObj rightFoot;

    public ArmorRPAItemsRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.head = new ModelRendererObj(HBMResourceManager.armor_remnant, "Head");
        this.body = new ModelRendererObj(HBMResourceManager.armor_remnant, "Body");
        this.fan = new ModelRendererObj(HBMResourceManager.armor_remnant, "Fan");
        this.glow = new ModelRendererObj(HBMResourceManager.armor_remnant, "Glow");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_remnant, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_remnant, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_remnant, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_remnant, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_remnant, "LeftBoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_remnant, "RightBoot");
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        float scale = 0.0625F;

        // Поворачиваем модель
        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        Item item = stack.getItem();

        // Копируем повороты тела для свечения
        this.glow.rotationPointX = this.body.rotationPointX;
        this.glow.rotationPointY = this.body.rotationPointY;
        this.glow.rotationPointZ = this.body.rotationPointZ;
        this.glow.rotateAngleX = this.body.rotateAngleX;
        this.glow.rotateAngleY = this.body.rotateAngleY;
        this.glow.rotateAngleZ = this.body.rotateAngleZ;

        if (item == ModArmorItems.RPA_HELMET.get()) {
            poseStack.translate(0.5D, -0.2D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/rpa_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.RPA_CHESTPLATE.get()) {
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.translate(0.6D, -0.9D, 0.0D);

            VertexConsumer consumerArm = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/rpa_arm.png")));
            this.leftArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);

            VertexConsumer consumerChest = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/rpa_chest.png")));
            this.body.render(poseStack, consumerChest, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.RPA_LEGGINGS.get()) {
            poseStack.translate(0.5D, -1.45D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/rpa_leg.png")));
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.RPA_BOOTS.get()) {
            poseStack.translate(0.5D, -1.8D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/rpa_leg.png")));
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}