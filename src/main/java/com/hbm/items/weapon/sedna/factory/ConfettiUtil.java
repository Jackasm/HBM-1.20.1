package com.hbm.items.weapon.sedna.factory;

import java.util.Locale;

import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.particle.helper.AshesCreator;
//import com.hbm.particle.helper.SkeletonCreator;

import com.hbm.particle.helper.SkeletonCreator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraftforge.network.PacketDistributor;

public class ConfettiUtil {

    public static void decideConfetti(LivingEntity entity, DamageSource source) {
        if(entity.isAlive()) return;

        String msgId = source.getMsgId();
        if(source.getMsgId().equals(BulletConfig.DamageClass.LASER.name().toLowerCase(Locale.US))) pulverize(entity);
        if(msgId.contains("electricity")) pulverize(entity);
        if(msgId.contains("explosive") || source.is(DamageTypeTags.IS_EXPLOSION)) gib(entity);
        if(source.is(DamageTypeTags.IS_FIRE)) cremate(entity);
    }

    public static void pulverize(LivingEntity entity) {
        int amount = Mth.clamp((int) (entity.getBbWidth() * entity.getBbHeight() * entity.getBbWidth() * 25), 5, 50);
        AshesCreator.composeEffect(entity.level(), entity, amount, 0.125F);
        SkeletonCreator.composeEffect(entity.level(), entity, 1F);
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                entity.getSoundSource(), 2.0F, 0.9F + entity.getRandom().nextFloat() * 0.2F);
    }

    public static void cremate(LivingEntity entity) {
        int amount = Mth.clamp((int) (entity.getBbWidth() * entity.getBbHeight() * entity.getBbWidth() * 25), 5, 50);
        AshesCreator.composeEffect(entity.level(), entity, amount, 0.125F);
        SkeletonCreator.composeEffect(entity.level(), entity, 0.25F);
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                entity.getSoundSource(), 2.0F, 0.9F + entity.getRandom().nextFloat() * 0.2F);
    }

    public static void gib(LivingEntity entity) {

        /*
        if(entity instanceof EntityCyberCrab) return;
        if(entity instanceof EntityTeslaCrab) return;
        if(entity instanceof EntityTaintCrab) return;
        if(entity instanceof Slime) return;
        */
        SkeletonCreator.composeEffectGib(entity.level(), entity, 0.25F);

        if(entity instanceof Skeleton) return;

        CompoundTag vdat = new CompoundTag();
        vdat.putString("type", "giblets");
        vdat.putInt("ent", entity.getId());

         PacketDispatcher.sendAuxParticleNT(vdat, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),entity);

        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                net.minecraft.sounds.SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                entity.getSoundSource(), 2.0F, 0.95F + entity.getRandom().nextFloat() * 0.2F);
    }
}