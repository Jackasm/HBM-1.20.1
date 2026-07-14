package com.hbm.particle.helper;

import com.hbm.network.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExplosionCreator implements IParticleCreator {

    public static final double SPEED_OF_SOUND = 17.15D * 0.5;

    static {
        IParticleCreator.creators.put("explosionLarge", new ExplosionCreator());
    }

    // Статические методы для отправки пакетов
    public static void composeEffect(Level world, double x, double y, double z, int cloudCount, float cloudScale,
                                     float cloudSpeedMult, float waveScale, int debrisCount, int debrisSize,
                                     int debrisRetry, float debrisVelocity, float debrisHorizontalDeviation,
                                     float debrisVerticalOffset, float soundRange) {

        if (world.isClientSide()) return;

        CompoundTag data = new CompoundTag();
        data.putString("type", "explosionLarge");
        data.putByte("cloudCount", (byte) cloudCount);
        data.putFloat("cloudScale", cloudScale);
        data.putFloat("cloudSpeedMult", cloudSpeedMult);
        data.putFloat("waveScale", waveScale);
        data.putByte("debrisCount", (byte) debrisCount);
        data.putByte("debrisSize", (byte) debrisSize);
        data.putShort("debrisRetry", (short) debrisRetry);
        data.putFloat("debrisVelocity", debrisVelocity);
        data.putFloat("debrisHorizontalDeviation", debrisHorizontalDeviation);
        data.putFloat("debrisVerticalOffset", debrisVerticalOffset);
        data.putFloat("soundRange", soundRange);

        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world, BlockPos.containing(x, y, z));

        // Серверный звук
        world.playSound(null, BlockPos.containing(x, y, z),
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                4.0F, (1.0F + world.random.nextFloat() * 0.2F) * 0.7F);
    }

    public static void composeEffectSmall(Level world, double x, double y, double z) {
        composeEffect(world, x, y, z, 10, 2F, 0.5F, 25F, 5, 8, 20, 0.75F, 1F, -2F, 150);
    }

    public static void composeEffectStandard(Level world, double x, double y, double z) {
        composeEffect(world, x, y, z, 15, 5F, 1F, 45F, 10, 16, 50, 1F, 3F, -2F, 200);
    }

    public static void composeEffectLarge(Level world, double x, double y, double z) {
        composeEffect(world, x, y, z, 30, 6.5F, 2F, 65F, 25, 16, 50, 1.25F, 3F, -2F, 350);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag data) {
        int cloudCount = data.getByte("cloudCount");
        float cloudScale = data.getFloat("cloudScale");
        float cloudSpeedMult = data.getFloat("cloudSpeedMult");
        float waveScale = data.getFloat("waveScale");
        int debrisCount = data.getByte("debrisCount");
        int debrisSize = data.getByte("debrisSize");
        int debrisRetry = data.getShort("debrisRetry");
        float debrisVelocity = data.getFloat("debrisVelocity");
        float debrisHorizontalDeviation = data.getFloat("debrisHorizontalDeviation");
        float debrisVerticalOffset = data.getFloat("debrisVerticalOffset");
        float soundRange = data.getFloat("soundRange");

        createClientEffects(world, x, y, z, cloudCount, cloudScale, cloudSpeedMult,
                waveScale, debrisCount, debrisSize, debrisRetry, debrisVelocity,
                debrisHorizontalDeviation, debrisVerticalOffset, soundRange);
    }

    @OnlyIn(Dist.CLIENT)
    public static void createClientEffects(Level world, double x, double y, double z, int cloudCount,
                                           float cloudScale, float cloudSpeedMult, float waveScale,
                                           int debrisCount, int debrisSize, int debrisRetry,
                                           float debrisVelocity, float debrisHorizontalDeviation,
                                           float debrisVerticalOffset, float soundRange) {

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        // Звук на клиенте
        double distSqr = player.distanceToSqr(x, y, z);
        if (distSqr <= soundRange * soundRange) {
            SoundEvent sound = distSqr <= Math.pow(soundRange * 0.4, 2) ?
                    SoundEvents.WITHER_SPAWN :
                    SoundEvents.GENERIC_EXPLODE;

            float volume = 1.0F;
            float pitch = 0.9F + world.random.nextFloat() * 0.2F;

            mc.getSoundManager().play(
                    net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(sound, pitch, volume)
            );
        }

        RandomSource rand = world.random;

        // Волна взрыва
        createExplosionWave(world, x, y + 2, z, waveScale);

        // Облако дыма
        createSmokePlume(world, x, y, z, cloudCount, cloudScale, cloudSpeedMult, rand);

        // Обломки
        createDebris(world, x, y, z, debrisCount, debrisSize, debrisRetry,
                debrisVelocity, debrisHorizontalDeviation, debrisVerticalOffset, rand);
    }

    @OnlyIn(Dist.CLIENT)
    private static void createExplosionWave(Level world, double x, double y, double z, float waveScale) {
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * waveScale * 2;
            double offsetY = (world.random.nextDouble() - 0.5) * waveScale;
            double offsetZ = (world.random.nextDouble() - 0.5) * waveScale * 2;

            world.addParticle(ParticleTypes.EXPLOSION,
                    x + offsetX, y + offsetY, z + offsetZ,
                    offsetX * 0.1, offsetY * 0.05, offsetZ * 0.1);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void createSmokePlume(Level world, double x, double y, double z,
                                         int cloudCount, float cloudScale, float cloudSpeedMult, RandomSource rand) {

        for (int i = 0; i < cloudCount; i++) {
            double motionX = rand.nextGaussian() * 0.5 * cloudSpeedMult;
            double motionY = rand.nextDouble() * 3 * cloudSpeedMult;
            double motionZ = rand.nextGaussian() * 0.5 * cloudSpeedMult;

            world.addParticle(ParticleTypes.LARGE_SMOKE,
                    x, y, z, motionX, motionY, motionZ);

            for (int j = 0; j < 3; j++) {
                world.addParticle(ParticleTypes.SMOKE,
                        x + (rand.nextDouble() - 0.5) * cloudScale,
                        y + rand.nextDouble() * cloudScale * 0.5,
                        z + (rand.nextDouble() - 0.5) * cloudScale,
                        motionX * 0.5, motionY * 0.5, motionZ * 0.5);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void createDebris(Level world, double x, double y, double z, int debrisCount,
                                     int debrisSize, int debrisRetry, float debrisVelocity,
                                     float debrisHorizontalDeviation, float debrisVerticalOffset, RandomSource rand) {

        for (int c = 0; c < debrisCount; c++) {
            double oX = rand.nextGaussian() * debrisHorizontalDeviation;
            double oY = debrisVerticalOffset;
            double oZ = rand.nextGaussian() * debrisHorizontalDeviation;

            BlockPos centerPos = BlockPos.containing(x + oX, y + oY, z + oZ);

            BlockState debrisState = findDebrisBlock(world, centerPos, debrisSize);

            if (!debrisState.isAir()) {
                Vec3 motion = new Vec3(debrisVelocity, 0, 0);
                motion = motion.yRot((float) (rand.nextDouble() * Math.PI * 2));
                motion = motion.xRot((float) Math.toRadians(-45 - rand.nextFloat() * 25));

                for (int i = 0; i < 10; i++) {
                    world.addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK, debrisState),
                            x, y, z,
                            motion.x + (rand.nextDouble() - 0.5) * 0.2,
                            motion.y + rand.nextDouble() * 0.5,
                            motion.z + (rand.nextDouble() - 0.5) * 0.2
                    );
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static BlockState findDebrisBlock(Level world, BlockPos center, int radius) {
        for (int r = 0; r <= radius; r++) {
            for (Direction dir : Direction.values()) {
                for (int i = -r; i <= r; i++) {
                    for (int j = -r; j <= r; j++) {
                        BlockPos checkPos = center.relative(dir, r).offset(
                                dir.getClockWise().getStepX() * i,
                                0,
                                dir.getClockWise().getStepZ() * i
                        ).above(j);

                        BlockState state = world.getBlockState(checkPos);
                        if (!state.isAir() && state.isSolidRender(world, checkPos)) {
                            return state;
                        }
                    }
                }
            }
        }
        return Blocks.STONE.defaultBlockState();
    }
}