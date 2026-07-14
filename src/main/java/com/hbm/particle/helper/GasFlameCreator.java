package com.hbm.particle.helper;

import com.hbm.network.PacketDispatcher;
import com.hbm.particle.ModParticles;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GasFlameCreator implements IParticleCreator {

    public static void spawnEffect(Level world, double x, double y, double z, double mX, double mY, double mZ, float scale) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "gasflame");
        data.putDouble("mX", mX);
        data.putDouble("mY", mY);
        data.putDouble("mZ", mZ);
        data.putFloat("scale", scale);

        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world, BlockPos.containing(x, y, z));
    }

    public static void spawnEffect(Entity entity, double mX, double mY, double mZ, float scale) {
        spawnEffect(entity.level(), entity.getX(), entity.getY(), entity.getZ(), mX, mY, mZ, scale);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag nbt) {
        if (nbt == null) return;

        double mX = nbt.getDouble("mX");
        double mY = nbt.getDouble("mY");
        double mZ = nbt.getDouble("mZ");
        float scale = nbt.getFloat("scale");
        if (scale <= 0) scale = 6.5F;

        // Создаем частицы газа/пламени
        for (int i = 0; i < 8; i++) {
            double variationX = (rand.nextDouble() - 0.5) * 0.5;
            double variationY = (rand.nextDouble() - 0.5) * 0.5;
            double variationZ = (rand.nextDouble() - 0.5) * 0.5;

            world.addParticle(
                    ModParticles.GAS_FLAME.get(),
                    x, y, z,
                    mX + variationX, mY + variationY, mZ + variationZ
            );
        }

        // Добавляем немного дополнительных частиц для эффекта
        for (int i = 0; i < 4; i++) {
            world.addParticle(
                    ModParticles.GAS_FLAME.get(),
                    x + (rand.nextDouble() - 0.5) * 0.8,
                    y + (rand.nextDouble() - 0.5) * 0.8,
                    z + (rand.nextDouble() - 0.5) * 0.8,
                    mX * 0.5, mY * 0.5 + 0.02, mZ * 0.5
            );
        }
    }
}