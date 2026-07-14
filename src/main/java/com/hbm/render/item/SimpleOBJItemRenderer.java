package com.hbm.render.item;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SimpleOBJItemRenderer extends BlockEntityWithoutLevelRenderer {
    public SimpleOBJItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        Item item = stack.getItem();

        applyTransformations(item, context, poseStack);
        renderItemModel(item, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void applyTransformations(Item item, ItemDisplayContext context, PoseStack poseStack) {
        boolean isGrate = item == ModBlocks.STEEL_GRATE.get().asItem() ||
                item == ModBlocks.STEEL_GRATE_WIDE.get().asItem();
        boolean isAnvil = isAnvilItem(item);
        boolean isBarrel = isBarrelItem(item);
        boolean isSpotlight = isSpotlightItem(item);
        boolean isFloodlight = item == ModBlocks.FLOODLIGHT.get().asItem();

        switch (context) {
            case GUI:
                if (isGrate) {
                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.scale(0.9f, 0.9f, 0.9f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                } else if (isAnvil) {
                    poseStack.translate(0.5, 0.2, 0.5);
                    poseStack.scale(0.6f, 0.6f, 0.6f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(15));
                    poseStack.mulPose(Axis.YP.rotationDegrees(45));
                } else if (isBarrel) {
                    poseStack.translate(0.5, 0.2, 0.5);
                    poseStack.scale(0.6f, 0.6f, 0.6f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(15));
                    poseStack.mulPose(Axis.YP.rotationDegrees(45));
                } else if (item == ModBlocks.STEEL_POLES.get().asItem()) {
                    poseStack.translate(0.4, 0.15, 0);
                    poseStack.scale(0.7f, 0.7f, 0.7f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(15));
                    poseStack.mulPose(Axis.YP.rotationDegrees(45));
                } else if (item == ModBlocks.TAPE_RECORDER.get().asItem()) {
                    poseStack.translate(0.5, 0.15, 0);
                    poseStack.scale(0.7f, 0.7f, 0.7f);
                    poseStack.mulPose(Axis.YN.rotationDegrees(90));
                } else if (isSpotlight) {
                    poseStack.translate(0.4, 0.5, 0.5);
                    poseStack.scale(1f, 1f, 1f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(15));
                    poseStack.mulPose(Axis.YP.rotationDegrees(-45));

                } else if (isFloodlight) {
                    poseStack.translate(0.5, 0.15, 0.5);
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(15));
                    poseStack.mulPose(Axis.YP.rotationDegrees(45));

                } else {
                    poseStack.translate(0.5, 0.15, 0.5);
                    poseStack.scale(0.7f, 0.7f, 0.7f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(15));
                    poseStack.mulPose(Axis.YP.rotationDegrees(45));
                }
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
                if (isAnvil) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(45));
                }
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                if (isAnvil || isBarrel) {
                    poseStack.scale(0.3f, 0.3f, 0.3f);
                } else {
                    poseStack.scale(0.35f, 0.35f, 0.35f);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.75f, 0.75f, 0.75f);
        }
    }

    private void renderItemModel(Item item, PoseStack poseStack, MultiBufferSource buffer,
                                 int packedLight, int packedOverlay) {

        ResourceLocation texture = getItemTexture(item);

        if (isAnvilItem(item)) {
            HBMResourceManager.anvil.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (isBarrelItem(item)) {
            HBMResourceManager.barrel.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.STEEL_BEAM.get().asItem()) {
            HBMResourceManager.steel_beam.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.STEEL_SCAFFOLD.get().asItem()) {
            HBMResourceManager.steel_scaffold.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.STEEL_GRATE.get().asItem()) {
            HBMResourceManager.steel_grate.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.STEEL_GRATE_WIDE.get().asItem()) {
            HBMResourceManager.steel_grate_wide.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.STEEL_POLES.get().asItem()) {
            HBMResourceManager.pole.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.TAPE_RECORDER.get().asItem()) {
            HBMResourceManager.taperecorder.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.DECO_CRT.get().asItem()) {
                HBMResourceManager.deco_crt.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (isSpotlightItem(item)) {
            HFRWavefrontObject model = getSpotlightModel(item);
            texture = getSpotlightItemTexture(item);

            // Для FLUORESCENT в инвентаре рендерим только серединку
            if (item == ModBlocks.SPOTLIGHT_FLUORO.get().asItem() ||
                    item == ModBlocks.SPOTLIGHT_FLUORO_OFF.get().asItem()) {
                model.renderPart(poseStack, buffer, "FluoroMid", texture, packedLight, packedOverlay);
            } else {
                model.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
            }
        } else if (item == ModBlocks.FLOODLIGHT.get().asItem()) {
            HBMResourceManager.floodlight.renderAll(poseStack, buffer, HBMResourceManager.floodlight_tex, packedLight, packedOverlay);
        }
    }

    private boolean isSpotlightItem(Item item) {
        return item == ModBlocks.SPOTLIGHT_INCANDESCENT.get().asItem() ||
                item == ModBlocks.SPOTLIGHT_INCANDESCENT_OFF.get().asItem() ||
                item == ModBlocks.SPOTLIGHT_FLUORO.get().asItem() ||
                item == ModBlocks.SPOTLIGHT_FLUORO_OFF.get().asItem() ||
                item == ModBlocks.SPOTLIGHT_HALOGEN.get().asItem() ||
                item == ModBlocks.SPOTLIGHT_HALOGEN_OFF.get().asItem();
    }

    private HFRWavefrontObject getSpotlightModel(Item item) {
        if (item == ModBlocks.SPOTLIGHT_FLUORO.get().asItem() || item == ModBlocks.SPOTLIGHT_FLUORO_OFF.get().asItem()) {
            return HBMResourceManager.fluorescent_lamp;
        } else if (item == ModBlocks.SPOTLIGHT_HALOGEN.get().asItem() || item == ModBlocks.SPOTLIGHT_HALOGEN_OFF.get().asItem()) {
            return HBMResourceManager.flood_lamp;
        }
        return HBMResourceManager.cage_lamp;
    }

    private ResourceLocation getSpotlightItemTexture(Item item) {
        if (item == ModBlocks.SPOTLIGHT_INCANDESCENT.get().asItem()) return HBMResourceManager.spotlight_incandescent_tex;
        if (item == ModBlocks.SPOTLIGHT_INCANDESCENT_OFF.get().asItem()) return HBMResourceManager.spotlight_incandescent_off_tex;
        if (item == ModBlocks.SPOTLIGHT_FLUORO.get().asItem()) return HBMResourceManager.fluorescent_lamp_tex;
        if (item == ModBlocks.SPOTLIGHT_FLUORO_OFF.get().asItem()) return HBMResourceManager.fluorescent_lamp_off_tex;
        if (item == ModBlocks.SPOTLIGHT_HALOGEN.get().asItem()) return HBMResourceManager.flood_lamp_tex;
        if (item == ModBlocks.SPOTLIGHT_HALOGEN_OFF.get().asItem()) return HBMResourceManager.flood_lamp_off_tex;
        return HBMResourceManager.spotlight_incandescent_tex;
    }

    private ResourceLocation getItemTexture(Item item) {
        if (item == ModBlocks.ANVIL_IRON.get().asItem()) {
            return HBMResourceManager.anvil_iron_tex;
        } else if (item == ModBlocks.ANVIL_LEAD.get().asItem()) {
            return HBMResourceManager.anvil_lead_tex;
        } else if (item == ModBlocks.ANVIL_STEEL.get().asItem()) {
            return HBMResourceManager.anvil_steel_tex;
        } else if (item == ModBlocks.ANVIL_ARSENIC_BRONZE.get().asItem()) {
            return HBMResourceManager.anvil_arsenic_bronze_tex;
        } else if (item == ModBlocks.ANVIL_BISMUTH_BRONZE.get().asItem()) {
            return HBMResourceManager.anvil_bismuth_bronze_tex;
        } else if (item == ModBlocks.ANVIL_DESH.get().asItem()) {
            return HBMResourceManager.anvil_desh_tex;
        } else if (item == ModBlocks.ANVIL_DNT.get().asItem()) {
            return HBMResourceManager.anvil_dnt_tex;
        } else if (item == ModBlocks.ANVIL_FERROURANIUM.get().asItem()) {
            return HBMResourceManager.anvil_ferrouranium_tex;
        } else if (item == ModBlocks.ANVIL_MURKY.get().asItem()) {
            return HBMResourceManager.anvil_murky_tex;
        } else if (item == ModBlocks.ANVIL_OSMIRIDIUM.get().asItem()) {
            return HBMResourceManager.anvil_osmiridium_tex;
        } else if (item == ModBlocks.ANVIL_SATURNITE.get().asItem()) {
            return HBMResourceManager.anvil_saturnite_tex;
        } else if (item == ModBlocks.ANVIL_SCHRABIDATE.get().asItem()) {
            return HBMResourceManager.anvil_schrabidate_tex;
        } else if (item == ModBlocks.RED_BARREL.get().asItem()) {
            return HBMResourceManager.barrel_red_tex;
        } else if (item == ModBlocks.PINK_BARREL.get().asItem()) {
            return HBMResourceManager.barrel_pink_tex;
        } else if (item == ModBlocks.YELLOW_BARREL.get().asItem()) {
            return HBMResourceManager.barrel_yellow_tex;
        } else if (item == ModBlocks.LOX_BARREL.get().asItem()) {
            return HBMResourceManager.barrel_lox_tex;
        } else if (item == ModBlocks.TAINT_BARREL.get().asItem()) {
            return HBMResourceManager.barrel_taint_tex;
        } else if (item == ModBlocks.STEEL_BEAM.get().asItem()) {
            return HBMResourceManager.steel_beam_tex;
        } else if (item == ModBlocks.STEEL_SCAFFOLD.get().asItem()) {
            return HBMResourceManager.steel_scaffold_tex;
        } else if (item == ModBlocks.STEEL_GRATE.get().asItem()) {
            return HBMResourceManager.steel_grate_tex;
        } else if (item == ModBlocks.STEEL_GRATE_WIDE.get().asItem()) {
            return HBMResourceManager.steel_grate_wide_tex;
        } else if (item == ModBlocks.STEEL_POLES.get().asItem()) {
            return HBMResourceManager.pole_tex;
        } else if (item == ModBlocks.TAPE_RECORDER.get().asItem()) {
            return HBMResourceManager.taperecorder_tex;
        } else if (item == ModBlocks.DECO_CRT.get().asItem()) {
            return HBMResourceManager.deco_crt_clean_tex;
        }

        return HBMResourceManager.anvil_iron_tex;
    }

    private boolean isAnvilItem(Item item) {
        return item == ModBlocks.ANVIL_IRON.get().asItem() ||
                item == ModBlocks.ANVIL_LEAD.get().asItem() ||
                item == ModBlocks.ANVIL_STEEL.get().asItem() ||
                item == ModBlocks.ANVIL_ARSENIC_BRONZE.get().asItem() ||
                item == ModBlocks.ANVIL_BISMUTH_BRONZE.get().asItem() ||
                item == ModBlocks.ANVIL_DESH.get().asItem() ||
                item == ModBlocks.ANVIL_DNT.get().asItem() ||
                item == ModBlocks.ANVIL_FERROURANIUM.get().asItem() ||
                item == ModBlocks.ANVIL_MURKY.get().asItem() ||
                item == ModBlocks.ANVIL_OSMIRIDIUM.get().asItem() ||
                item == ModBlocks.ANVIL_SATURNITE.get().asItem() ||
                item == ModBlocks.ANVIL_SCHRABIDATE.get().asItem();
    }

    private boolean isBarrelItem(Item item) {
        return item == ModBlocks.RED_BARREL.get().asItem() ||
                item == ModBlocks.PINK_BARREL.get().asItem() ||
                item == ModBlocks.YELLOW_BARREL.get().asItem() ||
                item == ModBlocks.LOX_BARREL.get().asItem() ||
                item == ModBlocks.TAINT_BARREL.get().asItem();
    }
}