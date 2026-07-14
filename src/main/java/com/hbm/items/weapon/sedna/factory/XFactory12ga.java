package com.hbm.items.weapon.sedna.factory;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockDetonatable;
import com.hbm.blocks.generic.BlockDecoCRT;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.entity.projectile.EntityDuchessGambit;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.weapon.sedna.*;
import com.hbm.items.weapon.sedna.GunItem.LambdaContext;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
import com.hbm.main.HBMResourceManager;
import com.hbm.main.MainRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.render.anim.AnimationEnums.GunAnimation;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.sound.ModSounds;
import com.hbm.util.BobMathUtil;
import com.hbm.items.weapon.sedna.BulletConfig.DamageClass;
import com.hbm.util.EntityDamageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class XFactory12ga {



    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE = (bullet, hitResult) -> {
        Lego.standardExplode(bullet, hitResult, 2F);
        bullet.discard();
    };

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_BOAT = (bullet, hitResult) -> {
        EntityDuchessGambit pippo = new EntityDuchessGambit(bullet.level());
        pippo.setPos(hitResult.getLocation().x, hitResult.getLocation().y + 50, hitResult.getLocation().z);
        bullet.level().addFreshEntity(pippo);

        bullet.level().playSound(null, pippo.getX(), pippo.getY() + 50, pippo.getZ(),
                ModSounds.WEAPON_BOAT.get(), pippo.getSoundSource(), 100F, 1F);

        bullet.discard();
    };

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_INC = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            if (entity instanceof LivingEntity living) {
                int currentPhosphorus = HbmLivingProps.getPhosphorus(living);
                if (currentPhosphorus < 300) {
                    HbmLivingProps.setPhosphorus(living, 300);
                }
            }
        }
    };

    public static final BiConsumer<EntityBulletBeamBase, HitResult> LAMBDA_SHREDDER_IMPACT = (beam, hitResult) -> {
        int projectiles = beam.config.projectilesMin;
        if(beam.config.projectilesMax > beam.config.projectilesMin) {
            projectiles += beam.level().random.nextInt(beam.config.projectilesMax - beam.config.projectilesMin + 1);
        }

        if(hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            Direction dir = blockHit.getDirection();

            Vec3 hitPos = hitResult.getLocation().add(dir.getStepX() * 0.1, dir.getStepY() * 0.1, dir.getStepZ() * 0.1);

            // Создание импульса
            spawnPulse(beam.level(), hitResult, beam.getYRot(), beam.getXRot());

            List<Entity> blast = beam.level().getEntities(beam,
                    AABB.ofSize(hitPos, 1.5, 1.5, 1.5));
            DamageSource source = BulletConfig.getDamage(beam, beam.getThrower(), BulletConfig.DamageClass.LASER);

            for(Entity e : blast) {
                if(!e.isAlive()) continue;
                if(e instanceof LivingEntity living) {
                    EntityDamageUtil.attackEntityFromNT(living, source, beam.getDamage(), true, 0D, 0F, 0F);
                    if(!e.isAlive()) {
                        ConfettiUtil.decideConfetti(living, source);
                    }
                } else {
                    e.hurt(source, beam.getDamage());
                }
            }

            for(int i = 0; i < projectiles; i++) {
                EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(
                        beam.level(),
                        beam.getThrower(),
                        beam.config,
                        beam.getDamage() * beam.config.damageMult,
                        0.2F,
                        hitPos.x,
                        hitPos.y,
                        hitPos.z
                );
                // Устанавливаем движение вручную, так как конструктор не принимает параметры движения
                bullet.setPos(hitPos.x, hitPos.y, hitPos.z);
                bullet.setDeltaMovement(dir.getStepX(), dir.getStepY(), dir.getStepZ());
                beam.level().addFreshEntity(bullet);
            }
        }

        if(hitResult.getType() == HitResult.Type.ENTITY) {
            Vec3 hitPos = hitResult.getLocation();

            // Создание импульса
            spawnPulse(beam.level(), hitResult, beam.getYRot(), beam.getXRot());

            for(int i = 0; i < projectiles; i++) {
                double x = beam.level().random.nextGaussian();
                double y = beam.level().random.nextGaussian();
                double z = beam.level().random.nextGaussian();
                double length = Math.sqrt(x*x + y*y + z*z);

                EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(
                        beam.level(),
                        beam.getThrower(),
                        beam.config,
                        beam.getDamage() * beam.config.damageMult,
                        0.2F,
                        hitPos.x,
                        hitPos.y,
                        hitPos.z
                );
                bullet.setPos(hitPos.x, hitPos.y, hitPos.z);
                bullet.setDeltaMovement(x/length, y/length, z/length);
                beam.level().addFreshEntity(bullet);
            }
        }
    };

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_SHREDDER_RICOCHET = (bullet, hitResult) -> {
        if(hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = bullet.level().getBlockState(pos);
            Block block = state.getBlock();

            // Проверка на стекло
            if(state.is(Tags.Blocks.GLASS) || state.is(Tags.Blocks.GLASS_PANES)) {
                bullet.level().destroyBlock(pos, false);
                bullet.setPos(hitResult.getLocation());
                return;
            }

            if(block instanceof BlockDetonatable detonatable) {
                detonatable.onShot(bullet.level(), pos);
            }

            if(block == ModBlocks.DECO_CRT.get()) {
                int meta = state.getValue(BlockDecoCRT.META);
                int newMeta = (meta % 4) + 4;
                bullet.level().setBlock(pos, state.setValue(BlockDecoCRT.META, newMeta), 3);
            }

            Direction dir = blockHit.getDirection();
            Vec3 face = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
            Vec3 vel = bullet.getDeltaMovement().normalize();

            double angle = Math.abs(BobMathUtil.getCrossAngle(vel, face) - 90);

            if(angle <= bullet.getConfig().ricochetAngle) {
                // Создание импульса
                spawnPulse(bullet.level(), hitResult, bullet.getYRot(), bullet.getXRot());

                List<Entity> blast = bullet.level().getEntities(bullet,
                        AABB.ofSize(hitResult.getLocation(), 1.0, 1.0, 1.0));
                DamageSource source = BulletConfig.getDamage(bullet, (LivingEntity) bullet.getOwner(), DamageClass.LASER);

                for(Entity e : blast) {
                    if(!e.isAlive()) continue;
                    if(e instanceof LivingEntity living) {
                        EntityDamageUtil.attackEntityFromNT(living, source, bullet.getDamage(), true, 0D, 0F, 0F);
                        if(!e.isAlive()) {
                            ConfettiUtil.decideConfetti(living, source);
                        }
                    } else {
                        e.hurt(source, bullet.getDamage());
                    }
                }

                bullet.setRicochets(bullet.getRicochets() + 1);
                if(bullet.getRicochets() > bullet.getConfig().maxRicochetCount) {
                    bullet.setPos(hitResult.getLocation());
                    bullet.discard();
                    return;
                }

                // Отражение скорости
                Vec3 motion = bullet.getDeltaMovement();
                switch(dir) {
                    case UP:
                    case DOWN:
                        bullet.setDeltaMovement(motion.x, -motion.y, motion.z);
                        break;
                    case NORTH:
                    case SOUTH:
                        bullet.setDeltaMovement(motion.x, motion.y, -motion.z);
                        break;
                    case WEST:
                    case EAST:
                        bullet.setDeltaMovement(-motion.x, motion.y, motion.z);
                        break;
                }

                bullet.setPos(hitResult.getLocation());
            } else {
                bullet.setPos(hitResult.getLocation());
                bullet.discard();
            }
        }
    };

    public static void spawnPulse(Level level, HitResult hitResult, float yaw, float pitch) {

        double x = hitResult.getLocation().x;
        double y = hitResult.getLocation().y;
        double z = hitResult.getLocation().z;

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            Direction side = blockHit.getDirection();

            // Устанавливаем углы в зависимости от стороны блока
            if (side == Direction.UP) { yaw = 0F; pitch = 0F; }
            else if (side == Direction.DOWN) { yaw = 0F; pitch = 0F; }
            else if (side == Direction.NORTH) { yaw = 0F; pitch = 90F; }
            else if (side == Direction.SOUTH) { yaw = 180F; pitch = 90F; }
            else if (side == Direction.EAST) { yaw = 90F; pitch = 90F; }
            else if (side == Direction.WEST) { yaw = 270F; pitch = 90F; }

            // Смещаем точку немного в сторону от блока
            x += side.getStepX() * 0.05;
            y += side.getStepY() * 0.05;
            z += side.getStepZ() * 0.05;
        }

        // Создаем NBT данные
        CompoundTag data = new CompoundTag();
        data.putString("type", "plasmablast");
        data.putFloat("r", 0.5F);
        data.putFloat("g", 0.5F);
        data.putFloat("b", 1.0F);
        data.putFloat("pitch", pitch);
        data.putFloat("yaw", yaw);
        data.putFloat("scale", 0.75F);

        // Отправляем пакет всем в радиусе 100 блоков
        if (!level.isClientSide) {
            // Создаем временную сущность для отправки пакета по позиции
            // Используем сущность как null, но указываем позицию через координаты
            PacketDispatcher.sendAuxParticleNT(data, x, y, z, level, BlockPos.containing(x, y, z));
        }
    }

    public static final Function<ItemStack, String> LAMBDA_NAME_MARESLEG = (stack) -> {
        if(WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SAWED_OFF)) {
            return stack.getDescriptionId() + "_short";
        }
        return null;
    };

    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_MARESLEG = (stack, ctx) -> GunItem.setupRecoil(10, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_LIBERATOR = (stack, ctx) -> GunItem.setupRecoil(5, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_AUTOSHOTGUN = (stack, ctx) -> GunItem.setupRecoil((float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5) + 1.5F,
            (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5));

    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_SEXY = (stack, ctx) -> GunItem.setupRecoil((float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.5),
            (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5));

    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_SPAS_SECONDARY = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        Player player = ctx.getPlayer();
        Receiver rec = ctx.config.getReceivers(stack)[0];
        int index = ctx.configIndex;
        GunItem.GunState state = GunItem.getState(stack, index);

        if(state == GunItem.GunState.IDLE) {
            if(Objects.requireNonNull(rec.getCanFire(stack)).apply(stack, ctx)) {
                Objects.requireNonNull(rec.getOnFire(stack)).accept(stack, ctx);
                int remaining = rec.getRoundsPerCycle(stack);
                int timeFired = 1;
                for(int i = 0; i < remaining; i++) {
                    if(Objects.requireNonNull(rec.getCanFire(stack)).apply(stack, ctx)) {
                        Objects.requireNonNull(rec.getOnFire(stack)).accept(stack, ctx);
                        timeFired++;
                    }
                }

                // Теперь получаем SoundEvent напрямую
                SoundEvent sound = rec.getFireSound(stack);
                if(sound != null) {
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            sound, entity.getSoundSource(),
                            rec.getFireVolume(stack), rec.getFirePitch(stack) * (timeFired > 1 ? 0.9F : 1F));
                }

                GunItem.setState(stack, index, GunItem.GunState.COOLDOWN);
                GunItem.setTimer(stack, index, 20);
            } else {
                if(rec.getDoesDryFire(stack)) {
                    GunItem.playAnimation(player, stack, GunAnimation.CYCLE_DRY, index);
                    GunItem.setState(stack, index, GunItem.GunState.DRAWING);
                    GunItem.setTimer(stack, index, rec.getDelayAfterDryFire(stack));
                }
            }
        }
        if(state == GunItem.GunState.RELOADING) {
            GunItem.setReloadCancel(stack, true);
        }
    };

    @SuppressWarnings("incomplete-switch")
    public static final BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MARESLEG_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(-60, 0, 0, 0)
                        .addPos(0, 0, -3, 500, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(0, 0, 0, 50)
                        .addPos(0, 0, -1, 50)
                        .addPos(0, 0, 0, 250))
                .addBus("SIGHT", new BusAnimationSequence()
                        .addPos(35, 0, 0, 100, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(0, 0, 0, 600)
                        .addPos(-85, 0, 0, 200)
                        .addPos(0, 0, 0, 200))
                .addBus("TURN", new BusAnimationSequence()
                        .addPos(0, 0, 0, 600)
                        .addPos(0, 0, 45, 200, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("HAMMER", new BusAnimationSequence()
                        .addPos(30, 0, 0, 50)
                        .addPos(30, 0, 0, 550)
                        .addPos(0, 0, 0, 200));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(0, 0, 0, 600)
                        .addPos(-90, 0, 0, 200)
                        .addPos(0, 0, 0, 200))
                .addBus("TURN", new BusAnimationSequence()
                        .addPos(0, 0, 0, 600)
                        .addPos(0, 0, 45, 200, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("HAMMER", new BusAnimationSequence()
                        .addPos(30, 0, 0, 50)
                        .addPos(30, 0, 0, 550)
                        .addPos(0, 0, 0, 200));
        case RELOAD -> {
            boolean empty = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0)
                    .getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory()) <= 0;
            yield new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence()
                            .addPos(30, 0, 0, 400, IType.SIN_FULL))
                    .addBus("LEVER", new BusAnimationSequence()
                            .addPos(0, 0, 0, 400)
                            .addPos(-85, 0, 0, 200))
                    .addBus("SHELL", new BusAnimationSequence()
                            .addPos(0, 0, 0, 600)
                            .addPos(0, 0.25, -3, 0)
                            .addPos(0, empty ? 0.25 : 0.125, -1.5, 150, IType.SIN_UP)
                            .addPos(0, empty ? 0.25 : -0.25, 0, 150, IType.SIN_DOWN))
                    .addBus("FLAG", new BusAnimationSequence()
                            .addPos(0, 0, 0, empty ? 900 : 0)
                            .addPos(1, 1, 1, 0));
        }
        case RELOAD_CYCLE -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence().addPos(30, 0, 0, 0))
                .addBus("LEVER", new BusAnimationSequence().addPos(-85, 0, 0, 0))
                .addBus("SHELL", new BusAnimationSequence()
                        .addPos(0, 0.25, -3, 0)
                        .addPos(0, 0.125, -1.5, 150, IType.SIN_UP)
                        .addPos(0, -0.125, 0, 150, IType.SIN_DOWN))
                .addBus("FLAG", new BusAnimationSequence().addPos(1, 1, 1, 0));
        case RELOAD_END -> {
            if (stack.hasTag()) {
                CompoundTag tag = stack.getTag();
                if (Objects.requireNonNull(tag).contains("maresleg_reload_empty")) {
                    tag.remove("maresleg_reload_empty");
                }
            }
            yield new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence()
                            .addPos(30, 0, 0, 0)
                            .addPos(30, 0, 0, 250)
                            .addPos(0, 0, 0, 400, IType.SIN_FULL))
                    .addBus("LEVER", new BusAnimationSequence()
                            .addPos(-85, 0, 0, 0)
                            .addPos(0, 0, 0, 200))
                    .addBus("FLAG", new BusAnimationSequence().addPos(1, 1, 1, 0));
        }
        case JAMMED -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(30, 0, 0, 0)
                        .addPos(30, 0, 0, 250)
                        .addPos(0, 0, 0, 400, IType.SIN_FULL))
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(-85, 0, 0, 0)
                        .addPos(-15, 0, 0, 200)
                        .addPos(-15, 0, 0, 650)
                        .addPos(-85, 0, 0, 200)
                        .addPos(-15, 0, 0, 200)
                        .addPos(-15, 0, 0, 200)
                        .addPos(-85, 0, 0, 200)
                        .addPos(0, 0, 0, 200))
                .addBus("TURN", new BusAnimationSequence()
                        .addPos(0, 0, 0, 850)
                        .addPos(0, 0, 45, 200, IType.SIN_DOWN)
                        .addPos(0, 0, 45, 800)
                        .addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("FLAG", new BusAnimationSequence().addPos(1, 1, 1, 0));
        case INSPECT -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(-35, 0, 0, 300, IType.SIN_FULL)
                        .addPos(-35, 0, 0, 1150)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("TURN", new BusAnimationSequence()
                        .addPos(0, 0, 0, 450)
                        .addPos(0, 0, -90, 500, IType.SIN_FULL)
                        .addPos(0, 0, -90, 500)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static final BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MARESLEG_SHORT_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(-60, 0, 0, 0)
                        .addPos(0, 0, -3, 250, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(0, 0, 0, 50)
                        .addPos(0, 0, -1, 50)
                        .addPos(0, 0, 0, 250))
                .addBus("SIGHT", new BusAnimationSequence()
                        .addPos(35, 0, 0, 100, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(0, 0, 0, 600)
                        .addPos(-85, 0, 0, 200)
                        .addPos(0, 0, 0, 200))
                .addBus("HAMMER", new BusAnimationSequence()
                        .addPos(30, 0, 0, 50)
                        .addPos(30, 0, 0, 550)
                        .addPos(0, 0, 0, 200))
                .addBus("FLIP", new BusAnimationSequence()
                        .addPos(0, 0, 0, 600)
                        .addPos(360, 0, 0, 400))
                .addBus("SHELL", new BusAnimationSequence()
                        .addPos(-20, 0, 0, 0)); // убирает гильзу в стволе во время перезарядки
        case CYCLE_DRY -> new BusAnimation()
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(0, 0, 0, 600)
                        .addPos(-90, 0, 0, 200)
                        .addPos(0, 0, 0, 200))
                .addBus("HAMMER", new BusAnimationSequence()
                        .addPos(30, 0, 0, 50)
                        .addPos(30, 0, 0, 550)
                        .addPos(0, 0, 0, 200))
                .addBus("FLIP", new BusAnimationSequence()
                        .addPos(0, 0, 0, 600)
                        .addPos(360, 0, 0, 400))
                .addBus("SHELL", new BusAnimationSequence()
                        .addPos(-20, 0, 0, 0));
        case JAMMED -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(30, 0, 0, 0)
                        .addPos(30, 0, 0, 250)
                        .addPos(0, 0, 0, 400, IType.SIN_FULL))
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(-85, 0, 0, 0)
                        .addPos(-15, 0, 0, 200)
                        .addPos(-15, 0, 0, 650)
                        .addPos(-85, 0, 0, 200)
                        .addPos(-15, 0, 0, 200)
                        .addPos(-15, 0, 0, 200)
                        .addPos(-85, 0, 0, 200)
                        .addPos(0, 0, 0, 200))
                .addBus("FLAG", new BusAnimationSequence()
                        .addPos(1, 1, 1, 0));
        default -> LAMBDA_MARESLEG_ANIMS.apply(stack, type);
    };

    @SuppressWarnings("incomplete-switch")
    public static final BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_LIBERATOR_ANIMS = (stack, type) -> {

        int ammo = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory());

        switch(type) {
            case EQUIP:
                return new BusAnimation()
                        .addBus("EQUIP", new BusAnimationSequence()
                                .addPos(60, 0, 0, 0)
                                .addPos(0, 0, 0, 500, IType.SIN_DOWN));
            case CYCLE:
                return new BusAnimation()
                        .addBus("RECOIL", new BusAnimationSequence()
                                .addPos(0, 0, -2.5, 50, IType.SIN_DOWN)
                                .addPos(0, 0, 0, 350, IType.SIN_FULL));
            case CYCLE_DRY:
                return new BusAnimation();
            case RELOAD:
                if(ammo == 0) return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 100))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(0, 0, 0, 100)
                                .addPos(60, 0, 0, 350, IType.SIN_DOWN))
                        .addBus("SHELL1", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0)
                                .addPos(2, -4, -2, 400)
                                .addPos(0, 0, -2, 450, IType.SIN_FULL)
                                .addPos(0, 0, 0, 50, IType.SIN_UP))
                        .addBus("SHELL2", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0))
                        .addBus("SHELL3", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0))
                        .addBus("SHELL4", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0));
                if(ammo == 1) return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 100))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(0, 0, 0, 100)
                                .addPos(60, 0, 0, 350, IType.SIN_DOWN))
                        .addBus("SHELL1", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL2", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0)
                                .addPos(2, -4, -2, 400)
                                .addPos(0, 0, -2, 450, IType.SIN_FULL)
                                .addPos(0, 0, 0, 50, IType.SIN_UP))
                        .addBus("SHELL3", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0))
                        .addBus("SHELL4", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0));
                if(ammo == 2) return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 100))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(0, 0, 0, 100)
                                .addPos(60, 0, 0, 350, IType.SIN_DOWN))
                        .addBus("SHELL1", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL2", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL3", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0)
                                .addPos(2, -4, -2, 400)
                                .addPos(0, 0, -2, 450, IType.SIN_FULL)
                                .addPos(0, 0, 0, 50, IType.SIN_UP))
                        .addBus("SHELL4", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0));
                if(ammo == 3) return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 100))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(0, 0, 0, 100)
                                .addPos(60, 0, 0, 350, IType.SIN_DOWN))
                        .addBus("SHELL1", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL2", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL3", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL4", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0)
                                .addPos(2, -4, -2, 400)
                                .addPos(0, 0, -2, 450, IType.SIN_FULL)
                                .addPos(0, 0, 0, 50, IType.SIN_UP));
            case RELOAD_CYCLE:
                if(ammo == 0) return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 0))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(60, 0, 0, 0))
                        .addBus("SHELL1", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL2", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0)
                                .addPos(0, 0, -2, 450, IType.SIN_FULL)
                                .addPos(0, 0, 0, 50, IType.SIN_UP))
                        .addBus("SHELL3", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0))
                        .addBus("SHELL4", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0));
                if(ammo == 1) return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 0))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(60, 0, 0, 0))
                        .addBus("SHELL1", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL2", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL3", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0)
                                .addPos(0, 0, -2, 450, IType.SIN_FULL)
                                .addPos(0, 0, 0, 50, IType.SIN_UP))
                        .addBus("SHELL4", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0));
                if(ammo == 2) return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 0))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(60, 0, 0, 0))
                        .addBus("SHELL1", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL2", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL3", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus("SHELL4", new BusAnimationSequence()
                                .addPos(2, -4, -2, 0)
                                .addPos(0, 0, -2, 450, IType.SIN_FULL)
                                .addPos(0, 0, 0, 50, IType.SIN_UP));
                return null;
            case RELOAD_END:
                return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 0)
                                .addPos(15, 0, 0, 250)
                                .addPos(0, 0, 0, 50))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(60, 0, 0, 0)
                                .addPos(0, 0, 0, 250, IType.SIN_UP))
                        .addBus(ammo >= 0 ? "SHELL1" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo >= 1 ? "SHELL2" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo >= 2 ? "SHELL3" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo >= 3 ? "SHELL4" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo < 0 ? "SHELL1" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 1 ? "SHELL2" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 2 ? "SHELL3" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 3 ? "SHELL4" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0));
            case JAMMED:
                return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 0)
                                .addPos(15, 0, 0, 250)
                                .addPos(0, 0, 0, 50)
                                .addPos(0, 0, 0, 550)
                                .addPos(15, 0, 0, 100)
                                .addPos(15, 0, 0, 600)
                                .addPos(0, 0, 0, 50))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(60, 0, 0, 0)
                                .addPos(0, 0, 0, 250, IType.SIN_UP)
                                .addPos(0, 0, 0, 600)
                                .addPos(45, 0, 0, 250, IType.SIN_DOWN)
                                .addPos(45, 0, 0, 300)
                                .addPos(0, 0, 0, 150, IType.SIN_UP))
                        .addBus(ammo >= 0 ? "SHELL1" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo >= 1 ? "SHELL2" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo >= 2 ? "SHELL3" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo >= 3 ? "SHELL4" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo < 0 ? "SHELL1" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 1 ? "SHELL2" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 2 ? "SHELL3" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 3 ? "SHELL4" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0));
            case INSPECT:
                return new BusAnimation()
                        .addBus("LATCH", new BusAnimationSequence()
                                .addPos(15, 0, 0, 100)
                                .addPos(15, 0, 0, 1100)
                                .addPos(0, 0, 0, 50))
                        .addBus("BREAK", new BusAnimationSequence()
                                .addPos(0, 0, 0, 100)
                                .addPos(60, 0, 0, 350, IType.SIN_DOWN)
                                .addPos(60, 0, 0, 500)
                                .addPos(0, 0, 0, 250, IType.SIN_UP))
                        .addBus(ammo > 0 ? "SHELL1" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo > 1 ? "SHELL2" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo > 2 ? "SHELL3" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo > 3 ? "SHELL4" : "NULL", new BusAnimationSequence()
                                .addPos(0, 0, 0, 0))
                        .addBus(ammo < 1 ? "SHELL1" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 2 ? "SHELL2" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 3 ? "SHELL3" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0))
                        .addBus(ammo < 4 ? "SHELL4" : "NULL", new BusAnimationSequence()
                                .addPos(2, -8, -2, 0));
        }

        return null;
    };

    @SuppressWarnings("incomplete-switch")
    public static final BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_SPAS_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(-60, 0, 0, 0)
                        .addPos(0, 0, -3, 500, IType.SIN_DOWN));
        case CYCLE -> HBMResourceManager.getWeaponAnimation("spas12", "Fire");
        case CYCLE_DRY -> HBMResourceManager.getWeaponAnimation("spas12", "FireDry");
        case ALT_CYCLE -> HBMResourceManager.getWeaponAnimation("spas12", "FireAlt");
        case RELOAD -> {
            boolean empty = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0)
                    .getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory()) <= 0;
            yield HBMResourceManager.getWeaponAnimation("spas12", empty ? "ReloadEmptyStart" : "ReloadStart");
        }
        case RELOAD_CYCLE -> HBMResourceManager.getWeaponAnimation("spas12", "Reload");
        case RELOAD_END -> HBMResourceManager.getWeaponAnimation("spas12", "ReloadEnd");
        case JAMMED -> HBMResourceManager.getWeaponAnimation("spas12", "Jammed");
        case INSPECT -> HBMResourceManager.getWeaponAnimation("spas12", "Inspect");
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static final BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_SHREDDER_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(60, 0, 0, 0)
                        .addPos(0, 0, 0, 500, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(0, 0, -1, 50, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 150, IType.SIN_FULL))
                .addBus("CYCLE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 150)
                        .addPos(0, 0, 18, 100));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("CYCLE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 150)
                        .addPos(0, 0, 18, 100));
        case RELOAD -> new BusAnimation()
                .addBus("MAG", new BusAnimationSequence()
                        .addPos(0, -8, 0, 250, IType.SIN_UP)
                        .addPos(0, -8, 0, 1000)
                        .addPos(0, 0, 0, 300))
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 750)
                        .addPos(-25, 0, 0, 300, IType.SIN_FULL)
                        .addPos(-25, 0, 0, 500)
                        .addPos(-27, 0, 0, 100, IType.SIN_DOWN)
                        .addPos(-25, 0, 0, 100, IType.SIN_FULL)
                        .addPos(-25, 0, 0, 150)
                        .addPos(0, 0, 0, 300, IType.SIN_FULL));
        case JAMMED -> new BusAnimation()
                .addBus("MAG", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, -2, 0, 150, IType.SIN_UP)
                        .addPos(0, 0, 0, 100))
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 750)
                        .addPos(-2, 0, 0, 100, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL));
        case INSPECT -> new BusAnimation()
                .addBus("MAG", new BusAnimationSequence()
                        .addPos(0, -1, 0, 150)
                        .addPos(6, -1, 0, 150)
                        .addPos(6, 12, 0, 350, IType.SIN_DOWN)
                        .addPos(6, -2, 0, 350, IType.SIN_UP)
                        .addPos(6, -1, 0, 50)
                        .addPos(6, -1, 0, 100)
                        .addPos(0, -1, 0, 150, IType.SIN_FULL)
                        .addPos(0, 0, 0, 150, IType.SIN_UP))
                .addBus("SPEEN", new BusAnimationSequence()
                        .addPos(0, 0, 0, 300)
                        .addPos(360, 0, 0, 700))
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 1450)
                        .addPos(-2, 0, 0, 100, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static final BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_SEXY_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(45, 0, 0, 0)
                        .addPos(0, 0, 0, 1000, IType.SIN_DOWN));
        case CYCLE -> {
            int amount = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0)
                    .getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, null);
            yield new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence()
                            .hold(50)
                            .addPos(0, 0, -0.25, 50, IType.SIN_DOWN)
                            .addPos(0, 0, 0, 100, IType.SIN_FULL))
                    .addBus("BARREL", new BusAnimationSequence()
                            .addPos(0, 0, -1, 50, IType.SIN_DOWN)
                            .addPos(0, 0, 0, 150))
                    .addBus("CYCLE", new BusAnimationSequence()
                            .addPos(1, 0, 0, 150))
                    .addBus("HOOD", new BusAnimationSequence()
                            .hold(50)
                            .addPos(3, 0, 0, 50, IType.SIN_DOWN)
                            .addPos(0, 0, 0, 50, IType.SIN_UP))
                    .addBus("SHELLS", new BusAnimationSequence()
                            .setPos(amount - 1, 0, 0)); // временно
        }
        case CYCLE_DRY -> new BusAnimation()
                .addBus("CYCLE", new BusAnimationSequence()
                        .addPos(0, 0, 18, 50));
        case RELOAD -> new BusAnimation()
                .addBus("LOWER", new BusAnimationSequence()
                        .addPos(15, 0, 0, 500, IType.SIN_FULL)
                        .hold(2750)
                        .addPos(12, 0, 0, 100, IType.SIN_DOWN)
                        .addPos(15, 0, 0, 100, IType.SIN_FULL)
                        .hold(1050)
                        .addPos(18, 0, 0, 100, IType.SIN_DOWN)
                        .addPos(15, 0, 0, 100, IType.SIN_FULL)
                        .hold(300)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(0, 0, 1, 150)
                        .hold(4700)
                        .addPos(0, 0, 0, 150))
                .addBus("HOOD", new BusAnimationSequence()
                        .hold(250)
                        .addPos(60, 0, 0, 500, IType.SIN_FULL)
                        .hold(3250)
                        .addPos(0, 0, 0, 500, IType.SIN_UP))
                .addBus("BELT", new BusAnimationSequence()
                        .setPos(1, 0, 0)
                        .hold(750)
                        .addPos(0, 0, 0, 500, IType.SIN_UP)
                        .hold(2000)
                        .addPos(1, 0, 0, 500, IType.SIN_UP))
                .addBus("MAG", new BusAnimationSequence()
                        .hold(1500)
                        .addPos(0, -1, 0, 250, IType.SIN_UP)
                        .addPos(2, -1, 0, 500, IType.SIN_UP)
                        .addPos(7, 1, 0, 250, IType.SIN_UP)
                        .addPos(15, 2, 0, 250)
                        .setPos(0, -2, 0)
                        .addPos(0, 0, 0, 500, IType.SIN_UP))
                .addBus("MAGROT", new BusAnimationSequence()
                        .hold(2250)
                        .addPos(0, 0, -180, 500, IType.SIN_FULL)
                        .setPos(0, 0, 0));
        case INSPECT -> new BusAnimation()
                .addBus("BOTTLE", new BusAnimationSequence()
                        .setPos(8, -8, -2)
                        .addPos(6, -4, -2, 500, IType.SIN_DOWN)
                        .addPos(3, -3, -5, 500, IType.SIN_FULL)
                        .addPos(3, -2, -5, 1000)
                        .addPos(4, -6, -2, 750, IType.SIN_FULL)
                        .addPos(6, -8, -2, 500, IType.SIN_UP))
                .addBus("SIP", new BusAnimationSequence()
                        .setPos(25, 0, 0)
                        .hold(500)
                        .addPos(-90, 0, 0, 500, IType.SIN_FULL)
                        .addPos(-110, 0, 0, 1000)
                        .addPos(25, 0, 0, 750, IType.SIN_FULL));
        default -> null;
    };
}