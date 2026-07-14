package com.hbm.render.entity.item;

import com.hbm.entity.item.EntityBoatRubber;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderBoatRubber extends EntityRenderer<EntityBoatRubber> {

    private static final ResourceLocation BOAT_TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/boat_rubber.png");
    private final BoatModel model;

    public RenderBoatRubber(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;
        this.model = new BoatModel(context.bakeLayer(ModelLayers.createBoatModelName(net.minecraft.world.entity.vehicle.Boat.Type.OAK)));
    }

    @Override
    public void render(@NotNull EntityBoatRubber entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        float hurtTime = (float) entity.getHurtTime() - partialTicks;
        float damage = entity.getDamage() - partialTicks;
        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (hurtTime > 0.0F) {
            float f = Mth.sin(hurtTime) * hurtTime * damage / 10.0F * (float) entity.getHurtDir();
            poseStack.mulPose(Axis.XP.rotationDegrees(f));
        }

        float bubbleAngle = entity.getBubbleAngle(partialTicks);
        if (!Mth.equal(bubbleAngle, 0.0F)) {
            poseStack.mulPose(new Quaternionf().setAngleAxis(bubbleAngle * ((float) Math.PI / 180F), 1.0F, 0.0F, 1.0F));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        this.model.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        if (!entity.isUnderWater()) {
            VertexConsumer vertexconsumer1 = buffer.getBuffer(RenderType.waterMask());
            this.model.waterPatch().render(poseStack, vertexconsumer1, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityBoatRubber entity) {
        return BOAT_TEXTURE;
    }
}