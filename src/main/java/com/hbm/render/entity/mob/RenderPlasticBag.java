package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityPlasticBag;
import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * Рендер мусорного пакета: OBJ {@code models/mobs/plasticbag.obj}, как в 1.7.10.
 *
 * <p>Ориентация повторяет оригинал: поворот по рысканью плюс 90° и по тангажу
 * минус 90°, грани не отсекаются (модель — тонкая оболочка пакета).</p>
 */
@OnlyIn(Dist.CLIENT)
public class RenderPlasticBag extends EntityRenderer<EntityPlasticBag> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/plasticbag.png");

    public RenderPlasticBag(EntityRendererProvider.Context context) {
        super(context);
        // В оригинале shadowOpaque = 0 — тени у пакета нет.
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull EntityPlasticBag entity, float entityYaw, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        if (HBMResourceManager.plastic_bag == null) {
            return;
        }

        poseStack.pushPose();

        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) + 90.0F;
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) - 90.0F;

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        HBMResourceManager.plastic_bag.renderAll(poseStack, builder, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityPlasticBag entity) {
        return TEXTURE;
    }
}
