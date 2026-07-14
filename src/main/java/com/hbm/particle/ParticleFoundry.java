package com.hbm.particle;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ParticleFoundry extends Particle {

    private static final ResourceLocation LAVA = ResLocation(RefStrings.MODID, "textures/block/network/lava_gray.png");

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, LAVA);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return RefStrings.MODID + ":foundry";
        }
    };

    private final int color;
    private final Direction dir;
    private final double length;
    private final double base;
    private final double offset;
    private int age;

    public ParticleFoundry(ClientLevel level, double x, double y, double z,
                           int color, Direction dir, double length, double base, double offset) {
        super(level, x, y, z);
        this.color = color;
        this.dir = dir;
        this.length = length;
        this.base = base;
        this.offset = offset;
        this.lifetime = 20;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.age++;
        if (this.age >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float pX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float pY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float pZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        Direction rot = dir.getClockWise();
        double progress = (age + partialTicks) / lifetime;
        double width = 0.0625 + progress * 0.0625;
        double girth = 0.125 * (1 - progress);

        Color col = new Color(this.color);
        double brightener = 0.7;
        float r = (float) (255.0 - (255.0 - col.getRed()) * brightener) / 255f;
        float g = (float) (255.0 - (255.0 - col.getGreen()) * brightener) / 255f;
        float b = (float) (255.0 - (255.0 - col.getBlue()) * brightener) / 255f;

        double dirXG = dir.getStepX() * girth;
        double dirZG = dir.getStepZ() * girth;
        double rotXW = rot.getStepX() * width;
        double rotZW = rot.getStepZ() * width;
        double dirOX = dir.getStepX() * offset;
        double dirOZ = dir.getStepZ() * offset;

        double uMin = 0.5 - width;
        double uMax = 0.5 + width;
        double vMin = 0;
        double vMax = length;
        double add = (System.currentTimeMillis() / 100 % 16) / 16.0;

        int light = getLightColor(partialTicks);

        // Lower back
        vertex(buffer, pX + rotXW, pY + girth, pZ + rotZW, uMax, vMax + add + girth, r, g, b, light);
        vertex(buffer, pX - rotXW, pY + girth, pZ - rotZW, uMin, vMax + add + girth, r, g, b, light);
        vertex(buffer, pX - rotXW, pY - length, pZ - rotZW, uMin, vMin + add, r, g, b, light);
        vertex(buffer, pX + rotXW, pY - length, pZ + rotZW, uMax, vMin + add, r, g, b, light);

        // Lower front
        vertex(buffer, pX + dirXG + rotXW, pY, pZ + dirZG + rotZW, uMax, vMax + add, r, g, b, light);
        vertex(buffer, pX + dirXG - rotXW, pY, pZ + dirZG - rotZW, uMin, vMax + add, r, g, b, light);
        vertex(buffer, pX + dirXG - rotXW, pY - length, pZ + dirZG - rotZW, uMin, vMin + add, r, g, b, light);
        vertex(buffer, pX + dirXG + rotXW, pY - length, pZ + dirZG + rotZW, uMax, vMin + add, r, g, b, light);

        double wMin = 0;
        double wMax = girth;

        // Lower left
        vertex(buffer, pX + rotXW, pY + girth, pZ + rotZW, wMin, vMax + add + girth, r, g, b, light);
        vertex(buffer, pX + dirXG + rotXW, pY, pZ + dirZG + rotZW, wMax, vMax + add, r, g, b, light);
        vertex(buffer, pX + dirXG + rotXW, pY - length, pZ + dirZG + rotZW, wMax, vMin + add, r, g, b, light);
        vertex(buffer, pX + rotXW, pY - length, pZ + rotZW, wMin, vMin + add, r, g, b, light);

        // Lower right
        vertex(buffer, pX - rotXW, pY + girth, pZ - rotZW, wMin, vMax + add + girth, r, g, b, light);
        vertex(buffer, pX + dirXG - rotXW, pY, pZ + dirZG - rotZW, wMax, vMax + add, r, g, b, light);
        vertex(buffer, pX + dirXG - rotXW, pY - length, pZ + dirZG - rotZW, wMax, vMin + add, r, g, b, light);
        vertex(buffer, pX - rotXW, pY - length, pZ - rotZW, wMin, vMin + add, r, g, b, light);

        vMax = offset;

        // Upper back (инвертированный порядок)
        vertex(buffer, pX + rotXW - dirOX, pY + base, pZ + rotZW - dirOZ, uMax, vMin - add, r, g, b, light);
        vertex(buffer, pX - rotXW - dirOX, pY + base, pZ - rotZW - dirOZ, uMin, vMin - add, r, g, b, light);
        vertex(buffer, pX - rotXW, pY, pZ - rotZW, uMin, vMax - add, r, g, b, light);
        vertex(buffer, pX + rotXW, pY, pZ + rotZW, uMax, vMax - add, r, g, b, light);

        // Upper front (инвертированный порядок)
        vertex(buffer, pX + rotXW - dirOX, pY + base + girth, pZ + rotZW - dirOZ, uMax, vMin - add + 0.25, r, g, b, light);
        vertex(buffer, pX - rotXW - dirOX, pY + base + girth, pZ - rotZW - dirOZ, uMin, vMin - add + 0.25, r, g, b, light);
        vertex(buffer, pX - rotXW, pY + girth, pZ - rotZW, uMin, vMax - add + 0.25, r, g, b, light);
        vertex(buffer, pX + rotXW, pY + girth, pZ + rotZW, uMax, vMax - add + 0.25, r, g, b, light);

        // Upper left (инвертированный порядок)
        vertex(buffer, pX + rotXW - dirOX, pY + base + girth, pZ + rotZW - dirOZ, wMin, vMin - add + 0.75, r, g, b, light);
        vertex(buffer, pX + rotXW - dirOX, pY + base, pZ + rotZW - dirOZ, wMax, vMin - add + 0.75, r, g, b, light);
        vertex(buffer, pX + rotXW, pY, pZ + rotZW, wMax, vMax - add + 0.75, r, g, b, light);
        vertex(buffer, pX + rotXW, pY + girth, pZ + rotZW, wMin, vMax - add + 0.75, r, g, b, light);

        // Upper right (инвертированный порядок)
        vertex(buffer, pX - rotXW - dirOX, pY + base + girth, pZ - rotZW - dirOZ, wMin, vMin - add + 0.75, r, g, b, light);
        vertex(buffer, pX - rotXW - dirOX, pY + base, pZ - rotZW - dirOZ, wMax, vMin - add + 0.75, r, g, b, light);
        vertex(buffer, pX - rotXW, pY, pZ - rotZW, wMax, vMax - add + 0.75, r, g, b, light);
        vertex(buffer, pX - rotXW, pY + girth, pZ - rotZW, wMin, vMax - add + 0.75, r, g, b, light);

        vMax = 0.125F;

        // Bend (без изменений)
        vertex(buffer, pX + dirXG + rotXW, pY, pZ + dirZG + rotZW, uMax, vMin + add + 0.75, r, g, b, light);
        vertex(buffer, pX + dirXG - rotXW, pY, pZ + dirZG - rotZW, uMin, vMin + add + 0.75, r, g, b, light);
        vertex(buffer, pX - rotXW, pY + girth, pZ - rotZW, uMin, vMax + add + 0.75, r, g, b, light);
        vertex(buffer, pX + rotXW, pY + girth, pZ + rotZW, uMax, vMax + add + 0.75, r, g, b, light);
    }

    private void vertex(VertexConsumer buffer, double x, double y, double z,
                        double u, double v, float r, float g, float b, int light) {
        buffer.vertex((float) x, (float) y, (float) z)
                .color(r, g, b, 1.0f)
                .uv((float) u, (float) v)
                .uv2(light)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    protected int getLightColor(float partialTick) {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(pos) ? LightTexture.pack(15, 15) : 0;
    }


    @Override
    public boolean shouldCull() {
        return false; // отключаем отсечение по дальности и видимости
    }
}