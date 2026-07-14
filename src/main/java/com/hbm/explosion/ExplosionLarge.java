package com.hbm.explosion;

import java.util.List;
import java.util.Random;

import com.hbm.entity.projectile.EntityRubble;
import com.hbm.entity.projectile.EntityShrapnel;
import com.hbm.network.PacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class ExplosionLarge {

    static Random rand = new Random();

    public static void spawnParticles(Level world, double x, double y, double z, int count) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "smoke");
        data.putString("mode", "cloud");
        data.putInt("count", count);
        BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world, pos);
    }

    public static void spawnParticlesRadial(Level world, double x, double y, double z, int count) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "smoke");
        data.putString("mode", "radial");
        data.putInt("count", count);
        BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world, pos);
    }

    public static void spawnFoam(Level world, double x, double y, double z, int count) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "smoke");
        data.putString("mode", "foamSplash");
        data.putInt("count", count);
        BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world, pos);
    }

    public static void spawnShock(Level world, double x, double y, double z, int count, double strength) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "smoke");
        data.putString("mode", "shock");
        data.putInt("count", count);
        data.putDouble("strength", strength);
        BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world, pos);
    }

    public static void spawnBurst(Level world, double x, double y, double z, int count, double strength) {
        Vec3 vec = new Vec3(strength, 0, 0);
        vec = vec.yRot((float) Math.toRadians(rand.nextInt(360)));

        for(int i = 0; i < count; i++) {
            //ParticleUtil.spawnGasFlame(world, x, y, z, vec.x, 0.0, vec.z);
            vec = vec.yRot((float) Math.toRadians(360.0f / count));
        }
    }

    public static void spawnRubble(Level world, double x, double y, double z, int count) {
        for(int i = 0; i < count; i++) {
            EntityRubble rubble = new EntityRubble(world);
            rubble.setPos(x, y, z);
            rubble.setDeltaMovement(
                    rand.nextGaussian() * 0.75 * (1 + (count / 50.0)),
                    0.75 * (1 + ((count + rand.nextInt(count * 5))) / 25.0),
                    rand.nextGaussian() * 0.75 * (1 + (count / 50.0))
            );
            rubble.setMetaBasedOnBlock(Blocks.STONE.defaultBlockState());
            world.addFreshEntity(rubble);
        }
    }

    public static void spawnShrapnels(Level world, double x, double y, double z, int count) {

        for(int i = 0; i < count; i++) {
            EntityShrapnel shrapnel = new EntityShrapnel(world);
            shrapnel.setPos(x, y, z);
            shrapnel.setDeltaMovement(
                    rand.nextGaussian() * 1 * (1 + (count / 50.0)),
                    ((rand.nextFloat() * 0.5) + 0.5) * (1 + (count / (15 + rand.nextInt(21)))) + (rand.nextFloat() / 50 * count),
                    rand.nextGaussian() * 1 * (1 + (count / 50.0))
            );
            shrapnel.setTrail(rand.nextInt(3) == 0);
            world.addFreshEntity(shrapnel);
        }
    }

    public static void spawnTracers(Level world, double x, double y, double z, int count) {
        for(int i = 0; i < count; i++) {
            EntityShrapnel shrapnel = new EntityShrapnel(world);
            shrapnel.setPos(x, y, z);
            shrapnel.setDeltaMovement(
                    rand.nextGaussian() * 1 * (1 + (count / 50.0)) * 0.25F,
                    ((rand.nextFloat() * 0.5) + 0.5) * (1 + (count / (15 + rand.nextInt(21)))) + (rand.nextFloat() / 50 * count) * 0.25F,
                    rand.nextGaussian() * 1 * (1 + (count / 50.0)) * 0.25F
            );
            shrapnel.setTrail(true);
            world.addFreshEntity(shrapnel);
        }
    }

    public static void spawnShrapnelShower(Level world, double x, double y, double z,
                                           double motionX, double motionY, double motionZ,
                                           int count, double deviation) {
        for(int i = 0; i < count; i++) {
            EntityShrapnel shrapnel = new EntityShrapnel(world);
            shrapnel.setPos(x, y, z);
            shrapnel.setDeltaMovement(
                    motionX + rand.nextGaussian() * deviation,
                    motionY + rand.nextGaussian() * deviation,
                    motionZ + rand.nextGaussian() * deviation
            );
            shrapnel.setTrail(rand.nextInt(3) == 0);
            world.addFreshEntity(shrapnel);
        }
    }

    public static void spawnMissileDebris(Level world, double x, double y, double z,
                                          double motionX, double motionY, double motionZ,
                                          double deviation, List<ItemStack> debris, ItemStack rareDrop) {
        if(debris != null) {
            for (ItemStack stack : debris) {
                if (stack != null && !stack.isEmpty()) {
                    int k = rand.nextInt(stack.getCount() + 1);
                    for (int j = 0; j < k; j++) {
                        ItemEntity item = new ItemEntity(world, x, y, z, stack.copy());
                        item.setDeltaMovement(
                                (motionX + rand.nextGaussian() * deviation) * 0.85,
                                (motionY + rand.nextGaussian() * deviation) * 0.85,
                                (motionZ + rand.nextGaussian() * deviation) * 0.85
                        );
                        item.setPos(
                                item.getX() + item.getDeltaMovement().x * 2,
                                item.getY() + item.getDeltaMovement().y * 2,
                                item.getZ() + item.getDeltaMovement().z * 2
                        );
                        world.addFreshEntity(item);
                    }
                }
            }
        }

        if(rareDrop != null && !rareDrop.isEmpty() && rand.nextInt(10) == 0) {
            ItemEntity item = new ItemEntity(world, x, y, z, rareDrop.copy());
            item.setDeltaMovement(
                    motionX + rand.nextGaussian() * deviation * 0.1,
                    motionY + rand.nextGaussian() * deviation * 0.1,
                    motionZ + rand.nextGaussian() * deviation * 0.1
            );
            world.addFreshEntity(item);
        }
    }

    public static void explode(Level world, double x, double y, double z, float strength,
                               boolean cloud, boolean rubble, boolean shrapnel, Entity exploder) {
        world.explode(exploder, x, y, z, strength, true, Level.ExplosionInteraction.BLOCK);
        if(cloud)
            spawnParticles(world, x, y, z, cloudFunction((int)strength));
        if(rubble)
            spawnRubble(world, x, y, z, rubbleFunction((int)strength));
        if(shrapnel)
            spawnShrapnels(world, x, y, z, shrapnelFunction((int)strength));
    }

    public static void explode(Level world, double x, double y, double z, float strength,
                               boolean cloud, boolean rubble, boolean shrapnel) {
        world.explode(null, x, y, z, strength, true, Level.ExplosionInteraction.BLOCK);
        if(cloud)
            spawnParticles(world, x, y, z, cloudFunction((int)strength));
        if(rubble)
            spawnRubble(world, x, y, z, rubbleFunction((int)strength));
        if(shrapnel)
            spawnShrapnels(world, x, y, z, shrapnelFunction((int)strength));
    }

    public static void explodeFire(Level world, double x, double y, double z, float strength,
                                   boolean cloud, boolean rubble, boolean shrapnel) {
        world.explode(null, x, y, z, strength, true, Level.ExplosionInteraction.MOB);
        if(cloud)
            spawnParticles(world, x, y, z, cloudFunction((int)strength));
        if(rubble)
            spawnRubble(world, x, y, z, rubbleFunction((int)strength));
        if(shrapnel)
            spawnShrapnels(world, x, y, z, shrapnelFunction((int)strength));
    }

    public static void buster(Level world, double x, double y, double z, Vec3 vector, float strength, float depth) {
        vector = vector.normalize();

        for(int i = 0; i < depth; i += 2) {
            world.explode(null, x + vector.x * i, y + vector.y * i, z + vector.z * i,
                    strength, true, Level.ExplosionInteraction.BLOCK);
        }
    }

    public static void jolt(Level world, double posX, double posY, double posZ, double strength, int count, double vel) {
        for(int j = 0; j < count; j++) {
            double phi = rand.nextDouble() * (Math.PI * 2);
            double costheta = rand.nextDouble() * 2 - 1;
            double theta = Math.acos(costheta);
            double x = Math.sin(theta) * Math.cos(phi);
            double y = Math.sin(theta) * Math.sin(phi);
            double z = Math.cos(theta);

            Vec3 vec = new Vec3(x, y, z);

            for(int i = 0; i < strength; i++) {
                double x0 = posX + (vec.x * i);
                double y0 = posY + (vec.y * i);
                double z0 = posZ + (vec.z * i);
                BlockPos pos = new BlockPos((int) x0, (int) y0, (int) z0);

                if(!world.isClientSide()) {
                    // Проверка на жидкость
                    if(world.getFluidState(pos).getType() != Fluids.EMPTY) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }

                    if(!world.isEmptyBlock(pos)) {
                        float explosionResistance = world.getBlockState(pos).getBlock()
                                .getExplosionResistance(world.getBlockState(pos), world, pos,
                                        new Explosion(world, null, posX, posY, posZ, 1.0f, List.of()));

                        if(explosionResistance > 70)
                            continue;

                        EntityRubble rubble = new EntityRubble(world);
                        rubble.setPos(x0 + 0.5F, y0 + 0.5F, z0 + 0.5F);
                        rubble.setMetaBasedOnBlock(world.getBlockState(pos));

                        Vec3 vec4 = new Vec3(posX - rubble.getX(), posY - rubble.getY(), posZ - rubble.getZ());
                        vec4 = vec4.normalize();

                        rubble.setDeltaMovement(vec4.x * vel, vec4.y * vel, vec4.z * vel);
                        world.addFreshEntity(rubble);
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        break;
                    }
                }
            }
        }
    }

    public static int cloudFunction(int i) {
        return (int)(850 * (1 - Math.pow(Math.E, -i/15.0)) + 15);
    }

    public static int rubbleFunction(int i) {
        return i/10;
    }

    public static int shrapnelFunction(int i) {
        return i/3;
    }

    // Новый метод для создания взрыва с частицами
    public static void spawnExplosionParticles(Level world, double x, double y, double z) {
        if(world.isClientSide()) {
            for(int i = 0; i < 50; i++) {
                double d0 = x + world.random.nextDouble() * 2.0D - 1.0D;
                double d1 = y + world.random.nextDouble() * 2.0D - 1.0D;
                double d2 = z + world.random.nextDouble() * 2.0D - 1.0D;
                double d3 = (world.random.nextDouble() - 0.5D) * 0.5D;
                double d4 = (world.random.nextDouble() - 0.5D) * 0.5D;
                double d5 = (world.random.nextDouble() - 0.5D) * 0.5D;

                world.addParticle(ParticleTypes.EXPLOSION, d0, d1, d2, d3, d4, d5);
            }
        }
    }
}