package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityFBIDrone;
import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderFBIDrone extends EntityRenderer<EntityFBIDrone> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/quadcopter.png");

    public RenderFBIDrone(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(EntityFBIDrone entity, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0.25, 0);

        Random rand = new Random(entity.getId());
        float rotation = (float) (rand.nextDouble() * 360D);
        poseStack.mulPose(new Quaternionf().rotationXYZ(
                0,
                (float) Math.toRadians(rotation),
                0
        ));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        HBMResourceManager.drone.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityFBIDrone entity) {
        return TEXTURE;
    }
}