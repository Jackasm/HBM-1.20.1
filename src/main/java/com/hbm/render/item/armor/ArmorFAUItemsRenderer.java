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
public class ArmorFAUItemsRenderer extends BlockEntityWithoutLevelRenderer {

    public ModelRendererObj head;
    public ModelRendererObj body;
    public ModelRendererObj cassette;
    public ModelRendererObj leftArm;
    public ModelRendererObj rightArm;
    public ModelRendererObj leftLeg;
    public ModelRendererObj rightLeg;
    public ModelRendererObj leftFoot;
    public ModelRendererObj rightFoot;

    public ArmorFAUItemsRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.head = new ModelRendererObj(HBMResourceManager.armor_fau, "Head");
        this.body = new ModelRendererObj(HBMResourceManager.armor_fau, "Body");
        this.cassette = new ModelRendererObj(HBMResourceManager.armor_fau, "Cassette");
        this.leftArm = new ModelRendererObj(HBMResourceManager.armor_fau, "LeftArm");
        this.rightArm = new ModelRendererObj(HBMResourceManager.armor_fau, "RightArm");
        this.leftLeg = new ModelRendererObj(HBMResourceManager.armor_fau, "LeftLeg");
        this.rightLeg = new ModelRendererObj(HBMResourceManager.armor_fau, "RightLeg");
        this.leftFoot = new ModelRendererObj(HBMResourceManager.armor_fau, "LeftBoot");
        this.rightFoot = new ModelRendererObj(HBMResourceManager.armor_fau, "RightBoot");
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        float scale = 0.05F;

        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        Item item = stack.getItem();

        // Копируем повороты тела для кассеты
        this.cassette.rotationPointX = this.body.rotationPointX;
        this.cassette.rotationPointY = this.body.rotationPointY;
        this.cassette.rotationPointZ = this.body.rotationPointZ;
        this.cassette.rotateAngleX = this.body.rotateAngleX;
        this.cassette.rotateAngleY = this.body.rotateAngleY;
        this.cassette.rotateAngleZ = this.body.rotateAngleZ;

        if (item == ModArmorItems.FAU_HELMET.get()) {
            poseStack.translate(0.5D, -0.15D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/fau_helmet.png")));
            this.head.render(poseStack, consumer, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.FAU_CHESTPLATE.get()) {
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.translate(0.6D, -0.9D, 0.0D);

            VertexConsumer consumerChest = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/fau_chest.png")));
            this.body.render(poseStack, consumerChest, packedLight, packedOverlay, scale);

            VertexConsumer consumerArm = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/fau_arm.png")));
            this.leftArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);
            this.rightArm.render(poseStack, consumerArm, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.FAU_LEGGINGS.get()) {
            poseStack.translate(0.5D, -1.3D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/fau_leg.png")));
            this.leftLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightLeg.render(poseStack, consumer, packedLight, packedOverlay, scale);

        } else if (item == ModArmorItems.FAU_BOOTS.get()) {
            poseStack.translate(0.5D, -1.5D, 0.0D);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(
                    ResLocation(RefStrings.MODID, "textures/models/armor/fau_leg.png")));
            this.leftFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
            this.rightFoot.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}
