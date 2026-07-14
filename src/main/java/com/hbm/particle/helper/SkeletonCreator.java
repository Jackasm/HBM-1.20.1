package com.hbm.particle.helper;

import com.hbm.main.ClientProxy;
import com.hbm.particle.ParticleSkeleton;
import com.hbm.particle.ParticleSkeleton.EnumSkeletonType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.function.Function;

public class SkeletonCreator implements IParticleCreator {

    public static HashMap<String, Function<LivingEntity, BoneDefinition[]>> skullanizer = new HashMap<>();

    public static void composeEffect(Level world, Entity toSkeletonize, float brightness) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "skeleton");
        data.putInt("entityID", toSkeletonize.getId());
        data.putFloat("brightness", brightness);
        IParticleCreator.sendPacket(world, toSkeletonize.getX(), toSkeletonize.getY(), toSkeletonize.getZ(), 100, data);
    }

    public static void composeEffectGib(Level world, Entity toSkeletonize, float force) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "skeleton");
        data.putInt("entityID", toSkeletonize.getId());
        data.putFloat("brightness", 1F);
        data.putFloat("force", force);
        data.putBoolean("gib", true);
        IParticleCreator.sendPacket(world, toSkeletonize.getX(), toSkeletonize.getY(), toSkeletonize.getZ(), 100, data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag data) {

        if(skullanizer.isEmpty()) init();

        boolean gib = data.getBoolean("gib");
        float force = data.getFloat("force");
        int entityID = data.getInt("entityID");
        Entity entity = world.getEntity(entityID);
        boolean skel = entity instanceof AbstractSkeleton;
        if(!(entity instanceof LivingEntity living)) return;

        ClientProxy.vanish(entityID);

        float brightness = data.getFloat("brightness");

        Function<LivingEntity, BoneDefinition[]> bonealizer = skullanizer.get(entity.getClass().getSimpleName());

        if(bonealizer != null) {
            BoneDefinition[] bones = bonealizer.apply(living);
            for(BoneDefinition bone : bones) {
                if(gib && rand.nextBoolean() && !skel) continue;
                ParticleSkeleton skeleton = new ParticleSkeleton(
                        Minecraft.getInstance().getTextureManager(),
                        (ClientLevel) world,
                        bone.x, bone.y, bone.z,
                        brightness, brightness, brightness,
                        bone.type
                );
                skeleton.prevRotationYaw = skeleton.rotationYaw = bone.yaw;
                skeleton.prevRotationPitch = skeleton.rotationPitch = bone.pitch;
                if(gib) {
                    skeleton.makeGib();
                    if(skel) {
                        skeleton.useTexture = skeleton.texture;
                        skeleton.useTextureExt = skeleton.texture_ext;
                    }
                    skeleton.setDeltaMovement(
                            rand.nextGaussian() * force,
                            (rand.nextGaussian() + 1) * force,
                            rand.nextGaussian() * force
                    );
                }
                Minecraft.getInstance().particleEngine.add(skeleton);
            }
        }
    }

    public static class BoneDefinition {
        public EnumSkeletonType type;
        public float yaw;
        public float pitch;
        public double x;
        public double y;
        public double z;

        public BoneDefinition(EnumSkeletonType type, float yaw, float pitch, double x, double y, double z) {
            this.type = type;
            this.yaw = yaw;
            this.pitch = pitch;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static Function<LivingEntity, BoneDefinition[]> BONES_BIPED = (entity) -> {
        float yawRad = -entity.yBodyRot * ((float)Math.PI / 180F);
        Vec3 leftarm = new Vec3(0.375, 0, 0).yRot(yawRad);
        Vec3 leftleg = new Vec3(0.125, 0, 0).yRot(yawRad);
        return new BoneDefinition[] {
                new BoneDefinition(EnumSkeletonType.SKULL, -entity.yHeadRot, entity.getXRot(), entity.getX(), entity.getY() + 1.75, entity.getZ()),
                new BoneDefinition(EnumSkeletonType.TORSO, -entity.yBodyRot, 0, entity.getX(), entity.getY() + 1.125, entity.getZ()),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() + leftarm.x, entity.getY() + 1.125, entity.getZ() + leftarm.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() - leftarm.x, entity.getY() + 1.125, entity.getZ() - leftarm.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() + leftleg.x, entity.getY() + 0.375, entity.getZ() + leftleg.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() - leftleg.x, entity.getY() + 0.375, entity.getZ() - leftleg.z),
        };
    };

    public static Function<LivingEntity, BoneDefinition[]> BONES_ZOMBIE = (entity) -> {
        float yawRad = -entity.yBodyRot * ((float)Math.PI / 180F);
        Vec3 leftarm = new Vec3(0.375, 0, 0).yRot(yawRad);
        Vec3 forward = new Vec3(0, 0, 0.25).yRot(yawRad);
        Vec3 leftleg = new Vec3(0.125, 0, 0).yRot(yawRad);
        return new BoneDefinition[] {
                new BoneDefinition(EnumSkeletonType.SKULL, -entity.yHeadRot, entity.getXRot(), entity.getX(), entity.getY() + 1.75, entity.getZ()),
                new BoneDefinition(EnumSkeletonType.TORSO, -entity.yBodyRot, 0, entity.getX(), entity.getY() + 1.125, entity.getZ()),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, -90, entity.getX() + leftarm.x + forward.x, entity.getY() + 1.375, entity.getZ() + leftarm.z + forward.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, -90, entity.getX() - leftarm.x + forward.x, entity.getY() + 1.375, entity.getZ() - leftarm.z + forward.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() + leftleg.x, entity.getY() + 0.375, entity.getZ() + leftleg.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() - leftleg.x, entity.getY() + 0.375, entity.getZ() - leftleg.z),
        };
    };

    public static Function<LivingEntity, BoneDefinition[]> BONES_VILLAGER = (entity) -> {
        float yawRad = -entity.yBodyRot * ((float)Math.PI / 180F);
        Vec3 leftarm = new Vec3(0.375, 0, 0).yRot(yawRad);
        Vec3 forward = new Vec3(0, 0, 0.25).yRot(yawRad);
        Vec3 leftleg = new Vec3(0.125, 0, 0).yRot(yawRad);
        return new BoneDefinition[] {
                new BoneDefinition(EnumSkeletonType.SKULL_VILLAGER, -entity.yHeadRot, entity.getXRot(), entity.getX(), entity.getY() + 1.6875, entity.getZ()),
                new BoneDefinition(EnumSkeletonType.TORSO, -entity.yBodyRot, 0, entity.getX(), entity.getY() + 1, entity.getZ()),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, -45, entity.getX() + leftarm.x + forward.x, entity.getY() + 1.125, entity.getZ() + leftarm.z + forward.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, -45, entity.getX() - leftarm.x + forward.x, entity.getY() + 1.125, entity.getZ() - leftarm.z + forward.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() + leftleg.x, entity.getY() + 0.375, entity.getZ() + leftleg.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() - leftleg.x, entity.getY() + 0.375, entity.getZ() - leftleg.z),
        };
    };

    public static Function<LivingEntity, BoneDefinition[]> BONES_DUMMY = (entity) -> {
        float yawRad = -entity.yBodyRot * ((float)Math.PI / 180F);
        Vec3 leftarm = new Vec3(0.375, 0, 0).yRot(yawRad);
        Vec3 leftleg = new Vec3(0.125, 0, 0).yRot(yawRad);
        return new BoneDefinition[] {
                new BoneDefinition(EnumSkeletonType.SKULL, -entity.yHeadRot, entity.getXRot(), entity.getX(), entity.getY() + 1.75, entity.getZ()),
                new BoneDefinition(EnumSkeletonType.TORSO, -entity.yBodyRot, 0, entity.getX(), entity.getY() + 1.125, entity.getZ()),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() + leftarm.x, entity.getY() + 1.125, entity.getZ() + leftarm.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() - leftarm.x, entity.getY() + 1.125, entity.getZ() - leftarm.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() + leftleg.x, entity.getY() + 0.375, entity.getZ() + leftleg.z),
                new BoneDefinition(EnumSkeletonType.LIMB, -entity.yBodyRot, 0, entity.getX() - leftleg.x, entity.getY() + 0.375, entity.getZ() - leftleg.z),
        };
    };

    public static void init() {
        // Players
        skullanizer.put(Player.class.getSimpleName(), BONES_BIPED);

        // Zombie family
        skullanizer.put(Zombie.class.getSimpleName(), BONES_ZOMBIE);
        skullanizer.put(AbstractSkeleton.class.getSimpleName(), BONES_ZOMBIE);
        skullanizer.put(Piglin.class.getSimpleName(), BONES_ZOMBIE);
        //skullanizer.put(EntityUndeadSoldier.class.getSimpleName(), BONES_ZOMBIE);

        // Villagers
        skullanizer.put(Villager.class.getSimpleName(), BONES_VILLAGER);
        skullanizer.put(Witch.class.getSimpleName(), BONES_VILLAGER);

        // Dummy
        //skullanizer.put(EntityDummy.class.getSimpleName(), BONES_DUMMY);

        // Techguns compat
        skullanizer.put("ArmySoldier", BONES_ZOMBIE);
        skullanizer.put("PsychoSteve", BONES_ZOMBIE);
        skullanizer.put("SkeletonSoldier", BONES_ZOMBIE);
        skullanizer.put("ZombieFarmer", BONES_ZOMBIE);
        skullanizer.put("ZombieMiner", BONES_ZOMBIE);
        skullanizer.put("ZombiePigmanSoldier", BONES_ZOMBIE);
        skullanizer.put("ZombieSoldier", BONES_ZOMBIE);
    }
}