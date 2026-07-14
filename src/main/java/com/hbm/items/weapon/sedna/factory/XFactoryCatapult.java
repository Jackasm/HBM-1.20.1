package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.MainRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class XFactoryCatapult {

    public static void incrementRad(Level level, double posX, double posY, double posZ, float mult) {
        for(int i = -2; i <= 2; i++) { for(int j = -2; j <= 2; j++) {
            if(Math.abs(i) + Math.abs(j) < 4) {
                BlockPos pos = new BlockPos((int) Math.floor(posX + i * 16), (int) Math.floor(posY), (int) Math.floor(posZ + j * 16));
                RadiationEvents.incrementRadiation(level, pos, 50F / (Math.abs(i) + Math.abs(j) + 1) * mult);
            }
        }
        }
    }

    public static void spawnMush(EntityBulletBaseMK4 bullet, HitResult hitResult) {
        Vec3 hitPos = hitResult.getLocation();
        Level level = bullet.level();

        level.playSound(null, hitPos.x, hitPos.y + 0.5, hitPos.z,
                ModSounds.MUKE_EXPLOSION.get(),
                SoundSource.BLOCKS, 15.0F, 1.0F);

        CompoundTag data = new CompoundTag();
        data.putString("type", "muke");
        data.putBoolean("balefire", MainRegistry.polaroidID == 11 || level.random.nextInt(100) == 0);

        PacketDispatcher.sendToAllAround(
                new AuxParticlePacketNT(data, hitPos.x, hitPos.y + 0.5, hitPos.z),
                level, BlockPos.containing(hitPos), 250);
    }

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_NUKE_STANDARD = (bullet, hitResult) -> {
        // Проверка на самострел в первые 3 тика
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (bullet.tickCount < 3 && entityHit.getEntity() == bullet.getOwner()) return;
        }

        if (bullet.isRemoved()) return;
        bullet.discard();

        Vec3 hitPos = hitResult.getLocation();

        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 10);
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(2, bullet.getDamage()).withRangeMod(1.5F));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.explode();

        incrementRad(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 1F);
        spawnMush(bullet, hitResult);
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_NUKE_DEMO = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (bullet.tickCount < 3 && entityHit.getEntity() == bullet.getOwner()) return;
        }
        if (bullet.isRemoved()) return;
        bullet.discard();

        Vec3 hitPos = hitResult.getLocation();

        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 15);
        vnt.setBlockAllocator(new BlockAllocatorStandard(64));
        vnt.setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorFire(Blocks.FIRE)));
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(2, bullet.getDamage()).withRangeMod(1.5F));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.explode();

        incrementRad(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 1.5F);
        spawnMush(bullet, hitResult);
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_NUKE_HIGH = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (bullet.tickCount < 3 && entityHit.getEntity() == bullet.getOwner()) return;
        }
        if (bullet.isRemoved()) return;
        bullet.discard();

        Vec3 hitPos = hitResult.getLocation();

        if (bullet.level() instanceof ServerLevel serverLevel) {
            EntityNukeExplosionMK5.statFac(serverLevel, 35, hitPos.x, hitPos.y, hitPos.z);
        }
        spawnMush(bullet, hitResult);
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_NUKE_BALEFIRE = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (bullet.tickCount < 3 && entityHit.getEntity() == bullet.getOwner()) return;
        }
        if (bullet.isRemoved()) return;
        bullet.discard();

        Vec3 hitPos = hitResult.getLocation();

        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 10);
        vnt.setBlockAllocator(new BlockAllocatorStandard(64));
        vnt.setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorBalefire()));
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(2, bullet.getDamage()).withRangeMod(1.5F));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.explode();

        incrementRad(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 1.5F);

        bullet.level().playSound(null, hitPos.x, hitPos.y + 0.5, hitPos.z,
                ModSounds.MUKE_EXPLOSION.get(),
                SoundSource.BLOCKS, 15.0F, 1.0F);

        CompoundTag data = new CompoundTag();
        data.putString("type", "muke");
        data.putBoolean("balefire", true);

        PacketDispatcher.sendToAllAround(
                new AuxParticlePacketNT(data, hitPos.x, hitPos.y + 0.5, hitPos.z),
                bullet.level(), BlockPos.containing(hitPos), 250);
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_NUKE_TINYTOT = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (bullet.tickCount < 3 && entityHit.getEntity() == bullet.getOwner()) return;
        }
        if (bullet.isRemoved()) return;
        bullet.discard();

        Vec3 hitPos = hitResult.getLocation();

        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 5);
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(2, bullet.getDamage()).withRangeMod(1.5F));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.explode();

        incrementRad(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 0.25F);

        bullet.level().playSound(null, hitPos.x, hitPos.y + 0.5, hitPos.z,
                ModSounds.MUKE_EXPLOSION.get(),
                SoundSource.BLOCKS, 15.0F, 1.0F);

        CompoundTag data = new CompoundTag();
        data.putString("type", "tinytot");
        data.putBoolean("balefire", MainRegistry.polaroidID == 11 || bullet.level().random.nextInt(100) == 0);

        PacketDispatcher.sendToAllAround(
                new AuxParticlePacketNT(data, hitPos.x, hitPos.y + 0.5, hitPos.z),
                bullet.level(), BlockPos.containing(hitPos), 250);
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_NUKE_HIVE = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (bullet.tickCount < 3 && entityHit.getEntity() == bullet.getOwner()) return;
        }
        if (bullet.isRemoved()) return;
        bullet.discard();

        Vec3 hitPos = hitResult.getLocation();

        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 5);
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.getDamage()).withRangeMod(1.5F));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
        vnt.explode();
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_FATMAN = (stack, ctx) -> { };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_FATMAN_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_DOWN));
        case CYCLE -> {
            Random rand = new Random();
            yield new BusAnimation()
                    .addBus("GAUGE", new BusAnimationSequence().addPos(0, 0, 135 + rand.nextInt(136), 100, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_DOWN))
                    .addBus("PISTON", new BusAnimationSequence().addPos(0, 0, 3, 100, IType.SIN_UP))
                    .addBus("NUKE", new BusAnimationSequence().addPos(0, 0, 3, 100, IType.SIN_UP).addPos(0, 0, 0, 0));
        }
        case RELOAD -> new BusAnimation()
                .addBus("LID", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -45, 250, IType.SIN_UP).addPos(0, 0, -45, 1200).addPos(0, 0, 0, 250, IType.SIN_UP))
                .addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, -2, 500, IType.SIN_FULL).addPos(0, 0, -2, 1700).addPos(0, 0, 0, 750, IType.SIN_FULL))
                .addBus("NUKE", new BusAnimationSequence().addPos(5, -4, 3, 0).addPos(5, -4, 3, 750).addPos(2, 0.5, 3, 500, IType.SIN_UP).addPos(1, 0.5, 3, 100).addPos(0, 0, 3, 100).addPos(0, 0, 3, 750).addPos(0, 0, 0, 750, IType.SIN_FULL))
                .addBus("PISTON", new BusAnimationSequence().addPos(0, 0, 3, 0).addPos(0, 0, 3, 2200).addPos(0, 0, 0, 750, IType.SIN_FULL))
                .addBus("EQUIP", new BusAnimationSequence().addPos(5, 0, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 450).addPos(3, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL).addPos(0, 0, 0, 500).addPos(-10, 0, 0, 375, IType.SIN_DOWN).addPos(0, 0, 0, 375, IType.SIN_UP));
        case JAMMED -> new BusAnimation()
                .addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
                .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-15, 0, 0, 250, IType.SIN_FULL).addPos(-15, 0, 0, 1000).addPos(0, 0, 0, 250, IType.SIN_FULL));
        case INSPECT -> new BusAnimation()
                .addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
                .addBus("EQUIP", new BusAnimationSequence().addPos(-15, 0, 0, 250, IType.SIN_FULL).addPos(-15, 0, 0, 1000).addPos(0, 0, 0, 250, IType.SIN_FULL));
        default -> null;
    };
}
