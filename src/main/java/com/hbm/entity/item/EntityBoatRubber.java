package com.hbm.entity.item;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityBoatRubber extends Boat {

    public EntityBoatRubber(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    public EntityBoatRubber(Level level, double x, double y, double z) {
        this(ModEntities.BOAT_RUBBER.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public @NotNull Item getDropItem() {
        return ModItems.BOAT_RUBBER.get();
    }

    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(ModItems.BOAT_RUBBER.get());
    }
}