package com.hbm.items.special;

import com.hbm.entity.item.EntityItemWaste;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemNuclearWaste extends Item {

    public ItemNuclearWaste(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCustomEntity(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getEntityLifespan(@NotNull ItemStack itemStack, @NotNull Level level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public Entity createEntity(Level level, Entity entityItem, @NotNull ItemStack itemstack) {
        if (!(entityItem instanceof ItemEntity originalEntity)) return null;

        EntityItemWaste entity = new EntityItemWaste(level,
                originalEntity.getX(),
                originalEntity.getY(),
                originalEntity.getZ(),
                itemstack);

        entity.setDeltaMovement(originalEntity.getDeltaMovement());
        entity.setPickUpDelay(10);

        originalEntity.discard();

        return entity;
    }
}