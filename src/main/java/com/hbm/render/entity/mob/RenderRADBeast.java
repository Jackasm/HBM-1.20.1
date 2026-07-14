package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityRADBeast;
import com.hbm.render.model.ModelM65Blaze;
import com.hbm.render.util.BeamPronter;
import com.hbm.render.util.BeamPronter.EnumBeamType;
import com.hbm.render.util.BeamPronter.EnumWaveType;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderRADBeast extends MobRenderer<EntityRADBeast, BlazeModel<EntityRADBeast>> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/radbeast.png");
    private static final ResourceLocation MASK = ResLocation(RefStrings.MODID, "textures/models/armor/model_m65_blaze.png");

    private final ModelM65Blaze modelM65;

    public RenderRADBeast(EntityRendererProvider.Context context) {
        super(context, new BlazeModel<>(context.bakeLayer(ModelLayers.BLAZE)), 0.5F);
        this.modelM65 = new ModelM65Blaze(context.bakeLayer(ModelM65Blaze.LAYER_LOCATION));
        // Добавляем кастомный слой для маски
        this.addLayer(new MaskLayer<>(this, this.modelM65));
    }

    @Override
    public void render(EntityRADBeast entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        Entity victim = entity.getUnfortunateSoul();

        if (victim != null && entity.getY() > 0.1) {
            poseStack.pushPose();
            poseStack.translate(0, 1.25, 0);

            double sx = entity.getX();
            double sy = entity.getY() + 1.25;
            double sz = entity.getZ();

            double tX = victim.getX();
            double tY = victim.getY() + victim.getBbHeight() / 2;
            double tZ = victim.getZ();

            if (victim == Minecraft.getInstance().player) {
                tY -= 1.5;
            }

            double length = Math.sqrt(Math.pow(tX - sx, 2) + Math.pow(tY - sy, 2) + Math.pow(tZ - sz, 2));
            if (length < 200) {
                BeamPronter.prontBeam(poseStack, buffer,
                        new Vec3(tX - sx, tY - sy, tZ - sz),
                        EnumWaveType.RANDOM, EnumBeamType.SOLID,
                        0x004000, 0x004000,
                        (int) (entity.level().getGameTime() % 1000 + 1),
                        (int) (length * 5), 0.125F, 2, 0.03125F);
            }

            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityRADBeast entity) {
        return TEXTURE;
    }

    // Кастомный слой для рендеринга маски
    @OnlyIn(Dist.CLIENT)
    public static class MaskLayer<T extends EntityRADBeast> extends RenderLayer<T, BlazeModel<T>> {

        private final ModelM65Blaze modelM65;

        public MaskLayer(RenderLayerParent<T, BlazeModel<T>> renderer, ModelM65Blaze modelM65) {
            super(renderer);
            this.modelM65 = modelM65;
        }

        @Override
        public void render(@NotNull PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks,
                           float ageInTicks, float netHeadYaw, float headPitch) {

            // Устанавливаем повороты для маски
            this.modelM65.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(MASK));
            this.modelM65.renderToBuffer(poseStack, consumer, packedLight, 0, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}