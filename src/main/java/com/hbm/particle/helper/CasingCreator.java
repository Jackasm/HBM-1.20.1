package com.hbm.particle.helper;

import com.hbm.particle.ParticleSpentCasing;
import com.hbm.particle.SpentCasing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CasingCreator implements IParticleCreator {

    /** Default casing without smoke */
    public static void composeEffect(Level world, LivingEntity player, double frontOffset, double heightOffset, double sideOffset, double frontMotion, double heightMotion, double sideMotion, double motionVariance, String casing) {
        composeEffect(world, player, frontOffset, heightOffset, sideOffset, frontMotion, heightMotion, sideMotion, motionVariance, 5F, 10F, casing, false, 0, 0, 0);
    }

    /** Casing without smoke */
    public static void composeEffect(Level world, LivingEntity player, double frontOffset, double heightOffset, double sideOffset, double frontMotion, double heightMotion, double sideMotion, double motionVariance, float multPitch, float multYaw, String casing) {
        composeEffect(world, player, frontOffset, heightOffset, sideOffset, frontMotion, heightMotion, sideMotion, motionVariance, multPitch, multYaw, casing, false, 0, 0, 0);
    }

    /** Default casing, but with smoke*/
    public static void composeEffect(Level world, LivingEntity player, double frontOffset, double heightOffset, double sideOffset, double frontMotion, double heightMotion, double sideMotion, double motionVariance, String casing, boolean smoking, int smokeLife, double smokeLift, int nodeLife) {
        composeEffect(world, player, frontOffset, heightOffset, sideOffset, frontMotion, heightMotion, sideMotion, motionVariance, 5F, 10F, casing, smoking, smokeLife, smokeLift, nodeLife);
    }

    public static void composeEffect(Level world, LivingEntity player, double frontOffset, double heightOffset, double sideOffset, double frontMotion, double heightMotion, double sideMotion, double motionVariance, float mPitch, float mYaw, String casing, boolean smoking, int smokeLife, double smokeLift, int nodeLife) {

        if(player.isCrouching()) heightOffset -= 0.075F;

        // Создаем вектор смещения
        Vec3 offset = new Vec3(sideOffset, heightOffset, frontOffset);
        // Вращаем вокруг осей с учетом поворота игрока
        offset = offset.xRot(-player.getXRot() * (float) Math.PI / 180F);
        offset = offset.yRot(-player.getYRot() * (float) Math.PI / 180F);

        double x = player.getX() + offset.x;
        double y = player.getY() + player.getEyeHeight() + offset.y;
        double z = player.getZ() + offset.z;

        // Создаем вектор движения
        Vec3 motion = new Vec3(sideMotion, heightMotion, frontMotion);
        // Вращаем вокруг осей с учетом поворота игрока
        motion = motion.xRot(-player.getXRot() * (float) Math.PI / 180F);
        motion = motion.yRot(-player.getYRot() * (float) Math.PI / 180F);

        double mX = player.getDeltaMovement().x + motion.x + player.getRandom().nextGaussian() * motionVariance;
        double mY = player.getDeltaMovement().y + motion.y + player.getRandom().nextGaussian() * motionVariance;
        double mZ = player.getDeltaMovement().z + motion.z + player.getRandom().nextGaussian() * motionVariance;

        if(player instanceof Player && ((Player) player).getAbilities().flying) mY -= 0.04D;

        CompoundTag data = new CompoundTag();
        data.putString("type", "casingNT");
        data.putDouble("mX", mX);
        data.putDouble("mY", mY);
        data.putDouble("mZ", mZ);
        data.putFloat("yaw", player.getYRot());
        data.putFloat("pitch", player.getXRot());
        data.putFloat("mPitch", mPitch);
        data.putFloat("mYaw", mYaw);
        data.putString("name", casing);
        data.putBoolean("smoking", smoking);
        data.putInt("smokeLife", smokeLife);
        data.putDouble("smokeLift", smokeLift);
        data.putInt("nodeLife", nodeLife);

        IParticleCreator.sendPacket(world, x, y, z, 50, data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag data) {

        String name = data.getString("name");
        SpentCasing casingConfig = SpentCasing.casingMap.get(name);
        double mX = data.getDouble("mX");
        double mY = data.getDouble("mY");
        double mZ = data.getDouble("mZ");
        float yaw = data.getFloat("yaw");
        float pitch = data.getFloat("pitch");
        float mPitch = data.getFloat("mPitch");
        float mYaw = data.getFloat("mYaw");
        boolean smoking = data.getBoolean("smoking");
        int smokeLife = data.getInt("smokeLife");
        double smokeLift = data.getDouble("smokeLift");
        int nodeLife = data.getInt("nodeLife");

        // Создаем частицу с обновленным конструктором
        ParticleSpentCasing casing = new ParticleSpentCasing(
                (ClientLevel) world, // Преобразуем Level в ClientLevel
                x, y, z,            // Позиция
                mX, mY, mZ,         // Движение
                mPitch, mYaw,       // Начальное вращение
                casingConfig,        // Конфигурация
                smoking,            // Дым
                smokeLife,          // Время жизни дыма
                smokeLift,          // Подъем дыма
                nodeLife            // Время жизни узла
        );

        // Устанавливаем начальное вращение
        casing.prevRotationYaw = yaw;
        casing.rotationYaw = yaw;
        casing.prevRotationPitch = pitch;
        casing.rotationPitch = pitch;

        Minecraft.getInstance().particleEngine.add(casing);
    }
}