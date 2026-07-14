package com.hbm.explosion.vanillant.standard;

import java.util.HashSet;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IExplosionSFX;
import com.hbm.network.PacketDispatcher;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExplosionEffectStandard implements IExplosionSFX {

    @Override
    public void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
        if(world.isClientSide()) return;

        world.playSound(null, x, y, z,
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);

        // Преобразуем List<BlockPos> в HashSet<BlockPos>
        HashSet<BlockPos> affectedBlocks = new HashSet<>(explosion.compat.getToBlow());
        performClient(world, x, y, z, size, affectedBlocks);
    }

    public static void performClient(Level world, double x, double y, double z, float size, HashSet<BlockPos> affectedBlocks) {

        if(size >= 2.0F) {
            // Большие частицы взрыва
            for(int i = 0; i < 10; i++) {
                world.addParticle(ParticleTypes.EXPLOSION,
                        x, y, z,
                        world.random.nextGaussian() * 0.5,
                        world.random.nextGaussian() * 0.5,
                        world.random.nextGaussian() * 0.5);
            }
        } else {
            // Малые частицы взрыва
            world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1.0D, 0.0D, 0.0D);
        }

        int count = Math.min(affectedBlocks.size(), 100); // Ограничиваем количество частиц

        for(BlockPos pos : affectedBlocks) {
            if(count-- <= 0) break;

            double oX = pos.getX() + world.random.nextDouble();
            double oY = pos.getY() + world.random.nextDouble();
            double oZ = pos.getZ() + world.random.nextDouble();
            double dX = oX - x;
            double dY = oY - y;
            double dZ = oZ - z;
            double delta = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

            if(delta != 0) {
                dX /= delta;
                dY /= delta;
                dZ /= delta;

                double mod = 0.5D / (delta / size + 0.1D);
                mod *= world.random.nextDouble() * world.random.nextDouble() + 0.3F;

                Vec3 velocity = new Vec3(dX * mod, dY * mod, dZ * mod);
                Vec3 posMid = new Vec3((oX + x) / 2.0D, (oY + y) / 2.0D, (oZ + z) / 2.0D);

                // Частицы взрыва
                world.addParticle(ParticleTypes.EXPLOSION,
                        posMid.x, posMid.y, posMid.z,
                        velocity.x, velocity.y, velocity.z);

                // Частицы дыма
                world.addParticle(ParticleTypes.SMOKE,
                        oX, oY, oZ,
                        velocity.x, velocity.y, velocity.z);
            }
        }
    }
}