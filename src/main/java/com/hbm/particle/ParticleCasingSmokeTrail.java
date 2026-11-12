package com.hbm.particle;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * One independent smoke-trail particle per smoking spent casing.
 *
 * The casing contributes only its latest world position. Smoke is emitted,
 * updated and drawn in a DIFFERENT ParticleRenderType from the OBJ casing.
 * Never call MultiBufferSource.getBuffer/endBatch from Particle.render:
 * the particle engine already owns the BufferBuilder and its draw call.
 */
@OnlyIn(Dist.CLIENT)
public final class ParticleCasingSmokeTrail extends Particle {

    private static final ResourceLocation WHITE_TEXTURE =
            ResLocation(RefStrings.MODID, "textures/item/weapon/smoke_white.png");
    private static final Random RANDOM = new Random();
    private static final float D_SCALE = 0.05F;
    private static final float JITTER = 0.001F;
    private static final int MAX_NODES = 64;

    public static final ParticleRenderType SMOKE_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getRendertypeEntityTranslucentShader);
            RenderSystem.setShaderTexture(0, WHITE_TEXTURE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        }

        @Override
        public void end(Tesselator tesselator) {
            // Draw only the smoke batch; the casing batch has its own type.
            RenderSystem.setShader(GameRenderer::getRendertypeEntityTranslucentShader);
            RenderSystem.setShaderTexture(0, WHITE_TEXTURE);
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }

        @Override
        public String toString() {
            return "HBM:CASING_SMOKE";
        }
    };

    private static final class SmokeNode {
        Vec3 previousPos;
        Vec3 pos;
        double alpha;

        SmokeNode(Vec3 origin, double alpha) {
            this.previousPos = origin;
            this.pos = origin;
            this.alpha = alpha;
        }
    }

    private final ParticleSpentCasing casing;
    private final List<SmokeNode> smokeNodes = new ArrayList<>();
    private final int emissionTicks;
    private final int nodeLife;
    private final double smokeLift;
    private final float width;

    public ParticleCasingSmokeTrail(ClientLevel level, double x, double y, double z,
                                    ParticleSpentCasing casing, int smokeLife,
                                    double smokeLift, int nodeLife, float casingScaleX) {
        super(level, x, y, z);
        this.casing = casing;
        this.emissionTicks = Math.max(0, smokeLife);
        this.nodeLife = Math.max(1, nodeLife);
        this.smokeLift = smokeLift;
        this.width = Math.max(0.001F, casingScaleX * 0.5F * D_SCALE);
        this.gravity = 0;
        this.hasPhysics = false;
        // Allow existing nodes to fade after their casing stops emitting.
        this.lifetime = (int) Math.min(Integer.MAX_VALUE,
                (long) emissionTicks + this.nodeLife + 2L);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        for (SmokeNode node : smokeNodes) {
            node.previousPos = node.pos;
            node.pos = node.pos.add(
                    RANDOM.nextGaussian() * JITTER,
                    smokeLift * D_SCALE,
                    RANDOM.nextGaussian() * JITTER);
            node.alpha = Math.max(0D, node.alpha - 1D / nodeLife);
        }

        if (this.age < emissionTicks && casing.isSmokeSourceAlive()) {
            Vec3 origin = casing.smokeWorldPosition();
            this.setPos(origin.x, origin.y, origin.z);
            smokeNodes.add(new SmokeNode(origin,
                    smokeNodes.isEmpty() && age == 1 ? 0D : 1D));
        }

        // Keep the very first transparent anchor long enough to form the
        // first ribbon segment. Each subsequent node fades independently.
        if (smokeNodes.size() > 2) {
            smokeNodes.removeIf(node -> node.alpha <= 0D);
        }
        if (smokeNodes.size() > MAX_NODES) smokeNodes.remove(0);

        if ((age >= emissionTicks || !casing.isSmokeSourceAlive()) &&
                smokeNodes.stream().noneMatch(node -> node.alpha > 0D)) {
            this.remove();
        }
    }

    @Override
    public void render(@NotNull VertexConsumer builder, @NotNull Camera camera, float partialTicks) {
        if (smokeNodes.size() < 2) return;

        PoseStack poseStack = new PoseStack();
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        Vec3 cameraPos = camera.getPosition();

        double yaw = Math.toRadians(camera.getYRot());
        float rightX = (float) -Math.sin(yaw);
        float rightZ = (float) Math.cos(yaw);
        int light = getLightColor(partialTicks);

        for (int i = 0; i < smokeNodes.size() - 1; i++) {
            SmokeNode a = smokeNodes.get(i);
            SmokeNode b = smokeNodes.get(i + 1);
            Vec3 start = a.previousPos.lerp(a.pos, partialTicks).subtract(cameraPos);
            Vec3 end = b.previousPos.lerp(b.pos, partialTicks).subtract(cameraPos);

            float alphaA = (float) (a.alpha * this.alpha);
            float alphaB = (float) (b.alpha * this.alpha);
            if (alphaA <= 0F && alphaB <= 0F) continue;

            // Two camera-facing quads with transparent outward edges.
            smokeVertex(builder, matrix, normal, start, 0F, 0F, alphaA, 0F, 0F, light);
            smokeVertex(builder, matrix, normal, start, rightX * width, rightZ * width, 0F, 1F, 0F, light);
            smokeVertex(builder, matrix, normal, end, rightX * width, rightZ * width, 0F, 1F, 1F, light);
            smokeVertex(builder, matrix, normal, end, 0F, 0F, alphaB, 0F, 1F, light);

            smokeVertex(builder, matrix, normal, start, 0F, 0F, alphaA, 0F, 0F, light);
            smokeVertex(builder, matrix, normal, start, -rightX * width, -rightZ * width, 0F, 1F, 0F, light);
            smokeVertex(builder, matrix, normal, end, -rightX * width, -rightZ * width, 0F, 1F, 1F, light);
            smokeVertex(builder, matrix, normal, end, 0F, 0F, alphaB, 0F, 1F, light);
        }
    }

    private static void smokeVertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normal,
                                    Vec3 pos, float dx, float dz, float alpha,
                                    float u, float v, int light) {
        buffer.vertex(pose, (float) pos.x + dx, (float) pos.y, (float) pos.z + dz)
                .color(1F, 1F, 1F, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0F, 1F, 0F)
                .endVertex();
    }

    @Override
    protected int getLightColor(float partialTick) {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        int blockLight = this.level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos);
        int skyLight = this.level.getBrightness(net.minecraft.world.level.LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return SMOKE_RENDER_TYPE;
    }
}
