package com.hbm.handler;

import com.hbm.particle.ParticleSpentCasing;
import com.hbm.particle.SpentCasing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

/**
 * Config for the guns themselves on where to spawn casings and at what angle
 */
public class CasingEjector implements Cloneable {

    public static final Map<Integer, CasingEjector> mappings = new HashMap<>();
    public static final RandomSource rand = RandomSource.create();

    private final int id;
    private static int nextId = 0;
    private Vec3 posOffset = Vec3.ZERO;
    private Vec3 initialMotion = Vec3.ZERO;
    private float randomYaw = 0F;
    private float randomPitch = 0F;

    public CasingEjector() {
        this.id = nextId;
        nextId++;
        mappings.put(id, this);
    }

    public CasingEjector setOffset(double x, double y, double z) {
        return setOffset(new Vec3(x, y, z));
    }

    public CasingEjector setOffset(Vec3 vec) {
        this.posOffset = vec;
        return this;
    }

    public CasingEjector setMotion(double x, double y, double z) {
        return setMotion(new Vec3(x, y, z));
    }

    public CasingEjector setMotion(Vec3 vec) {
        this.initialMotion = vec;
        return this;
    }

    public CasingEjector setAngleRange(float yaw, float pitch) {
        this.randomYaw = yaw;
        this.randomPitch = pitch;
        return this;
    }

    public int getId() { return this.id; }
    public Vec3 getOffset() { return this.posOffset; }
    public Vec3 getMotion() { return this.initialMotion; }
    public float getYawFactor() { return this.randomYaw; }
    public float getPitchFactor() { return this.randomPitch; }


    @OnlyIn(Dist.CLIENT)
    public void spawnCasing(TextureManager textureManager, SpentCasing config, Level world, double x, double y, double z, float pitch, float yaw, boolean crouched) {
        if (!(world instanceof ClientLevel clientLevel)) return;

        Vec3 rotatedMotionVec = rotateVector(getMotion(), pitch + (float) rand.nextGaussian() * getPitchFactor(),
                yaw + (float) rand.nextGaussian() * getPitchFactor(), getPitchFactor(), getPitchFactor());

        ParticleSpentCasing casing = new ParticleSpentCasing(clientLevel, x, y, z,
                rotatedMotionVec.x, rotatedMotionVec.y, rotatedMotionVec.z,
                (float) (world.random.nextGaussian() * 5F), (float) (world.random.nextGaussian() * 10F),
                config, false, 0, 0, 0);

        offsetCasing(casing, getOffset(), pitch, yaw, crouched);

        casing.rotationPitch = (float) Math.toDegrees(pitch);
        casing.rotationYaw = (float) Math.toDegrees(yaw);

        Minecraft.getInstance().particleEngine.add(casing);
    }

    // Rotate a position
    private static void offsetCasing(ParticleSpentCasing casing, Vec3 offset, float pitch, float yaw, boolean crouched) {
        // x-axis offset, 0 if crouched to center
        final float oX = (float) (crouched ? 0 : offset.x);

        // Создаём матрицы поворота
        Matrix4f pitchMatrix = new Matrix4f().rotationX(pitch);
        Matrix4f yawMatrix = new Matrix4f().rotationY(-yaw);

        // Умножаем матрицы
        Matrix4f rotMatrix = yawMatrix.mul(pitchMatrix, new Matrix4f());

        // Вектор смещения
        Vector4f offsetVector = new Vector4f(oX, (float) offset.y, (float) offset.z, 1);
        offsetVector.mul(rotMatrix);

        // Применяем смещение
        casing.addPosition(offsetVector.x(), offsetVector.y(), offsetVector.z());
    }

    private static Vec3 rotateVector(Vec3 vector, float pitch, float yaw, float pitchFactor, float yawFactor) {
        Matrix4f pitchMatrix = new Matrix4f().rotationX(pitch);
        Matrix4f yawMatrix = new Matrix4f().rotationY(-yaw);

        // Применяем случайность к вектору
        Vector4f vector4f = new Vector4f(
                (float) (vector.x + rand.nextGaussian() * yawFactor),
                (float) (vector.y + rand.nextGaussian() * pitchFactor),
                (float) (vector.z + rand.nextGaussian() * yawFactor),
                1
        );

        vector4f.mul(pitchMatrix);
        vector4f.mul(yawMatrix);

        return new Vec3(vector4f.x(), vector4f.y(), vector4f.z());
    }

    public static CasingEjector fromId(int id) {
        return mappings.get(id);
    }

    @Override
    public CasingEjector clone() {
        try {
            return (CasingEjector) super.clone();
        } catch (CloneNotSupportedException e) {
            return new CasingEjector();
        }
    }
}