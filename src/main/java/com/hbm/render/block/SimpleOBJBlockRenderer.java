package com.hbm.render.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.deco.*;
import com.hbm.blocks.deco.DecoSteelPoles;
import com.hbm.blocks.deco.DecoTapeRecorder;
import com.hbm.blocks.generic.BlockDecoCRT;
import com.hbm.blocks.generic.RedBarrel;
import com.hbm.blocks.generic.YellowBarrel;
import com.hbm.blocks.machine.BlockFloodlight;
import com.hbm.blocks.machine.BlockSpotlight;
import com.hbm.blocks.machine.NTMAnvil;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import com.hbm.util.HBMEnums;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SimpleOBJBlockRenderer implements BlockEntityRenderer<TileEntitySimpleOBJ> {

    public SimpleOBJBlockRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntitySimpleOBJ tile, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Block block = tile.getBlockState().getBlock();

        if (block instanceof BlockSteelGrate) {
            renderGrate(tile, poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof BlockSteelBeam) {
            renderBeam(poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof BlockSteelScaffold) {
            renderScaffold(tile, poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof NTMAnvil) {
            renderAnvil(tile, poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof DecoSteelPoles) {
            renderSteelPoles(tile, poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof DecoTapeRecorder) {
            renderTapeRecorder(tile, poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof RedBarrel || block instanceof YellowBarrel) {
            renderBarrel(tile, poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof BlockSpotlight) {
            renderSpotlight(tile, poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof BlockFloodlight) {
            renderFloodlight(tile, poseStack, buffer, combinedLight, combinedOverlay);
        } else if (block instanceof BlockDecoCRT) {
            renderDecoCRT(tile, poseStack, buffer, combinedLight, combinedOverlay);
        }

        poseStack.popPose();
    }

    private void renderSpotlight(TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                                 int combinedLight, int combinedOverlay) {
        BlockState state = tile.getBlockState();
        Block block = state.getBlock();
        Level level = tile.getLevel();
        BlockPos pos = tile.getBlockPos();
        Direction facing = state.getValue(BlockSpotlight.FACING);
        Direction horizontal = state.getValue(BlockSpotlight.HORIZONTAL_FACING);
        HBMEnums.LightType type = state.getValue(BlockSpotlight.TYPE);
        boolean isOn = !state.getValue(BlockSpotlight.POWERED) && !state.getValue(BlockSpotlight.BROKEN);

        // Поворот в зависимости от facing
        float yRot;
        float zRot;


        if (facing.getAxis() == Direction.Axis.Y) {
            // Вертикальные лампы: поворот по горизонтали берём из HORIZONTAL_FACING
            yRot = switch (horizontal) {
                case NORTH -> 90f;
                case EAST -> 180f;
                case SOUTH -> 270f;
                default -> 0f;
            };
            zRot = (facing == Direction.UP) ? -90f : 90f;
        } else {
            // Горизонтальные лампы (на стенах)
            yRot = switch (facing) {
                case EAST -> 180f;
                case SOUTH -> 90f;
                case WEST -> 0f;
                default -> 270f;
            };
            zRot = 0f;
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zRot));

        // Сдвиг в зависимости от направления
        switch (facing) {
            case EAST, WEST, SOUTH, NORTH -> poseStack.translate(-0.5, 0.5, 0);
            case UP -> poseStack.translate(-1, 0, 0);
            case DOWN -> poseStack.translate(0, 0, 0);
        }

        // Выбираем модель и текстуру
        HFRWavefrontObject model = switch (type) {
            case FLUORESCENT -> HBMResourceManager.fluorescent_lamp;
            case HALOGEN -> HBMResourceManager.flood_lamp;
            default -> HBMResourceManager.cage_lamp;
        };

        ResourceLocation texture = switch (type) {
            case FLUORESCENT -> isOn ? HBMResourceManager.fluorescent_lamp_tex : HBMResourceManager.fluorescent_lamp_off_tex;
            case HALOGEN -> isOn ? HBMResourceManager.flood_lamp_tex : HBMResourceManager.flood_lamp_off_tex;
            default -> isOn ? HBMResourceManager.spotlight_incandescent_tex : HBMResourceManager.spotlight_incandescent_off_tex;
        };

        if (type == HBMEnums.LightType.FLUORESCENT && block instanceof BlockSpotlight spotlight) {
            String partName = spotlight.getModelPartName(state, level, pos);
            if (partName != null) {
                // Для правой CapSide доворачиваем модель на 180 градусов
                if (partName.equals("FluoroCap")) {
                    BlockSpotlight.CapSide side = spotlight.getCapSide(state, level, pos);
                    if (side == BlockSpotlight.CapSide.LEFT) {
                        poseStack.mulPose(Axis.XP.rotationDegrees(180));
                    }
                    if (horizontal.getAxis() == Direction.Axis.Z) poseStack.mulPose(Axis.XP.rotationDegrees(180));
                }
                model.renderPart(poseStack, buffer, partName, texture, combinedLight, combinedOverlay);
            } else {
                model.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
            }
        } else {
            model.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
        }
    }

    private void renderFloodlight(TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                                  int combinedLight, int combinedOverlay) {
        HBMResourceManager.floodlight.renderAll(poseStack, buffer, HBMResourceManager.floodlight_tex, combinedLight, combinedOverlay);
    }

    private void renderBarrel (TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                               int combinedLight, int combinedOverlay)
    {
        Block block = tile.getBlockState().getBlock();
        ResourceLocation texture;

        if (block == ModBlocks.RED_BARREL.get()) {
            texture = HBMResourceManager.barrel_red_tex;
        } else if (block == ModBlocks.PINK_BARREL.get()) {
            texture = HBMResourceManager.barrel_pink_tex;
        } else if (block == ModBlocks.YELLOW_BARREL.get()) {
            texture = HBMResourceManager.barrel_yellow_tex;
        } else if (block == ModBlocks.LOX_BARREL.get()) {
            texture = HBMResourceManager.barrel_lox_tex;
        } else if (block == ModBlocks.TAINT_BARREL.get()) {
            texture = HBMResourceManager.barrel_taint_tex;
        } else {
            return;
        }

        HBMResourceManager.barrel.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
    }

    private void renderSteelPoles (TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                                   int combinedLight, int combinedOverlay) {
        Direction facing = tile.getBlockState().getValue(DecoSteelPoles.FACING);
        float rotation = facing.toYRot();
        if (facing.getAxis() == Direction.Axis.Z) {
            rotation += 180;
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        ResourceLocation texture = HBMResourceManager.pole_tex;
        HBMResourceManager.pole.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
    }

    private void renderDecoCRT (TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                                int combinedLight, int combinedOverlay) {
        BlockState state = tile.getBlockState();
        int meta = state.getValue(BlockDecoCRT.META);
        int typeIndex = meta / 4; // 0=clean, 1=broken, 2=blinking, 3=bsod
        int rotationIndex = meta % 4; // 0-3 поворот

        // Выбираем текстуру в зависимости от типа
        ResourceLocation texture = switch (typeIndex) {
            case 1 -> HBMResourceManager.deco_crt_broken_tex;
            case 2 -> HBMResourceManager.deco_crt_blinking_tex;
            case 3 -> HBMResourceManager.deco_crt_bsod_tex;
            default -> HBMResourceManager.deco_crt_clean_tex;
        };

        // Поворот в зависимости от rotationIndex
        float rotation = switch (rotationIndex) {
            case 0 -> 90f;   // север
            case 1 -> 0f;    // восток
            case 2 -> 270f;  // юг
            case 3 -> 180f;  // запад
            default -> 0f;
        };

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // Рендерим модель CRT
        HBMResourceManager.deco_crt.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
    }

    private void renderTapeRecorder (TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                                     int combinedLight, int combinedOverlay) {
        Direction facing = tile.getBlockState().getValue(DecoTapeRecorder.FACING);
        float rotation = facing.toYRot() -90;
        if (facing.getAxis() == Direction.Axis.X) {
            rotation += 180;
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        ResourceLocation texture = HBMResourceManager.taperecorder_tex;
        HBMResourceManager.taperecorder.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
    }

    private void renderGrate(TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                             int combinedLight, int combinedOverlay) {
        int layer = tile.getBlockState().getValue(BlockSteelGrate.LAYER);
        float offsetY;
        ResourceLocation texture;

        if (tile.getBlockState().getBlock() instanceof BlockSteelGrateWide) {
            // Широкая решетка
            offsetY = BlockSteelGrateWide.getRenderYOffset(layer);
            poseStack.translate(0, offsetY, 0);
            texture = HBMResourceManager.steel_grate_wide_tex;
            HBMResourceManager.steel_grate_wide.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
        } else {
            // Обычная решетка
            offsetY = BlockSteelGrate.getRenderYOffset(layer);
            poseStack.translate(0, offsetY, 0);
            texture = HBMResourceManager.steel_grate_tex;
            HBMResourceManager.steel_grate.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
        }

    }

    private void renderBeam(PoseStack poseStack, MultiBufferSource buffer,
                            int combinedLight, int combinedOverlay) {
        ResourceLocation texture = HBMResourceManager.steel_beam_tex;
        HBMResourceManager.steel_beam.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
    }

    private void renderScaffold(TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                                int combinedLight, int combinedOverlay) {

        Direction facing = tile.getBlockState().getValue(BlockSteelScaffold.FACING);

        switch (facing) {
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            default -> {}
        }

        HBMResourceManager.steel_scaffold.renderAll(poseStack, buffer, HBMResourceManager.steel_scaffold_tex, combinedLight, combinedOverlay);
    }

    private void renderAnvil(TileEntitySimpleOBJ tile, PoseStack poseStack, MultiBufferSource buffer,
                             int combinedLight, int combinedOverlay) {

        Block block = tile.getBlockState().getBlock();
        ResourceLocation texture = getAnvilTexture(block);

        if (tile.getBlockState().hasProperty(NTMAnvil.FACING)) {
            Direction facing = tile.getBlockState().getValue(NTMAnvil.FACING);
            poseStack.mulPose(Axis.YP.rotationDegrees(90 - facing.toYRot()));
        }

        HBMResourceManager.anvil.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);
    }

    private ResourceLocation getAnvilTexture(Block block) {
        if (block == ModBlocks.ANVIL_IRON.get()) {
            return HBMResourceManager.anvil_iron_tex;
        } else if (block == ModBlocks.ANVIL_LEAD.get()) {
            return HBMResourceManager.anvil_lead_tex;
        } else if (block == ModBlocks.ANVIL_STEEL.get()) {
            return HBMResourceManager.anvil_steel_tex;
        } else if (block == ModBlocks.ANVIL_ARSENIC_BRONZE.get()) {
            return HBMResourceManager.anvil_arsenic_bronze_tex;
        } else if (block == ModBlocks.ANVIL_BISMUTH_BRONZE.get()) {
            return HBMResourceManager.anvil_bismuth_bronze_tex;
        } else if (block == ModBlocks.ANVIL_DESH.get()) {
            return HBMResourceManager.anvil_desh_tex;
        } else if (block == ModBlocks.ANVIL_DNT.get()) {
            return HBMResourceManager.anvil_dnt_tex;
        } else if (block == ModBlocks.ANVIL_FERROURANIUM.get()) {
            return HBMResourceManager.anvil_ferrouranium_tex;
        } else if (block == ModBlocks.ANVIL_MURKY.get()) {
            return HBMResourceManager.anvil_murky_tex;
        } else if (block == ModBlocks.ANVIL_OSMIRIDIUM.get()) {
            return HBMResourceManager.anvil_osmiridium_tex;
        } else if (block == ModBlocks.ANVIL_SATURNITE.get()) {
            return HBMResourceManager.anvil_saturnite_tex;
        } else if (block == ModBlocks.ANVIL_SCHRABIDATE.get()) {
            return HBMResourceManager.anvil_schrabidate_tex;
        }

        return HBMResourceManager.anvil_iron_tex;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntitySimpleOBJ blockEntity) {
        return true;
    }
}