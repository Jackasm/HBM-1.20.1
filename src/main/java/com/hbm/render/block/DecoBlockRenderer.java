package com.hbm.render.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.deco.DecoPoleSatelliteReceiver;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.model.*;
import com.hbm.tileentity.machine.TileEntityRadiobox;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class DecoBlockRenderer implements BlockEntityRenderer<BlockEntity> {

    private static final ResourceLocation TEXTURE_STEEL_ROOF = ResLocation(RefStrings.MODID, "textures/block/steel_roof.png");
    private static final ResourceLocation TEXTURE_STEEL_WALL = ResLocation(RefStrings.MODID, "textures/block/steel_wall.png");
    private static final ResourceLocation TEXTURE_BROADCASTER = ResLocation(RefStrings.MODID, "textures/block/model_broadcaster.png");
    private static final ResourceLocation TEXTURE_RADIO = ResLocation(RefStrings.MODID, "textures/block/model_radio.png");
    private static final ResourceLocation TEXTURE_RADIO_RECEIVER = ResLocation(RefStrings.MODID, "textures/block/model_radio_receiver.png");
    private static final ResourceLocation TEXTURE_POLE_TOP = ResLocation(RefStrings.MODID, "textures/block/deco/pole_top.png");
    private static final ResourceLocation TEXTURE_POLE_SATELLITE_RECEIVER = ResLocation(RefStrings.MODID, "textures/block/deco/pole_satellite_receiver.png");
    private static final ResourceLocation TEXTURE_GEIGER = ResLocation(RefStrings.MODID, "textures/block/geiger.png");


    private final ModelSteelRoof modelSteelRoof;
    private final ModelSteelWall modelSteelWall;
    private final ModelSteelCorner modelSteelCorner;
    private final ModelBroadcaster modelBroadcaster;
    private final ModelRadio modelRadio;
    private final ModelSatelliteReceiver modelSatelliteReceiver;
    private final ModelPoleTop modelPoleTop;
    private final ModelGeiger modelGeiger;

    public DecoBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.modelSteelRoof = new ModelSteelRoof();
        this.modelSteelWall = new ModelSteelWall();
        this.modelSteelCorner = new ModelSteelCorner();
        this.modelBroadcaster = new ModelBroadcaster();
        this.modelRadio = new ModelRadio();
        this.modelPoleTop = new ModelPoleTop();
        this.modelGeiger = new ModelGeiger();
        this.modelSatelliteReceiver = new ModelSatelliteReceiver();
    }

    @Override
    public void render(BlockEntity tileentity, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = tileentity.getBlockState();
        Block b = state.getBlock();

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        if (b == ModBlocks.BROADCASTER_PC.get()) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            float yaw = facing.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            renderModel(poseStack, buffer, packedLight, packedOverlay, modelBroadcaster, TEXTURE_BROADCASTER);
        }

        if (b == ModBlocks.RADIOREC.get()) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            float yaw = facing.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            renderModel(poseStack, buffer, packedLight, packedOverlay, modelBroadcaster, TEXTURE_RADIO_RECEIVER);
        }
        if (b == ModBlocks.BOAT.get()) {
            renderBoat(poseStack, buffer, packedLight, packedOverlay);
        }

        if (b == ModBlocks.POLE_TOP.get()) {
            renderModel(poseStack, buffer, packedLight, packedOverlay, modelPoleTop, TEXTURE_POLE_TOP);
        }
        if (b == ModBlocks.STEEL_ROOF.get()) {
            renderModel(poseStack, buffer, packedLight, packedOverlay, modelSteelRoof, TEXTURE_STEEL_ROOF);
        }
        if (b == ModBlocks.STEEL_WALL.get()) {
            poseStack.translate(0, 1.0, 0);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            switch (facing) {
                case EAST:
                    poseStack.translate(0.0625, 0, 0);
                    break;
                case WEST:
                    poseStack.translate(-0.0625, 0, 0);
                    break;
                case SOUTH:
                    poseStack.translate(0, 0, -0.0625);
                    break;
                case NORTH:
                    poseStack.translate(0, 0, 0.0625);
                    break;
            }
            float yaw = facing.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            renderModel(poseStack, buffer, packedLight, packedOverlay, modelSteelWall, TEXTURE_STEEL_WALL);
        }

        if (b == ModBlocks.STEEL_CORNER.get()) {
            poseStack.translate(0, 1.0, 0);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            switch (facing) {
                case EAST:
                    poseStack.translate(0.0625, 0, 0);
                    break;
                case WEST:
                    poseStack.translate(-0.0625, 0, 0);
                    break;
                case SOUTH:
                    poseStack.translate(0, 0, -0.0625);
                    break;
                case NORTH:
                    poseStack.translate(0, 0, 0.0625);
                    break;
            }
            float yaw = facing.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            renderModel(poseStack, buffer, packedLight, packedOverlay, modelSteelCorner, TEXTURE_STEEL_WALL);
        }
        if (b == ModBlocks.POLE_SATELLITE_RECEIVER.get()) {
            Direction facing = state.getValue(DecoPoleSatelliteReceiver.FACING).getOpposite();
            float rotation = facing.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            renderModel(poseStack, buffer, packedLight, packedOverlay, modelSatelliteReceiver, TEXTURE_POLE_SATELLITE_RECEIVER);
        }

        if (b == ModBlocks.BOXCAR.get()) {
            renderBoxcar(poseStack, buffer, packedLight, packedOverlay, state);
        }


        if (b == ModBlocks.RADIOBOX.get()) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.mulPose(Axis.YP.rotationDegrees(getRotationFromFacing(facing)));
            poseStack.translate(0, 0, 1);
            BlockEntity te = tileentity;
            int power = 20;
            if (te instanceof TileEntityRadiobox radiobox) {
                // Проверяем, есть ли энергия или бесконечный режим
                boolean hasPower = radiobox.infinite || radiobox.getPower() > 0;
                power = hasPower ? 160 : 20;
            }

            renderModel(poseStack, buffer, packedLight, packedOverlay, modelRadio, TEXTURE_RADIO);
        }

        if (b == ModBlocks.SAT_RADAR.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, state, "radar");
        }

        if (b == ModBlocks.SAT_RESONATOR.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, state, "resonator");
        }

        if (b == ModBlocks.SAT_SCANNER.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, state, "scanner");
        }

        if (b == ModBlocks.SAT_MAPPER.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, state, "mapper");
        }

        if (b == ModBlocks.SAT_LASER.get()) {
            renderSatellite(poseStack, buffer, packedLight, packedOverlay, state, "laser");
        }

        if (b == ModBlocks.SAT_FOEQ.get()) {
            renderSatelliteSimple(poseStack, buffer, packedLight, packedOverlay, state, HBMResourceManager.sat_foeq_tex, HBMResourceManager.sat_foeq);
        }

        if (b == ModBlocks.SAT_DOCK.get()) {
            renderSatelliteDock(poseStack, buffer, packedLight, packedOverlay);
        }

        if (b == ModBlocks.GEIGER.get()) {
            renderGeiger(poseStack, buffer, packedLight, packedOverlay, modelGeiger, TEXTURE_GEIGER);
        }

        poseStack.popPose();
    }

    private void renderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
                             Model model, ResourceLocation texture) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private float getRotationFromFacing(Direction facing) {
        return switch (facing) {
            case NORTH -> 180;
            case EAST -> 270;
            case SOUTH -> 0;
            case WEST -> 90;
            default -> 0;
        };
    }

    private void renderGeiger(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
                              ModelGeiger model, ResourceLocation texture) {
        renderModel(poseStack, buffer, packedLight, packedOverlay, model, texture);
    }

    private void renderBoat(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, 0, -1.5);
        poseStack.translate(0, 0.5, 0);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.boat_tex));
        HBMResourceManager.boat.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
    }


    private void renderBoxcar(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, -1.5, 0);

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        poseStack.mulPose(Axis.YP.rotationDegrees(getBoxcarRotation(facing)));

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.boxcar_tex));
        HBMResourceManager.boxcar.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    private float getBoxcarRotation(Direction facing) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST -> 270;
            case SOUTH -> 180;
            case WEST -> 90;
            default -> 0;
        };
    }

    private void renderSatellite(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
                                 BlockState state, String type) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, -1.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        poseStack.mulPose(Axis.YP.rotationDegrees(getSatelliteRotation(facing)));

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.sat_base_tex));
        HBMResourceManager.sat_base.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);

        ResourceLocation tex = switch (type) {
            case "radar" -> HBMResourceManager.sat_radar_tex;
            case "resonator" -> HBMResourceManager.sat_resonator_tex;
            case "scanner" -> HBMResourceManager.sat_scanner_tex;
            case "mapper" -> HBMResourceManager.sat_mapper_tex;
            case "laser" -> HBMResourceManager.sat_laser_tex;
            default -> null;
        };

        HFRWavefrontObject sat = switch (type) {
            case "radar" -> HBMResourceManager.sat_radar;
            case "resonator" -> HBMResourceManager.sat_resonator;
            case "scanner" -> HBMResourceManager.sat_scanner;
            case "mapper" -> HBMResourceManager.sat_mapper;
            case "laser" -> HBMResourceManager.sat_laser;
            default -> null;
        };

        if (tex != null) {
            vertexConsumer = buffer.getBuffer(RenderType.entityCutout(tex));
            sat.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
    }


    private void renderSatelliteSimple(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
                                       BlockState state, ResourceLocation tex, HFRWavefrontObject model) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, -1.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        poseStack.mulPose(Axis.YP.rotationDegrees(getSatelliteRotation(facing)));

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(tex));
        model.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    private void renderSatelliteDock(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, -1.5, 0);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.sat_dock_tex));
        HBMResourceManager.sat_dock.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    private float getSatelliteRotation(Direction facing) {
        return switch (facing) {
            case NORTH -> 270;
            case EAST -> 180;
            case SOUTH -> 90;
            case WEST -> 0;
            default -> 0;
        };
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}