package com.hbm.entity.missile;

import com.hbm.items.ModItems;
import com.hbm.particle.helper.ExplosionCreator;
import com.hbm.util.HBMEnums;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class EntityMissileStealth extends EntityMissileBaseNT {

    public EntityMissileStealth(EntityType<? extends EntityMissileStealth> entityType, Level level) {
        super(entityType, level);
    }

    public EntityMissileStealth(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileStealth> entityType) {
        super(level, x, y, z, targetX, targetZ, entityType);
    }

    @Override
    public List<ItemStack> getDebris() {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(ModItems.BOLT_STEEL.get(), 4));
        return list;
    }

    @Override
    public ItemStack getMissileItemForInfo() {
        return new ItemStack(ModItems.MISSILE_STEALTH.get());
    }

    @Override
    public boolean canBeSeenBy(Object radar) {
        return false;
    }

    @Override
    public void onMissileImpact(HitResult mop) {
        this.explodeStandard(20F, 24, false);
        ExplosionCreator.composeEffectStandard(level(), getX(), getY(), getZ());
    }

    @Override
    public ItemStack getDebrisRareDrop() {
        // Для POWDER_ASH с типом MISC используем обычный стек, т.к. это ItemEnumMulti
        ItemStack stack = new ItemStack(ModItems.POWDER_ASH.get());
        stack.getOrCreateTag().putInt("CustomModelData", HBMEnums.EnumAshType.MISC.ordinal());
        return stack;
    }
}