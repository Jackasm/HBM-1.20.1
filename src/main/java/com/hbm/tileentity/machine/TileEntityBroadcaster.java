package com.hbm.tileentity.machine;

import com.hbm.sound.AudioWrapper;
import com.hbm.sound.SoundHelper;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;

import static com.hbm.sound.ModSounds.*;

public class TileEntityBroadcaster extends TileEntityLoadedBase {

    private AudioWrapper audio;
    private AABB bb = null;

    public TileEntityBroadcaster(BlockPos pos, BlockState state) {
        super(ModTileEntity.BROADCASTER.get(), pos, state);
    }

    public void tick() {
        if (level == null) return;


        if (!level.isClientSide) {
            List<Entity> list = level.getEntitiesOfClass(Entity.class,
                    new AABB(worldPosition.getX() + 0.5 - 25, worldPosition.getY() + 0.5 - 25, worldPosition.getZ() + 0.5 - 25,
                            worldPosition.getX() + 0.5 + 25, worldPosition.getY() + 0.5 + 25, worldPosition.getZ() + 0.5 + 25));

            for (Entity entity : list) {
                if (entity instanceof LivingEntity living) {
                    double d = Math.sqrt(Math.pow(entity.getX() - (worldPosition.getX() + 0.5), 2) +
                            Math.pow(entity.getY() - (worldPosition.getY() + 0.5), 2) +
                            Math.pow(entity.getZ() - (worldPosition.getZ() + 0.5), 2));

                    if (d <= 25) {
                        if (!living.hasEffect(MobEffects.CONFUSION)) {
                            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));
                        }
                    }

                    if (d <= 15) {
                        double t = (15 - d) / 15 * 10;
                        living.hurt(ModDamageSource.broadcast(level), (float) t);
                    }
                }
            }
        } else {
            handleAudio();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void handleAudio() {
        if (audio == null) {
            audio = createAudioLoop();
            if (audio != null) audio.startSound();
        } else if (!audio.isPlaying()) {
            audio = rebootAudio(audio);
        }

        if (audio != null) {
            audio.keepAlive();
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        stopAudio();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        stopAudio();
    }

    private void stopAudio() {
        if (audio != null) {
            audio.stopSound();
            audio = null;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AudioWrapper createAudioLoop() {

        float volumeForRange = 1.0F;
        float range = 16.0F;
        return SoundHelper.createLoopedSound(randomBroadCastSound(),
                worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5F, worldPosition.getZ() + 0.5F,
                volumeForRange, range, 1.0F, 20);
    }

    SoundEvent randomBroadCastSound()
    {
        Random rand = new Random(worldPosition.getX() + worldPosition.getY() + worldPosition.getZ());
        int selector = rand.nextInt(3) + 1;
        return switch (selector) {
            case 1 -> BROADCAST_1.get();
            case 2 -> BROADCAST_2.get();
            default -> BROADCAST_3.get();
        };
    }

    @Override
    public AABB getRenderBoundingBox() {
        if (bb == null) {
            bb = new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                    worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1);
        }
        return bb;
    }

}