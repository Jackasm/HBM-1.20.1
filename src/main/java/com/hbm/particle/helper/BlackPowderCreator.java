package com.hbm.particle.helper;

import com.hbm.network.PacketDispatcher;
import com.hbm.particle.ModParticles;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class BlackPowderCreator implements IParticleCreator{


    public static void doEffect(LivingEntity entity)
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", "blackPowder");
        nbt.putDouble("posX", entity.getX());
        nbt.putDouble("posY", entity.getY() + entity.getEyeHeight() - 0.2); // немного ниже глаз
        nbt.putDouble("posZ", entity.getZ());
        nbt.putFloat("lookX", (float) entity.getLookAngle().x);
        nbt.putFloat("lookY", (float) entity.getLookAngle().y);
        nbt.putFloat("lookZ", (float) entity.getLookAngle().z);
        nbt.putInt("smokeCount", 15); // Количество частиц дыма
        nbt.putInt("sparkCount", 10); // Количество искр
        nbt.putFloat("smokeScale", 0.5F);
        nbt.putFloat("smokeSpeed", 0.5F);
        nbt.putFloat("sparkSpeed", 0.25F);

        PacketDispatcher.sendAuxParticleNT(nbt,
                entity.getX(),
                entity.getY() + entity.getEyeHeight() - 0.2,
                entity.getZ(),
                entity);

    }
        
    @OnlyIn(Dist.CLIENT)
    @Override
    public void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag nbt) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        
        if (mc.level == null || nbt == null) return;

        float headingX = nbt.getFloat("lookX");
        float headingY = nbt.getFloat("lookY");
        float headingZ = nbt.getFloat("lookZ");
        int cloudCount = nbt.getInt("smokeCount");
        int sparkCount = nbt.getInt("sparkCount");
        float cloudScale = nbt.getFloat("smokeScale");
        float cloudSpeedMult = nbt.getFloat("smokeSpeed");
        float sparkSpeedMult = nbt.getFloat("sparkSpeed");

        Vector3f heading = new Vector3f(headingX, headingY, headingZ);
        float length = heading.length();
        if (length > 0) {
            heading.div(length); // Нормализация
        }

        // Создаем дым через зарегистрированный ParticleType
        for (int i = 0; i < cloudCount; i++) {
            double speedMult = 0.85 + world.random.nextDouble() * 0.3;
            double motionX = heading.x() * cloudSpeedMult * speedMult + world.random.nextGaussian() * 0.05;
            double motionY = heading.y() * cloudSpeedMult * speedMult + world.random.nextGaussian() * 0.05;
            double motionZ = heading.z() * cloudSpeedMult * speedMult + world.random.nextGaussian() * 0.05;

            // Используем зарегистрированный ParticleType
            world.addParticle(
                    ModParticles.BLACK_POWDER_SMOKE.get(),
                    x, y, z,
                    motionX, motionY, motionZ
            );
        }

        // Создаем искры через зарегистрированный ParticleType
        for (int i = 0; i < sparkCount; i++) {
            double speedMult = 0.85 + world.random.nextDouble() * 0.3;
            double motionX = heading.x() * sparkSpeedMult * speedMult + world.random.nextGaussian() * 0.02;
            double motionY = heading.y() * sparkSpeedMult * speedMult + world.random.nextGaussian() * 0.02;
            double motionZ = heading.z() * sparkSpeedMult * speedMult + world.random.nextGaussian() * 0.02;

            // Используем зарегистрированный ParticleType
            world.addParticle(
                    ModParticles.BLACK_POWDER_SPARK.get(),
                    x, y, z,
                    motionX, motionY, motionZ
            );
        }
    }

}