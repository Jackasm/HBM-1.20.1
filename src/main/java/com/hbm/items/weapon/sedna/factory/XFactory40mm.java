package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityFireLingering;
import com.hbm.entity.logic.EntityC130;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.HBMResourceManager;
import com.hbm.main.MainRegistry;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.util.EntityDamageUtil;
import com.hbm.world.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.hbm.sound.ModSounds.TECH_BLEEP;

public class XFactory40mm {

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> Lego.handleStandardSmoke(ctx.entity, stack, 1500, 0.025D, 1.05D, 0);

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_GL = (stack, ctx) -> GunItem.setupRecoil(10, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_IGNITE = (bullet, hitResult) -> {
        if(hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            if(entity instanceof LivingEntity living) {
                int fire = HbmLivingProps.getFire(living);
                HbmLivingProps.setFire(living, fire + 200);
            }
        }
    };

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE = (bullet, hitResult) -> {
        Lego.standardExplode(bullet, hitResult, 5F);
        bullet.discard();
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE_HEAT = (bullet, hitResult) -> {

        // Если попали в сущность и пуля только что создана - выходим
        if (hitResult.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) return;

        // Стандартный взрыв
        Lego.standardExplode(bullet, hitResult, 3.5F);
        bullet.discard();

        // Дополнительный урон по живым сущностям
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            DamageSource source = BulletConfig.getDamage(bullet, (LivingEntity) bullet.getOwner(), BulletConfig.DamageClass.EXPLOSIVE);

            if (entity instanceof LivingEntity living) {
                EntityDamageUtil.attackEntityFromNT(
                        living,
                        source,
                        bullet.getDamage() * 3F,
                        true, 0.5F, 3F, 0.15F
                );
            } else {
                entity.hurt(source, bullet.getDamage() * 3F);
            }
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE_INC = (bullet, hitResult) -> spawnFire(bullet, hitResult, false, 200);

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE_PHOSPHORUS = (bullet, hitResult) -> spawnFire(bullet, hitResult, true, 400);

    public static void spawnFire(EntityBulletBaseMK4 bullet, HitResult hitResult, boolean phosphorus, int duration) {
        // Если попали в сущность и пуля только что создана - выходим
        if (hitResult.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) return;

        Level world = bullet.level();

        // Стандартный взрыв
        Lego.standardExplode(bullet, hitResult, 3F);

        // Создаем пожар
        EntityFireLingering fire = new EntityFireLingering(
                ModEntities.FIRE_LINGERING.get(),
                world
        );
        fire.setArea(5, 2)
                .setDuration(duration)
                .setType(phosphorus ? EntityFireLingering.TYPE_PHOSPHORUS : EntityFireLingering.TYPE_DIESEL);

        fire.setPos(
                hitResult.getLocation().x,
                hitResult.getLocation().y,
                hitResult.getLocation().z
        );

        world.addFreshEntity(fire);
        bullet.discard();

        // Распространение огня на соседние блоки
        int baseX = (int) Math.floor(hitResult.getLocation().x);
        int baseY = (int) Math.floor(hitResult.getLocation().y);
        int baseZ = (int) Math.floor(hitResult.getLocation().z);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int x = baseX + dx;
                    int y = baseY + dy;
                    int z = baseZ + dz;
                    BlockPos pos = new BlockPos(x, y, z);

                    if (world.isEmptyBlock(pos)) {
                        // Проверяем все 6 направлений
                        for (Direction dir : Direction.values()) {
                            BlockPos neighborPos = pos.relative(dir);

                            // Проверяем, горючий ли соседний блок
                            BlockState neighborState = world.getBlockState(neighborPos);
                            if (neighborState.isFlammable(world, neighborPos, dir.getOpposite())) {
                                // Пытаемся поставить огонь
                                if (net.minecraft.world.level.block.FireBlock.canBePlacedAt(world, pos, dir.getOpposite())) {
                                    world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE_DEMO = (bullet, hitResult) -> {

        // Если попали в сущность и пуля только что создана - выходим
        if (hitResult.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) return;

        // Создаем взрыв
        ExplosionVNT vnt = new ExplosionVNT(
                bullet.level(),
                hitResult.getLocation().x,
                hitResult.getLocation().y,
                hitResult.getLocation().z,
                5F,
                bullet.getOwner()
        );

        vnt.setBlockAllocator(new BlockAllocatorStandard());
        vnt.setBlockProcessor(new BlockProcessorStandard());
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.getDamage())
                .setupPiercing(bullet.getConfig().armorThresholdNegation,
                        bullet.getConfig().armorPiercingPercent));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
        vnt.explode();

        bullet.discard();
    };

    public static Consumer<Entity> LAMBDA_SPAWN_C130_SUPPLIESS = (entity) -> spawnPlane(entity, EntityC130.C130PayloadType.SUPPLIES);
    public static Consumer<Entity> LAMBDA_SPAWN_C130_WEAPONS = (entity) -> spawnPlane(entity, EntityC130.C130PayloadType.WEAPONS);

    public static void spawnPlane(Entity entity, EntityC130.C130PayloadType payload) {
        Level world = entity.level();

        if(!world.isClientSide() && entity.tickCount == 40) {
            EntityBulletBaseMK4 bullet = (EntityBulletBaseMK4) entity;
            if(bullet.getOwner() != null) {
                world.playSound(null,
                        entity.getX(), entity.getY(), entity.getZ(),
                        TECH_BLEEP.get(),
                        SoundSource.PLAYERS,
                        1.0F, 1.0F);
            }

            EntityC130 c130 = new EntityC130(ModEntities.C130.get(), bullet.level());
            int x = (int) Math.floor(bullet.getX());
            int z = (int) Math.floor(bullet.getZ());
            int y = bullet.level().getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            c130.fac(bullet.level(), x, y, z, payload);

            bullet.level().addFreshEntity(c130);

            WorldUtil.loadAndSpawnEntityInWorld(c130);

        }
    }

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_FLAREGUN_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(-90, 0, 0, 0).addPos(0, 0, 0, 350, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -3, 50).addPos(0, 0, 0, 250))
                .addBus("HAMMER", new BusAnimationSequence().addPos(15, 0, 0, 50).addPos(15, 0, 0, 550).addPos(0, 0, 0, 100));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("HAMMER", new BusAnimationSequence().addPos(15, 0, 0, 50).addPos(15, 0, 0, 550).addPos(0, 0, 0, 100));
        case RELOAD -> new BusAnimation()
                .addBus("OPEN", new BusAnimationSequence().addPos(45, 0, 0, 200, IType.SIN_FULL).addPos(45, 0, 0, 750).addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("SHELL", new BusAnimationSequence().addPos(4, -8, -4, 0).addPos(4, -8, -4, 200).addPos(0, 0, -5, 500, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("FLIP", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(25, 0, 0, 200, IType.SIN_DOWN).addPos(25, 0, 0, 800).addPos(0, 0, 0, 200, IType.SIN_DOWN));
        case JAMMED -> new BusAnimation()
                .addBus("OPEN", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(45, 0, 0, 200, IType.SIN_FULL).addPos(45, 0, 0, 500).addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("FLIP", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 200).addPos(25, 0, 0, 200, IType.SIN_DOWN).addPos(25, 0, 0, 550).addPos(0, 0, 0, 200, IType.SIN_DOWN));
        case INSPECT -> new BusAnimation()
                .addBus("FLIP", new BusAnimationSequence().addPos(-360 * 3, 0, 0, 1500, IType.SIN_FULL));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_CONGOLAKE_ANIMS = (stack, type) -> {
        int ammo = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0)
                .getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory());
        return switch (type) {
            case EQUIP -> HBMResourceManager.getWeaponAnimation("congolake", "Equip");
            case CYCLE -> HBMResourceManager.getWeaponAnimation("congolake", ammo <= 1 ? "FireEmpty" : "Fire");
            case RELOAD ->
                    HBMResourceManager.getWeaponAnimation("congolake", ammo == 0 ? "ReloadEmpty" : "ReloadStart");
            case RELOAD_CYCLE -> HBMResourceManager.getWeaponAnimation("congolake", ("Reload"));
            case RELOAD_END -> HBMResourceManager.getWeaponAnimation("congolake", "ReloadEnd");
            case JAMMED -> HBMResourceManager.getWeaponAnimation("congolake", "Jammed");
            case INSPECT -> HBMResourceManager.getWeaponAnimation("congolake", "Inspect");
            default -> null;
        };

    };

}
