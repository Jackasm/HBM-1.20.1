package com.hbm.render.tileentity;

import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.tileentity.machine.TileEntityDemonLamp;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderLampDemon implements BlockEntityRenderer<TileEntityDemonLamp> {

    public RenderLampDemon(BlockEntityRendererProvider.Context ignoredContext){}
    private static final HFRWavefrontObject DEMON_LAMP = new HFRWavefrontObject(
            ResLocation(RefStrings.MODID, "models/block/machines/lamp_demon.obj")
    );
    private static final ResourceLocation TEX = ResLocation(RefStrings.MODID, "textures/block/machines/lamp_demon.png");

    @Override
    public void render(TileEntityDemonLamp tileEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);

        int metadata = tileEntity.getBlockState().getBlock().getDescriptionId().hashCode(); // Упрощенно
        // Лучше использовать BlockState с property, но для совместимости с метаданными:
        BlockState state = tileEntity.getBlockState();

        // Вращения на основе метаданных (нужно адаптировать под ваши свойства блока)
        // Предполагается, что у блока есть свойство ROTATION или类似

        poseStack.translate(0, -0.5F, 0);

        // Рендерим модель
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEX));
        DEMON_LAMP.renderAll(poseStack, consumer, packedLight, packedOverlay);

        // Рендерим свечение (лазерные лучи)
        renderGlow(poseStack, buffer, tileEntity.getBlockPos());

        poseStack.popPose();
    }

    private void renderGlow(PoseStack poseStack, MultiBufferSource buffer, BlockPos pos) {
        Tesselator tesselator = Tesselator.getInstance();
        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());

        poseStack.pushPose();

        Vec3 vec = new Vec3(1, 0, 0);
        double near = 0.375D;
        double far = 15D;

        // Включаем смешивание для свечения
        for (int j = 0; j < 2; j++) {
            double h = 0.5;
            double height = j == 0 ? -h : h;

            for (int i = 0; i < 16; i++) {
                float r = 0F;
                float g = 0.75F;
                float b = 1F;
                float a1 = 0.25F;
                float a2 = 0F;

                double x1 = vec.x * near;
                double z1 = vec.z * near;
                double x2 = vec.x * far;
                double z2 = vec.z * far;
                double y = 0.5D + j * 0.125D;
                double yEnd = y + height;

                // Первый треугольник
                consumer.vertex(poseStack.last().pose(), (float) x1, (float) y, (float) z1)
                        .color(r, g, b, a1).endVertex();
                consumer.vertex(poseStack.last().pose(), (float) x2, (float) yEnd, (float) z2)
                        .color(r, g, b, a2).endVertex();

                // Поворачиваем вектор
                vec = vec.yRot((float) (Math.PI * 2D / 16D));
                x1 = vec.x * near;
                z1 = vec.z * near;
                x2 = vec.x * far;
                z2 = vec.z * far;

                // Второй треугольник
                consumer.vertex(poseStack.last().pose(), (float) x2, (float) yEnd, (float) z2)
                        .color(r, g, b, a2).endVertex();
                consumer.vertex(poseStack.last().pose(), (float) x1, (float) y, (float) z1)
                        .color(r, g, b, a1).endVertex();
            }
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityDemonLamp tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}