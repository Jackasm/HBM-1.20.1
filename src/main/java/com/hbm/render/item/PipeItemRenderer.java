package com.hbm.render.item;

import com.hbm.blocks.generic.BlockPipe;
import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class PipeItemRenderer extends BlockEntityWithoutLevelRenderer {

    public PipeItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        applyTransformations(context, poseStack);

        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof BlockPipe pipe) {

            // Определяем модель и текстуры
            String modelName = getModelName(pipe);
            String sideTexture = pipe.getSideTexture();
            String topTexture = pipe.getTopTexture();

            // Рендерим в зависимости от типа трубы
            switch (pipe.getPipeType()) {
                case NORMAL:
                    HBMResourceManager.pipe.renderPart(poseStack, buffer, "Top",
                            ResLocation(RefStrings.MODID, "textures/block/" + topTexture + ".png"),
                            packedLight, packedOverlay);
                    HBMResourceManager.pipe.renderPart(poseStack, buffer, "Side",
                            ResLocation(RefStrings.MODID, "textures/block/" + sideTexture + ".png"),
                            packedLight, packedOverlay);
                    break;

                case RIM:
                    HBMResourceManager.pipe_rim.renderPart(poseStack, buffer, "Top",
                            ResLocation(RefStrings.MODID, "textures/block/" + topTexture + ".png"),
                            packedLight, packedOverlay);
                    HBMResourceManager.pipe_rim.renderPart(poseStack, buffer, "Side",
                            ResLocation(RefStrings.MODID, "textures/block/" + sideTexture + ".png"),
                            packedLight, packedOverlay);
                    break;

                case QUAD:
                    HBMResourceManager.pipe_quad.renderPart(poseStack, buffer, "Top",
                            ResLocation(RefStrings.MODID, "textures/block/" + topTexture + ".png"),
                            packedLight, packedOverlay);
                    HBMResourceManager.pipe_quad.renderPart(poseStack, buffer, "Side",
                            ResLocation(RefStrings.MODID, "textures/block/" + sideTexture + ".png"),
                            packedLight, packedOverlay);
                    break;

                case FRAMED:
                    HBMResourceManager.pipe_rim.renderPart(poseStack, buffer, "Top",
                            ResLocation(RefStrings.MODID, "textures/block/" + topTexture + ".png"),
                            packedLight, packedOverlay);
                    HBMResourceManager.pipe_rim.renderPart(poseStack, buffer, "Side",
                            ResLocation(RefStrings.MODID, "textures/block/" + sideTexture + ".png"),
                            packedLight, packedOverlay);
                    HBMResourceManager.pipe_frame.renderPart(poseStack, buffer, "Frame",
                            ResLocation(RefStrings.MODID, "textures/block/pipe_frame.png"),
                            packedLight, packedOverlay);
                    HBMResourceManager.pipe_frame.renderPart(poseStack, buffer, "Mesh",
                            ResLocation(RefStrings.MODID, "textures/block/pipe_mesh.png"),
                            packedLight, packedOverlay);
                    break;
            }
        }

        poseStack.popPose();
    }

    private String getModelName(BlockPipe pipe) {
        return switch (pipe.getPipeType()) {
            case RIM -> "pipe_rim";
            case QUAD -> "pipe_quad";
            case FRAMED -> "pipe_framed";
            default -> "pipe";
        };
    }

    private void applyTransformations(ItemDisplayContext context, PoseStack poseStack) {
        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case GROUND:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.75f, 0.75f, 0.75f);
        }
    }
}