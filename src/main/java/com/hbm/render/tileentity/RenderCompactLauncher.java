package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.util.MissileMultipart;
import com.hbm.render.util.MissilePronter;
import com.hbm.tileentity.bomb.TileEntityCompactLauncher;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class RenderCompactLauncher implements BlockEntityRenderer<TileEntityCompactLauncher> {

    public RenderCompactLauncher(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityCompactLauncher launcher, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        // Рендерим модель самой пусковой установки
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.compact_launcher_tex));
        HBMResourceManager.compact_launcher.renderAll(poseStack, consumer, packedLight, packedOverlay);

        // Поднимаемся на высоту ракеты
        poseStack.translate(0, 1.0625, 0);

        // Рендерим ракету (если есть)
        if (launcher.load != null) {
            MissileMultipart missile = MissileMultipart.loadFromStruct(launcher.load);
            if (missile != null) {
                // Сохраняем матрицу для ракеты
                poseStack.pushPose();

                // Масштаб для отображения в мире (можно подогнать)
                float scale = 0.5F;
                poseStack.scale(scale, scale, scale);

                MissilePronter.prontMissile(missile, buffer, poseStack,  packedLight);

                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityCompactLauncher launcher) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}