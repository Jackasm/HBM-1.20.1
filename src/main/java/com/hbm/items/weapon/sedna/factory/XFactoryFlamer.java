package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityFireLingering;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.main.HBMResourceManager;
import com.hbm.particle.helper.FlameCreator;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class XFactoryFlamer {

    public static Consumer<Entity> LAMBDA_FIRE = (bullet) -> {
        if (bullet.level().isClientSide()) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player != null) {
                double distance = player.distanceTo(bullet);

                if (distance < 100) {
                    FlameCreator.composeEffectClient(
                            bullet.level(),
                            bullet.getX(),
                            bullet.getY() - 0.125,
                            bullet.getZ(),
                            FlameCreator.META_FIRE
                    );
                }
            }
        }
    };

    public static Consumer<Entity> LAMBDA_BALEFIRE = (bullet) -> {
        if (bullet.level().isClientSide()) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player != null && player.distanceTo(bullet) < 100) {
                FlameCreator.composeEffectClient(
                        bullet.level(),
                        bullet.getX(),
                        bullet.getY() - 0.125,
                        bullet.getZ(),
                        FlameCreator.META_BALEFIRE
                );
            }
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_LINGER_DIESEL = (bullet, hitResult) -> {
        if(!igniteIfPossible(bullet, hitResult))
            spawnFire(bullet, hitResult, 2F, 1F, 100, EntityFireLingering.TYPE_DIESEL);
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_LINGER_GAS = (bullet, hitResult) -> {
        igniteIfPossible(bullet, hitResult);
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_LINGER_NAPALM = (bullet, hitResult) -> {
        if(!igniteIfPossible(bullet, hitResult))
            spawnFire(bullet, hitResult, 2.5F, 1F, 200, EntityFireLingering.TYPE_DIESEL);
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_LINGER_BALEFIRE = (bullet, hitResult) -> {
        spawnFire(bullet, hitResult, 3F, 1F, 300, EntityFireLingering.TYPE_BALEFIRE);
    };

    public static boolean igniteIfPossible(EntityBulletBaseMK4 bullet, HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            Level level = bullet.level();
            BlockPos hitPos = blockHit.getBlockPos();
            Direction face = blockHit.getDirection();

            BlockState hitState = level.getBlockState(hitPos);
            Block hitBlock = hitState.getBlock();

            // Проверяем, является ли блок горючим
            // В 1.20.1 используем isFlammable для проверки горючести
            if (hitState.isFlammable(level, hitPos, face.getOpposite())) {
                BlockPos firePos = hitPos.relative(face);

                // Проверяем, можно ли разместить огонь
                if (level.isEmptyBlock(firePos)) {
                    BlockState fireState = Blocks.FIRE.defaultBlockState();

                    if (fireState.canSurvive(level, firePos)) {
                        level.setBlockAndUpdate(firePos, fireState);
                        return true;
                    }
                }
            }

            bullet.discard();
        }
        return false;
    }

    public static void spawnFire(EntityBulletBaseMK4 bullet, HitResult hitResult, float width, float height, int duration, int type) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            // Получаем позицию попадания
            Vec3 hitPos = hitResult.getLocation();

            // Создаем AABB для поиска уже существующих огней
            AABB searchBox = new AABB(
                    hitPos.x, hitPos.y, hitPos.z,
                    hitPos.x, hitPos.y, hitPos.z
            ).inflate(width / 2 + 0.5, height / 2 + 0.5, width / 2 + 0.5);

            // Ищем уже существующие огни в области
            // Предполагаем, что EntityFireLingering существует в вашем моде
            List<EntityFireLingering> fires = bullet.level().getEntitiesOfClass(
                    EntityFireLingering.class,
                    searchBox
            );

            // Если огней нет, создаем новый
            if (fires.isEmpty()) {
                // Предполагаем, что у EntityFireLingering есть конструктор и методы установки
                EntityFireLingering fire = new EntityFireLingering(ModEntities.FIRE_LINGERING.get(), bullet.level());
                fire.setArea(width, height);
                fire.setDuration(duration);
                fire.setType(type);
                fire.setPos(hitPos.x, hitPos.y, hitPos.z);

                // Добавляем в мир
                bullet.level().addFreshEntity(fire);
            }

            // Уничтожаем пулю
            bullet.discard();
        }
    }

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_FLAMER_ANIMS = (stack, type) -> {
        switch(type) {
            case EQUIP: return new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(-45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
            case RELOAD: return HBMResourceManager.getWeaponAnimation("flamethrower","Reload");
            case INSPECT:
            case JAMMED: return new BusAnimation()
                    .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 45, 250, IType.SIN_FULL).addPos(0, 0, 45, 350).addPos(0, 0, -15, 150, IType.SIN_FULL).addPos(0, 0, 0, 100, IType.SIN_FULL));
        }

        return null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_CHEMTHROWER_ANIMS = (stack, type) -> {
        switch(type) {
            case EQUIP: return new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(-45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
        }

        return null;
    };
}
