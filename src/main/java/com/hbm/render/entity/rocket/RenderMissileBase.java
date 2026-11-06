package com.hbm.render.entity.rocket;

import com.hbm.entity.missile.EntityMissileBaseNT;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class RenderMissileBase extends EntityRenderer<Entity> {

    protected static class InterpState {
        Quaternionf prev = new Quaternionf();
        Quaternionf curr = new Quaternionf();
        float lastDx = 0f, lastDy = 1f, lastDz = 0f;
        boolean initialized = false;
    }

    private final Map<Entity, InterpState> states = new WeakHashMap<>();

    public RenderMissileBase(EntityRendererProvider.Context context) {
        super(context);
    }

    protected abstract ResourceLocation getTexture(Entity entity);

    protected abstract void renderModel(Entity entity, PoseStack poseStack,
                                        VertexConsumer consumer, int packedLight);

    protected float getScale(Entity entity) {
        return 1F;
    }

    @Override
    public void render(@NotNull Entity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        applyRotation(entity, partialTicks, poseStack);

        if (entity instanceof EntityMissileBaseNT missile) {
            byte rot = missile.getEntityData().get(EntityMissileBaseNT.ROTATION);
            switch (rot) {
                case 2 -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
                case 3 -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
                case 4 -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
                case 5 -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            }
        }

        float scale = getScale(entity);
        poseStack.scale(scale, scale, scale);

        ResourceLocation texture = getTexture(entity);
        VertexConsumer consumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutout(texture));
        renderModel(entity, poseStack, consumer, packedLight);

        poseStack.popPose();
    }

    private void applyRotation(Entity entity, float partialTicks, PoseStack poseStack) {
        if (!(entity instanceof EntityMissileBaseNT missile)) {
            return;
        }

        float dx = missile.getEntityData().get(EntityMissileBaseNT.DIR_X);
        float dy = missile.getEntityData().get(EntityMissileBaseNT.DIR_Y);
        float dz = missile.getEntityData().get(EntityMissileBaseNT.DIR_Z);

        InterpState st = states.computeIfAbsent(entity, e -> new InterpState());

        boolean changed =
                Math.abs(dx - st.lastDx) > 1.0E-4f ||
                        Math.abs(dy - st.lastDy) > 1.0E-4f ||
                        Math.abs(dz - st.lastDz) > 1.0E-4f;

        if (changed) {
            st.prev.set(st.curr);
            st.curr.set(new Quaternionf().rotationTo(
                    new Vector3f(0, 1, 0),
                    new Vector3f(dx, dy, dz)
            ));
            st.lastDx = dx;
            st.lastDy = dy;
            st.lastDz = dz;
        }

        if (!st.initialized) {
            st.prev.set(st.curr);
            st.initialized = true;
        }

        Quaternionf smooth = new Quaternionf(st.prev).slerp(st.curr, partialTicks);
        poseStack.mulPose(smooth);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        return getTexture(entity);
    }
}