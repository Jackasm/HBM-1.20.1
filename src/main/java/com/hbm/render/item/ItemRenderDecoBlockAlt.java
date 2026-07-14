package com.hbm.render.item;

import com.hbm.items.ModItems;
import com.hbm.render.model.ModelGun;
import com.hbm.render.model.ModelStatue;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class ItemRenderDecoBlockAlt extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/models/model_statue.png");
    private static final ResourceLocation GUN_TEXTURE = ResLocation(RefStrings.MODID, "textures/models/model_gun.png");

    private final ModelStatue model;
    private final ModelGun gun;

    public ItemRenderDecoBlockAlt() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.model = new ModelStatue();
        this.gun = new ModelGun();
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext transformType,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);

        // Базовый масштаб для инвентаря
        if (transformType == ItemDisplayContext.GUI) {
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.mulPose(Axis.XP.rotationDegrees(20));
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
        } else if (transformType == ItemDisplayContext.GROUND) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.scale(0.6F, 0.6F, 0.6F);
        } else {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }

        // Рендерим статую
        VertexConsumer statueConsumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        model.renderToBuffer(poseStack, statueConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        // Определяем тип предмета по NBT или по имени
        boolean hasWatch = false;
        boolean hasGun = false;

        // Проверяем NBT или метаданные предмета
        if (stack.hasTag()) {
            var tag = stack.getTag();
            if (tag != null) {
                hasWatch = tag.getBoolean("hasWatch");
                hasGun = tag.getBoolean("hasGun");
            }
        }

        float g = 0.0625F;
        float q = g * 2 + 0.0625F / 3;

        // Рендерим часы если нужно
        if (hasWatch) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -2 * g, q);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            renderWatch(poseStack, buffer, packedLight, packedOverlay, g);
            poseStack.popPose();
        }

        // Рендерим пистолет если нужно
        if (hasGun) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 2 * g, -q);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(-g * 20, g * 4, g * 11);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-20));

            VertexConsumer gunConsumer = buffer.getBuffer(RenderType.entityCutout(GUN_TEXTURE));
            gun.renderToBuffer(poseStack, gunConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void renderWatch(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, float g) {
        ItemStack watchStack = new ItemStack(ModItems.WATCH.get());
        Minecraft mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(0.0F, -2 * g, 0.0F);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        itemRenderer.renderStatic(
                watchStack,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                buffer,
                mc.level,
                0
        );
        poseStack.popPose();
    }
}