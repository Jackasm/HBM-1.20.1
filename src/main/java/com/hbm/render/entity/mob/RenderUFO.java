package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityUFO;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.util.BeamPronter;
import com.hbm.render.util.BeamPronter.EnumBeamType;
import com.hbm.render.util.BeamPronter.EnumWaveType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class RenderUFO extends EntityRenderer<EntityUFO> {

    private static final ResourceLocation TEXTURE = HBMResourceManager.ufo_tex;

    public RenderUFO(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(EntityUFO entity, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 1, 0);

        if (!entity.isAlive()) {
            float tilt = entity.deathTime + 30 + partialTicks;
            poseStack.mulPose(new Quaternionf().rotationXYZ(
                    (float) Math.toRadians(tilt),
                    (float) Math.toRadians(tilt),
                    0
            ));
        }

        double scale = 2.0D;

        poseStack.pushPose();
        double rot = (entity.tickCount + partialTicks) * 5 % 360D;
        poseStack.mulPose(new Quaternionf().rotationXYZ(
                0,
                (float) Math.toRadians(rot),
                0
        ));
        poseStack.scale((float) scale, (float) scale, (float) scale);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        HBMResourceManager.ufo.renderAll(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        // Рендерим луч
        if (entity.getBeam()) {
            int ix = (int) Math.floor(entity.getX());
            int iz = (int) Math.floor(entity.getZ());
            int iy = 0;

            for (int i = (int) Math.ceil(entity.getY()); i >= 0; i--) {
                if (entity.level().getBlockState(new BlockPos(ix, i, iz)).getBlock() != Blocks.AIR) {
                    iy = i;
                    break;
                }
            }

            double length = entity.getY() - iy;

            if (length > 0) {
                // Используем портированный BeamPronter
                BeamPronter.prontBeam(
                        poseStack,
                        buffer,
                        new Vec3(0, -length, 0),
                        EnumWaveType.SPIRAL,
                        EnumBeamType.SOLID,
                        0x101020, 0x101020, 0,
                        (int)(length + 1), 0F, 6, (float)scale * 0.75F
                );
                BeamPronter.prontBeam(
                        poseStack,
                        buffer,
                        new Vec3(0, -length, 0),
                        EnumWaveType.RANDOM,
                        EnumBeamType.SOLID,
                        0x202060, 0x202060, entity.tickCount / 2,
                        (int)(length / 2 + 1), (float)scale * 1.5F, 2, 0.0625F
                );
                BeamPronter.prontBeam(
                        poseStack,
                        buffer,
                        new Vec3(0, -length, 0),
                        EnumWaveType.RANDOM,
                        EnumBeamType.SOLID,
                        0x202060, 0x202060, entity.tickCount / 4,
                        (int)(length / 2 + 1), (float)scale * 1.5F, 2, 0.0625F
                );
            }
        }

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityUFO entity) {
        return TEXTURE;
    }
}