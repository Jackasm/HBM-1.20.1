package com.hbm.particle;

import com.hbm.main.HBMResourceManager;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ParticleSpentCasing extends Particle {

    public static final Random rand = new Random();
    private static final float D_SCALE = 0.05F;
    private static final float SMOKE_JITTER = 0.001F;

    // Текстура для дыма
    protected static final ResourceLocation WHITE_TEXTURE = ResLocation(RefStrings.MODID, "textures/item/weapon/smoke_white.png");

    private final int maxSmokeGen;
    private final double smokeLift;
    private final int nodeLife;

    // Внутренний класс для узлов дыма
    private static class SmokeNode {
        public Vec3 pos;
        public double alpha;
        public double width;

        public SmokeNode(Vec3 pos, double alpha, double width) {
            this.pos = pos;
            this.alpha = alpha;
            this.width = width;
        }
    }

    private final List<SmokeNode> smokeNodes = new ArrayList<>();

    private final SpentCasing config;
    private final boolean isSmoking;

    private float momentumPitch, momentumYaw;

    // Вращение частицы
    public float rotationPitch;
    public float rotationYaw;
    public float prevRotationPitch;
    public float prevRotationYaw;

    private double prevRenderX;
    private double prevRenderY;
    private double prevRenderZ;
    private boolean setupDeltas;

    // Кастомный RenderType для гильз
    public static final ParticleRenderType PARTICLE_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
            RenderSystem.setShaderTexture(0, HBMResourceManager.casings_tex);
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.disableBlend();
        }

        @Override
        public String toString() {
            return "HBM:SPENT_CASING";
        }
    };

    public ParticleSpentCasing(ClientLevel world, double x, double y, double z,
                               double mx, double my, double mz, float momentumPitch, float momentumYaw,
                               SpentCasing config, boolean smoking, int smokeLife, double smokeLift, int nodeLife) {
        super(world, x, y, z, mx, my, mz);
        this.momentumPitch = momentumPitch;
        this.momentumYaw = momentumYaw;
        this.config = config;
        this.isSmoking = smoking;

        this.lifetime = config.getMaxAge();

        // Настройки дыма
        this.maxSmokeGen = smokeLife;
        this.smokeLift = smokeLift;
        this.nodeLife = nodeLife;

        this.gravity = 1.0F;
        this.alpha = 1.0F;

        // Инициализируем prevRender позиции
        this.prevRenderX = x;
        this.prevRenderY = y;
        this.prevRenderZ = z;
        this.setupDeltas = false;

        this.setSize(D_SCALE * Math.max(config.getScaleX(), config.getScaleZ()) * 2,
                D_SCALE * config.getScaleY());

        // Добавляем начальные узлы дыма
        if (isSmoking) {
            smokeNodes.add(new SmokeNode(new Vec3(0, 0, 0), 0.9, 0.02));
            for (int i = 0; i < 8; i++) {  // Было 3
                smokeNodes.add(new SmokeNode(
                        new Vec3(0, 0.05 + i * 0.04, 0),  // Увеличено расстояние
                        0.7 - i * 0.07,                  // Медленнее затухание
                        0.03 + i * 0.015                // Больше ширина
                ));
            }
        }
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

        this.yd -= 0.04D * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98D;
        this.yd *= 0.98D;
        this.zd *= 0.98D;

        if (this.onGround) {
            this.xd *= 0.7D;
            this.zd *= 0.7D;
            this.rotationPitch = (float) (Math.floor(this.rotationPitch / 180F + 0.5F)) * 180F;
            this.momentumYaw *= 0.7F;
            this.onGround = false;
        }

        // Обновляем дымовые узлы
        updateSmokeNodes();

        prevRotationPitch = rotationPitch;
        prevRotationYaw = rotationYaw;

        rotationPitch += momentumPitch;
        rotationYaw += momentumYaw;

        // Обновляем bounding box
        updateBoundingBox();
    }

    public void addPosition(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    private void updateSmokeNodes() {
        if (age > maxSmokeGen && !smokeNodes.isEmpty()) {
            smokeNodes.clear();
            return;
        }

        if (isSmoking && age <= maxSmokeGen) {
            // Обновляем существующие ноды
            for (SmokeNode node : smokeNodes) {
                // Добавляем случайное движение и подъем
                node.pos = node.pos.add(
                        rand.nextGaussian() * SMOKE_JITTER,
                        smokeLift * D_SCALE * 2.5,
                        rand.nextGaussian() * SMOKE_JITTER
                );

                // Уменьшаем alpha
                if (node.alpha > 0) node.alpha -= 0.3D / nodeLife;

                // Увеличиваем ширину
                node.width *= 1.02;
            }

            // Удаляем полностью прозрачные ноды
            smokeNodes.removeIf(node -> node.alpha <= 0.001);

            if (smokeNodes.size() > 80) {
                smokeNodes.remove(smokeNodes.size() - 1);
            }

            // Добавляем новую ноду
            if (age < maxSmokeGen) {
                Vec3 newPos = new Vec3(0, 0, 0);
                smokeNodes.add(0, new SmokeNode(newPos, 0.9, 0.02));
            }
        }
    }

    private void updateBoundingBox() {
        float width = this.bbWidth / 2.0F;
        float height = this.bbHeight / 2.0F;
        this.setBoundingBox(new AABB(
                this.x - width, this.y - height, this.z - width,
                this.x + width, this.y + height, this.z + width
        ));
    }

    @Override
    public void move(double motionX, double motionY, double motionZ) {
        if (this.isInWater()) {
            motionX *= 0.25D;
            motionY *= 0.05D;
            motionZ *= 0.25D;
            this.xd = 0.0D;
            this.yd = 0.0D;
            this.zd = 0.0D;
        }

        double initMoX = motionX;
        double initMoY = motionY;
        double initMoZ = motionZ;

        this.setBoundingBox(this.getBoundingBox().move(motionX, motionY, motionZ));
        this.setLocationFromBoundingbox();

        this.onGround = initMoY != motionY && initMoY < 0.0D;

        if (initMoX != motionX) {
            this.xd *= -0.25D;
            if (Math.abs(momentumYaw) > 1e-7) {
                momentumYaw *= -0.75F;
            } else {
                momentumYaw = (float) rand.nextGaussian() * 10F * this.config.getBounceYaw();
            }
        }

        if (initMoY != motionY) {
            this.yd *= -0.5D;
            boolean rotFromSpeed = Math.abs(this.yd) > 0.04;
            if (rotFromSpeed || Math.abs(momentumPitch) > 1e-7) {
                momentumPitch *= -0.75F;
                if (rotFromSpeed) {
                    float mult = (float) com.hbm.util.BobMathUtil.safeClamp(initMoY / 0.2F, -1F, 1F);
                    momentumPitch += (float) (rand.nextGaussian() * 10F * this.config.getBouncePitch() * mult);
                    momentumYaw += (float) rand.nextGaussian() * 10F * this.config.getBounceYaw() * mult;
                }
            }
        }

        if (initMoZ != motionZ) {
            this.zd *= -0.25D;
            if (Math.abs(momentumYaw) > 1e-7) {
                momentumYaw *= -0.75F;
            } else {
                momentumYaw = (float) rand.nextGaussian() * 10F * this.config.getBounceYaw();
            }
        }
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        // ВАЖНО: Сохраняем и восстанавливаем шейдер для гильз
        var oldShader = RenderSystem.getShader();

        // Интерполированная позиция
        double pX = Mth.lerp(partialTicks, this.xo, this.x);
        double pY = Mth.lerp(partialTicks, this.yo, this.y);
        double pZ = Mth.lerp(partialTicks, this.zo, this.z);

        float yaw = Mth.lerp(partialTicks, prevRotationYaw, rotationYaw);
        float pitch = Mth.lerp(partialTicks, prevRotationPitch, rotationPitch);

        // Рендер гильзы - используем стандартный шейдер
        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
        renderCasing(buffer, camera, pX, pY, pZ, yaw, pitch, partialTicks);

        // Рендер дыма
        if (!smokeNodes.isEmpty()) {
            renderSmoke(camera, pX, pY, pZ, partialTicks);
        }

        // Восстанавливаем шейдер
        RenderSystem.setShader(() -> oldShader);

        // Обновляем prevRender для следующего кадра
        if (!setupDeltas) {
            prevRenderX = pX;
            prevRenderY = pY;
            prevRenderZ = pZ;
            setupDeltas = true;
        } else {
            prevRenderX = pX;
            prevRenderY = pY;
            prevRenderZ = pZ;
        }
    }

    private void renderCasing(VertexConsumer buffer, Camera camera, double pX, double pY, double pZ,
                              float yaw, float pitch, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        Vec3 cameraPos = camera.getPosition();

        poseStack.translate(pX - cameraPos.x, pY - cameraPos.y, pZ - cameraPos.z);
        poseStack.scale(D_SCALE, D_SCALE, D_SCALE);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        poseStack.scale(config.getScaleX(), config.getScaleY(), config.getScaleZ());

        int packedLight = getLightColor(partialTicks);

        int index = 0;
        for (String name : config.getType().partNames) {
            int col = this.config.getColors()[index];
            Color color = new Color(col);
            float r = color.getRed() / 255.0F;
            float g = color.getGreen() / 255.0F;
            float b = color.getBlue() / 255.0F;

            HBMResourceManager.casings.renderPartColored(
                    poseStack,
                    buffer,
                    name,
                    packedLight,
                    packedLight,
                    r, g, b, this.alpha
            );
            index++;
        }
    }

    private void renderSmoke(Camera camera, double pX, double pY, double pZ, float partialTicks) {
        if (smokeNodes.size() < 2) return;

        // Интерполяция движения
        double deltaX = (prevRenderX - pX) * partialTicks;
        double deltaY = (prevRenderY - pY) * partialTicks;
        double deltaZ = (prevRenderZ - pZ) * partialTicks;

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        // ВАЖНО: Используем правильный RenderType для прозрачности
        VertexConsumer builder = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(WHITE_TEXTURE));

        PoseStack poseStack = new PoseStack();
        Vec3 cameraPos = camera.getPosition();

        poseStack.translate(pX - cameraPos.x, pY - cameraPos.y, pZ - cameraPos.z);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        // Билборд - всегда повернут к камере
        float yaw = camera.getYRot();
        double yawRad = Math.toRadians(yaw);
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);

        double rightX = -sinYaw;

        // Цвет дыма - берем из конфига или используем светло-серый
        float r = 0.3f, g = 0.7f, b = 0.7f;

        // Рендерим сегменты между узлами
        for (int i = 0; i < smokeNodes.size() - 1; i++) {
            SmokeNode node = smokeNodes.get(i);
            SmokeNode next = smokeNodes.get(i + 1);

            // Интерполируем позиции
            Vec3 nodePos = node.pos.add(deltaX, deltaY, deltaZ);
            Vec3 nextPos = next.pos.add(deltaX, deltaY, deltaZ);

            float alpha = (float) (node.alpha * this.alpha);
            float nextAlpha = (float) (next.alpha * this.alpha);
            float width = (float) node.width;
            float nextWidth = (float) next.width;

            // Первая полоса (вправо)
            builder.vertex(matrix,
                            (float) nodePos.x,
                            (float) nodePos.y,
                            (float) nodePos.z)
                    .color(r, g, b, alpha)
                    .uv(0, 0)
                    .overlayCoords(0)
                    .uv2(getLightColor(partialTicks))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            builder.vertex(matrix,
                            (float) (nodePos.x + rightX * width),
                            (float) nodePos.y,
                            (float) (nodePos.z + cosYaw * width))
                    .color(r, g, b, 0)
                    .uv(1, 0)
                    .overlayCoords(0)
                    .uv2(getLightColor(partialTicks))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            builder.vertex(matrix,
                            (float) (nextPos.x + rightX * nextWidth),
                            (float) nextPos.y,
                            (float) (nextPos.z + cosYaw * nextWidth))
                    .color(r, g, b, 0)
                    .uv(1, 1)
                    .overlayCoords(0)
                    .uv2(getLightColor(partialTicks))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            builder.vertex(matrix,
                            (float) nextPos.x,
                            (float) nextPos.y,
                            (float) nextPos.z)
                    .color(r, g, b, nextAlpha)
                    .uv(0, 1)
                    .overlayCoords(0)
                    .uv2(getLightColor(partialTicks))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            // Вторая полоса (влево)
            builder.vertex(matrix,
                            (float) nodePos.x,
                            (float) nodePos.y,
                            (float) nodePos.z)
                    .color(r, g, b, alpha)
                    .uv(0, 0)
                    .overlayCoords(0)
                    .uv2(getLightColor(partialTicks))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            builder.vertex(matrix,
                            (float) (nodePos.x - rightX * width),
                            (float) nodePos.y,
                            (float) (nodePos.z - cosYaw * width))
                    .color(r, g, b, 0)
                    .uv(1, 0)
                    .overlayCoords(0)
                    .uv2(getLightColor(partialTicks))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            builder.vertex(matrix,
                            (float) (nextPos.x - rightX * nextWidth),
                            (float) nextPos.y,
                            (float) (nextPos.z - cosYaw * nextWidth))
                    .color(r, g, b, 0)
                    .uv(1, 1)
                    .overlayCoords(0)
                    .uv2(getLightColor(partialTicks))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            builder.vertex(matrix,
                            (float) nextPos.x,
                            (float) nextPos.y,
                            (float) nextPos.z)
                    .color(r, g, b, nextAlpha)
                    .uv(0, 1)
                    .overlayCoords(0)
                    .uv2(getLightColor(partialTicks))
                    .normal(normal, 0, 1, 0)
                    .endVertex();
        }

        bufferSource.endBatch(RenderType.entityTranslucentEmissive(WHITE_TEXTURE));
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
        return PARTICLE_RENDER_TYPE;
    }

    private boolean isInWater() {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
            return this.level.getBlockState(pos)
                    .getFluidState().is(FluidTags.WATER);
    }
}