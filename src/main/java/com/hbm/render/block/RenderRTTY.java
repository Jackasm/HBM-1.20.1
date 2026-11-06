package com.hbm.render.block;

import com.hbm.blocks.network.RadioTorchBase;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.network.TileEntityRadioTorchBase;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * Рендер факела RTTY: OBJ {@code rtty.obj}, повёрнутый к опоре.
 *
 * <p>Геометрия поворота перенесена из {@code ref:render/block/RenderRTTY.java:47-73}
 * один в один: там к вершинам применялись {@code rotateAroundZ(flip)} и
 * {@code rotateAroundY(rotation)} с метой блока в роли направления к опоре.
 * У {@code Vec3.rotateAroundZ} в 1.7.10 знак обратный стандартному (оси X и Z —
 * обратные, Y — прямая), поэтому {@code Axis.ZP} крутится с минусом, а
 * {@code Axis.YP} — как есть.</p>
 */
@OnlyIn(Dist.CLIENT)
public class RenderRTTY<T extends com.hbm.tileentity.TileEntityLoadedBase>
        implements BlockEntityRenderer<T> {

    private static final float HALF_PI = (float) (Math.PI / 2);
    private static final float PI = (float) Math.PI;

    public RenderRTTY(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull T te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = te.getBlockState();
        Direction facing = state.getValue(RadioTorchBase.FACING);
        // Состояние on/off есть только у sender/receiver/logic; у counter,
        // reader и controller текстура одна, и lastState тут ни при чём.
        boolean on = te instanceof TileEntityRadioTorchBase base && base.lastState > 0;

        String name = state.getBlock() instanceof RadioTorchBase torch
                ? torch.textureName(on)
                : "rtty_sender_off";
        // entityCutout ждёт полный путь текстуры, а не имя спрайта из атласа.
        ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/block/" + name + ".png");

        // Модель нарисована вокруг центра блока: y −0.5…0.125, x/z −0.125…0.125.
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        float rotationY = 0F;
        float rotationZ = 0F;
        // FACING порта смотрит от блока К опоре, мета 1.7.10 — наоборот, от
        // опоры к блоку. Поэтому таблица эталона применяется к FACING.getOpposite().
        switch (facing.getOpposite()) {
            case UP -> { /* стоит на опоре снизу: как в модели */ }
            case DOWN -> rotationZ = PI;                        // висит под опорой
            case NORTH -> { rotationY = HALF_PI; rotationZ = HALF_PI; }
            case SOUTH -> { rotationY = 3 * HALF_PI; rotationZ = HALF_PI; }
            case WEST -> { rotationY = PI; rotationZ = HALF_PI; }
            case EAST -> rotationZ = HALF_PI;
        }

        // Порядок как в 1.7.10: сначала Z (flip), затем Y (rotation).
        poseStack.mulPose(Axis.YP.rotation(rotationY));
        poseStack.mulPose(Axis.ZP.rotation(-rotationZ));

        if (HBMResourceManager.rtty != null) {
            // Модель — тонкая открытая оболочка: у неё нет ни одной грани со
            // стороны опоры, а сами стенки двойные. `entityCutout` отсекает
            // обратные грани, поэтому с четырёх сторон факел просвечивал
            // насквозь, и «поверхности грани» не отрисовывались. 1.7.10 рисовал
            // ту же модель без отсечения (ObjUtil рисует все грани подряд), так
            // что NoCull — это паритет, а не улучшение.
            VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            HBMResourceManager.rtty.renderAll(poseStack, builder, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull T te) { return true; }

    @Override
    public int getViewDistance() { return 128; }
}
