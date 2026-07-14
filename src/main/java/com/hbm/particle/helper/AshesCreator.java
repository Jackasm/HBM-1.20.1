package com.hbm.particle.helper;

import com.hbm.main.ClientProxy;
import com.hbm.particle.ModParticles;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AshesCreator implements IParticleCreator {

    public static void composeEffect(Level world, Entity toPulverize, int ashesCount, float ashesScale) {

        if(world.isClientSide) return;

        CompoundTag data = new CompoundTag();
        data.putString("type", "ashes");
        data.putInt("entityID", toPulverize.getId());
        data.putInt("ashesCount", ashesCount);
        data.putFloat("ashesScale", ashesScale);
        IParticleCreator.sendPacket(world, toPulverize.getX(), toPulverize.getY(), toPulverize.getZ(), 100, data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag data) {

        int entityID = data.getInt("entityID");
        Entity entity = world.getEntity(entityID);
        if(entity == null) return;

        ClientProxy.vanish(entityID);

        int amount = data.getInt("ashesCount");
        float scale = data.getFloat("ashesScale");

        for(int i = 0; i < amount; i++) {
            world.addParticle(
                    ModParticles.ASHES.get(),
                    entity.getX() + (entity.getBbWidth() + scale * 2) * (rand.nextDouble() - 0.5),
                    entity.getY() + entity.getBbHeight() * rand.nextDouble(),
                    entity.getZ() + (entity.getBbWidth() + scale * 2) * (rand.nextDouble() - 0.5),
                    0, 0, scale
            );

            world.addParticle(
                    ParticleTypes.FLAME,
                    entity.getX() + (entity.getBbWidth() + scale * 2) * (rand.nextDouble() - 0.5),
                    entity.getY() + entity.getBbHeight() * rand.nextDouble(),
                    entity.getZ() + (entity.getBbWidth() + scale * 2) * (rand.nextDouble() - 0.5),
                    0, 0, 0
            );
        }
    }
}