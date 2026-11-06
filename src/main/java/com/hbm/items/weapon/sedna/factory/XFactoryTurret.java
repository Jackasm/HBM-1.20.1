package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class XFactoryTurret {

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_EXPLOSIVE = (bullet, hitResult) -> {

        Vec3 hitPos = hitResult.getLocation();

        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), hitPos.x, hitPos.y, hitPos.z, 10F);
        vnt.setBlockAllocator(new BlockAllocatorStandard());
        vnt.setBlockProcessor(new BlockProcessorStandard());
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.damage));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
        vnt.explode();
        bullet.discard();

    };

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE = (bullet, hitResult) -> {
        Lego.standardExplode(bullet, hitResult, 10F);
        bullet.discard();
    };
}
