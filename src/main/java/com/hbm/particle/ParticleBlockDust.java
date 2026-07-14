package com.hbm.particle;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ParticleBlockDust extends Particle {

    private final BlockState blockState;
    private final float baseSize;
    private float quadSize;

    public ParticleBlockDust(ClientLevel world, double x, double y, double z,
                             double motionX, double motionY, double motionZ,
                             BlockState blockState) {
        super(world, x, y, z, motionX, motionY, motionZ);

        this.blockState = blockState;
        this.baseSize = 0.1F; // Базовый размер
        this.quadSize = this.baseSize * 2.0F; // Увеличиваем в 2 раза (multipleParticleScaleBy(2))

        // Время жизни как в оригинале: 50 + rand.nextInt(20)
        this.lifetime = 50 + random.nextInt(20);

        this.gravity = 0.005F; // Гравитация
        this.hasPhysics = true;
        this.friction = 0.98F;

        // Цвет берем из блока (будет установлен автоматически при рендере)
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTick) {

        var minecraft = net.minecraft.client.Minecraft.getInstance();
        var model = minecraft.getBlockRenderer().getBlockModel(this.blockState);

        var sprite = model.getParticleIcon(net.minecraftforge.client.model.data.ModelData.EMPTY);

        Vec3 cameraPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float y = (float)(Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float z = (float)(Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());


        int color = net.minecraft.client.Minecraft.getInstance()
                .getBlockColors()
                .getColor(this.blockState, this.level,
                        net.minecraft.core.BlockPos.containing(this.x, this.y, this.z), 0);

        if (color == -1) {
            color = 0xFFFFFF;
        }

        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        // Уменьшаем альфа со временем
        float ageRatio = (this.age + partialTick) / (float)this.lifetime;
        float alpha = this.alpha * (1.0F - ageRatio);

        // Вычисляем размер
        float quadSize = this.getQuadSize() * 0.5F;

        // Массив вертексов для квадрата
        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        Quaternionf rotation;
        if (this.roll == 0.0F) {
            rotation = camera.rotation();
        } else {
            rotation = new Quaternionf(camera.rotation());
            rotation.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));
        }

        // Трансформируем вершины
        for (Vector3f vertex : vertices) {
            vertex.rotate(rotation);
            vertex.mul(quadSize);
            vertex.add(x, y, z);
        }

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        int light = this.getLightColor(partialTick);

        // Рендерим квадрат
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .uv(u1, v1)
                .color(r, g, b, alpha)
                .uv2(light)
                .endVertex();

        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .uv(u1, v0)
                .color(r, g, b, alpha)
                .uv2(light)
                .endVertex();

        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .uv(u0, v0)
                .color(r, g, b, alpha)
                .uv2(light)
                .endVertex();

        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .uv(u0, v1)
                .color(r, g, b, alpha)
                .uv2(light)
                .endVertex();
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

        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;

        // Уменьшаем размер со временем
        float ageRatio = (float) this.age / (float) this.lifetime;
        this.quadSize = this.baseSize * (1.0F - ageRatio * 0.5F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        // Используем стандартный рендер для блоковых частиц
        return ParticleRenderType.TERRAIN_SHEET;
    }

    public float getQuadSize() {
        // Возвращаем текущий размер, расчет уже сделан в tick()
        return this.quadSize;
    }

    @Override
    protected int getLightColor(float partialTick) {
        // Стандартное освещение
        return super.getLightColor(partialTick);
    }

}