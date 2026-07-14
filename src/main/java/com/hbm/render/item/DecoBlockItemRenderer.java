package com.hbm.render.item;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.model.ModelPoleTop;
import com.hbm.render.model.ModelSatelliteReceiver;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class DecoBlockItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEXTURE_POLE_TOP = ResLocation(MODID, "textures/block/deco/pole_top.png");
    private static final ResourceLocation TEXTURE_POLE_SATELLITE_RECEIVER = ResLocation(MODID, "textures/block/deco/pole_satellite_receiver.png");

    private final ModelPoleTop modelPoleTop;
    private final ModelSatelliteReceiver modelSatelliteReceiver;

    public DecoBlockItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.modelPoleTop = new ModelPoleTop();
        this.modelSatelliteReceiver = new ModelSatelliteReceiver();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Item item = stack.getItem();
        poseStack.pushPose();

        poseStack.translate(0, 1.5, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(35));

        // Настройка позиции в зависимости от контекста
        switch (displayContext) {
            case GUI:
                poseStack.translate(0.25, 0.5, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                break;
            case GROUND:
                poseStack.translate(0.5, 0.2, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.7, 0.4, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                break;
            default:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
        }

        // Рендерим модель в зависимости от блока
        if (item == ModBlocks.POLE_TOP.get().asItem()) {
            renderModel(poseStack, buffer, packedLight, packedOverlay, modelPoleTop, TEXTURE_POLE_TOP);
        } else if (item == ModBlocks.POLE_SATELLITE_RECEIVER.get().asItem()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            renderModel(poseStack, buffer, packedLight, packedOverlay, modelSatelliteReceiver, TEXTURE_POLE_SATELLITE_RECEIVER);
        }

        poseStack.popPose();
    }

    private void renderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
                             net.minecraft.client.model.Model model, ResourceLocation texture) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}