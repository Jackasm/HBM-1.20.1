package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.items.IAnimatedItem;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.EntityDamageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class XFactoryFolly {

    public static BiFunction<ItemStack, GunItem.LambdaContext, Boolean> LAMBDA_CAN_FIRE = (stack, ctx) -> {
        if (!GunItem.getIsAiming(stack)) return false;
        if (GunItem.getLastAnim(stack, ctx.configIndex) == AnimationEnums.GunAnimation.RELOAD) return false;
        if (GunItem.getAnimTimer(stack, ctx.configIndex) < 100) return false;
        return Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, ctx.inventory) > 0;
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_FIRE = (stack, ctx) -> Lego.doStandardFire(stack, ctx, AnimationEnums.GunAnimation.CYCLE, false);

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_FOLLY = (stack, ctx) -> GunItem.setupRecoil(25, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_TOGGLE_AIM = (stack, ctx) -> {
        if (GunItem.getState(stack, ctx.configIndex) == GunItem.GunState.IDLE) {
            boolean wasAiming = GunItem.getIsAiming(stack);
            GunItem.setIsAiming(stack, !wasAiming);
            if (!wasAiming) {
                ((IAnimatedItem<AnimationEnums.GunAnimation>) stack.getItem()).playAnimation(ctx.getPlayer(), AnimationEnums.GunAnimation.SPINUP);
            }
        }
    };

    public static Consumer<Entity> LAMBDA_SM_UPDATE = (entity) -> {
        if (entity.level().isClientSide) return;
        EntityBulletBeamBase beam = (EntityBulletBeamBase) entity;
        Vec3 dir = new Vec3(beam.heading.x, beam.heading.y, beam.heading.z).normalize();

        if (beam.tickCount < 50) {
            double spacing = 10;
            double dist = beam.tickCount * spacing;
            CompoundTag data = new CompoundTag();
            data.putString("type", "plasmablast");
            data.putFloat("r", 0.75F);
            data.putFloat("g", 0.75F);
            data.putFloat("b", 0.75F);
            data.putFloat("pitch", beam.getXRot() + 90);
            data.putFloat("yaw", -beam.getYRot());
            data.putFloat("scale", 2F + beam.tickCount / (float)(beam.getBeamLength() / spacing) * 3F);

            PacketDispatcher.sendToAllAround(
                    new AuxParticlePacketNT(data,
                            beam.getX() + dir.x * dist,
                            beam.getY() + dir.y * dist,
                            beam.getZ() + dir.z * dist),
                    beam.level(), beam.blockPosition(), 250);
        }

        if (entity.tickCount != 2) return;

        if (beam.getThrower() != null) {
            ContaminationUtil.contaminate(beam.getThrower(), ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, 150F);
        }

        AABB beamBox = beam.getBoundingBox();
        Vec3 heading = new Vec3(beam.heading.x, beam.heading.y, beam.heading.z);
        AABB expandedBox = beamBox.expandTowards(heading).inflate(1.0, 1.0, 1.0);
        List<Entity> entities = beam.level().getEntitiesOfClass(Entity.class, expandedBox, e -> e != beam);

        for (int i = 1; i < beam.getBeamLength(); i += 2) {
            int x = (int) Math.floor(beam.getX() + dir.x * i);
            int y = (int) Math.floor(beam.getY() + dir.y * i);
            int z = (int) Math.floor(beam.getZ() + dir.z * i);

            for (int ix = x - 1; ix <= x + 1; ix++) {
                for (int iy = y - 1; iy <= y + 1; iy++) {
                    for (int iz = z - 1; iz <= z + 1; iz++) {
                        if (iy > 0 && iy < 256) {
                            beam.level().setBlock(new BlockPos(ix, iy, iz), Blocks.AIR.defaultBlockState(), 3);
                        }
                        AABB aabb = new AABB(ix - 1, iy - 1, iz - 1, ix + 2, iy + 2, iz + 2);
                        for (Entity e : entities) {
                            if (e != beam.getThrower() && e.getBoundingBox().intersects(aabb)) {
                                if (e instanceof LivingEntity living) {
                                    EntityDamageUtil.attackEntityFromNT(living,
                                            beam.getConfig().getDamage(beam, beam.getThrower(), Objects.requireNonNull(beam.getConfig()).dmgClass),
                                            beam.getDamage(), true, 0D, 0F, 0F);
                                } else {
                                    EntityDamageUtil.attackEntityFromIgnoreIFrame(e,
                                            beam.getConfig().getDamage(beam, beam.getThrower(), Objects.requireNonNull(beam.getConfig()).dmgClass),
                                            beam.getDamage());
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_NUKE_IMPACT = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY && bullet.tickCount < 2) return;
        if (bullet.isRemoved()) return;
        bullet.discard();

        Vec3 hitPos = hitResult.getLocation();

        if (bullet.level() instanceof ServerLevel serverLevel) {
            EntityNukeExplosionMK5.statFac(serverLevel, 100, hitPos.x, hitPos.y, hitPos.z);
            EntityNukeTorex.statFacStandard(serverLevel, hitPos.x, hitPos.y, hitPos.z, 100);
        }
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_FOLLY_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(5, 0, 0, 1500, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_FULL));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -4.5, 50).addPos(0, 0, -4.5, 500).addPos(0, 0, 0, 500, IType.SIN_UP))
                .addBus("LOAD", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(-25, 0, 0, 250, IType.SIN_DOWN).addPos(0, 0, 0, 1000, IType.SIN_FULL));
        case RELOAD -> new BusAnimation()
                .addBus("LOAD", new BusAnimationSequence().addPos(60, 0, 0, 1000, IType.SIN_FULL).addPos(60, 0, 0, 6000).addPos(0, 0, 0, 1000, IType.SIN_FULL))
                .addBus("SCREW", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(0, 0, -135, 1000, IType.SIN_FULL).addPos(0, 0, -135, 4000).addPos(0, 0, 0, 1000, IType.SIN_FULL))
                .addBus("BREECH", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(0, 0, -0.5, 1000, IType.SIN_FULL).addPos(0, -4, -0.5, 1000, IType.SIN_FULL).addPos(0, -4, -0.5, 2000).addPos(0, 0, -0.5, 1000, IType.SIN_FULL).addPos(0, 0, 0, 1000, IType.SIN_FULL))
                .addBus("SHELL", new BusAnimationSequence().addPos(0, -4, -4.5, 0).addPos(0, -4, -4.5, 3000).addPos(0, 0, -4.5, 1000, IType.SIN_FULL).addPos(0, 0, 0, 500, IType.SIN_UP));
        default -> null;
    };
}
