package com.hbm.render.tileentity;

import com.hbm.blocks.bomb.LaunchTable;
import com.hbm.items.weapon.ItemCustomMissilePart.PartSize;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.util.MissileMultipart;
import com.hbm.render.util.MissilePronter;
import com.hbm.tileentity.bomb.TileEntityLaunchTable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class RenderLaunchTable implements BlockEntityRenderer<TileEntityLaunchTable> {

    public RenderLaunchTable(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityLaunchTable launcher, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Поворот в зависимости от метаданных (направления)
        Direction facing = launcher.getBlockState().getValue(LaunchTable.FACING);
        switch (facing) {
            case EAST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
            case WEST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270));
            case SOUTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
            default -> {}
        }

        // База
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.launch_table_base_tex));
        HBMResourceManager.launch_table_base.renderAll(poseStack, consumer, packedLight, packedOverlay);

        // Платформа в зависимости от размера
        if (launcher.padSize == PartSize.SIZE_10 || launcher.padSize == PartSize.SIZE_15) {
            consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.launch_table_small_pad_tex));
            HBMResourceManager.launch_table_small_pad.renderAll(poseStack, consumer, packedLight, packedOverlay);
        } else if (launcher.padSize == PartSize.SIZE_20) {
            consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.launch_table_large_pad_tex));
            HBMResourceManager.launch_table_large_pad.renderAll(poseStack, consumer, packedLight, packedOverlay);
        }

        // Scaffolding (башня)
        renderScaffolding(launcher, poseStack, buffer, packedLight, packedOverlay);

        // Ракета
        poseStack.translate(0, 2.0625, 0);
        if (launcher.load != null && launcher.load.fuselage != null &&
                launcher.load.fuselage.top == launcher.padSize) {
            MissileMultipart missile = MissileMultipart.loadFromStruct(launcher.load);
            if (missile != null) {
                // Масштаб для отображения в мире
                float scale = 0.75F;
                poseStack.pushPose();
                poseStack.scale(scale, scale, scale);
                MissilePronter.prontMissile(missile, buffer, poseStack, packedLight);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private void renderScaffolding(TileEntityLaunchTable launcher, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight, int packedOverlay) {

        // Определяем высоту ракеты
        int height = 0;
        if (launcher.load != null && launcher.load.fuselage != null) {
            MissileMultipart mp = MissileMultipart.loadFromStruct(launcher.load);
            if (mp != null && mp.fuselage != null) {
                height = (int) mp.getHeight();
            }
        }

        int scaffoldHeight = (int) (height * 0.75);

        // Выбор текстур и моделей в зависимости от размера
        boolean isSmall = launcher.padSize == PartSize.SIZE_10;
        HFRWavefrontObject baseModel = isSmall ? HBMResourceManager.launch_table_small_scaffold_base : HBMResourceManager.launch_table_large_scaffold_base;
        HFRWavefrontObject connectorModel = isSmall ? HBMResourceManager.launch_table_small_scaffold_connector : HBMResourceManager.launch_table_large_scaffold_connector;
        HFRWavefrontObject emptyModel = isSmall ? HBMResourceManager.launch_table_small_scaffold_empty : HBMResourceManager.launch_table_large_scaffold_empty;
        var baseTex = isSmall ? HBMResourceManager.launch_table_small_scaffold_base_tex : HBMResourceManager.launch_table_large_scaffold_base_tex;
        var connectorTex = isSmall ? HBMResourceManager.launch_table_small_scaffold_connector_tex : HBMResourceManager.launch_table_large_scaffold_connector_tex;

        poseStack.pushPose();

        // Смещение для маленькой платформы
        if (isSmall) {
            poseStack.translate(0, 0, -1);
        }
        poseStack.translate(0, 1, 3.5);

        for (int i = 0; i < height + 1; i++) {
            VertexConsumer consumer;

            if (i < scaffoldHeight) {
                consumer = buffer.getBuffer(RenderType.entityCutout(baseTex));
                baseModel.renderAll(poseStack, consumer, packedLight, packedOverlay);
            } else if (i > scaffoldHeight) {
                consumer = buffer.getBuffer(RenderType.entityCutout(baseTex));
                emptyModel.renderAll(poseStack, consumer, packedLight, packedOverlay);
            } else {
                // На уровне соединения — проверяем, совпадает ли размер
                boolean valid = launcher.load != null &&
                        launcher.load.fuselage != null &&
                        launcher.load.fuselage.top == launcher.padSize;
                if (valid) {
                    consumer = buffer.getBuffer(RenderType.entityCutout(connectorTex));
                    connectorModel.renderAll(poseStack, consumer, packedLight, packedOverlay);
                } else {
                    consumer = buffer.getBuffer(RenderType.entityCutout(baseTex));
                    baseModel.renderAll(poseStack, consumer, packedLight, packedOverlay);
                }
            }

            poseStack.translate(0, 1, 0);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityLaunchTable launcher) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}