package com.hbm.entity.grenade;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EntityGrenadeIFNull extends EntityGrenadeBouncyBase {

    public EntityGrenadeIFNull(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeIFNull(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeIFNull(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            int range = 5;
            BlockPos center = BlockPos.containing(this.getX(), this.getY(), this.getZ());

            for (int a = -range; a <= range; a++) {
                for (int b = -range; b <= range; b++) {
                    for (int c = -range; c <= range; c++) {
                        BlockPos pos = center.offset(a, b, c);
                        this.level().removeBlock(pos, false);
                    }
                }
            }

            AABB box = new AABB(
                    center.getX() + 0.5 - 3, center.getY() + 0.5 - 3, center.getZ() + 0.5 - 3,
                    center.getX() + 0.5 + 3, center.getY() + 0.5 + 3, center.getZ() + 0.5 + 3
            );

            List<Entity> list = this.level().getEntities(this, box);

            for (Entity entity : list) {
                if (entity instanceof LivingEntity living) {
                    living.setHealth(0);
                    living.hurt(this.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
                } else {
                    entity.discard();
                }
            }
        }
    }

    @Override
    protected int getMaxTimer() {
        return ItemGrenade.getFuseTicks(new ItemStack(ModItems.GRENADE_IF_NULL.get()));
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }
}