package com.hbm.particle.helper;

import com.hbm.network.PacketDispatcher;
import com.hbm.particle.ModParticles;
import com.hbm.particle.ParticleBlockDust;
import com.hbm.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;



public class ExplosionSmallCreator implements IParticleCreator{

    public static void composeEffect(Level world, double x, double y, double z, int cloudCount, float cloudScale, float cloudSpeedMult) {
        if(world.isClientSide()) return;

        createServerEffects(world, x, y, z);

        CompoundTag data = new CompoundTag();
        data.putString("type", "explosionSmall");
        data.putInt("cloudCount", cloudCount);
        data.putFloat("cloudScale", cloudScale);
        data.putFloat("cloudSpeedMult", cloudSpeedMult);
        data.putInt("debris", 15);

        PacketDispatcher.sendAuxParticleNT(data, x, y, z, world,
                net.minecraft.core.BlockPos.containing(x, y, z));

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag data) {

        int cloudCount = data.getInt("cloudCount");
        float cloudScale = data.getFloat("cloudScale");
        float cloudSpeedMult = data.getFloat("cloudSpeedMult");
        int debris = data.getInt("debris");

        float dist = (float) player.distanceToSqr(x, y, z);
        float soundRange = 200F;
        float speedOfSound = 343F;

        if(dist <= soundRange * soundRange) {
            SoundEvent sound = dist <= (soundRange * 0.4) * (soundRange * 0.4) ?
                    ModSounds.EXPLOSION_SMALL_NEAR.get() : ModSounds.EXPLOSION_SMALL_FAR.get();

            Minecraft mc = Minecraft.getInstance();
            SimpleSoundInstance soundInstance = SimpleSoundInstance.forUI(
                    sound,
                    1.0F,
                    0.9F + rand.nextFloat() * 0.2F
            );

            mc.getSoundManager().playDelayed(soundInstance, (int) (Math.sqrt(dist) / speedOfSound));
        }

        Minecraft mc = Minecraft.getInstance();
        for(int i = 0; i < cloudCount; i++) {

            mc.particleEngine.createParticle(
                    ModParticles.EXPLOSION_SMALL.get(),
                    x + (rand.nextDouble() - 0.5) * 0.5,
                    y + (rand.nextDouble() - 0.5) * 0.5,
                    z + (rand.nextDouble() - 0.5) * 0.5,
                    cloudScale,
                    cloudSpeedMult,
                    0
            );
        }

        // Создание частиц разрушения блока
        BlockState blockState = Blocks.AIR.defaultBlockState();

        for(Direction dir : Direction.values()) {
            BlockPos pos = BlockPos.containing(x, y, z).relative(dir);
            blockState = world.getBlockState(pos);
            if(!blockState.isAir()) {
                break;
            }
        }

        if(!blockState.isAir()) {
            for(int i = 0; i < debris; i++) {

                ParticleBlockDust particle = new ParticleBlockDust(
                        (ClientLevel) world,
                        x,
                        y + 0.1,
                        z,
                        rand.nextGaussian() * 0.2,
                        0.5F + rand.nextFloat() * 0.7,
                        rand.nextGaussian() * 0.2,
                        blockState
                );


                mc.particleEngine.add(particle);

            }
        }
    }

    private static void createServerEffects(Level world, double x, double y, double z) {
        // Звук на сервере
        world.playSound(null, BlockPos.containing(x, y, z),
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                1.0F, (1.0F + world.random.nextFloat() - world.random.nextFloat()) * 0.2F);
    }

}